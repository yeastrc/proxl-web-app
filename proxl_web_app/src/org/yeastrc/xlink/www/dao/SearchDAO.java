package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.enum_classes.SearchRecordStatus;
import org.yeastrc.xlink.www.dto.SearchDTO;

/**
 * Table search
 *
 */
public class SearchDAO {
	
	private static final Logger log = Logger.getLogger(SearchDAO.class);

	private SearchDAO() { }
	public static SearchDAO getInstance() { return new SearchDAO(); }
	
//	public void deleteSearch( int id ) throws Exception {
//		
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//
//		String sql = "DELETE FROM search WHERE id = ?";
//
//		try {
//			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//			
//			pstmt = conn.prepareStatement( sql );
//			pstmt.setInt( 1, id );
//			
//			pstmt.executeUpdate();			
//			
//		} catch ( Exception e ) {
//			
//			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql, e );
//			
//			throw e;
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
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
	 * Get the given Search from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SearchDTO getSearch( int id ) throws Exception {
		SearchDTO search = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT path, directory_name, load_time, fasta_filename, name, project_id, display_order, has_scan_data FROM search WHERE id = ?";


		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				search = new SearchDTO();
				
				search.setId( id );
				search.setFastaFilename( rs.getString( "fasta_filename" ) );
				search.setPath( rs.getString( "path" ) );
				search.setDirectoryName( rs.getString( "directory_name" ) );
				search.setLoad_time( new DateTime( rs.getTimestamp( "load_time" ) ) );
				search.setName( rs.getString( "name" ) );
				search.setProjectId( rs.getInt( "project_id" ) );
				
				int hasScanDataInt = rs.getInt( "has_scan_data" );
				
				if ( Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_FALSE == hasScanDataInt ) {
					search.setHasScanData( false );
				} else {
					search.setHasScanData( true );
				}
				
				
				search.setDisplayOrder( rs.getInt( "display_order" ) );

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
		
		
		return search;
	}
	
	
	
	/**
	 * Get the project id for the search id from the database
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Integer getSearchProjectId( int id ) throws Exception {
		
		
		Integer result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT project_id FROM search WHERE id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				result = rs.getInt( "project_id" );
				
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
	
	

	/**
	 * Update the name associated with this search
	 * @param search
	 * @param name
	 * @throws Exception
	 */
	public void updateName( SearchDTO search, String name ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "UPDATE search SET name = ? WHERE id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, name );
			pstmt.setInt( 2, search.getId() );
			
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
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		
	}
	
	
	/**
	 * Update the project_id associated with this search
	 * @param searchId
	 * @param newProjectId
	 * @throws Exception
	 */
	public void updateProjectIdForSearch( int searchId, int newProjectId ) throws Exception {
		
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			updateProjectIdForSearch( searchId, newProjectId, dbConnection );
			
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
	public void updateProjectIdForSearch( int searchId, int newProjectId, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "UPDATE search SET project_id = ? WHERE id = ?";

		try {
			
			
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, newProjectId );
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
	 * Update the display_order associated with this search
	 * @param searchId
	 * @param newDisplayOrder
	 * @throws Exception
	 */
	public void updateDisplayOrderForSearch( int searchId, int newDisplayOrder, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "UPDATE search SET display_order = ? WHERE id = ?";

		try {
			
			
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, newDisplayOrder );
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
	 * Mark search as deleted
	 * @param searchId
	 * @param deletionAuthUserId
	 * @throws Exception
	 */
	public void markAsDeleted( int searchId, int deletionAuthUserId ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		Connection dbConnection = null;

		String sql = "UPDATE search SET status_id = " + SearchRecordStatus.MARKED_FOR_DELETION.value()
				+ ", marked_for_deletion_auth_user_id = ?, marked_for_deletion_timestamp = NOW() "
				+ " WHERE id = ?";

		try {

			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = dbConnection.prepareStatement( sql );

			pstmt.setInt( 1, deletionAuthUserId );
			
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

			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
		
	}
	

}
