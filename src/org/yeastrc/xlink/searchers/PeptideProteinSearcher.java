package org.yeastrc.xlink.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;

import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PeptideDTO;

public class PeptideProteinSearcher {

	private PeptideProteinSearcher() { }
	private static final PeptideProteinSearcher _INSTANCE = new PeptideProteinSearcher();
	public static PeptideProteinSearcher getInstance() { return _INSTANCE; }
	
	public Collection<NRProteinDTO> getProteinsContainingPeptide( PeptideDTO peptide, int databaseId ) throws Exception {
		Collection<NRProteinDTO> proteins = new HashSet<NRProteinDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT nrseq_id FROM nrseq_database_peptide_protein WHERE nrseq_database_id = ? AND peptide_id = ?";
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, databaseId );
			pstmt.setInt( 2, peptide.getId() );
			
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				proteins.add( NRProteinDAO.getInstance().getNrProtein( rs.getInt( 1 ) ) );
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
		
		return proteins;
	}
	
}
