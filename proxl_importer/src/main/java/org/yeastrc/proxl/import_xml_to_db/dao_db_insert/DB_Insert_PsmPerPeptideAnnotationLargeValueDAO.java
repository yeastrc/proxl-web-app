package org.yeastrc.proxl.import_xml_to_db.dao_db_insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table psm_per_peptide_annotation_large_value
 *
 */
public class DB_Insert_PsmPerPeptideAnnotationLargeValueDAO {


	private static final Logger log = LoggerFactory.getLogger( DB_Insert_PsmPerPeptideAnnotationLargeValueDAO.class);

	private DB_Insert_PsmPerPeptideAnnotationLargeValueDAO() { }
	public static DB_Insert_PsmPerPeptideAnnotationLargeValueDAO getInstance() { return new DB_Insert_PsmPerPeptideAnnotationLargeValueDAO(); }


//	/**
//	 * This will INSERT the given data into the database.
//	 * @param item
//	 * @throws Exception
//	 */
//	public void saveToDatabase( int psmPerPeptideAnnotationId, String valueString ) throws Exception {
//		
//		Connection dbConnection = null;
//
//		try {
//
////			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//			
//			dbConnection = ImportDBConnectionFactory.getInstance().getInsertControlCommitConnection();
//			
//			saveToDatabase( psmPerPeptideAnnotationId, valueString, dbConnection );
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
			"INSERT INTO psm_per_peptide_annotation_large_value "
			
			+ "(psm_per_peptide_annotation_id, value_string ) "
			
			+ "VALUES (?, ?)";

	
	/**
	 * This will INSERT the given data into the database
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( int psmPerPeptideAnnotationId, String valueString, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = INSERT_SQL;

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, psmPerPeptideAnnotationId );
			counter++;
			pstmt.setString( counter, valueString );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql
					+ ".  psmAnnotationId: " + psmPerPeptideAnnotationId, e );
			
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
