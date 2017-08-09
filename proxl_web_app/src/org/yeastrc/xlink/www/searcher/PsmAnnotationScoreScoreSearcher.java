package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.objects.PsmScoreVsScoreEntry;
import org.yeastrc.xlink.www.objects.PsmScoreVsScoreSearcherResults;
/**
 * Get 2 PSM annotation values for search criteria
 * in support of PSM Score VS Score QC Plot on Project Page
 *
 */
public class PsmAnnotationScoreScoreSearcher {
	
	private static final Logger log = Logger.getLogger(PsmAnnotationScoreScoreSearcher.class);
	private PsmAnnotationScoreScoreSearcher() { }
	private static final PsmAnnotationScoreScoreSearcher _INSTANCE = new PsmAnnotationScoreScoreSearcher();
	public static PsmAnnotationScoreScoreSearcher getInstance() { return _INSTANCE; }
	/**
	 * @param searchId
	 * @param scanFileId - Optional
	 * @param selectedLinkTypes
	 * @param annotationTypeId_Score_1
	 * @param psmScoreCutoff_1 - null if not applicable
	 * @param annotationTypeId_Score_2
	 * @param psmScoreCutoff_2 - null if not applicable
	 * @return
	 * @throws Exception
	 */
	public PsmScoreVsScoreSearcherResults getPsmScoreVsScoreList( 
			int searchId, 
			Integer scanFileId,
			Set<String> selectedLinkTypes,
			int annotationTypeId_Score_1, 
			Double psmScoreCutoff_1,
			int annotationTypeId_Score_2,
			Double psmScoreCutoff_2 ) throws Exception {
		List<PsmScoreVsScoreEntry> crosslinkEntries = new ArrayList<>();
		List<PsmScoreVsScoreEntry> looplinkEntries = new ArrayList<>();
		List<PsmScoreVsScoreEntry> unlinkedEntries = new ArrayList<>();
		//////////////////////
		String sqlScore_1_Max = "";
		if ( psmScoreCutoff_1 != null ) {
			sqlScore_1_Max = " AND pfagl_1.value_double <= " + psmScoreCutoff_1;
		}
		String sqlScore_2_Max = "";
		if ( psmScoreCutoff_2 != null ) {
			sqlScore_2_Max = " AND pfagl_2.value_double <= " + psmScoreCutoff_2;
		}
		String sqlScanFileId = "";
		//  If Scan file Id, add to SQL
		if ( scanFileId != null ) {
			sqlScanFileId = " INNER JOIN ( \n" 
			
			+ "   SELECT psm.id AS psm_id FROM \n" 
			+ "    psm INNER JOIN scan ON psm.scan_id = scan.id  \n" 
			+ "    WHERE psm.search_id = " 
			+ Integer.toString( searchId ) 
			+ " AND scan.scan_file_id = " 
			+ Integer.toString( scanFileId ) 
			+ " \n" 

			+ "\n ) AS psm_ids_for_scan_files ON pfagl_1.psm_id = psm_ids_for_scan_files.psm_id "; //  Close the subselect and specify join
		}
		
		String sqlLinkType = "";
		if ( selectedLinkTypes != null && ( ! selectedLinkTypes.isEmpty() ) ) {
			StringBuilder sqlSB = new StringBuilder( 1000 );
			sqlSB.append( " AND pfagl_1.psm_type IN ( " );
			boolean firstLinkType = true;
			for ( String selectedLinkType : selectedLinkTypes ) {
				if ( firstLinkType ) {
					firstLinkType = false;
				} else {
					sqlSB.append(",");
				}
				sqlSB.append("'");
				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals(selectedLinkType) ) {
					sqlSB.append( XLinkUtils.CROSS_TYPE_STRING);
				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals(selectedLinkType) ) {
					sqlSB.append( XLinkUtils.LOOP_TYPE_STRING);
				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals(selectedLinkType) ) {
					sqlSB.append( XLinkUtils.DIMER_TYPE_STRING);
					sqlSB.append("','");
					sqlSB.append( XLinkUtils.UNLINKED_TYPE_STRING);
				} else {
				}
				sqlSB.append("'");
			}
			sqlSB.append(")");
			sqlLinkType = sqlSB.toString();
		}
		
		String sql = "SELECT  pfagl_1.psm_id, pfagl_1.psm_type, pfagl_1.value_double AS score_1,  pfagl_2.value_double AS score_2 "
				+ " FROM psm_filterable_annotation__generic_lookup AS pfagl_1 "
				+ " INNER JOIN psm_filterable_annotation__generic_lookup AS pfagl_2 "
				+ "  ON pfagl_1.psm_id = pfagl_2.psm_id "
				+ sqlScanFileId // Optional restriction on scan file id
				+ " WHERE pfagl_1.search_id = " + searchId 
				+ " AND ( pfagl_1.annotation_type_id = " + annotationTypeId_Score_1
				+         sqlScore_1_Max
				+      " ) "
				+ " AND ( pfagl_2.annotation_type_id = " + annotationTypeId_Score_2
				+         sqlScore_2_Max
				+      " ) "
				+ sqlLinkType;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			rs = pstmt.executeQuery();
			while( rs.next() ) {
				PsmScoreVsScoreEntry entry = new PsmScoreVsScoreEntry();
				entry.setPsmId( rs.getInt( "psm_id" ) );
				entry.setScore_1( rs.getDouble( "score_1" ) );
				entry.setScore_2( rs.getDouble( "score_2" ) );
				String psmLinkType = rs.getString( "psm_type" );
				if ( XLinkUtils.CROSS_TYPE_STRING.equals( psmLinkType ) ) {
					crosslinkEntries.add( entry );
				} else if ( XLinkUtils.LOOP_TYPE_STRING.equals( psmLinkType ) ) {
					looplinkEntries.add( entry );
				} else {
					unlinkedEntries.add( entry );
				}
			}
		} catch ( Exception e ) {
			String msg = "Exception in getPsmScoreVsScoreList( ... ): sql: " + sql;
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
		// transfer results to output object
		PsmScoreVsScoreSearcherResults results = new PsmScoreVsScoreSearcherResults();
		if ( ! crosslinkEntries.isEmpty() ) {
			results.setCrosslinkEntries( crosslinkEntries );
		}
		if ( ! looplinkEntries.isEmpty() ) {
			results.setLooplinkEntries( looplinkEntries );
		}
		if ( ! unlinkedEntries.isEmpty() ) {
			results.setUnlinkedEntries( unlinkedEntries );
		}
		return results;
	}
}
