package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table psm_annotation_large_value
 *
 */
public class PsmAnnotationLargeValueDAO {
	
	private static final Logger log = Logger.getLogger(PsmAnnotationLargeValueDAO.class);

	private PsmAnnotationLargeValueDAO() { }
	public static PsmAnnotationLargeValueDAO getInstance() { return new PsmAnnotationLargeValueDAO(); }
	
	/**
	 * Get the given psm_annotation_large_value String from the database
	 * 
	 * @param psmAnnotationId
	 * @return
	 * @throws Exception
	 */
	public String getValueString( int psmAnnotationId ) throws Exception {
		
		String valueString = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT value_string FROM psm_annotation_large_value WHERE psm_annotation_id = ?";
		

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmAnnotationId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				valueString = rs.getString( "value_string" );
			}
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
			
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
		
		
		return valueString;
	}
	
	

	
	/**
	 * This will INSERT the given data into the database.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( int psmAnnotationId, String valueString ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			saveToDatabase( psmAnnotationId, valueString, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
		

	private final static String INSERT_SQL = 
			"INSERT INTO psm_annotation_large_value "
			
			+ "(psm_annotation_id, value_string ) "
			
			+ "VALUES (?, ?)";

	
	/**
	 * This will INSERT the given data into the database
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( int psmAnnotationId, String valueString, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = INSERT_SQL;

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, psmAnnotationId );
			counter++;
			pstmt.setString( counter, valueString );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql
					+ ".  psmAnnotationId: " + psmAnnotationId, e );
			
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
