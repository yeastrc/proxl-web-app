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
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
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
					+ " INNER JOIN "
			+ " psm_filterable_annotation__generic_lookup" 
			+ " ON "  
			+ "psm.id = psm_filterable_annotation__generic_lookup.psm_id ";
	
	private static final String SQL_SUB_SELECT__SCAN_IDS_LINK_TYPE_JOIN_MAIN =
			" INNER JOIN search_reported_peptide "
			+ 	" ON psm.search_id = search_reported_peptide.search_id"
			+ 		" AND psm.reported_peptide_id = search_reported_peptide.reported_peptide_id ";
	
	private static final String SQL_SUB_SELECT__SCAN_IDS_WHERE = 
			" WHERE psm.search_id = ? "
			+ 	" AND psm_filterable_annotation__generic_lookup.annotation_type_id = ? "
			+ 	" AND psm_filterable_annotation__generic_lookup.value_double ";
	
	private static final String SQL_SUB_SELECT__SCAN_IDS__START_WHERE_LINK_TYPE_IN = 
			" AND search_reported_peptide.link_type IN ( "; 
	
	private static final String SQL_SUB_SELECT__SCAN_IDS__END_WHERE_LINK_TYPE_IN = " ) "; 

	/**
	 * @param scansForSelectedLinkTypes
	 * @param searchId
	 * @param scanFileId
	 * @param annotationTypeDTO
	 * @param psmScoreCutoff
	 * @param retentionTimeInSecondsCutoff
	 * @return
	 * @throws Exception
	 */
	public List<BigDecimal> getRetentionTimes( 
			List<String> scansForSelectedLinkTypes, 
			int searchId,
			int scanFileId,
			AnnotationTypeDTO annotationTypeDTO,
			double psmScoreCutoff,
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
			sqlSB.append( SQL_SUB_SELECT__SCAN_IDS_LINK_TYPE_JOIN_MAIN );
		}
		sqlSB.append( SQL_SUB_SELECT__SCAN_IDS_WHERE );
		if ( annotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
			String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + annotationTypeDTO.getId();
			log.error( msg );
			throw new Exception(msg);
		}
		if ( annotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {
			sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );
		} else {
			sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );
		}
		sqlSB.append( " ? " );
		if ( webLinkTypeCrosslink
				&& webLinkTypeLooplink
				&& webLinkTypeUnlinked ) {
		} else {
			sqlSB.append( SQL_SUB_SELECT__SCAN_IDS__START_WHERE_LINK_TYPE_IN );   //   ...  IN (
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
			sqlSB.append( SQL_SUB_SELECT__SCAN_IDS__END_WHERE_LINK_TYPE_IN );   //  )
		}
		sqlSB.append( " ) AS scan_ids ON scan_ids.scan_id = scan.id   " );
		sqlSB.append( " WHERE scan_file_id = ? " );
		if ( retentionTimeInSecondsCutoff != null ) {
			sqlSB.append( " AND retention_time < ?  " );
		}
		String sql = sqlSB.toString();
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			paramCounter++;
			pstmt.setInt( paramCounter, annotationTypeDTO.getId() );
			paramCounter++;
			pstmt.setDouble( paramCounter, psmScoreCutoff );
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
