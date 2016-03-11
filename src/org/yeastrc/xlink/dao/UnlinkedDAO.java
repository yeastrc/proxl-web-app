package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.UnlinkedDTO;



public class UnlinkedDAO {
	
	private static final Logger log = Logger.getLogger( UnlinkedDAO.class );

	private UnlinkedDAO() { }
	public static UnlinkedDAO getInstance() { return new UnlinkedDAO(); }

	


	/**
	 * Gets a random UnlinkedDTO for psmId
	 * 
	 * Do Not Use the Protein data from this 
	 * since there may be other unlinked records with different protein id data  
	 * 
	 * @param psmId
	 * @return
	 * @throws Exception
	 */
	public UnlinkedDTO getARandomUnlinkedDTOForPsmId( int psmId ) throws Exception {
		
		
		UnlinkedDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM unlinked WHERE psm_id = ?";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				result = new UnlinkedDTO();
				
				result.setId( rs.getInt( "id" ) );
				
				result.setPeptideId( rs.getInt( "peptide_id" ) );
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
	private UnlinkedDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		UnlinkedDTO result = new UnlinkedDTO();
		
		result.setId( rs.getInt( "id" ) );
		
		result.setPeptideId( rs.getInt( "peptide_id" ) );
		
		return result;
	}
	
	
	
}
