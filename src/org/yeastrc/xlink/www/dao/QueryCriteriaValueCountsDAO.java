package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * DAO for query_criteria_value_counts table
 *
 */
public class QueryCriteriaValueCountsDAO {
	
	private static final Logger log = Logger.getLogger(QueryCriteriaValueCountsDAO.class);

	//  private constructor
	private QueryCriteriaValueCountsDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static QueryCriteriaValueCountsDAO getInstance() { 
		return new QueryCriteriaValueCountsDAO(); 
	}
	

	



	/**
	 * @param field
	 * @param value
	 * @throws Exception
	 */
	public void saveOrIncrement( String field, String value ) throws Exception {
		
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			saveOrIncrement( field, value, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}

	/**
	 * @param field
	 * @param value
	 * @throws Exception
	 */
	public void saveOrIncrement( String field, String value, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;

		

//		CREATE TABLE query_criteria_value_counts (
//				  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//				  field VARCHAR(45) NOT NULL,
//		  		  value VARCHAR(45) NOT NULL,
//				  count INT(10) UNSIGNED NOT NULL DEFAULT 1,

//			ADD UNIQUE INDEX `query_criteria_value_counts__field_value_unique_idx` (`field` ASC, `value` ASC),
		
		final String sql = "INSERT INTO query_criteria_value_counts ( field, value ) " 
				+ " VALUES ( ?, ? ) "
				+ " ON DUPLICATE KEY UPDATE count = count + 1";
		
		try {
			
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setString( counter, field );
			counter++;
			pstmt.setString( counter, value );


			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to insert query_criteria_value_counts, sql: " + sql;
			
			log.error( msg, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			
			
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
			if( pstmt != null ) {
				try { pstmt.close(); } catch( Throwable t ) { ; }
				pstmt = null;
			}
			

		}
		
	}
	
	
}
