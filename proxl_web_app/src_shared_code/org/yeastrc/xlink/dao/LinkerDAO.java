package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LinkerDTO;

/**
 * linker table
 */
public class LinkerDAO {
	
	private static final Logger log = Logger.getLogger(LinkerDAO.class);

	private LinkerDAO() { }
	public static LinkerDAO getInstance() { return new LinkerDAO(); }
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<LinkerDTO> getAllLinkerDTO( ) throws Exception {
		
		 List<LinkerDTO>  resultList = new ArrayList<LinkerDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM linker order by abbr";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			
			pstmt = conn.prepareStatement( sql );
			
			rs = pstmt.executeQuery();
			
			while ( rs.next() ) {
				
				LinkerDTO linker = getFromResultSet( rs );
				resultList.add( linker );
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
	public LinkerDTO getLinkerDTOForId( int id ) throws Exception {
		
		 LinkerDTO  result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM linker WHERE id = ?";
		
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
	 * @param abbr
	 * @return null if not found
	 * @throws Exception
	 */
	public LinkerDTO getLinkerDTOForAbbr( String abbr ) throws Exception {
		
		 LinkerDTO  result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM linker WHERE abbr = ?";
		
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setString( 1, abbr );
			
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
	private LinkerDTO getFromResultSet( ResultSet rs ) throws SQLException {
		
		LinkerDTO linker = new LinkerDTO();

		linker.setId( rs.getInt( "id" ) );
		linker.setAbbr( rs.getString( "abbr" ) );
		linker.setName( rs.getString( "name" ) );
		
		return linker;
	}
	
	
}
