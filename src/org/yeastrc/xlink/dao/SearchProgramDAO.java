package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.SearchProgramDTO;

/**
 * search_program table
 */
public class SearchProgramDAO {
	
	private static final Logger log = Logger.getLogger(SearchProgramDAO.class);

	private SearchProgramDAO() { }
	public static SearchProgramDAO getInstance() { return new SearchProgramDAO(); }
	


	/**
	 * @param id
	 * @return null if not found
	 * @throws Exception
	 */
	public SearchProgramDTO getSearchProgramDTOForId( int id ) throws Exception {
		
		 SearchProgramDTO  result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		String sql = "SELECT * "
				+ " FROM search_program  WHERE id = ?";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				
				result = getFromResultSet( rs );
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
	 * @param id
	 * @return null if not found
	 * @throws Exception
	 */
	public Integer getSearchProgramIdForShortName( String nameShort ) throws Exception {
		
		Integer  result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		String sql = "SELECT id "
				+ " FROM search_program  WHERE short_name = ?";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setString( 1, nameShort );
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				
				result = rs.getInt( "id" );
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
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private SearchProgramDTO getFromResultSet( ResultSet rs ) throws SQLException {
		
		SearchProgramDTO item = new SearchProgramDTO();

		item.setId( rs.getInt( "id" ) );
		item.setName( rs.getString( "name" ) );
		item.setShortName( rs.getString( "display_filename" ) );
		item.setDisplayName( rs.getString( "filename" ) );
		item.setDescription( rs.getString( "description" ) );


		return item;
	}
	
//	Programatic add not allowed
	
//	
//	/**
//	 * @param item
//	 * @throws Exception
//	 */
//	public void save( SearchProgramDTO item ) throws Exception {
//		
//		Connection conn = null;
//		
//		try {
//			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//
//			save( item, conn );
//
//		} catch ( Exception e ) {
//			
//			String msg = "ERROR inserting item. Error getting database connection: '" + DBConnectionFactory.CROSSLINKS + "'"
//					+ "\n item: " + item;
//			log.error( msg, e );
//			
//			throw e;
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//	}
//	
//	
//
//	private final String INSERT_SQL = 
//			"INSERT INTO search_program ( name, short_name, display_name, description ) "
//			+ "VALUES ( ?, ?, ?, ? )";
//	
//	/**
//	 * @param item
//	 * @param conn
//	 * @throws Exception
//	 */
//	public void save( SearchProgramDTO item, Connection conn ) throws Exception {
//		
////		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		
//		final String sql = INSERT_SQL;
//
//		try {
//			
////			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//			
//			pstmt = conn.prepareStatement( sql );
//			
//			int counter = 0;
//			
//			counter++;
//			pstmt.setString( counter, item.getName() );
//			counter++;
//			pstmt.setString( counter, item.getShortName() );
//			counter++;
//			pstmt.setString( counter, item.getDisplayName() );
//			counter++;
//			pstmt.setString( counter, item.getDescription() );
//			
//			pstmt.executeUpdate();
//			
//			rs = pstmt.getGeneratedKeys();
//
//			if( rs.next() ) {
//				item.setId( rs.getInt( 1 ) );
//			} else
//				throw new Exception( "Failed to insert item" );
//			
//		} catch ( Exception e ) {
//			
//			String msg = "ERROR inserting item. database connection: '" + DBConnectionFactory.CROSSLINKS + "'"
//					+ "\n item: " + item
//					+ "\nsql: " + sql;
//			log.error( msg, e );
//			
//			throw e;
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			
////			if( conn != null ) {
////				try { conn.close(); } catch( Throwable t ) { ; }
////				conn = null;
////			}
//			
//		}
//		
//	}
//	
//	

	
}

