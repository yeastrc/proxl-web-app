package org.yeastrc.xlink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.ScanFileStatisticsDTO;

/**
 * table: scan_file_statistics
 */
public class ScanFileStatisticsDAO {
	
	private static final Logger log = Logger.getLogger(ScanFileStatisticsDAO.class);
	private ScanFileStatisticsDAO() { }
	public static ScanFileStatisticsDAO getInstance() { return new ScanFileStatisticsDAO(); }
	
	/**
	 * @param id
	 * @return null if not found
	 * @throws Exception
	 */
	public ScanFileStatisticsDTO getScanFileStatisticsDTOForScanFileId( int id ) throws Exception {
		ScanFileStatisticsDTO  result = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM scan_file_statistics WHERE scan_file_id = ?";
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			pstmt.setInt( 1, id );
			rs = pstmt.executeQuery();
			if ( rs.next() ) {
				result = getFromResultSet( rs );
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
	private ScanFileStatisticsDTO getFromResultSet( ResultSet rs ) throws SQLException {
		ScanFileStatisticsDTO item = new ScanFileStatisticsDTO();
		item.setScanFileId( rs.getInt( "scan_file_id" ) );
		item.setMs_1_ScanCount( rs.getInt( "ms1_scan_count" ) );
		item.setMs_1_ScanIntensitiesSummed( rs.getDouble( "ms1_scan_intensities_summed" ) );
		item.setMs_2_ScanCount( rs.getInt( "ms2_scan_count" ) );
		item.setMs_2_ScanIntensitiesSummed( rs.getDouble( "ms2_scan_intensities_summed" ) );
		return item;
	}
}
