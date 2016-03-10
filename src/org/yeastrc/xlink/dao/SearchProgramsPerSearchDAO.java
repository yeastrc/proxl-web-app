package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;

/**
 * search_programs_per_search table
 */
public class SearchProgramsPerSearchDAO {
	
	private static final Logger log = Logger.getLogger(SearchProgramsPerSearchDAO.class);

	private SearchProgramsPerSearchDAO() { }
	public static SearchProgramsPerSearchDAO getInstance() { return new SearchProgramsPerSearchDAO(); }
	


	/**
	 * @param id
	 * @return null if not found
	 * @throws Exception
	 */
	public SearchProgramsPerSearchDTO getSearchProgramDTOForId( int id ) throws Exception {
		
		 SearchProgramsPerSearchDTO  result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		String sql = "SELECT * "
				+ " FROM search_programs_per_search  WHERE id = ?";
		
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
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private SearchProgramsPerSearchDTO getFromResultSet( ResultSet rs ) throws SQLException {
		
		SearchProgramsPerSearchDTO item = new SearchProgramsPerSearchDTO();

		item.setId( rs.getInt( "id" ) );
		item.setSearchId( rs.getInt( "search_id" ) );
		item.setName( rs.getString( "name" ) );
		item.setDisplayName( rs.getString( "display_name" ) );
		item.setVersion( rs.getString( "version" ) );
		item.setDescription( rs.getString( "description" ) );


		return item;
	}

	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void save( SearchProgramsPerSearchDTO item ) throws Exception {
		
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
			"INSERT INTO search_programs_per_search ( search_id, name, display_name, version, description ) "
			+ "VALUES ( ?, ?, ?, ?, ? )";
	
	/**
	 * @param item
	 * @param conn
	 * @throws Exception
	 */
	public void save( SearchProgramsPerSearchDTO item, Connection conn ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = INSERT_SQL;

		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, item.getSearchId() );
			counter++;
			pstmt.setString( counter, item.getName() );
			counter++;
			pstmt.setString( counter, item.getDisplayName() );
			counter++;
			pstmt.setString( counter, item.getVersion() );
			counter++;
			pstmt.setString( counter, item.getDescription() );
			
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
	
	

	
}

