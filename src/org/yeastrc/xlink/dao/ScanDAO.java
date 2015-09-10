package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ScanDTO;
import org.yeastrc.xlink.utils.ValidateIsCentroidValidValue;

public class ScanDAO {
	
	private static final Logger log = Logger.getLogger(ScanDAO.class);
	
	
	

	public static ScanDTO getScanFromId( int scanId ) throws Exception {


		ScanDTO scanDTO = null;;

		// Get our connection to the database.
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// Our SQL statement
		final String sqlStr =  "SELECT UNCOMPRESS( spectrum_data ) AS spectrum_data, scan.* "
			+ " FROM scan INNER JOIN scan_spectrum_data AS ssd ON  scan.id = ssd.scan_id "
			+ " WHERE id = ?  " ;


	
		try {
			

			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );


			pstmt = connection.prepareStatement( sqlStr );

			pstmt.setInt( 1, scanId );

			// Our results
			rs = pstmt.executeQuery();

			if ( rs.next() ) {

				scanDTO = new ScanDTO();

				scanDTO.setId( rs.getInt( "id" ) );
				scanDTO.setScanFileId( rs.getInt( "scan_file_id" ) );
				scanDTO.setStartScanNumber( rs.getInt( "start_scan_number" ) );
				scanDTO.setEndScanNumber( rs.getInt( "end_scan_number" ) );
				scanDTO.setLevel( rs.getInt( "level" ) );
				scanDTO.setPreMZ( rs.getBigDecimal( "preMZ" ) );
				scanDTO.setPrecursorScanNum( rs.getInt( "precursor_scan_number" ) );
				scanDTO.setPrecursorScanId( rs.getInt( "precursor_scan_id" ) );
				scanDTO.setRetentionTime( rs.getBigDecimal( "retention_time" ) );
				scanDTO.setPeakCount( rs.getInt( "peak_count" ) );
				scanDTO.setFragmentationType( rs.getString( "fragmentation_type" ) );;
				scanDTO.setIsCentroid( rs.getString( "is_centroid" ) );
				
				scanDTO.setMzIntListAsString( rs.getString( "spectrum_data" ) );
			}
			
//			CREATE TABLE scan (
//			  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//			  scan_file_id INT UNSIGNED NOT NULL,
//			  start_scan_number INT UNSIGNED NOT NULL,
//			  end_scan_number INT UNSIGNED NULL,
//			  level SMALLINT UNSIGNED NOT NULL,
//			  preMZ DECIMAL(18,9) NULL,
//			  precursor_scan_number INT NOT NULL,
//			  precursor_scan_id INT UNSIGNED NULL,
//			  retention_time DECIMAL(18,9) NULL,
//			  peak_count INT NOT NULL,
//			  fragmentation_type VARCHAR(45) NULL,
//			  is_centroid CHAR(1) NULL DEFAULT NULL

//			CREATE TABLE scan_spectrum_data (
//					  scan_id INT(10) UNSIGNED NOT NULL,
//					  spectrum_data LONGBLOB NULL DEFAULT NULL,
		

			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sqlStr, e );
			
			throw e;
			
		}
		
		
		finally {

			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (pstmt != null) {
				try { pstmt.close(); } catch (SQLException e) { ; }
				pstmt = null;
			}
			if (connection != null) {
				try { connection.close(); } catch (SQLException e) { ; }
				connection = null;
			}
		}

		return scanDTO;
	}
	
	


	public static Integer getMaxScanId(  ) throws Exception {


		Integer maxScanId = null;;

		// Get our connection to the database.
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// Our SQL statement
		final String sqlStr =  "SELECT MAX( id ) AS max_id FROM scan" ;


	
		try {
			

			connection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );


			pstmt = connection.prepareStatement( sqlStr );


			// Our results
			rs = pstmt.executeQuery();

			if ( rs.next() ) {

				maxScanId = rs.getInt( "max_id" );
			}
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.CROSSLINKS + "' sql: " + sqlStr, e );
			
			throw e;
			
		}
		
		
		finally {

			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (pstmt != null) {
				try { pstmt.close(); } catch (SQLException e) { ; }
				pstmt = null;
			}
			if (connection != null) {
				try { connection.close(); } catch (SQLException e) { ; }
				connection = null;
			}
		}

		return maxScanId;
	}
	
	
	
	
	

	private static String insertSQL = "INSERT INTO scan "
			+ "(scan_file_id, start_scan_number, end_scan_number, level, preMZ, "
			+ "  precursor_scan_number,  precursor_scan_id, retention_time, peak_count, fragmentation_type, is_centroid )"
			+ " VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

   

	private static String insert_scan_spectrum_data_SQL = "INSERT INTO scan_spectrum_data (scan_id, spectrum_data )"
		+ " VALUES ( ?, COMPRESS(?) )";



