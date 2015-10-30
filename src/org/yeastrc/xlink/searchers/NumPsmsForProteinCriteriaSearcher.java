package org.yeastrc.xlink.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * 
 *
 */
public class NumPsmsForProteinCriteriaSearcher {

	private static final Logger log = Logger.getLogger(NumPsmsForProteinCriteriaSearcher.class);
	
	private NumPsmsForProteinCriteriaSearcher() { }
	private static final NumPsmsForProteinCriteriaSearcher _INSTANCE = new NumPsmsForProteinCriteriaSearcher();
	public static NumPsmsForProteinCriteriaSearcher getInstance() { return _INSTANCE; }

	
	/**
	 * Get the number of PSMs in the database corresponding to the given crosslink with its given cutoffs
	 * 
	 * @param searchId
	 * @param psmCutoff
	 * @param peptideCutoff
	 * @param nrseqId_protein_1
	 * @param nrseqId_protein_2
	 * @param position_protein_1
	 * @param position_protein_2
	 * @return
	 * @throws Exception
	 */
	public int getNumPsmsForCrosslink(   
			int searchId,
			double psmCutoff,
			double peptideCutoff,
			int nrseqId_protein_1,
			int nrseqId_protein_2,
			int position_protein_1,
			int position_protein_2
			
			) throws Exception {
		
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = 
				"SELECT COUNT(*) FROM psm AS a INNER JOIN search_reported_peptide AS b ON ( a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id ) "
				+ "INNER JOIN crosslink AS c ON a.id = c.psm_id WHERE a.search_id = ? AND a.q_value <= ? AND ( b.q_value <= ? OR b.q_value IS NULL )   AND "
				+ "c.nrseq_id_1 = ? AND c.nrseq_id_2 = ? AND c.protein_1_position = ? AND c.protein_2_position = ? ";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
	
			pstmt = conn.prepareStatement( sql );

			pstmt.setInt( 1,  searchId );
			pstmt.setDouble( 2,  psmCutoff );
			pstmt.setDouble( 3,  peptideCutoff );
			pstmt.setInt( 4,  nrseqId_protein_1 );
			pstmt.setInt( 5,  nrseqId_protein_2 );
			pstmt.setInt( 6,  position_protein_1 );
			pstmt.setInt( 7,  position_protein_2 );

			
			//  WAS   param:  SearchProteinCrosslink crosslink
			
//			pstmt.setInt( 1,  crosslink.getSearch().getId() );
//			pstmt.setDouble( 2,  crosslink.getPsmCutoff() );
//			pstmt.setDouble( 3,  crosslink.getPeptideCutoff() );
//			pstmt.setInt( 4,  crosslink.getProtein1().getNrProtein().getNrseqId() );
//			pstmt.setInt( 5,  crosslink.getProtein2().getNrProtein().getNrseqId() );
//			pstmt.setInt( 6,  crosslink.getProtein1Position() );
//			pstmt.setInt( 7,  crosslink.getProtein2Position() );

			rs = pstmt.executeQuery();
			
			rs.next();
			count = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPsmsForCrosslink( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		
		return count;
	}
	
	
	//  WAS   SearchPsmSearcher.getNumPsms( SearchProteinLooplink looplink )   in proxl web app

	/**
	 * Get the number of PSMs in the database corresponding to the given looplink with its given cutoffs
	 * 
	 * @param searchId
	 * @param psmCutoff
	 * @param peptideCutoff
	 * @param nrseqId_protein
	 * @param protein_position_1
	 * @param protein_position_2
	 * @return
	 * @throws Exception
	 */
	public int getNumPsmsForLooplink( 
			
			int searchId,
			double psmCutoff,
			double peptideCutoff,
			int nrseqId_protein,
			int protein_position_1,
			int protein_position_2

			) throws Exception {
		
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = 
				"SELECT COUNT(*) FROM psm AS a INNER JOIN search_reported_peptide AS b ON ( a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id ) "
				+ "INNER JOIN looplink AS c ON a.id = c.psm_id WHERE a.search_id = ? AND a.q_value <= ? AND ( b.q_value <= ? OR b.q_value IS NULL )   AND "
				+ "c.nrseq_id = ? AND c.protein_position_1 = ? AND c.protein_position_2 = ? ";
	
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );


			pstmt.setInt( 1,  searchId );
			pstmt.setDouble( 2,  psmCutoff );
			pstmt.setDouble( 3,  peptideCutoff );
			pstmt.setInt( 4,  nrseqId_protein );
			pstmt.setInt( 5,  protein_position_1 );
			pstmt.setInt( 6,  protein_position_2 );
			
			
			//  WAS   param:  SearchProteinLooplink looplink
			
//			pstmt.setInt( 1,  looplink.getSearch().getId() );
//			pstmt.setDouble( 2,  looplink.getPsmCutoff() );
//			pstmt.setDouble( 3,  looplink.getPeptideCutoff() );
//			pstmt.setInt( 4,  looplink.getProtein().getNrProtein().getNrseqId() );
//			pstmt.setInt( 5,  looplink.getProteinPosition1() );
//			pstmt.setInt( 6,  looplink.getProteinPosition2() );

			rs = pstmt.executeQuery();
			
			rs.next();
			count = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPsmsForLooplink( ... ): sql: " + sql;
			
			log.error( msg );
			
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
		
		
		return count;
	}
	
}
