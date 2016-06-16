package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchDTO;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;

/**
 * table search
 *
 */
public class SearchDAO {

	private static final Logger log = Logger.getLogger(SearchDAO.class);

	private SearchDAO() { }
	public static SearchDAO getInstance() { return new SearchDAO(); }


	
	/**
	 * This will INSERT the given SearchDTO into the database... even if an id is already set.
	 * This will result in a new id being set in the object.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchDTO item ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			saveToDatabase( item, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
		
		
	private static final String INSERT_SQL =
			"INSERT INTO search "
			+ " (path, directory_name, fasta_filename, name, project_id, insert_complete, no_scan_data) "
			+ " VALUES (?, ?, ?, ?, ?, ?, ?)";
			
	
	/**
	 * This will INSERT the given SearchDTO into the database... even if an id is already set.
	 * This will result in a new id being set in the object.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = INSERT_SQL;
		

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setString( counter, item.getPath() );
			counter++;
			pstmt.setString( counter, item.getDirectoryName() );
			counter++;
			pstmt.setString( counter, item.getFastaFilename() );
			
			counter++;  // Call getName_AsActuallyInObject() since getName() substitutes a value if the property name is null
			pstmt.setString( counter, item.getName_AsActuallyInObject() );
			
			counter++;
			pstmt.setInt( counter, item.getProjectId() );
			
			counter++;
			if ( item.isInsertComplete() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}
			
			counter++;
			if ( item.isNoScanData() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert search for " + item.getPath() );
			
			
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
		
		
	}
	

	/**
	 * Update the project_id associated with this search
	 * @param searchId
	 * @param insertComplete
	 * @throws Exception
	 */
	public void updateInsertComplete( int searchId, boolean insertComplete ) throws Exception {
		
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			updateInsertComplete( searchId, insertComplete, dbConnection );
			
		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
	

	
	/**
	 * Update the project_id associated with this search
	 * @param searchId
	 * @param newProjectId
	 * @throws Exception
	 */
	public void updateInsertComplete( int searchId, boolean insertComplete, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "UPDATE search SET insert_complete = ? WHERE id = ?";

		try {
			
			
			pstmt = dbConnection.prepareStatement( sql );
			

			if ( insertComplete ) {
				pstmt.setInt( 1, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( 1, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}
			
			pstmt.setInt( 2, searchId );
			
			pstmt.executeUpdate();
			
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
			
		}
		
		
	}
	

	
}
