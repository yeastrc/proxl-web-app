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
	public CrosslinkDTO getCrosslinkDTOByPsmId( int psmId ) throws Exception {
		
		
		CrosslinkDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM crosslink WHERE psm_id = ?";
		
		
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
	

//	CREATE TABLE crosslink (
//			  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
//			  psm_id INT(10) UNSIGNED NOT NULL,
//			  nrseq_id_1 INT(10) UNSIGNED NOT NULL,
//			  nrseq_id_2 INT(10) UNSIGNED NOT NULL,
//			  protein_1_position INT(10) UNSIGNED NOT NULL,
//			  protein_2_position INT(10) UNSIGNED NOT NULL,
//			  peptide_1_id INT(10) UNSIGNED NOT NULL,
//			  peptide_2_id INT(10) UNSIGNED NOT NULL,
//			  peptide_1_position INT(10) UNSIGNED NOT NULL,
//			  peptide_2_position INT(10) UNSIGNED NOT NULL,
//			  peptide_1_matched_peptide_id INT UNSIGNED NOT NULL,
//			  peptide_2_matched_peptide_id INT UNSIGNED NOT NULL,
//			  linker_mass DECIMAL(18,9) NOT NULL,
//	  		  linker_id INT UNSIGNED NOT NULL,

	
	private final String INSERT_SQL = 
			"INSERT INTO crosslink (psm_id, nrseq_id_1, nrseq_id_2, protein_1_position, protein_2_position, "
			+ " peptide_1_id, peptide_2_id, peptide_1_position, peptide_2_position, " 
			+ " peptide_1_matched_peptide_id, peptide_2_matched_peptide_id, linker_mass, linker_id) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	public void save( CrosslinkDTO crosslink ) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		final String sql = INSERT_SQL;

		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			pstmt = conn.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, crosslink.getPsm().getId() );
			counter++;
			pstmt.setInt( counter, crosslink.getProtein1().getNrseqId() );
			counter++;
			pstmt.setInt( counter, crosslink.getProtein2().getNrseqId() );
			counter++;
			pstmt.setInt( counter, crosslink.getProtein1Position() );
			counter++;
			pstmt.setInt( counter, crosslink.getProtein2Position() );
			counter++;
			pstmt.setInt( counter, crosslink.getPeptide1Id() );
			counter++;
			pstmt.setInt( counter, crosslink.getPeptide2Id() );
			counter++;
			pstmt.setInt( counter, crosslink.getPeptide1Position() );
			counter++;
			pstmt.setInt( counter, crosslink.getPeptide2Position() );
			counter++;
			pstmt.setInt( counter, crosslink.getPeptide1MatchedPeptideId() );
			counter++;
			pstmt.setInt( counter, crosslink.getPeptide2MatchedPeptideId() );
			counter++;
			pstmt.setBigDecimal( counter, crosslink.getLinkerMass() );
			counter++;
			pstmt.setInt( counter, crosslink.getLinkerId() );
			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();

			if( rs.next() ) {
				crosslink.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert crosslink" );
			
		} catch ( Exception e ) {
			
			String msg = "ERROR inserting crosslink. database connection: '" + DBConnectionFactory.CROSSLINKS + "'"
					+ "\n crosslink: " + crosslink
					+ "\nsql: " + sql;
			log.error( msg, e );
			
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
