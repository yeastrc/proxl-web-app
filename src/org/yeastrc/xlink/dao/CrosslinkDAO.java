package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.CrosslinkDTO;

/**
 * table crosslink
 *
 */
public class CrosslinkDAO {

	private static final Logger log = Logger.getLogger(CrosslinkDAO.class);
	
	private CrosslinkDAO() { }
	public static CrosslinkDAO getInstance() { return new CrosslinkDAO(); }


	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CrosslinkDTO getCrosslinkDTOById( int id ) throws Exception {
		
		
		CrosslinkDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM crosslink WHERE id = ?";
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
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
	 * @param psmId
	 * @return
	 * @throws Exception
	 */
	public CrosslinkDTO getCrosslinkDTOByPsmId( int psmId ) throws Exception {
		
		
		CrosslinkDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM crosslink WHERE psm_id = ?";
		
		
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
	private CrosslinkDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		CrosslinkDTO result = new CrosslinkDTO();
		
		result.setId( rs.getInt( "id" ) );
		
		result.setPeptide1Id( rs.getInt( "peptide_1_id" ) );
		result.setPeptide1Position( rs.getInt( "peptide_1_position" ) );
		result.setPeptide1MatchedPeptideId( rs.getInt( "peptide_1_matched_peptide_id" ) );
		result.setProtein1Position( rs.getInt( "protein_1_position" ) );

		result.setPeptide2Id( rs.getInt( "peptide_2_id" ) );
		result.setPeptide2Position( rs.getInt( "peptide_2_position" ) );
		result.setPeptide2MatchedPeptideId( rs.getInt( "peptide_2_matched_peptide_id" ) );
		result.setProtein2Position( rs.getInt( "protein_2_position" ) );
		
		result.setLinkerMass(  rs.getBigDecimal( "linker_mass" ) );
		
		return result;
	}
	

	
}
