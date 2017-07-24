package org.yeastrc.proxl.import_xml_to_db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.dbcp2.DelegatingPreparedStatement;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ScanFileStatisticsDTO;

/**
 * table: scan_file_statistics
 *
 */
public class ScanFileStatistics_Importer_DAO {

	private static final Logger log = Logger.getLogger(ScanFileStatistics_Importer_DAO.class);
	private ScanFileStatistics_Importer_DAO() { }
	public static ScanFileStatistics_Importer_DAO getInstance() { return new ScanFileStatistics_Importer_DAO(); }
	
	/**
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( ScanFileStatisticsDTO item ) throws Exception {
		Connection dbConnection = null;
		try {
			dbConnection = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			saveToDatabase( item, dbConnection );
		} finally {
			if( dbConnection != null ) {
				try { dbConnection.close(); } catch( Throwable t ) { ; }
				dbConnection = null;
			}
		}
	}
	
	private static final String INSERT_SQL =
			"INSERT INTO scan_file_statistics "
			+ " (scan_file_id, ms1_scan_count, ms1_scan_intensities_summed, ms2_scan_count, ms2_scan_intensities_summed ) "
			+ " VALUES (?, ?, ?, ?, ? )"
			+ " ON DUPLICATE KEY UPDATE only_for_on_duplicate_update = 1 "// On duplicate is update that won't change the record
			;  
	/**
	 * @param item
	 * @throws Exception
	 */
	public void saveToDatabase( ScanFileStatisticsDTO item, Connection conn ) throws Exception {
//		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = INSERT_SQL;
		try {
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
//			pstmt = conn.prepareStatement( sql, Statement.RETURN_GENERATED_KEYS );
			pstmt = conn.prepareStatement( sql );
			int counter = 0;
			counter++;
			pstmt.setInt( counter, item.getScanFileId() );
			counter++;
			pstmt.setLong( counter, item.getMs_1_ScanCount() );
			counter++;
			pstmt.setDouble( counter, item.getMs_1_ScanIntensitiesSummed() );
			counter++;
			pstmt.setLong( counter, item.getMs_2_ScanCount() );
			counter++;
			pstmt.setDouble( counter, item.getMs_2_ScanIntensitiesSummed() );
			
			pstmt.executeUpdate();
			
//			rs = pstmt.getGeneratedKeys();
//			if( rs.next() ) {
//				item.setId( rs.getInt( 1 ) );
//			} else
//				throw new Exception( "Failed to insert search for " + item.getPath() );
		} catch ( Exception e ) {
			log.error( "ERROR: database connection: '" + DBConnectionFactory.PROXL + "' sql: " + sql
					+ "\n item: " + item
					+ "\n Executed Statement: " + ((DelegatingPreparedStatement)pstmt).getDelegate().toString(), e );

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
