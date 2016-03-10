package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnifiedRepPepMatchedPeptideLookupDTO;

public class DB_Insert_UnifiedRepPepMatchedPeptideLookupDAO {


	private static final Logger log = Logger.getLogger(DB_Insert_UnifiedRepPepMatchedPeptideLookupDAO.class);

	private DB_Insert_UnifiedRepPepMatchedPeptideLookupDAO() { }
	public static DB_Insert_UnifiedRepPepMatchedPeptideLookupDAO getInstance() { return new DB_Insert_UnifiedRepPepMatchedPeptideLookupDAO(); }
	

//	/**
//	 * @param entry
//	 * @throws Exception
//	 */
//	public void save( UnifiedRepPepMatchedPeptideLookupDTO entry ) throws Exception {
//		
//		Connection conn = null;
//
//		try {
//			
//					  
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//			
//			save( entry, conn );
//			
//		} catch ( Exception e ) {
//			
//			throw e;
//			
//		} finally {
//			
//			// be sure database handles are closed
//
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//		
//	}
	
	

	
	/**
	 * @param entry
	 * @throws Exception
	 */
	public void save( UnifiedRepPepMatchedPeptideLookupDTO entry, Connection conn ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO unified_rep_pep_matched_peptide_lookup (unified_reported_peptide_id, peptide_id, peptide_order, link_position_1, link_position_2) VALUES ( ?, ?, ?, ?, ? )";
		  
		try {
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter,  entry.getUnifiedReportedPeptideId() );
			counter++;
			pstmt.setInt( counter,  entry.getPeptideId() );
			counter++;
			pstmt.setInt( counter,  entry.getPeptideOrder() );
			
			counter++;
			
			if ( entry.getLinkPosition1() != null ) {
				pstmt.setInt( counter, entry.getLinkPosition1() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}

			counter++;
			
			if ( entry.getLinkPosition2() != null ) {
				pstmt.setInt( counter, entry.getLinkPosition2() );
			} else {
				pstmt.setNull( counter, java.sql.Types.INTEGER );
			}
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				entry.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert UnifiedRpMatchedPeptideDTO" );
			
			
		} catch ( Exception e ) {
			
			String msg = "Failed to save, sql: " + sql;
			
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
			
			
		}
		
	}
}
