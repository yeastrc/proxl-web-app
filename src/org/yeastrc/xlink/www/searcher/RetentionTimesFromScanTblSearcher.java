package org.yeastrc.xlink.www.searcher;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.QCPlotConstants;

/**

 *
 */
public class RetentionTimesFromScanTblSearcher {

	private static final Log log = LogFactory.getLog(RetentionTimesFromScanTblSearcher.class);
	
	private RetentionTimesFromScanTblSearcher() { }
	private static final RetentionTimesFromScanTblSearcher _INSTANCE = new RetentionTimesFromScanTblSearcher();
	public static RetentionTimesFromScanTblSearcher getInstance() { return _INSTANCE; }
	
	
	private static final String SQL_SELECT__MAIN =
			"SELECT retention_time FROM scan INNER JOIN ( ";

	
	private static final String SQL_SUB_SELECT__SCAN_IDS_MAIN =
			" SELECT psm.scan_id FROM psm  " 
					+ " WHERE psm.search_id = ? AND psm.q_value <= ? ";

	private static final String SQL_SUB_SELECT__SCAN_IDS__START_WHERE_PSM_TYPE_IN = " AND psm.type IN ( "; 

	private static final String SQL_SUB_SELECT__SCAN_IDS__END_WHERE_PSM_TYPE_IN = " ) "; 
	


	public List<BigDecimal> getRetentionTimes( 
			List<String> scansForSelectedLinkTypes, 
			int searchId,
			int scanFileId,
			double psmQValueCutoff,
			Double retentionTimeInSecondsCutoff
			) throws Exception {
		
		
		List<BigDecimal> retentionTimeList = new ArrayList<BigDecimal>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		

		
		boolean webLinkTypeCrosslink = false;
		boolean webLinkTypeLooplink = false;
		boolean webLinkTypeUnlinked = false;
		

		for ( String selectedLinkType : scansForSelectedLinkTypes ) {
			
			if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_CROSSLINK_PSM.equals( selectedLinkType ) ) {
		
				webLinkTypeCrosslink = true;
				
			} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_LOOPLINK_PSM.equals( selectedLinkType ) ) {

				webLinkTypeLooplink = true;
				
			} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_UNLINKED_PSM.equals( selectedLinkType ) ) {
				
				webLinkTypeUnlinked = true;
				
			} else {
				
				String msg = "selectedLinkType is invalid, selectedLinkType: " + selectedLinkType;
				
				log.error( selectedLinkType );
				
				throw new Exception( msg );
			}
			
		}

		StringBuilder sqlSB = new StringBuilder( 10000 );

		sqlSB.append( SQL_SELECT__MAIN );

		sqlSB.append( SQL_SUB_SELECT__SCAN_IDS_MAIN );

		if ( webLinkTypeCrosslink
				&& webLinkTypeLooplink
				&& webLinkTypeUnlinked ) {


		} else {

			sqlSB.append( SQL_SUB_SELECT__SCAN_IDS__START_WHERE_PSM_TYPE_IN );   //   ...  IN (
			

			boolean firstScanForType = true;

			for ( String scansForSelectedLinkType : scansForSelectedLinkTypes ) {

				if ( firstScanForType ) {

					firstScanForType = false;

				} else {

					sqlSB.append( " , " );    
				}

				if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_CROSSLINK_PSM.equals( scansForSelectedLinkType ) ) {

					sqlSB.append(  "'" ); 
					sqlSB.append( XLinkUtils.CROSS_TYPE_STRING );
					sqlSB.append ( "'" );

				} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_LOOPLINK_PSM.equals( scansForSelectedLinkType ) ) {

					sqlSB.append(  "'" ); 
					sqlSB.append( XLinkUtils.LOOP_TYPE_STRING );
					sqlSB.append ( "'" );


				} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_UNLINKED_PSM.equals( scansForSelectedLinkType ) ) {

					sqlSB.append(  "'" ); 
					sqlSB.append( XLinkUtils.UNLINKED_TYPE_STRING );
					sqlSB.append ( "'" );

					sqlSB.append ( ", " );
					
					sqlSB.append(  "'" ); 
					sqlSB.append( XLinkUtils.DIMER_TYPE_STRING );
					sqlSB.append ( "'" );

				} else {

					String msg = "scanForType is invalid, scanForType: " + scansForSelectedLinkType;

					log.error( scansForSelectedLinkType );

					throw new Exception( msg );
				}

			}
			
			
			sqlSB.append( SQL_SUB_SELECT__SCAN_IDS__END_WHERE_PSM_TYPE_IN );   //  )

		}

		sqlSB.append( " ) AS scan_ids ON scan_ids.scan_id = scan.id   " );


		sqlSB.append( " WHERE scan_file_id = ? " );


		if ( retentionTimeInSecondsCutoff != null ) {
			sqlSB.append( " AND retention_time < ?  " );
		}


		String sql = sqlSB.toString();



//		if ( true ) {
//			
//			throw new Exception( "TEMP" );
//		}
		
		
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			
			pstmt = conn.prepareStatement( sql );
			
			int paramCounter = 0;
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			paramCounter++;
			pstmt.setDouble( paramCounter, psmQValueCutoff );

			
			paramCounter++;
			pstmt.setInt( paramCounter, scanFileId );
			

			if ( retentionTimeInSecondsCutoff != null ) {
				
				paramCounter++;
				pstmt.setDouble( paramCounter, retentionTimeInSecondsCutoff );
			}
			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				retentionTimeList.add( rs.getBigDecimal( "retention_time" ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getRetentionTimes(), sql: " + sql;
			
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
		
		
		
		return retentionTimeList;
	}
	
	
	
}
