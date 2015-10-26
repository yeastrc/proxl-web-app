package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.objects.SearchPeptideMonolink;
import org.yeastrc.xlink.www.objects.SearchProteinMonolink;



public class SearchPeptideMonolinkSearcher {
	
	private static final Logger log = Logger.getLogger(SearchPeptideMonolinkSearcher.class);

	private SearchPeptideMonolinkSearcher() { }
	private static final SearchPeptideMonolinkSearcher _INSTANCE = new SearchPeptideMonolinkSearcher();
	public static SearchPeptideMonolinkSearcher getInstance() { return _INSTANCE; }
	
	

//	CREATE TABLE IF NOT EXISTS proxl.monolink (
//			  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//			  psm_id INT(10) UNSIGNED NOT NULL,
//			  nrseq_id INT(10) UNSIGNED NOT NULL,
//			  protein_position INT(10) UNSIGNED NOT NULL,
//			  peptide_id INT(10) UNSIGNED NOT NULL,
//			  peptide_position INT(10) UNSIGNED NOT NULL,

		
	
	final String SEARCH_USING_MONO_LINK_VALUES = 
			"SELECT monolink.id, monolink.psm_id, monolink.nrseq_id, monolink.protein_position, monolink.peptide_id, monolink.peptide_position, "
					+ " srp.reported_peptide_id, srp.q_value "
//					+ " srp.reported_peptide_id, srp.svm_score, srp.q_value, srp.pep, srp.p_value, "
					+ " , psrp.svm_score, psrp.pep, psrp.p_value "
					
					+ " , COUNT( DISTINCT( psm.id ) ) AS num_psms, MIN( psm.q_value ) AS best_psm_q_value " 
					
					+ "FROM search_reported_peptide AS srp  " 
					
					+ "INNER JOIN psm ON (srp.search_id = psm.search_id AND srp.reported_peptide_id = psm.reported_peptide_id) " 
					+ "INNER JOIN monolink ON psm.id = monolink.psm_id " 

					+ " LEFT OUTER JOIN percolator_search_reported_peptide AS psrp " 
					+ " ON (srp.search_id = psrp.search_id AND srp.reported_peptide_id = psrp.reported_peptide_id) " 

					
					+ "WHERE srp.search_id = ? AND ( srp.q_value <= ? OR srp.q_value IS NULL )   AND psm.q_value <=? "  
					+ 		"AND monolink.nrseq_id = ? AND monolink.protein_position = ? " 
					
					+ "GROUP BY srp.reported_peptide_id " 
					
					+ "ORDER BY srp.q_value, psrp.p_value, psrp.pep, monolink.id";

	
	/**
	 * Get all monolink peptides corresponding to the given monolink protein
	 * @param proteinLink
	 * @return
	 * @throws Exception
	 */
	public List<SearchPeptideMonolink> search( SearchProteinMonolink proteinLink ) throws Exception {
		
		List<SearchPeptideMonolink> links = new ArrayList<SearchPeptideMonolink>();
				
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = SEARCH_USING_MONO_LINK_VALUES;
		
	
				
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, proteinLink.getSearch().getId() );
			counter++;
			pstmt.setDouble( counter, proteinLink.getPeptideCutoff() );
			counter++;
			pstmt.setDouble( counter, proteinLink.getPsmCutoff() );
			counter++;
			pstmt.setInt( counter, proteinLink.getProtein().getNrProtein().getNrseqId() );
			counter++;
			pstmt.setInt( counter, proteinLink.getProteinPosition() );
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				SearchPeptideMonolink link = new SearchPeptideMonolink();
				
				//  From monolink table:
				
				link.setMonolinkId( rs.getInt( "id" ) );
				link.setMonolinkPsmId( rs.getInt( "psm_id" ) );
				link.setMonolinkNrseqProteinId( rs.getInt( "nrseq_id" ) );
				link.setMonolinkProteinPosition( rs.getInt( "protein_position" ) );
				link.setMonolinkPeptideId( rs.getInt( "peptide_id" ) );
				link.setMonolinkPeptidePosition( rs.getInt( "peptide_position" ) );
				
				
				link.setSearch( proteinLink.getSearch() );
				link.setPsmQValueCutoff( proteinLink.getPsmCutoff() );
				link.setPeptideQValueCutoff( proteinLink.getPeptideCutoff() );
				
//				final String sql = "SELECT a.reported_peptide_id, a.svm_score, a.q_value, a.pep, a.p_value, count(*), min(b.q_value) " +

				link.setReportedPeptide( ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( rs.getInt( "reported_peptide_id" ) ) );
				link.setSvmScore( rs.getDouble( "svm_score" ) );

				link.setQValue( rs.getDouble( "q_value" ) );
				if ( rs.wasNull() ) {
					link.setQValue( null );
				}

				link.setPep( rs.getDouble( "pep" ) );
				link.setpValue( rs.getInt( "p_value" ) );
				link.setNumPsms( rs.getInt( "num_psms" ) );
				link.setBestPsmQValue( rs.getDouble( "best_psm_q_value" ) );
				
