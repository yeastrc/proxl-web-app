package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.IsotopeLabelDTO;

/**
 * isotope_label table
 */
public class IsotopeLabelDAO {
	
	private static final Logger log = LoggerFactory.getLogger( IsotopeLabelDAO.class);

	private IsotopeLabelDAO() { }
	public static IsotopeLabelDAO getInstance() { return new IsotopeLabelDAO(); }
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<IsotopeLabelDTO> getAllIsotopeLabelDTO( ) throws Exception {
		
		 List<IsotopeLabelDTO>  resultList = new ArrayList<IsotopeLabelDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = "SELECT * FROM isotope_label order by name";
		
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			rs = pstmt.executeQuery();
			
			while ( rs.next() ) {
				IsotopeLabelDTO linker = getFromResultSet( rs );
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
	public IsotopeLabelDTO getIsotopeLabelDTOForId( int id ) throws Exception {
		
		 IsotopeLabelDTO  result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "SELECT * FROM isotope_label WHERE id = ?";
		
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
	 * @param name
	 * @return null if not found
	 * @throws Exception
	 */
	public IsotopeLabelDTO getIsotopeLabelDTOForName( String name ) throws Exception {
		
		 IsotopeLabelDTO  result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = "SELECT * FROM isotope_label WHERE name = ?";
		
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setString( 1, name );
			
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
	private IsotopeLabelDTO getFromResultSet( ResultSet rs ) throws SQLException {
		
		IsotopeLabelDTO linker = new IsotopeLabelDTO();

		linker.setId( rs.getInt( "id" ) );
		linker.setName( rs.getString( "name" ) );
		
		return linker;
	}
	
	
}
