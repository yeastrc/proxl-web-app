package org.yeastrc.proxl.import_xml_to_db.spectrum.common.searchers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class ScanTableScanFileIdScanNumberGetIdSearcher {

	public static final int RECORD_NOT_FOUND_VALUE = 0;
	
	/**
	 * @param scanFileId
	 * @param scanNumber
	 * @return the 'id' field of the found record, otherwise RECORD_NOT_FOUND_VALUE   
	 * @throws Exception
	 */
	public static int recordIdForScanFileIdScanNumberInScanTable( int scanFileId, int scanNumber, Connection dbConnection ) throws Exception {
		
		int result = RECORD_NOT_FOUND_VALUE;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			String sql = "SELECT id FROM scan WHERE scan_file_id = ? AND start_scan_number = ? LIMIT 1";
			
			pstmt = dbConnection.prepareStatement( sql );
			
			int counter = 0;
			
			counter++;
			pstmt.setInt( counter, scanFileId );
			counter++;
			pstmt.setInt( counter, scanNumber );
			
			rs = pstmt.executeQuery();
			
			if( rs.next() ) {

				result = rs.getInt( "id" );
			}
			
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
			
			
		}
		
		return result;
	}
}
