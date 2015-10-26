package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.objects.SearchPeptideLooplink;

public class SearchPeptideLooplinkSearcher {

	private SearchPeptideLooplinkSearcher() { }
	private static final SearchPeptideLooplinkSearcher _INSTANCE = new SearchPeptideLooplinkSearcher();
	public static SearchPeptideLooplinkSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * Get all looplink peptides corresponding to the given looplink protein
	 * @param proteinLink
	 * @return
	 * @throws Exception
	 */
	public List<SearchPeptideLooplink> searchOnSearchProteinLooplink( SearchProteinLooplink proteinLink ) throws Exception {
		

		
		return searchOnSearchProteinLooplinkInternal(
				proteinLink.getSearch().getId(),
				proteinLink.getPeptideCutoff(),
				proteinLink.getPsmCutoff(),
				proteinLink.getProtein().getNrProtein().getNrseqId(),
				proteinLink.getProteinPosition1(),
				proteinLink.getProteinPosition2(),
				proteinLink);
	}

	

	/**
	 * Get all looplink peptides corresponding to the given Criteria
	 * @param searchId
	 * @param peptideQValueCutoff
	 * @param psmQValueCutoff
	 * @param proteinId
	 * @param proteinPosition1
	 * @param proteinPosition2
	 * @return
	 * @throws Exception
	 */
	public List<SearchPeptideLooplink> searchOnSearchProteinLooplink( 
			int searchId,
			double peptideQValueCutoff,
			double psmQValueCutoff,
			int proteinId,
			int proteinPosition1,
			int proteinPosition2
			
			) throws Exception {
		
		return searchOnSearchProteinLooplinkInternal( searchId, peptideQValueCutoff, psmQValueCutoff, proteinId, proteinPosition1, proteinPosition2, null /* proteinLink */ );
	}
	
	



	/**
	 * Get all looplink peptides corresponding to the given looplink protein
	 * 
	 * @param searchId
	 * @param peptideQValueCutoff
	 * @param psmQValueCutoff
	 * @param proteinId
	 * @param proteinPosition1
	 * @param proteinPosition2
	 * @param proteinLink
	 * @return
	 * @throws Exception
	 */
	private List<SearchPeptideLooplink> searchOnSearchProteinLooplinkInternal( 
			int searchId,
			double peptideQValueCutoff,
			double psmQValueCutoff,
			int proteinId,
			int proteinPosition1,
			int proteinPosition2,
			
			SearchProteinLooplink proteinLink
			
			) throws Exception {


		List<SearchPeptideLooplink> links = new ArrayList<SearchPeptideLooplink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT a.reported_peptide_id, a.q_value, count(*) AS num_psms, min(b.q_value) AS best_psm_q_value " +
						  "FROM search_reported_peptide AS a INNER JOIN psm AS b " +
						  "ON (a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id) " +
						  "INNER JOIN looplink AS c ON b.id = c.psm_id " +
						  "WHERE a.search_id = ? AND ( a.q_value <= ? OR a.q_value IS NULL )   AND b.q_value <=? AND b.type = ? " +
						  "AND c.nrseq_id = ? AND c.protein_position_1 = ? AND c.protein_position_2 = ? " +
						  "GROUP BY a.reported_peptide_id " +
						  "ORDER BY a.q_value, a.reported_peptide_id";
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, searchId );
			pstmt.setDouble( 2, peptideQValueCutoff );
			pstmt.setDouble( 3, psmQValueCutoff );
			pstmt.setString( 4, XLinkUtils.getTypeString( XLinkUtils.TYPE_LOOPLINK ) );
			pstmt.setInt( 5, proteinId );
			pstmt.setInt( 6, proteinPosition1 );
			pstmt.setInt( 7, proteinPosition2 );
			
			rs = pstmt.executeQuery();
			
			
			SearchDTO searchDTO = null;
			
			if ( proteinLink != null ) {
				searchDTO = proteinLink.getSearch();
			} else {
				
				searchDTO = SearchDAO.getInstance().getSearch( searchId );
			}
			

			while( rs.next() ) {
				SearchPeptideLooplink link = new SearchPeptideLooplink();
				
				link.setSearch( searchDTO );
				link.setPsmQValueCutoff( psmQValueCutoff );
				link.setPeptideQValueCutoff( peptideQValueCutoff );
				
				link.setReportedPeptide( ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( rs.getInt( "reported_peptide_id" ) ) );
				
				link.setQValue( rs.getDouble( "q_value" ) );
				if ( rs.wasNull() ) {
					link.setQValue( null );
				}

				link.setNumPsms( rs.getInt( "num_psms" ) );
				link.setBestPsmQValue( rs.getDouble( "best_psm_q_value" ) );

				links.add( link );
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
		
		return links;
	}
	
