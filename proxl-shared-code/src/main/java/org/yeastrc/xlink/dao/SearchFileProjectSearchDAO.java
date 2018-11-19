package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchFileProjectSearchDTO;

/**
 * search_file__project_search table
 */
public class SearchFileProjectSearchDAO {

	private static final Logger log = Logger.getLogger(SearchFileProjectSearchDAO.class);
	
	private SearchFileProjectSearchDAO() { }
	public static SearchFileProjectSearchDAO getInstance() { return new SearchFileProjectSearchDAO(); }
	
	/**
	 * @param projectSearchId
	 * @return
	 * @throws Exception
	 */
	public List<SearchFileProjectSearchDTO> getSearchFileProjectSearchDTOForProjectSearchId( int projectSearchId ) throws Exception {
		
		 List<SearchFileProjectSearchDTO>  resultList = new ArrayList<SearchFileProjectSearchDTO>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM search_file__project_search  WHERE project_search_id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, projectSearchId );
			rs = pstmt.executeQuery();
			while ( rs.next() ) {
				SearchFileProjectSearchDTO item = getFromResultSet( rs );
				resultList.add( item );
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
		return resultList;
	}
	
	/**
	 * @param id
	 * @return null if not found
	 * @throws Exception
	 */
	public SearchFileProjectSearchDTO getSearchFileProjectSearchDTOForId( int id ) throws Exception {
		
		SearchFileProjectSearchDTO  result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM search_file__project_search  WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if ( rs.next() ) {
				result = getFromResultSet( rs );
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
	 * @param id
	 * @return null if not found
	 * @throws Exception
	 */
	public Integer getProjectSearchIdForId( int id ) throws Exception {
		
		Integer  result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT project_search_id FROM search_file__project_search  WHERE id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if ( rs.next() ) {
				result = rs.getInt( "project_search_id" );
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
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private SearchFileProjectSearchDTO getFromResultSet( ResultSet rs ) throws SQLException {
		
		SearchFileProjectSearchDTO item = new SearchFileProjectSearchDTO();
		item.setId( rs.getInt( "id" ) );
		item.setSearchFileId( rs.getInt( "search_file_id" ) );
		item.setProjectSearchId( rs.getInt( "project_search_id" ) );
		item.setDisplayFilename( rs.getString( "display_filename" ) );

		return item;
	}

	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( SearchFileProjectSearchDTO item ) throws Exception {
		Connection conn = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			save( item, conn );
		} catch ( Exception e ) {
			String msg = "ERROR inserting item. Error getting database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\n item: " + item;
			log.error( msg, e );
			throw e;
		} finally {
			// be sure database handles are closed
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
		}
	}

	private final String INSERT_SQL = 
			"INSERT INTO search_file__project_search ( search_file_id, project_search_id, display_filename ) "
			+ "VALUES ( ?, ?, ? )";
	
	/**
	 * @param item
	 * @param conn
	 * @throws Exception
	 */
	public void save( SearchFileProjectSearchDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sql = INSERT_SQL;
		try {
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			int counter = 0;

			counter++;
			pstmt.setInt( counter, item.getSearchFileId() );
			counter++;
			pstmt.setInt( counter, item.getProjectSearchId() );
			counter++;
			pstmt.setString( counter, item.getDisplayFilename() );

			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert item" );
		} catch ( Exception e ) {
			String msg = "ERROR inserting item. database connection: '" + DBConnectionFactory.PROXL + "'"
					+ "\n item: " + item
					+ "\nsql: " + sql;
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
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
		}
	}

	private static String updateDisplayFilenameSQL = "UPDATE search_file__project_search " 
			+ " SET display_filename = ? WHERE id = ?";
	
	/**
	 * @param displayFilename
	 * @param id
	 * @return number of rows updated
	 * @throws Exception
	 */
	public int updateDisplayFilename( String displayFilename, int id ) throws Exception {
		
		int rowsUpdated = 0;
		Connection connection = null;
		PreparedStatement pstmt = null;
		String sql = updateDisplayFilenameSQL;
		try {
			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = connection.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setString( counter, displayFilename );
			counter++;
			pstmt.setInt( counter, id );
			rowsUpdated = pstmt.executeUpdate();
			if ( rowsUpdated == 0 ) {
			}
		} catch (Exception sqlEx) {
			log.error("updateDisplayFilename: Exception '" + sqlEx.toString() + ".\nSQL = " + sql , sqlEx);
			throw sqlEx;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException ex) {
					// ignore
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (Exception ex) {
					// ignore
				}
			}
		}
		return rowsUpdated;
	}
	
}