//	CREATE TABLE scan (
//	  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//	  scan_file_id INT UNSIGNED NOT NULL,
//	  start_scan_number INT UNSIGNED NOT NULL,
//	  end_scan_number INT UNSIGNED NULL,
//	  level SMALLINT UNSIGNED NOT NULL,
//	  preMZ DECIMAL(18,9) NULL,
//	  precursor_scan_number INT NOT NULL,
//	  precursor_scan_id INT UNSIGNED NULL,
//	  retention_time DECIMAL(18,9) NULL,
//	  peak_count INT NOT NULL,
//	  fragmentation_type VARCHAR(45) NULL,
//	  is_centroid TINYINT(4) NULL DEFAULT NULL

//	CREATE TABLE scan_spectrum_data (
//			  scan_id INT(10) UNSIGNED NOT NULL,
//			  spectrum_data LONGBLOB NULL DEFAULT NULL,


	public static int save( ScanDTO item, boolean saveSpectrumData ) throws Exception {

		Connection dbConnection = null;

		try {

			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			save( item, saveSpectrumData, dbConnection );
			
		} finally {

			// be sure database handles are closed

			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}

		}
		

		return item.getId();

	}
		
	/**
	 * @param item
	 * @param saveSpectrumData - true if should save the spectrum in table scan_spectrum_data
	 * @return
	 * @throws Exception
	 */
	public static int save( ScanDTO item, boolean saveSpectrumData, Connection dbConnection ) throws Exception {

		
		if ( ! ValidateIsCentroidValidValue.validateIsCentroidValidValue( item.getIsCentroid() ) ) {
			
			String msg = "ERROR: ScanDAO.save(item): isCentroid is not a valid value:  is:" + item.getIsCentroid();
			
			log.error( msg );
			System.err.println( msg );
			
			throw new IllegalArgumentException(msg);
		}
		
		PreparedStatement pstmtSave = null;
		
		PreparedStatement pstmtSaveSpectrumData = null;

		ResultSet rsGenKeys = null;

		try {
			
			pstmtSave = dbConnection.prepareStatement( insertSQL );

			int counter = 0;

			counter++;
			pstmtSave.setInt( counter, item.getScanFileId() );
			counter++;
			pstmtSave.setInt( counter, item.getStartScanNumber() );
			counter++;
			pstmtSave.setInt( counter, item.getEndScanNumber() );
			counter++;
			pstmtSave.setInt( counter, item.getLevel() );
			counter++;
			pstmtSave.setBigDecimal( counter, item.getPreMZ() );
			counter++;
			pstmtSave.setInt( counter, item.getPrecursorScanNum() );
			
			counter++;
            if( item.getPrecursorScanId() > 0 )
            	pstmtSave.setInt( counter, item.getPrecursorScanId() );
            else 
            	pstmtSave.setNull( counter, Types.INTEGER); // precursorScanId

			counter++;
			pstmtSave.setBigDecimal( counter, item.getRetentionTime() );
			counter++;
			pstmtSave.setInt( counter, item.getPeakCount() );
			counter++;
			pstmtSave.setString( counter, item.getFragmentationType() );
			counter++;
			pstmtSave.setString( counter, item.getIsCentroid() );

			
			int rowsUpdated = pstmtSave.executeUpdate();

			if ( rowsUpdated == 0 ) {

			}

			rsGenKeys = pstmtSave.getGeneratedKeys();

			if ( rsGenKeys.next() ) {

				item.setId( rsGenKeys.getInt( 1 ) );
			}

			
			//  Save the spectrum
			
			if ( saveSpectrumData ) {
			
				pstmtSaveSpectrumData = dbConnection.prepareStatement( insert_scan_spectrum_data_SQL );

				counter = 0;

				counter++;
				pstmtSaveSpectrumData.setInt( counter, item.getId() );
				counter++;
				pstmtSaveSpectrumData.setString( counter, item.getMzIntListAsString() );

				rowsUpdated = pstmtSaveSpectrumData.executeUpdate();

				if ( rowsUpdated == 0 ) {

				}

			}



		} catch (Exception sqlEx) {
			
			String msg = "save:Exception '" + sqlEx.toString() + ".\nSQL = " + insertSQL;


			System.out.println( msg );
			System.err.println( msg );
			
			log.error( msg , sqlEx);

			throw sqlEx;

		} finally {

			if (rsGenKeys != null) {
				try {
					rsGenKeys.close();
				} catch (Exception ex) {
					// ignore
				}
			}

			if (pstmtSave != null) {
				try {
					pstmtSave.close();
				} catch (Exception ex) {
					// ignore
				}
			}
			if (pstmtSaveSpectrumData != null) {
				try {
					pstmtSaveSpectrumData.close();
				} catch (Exception ex) {
					// ignore
				}
			}


		}

		return item.getId();


	}

}