	/**
	 * Get the looplink peptides corresponding to the given parameters
	 * @param search The search we're searching
	 * @param psmCutoff The q-value cutoff to use for PSMs
	 * @param peptideCutoff The q-value cutoff to use for peptides
	 * @return
	 * @throws Exception
	 */
	public List<SearchPeptideLooplink> search( SearchDTO search, double psmCutoff, double peptideCutoff ) throws Exception {
		List<SearchPeptideLooplink> links = new ArrayList<SearchPeptideLooplink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT a.reported_peptide_id, a.q_value, count(*) AS num_psms, min(b.q_value) AS best_psm_q_value " +
						  "FROM search_reported_peptide AS a INNER JOIN psm AS b " +
						  "ON (a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id) " +
						  "WHERE a.search_id = ? AND ( a.q_value <= ? OR a.q_value IS NULL )   AND b.q_value <=? AND b.type = ?" +
						  "GROUP BY a.reported_peptide_id " +
						  "ORDER BY a.q_value, a.reported_peptide_id";			
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, search.getId() );
			pstmt.setDouble( 2, peptideCutoff );
			pstmt.setDouble( 3, psmCutoff );
			pstmt.setString( 4, XLinkUtils.getTypeString( XLinkUtils.TYPE_LOOPLINK ) );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				SearchPeptideLooplink link = new SearchPeptideLooplink();
				
				link.setSearch( search );
				link.setPsmQValueCutoff( psmCutoff );
				link.setPeptideQValueCutoff( peptideCutoff );
				
				link.setReportedPeptide ( ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( rs.getInt( "reported_peptide_id" ) ) );

				link.setQValue( rs.getDouble( "q_value" ) );
				if ( rs.wasNull() ) {
					link.setQValue( null );
				}

				link.setNumPsms( rs.getInt( "num_psms" ) );
				link.setBestPsmQValue( rs.getDouble( "best_psm_q_value" ) );
				
				links.add( link );
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
		
		return links;
	}
	
	
	/**
	 * Get the SearchPeptideLooplink for the given peptide in the given search with the given search parameters
	 * @param search
	 * @param psmCutoff
	 * @param peptideCutoff
	 * @param reportedPeptideId
	 * @return
	 * @throws Exception
	 */
	public SearchPeptideLooplink search( SearchDTO search, double psmCutoff, double peptideCutoff, int reportedPeptideId ) throws Exception {
		SearchPeptideLooplink link = null;
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT a.reported_peptide_id, a.q_value, count(*) AS num_psms, min(b.q_value) AS best_psm_q_value " +
					" , psrp.svm_score, psrp.pep, psrp.p_value " +
					" FROM search_reported_peptide AS a INNER JOIN psm AS b " +
					" ON (a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id) " +
					
					" LEFT OUTER JOIN percolator_search_reported_peptide AS psrp " +
					" ON (a.search_id = psrp.search_id AND a.reported_peptide_id = psrp.reported_peptide_id) " +

					" WHERE a.reported_peptide_id = ? AND a.search_id = ? AND ( a.q_value <= ? OR a.q_value IS NULL )   AND b.q_value <=? AND b.type = ?" +
					" GROUP BY a.reported_peptide_id ";
			
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1, reportedPeptideId );
			pstmt.setInt( 2, search.getId() );
			pstmt.setDouble( 3, peptideCutoff );
			pstmt.setDouble( 4, psmCutoff );
			pstmt.setString( 5, XLinkUtils.getTypeString( XLinkUtils.TYPE_LOOPLINK ) );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {
				link = new SearchPeptideLooplink();
				
				link.setSearch( search );
				link.setPsmQValueCutoff( psmCutoff );
				link.setPeptideQValueCutoff( peptideCutoff );
				
				link.setReportedPeptide ( ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( rs.getInt( "reported_peptide_id" ) ) );

				link.setQValue( rs.getDouble( "q_value" ) );
				
				if ( rs.wasNull() ) {
					
					link.setQValue( null );
				}

				link.setNumPsms( rs.getInt( "num_psms" ) );
				link.setBestPsmQValue( rs.getDouble( "best_psm_q_value" ) );
				
				link.setPep( rs.getDouble( "pep" ) );
				
				if ( ! rs.wasNull() ) {
					
					link.setPepPopulated(true);
				}
				
				link.setSvmScore( rs.getDouble( "svm_score" ) );

				if ( ! rs.wasNull() ) {
					
					link.setSvmScorePopulated(true);
				}
				
				link.setpValue( rs.getDouble( "p_value" ) );
				
				if ( ! rs.wasNull() ) {
					
					link.setpValuePopulated(true);
				}
				
				
				
				if( rs.next() )
					throw new Exception( "Got two instances of a peptide in a single search..." );
				
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
		
		return link;
	}
	
}
