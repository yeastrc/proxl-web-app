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
import org.yeastrc.xlink.www.constants.QC_Plot_ScoreVsScore_Constants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
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
	 * @param annotationTypeId_Score_1 - null if use altScoreType_1
	 * @param altScoreType_1
	 * @param psmScoreCutoff_1 - null if not applicable
	 * @param annotationTypeId_Score_2 - null if use altScoreType_2
	 * @param altScoreType_2
	 * @param psmScoreCutoff_2 - null if not applicable
	 * @return
	 * @throws Exception
	 */
	public PsmScoreVsScoreSearcherResults getPsmScoreVsScoreList( 
			int searchId, 
			Integer scanFileId,
			Set<String> selectedLinkTypes,
			Integer annotationTypeId_Score_1, 
			String altScoreType_1,  // for when not annotation type id
			Double psmScoreCutoff_1,
			Integer annotationTypeId_Score_2,
			String altScoreType_2,  // for when not annotation type id
			Double psmScoreCutoff_2 ) throws Exception {
		
		List<PsmScoreVsScoreEntry> crosslinkEntries = new ArrayList<>();
		List<PsmScoreVsScoreEntry> looplinkEntries = new ArrayList<>();
		List<PsmScoreVsScoreEntry> unlinkedEntries = new ArrayList<>();
		
		String sql = null;
		
		if ( annotationTypeId_Score_1 != null && annotationTypeId_Score_2 != null ) {
			sql =
					getSQL_Both_AnnTypeIds( 
							searchId, scanFileId, selectedLinkTypes, annotationTypeId_Score_1, psmScoreCutoff_1, annotationTypeId_Score_2, psmScoreCutoff_2 );
		
		} else {
			sql = getSQL_Other(
					searchId, scanFileId, selectedLinkTypes, annotationTypeId_Score_1, altScoreType_1, psmScoreCutoff_1, annotationTypeId_Score_2, altScoreType_2, psmScoreCutoff_2 );
		}
			
		if ( log.isDebugEnabled() ) {
			log.debug( "SQL: " + sql );
		}
		
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

	/**
	 * Create SQL for when both Annotation Type Ids are present
	 * 
	 * @param searchId
	 * @param scanFileId
	 * @param selectedLinkTypes
	 * @param annotationTypeId_Score_1
	 * @param psmScoreCutoff_1
	 * @param annotationTypeId_Score_2
	 * @param psmScoreCutoff_2
	 * @return
	 */
	private String getSQL_Both_AnnTypeIds( 	
			int searchId, 
			Integer scanFileId,
			Set<String> selectedLinkTypes,
			Integer annotationTypeId_Score_1, 
			Double psmScoreCutoff_1,
			Integer annotationTypeId_Score_2,
			Double psmScoreCutoff_2  ) {

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

		return sql;
	}


	/**
	 * Create SQL for Other
	 * 
	 * @param searchId
	 * @param scanFileId
	 * @param selectedLinkTypes
	 * @param altScoreType_1
	 * @param psmScoreCutoff_1
	 * @param altScoreType_2
	 * @param psmScoreCutoff_2
	 * @return
	 */
	private String getSQL_Other( 	
			int searchId, 
			Integer scanFileId,
			Set<String> selectedLinkTypes,
			Integer annotationTypeId_Score_1, 
			String altScoreType_1,  // for when not annotation type id
			Double psmScoreCutoff_1,
			Integer annotationTypeId_Score_2,
			String altScoreType_2,  // for when not annotation type id
			Double psmScoreCutoff_2 ) throws Exception {

		StringBuilder sqlSB = new StringBuilder( 1000 );
		String sqlPart = null;
		
		sqlSB.append( " SELECT psm.id AS psm_id, search_reported_peptide.link_type AS psm_type, " );
		sqlPart = sqlOther_Select( "1" /* pos_1_or_2 */, annotationTypeId_Score_1, altScoreType_1, psmScoreCutoff_1 );
		sqlSB.append( sqlPart );
		sqlSB.append( ", " );
		sqlPart = sqlOther_Select( "2" /* pos_1_or_2 */, annotationTypeId_Score_2, altScoreType_2, psmScoreCutoff_2 );
		sqlSB.append( sqlPart );
		
		sqlSB.append( "\n FROM psm INNER JOIN search_reported_peptide ON psm.search_id = search_reported_peptide.search_id AND psm.reported_peptide_id = search_reported_peptide.reported_peptide_id \n" );
		
		//  If Scan file Id, add to SQL
		if ( scanFileId != null ) {
			String sqlScanFileId = " INNER JOIN ( \n" 

				+ "   SELECT psm.id AS psm_id FROM \n" 
				+ "    psm INNER JOIN scan ON psm.scan_id = scan.id  \n" 
				+ "    WHERE psm.search_id = " 
				+ Integer.toString( searchId ) 
				+ " AND scan.scan_file_id = " 
				+ Integer.toString( scanFileId ) 
				+ " \n" 

				+ "\n ) AS psm_ids_for_scan_files ON psm.id = psm_ids_for_scan_files.psm_id \n"; //  Close the subselect and specify join
			
			sqlSB.append( sqlScanFileId );
		}
		
		sqlSB.append( "\n" );

		//  Add inner join for each score value to return
		sqlPart = sqlOther_InnerJoin( "1" /* pos_1_or_2 */, annotationTypeId_Score_1, altScoreType_1, psmScoreCutoff_1 );
		sqlSB.append( sqlPart );
		sqlSB.append( "\n" );
		sqlPart = sqlOther_InnerJoin( "2" /* pos_1_or_2 */, annotationTypeId_Score_2, altScoreType_2, psmScoreCutoff_2 );
		sqlSB.append( sqlPart );
		sqlSB.append( "\n" );
		
		//////  Start Where clause
		
		sqlSB.append( "\n WHERE psm.search_id = " );
		sqlSB.append( Integer.toString( searchId ) );
		
		if ( selectedLinkTypes != null && ( ! selectedLinkTypes.isEmpty() ) ) {
			sqlSB.append( "\n AND search_reported_peptide.link_type IN ( " );
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
		}

		sqlSB.append( "\n" );
		
		//  Add Where for each score value to return, if needed
		sqlPart = sqlOther_Where( "1" /* pos_1_or_2 */, annotationTypeId_Score_1, altScoreType_1, psmScoreCutoff_1 );
		sqlSB.append( sqlPart );
		sqlSB.append( "\n" );
		sqlPart = sqlOther_Where( "2" /* pos_1_or_2 */, annotationTypeId_Score_2, altScoreType_2, psmScoreCutoff_2 );
		sqlSB.append( sqlPart );
		sqlSB.append( "\n" );

		String sql = sqlSB.toString();
		
		return sql;
	}
	
	/**
	 * @param pos_1_or_2
	 * @param annotationTypeId_Score
	 * @param altScoreType
	 * @param psmScoreCutoff
	 * @return
	 * @throws ProxlWebappDataException 
	 */
	private String sqlOther_Select(
			String pos_1_or_2,
			Integer annotationTypeId_Score, 
			String altScoreType,  // for when not annotation type id
			Double psmScoreCutoff
			) throws ProxlWebappDataException {
		
		final String scoreFieldPrefix = "score_";
		
		final String scoreField = scoreFieldPrefix + pos_1_or_2;
		
		if ( annotationTypeId_Score != null ) {
			return "pfagl_" + pos_1_or_2 + ".value_double AS " + scoreField;
		}
		if ( QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_PRE_MZ.equals( altScoreType ) ) {
			return "scan_" + pos_1_or_2 + ".preMZ AS " + scoreField;
		}
		if ( QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_RETENTION_TIME.equals( altScoreType ) ) {
			return "scan_" + pos_1_or_2 + ".retention_time AS " + scoreField;
		}
		if ( QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_CHARGE.equals( altScoreType ) ) {
			return "psm.charge AS " + scoreField;
		}

		throw new ProxlWebappDataException( "Unknown altScoreType: " + altScoreType );
	}


	/**
	 * @param pos_1_or_2
	 * @param annotationTypeId_Score
	 * @param altScoreType
	 * @param psmScoreCutoff
	 * @return
	 * @throws ProxlWebappDataException 
	 */
	private String sqlOther_InnerJoin(
			String pos_1_or_2,
			Integer annotationTypeId_Score, 
			String altScoreType,  // for when not annotation type id
			Double psmScoreCutoff
			) throws ProxlWebappDataException {
		
		if ( annotationTypeId_Score != null ) {
			
			return "INNER JOIN psm_filterable_annotation__generic_lookup AS pfagl_" + pos_1_or_2
					+ " ON psm.id = pfagl_" + pos_1_or_2 + ".psm_id ";
		}
		
		if ( QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_PRE_MZ.equals( altScoreType )
				|| QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_RETENTION_TIME.equals( altScoreType ) ) {
			return "INNER JOIN scan AS scan_" + pos_1_or_2
					+ " ON psm.scan_id = scan_" + pos_1_or_2 + ".id ";
		}
		if ( QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_CHARGE.equals( altScoreType ) ) {
			return "";
		}

		throw new ProxlWebappDataException( "Unknown altScoreType: " + altScoreType );
	}


	/**
	 * @param pos_1_or_2
	 * @param annotationTypeId_Score
	 * @param altScoreType
	 * @param psmScoreCutoff
	 * @return
	 * @throws ProxlWebappDataException 
	 */
	private String sqlOther_Where(
			String pos_1_or_2,
			Integer annotationTypeId_Score, 
			String altScoreType,  // for when not annotation type id
			Double psmScoreCutoff
			) throws ProxlWebappDataException {
		
		if ( annotationTypeId_Score != null ) {
			String sql = " AND pfagl_" + pos_1_or_2 + ".annotation_type_id = " + annotationTypeId_Score;
			
			if ( psmScoreCutoff != null ) {
				return
						sql + " AND pfagl_" + pos_1_or_2 + ".value_double <= " + psmScoreCutoff;
			}
			return sql;
		}
		
		if ( QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_PRE_MZ.equals( altScoreType ) ) {
			if ( psmScoreCutoff != null ) {
				return " AND scan_" + pos_1_or_2 + ".preMZ <= " + psmScoreCutoff;
			}
			return "";
		}
		if ( QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_RETENTION_TIME.equals( altScoreType ) ) {
			if ( psmScoreCutoff != null ) {
				return " AND scan_" + pos_1_or_2 + ".retention_time <= " + psmScoreCutoff;
			}
			return "";
		}
		if ( QC_Plot_ScoreVsScore_Constants.SCORE_SELECTION_CHARGE.equals( altScoreType ) ) {
			if ( psmScoreCutoff != null ) {
				return " AND psm.charge <= " + psmScoreCutoff;
			}
			return "";
		}

		throw new ProxlWebappDataException( "Unknown altScoreType: " + altScoreType );
	}
}
