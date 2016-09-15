package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.NoteDTO;

/**
 * DAO for note table
 *
 */
public class NoteDAO {
	
	private static final Logger log = Logger.getLogger(NoteDAO.class);

	//  private constructor
	private NoteDAO() { }
	
	/**
	 * @return newly created instance
	 */
	public static NoteDAO getInstance() { 
		return new NoteDAO(); 
	}
	
	
	//CREATE TABLE note (
//	  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//	  project_id INT UNSIGNED NOT NULL,
//	  auth_user_id_created INT UNSIGNED NOT NULL,
//	  created_date_time DATETIME NOT NULL,
//	  auth_user_id_last_updated INT UNSIGNED NOT NULL,
//	  last_updated_date_time DATETIME NOT NULL,
//	  note_text TEXT NULL,


	
	
	

	/**
	 * @param noteId
	 * @return null if not found
	 * @throws Exception
	 */
	public NoteDTO getNoteDTOForNoteId( int noteId ) throws Exception {


		NoteDTO returnItem = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM note WHERE id = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, noteId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				returnItem = populateResultObject( rs );

			}
			
		} catch ( Exception e ) {
			
			String msg = "Failed to select NoteDTO, noteId: " + noteId + ", sql: " + sql;
			
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
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private NoteDTO populateResultObject(ResultSet rs) throws SQLException {
		
		NoteDTO returnItem = new NoteDTO();

		returnItem.setId( rs.getInt( "id" ) );
		returnItem.setProjectId( rs.getInt( "project_id" ) );
		returnItem.setNoteText( rs.getString( "note_text" ) );
		returnItem.setAuthUserIdCreated( rs.getInt( "auth_user_id_created" ) );
		returnItem.setCreatedDateTime( rs.getDate( "created_date_time" ));
		returnItem.setAuthUserIdLastUpdated( rs.getInt( "auth_user_id_last_updated" ) );
		returnItem.setLastUpdatedDateTime( rs.getDate( "last_updated_date_time" ));
		
		return returnItem;
	}
	//CREATE TABLE note (
//	  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//	  project_id INT UNSIGNED NOT NULL,
//	  auth_user_id_created INT UNSIGNED NOT NULL,
//	  created_date_time DATETIME NOT NULL,
//	  auth_user_id_last_updated INT UNSIGNED NOT NULL,
//	  last_updated_date_time DATETIME NOT NULL,
//	  note_text TEXT NULL,

	


	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( NoteDTO item ) throws Exception {
		
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			save( item, dbConnection );

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
	public void save( NoteDTO item, Connection dbConnection ) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		//CREATE TABLE note (
//		  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//		  project_id INT UNSIGNED NOT NULL,
//		  auth_user_id_created INT UNSIGNED NOT NULL,
//		  created_date_time DATETIME NOT NULL,
//		  auth_user_id_last_updated INT UNSIGNED NOT NULL,
//		  last_updated_date_time DATETIME NOT NULL,
//		  note_text TEXT NULL,

	

		final String sql = "INSERT INTO note (project_id, note_text, auth_user_id_created, auth_user_id_last_updated, created_date_time, last_updated_date_time ) " +
				"VALUES ( ?, ?, ?, ?, NOW(), NOW() )";
		
		try {
			
			
			pstmt = dbConnection.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getProjectId() );
			counter++;
			pstmt.setString( counter, item.getNoteText() );
			counter++;
			pstmt.setInt( counter, item.getAuthUserIdCreated() );
			counter++;
			pstmt.setInt( counter, item.getAuthUserIdLastUpdated() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else {
				
				String msg = "Failed to insert NoteDTO, generated key not found.";
				
				log.error( msg );
				
				throw new Exception( msg );
			}
			
			
		} catch ( Exception e ) {
			
			String msg = "Failed to insert NoteDTO, sql: " + sql;
			
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
	 * Update note_text = ? 
	 * @param id
	 * @param noteText
	 * @param authUserIdLastUpdated
	 * @throws Exception
	 */
	public void updateNoteText( int id, String noteText, int authUserIdLastUpdated ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "UPDATE note SET note_text = ?, auth_user_id_last_updated = ?, last_updated_date_time = NOW() WHERE id = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			//CREATE TABLE note (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  project_id INT UNSIGNED NOT NULL,
//			  auth_user_id_created INT UNSIGNED NOT NULL,
//			  created_date_time DATETIME NOT NULL,
//			  auth_user_id_last_updated INT UNSIGNED NOT NULL,
//			  last_updated_date_time DATETIME NOT NULL,
//			  note_text TEXT NULL,

			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setString( counter, noteText );
			
			counter++;
			pstmt.setInt( counter, authUserIdLastUpdated );
			
			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to update note_text, sql: " + sql;
			
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
	 * @param id
	 * @throws Exception
	 */
	public void deleteNote( int id ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "DELETE FROM note WHERE id = ?";

		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			//CREATE TABLE note (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  project_id INT UNSIGNED NOT NULL,
//			  auth_user_id_created INT UNSIGNED NOT NULL,
//			  created_date_time DATETIME NOT NULL,
//			  auth_user_id_last_updated INT UNSIGNED NOT NULL,
//			  last_updated_date_time DATETIME NOT NULL,
//			  note_text TEXT NULL,

			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to delete note, sql: " + sql;
			
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
