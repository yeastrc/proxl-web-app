package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.xlink.dao.PeptideDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;

public class MergedSearchPeptideSearcher {

	public static MergedSearchPeptideSearcher getInstance() { return new MergedSearchPeptideSearcher(); }
	
	// private constructor
	private MergedSearchPeptideSearcher( ) { }
	
	/**
	 * Get all peptides identified for the given protein in the given search, regardless of the type
	 * of peptide (i.e., regardless of whether or not it's a crosslink, looplink, monolink, dimer or none of the above)
	 * @param protein
	 * @return
	 * @throws Exception
	 */
	public Collection<PeptideDTO> getPeptides( NRProteinDTO protein, Collection<SearchDTO> searchs, double psmQValueCutoff, double peptideQValueCutoff ) throws Exception {
		
		Collection<PeptideDTO> peptides = new HashSet<PeptideDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			String sql = "SELECT DISTINCT a.peptide_id FROM nrseq_database_peptide_protein AS a "
					+ "INNER JOIN psm_peptide AS b ON a.peptide_id = b.peptide_id "
					+ "INNER JOIN psm AS c ON b.psm_id = c.id "
					+ "INNER JOIN search_reported_peptide AS d ON (c.search_id = d.search_id AND c.reported_peptide_id = d.reported_peptide_id) "
					+ "WHERE a.nrseq_id = ? AND c.search_id IN (#SEARCHES#) AND c.q_value <= ? AND  ( d.q_value <= ? OR d.q_value IS NULL ) ";
			
			
			Collection<Integer> searchIds = new HashSet<Integer>();
			for( SearchDTO search : searchs )
				searchIds.add( search.getId() );
			
			sql = sql.replaceAll( "#SEARCHES#", StringUtils.join( searchIds, "," ) );
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, protein.getNrseqId() );
			pstmt.setDouble( 2, psmQValueCutoff );
			pstmt.setDouble( 3, peptideQValueCutoff );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				peptides.add( PeptideDAO.getInstance().getPeptideDTOFromDatabase( rs.getInt( 1 ) ) );
			}
			
		} finally {
			
			// be sure database handles are closed
			if( rs != null ) {
				try { rs.close(); } catch( Throwable t ) { ; }
				rs = null;
			}
			
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}

		return peptides;
	}
	
}
