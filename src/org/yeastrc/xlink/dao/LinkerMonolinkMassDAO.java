package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LinkerMonolinkMassDTO;

/**
 * linker_monolink_mass table
 */
public class LinkerMonolinkMassDAO {
	
	private static final Logger log = Logger.getLogger(LinkerMonolinkMassDAO.class);

	private LinkerMonolinkMassDAO() { }
	public static LinkerMonolinkMassDAO getInstance() { return new LinkerMonolinkMassDAO(); }
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<LinkerMonolinkMassDTO> getAllLinkerMonolinkMassDTO( ) throws Exception {
		
		 List<LinkerMonolinkMassDTO>  resultList = new ArrayList<LinkerMonolinkMassDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM linker_monolink_mass";

		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			rs = pstmt.executeQuery();
			
			while ( rs.next() ) {
				
				LinkerMonolinkMassDTO linkerMonolinkMassDTO = getFromResultSet( rs );
				resultList.add( linkerMonolinkMassDTO );
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

		return resultList;
	}
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	public List<LinkerMonolinkMassDTO> getLinkerMonolinkMassDTOForLinkerId( int linkerId ) throws Exception {
		
		List<LinkerMonolinkMassDTO>  resultList = new ArrayList<LinkerMonolinkMassDTO>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM linker_monolink_mass WHERE linker_id = ? ORDER BY mass";

		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			pstmt.setInt( 1, linkerId );
			
			rs = pstmt.executeQuery();
			
			while ( rs.next() ) {
				
				LinkerMonolinkMassDTO linkerMonolinkMassDTO = getFromResultSet( rs );
				resultList.add( linkerMonolinkMassDTO );
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

		return resultList;
	}
	
	
	
	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private LinkerMonolinkMassDTO getFromResultSet( ResultSet rs ) throws SQLException {
		
		LinkerMonolinkMassDTO linkerMonolinkMassDTO = new LinkerMonolinkMassDTO();

		linkerMonolinkMassDTO.setLinkerId( rs.getInt( "linker_id" ) );
		linkerMonolinkMassDTO.setMass( rs.getDouble( "mass" ) );
		
		return linkerMonolinkMassDTO;
	}
	
	
}

//CREATE TABLE `linker_monolink_mass` (
//`linker_id` int(10) unsigned NOT NULL,
//`mass` double NOT NULL,
//KEY `linker_id` (`linker_id`),
//KEY `mass` (`mass`),
//CONSTRAINT `linker_monolink_mass_ibfk_1` FOREIGN KEY (`linker_id`) REFERENCES `linker` (`id`) ON DELETE CASCADE
//) ENGINE=InnoDB DEFAULT CHARSET=latin1;
