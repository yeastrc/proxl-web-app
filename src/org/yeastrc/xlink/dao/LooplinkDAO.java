package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.LooplinkDTO;

/**
 * 
 *
 */
public class LooplinkDAO {
	
	private static final Logger log = Logger.getLogger(LooplinkDAO.class);

	private LooplinkDAO() { }
	public static LooplinkDAO getInstance() { return new LooplinkDAO(); }

	


	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public LooplinkDTO getLooplinkDTOById( int id ) throws Exception {
		
		
		LooplinkDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM looplink WHERE id = ?";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
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
	 * @param psmId
	 * @return
	 * @throws Exception
	 */
	public LooplinkDTO getLooplinkDTOByPsmId( int psmId ) throws Exception {
		
		
		LooplinkDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM looplink WHERE psm_id = ?";
		
		
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
	private LooplinkDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		LooplinkDTO result = new LooplinkDTO();
		
		result.setId( rs.getInt( "id" ) );
		
		result.setPeptideId( rs.getInt( "peptide_id" ) );

		result.setPeptidePosition1( rs.getInt( "peptide_position_1" ) );
		result.setProteinPosition1( rs.getInt( "protein_position_1" ) );

		result.setPeptidePosition2( rs.getInt( "peptide_position_2" ) );
		result.setProteinPosition2( rs.getInt( "protein_position_2" ) );
		
		result.setLinkerMass(  rs.getBigDecimal( "linker_mass" ) );
		
		return result;
	}
	

	
	private static final String INSERT_SQL = 
			"INSERT INTO looplink (psm_id, nrseq_id, protein_position_1, protein_position_2, peptide_id, "
			+ "peptide_position_1, peptide_position_2, linker_mass, linker_id) " 
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			
	public void save( LooplinkDTO looplink ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		final String sql = INSERT_SQL;

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, looplink.getPsm().getId() );
			pstmt.setInt( 2, looplink.getProtein().getNrseqId() );
			pstmt.setInt( 3, looplink.getProteinPosition1() );
			pstmt.setInt( 4, looplink.getProteinPosition2() );
			pstmt.setInt( 5, looplink.getPeptideId() );
			pstmt.setInt( 6, looplink.getPeptidePosition1() );
			pstmt.setInt( 7, looplink.getPeptidePosition2() );
			pstmt.setBigDecimal( 8, looplink.getLinkerMass() );
			pstmt.setInt( 9, looplink.getLinkerId() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				looplink.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert looplink" );
			
			
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
