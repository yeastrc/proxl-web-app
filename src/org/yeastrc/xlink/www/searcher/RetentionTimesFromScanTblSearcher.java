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
	
	
	

	
	private static final String SQL_SCAN_IDS_FOR_ALL_WEB_LINK_TYPES =
			" SELECT psm.scan_id FROM psm  " 
					+ " WHERE psm.search_id = ? AND psm.q_value <= ? ";


	//  The UNION DISTINCT removes duplicate scan_id values so the sub queries don't have to

	
	private static final String SQL_CROSSLINK_PART =
			" SELECT psm.scan_id FROM psm  " 
					+ " WHERE psm.search_id = ? AND psm.q_value <= ? AND psm.type = '" + XLinkUtils.CROSS_TYPE_STRING + "' ";

	
	private static final String SQL_LOOPLINK_PART =
			" SELECT psm.scan_id FROM psm  " 
					+ " WHERE psm.search_id = ? AND psm.q_value <= ? AND psm.type = '" + XLinkUtils.LOOP_TYPE_STRING + "' ";
	
	private static final String SQL_MONOLINK_PART =
			" SELECT psm.scan_id FROM psm INNER JOIN monolink ON psm.id = monolink.psm_id " 
					+ " WHERE psm.search_id = ? AND psm.q_value <= ? ";
	
	private static final String SQL_NO_LINK_PART =
			" SELECT psm.scan_id FROM psm LEFT OUTER JOIN monolink ON psm.id = monolink.psm_id " 
					+ " WHERE psm.search_id = ? AND psm.q_value <= ? "
					+ " AND ( psm.type = '" + XLinkUtils.UNLINKED_TYPE_STRING + "' OR  psm.type = '" + XLinkUtils.DIMER_TYPE_STRING + "' ) "
					+ "   AND  monolink.psm_id IS NULL";
	


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
		boolean webLinkTypeMonolink = false;
		boolean webLinkTypeNolink = false;
		

		for ( String selectedLinkType : scansForSelectedLinkTypes ) {
			
			if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_CROSSLINK_PSM.equals( selectedLinkType ) ) {
		
				webLinkTypeCrosslink = true;
				
			} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_LOOPLINK_PSM.equals( selectedLinkType ) ) {

				webLinkTypeLooplink = true;
				
			} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_MONOLINK_PSM.equals( selectedLinkType ) ) {
				
				webLinkTypeMonolink = true;
				
			} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_NO_LINK_PSM.equals( selectedLinkType ) ) {

				webLinkTypeNolink = true;
				
			} else {
				
				String msg = "selectedLinkType is invalid, selectedLinkType: " + selectedLinkType;
				
				log.error( selectedLinkType );
				
				throw new Exception( msg );
			}
			
		}

		StringBuilder sqlSB = new StringBuilder( 10000 );

		sqlSB.append( "SELECT retention_time FROM scan INNER JOIN ( " );


		if ( webLinkTypeCrosslink
				&& webLinkTypeLooplink
				&& webLinkTypeMonolink 
				&& webLinkTypeNolink ) {

			sqlSB.append( SQL_SCAN_IDS_FOR_ALL_WEB_LINK_TYPES );

		} else {


			boolean firstScanForType = true;

			for ( String scansForSelectedLinkType : scansForSelectedLinkTypes ) {

				if ( firstScanForType ) {

					firstScanForType = false;

				} else {

					//  "UNION DISTINCT" is a union that removes duplicate rows where all the fields are evaluated as part of the duplicate match

					//   This "DISTINCT" will also remove duplicates from within the query result being UNION together

					sqlSB.append( " UNION DISTINCT " );  // DISTINCT is the default for UNION in MySQL.  
				}

				if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_CROSSLINK_PSM.equals( scansForSelectedLinkType ) ) {

					sqlSB.append( SQL_CROSSLINK_PART );

				} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_LOOPLINK_PSM.equals( scansForSelectedLinkType ) ) {

					sqlSB.append( SQL_LOOPLINK_PART );

				} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_MONOLINK_PSM.equals( scansForSelectedLinkType ) ) {


					sqlSB.append( SQL_MONOLINK_PART );

				} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_NO_LINK_PSM.equals( scansForSelectedLinkType ) ) {


					sqlSB.append( SQL_NO_LINK_PART );

				} else {

					String msg = "scanForType is invalid, scanForType: " + scansForSelectedLinkType;

					log.error( scansForSelectedLinkType );

					throw new Exception( msg );
				}

			}
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
			

			if ( webLinkTypeCrosslink
					&& webLinkTypeLooplink
					&& webLinkTypeMonolink 
					&& webLinkTypeNolink ) {

				paramCounter++;
				pstmt.setInt( paramCounter, searchId );
				paramCounter++;
				pstmt.setDouble( paramCounter, psmQValueCutoff );
				
			} else {

				for ( String scansForSelectedLinkType : scansForSelectedLinkTypes ) {

					if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_CROSSLINK_PSM.equals( scansForSelectedLinkType ) ) {

						paramCounter++;
						pstmt.setInt( paramCounter, searchId );
						paramCounter++;
						pstmt.setDouble( paramCounter, psmQValueCutoff );

					} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_LOOPLINK_PSM.equals( scansForSelectedLinkType ) ) {

						paramCounter++;
						pstmt.setInt( paramCounter, searchId );
						paramCounter++;
						pstmt.setDouble( paramCounter, psmQValueCutoff );

					} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_MONOLINK_PSM.equals( scansForSelectedLinkType ) ) {


						paramCounter++;
						pstmt.setInt( paramCounter, searchId );
						paramCounter++;
						pstmt.setDouble( paramCounter, psmQValueCutoff );

					} else if ( QCPlotConstants.RETENTION_TIME_PLOT_TYPE_SCANS_CONFIDENT_NO_LINK_PSM.equals( scansForSelectedLinkType ) ) {


						paramCounter++;
						pstmt.setInt( paramCounter, searchId );
						paramCounter++;
						pstmt.setDouble( paramCounter, psmQValueCutoff );
					}
				}
			}
			
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
