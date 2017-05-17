package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchDTO_Importer;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.SearchRecordStatus;

/**
 * table search
 *
 */
public class SearchDAO_Importer {

	private static final Logger log = Logger.getLogger(SearchDAO_Importer.class);
	private SearchDAO_Importer() { }
	public static SearchDAO_Importer getInstance() { return new SearchDAO_Importer(); }
	
	/**
	 * This will INSERT the given SearchDTO into the database... even if an id is already set.
	 * This will result in a new id being set in the object.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchDTO_Importer item ) throws Exception {
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
			+ " (path, directory_name, fasta_filename, has_scan_data, status_id, created_by_user_id ) "
			+ " VALUES (?, ?, ?, ?, " +  SearchRecordStatus.IMPORTING.value() + ", ? "
			+ " )";
	/**
	 * This will INSERT the given SearchDTO into the database... even if an id is already set.
	 * This will result in a new id being set in the object.
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( SearchDTO_Importer item, Connection conn ) throws Exception {
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
			counter++;
			if ( item.isHasScanData() ) {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE );
			} else {
				pstmt.setInt( counter, Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE );
			}
			counter++;
			if ( item.getCreatedByUserId() != null ) {
				pstmt.setInt( counter, item.getCreatedByUserId() );
			} else {
				pstmt.setNull(counter, java.sql.Types.INTEGER );
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
	 * Update the status_id associated with this search
	 * @param searchId
	 * @param status
	 * @throws Exception
	 */
	public void updateStatus( int searchId, SearchRecordStatus status ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			updateStatus( searchId, status, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * Update the status_id associated with this search
	 * @param searchId
	 * @param status
	 * @throws Exception
	 */
	public void updateStatus( int searchId, SearchRecordStatus status, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "UPDATE search SET status_id = ?, import_end_timestamp = NOW() WHERE id = ?";
		try {
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, status.value() );
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
	

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SearchDTO_Importer getSearchDTO_ImporterById( int id ) throws Exception {
		SearchDTO_Importer result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM search WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				result = new SearchDTO_Importer();
				result.setId( rs.getInt( "id" ) );
				result.setFastaFilename( rs.getString( "fasta_filename" ) );
				result.setPath( rs.getString( "path" ) );
				result.setDirectoryName( rs.getString( "directory_name" ) );
				result.setLoad_time( new DateTime( rs.getTimestamp( "load_time" ) ) );
				int hasScanDataInt = rs.getInt( "has_scan_data" );
				if ( Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE == hasScanDataInt ) {
					result.setHasScanData( false );
				} else {
					result.setHasScanData( true );
				}
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
		return result;
	}
}
