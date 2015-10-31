package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;

public class SearchPeptideCrosslinkSearcher {
	
	private static final Logger log = Logger.getLogger(SearchPeptideCrosslinkSearcher.class);

	private SearchPeptideCrosslinkSearcher() { }
	private static final SearchPeptideCrosslinkSearcher _INSTANCE = new SearchPeptideCrosslinkSearcher();
	public static SearchPeptideCrosslinkSearcher getInstance() { return _INSTANCE; }
	
	/**
	 * Get all crosslink peptides corresponding to the given Criteria
	 * @param searchId
	 * @param peptideQValueCutoff
	 * @param psmQValueCutoff
	 * @param protein1Id
	 * @param protein2Id
	 * @param protein1Position
	 * @param protein2Position
	 * @return
	 * @throws Exception
	 */
	public List<SearchPeptideCrosslink> searchOnSearchProteinCrosslink( 
			int searchId,
			double peptideQValueCutoff,
			double psmQValueCutoff,
			int protein1Id,
			int protein2Id,
			int protein1Position,
			int protein2Position
			
			) throws Exception {
		
		return searchOnSearchProteinCrosslinkInternal( searchId, peptideQValueCutoff, psmQValueCutoff, protein1Id, protein2Id, protein1Position, protein2Position );
	}
	
	


	/**
	 * Get all crosslink peptides corresponding to the given crosslink protein
	 * @param searchId
	 * @param peptideQValueCutoff
	 * @param psmQValueCutoff
	 * @param protein1Id
	 * @param protein2Id
	 * @param protein1Position
	 * @param protein2Position
	 * @param proteinLink
	 * @return
	 * @throws Exception
	 */
	private List<SearchPeptideCrosslink> searchOnSearchProteinCrosslinkInternal( 
			int searchId,
			double peptideQValueCutoff,
			double psmQValueCutoff,
			int protein1Id,
			int protein2Id,
			int protein1Position,
			int protein2Position
			
			) throws Exception {
		
		List<SearchPeptideCrosslink> links = new ArrayList<SearchPeptideCrosslink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = "SELECT a.reported_peptide_id AS reported_peptide_id, a.q_value AS q_value, count(*) AS num_psms, min(b.q_value) AS best_psm_q_value " +
						  
					" FROM search_reported_peptide AS a " +
					" INNER JOIN psm AS b ON (a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id) " +
					" INNER JOIN crosslink AS c ON b.id = c.psm_id " +

					" WHERE a.search_id = ? AND ( a.q_value <= ? OR a.q_value IS NULL )   AND b.q_value <=? AND b.type = ? " +
							" AND c.nrseq_id_1 = ? AND c.nrseq_id_2 = ? AND c.protein_1_position = ? AND c.protein_2_position = ? " +
					
					" GROUP BY a.reported_peptide_id " +
					
					" ORDER BY a.q_value, a.reported_peptide_id";			

			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, searchId );
			pstmt.setDouble( 2, peptideQValueCutoff );
			pstmt.setDouble( 3, psmQValueCutoff );
			pstmt.setString( 4, XLinkUtils.getTypeString( XLinkUtils.TYPE_CROSSLINK ) );
			pstmt.setInt( 5, protein1Id );
			pstmt.setInt( 6, protein2Id );
			pstmt.setInt( 7, protein1Position );
			pstmt.setInt( 8, protein2Position );

			
//			pstmt.setInt( 1, proteinLink.getSearch().getId() );
//			pstmt.setDouble( 2, proteinLink.getPeptideCutoff() );
//			pstmt.setDouble( 3, proteinLink.getPsmCutoff() );
//			pstmt.setString( 4, XLinkUtils.getTypeString( XLinkUtils.TYPE_CROSSLINK ) );
//			pstmt.setInt( 5, proteinLink.getProtein1().getNrProtein().getNrseqId() );
//			pstmt.setInt( 6, proteinLink.getProtein2().getNrProtein().getNrseqId() );
//			pstmt.setInt( 7, proteinLink.getProtein1Position() );
//			pstmt.setInt( 8, proteinLink.getProtein2Position() );
			
