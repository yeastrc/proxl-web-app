package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.objects.SearchPeptideUnlink;



public class SearchPeptideUnlinkedSearcher {
	
	private static final Logger log = Logger.getLogger(SearchPeptideUnlinkedSearcher.class);

	private SearchPeptideUnlinkedSearcher() { }
	private static final SearchPeptideUnlinkedSearcher _INSTANCE = new SearchPeptideUnlinkedSearcher();
	public static SearchPeptideUnlinkedSearcher getInstance() { return _INSTANCE; }
	

	
	/**
	 * Get the SearchPeptideUnlink for the given peptide in the given search with the given search parameters
	 * @param search
	 * @param psmCutoff
	 * @param peptideCutoff
	 * @param reportedPeptideId
	 * @return
	 * @throws Exception
	 */
	public SearchPeptideUnlink searchOnSearchIdPeptideIdPsmCutoffPeptideCutoff( SearchDTO search, double psmCutoff, double peptideCutoff, int reportedPeptideId ) throws Exception {
		
		
		SearchPeptideUnlink link = null;
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT a.reported_peptide_id, a.q_value, count(*) AS num_psms, min(b.q_value) AS best_psm_q_value " +
				" , psrp.svm_score, psrp.pep, psrp.p_value " +
				" FROM search_reported_peptide AS a INNER JOIN psm AS b " +
				" ON (a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id) " +
				
				" LEFT OUTER JOIN percolator_search_reported_peptide AS psrp " +
				" ON (a.search_id = psrp.search_id AND a.reported_peptide_id = psrp.reported_peptide_id) " +
				
				" WHERE a.reported_peptide_id = ? AND a.search_id = ? AND ( a.q_value <= ? OR a.q_value IS NULL )   AND b.q_value <=? AND b.type = ? " +
				" GROUP BY a.reported_peptide_id ";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1, reportedPeptideId );
			pstmt.setInt( 2, search.getId() );
			pstmt.setDouble( 3, peptideCutoff );
			pstmt.setDouble( 4, psmCutoff );
			pstmt.setString( 5, XLinkUtils.getTypeString( XLinkUtils.TYPE_UNLINKED ) );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {
				link = new SearchPeptideUnlink();
				
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
			
		} catch ( Exception e ) {
			
			String msg = "Exception in search( SearchDTO search, double psmCutoff, double peptideCutoff, ReportedPeptideDTO peptide ), sql: " + sql;
			
			log.error( msg, e );
			
			throw e;
			
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
