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
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, psmId );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				result = populateFromResultSet(rs);
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
	private MonolinkDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		MonolinkDTO result = new MonolinkDTO();
		
		result.setId( rs.getInt( "id" ) );
		
		result.setPeptideId( rs.getInt( "peptide_id" ) );
		result.setPeptidePosition( rs.getInt( "peptide_id" ) );
		result.setProteinPosition( rs.getInt( "protein_position" ) );
		
		return result;
	}
	
//	CREATE TABLE IF NOT EXISTS `proxl`.`monolink` (
//			  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//			  `psm_id` INT(10) UNSIGNED NOT NULL,
//			  `nrseq_id` INT(10) UNSIGNED NOT NULL,
//			  `protein_position` INT(10) UNSIGNED NOT NULL,
//			  `peptide_id` INT(10) UNSIGNED NOT NULL,
//			  `peptide_position` INT(10) UNSIGNED NOT NULL,
//	  			linker_id INT UNSIGNED NOT NULL,

	
	
	public void save( MonolinkDTO monolink ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO monolink (psm_id, nrseq_id, protein_position, peptide_id, peptide_position, linker_id) " +
				"VALUES (?, ?, ?, ?, ?, ?)";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, monolink.getPsm().getId() );
			pstmt.setInt( 2, monolink.getProtein().getNrseqId() );
			pstmt.setInt( 3, monolink.getProteinPosition() );
			pstmt.setInt( 4, monolink.getPeptideId() );
			pstmt.setInt( 5, monolink.getPeptidePosition() );
			pstmt.setInt( 6, monolink.getLinkerId() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				monolink.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert monolink" );
			
			
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
	
}
