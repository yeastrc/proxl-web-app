package org.yeastrc.xlink.www.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.www.dto.FolderProjectSearchDTO;

/**
 * DAO for folder_project_search table
 *
 */
public class FolderProjectSearchDAO {

	private static final Logger log = Logger.getLogger(FolderProjectSearchDAO.class);
	
	//  private constructor
	private FolderProjectSearchDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static FolderProjectSearchDAO getInstance() { 
		return new FolderProjectSearchDAO(); 
	}

	/**
	 * @param projectId
	 * @return 
	 * @throws Exception
	 */
	public List<FolderProjectSearchDTO> getFolderProjectSearchDTO_ForProjectId( int projectId ) throws Exception {
		
		List<FolderProjectSearchDTO> returnList = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "SELECT fps.* FROM folder_project_search AS fps INNER JOIN folder_for_project AS ffp ON fps.folder_id = ffp.id  WHERE ffp.project_id = ?";
				 
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectId );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {
				FolderProjectSearchDTO returnItem = populateResultObject( rs );
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
	private FolderProjectSearchDTO populateResultObject(ResultSet rs) throws SQLException {
		FolderProjectSearchDTO returnItem = new FolderProjectSearchDTO();
		returnItem.setFolderId( rs.getInt( "folder_id" ) );
		returnItem.setProjectSearchId( rs.getInt( "project_search_id" ) );
		return returnItem;
	}

	/**
	 * @param item
	 * @throws Exception
	 */
	public void saveOrUpdate( FolderProjectSearchDTO item ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			saveOrUpdate( item, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	// primary key project_search_id
	private static final String SAVE_OR_UPDATE_SQL =
			"INSERT INTO folder_project_search ( project_search_id, folder_id ) VALUES ( ?, ? )"
					+ " ON DUPLICATE KEY UPDATE folder_id = ?";

	/**
	 * @param item
	 * @throws Exception
	 */
	public void saveOrUpdate( FolderProjectSearchDTO item, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = SAVE_OR_UPDATE_SQL;
		try {
			pstmt = dbConnection.prepareStatement( sql );
//			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getProjectSearchId() );
			counter++;
			pstmt.setInt( counter, item.getFolderId() );
			counter++;
			pstmt.setInt( counter, item.getFolderId() );
			
			pstmt.executeUpdate();
			
//			rs = pstmt.getGeneratedKeys();
//			if( rs.next() ) {
//				item.setId( rs.getInt( 1 ) );
//			} else {
//				String msg = "Failed to insert FolderProjectSearchDTO, generated key not found.";
//				log.error( msg );
//				throw new Exception( msg );
//			}
		} catch ( Exception e ) {
			String msg = "Failed to insert FolderProjectSearchDTO, sql: " + sql;
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
	 * @param projectSearchId
	 * @throws Exception
	 */
	public void delete( int projectSearchId ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			delete( projectSearchId, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	// primary key project_search_id
	private static final String DELETE_SQL =
			"DELETE FROM folder_project_search WHERE project_search_id = ?";

	/**
	 * @param projectSearchId
	 * @throws Exception
	 */
	public void delete( int projectSearchId, Connection dbConnection ) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = DELETE_SQL;
		try {
			pstmt = dbConnection.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, projectSearchId );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			String msg = "Failed to delete projectSearchId, sql: " + sql;
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
	public void updateName( int id, String name ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = "UPDATE folder_for_project SET name = ? WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setString( counter, name );
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
	
}
