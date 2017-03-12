package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.FolderForProjectDTO;
/**
 * DAO for folder_for_project table
 *
 */
public class FolderForProjectDAO {
	
	private static final Logger log = Logger.getLogger(FolderForProjectDAO.class);
	//  private constructor
	private FolderForProjectDAO() { }
	/**
	 * @return newly created instance
	 */
	public static FolderForProjectDAO getInstance() { 
		return new FolderForProjectDAO(); 
	}
	
	/**
	 * @param id
	 * @return project_id, null if not found
	 * @throws Exception
	 */
	public Integer getProjectId_ForId( int id ) throws Exception {
		Integer returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT project_id FROM folder_for_project WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = rs.getInt( "project_id" );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select project_id, id: " + id + ", sql: " + sql;
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
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		return returnItem;
	}
	
	/**
	 * @param id
	 * @return null if not found
	 * @throws Exception
	 */
	public FolderForProjectDTO getFolderForProjectDTO_ForId( int id ) throws Exception {
		FolderForProjectDTO returnItem = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM folder_for_project WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if( rs.next() ) {
				returnItem = populateResultObject( rs );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select ProjectDTO, id: " + id + ", sql: " + sql;
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
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		return returnItem;
	}
	
	/**
	 * @param projectId
	 * @return 
	 * @throws Exception
	 */
	public List<FolderForProjectDTO> getFolderForProjectDTO_ForProjectId( int projectId ) throws Exception {
		List<FolderForProjectDTO> returnList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT * FROM folder_for_project WHERE project_id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {
				FolderForProjectDTO returnItem = populateResultObject( rs );
				returnList.add( returnItem );
			}
		} catch ( Exception e ) {
			String msg = "Failed to select ProjectDTO, projectId: " + projectId + ", sql: " + sql;
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
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
		return returnList;
	}
	
	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private FolderForProjectDTO populateResultObject(ResultSet rs) throws SQLException {
		FolderForProjectDTO returnItem = new FolderForProjectDTO();
		returnItem.setId( rs.getInt( "id" ) );
		returnItem.setProjectId( rs.getInt( "project_id" ) );
		returnItem.setName( rs.getString( "name" ) );
		returnItem.setDisplayOrder( rs.getInt( "display_order" ) );
		return returnItem;
	}
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( FolderForProjectDTO item, int createUserId ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			save( item, createUserId, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( FolderForProjectDTO item, int createUserId, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "INSERT INTO folder_for_project (project_id, name, display_order, created_by_user_id, updated_by_user_id ) VALUES ( ?, ?, ?, ?, ? )";
		try {
			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getProjectId() );
			counter++;
			pstmt.setString( counter, item.getName() );
			counter++;
			pstmt.setInt( counter, item.getDisplayOrder() );
			counter++;
			pstmt.setInt( counter, createUserId );
			counter++;
			pstmt.setInt( counter, createUserId );
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else {
				String msg = "Failed to insert FolderForProjectDTO, generated key not found.";
				log.error( msg );
				throw new Exception( msg );
			}
		} catch ( Exception e ) {
			String msg = "Failed to insert FolderForProjectDTO, sql: " + sql;
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
	
	/**
	 * @param folderId
	 * @throws Exception
	 */
	public void delete( int folderId ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			delete( folderId, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	// primary key project_search_id
	private static final String DELETE_SQL =
			"DELETE FROM folder_for_project WHERE id = ?";
	/**
	 * @param folderId
	 * @throws Exception
	 */
	public void delete( int folderId, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = DELETE_SQL;
		try {
			pstmt = dbConnection.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, folderId );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to delete folderId, sql: " + sql;
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
	
	/**
	 * Update name = ? 
	 * @param id
	 * @param name
	 * @throws Exception
	 */
	public void updateName( int id, String name, int updateUserId ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE folder_for_project SET name = ?, updated_by_user_id = ? WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setString( counter, name );
			counter++;
			pstmt.setInt( counter, updateUserId );
			counter++;
			pstmt.setInt( counter, id );
			pstmt.executeUpdate();
		} catch ( Exception e ) {
			String msg = "Failed to update name, sql: " + sql;
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
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
	}
	
	/**
	 * Update the display_order associated with this search
	 * @param folderId
	 * @param newDisplayOrder
	 * @throws Exception
	 */
	public void updateDisplayOrder( int folderId, int newDisplayOrder, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "UPDATE folder_for_project SET display_order = ? WHERE id = ?";
		try {
			pstmt = dbConnection.prepareStatement( sql );
			pstmt.setInt( 1, newDisplayOrder );
			pstmt.setInt( 2, folderId );
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
