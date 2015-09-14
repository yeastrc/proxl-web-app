package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchCommentDTO;

public class SearchCommentDAO {
	
	private static final Logger log = Logger.getLogger(SearchCommentDAO.class);

	private SearchCommentDAO() { }
	public static SearchCommentDAO getInstance() { return new SearchCommentDAO(); }

	/**
	 * Save the given comment to the database. Assumes it's not already in the database.
	 * @param comment
	 * @throws Exception
	 */
	public void save( SearchCommentDTO comment ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "INSERT INTO search_comment ( search_id, comment, auth_user_id, created_auth_user_id, commentCreatedTimestamp ) VALUES (?,?,?,?, NOW() )";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt.setInt( 1, comment.getSearchid() );
			pstmt.setString( 2, comment.getComment() );
			pstmt.setInt( 3, comment.getAuthUserId() );
			pstmt.setInt( 4, comment.getAuthUserId() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				comment.setId( rs.getInt( 1 ) );
				comment.setDateTime( new DateTime() );
			} else
				throw new Exception( "Failed to insert comment" );
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
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
	 * Delete the comment with the supplied id
	 * @param id
	 * @throws Exception
	 */
	public void delete( int id ) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;

		String sql = "DELETE FROM search_comment WHERE id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			pstmt.executeUpdate();
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
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
	 * Delete the supplied comment from the database (based on its id)
	 * @param comment
	 * @throws Exception
	 */
	public void delete( SearchCommentDTO comment ) throws Exception {
		delete( comment.getId() );
	}
	
	/**
	 * Load a comment with the given id from the database, return a comment object
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SearchCommentDTO load( int id ) throws Exception {
		SearchCommentDTO comment = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT search_id, comment, commentTimestamp FROM search_comment WHERE id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				comment = new SearchCommentDTO();
				
				comment.setId( id );
//				comment.setSearch( SearchDAO.getInstance().getSearch( rs.getInt( 1 ) ) );
				comment.setSearchid( rs.getInt( 1 ) );
				comment.setComment( rs.getString( 2 ) );
				comment.setDateTime( new DateTime( rs.getTimestamp( 3 ) ) );				
			}
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
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
		
		return comment;
		
	}
	
	

	/**
	 * update the comment string and auth_user_id for the given id in the database
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void updateComment( SearchCommentDTO item ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		String sql = "UPDATE search_comment SET comment = ?, auth_user_id = ? WHERE id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, item.getComment() );
			pstmt.setInt( 2, item.getAuthUserId() );
			pstmt.setInt( 3, item.getId() );
			
//			int updatedRowCount = 
			pstmt.executeUpdate();
			
			
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sql, e );
			
			throw e;
			
		} finally {
			
			// be sure database handles are closed
			
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
