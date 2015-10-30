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
public class NumPeptidesForProteinCriteriaSearcher {

	private static final Logger log = Logger.getLogger(NumPeptidesForProteinCriteriaSearcher.class);
	
	private NumPeptidesForProteinCriteriaSearcher() { }
	private static final NumPeptidesForProteinCriteriaSearcher _INSTANCE = new NumPeptidesForProteinCriteriaSearcher();
	public static NumPeptidesForProteinCriteriaSearcher getInstance() { return _INSTANCE; }

	
	/**
	 * Get the number of distinct peptides (that is, distinct pair of crosslinked peptides) found that identified the given crosslinked proteins/positions
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
	public int getNumLinkedPeptidesForCrosslink(   
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
		
		final String sql = "SELECT COUNT(distinct a.reported_peptide_id) " +
				"FROM psm AS a INNER JOIN crosslink AS b ON a.id = b.psm_id " +
				"INNER JOIN search_reported_peptide AS c ON a.reported_peptide_id = c.reported_peptide_id " +
				"WHERE a.q_value <= ? AND a.search_id = ? AND ( c.q_value <= ? OR c.q_value IS NULL )   AND c.search_id = ? AND b.nrseq_id_1 = ? AND b.nrseq_id_2 = ? AND b.protein_1_position = ? AND b.protein_2_position = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
	
			pstmt = conn.prepareStatement( sql );

			pstmt.setDouble( 1,  psmCutoff );
			pstmt.setInt( 2,  searchId );
			pstmt.setDouble( 3,  peptideCutoff );
			pstmt.setInt( 4,  searchId );

			pstmt.setInt( 5,  nrseqId_protein_1 );
			pstmt.setInt( 6,  nrseqId_protein_2 );
			pstmt.setInt( 7,  position_protein_1 );
			pstmt.setInt( 8,  position_protein_2 );

			
			//  WAS   param:  SearchProteinCrosslink crosslink
			
//			pstmt.setDouble( 1, crosslink.getPsmCutoff() );
//			pstmt.setInt( 2, crosslink.getSearch().getId() );
//			pstmt.setDouble( 3, crosslink.getPeptideCutoff() );
//			pstmt.setInt( 4, crosslink.getSearch().getId() );
//			pstmt.setInt( 5, crosslink.getProtein1().getNrProtein().getNrseqId() );
//			pstmt.setInt( 6, crosslink.getProtein2().getNrProtein().getNrseqId() );
//			pstmt.setInt( 7, crosslink.getProtein1Position() );
//			pstmt.setInt( 8, crosslink.getProtein2Position() );
			
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
	
	

	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this crosslink
	 * 
	 * @param searchId
	 * @param psmCutoff
	 * @param peptideCutoff
	 * @param nrseqId_protein_1
	 * @param nrseqId_protein_2
	 * @param position_protein_1
	 * @param position_protein_2
	 * @param fastaFileDatabaseId
	 * @return
	 * @throws Exception
	 */
	public int getNumUniqueLinkedPeptidesForCrosslink( 
			int searchId,
			double psmCutoff,
			double peptideCutoff,
			int nrseqId_protein_1,
			int nrseqId_protein_2,
			int position_protein_1,
			int position_protein_2,
			int fastaFileDatabaseId

			) throws Exception {
		
		
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = " SELECT COUNT(DISTINCT a.reported_peptide_id) "+
						 "FROM psm AS a INNER JOIN crosslink AS b ON a.id = b.psm_id "+
						 "INNER JOIN search_reported_peptide AS d ON a.reported_peptide_id = d.reported_peptide_id "+
						 "INNER JOIN nrseq_database_peptide_protein AS c1 ON b.peptide_1_id = c1.peptide_id "+
						 "INNER JOIN nrseq_database_peptide_protein AS c2 ON b.peptide_2_id = c2.peptide_id "+                
						 "WHERE a.q_value <= ? AND a.search_id = ? AND ( d.q_value <= ? OR d.q_value IS NULL )   AND d.search_id = ? AND b.nrseq_id_1 = ? AND b.nrseq_id_2 = ? " +
						 "AND b.protein_1_position = ? AND b.protein_2_position = ? AND  c2.nrseq_database_id = ? AND " +
						 "c1.is_unique = 'Y' AND c2.is_unique='Y'";
			
			pstmt = conn.prepareStatement( sql );
			

			pstmt.setDouble( 1,  psmCutoff );
			pstmt.setInt( 2,  searchId );
			pstmt.setDouble( 3,  peptideCutoff );
			pstmt.setInt( 4,  searchId );

			pstmt.setInt( 5,  nrseqId_protein_1 );
			pstmt.setInt( 6,  nrseqId_protein_2 );
			pstmt.setInt( 7,  position_protein_1 );
			pstmt.setInt( 8,  position_protein_2 );
			pstmt.setInt( 9,  fastaFileDatabaseId );

			
			//  WAS   param:  SearchProteinCrosslink crosslink
//			
//			pstmt.setDouble( 1, crosslink.getPsmCutoff() );
//			pstmt.setInt( 2, crosslink.getSearch().getId() );
//			pstmt.setDouble( 3, crosslink.getPeptideCutoff() );
//			pstmt.setInt( 4, crosslink.getSearch().getId() );
//			pstmt.setInt( 5, crosslink.getProtein1().getNrProtein().getNrseqId() );
//			pstmt.setInt( 6, crosslink.getProtein2().getNrProtein().getNrseqId() );
//			pstmt.setInt( 7, crosslink.getProtein1Position() );
//			pstmt.setInt( 8, crosslink.getProtein2Position() );
//			pstmt.setInt( 9, YRC_NRSEQUtils.getDatabaseIdFromName( crosslink.getSearch().getFastaFilename() ) );

			
			rs = pstmt.executeQuery();
			if( rs.next() )
				count = rs.getInt( 1 );
			
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
	 * Get the number of distinct peptides (that is, distinct pair of crosslinked peptides) found that identified the given crosslinked proteins/positions
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
	public int getNumPeptidesForLooplink( 
			
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
		
		final String sql = "SELECT COUNT(distinct a.reported_peptide_id) " +
				"FROM psm AS a INNER JOIN looplink AS b ON a.id = b.psm_id " +
				"INNER JOIN search_reported_peptide AS c ON a.reported_peptide_id = c.reported_peptide_id " +
				"WHERE a.q_value <= ? AND a.search_id = ? AND ( c.q_value <= ? OR c.q_value IS NULL )   AND c.search_id = ? AND b.nrseq_id = ? AND b.protein_position_1 = ? AND b.protein_position_2 = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );


			pstmt.setDouble( 1,  psmCutoff );
			pstmt.setInt( 2,  searchId );
			pstmt.setDouble( 3,  peptideCutoff );
			pstmt.setInt( 4,  searchId );
			pstmt.setInt( 5,  nrseqId_protein );
			pstmt.setInt( 6,  protein_position_1 );
			pstmt.setInt( 7,  protein_position_2 );
			
			
			//  WAS   param:  SearchProteinLooplink looplink
			
//			pstmt.setDouble( 1, looplink.getPsmCutoff() );
//			pstmt.setInt( 2, looplink.getSearch().getId() );
//			pstmt.setDouble( 3, looplink.getPeptideCutoff() );
//			pstmt.setInt( 4, looplink.getSearch().getId() );
//			pstmt.setInt( 5, looplink.getProtein().getNrProtein().getNrseqId() );
//			pstmt.setInt( 6, looplink.getProteinPosition1() );
//			pstmt.setInt( 7, looplink.getProteinPosition2() );

			rs = pstmt.executeQuery();
			
			rs.next();
			count = rs.getInt( 1 );
			
		} catch ( Exception e ) {
			
			String msg = "Exception in getNumPeptidesForLooplink( ... ): sql: " + sql;
			
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
	
	

	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this crosslink
	 * @param crosslink
	 * @return
	 * @throws Exception
	 */
	public int getNumUniquePeptidesForLooplink( 

			int searchId,
			double psmCutoff,
			double peptideCutoff,
			int nrseqId_protein,
			int protein_position_1,
			int protein_position_2,
			int fastaFileDatabaseId


			 ) throws Exception {
		
		int count = 0;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			String sql = " SELECT COUNT(DISTINCT a.reported_peptide_id) "+
						 "FROM psm AS a INNER JOIN looplink AS b ON a.id = b.psm_id "+
						 "INNER JOIN search_reported_peptide AS d ON a.reported_peptide_id = d.reported_peptide_id "+
						 "INNER JOIN nrseq_database_peptide_protein AS c1 ON b.peptide_id = c1.peptide_id "+
						 "WHERE a.q_value <= ? AND a.search_id = ? AND ( d.q_value <= ? OR d.q_value IS NULL )   AND d.search_id = ? AND b.nrseq_id = ? " +
						 "AND b.protein_position_1 = ? AND b.protein_position_2 = ? AND c1.nrseq_database_id = ? AND " +
						 "c1.is_unique = 'Y'";
			
			pstmt = conn.prepareStatement( sql );
			

			pstmt.setDouble( 1,  psmCutoff );
			pstmt.setInt( 2,  searchId );
			pstmt.setDouble( 3,  peptideCutoff );
			pstmt.setInt( 4,  searchId );
			pstmt.setInt( 5,  nrseqId_protein );
			pstmt.setInt( 6,  protein_position_1 );
			pstmt.setInt( 7,  protein_position_2 );
			pstmt.setInt( 8,  fastaFileDatabaseId );
			
			
			//  WAS   param:  SearchProteinLooplink looplink
			
//			pstmt.setDouble( 1, looplink.getPsmCutoff() );
//			pstmt.setInt( 2, looplink.getSearch().getId() );
//			pstmt.setDouble( 3, looplink.getPeptideCutoff() );
//			pstmt.setInt( 4, looplink.getSearch().getId() );
//			pstmt.setInt( 5, looplink.getProtein().getNrProtein().getNrseqId() );
//			pstmt.setInt( 6, looplink.getProteinPosition1() );
//			pstmt.setInt( 7, looplink.getProteinPosition2() );
//			pstmt.setInt( 8, YRC_NRSEQUtils.getDatabaseIdFromName( looplink.getSearch().getFastaFilename() ) );

			
			rs = pstmt.executeQuery();
			if( rs.next() )
				count = rs.getInt( 1 );
			
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
