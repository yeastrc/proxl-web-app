package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.MonolinkDTO;



public class MonolinkDAO {

	private static final Logger log = Logger.getLogger(MonolinkDAO.class);
			
	private MonolinkDAO() { }
	public static MonolinkDAO getInstance() { return new MonolinkDAO(); }

	


	/**
	 * @param psmId
	 * @return
	 * @throws Exception
	 */
	public MonolinkDTO getMonolinkDTOByPsmId( int psmId ) throws Exception {
		
		
		MonolinkDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM monolink WHERE psm_id = ?";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				result = populateFromResultSet(rs);
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
	private MonolinkDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		MonolinkDTO result = new MonolinkDTO();
		
		result.setId( rs.getInt( "id" ) );
		
		result.setPeptideId( rs.getInt( "peptide_id" ) );
		result.setPeptidePosition( rs.getInt( "peptide_id" ) );
		result.setProteinPosition( rs.getInt( "protein_position" ) );
		
		return result;
	}
	
	
}