				links.add( link );
			}
			
		} catch ( Exception e ) {
			
			String msg = "Exception in search( SearchProteinMonolink proteinLink ), SQL: " + sql;
			
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

	
	
//	UNUSED and untested
//	
//	/**
//	 * Get the monolink peptides corresponding to the given parameters
//	 * @param search The search we're searching
//	 * @param psmCutoff The q-value cutoff to use for PSMs
//	 * @param peptideCutoff The q-value cutoff to use for peptides
//	 * @return
//	 * @throws Exception
//	 */
//	public List<SearchPeptideMonolink> search( SearchDTO search, double psmCutoff, double peptideCutoff ) throws Exception {
//		
//		List<SearchPeptideMonolink> links = new ArrayList<SearchPeptideMonolink>();
//				
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		
//		final String sql = "SELECT a.reported_peptide_id, a.svm_score, a.q_value, a.pep, a.p_value, count(*), min(b.q_value) " +
//				  "FROM search_reported_peptide AS a INNER JOIN psm AS b " +
//				  "ON (a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id) " +
//				  "WHERE a.search_id = ? AND ( a.q_value <= ? OR a.q_value IS NULL )   AND b.q_value <=? " + // AND b.type = ? 
//				  "GROUP BY a.reported_peptide_id " +
//				  "ORDER BY a.q_value, a.p_value, a.pep";	
//		
//		try {
//						
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//		
//			
//			pstmt = conn.prepareStatement( sql );
//			
//			pstmt.setInt( 1, search.getId() );
//			pstmt.setDouble( 2, peptideCutoff );
//			pstmt.setDouble( 3, psmCutoff );
//			
//			rs = pstmt.executeQuery();
//
//			while( rs.next() ) {
//				SearchPeptideMonolink link = new SearchPeptideMonolink();
//				
//				link.setSearch( search );
//				link.setPsmQValueCutoff( psmCutoff );
//				link.setPeptideQValueCutoff( peptideCutoff );
//				
//				link.setReportedPeptide(KojakPeptideDAO.getInstance().getReportedPeptideFromDatabase( rs.getInt( 1 ) ) );
//				link.setSvmScore( rs.getDouble( 2 ) );
//				link.setQValue( rs.getDouble( 3 ) );
//				link.setPep( rs.getDouble( 4 ) );
//				link.setpValue( rs.getInt( 5 ) );
//				link.setNumPsms( rs.getInt( 6 ) );
//				link.setBestPsmQValue( rs.getDouble( 7 ) );
//				
//				links.add( link );
//			}
//
//		} catch ( Exception e ) {
//			
//			String msg = "Exception in search( SearchDTO search, double psmCutoff, double peptideCutoff ), SQL: " + sql;
//			
//			log.error( msg, e );
//			
//			throw e;
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//		
//		return links;
//	}
	
//	UNUSED and untested
//	
//
//	/**
//	 * Get the SearchPeptideMonolink for the given peptide in the given search with the given search parameters
//	 * @param search
//	 * @param psmCutoff
//	 * @param peptideCutoff
//	 * @param peptide
//	 * @return
//	 * @throws Exception
//	 */
//	public SearchPeptideMonolink search( SearchDTO search, double psmCutoff, double peptideCutoff, ReportedPeptideDTO peptide ) throws Exception {
//		SearchPeptideMonolink link = null;
//				
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		
//		final String sql = "SELECT a.reported_peptide_id, a.svm_score, a.q_value, a.pep, a.p_value, count(*), min(b.q_value) " +
//				  "FROM search_reported_peptide AS a INNER JOIN psm AS b " +
//				  "ON (a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id) " +
//				  "WHERE a.reported_peptide_id = ? AND a.search_id = ? AND ( a.q_value <= ? OR a.q_value IS NULL )   AND b.q_value <=? " + // AND b.type = ? 
//				  "GROUP BY a.reported_peptide_id ";
//		
//		try {
//						
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//
//			
//			pstmt = conn.prepareStatement( sql );
//
//			pstmt.setInt( 1, peptide.getId() );
//			pstmt.setInt( 2, search.getId() );
//			pstmt.setDouble( 3, peptideCutoff );
//			pstmt.setDouble( 4, psmCutoff );
//			
//			rs = pstmt.executeQuery();
//
//			if( rs.next() ) {
//				link = new SearchPeptideMonolink();
//				
//				link.setSearch( search );
//				link.setPsmQValueCutoff( psmCutoff );
//				link.setPeptideQValueCutoff( peptideCutoff );
//				
//				link.setReportedPeptide(KojakPeptideDAO.getInstance().getReportedPeptideFromDatabase( rs.getInt( 1 ) ) );
//				link.setSvmScore( rs.getDouble( 2 ) );
//				link.setQValue( rs.getDouble( 3 ) );
//				link.setPep( rs.getDouble( 4 ) );
//				link.setpValue( rs.getInt( 5 ) );
//				link.setNumPsms( rs.getInt( 6 ) );
//				link.setBestPsmQValue( rs.getDouble( 7 ) );
//
//				if( rs.next() )
//					throw new Exception( "Got two instances of a peptide in a single search..." );
//				
//			}
//
//		} catch ( Exception e ) {
//			
//			String msg = "Exception in search( SearchDTO search, double psmCutoff, double peptideCutoff, ReportedPeptideDTO peptide ), SQL: " + sql;
//			
//			log.error( msg, e );
//			
//			throw e;
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//		
//		return link;
//	}
	
}
