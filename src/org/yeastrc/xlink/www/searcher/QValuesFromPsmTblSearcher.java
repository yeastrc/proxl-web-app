package org.yeastrc.xlink.www.searcher;

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
public class QValuesFromPsmTblSearcher {

	private static final Log log = LogFactory.getLog(QValuesFromPsmTblSearcher.class);
	
	private QValuesFromPsmTblSearcher() { }
	private static final QValuesFromPsmTblSearcher _INSTANCE = new QValuesFromPsmTblSearcher();
	public static QValuesFromPsmTblSearcher getInstance() { return _INSTANCE; }
	
	
	


	//  The UNION DISTINCT removes duplicate psm.id values so the sub queries don't have to

	
	private static final String SQL_CROSSLINK_PART =
			" SELECT psm.id FROM psm  " 
					+ " WHERE psm.search_id = ? AND psm.type = '" + XLinkUtils.CROSS_TYPE_STRING + "' ";

	
	private static final String SQL_LOOPLINK_PART =
			" SELECT psm.id FROM psm  " 
					+ " WHERE psm.search_id = ? AND psm.type = '" + XLinkUtils.LOOP_TYPE_STRING + "' ";
	
	private static final String SQL_MONOLINK_PART =
			" SELECT psm.id FROM psm INNER JOIN monolink ON psm.id = monolink.psm_id " 
					+ " WHERE psm.search_id = ? ";
	
	private static final String SQL_NO_LINK_PART =
			" SELECT psm.id FROM psm LEFT OUTER JOIN monolink ON psm.id = monolink.psm_id " 
					+ " WHERE psm.search_id = ? "
					+ " AND ( psm.type = '" + XLinkUtils.UNLINKED_TYPE_STRING + "' OR  psm.type = '" + XLinkUtils.DIMER_TYPE_STRING + "' ) "
					+ "   AND  monolink.psm_id IS NULL";
	


