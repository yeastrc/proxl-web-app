package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.KojakPsmDTO;

public class KojakPsmDAO {
	
	private static final Logger log = Logger.getLogger(KojakPsmDAO.class);

	private KojakPsmDAO() { }
	public static KojakPsmDAO getInstance() { return new KojakPsmDAO(); }
	
	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public KojakPsmDTO getKojakPsmDTOById( int id ) throws Exception {
		
		
		KojakPsmDTO result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM kojak_psm WHERE id = ?";
		
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
	 * @param kojakFileId
	 * @param scanNumber
	 * @return
	 * @throws Exception
	 */
	public List<KojakPsmDTO> getKojakPsmDTOListForKojakFileIdScanNumber( int kojakFileId, int scanNumber ) throws Exception {
		
		
		List<KojakPsmDTO> resultList = null;
		

		
		Connection dbConnection = null;
		
		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			resultList = getKojakPsmDTOListForKojakFileIdScanNumber( kojakFileId, scanNumber, dbConnection );

		} finally {
			
			// be sure database handles are closed
			
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
			
		}
		
		return resultList;
	}

	/**
	 * @param kojakFileId
	 * @param scanNumber
	 * @return
	 * @throws Exception
	 */
	public List<KojakPsmDTO> getKojakPsmDTOListForKojakFileIdScanNumber( int kojakFileId, int scanNumber, Connection dbConnection ) throws Exception {
		
		
		List<KojakPsmDTO> resultList = new ArrayList<>();
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		String sql = "SELECT * FROM kojak_psm WHERE scan_number = ? AND kojak_file_id = ? ";
		
		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, scanNumber );
			counter++;
			pstmt.setInt( counter, kojakFileId );
			
			rs = pstmt.executeQuery();
			
			while ( rs.next() ) {
				
				KojakPsmDTO item = populateFromResultSet(rs);
				resultList.add( item );
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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
		}
		
		return resultList;
	}
	
	
	
	/**
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private KojakPsmDTO populateFromResultSet(ResultSet rs) throws SQLException {
		
		 KojakPsmDTO result = new KojakPsmDTO();
		
		result.setId( rs.getInt( "id" ) );
		result.setKojakFileId( rs.getInt( "kojak_file_id" ) );

		result.setScanNumber( rs.getInt( "scan_number" ) );
		result.setObsMass( rs.getString( "obs_mass" ) );
		result.setCharge( rs.getInt( "charge" ) );
		result.setPsmMass( rs.getString( "psm_mass" ) );
		result.setPpmError( rs.getString( "ppm_error" ) );
		
		result.setScore( rs.getString( "score" ) );
		result.setDscore( rs.getString( "dscore" ) );
		result.setPepDiff( rs.getString( "pep_diff" ) );
		
		result.setPeptide1( rs.getString( "peptide_1" ) );
		result.setLink1( rs.getString( "link_1" ) );
		result.setProtein1( rs.getString( "protein_1" ) );
		result.setPeptide2( rs.getString( "peptide_2" ) );
		result.setLink2( rs.getString( "link_2" ) );
		result.setProtein2( rs.getString( "protein_2" ) );
		
		result.setLinkerMass( rs.getString( "linker_mass" ) );
		
		result.setCorr( rs.getString( "corr" ) );
		result.setLabel( rs.getString( "label" ) );
		result.setNormRank( rs.getString( "norm_rank" ) );
		result.setModMass( rs.getString( "mod_mass" ) );

		result.setRet_time( rs.getString( "ret_time" ) );

		return result;
	}
	

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Integer getChargeById( int id ) throws Exception {
		
		
		Integer result = null;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String sql = "SELECT charge FROM kojak_psm WHERE id = ?";
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {
				
				result = rs.getInt( "charge" );
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
	
	
	



//	CREATE TABLE kojak_psm (
//	  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//	  kojak_file_id INT UNSIGNED NOT NULL,
//	  scan_number INT NOT NULL,
//	  obs_mass VARCHAR(200) NOT NULL,
//	  charge SMALLINT NOT NULL,
//	  psm_mass VARCHAR(200) NOT NULL,
//	  ppm_error VARCHAR(200) NOT NULL,
//	  score VARCHAR(200) NOT NULL,
//	  dscore VARCHAR(200) NOT NULL,
//	  pep_diff VARCHAR(200) NOT NULL,
//	  peptide_1 VARCHAR(2000) NOT NULL,
//	  link_1 VARCHAR(200) NOT NULL,
//	  protein_1 VARCHAR(2000) NOT NULL,
//	  peptide_2 VARCHAR(2000) NOT NULL,
//	  link_2 VARCHAR(200) NOT NULL,
//	  protein_2 VARCHAR(2000) NOT NULL,
//	  linker_mass VARCHAR(200) NOT NULL,
//	  corr VARCHAR(200) NULL,
//	  label VARCHAR(200) NULL,
//	  norm_rank VARCHAR(200) NULL,
//	  mod_mass VARCHAR(200) NULL,




	/**
	 *
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( KojakPsmDTO item ) throws Exception {
		

		
		Connection dbConnection = null;
		
		try {
			
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			saveToDatabase( item, dbConnection );

		} finally {

			// be sure database handles are closed

			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}

		}
	}
	
	
	private String INSERT_SQL = "INSERT INTO kojak_psm "
			+ " (kojak_file_id, scan_number, obs_mass, charge, psm_mass, ppm_error, score, dscore, pep_diff, "
			+       " peptide_1, link_1, protein_1, peptide_2, link_2, protein_2, linker_mass, "
			+ 		" corr, label, norm_rank, mod_mass, ret_time  ) "
			+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

	/**
	 *
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( KojakPsmDTO item, Connection dbConnection ) throws Exception {
		
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = INSERT_SQL;
		
		try {
			
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

	
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;

			counter++;
			pstmt.setInt( counter, item.getKojakFileId() );
			counter++;
			pstmt.setInt( counter, item.getScanNumber() );
			counter++;
			pstmt.setString( counter, item.getObsMass() );
			counter++;
			pstmt.setInt( counter, item.getCharge() );
			counter++;
			pstmt.setString( counter, item.getPsmMass() );
			counter++;
			pstmt.setString( counter, item.getPpmError() );
			counter++;
			pstmt.setString( counter, item.getScore() );
			counter++;
			pstmt.setString( counter, item.getDscore() );
			counter++;
			pstmt.setString( counter, item.getPepDiff() );
			counter++;
			pstmt.setString( counter, item.getPeptide1() );
			counter++;
			pstmt.setString( counter, item.getLink1() );
			counter++;
			pstmt.setString( counter, item.getProtein1() );
			counter++;
			pstmt.setString( counter, item.getPeptide2() );
			counter++;
			pstmt.setString( counter, item.getLink2() );
			counter++;
			pstmt.setString( counter, item.getProtein2() );
			counter++;
			pstmt.setString( counter, item.getLinkerMass() );
			counter++;
			pstmt.setString( counter, item.getCorr() );
			counter++;
			pstmt.setString( counter, item.getLabel() );
			counter++;
			pstmt.setString( counter, item.getNormRank() );
			counter++;
			pstmt.setString( counter, item.getModMass() );
			counter++;
			pstmt.setString( counter, item.getRet_time() );

			
			pstmt.executeUpdate();
			
			rs = pstmt.getGeneratedKeys();
			if( rs.next() ) {
				item.setId( rs.getInt( 1 ) );
			} else
				throw new Exception( "Failed to insert KojakPsmDTO..." );

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
			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
			
		}
	}
	
}
