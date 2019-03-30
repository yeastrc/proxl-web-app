package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * Table srch__rep_pept__annotation_large_value
 *
 */
public class SearchReportedPeptideAnnotationLargeValueDAO {
	
	private static final Logger log = LoggerFactory.getLogger( SearchReportedPeptideAnnotationLargeValueDAO.class);

	private SearchReportedPeptideAnnotationLargeValueDAO() { }
	public static SearchReportedPeptideAnnotationLargeValueDAO getInstance() { return new SearchReportedPeptideAnnotationLargeValueDAO(); }
	
	/**
	 * Get the given srch__rep_pept__annotation_large_value String from the database
	 * 
	 * @param searchReportedPeptideAnnotationId
	 * @return
	 * @throws Exception
	 */
	public String getValueString( int searchReportedPeptideAnnotationId ) throws Exception {
		
		String valueString = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT value_string FROM srch__rep_pept__annotation_large_value WHERE srch__rep_pept__annotation_id = ?";
		

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, searchReportedPeptideAnnotationId );
			
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
	public void saveToDatabase( int searchReportedPeptideAnnotationId, String valueString ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			saveToDatabase( searchReportedPeptideAnnotationId, valueString, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
		

	private final static String INSERT_SQL = 
			"INSERT INTO srch__rep_pept__annotation_large_value "
			
			+ "(srch__rep_pept__annotation_id, value_string ) "
			
			+ "VALUES (?, ?)";

	
	/**
	 * This will INSERT the given data into the database
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( int searchReportedPeptideAnnotationId, String valueString, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = INSERT_SQL;

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, searchReportedPeptideAnnotationId );
			counter++;
			pstmt.setString( counter, valueString );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql
					+ ".  searchReportedPeptideAnnotationId: " + searchReportedPeptideAnnotationId, e );
			
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