			rs = pstmt.executeQuery();
			
			SearchDTO searchDTO = null;
			
			searchDTO = SearchDAO.getInstance().getSearch( searchId );
			

			while( rs.next() ) {
				SearchPeptideCrosslink link = new SearchPeptideCrosslink();

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
	 * Get the crosslink peptides corresponding to the given parameters
	 * @param search The search we're searching
	 * @param psmCutoff The q-value cutoff to use for PSMs
	 * @param peptideCutoff The q-value cutoff to use for peptides
	 * @return
	 * @throws Exception
	 */
	public List<SearchPeptideCrosslink> searchOnSearchIdPsmCutoffPeptideCutoff( SearchDTO search, double psmCutoff, double peptideCutoff ) throws Exception {
		
		
		List<SearchPeptideCrosslink> links = new ArrayList<SearchPeptideCrosslink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT a.reported_peptide_id, a.q_value, count(*) AS num_psms, min(b.q_value) AS best_psm_q_value " +
				
				" FROM search_reported_peptide AS a " +
				" INNER JOIN psm AS b ON (a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id) " +
				
				  " WHERE a.search_id = ? AND ( a.q_value <= ? OR a.q_value IS NULL )   AND b.q_value <=? AND b.type = ? " +
				  
				  " GROUP BY a.reported_peptide_id " +
				  
				  " ORDER BY a.q_value, a.reported_peptide_id";
		
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			
			pstmt = conn.prepareStatement( sql );
			
			final String typeString = XLinkUtils.getTypeString( XLinkUtils.TYPE_CROSSLINK ) ;
			
			pstmt.setInt( 1, search.getId() );
			pstmt.setDouble( 2, peptideCutoff );
			pstmt.setDouble( 3, psmCutoff );
			pstmt.setString( 4, typeString );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				SearchPeptideCrosslink link = new SearchPeptideCrosslink();
				
				link.setSearch( search );
				link.setPsmQValueCutoff( psmCutoff );
				link.setPeptideQValueCutoff( peptideCutoff );
				
				link.setReportedPeptide( ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( rs.getInt( "reported_peptide_id" ) ) );

				link.setQValue( rs.getDouble( "q_value" ) );
				if ( rs.wasNull() ) {
					link.setQValue( null );
				}

				link.setNumPsms( rs.getInt( "num_psms" ) );
				link.setBestPsmQValue( rs.getDouble( "best_psm_q_value" ) );
				
				links.add( link );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Exception in search( SearchDTO search, double psmCutoff, double peptideCutoff ), sql: " + sql;
			
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
		
		return links;
	}
	
	
	/**
	 * Get the SearchPeptideCrosslink for the given peptide in the given search with the given search parameters
	 * @param search
	 * @param psmCutoff
	 * @param peptideCutoff
	 * @param reportedPeptideId
	 * @return
	 * @throws Exception
	 */
	public SearchPeptideCrosslink searchOnSearchIdPeptideIdPsmCutoffPeptideCutoff( SearchDTO search, double psmCutoff, double peptideCutoff, int reportedPeptideId ) throws Exception {
		
		
		SearchPeptideCrosslink link = null;
				
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
				" GROUP BY a.reported_peptide_id "
				+ " ORDER BY a.reported_peptide_id";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1, reportedPeptideId );
			pstmt.setInt( 2, search.getId() );
			pstmt.setDouble( 3, peptideCutoff );
			pstmt.setDouble( 4, psmCutoff );
			pstmt.setString( 5, XLinkUtils.getTypeString( XLinkUtils.TYPE_CROSSLINK ) );
			
			rs = pstmt.executeQuery();

			if( rs.next() ) {
				link = new SearchPeptideCrosslink();
				
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
