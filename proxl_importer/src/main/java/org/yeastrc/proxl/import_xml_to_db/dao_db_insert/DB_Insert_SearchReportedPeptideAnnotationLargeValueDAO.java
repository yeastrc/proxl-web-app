package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table srch__rep_pept__annotation_large_value
 *
 */
public class DB_Insert_SearchReportedPeptideAnnotationLargeValueDAO {

	private static final Logger log = Logger.getLogger(DB_Insert_SearchReportedPeptideAnnotationLargeValueDAO.class);

	private DB_Insert_SearchReportedPeptideAnnotationLargeValueDAO() { }
	public static DB_Insert_SearchReportedPeptideAnnotationLargeValueDAO getInstance() { return new DB_Insert_SearchReportedPeptideAnnotationLargeValueDAO(); }
	

	
//	/**
//	 * This will INSERT the given data into the database.
//	 * @param item
//	 * @throws Exception
//	 */
//	public void saveToDatabase( int psmAnnotationId, String valueString ) throws Exception {
//		
//		Connection dbConnection = null;
//
//		try {
//			
////			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//
//			dbConnection = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
//			
//			saveToDatabase( psmAnnotationId, valueString, dbConnection );
//
//		} finally {
//			
////			if( dbConnection != null ) {
////				try { dbConnection.close(); } catch( Throwable t ) { ; }
////				dbConnection = null;
////			}
//			
//		}
//		
//	}
		

	private final static String INSERT_SQL = 
			"INSERT INTO srch__rep_pept__annotation_large_value "
			
			+ "(srch__rep_pept__annotation_id, value_string ) "
			
			+ "VALUES (?, ?)";

	
	/**
	 * This will INSERT the given data into the database
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( int srchReportedPeptideAnnotationId, String valueString, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = INSERT_SQL;

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, srchReportedPeptideAnnotationId );
			counter++;
			pstmt.setString( counter, valueString );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql
					+ ".  srchReportedPeptideAnnotationId: " + srchReportedPeptideAnnotationId, e );
			
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
		
		
	}
}
