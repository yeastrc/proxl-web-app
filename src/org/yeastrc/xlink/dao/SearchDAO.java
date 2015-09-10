package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchDTO;

/**
 * Table search
 *
 */
public class SearchDAO {
	
	private static final Logger log = Logger.getLogger(SearchDAO.class);

	private SearchDAO() { }
	public static SearchDAO getInstance() { return new SearchDAO(); }
	
	public void deleteSearch( int id ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		String sql = "DELETE FROM search WHERE id = ?";

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

		String sql = "SELECT path, directory_name, load_time, fasta_filename, name, project_id, insert_complete, search_program, display_order FROM search WHERE id = ?";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
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
				search.setInsertComplete( rs.getBoolean( "insert_complete" ) );
				search.setSearchProgram( rs.getString( "search_program" ) );
				search.setDisplayOrder( rs.getInt( "display_order" ) );

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
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				result = rs.getInt( "project_id" );
				
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
		
		
		return result;
	}
	
	

	
	/**
	 * This will INSERT the given SearchDTO into the database... even if an id is already set.
	 * This will result in a new id being set in the object.
	 * @param pr
	 * @throws Exception
	 */
	public void saveToDatabase( SearchDTO pr ) throws Exception {
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			saveToDatabase( pr, dbConnection );

		} finally {
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
	}
		
		
	/**
	 * This will INSERT the given SearchDTO into the database... even if an id is already set.
	 * This will result in a new id being set in the object.
	 * @param pr
	 * @throws Exception
	 */
	public void saveToDatabase( SearchDTO pr, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO search (path, directory_name, fasta_filename, name, project_id, insert_complete, search_program) VALUES (?, ?, ?, ?, ?, ?, ?)";
		
		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setString( counter, pr.getPath() );
			counter++;
			pstmt.setString( counter, pr.getDirectoryName() );
			counter++;
			pstmt.setString( counter, pr.getFastaFilename() );
			
			counter++;  // Call getName_AsActuallyInObject() since getName() substitutes a value if the property name is null
			pstmt.setString( counter, pr.getName_AsActuallyInObject() );
			
			counter++;
			pstmt.setInt( counter, pr.getProjectId() );
			
			counter++;
			if ( pr.isInsertComplete() ) {
				pstmt.setInt( counter, SearchDTO.PERC_SEARCH_INSERT_COMPLETE_TRUE );
			} else {
				pstmt.setInt( counter, SearchDTO.PERC_SEARCH_INSERT_COMPLETE_FALSE );
			}
			
			counter++;
			pstmt.setString( counter, pr.getSearchProgram() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				pr.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert search for " + pr.getPath() );
			
			
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
		
		
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
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, name );
			pstmt.setInt( 2, search.getId() );
			
			pstmt.executeUpdate();
			
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
	 * Update the project_id associated with this search
	 * @param searchId
	 * @param newProjectId
	 * @throws Exception
	 */
	public void updateProjectIdForSearch( int searchId, int newProjectId ) throws Exception {
		
		
		Connection dbConnection = null;

		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

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
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

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
				pstmt.setInt( 1, SearchDTO.PERC_SEARCH_INSERT_COMPLETE_TRUE );
			} else {
				pstmt.setInt( 1, SearchDTO.PERC_SEARCH_INSERT_COMPLETE_FALSE );
			}
			
			pstmt.setInt( 2, searchId );
			
			pstmt.executeUpdate();
			
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
			
		}
		
		
	}
	

}
