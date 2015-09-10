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
	 * @param psmId
	 * @return
	 * @throws Exception
	 */
	public UnlinkedDTO getUnlinkedDTOByPsmId( int psmId ) throws Exception {
		
		
		UnlinkedDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM unlinked WHERE psm_id = ?";
		
		
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
	private UnlinkedDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		UnlinkedDTO result = new UnlinkedDTO();
		
		result.setId( rs.getInt( "id" ) );
		
		result.setPeptideId( rs.getInt( "peptide_id" ) );
		
		return result;
	}
	
	
	/**
	 * @param unlinked
	 * @throws Exception
	 */
	public void save( UnlinkedDTO unlinked ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "INSERT INTO unlinked (psm_id, nrseq_id, peptide_id) VALUES (?, ?, ?)";

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, unlinked.getPsm().getId() );
			pstmt.setInt( 2, unlinked.getProtein().getNrseqId() );
			pstmt.setInt( 3, unlinked.getPeptideId() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				unlinked.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert unlinked" );
			
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
