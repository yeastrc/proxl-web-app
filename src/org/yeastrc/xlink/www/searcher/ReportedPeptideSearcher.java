package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.utils.YRC_NRSEQUtils;



public class ReportedPeptideSearcher {
	
	private static final Logger log = Logger.getLogger(ReportedPeptideSearcher.class);

	private static final ReportedPeptideSearcher _INSTANCE = new ReportedPeptideSearcher();
	public static ReportedPeptideSearcher getInstance() { return _INSTANCE; }
	private ReportedPeptideSearcher() { }
	
	
	/**
	 * is the given peptide a unique peptide in the context of the collection of searches supplied. that is,
	 * do either of the linked peptides identify more than one protein? if so, return false otherwise return
	 * true
	 * @param peptide
	 * @param searches
	 * @return
	 * @throws Exception
	 */
	public boolean isUnique( ReportedPeptideDTO peptide, Collection<Integer> peptideIds, Collection<SearchDTO> searches ) throws Exception {
		
		boolean isUniqueRetBool = true;
		
		// get the FASTAs represented in this collection of searches
		Collection<Integer> databaseIds = new HashSet<Integer>();
		for( SearchDTO search : searches ) {
			databaseIds.add( YRC_NRSEQUtils.getDatabaseIdFromName( search.getFastaFilename() ) );
		}
			
		for( int peptideId : peptideIds ) {
			
			Connection conn = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
							
				conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
				String sql = "SELECT COUNT(DISTINCT nrseq_id) FROM nrseq_database_peptide_protein WHERE nrseq_database_id IN (#DATABASE_IDS#) AND peptide_id = ?";
				
				sql = sql.replaceAll( "#DATABASE_IDS#", StringUtils.join( databaseIds, "," ) );
				
				pstmt = conn.prepareStatement( sql );
				pstmt.setInt( 1, peptideId );
	
				
				rs = pstmt.executeQuery();
				rs.next();
				
				int count = rs.getInt( 1 );
				if( count == 0 )
					throw new Exception( "Got 0 proteins for peptide?" );
				
				if( count > 1 ) {
					isUniqueRetBool = false;
					break;
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
		}
		
//		boolean isUniqueFromOldCode = isUnique_OLD_CODE( peptide, searches );
//		
//		if ( isUniqueRetBool != isUniqueFromOldCode ) {
//			
//			String msg = "Result from new isUnique(...) not match old isUnique(...), isUniqueRetBool:  " + isUniqueRetBool + ", isUniqueFromOldCode: " + isUniqueFromOldCode
//					+ ", ReportedPeptideDTO peptide.id: " + peptide.getId();
//			
//			log.error( msg );
//			
//			throw new Exception( msg );
//		}
		
		
		return isUniqueRetBool;
	}
	
	
// 	/**
//	 * is the given peptide a unique peptide in the context of the collection of searches supplied. that is,
//	 * do either of the linked peptides identify more than one protein? if so, return false otherwise return
//	 * true
//	 * @param peptide
//	 * @param searches
//	 * @return
//	 * @throws Exception
//	 */
//	public boolean isUnique_OLD_CODE( ReportedPeptideDTO peptide, Collection<SearchDTO> searches ) throws Exception {
//		boolean retBool = true;
//		
//		// get the FASTAs represented in this collection of searches
//		Collection<Integer> databaseIds = new HashSet<Integer>();
//		for( SearchDTO search : searches ) {
//			databaseIds.add( YRC_NRSEQUtils.getDatabaseIdFromName( search.getFastaFilename() ) );
//		}
//		
//		Collection<String> peptideSequences = TempCopyKojakImporterUtilsPartial.getSequencesFromKojakSequence( peptide.getSequence() ).keySet();
//		
//		for( String sequence : peptideSequences ) {
//			
//			PeptideDTO pep = PeptideDAO.getInstance().getPeptideDTO( sequence );
//			
//			Connection conn = null;
//			PreparedStatement pstmt = null;
//			ResultSet rs = null;
//			try {
//							
//				conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//				String sql = "SELECT COUNT(DISTINCT nrseq_id) FROM nrseq_database_peptide_protein WHERE nrseq_database_id IN (#DATABASE_IDS#) AND peptide_id = ?";
//				
//				sql = sql.replaceAll( "#DATABASE_IDS#", StringUtils.join( databaseIds, "," ) );
//				
//				pstmt = conn.prepareStatement( sql );
//				pstmt.setInt( 1, pep.getId() );
//	
//				
//				rs = pstmt.executeQuery();
//				rs.next();
//				
//				int count = rs.getInt( 1 );
//				if( count == 0 )
//					throw new Exception( "Got 0 proteins for peptide?" );
//				
//				if( count > 1 ) {
//					retBool = false;
//					break;
//				}
//				
//				
//			} finally {
//				
//				// be sure database handles are closed
//				if( rs != null ) {
//					try { rs.close(); } catch( Throwable t ) { ; }
//					rs = null;
//				}
//				
//				if( pstmt != null ) {
//					try { pstmt.close(); } catch( Throwable t ) { ; }
//					pstmt = null;
//				}
//				
//				if( conn != null ) {
//					try { conn.close(); } catch( Throwable t ) { ; }
//					conn = null;
//				}
//				
//			}
//		}
//		
//		
//		return retBool;
//	}
//	
	
}
