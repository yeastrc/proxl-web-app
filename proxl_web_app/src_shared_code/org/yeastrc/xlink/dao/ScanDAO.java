package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		final String sqlStr =  "SELECT scan.* "
			+ " FROM scan "
			+ " WHERE id = ?  " ;

		try {
			

			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );


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
				
//				scanDTO.setMzIntListAsString( rs.getString( "spectrum_data" ) );
			}
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sqlStr, e );
			
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
			

			connection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );


			pstmt = connection.prepareStatement( sqlStr );


			// Our results
			rs = pstmt.executeQuery();

			if ( rs.next() ) {

				maxScanId = rs.getInt( "max_id" );
			}
			
		} catch ( Exception e ) {
			
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sqlStr, e );
			
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

	
	/**
	 * @param item
	 * @return
	 * @throws Exception
	 */
	public static int save( ScanDTO item ) throws Exception {

		Connection dbConnection = null;

		try {

			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );

			save( item, dbConnection );
			
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
	 * @return
	 * @throws Exception
	 */
	public static int save( ScanDTO item, Connection dbConnection ) throws Exception {

		
		if ( ! ValidateIsCentroidValidValue.validateIsCentroidValidValue( item.getIsCentroid() ) ) {
			
			String msg = "ERROR: ScanDAO.save(item): isCentroid is not a valid value:  is:" + item.getIsCentroid();
			
			log.error( msg );
			
			throw new IllegalArgumentException(msg);
		}
		
		PreparedStatement pstmtSave = null;
		
		PreparedStatement pstmtSaveSpectrumData = null;

		ResultSet rsGenKeys = null;

		try {
			
			pstmtSave = dbConnection.prepareStatement( insertSQL, Statement.RETURN_GENERATED_KEYS );

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

			
		} catch (Exception sqlEx) {
			
			String msg = "save:Exception '" + sqlEx.toString() + ".\nSQL = " + insertSQL;
			
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