	public List<Double> getQValues( 
			List<String> selectedLinkTypes, 
			int searchId
			) throws Exception {
		
		
		List<Double> qValueList = new ArrayList<Double>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		
		boolean webLinkTypeCrosslink = false;
		boolean webLinkTypeLooplink = false;
		boolean webLinkTypeMonolink = false;
		boolean webLinkTypeNolink = false;
		

		for ( String selectedLinkType : selectedLinkTypes ) {
			
			if ( QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__CROSSLINK_PSM.equals( selectedLinkType ) ) {
		
				webLinkTypeCrosslink = true;
				
			} else if ( QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__LOOPLINK_PSM.equals( selectedLinkType ) ) {

				webLinkTypeLooplink = true;
				
			} else if ( QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__MONOLINK_PSM.equals( selectedLinkType ) ) {
				
				webLinkTypeMonolink = true;
				
			} else if ( QCPlotConstants.Q_VALUE_PSM_COUNT_PLOT__NO_LINK_PSM.equals( selectedLinkType ) ) {

				webLinkTypeNolink = true;
				
			} else {
				
				String msg = "selectedLinkType is invalid, selectedLinkType: " + selectedLinkType;
				
				log.error( selectedLinkType );
				
				throw new Exception( msg );
			}
			
		}
		
		String sql = null;
		
		if ( webLinkTypeCrosslink
				&& webLinkTypeLooplink
				&& webLinkTypeMonolink 
				&& webLinkTypeNolink ) {
			
			sql = "SELECT q_value FROM psm WHERE search_id = ?";

			
		} else if ( ! webLinkTypeMonolink 
				&& ! webLinkTypeNolink ) {
		
			//  Optimization when only crosslink, looplink, or crosslink and looplink

			StringBuilder sqlSB = new StringBuilder( 10000 );

			sqlSB.append( "SELECT q_value FROM psm WHERE psm.search_id = ? AND psm.type IN ( " );

			if ( webLinkTypeCrosslink ) {
				
				sqlSB.append( " '" + XLinkUtils.CROSS_TYPE_STRING + "' " );
			}
			

			if ( webLinkTypeLooplink ) {

				if ( webLinkTypeCrosslink ) {

					sqlSB.append( ", " );
				}
			
				sqlSB.append( " '" + XLinkUtils.LOOP_TYPE_STRING + "' " );
			}
			
			sqlSB.append( " ) " );
			
			sql = sqlSB.toString();
		
		} else {

			StringBuilder sqlSB = new StringBuilder( 10000 );

			sqlSB.append( "SELECT q_value FROM psm INNER JOIN ( " );

			boolean firstWebLinkType = true;


			if ( webLinkTypeCrosslink ) {

				if ( firstWebLinkType ) {
					
					firstWebLinkType = false;
					
				} else {
					
					//  "UNION DISTINCT" is a union that removes duplicate rows where all the fields are evaluated as part of the duplicate match
					
					//   This "DISTINCT" will also remove duplicates from within the query result being UNION together
					
					sqlSB.append( " UNION DISTINCT " );  // DISTINCT is the default for UNION in MySQL.  
				}
			
				sqlSB.append( SQL_CROSSLINK_PART );
			}
			

			if ( webLinkTypeLooplink ) {

				if ( firstWebLinkType ) {
					
					firstWebLinkType = false;
					
				} else {
					
					//  "UNION DISTINCT" is a union that removes duplicate rows where all the fields are evaluated as part of the duplicate match
					
					//   This "DISTINCT" will also remove duplicates from within the query result being UNION together
					
					sqlSB.append( " UNION DISTINCT " );  // DISTINCT is the default for UNION in MySQL.  
				}
			
				sqlSB.append( SQL_LOOPLINK_PART );
			}
			

			if ( webLinkTypeMonolink ) {

				if ( firstWebLinkType ) {
					
					firstWebLinkType = false;
					
				} else {
					
					//  "UNION DISTINCT" is a union that removes duplicate rows where all the fields are evaluated as part of the duplicate match
					
					//   This "DISTINCT" will also remove duplicates from within the query result being UNION together
					
					sqlSB.append( " UNION DISTINCT " );  // DISTINCT is the default for UNION in MySQL.  
				}
			
				sqlSB.append( SQL_MONOLINK_PART );
			}
			

			if ( webLinkTypeNolink ) {

				if ( firstWebLinkType ) {
					
					firstWebLinkType = false;
					
				} else {
					
					//  "UNION DISTINCT" is a union that removes duplicate rows where all the fields are evaluated as part of the duplicate match
					
					//   This "DISTINCT" will also remove duplicates from within the query result being UNION together
					
					sqlSB.append( " UNION DISTINCT " );  // DISTINCT is the default for UNION in MySQL.  
				}
			
				sqlSB.append( SQL_NO_LINK_PART );
			}
		
			sqlSB.append( " ) AS psm_ids ON psm_ids.id = psm.id   " );

			sql = sqlSB.toString();
			
		}
		
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
				
			} else if ( ! webLinkTypeMonolink 
					&& ! webLinkTypeNolink ) {
			
				paramCounter++;
				pstmt.setInt( paramCounter, searchId );
				
				
			} else {
				
				if ( webLinkTypeCrosslink ) {
					paramCounter++;
					pstmt.setInt( paramCounter, searchId );
				}			
				if ( webLinkTypeLooplink ) {
					paramCounter++;
					pstmt.setInt( paramCounter, searchId );
				}			
				if ( webLinkTypeMonolink ) {
					paramCounter++;
					pstmt.setInt( paramCounter, searchId );
				}			
				if ( webLinkTypeNolink ) {
					paramCounter++;
					pstmt.setInt( paramCounter, searchId );
				}			
				
			}
			

			
			rs = pstmt.executeQuery();

			while( rs.next() ) {
				qValueList.add( rs.getDouble( "q_value" ) );
			}
			
		} catch ( Exception e ) {
			
			String msg = "getQValues(), sql: " + sql;
			
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
		
		
		
		return qValueList;
	}
	
	
	
}
