package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult;
import org.yeastrc.xlink.www.objects.ReportedPeptidesForMergedPeptidePage;
import org.yeastrc.xlink.www.objects.ReportedPeptidesForMergedPeptidePageWrapper;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;

/**
 * Get the Reported Peptides / Search combinations for a given Reported Peptide id and searchId and cutoffs
 *
 * This list is used on the Merged Peptide page as the details for each top level entry
 */
public class ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher {
	
	private static final Logger log = LoggerFactory.getLogger( ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher.class);
	private ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher() { }
	public static ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher getInstance() { return new ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher(); }
	/**
	 * Should it use the optimization of Peptide and PSM defaults to skip joining the tables with the annotation values?
	 */
	private final boolean USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES = false;  // UNTESTED for a value of "true"
	
	/**
	 * UNTESTED for a value of "true"
	 * 
	 * If make true, need to change calling code since best PSM annotation values will not be populated
	 * 
	 * Also, test web page and/or webservice 
	 */
//	private final boolean USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES = true;  //  UNTESTED for a value of "true"
	
	///  This is not used everywhere, most places the string values are hard coded in the SQL
	private final String PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS = "psm_fltrbl_tbl_";
	
	private final String SQL_FIRST_PART = 
			"SELECT"
//					+ " unified_rp__search__rep_pept__generic_lookup.reported_peptide_id, "
//			+ " unified_rp__search__rep_pept__generic_lookup.link_type, "
			+ " unified_rp__search__rep_pept__generic_lookup.psm_num_at_default_cutoff,"
			+ " unified_rp__search__rep_pept__generic_lookup.num_unique_psm_at_default_cutoff ";
	
	private final String SQL_FROM_PART = 
			" FROM "
			+ " unified_rp__search__rep_pept__generic_lookup ";
	
	private final String SQL_LAST_PART = "";
	
	private final String SQL_MAIN_WHERE_START = 
			" WHERE unified_rp__search__rep_pept__generic_lookup.search_id = ? "
			+ 	" AND unified_rp__search__rep_pept__generic_lookup.reported_peptide_id = ? ";
	
	/**
	 * Get the Reported Peptides / Search combinations for a given Reported Peptide id and searchId and cutoffs
	 * @param searchId
	 * @param reportedPeptideId
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	public List<ReportedPeptidesForMergedPeptidePageWrapper> getReportedPeptidesWebDisplay( 
			SearchDTO search,
			int searchId, 
			int reportedPeptideId, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel
			) throws Exception {
		List<ReportedPeptidesForMergedPeptidePageWrapper> results = new ArrayList<>();
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
				searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
		//  If null, create empty lists
		if ( peptideCutoffValuesList == null ) {
			peptideCutoffValuesList = new ArrayList<>();
		}
		if ( psmCutoffValuesList == null ) {
			psmCutoffValuesList = new ArrayList<>();
		}
		List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );
		List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );
		for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : peptideCutoffValuesList ) {
			peptideCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
		}
		for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {
			psmCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
		}

		//  Determine if can use PSM count at Default Cutoff
		DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult defaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult =
				DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.getInstance()
				.defaultCutoffsExactlyMatchAnnTypeDataToSearchData( searchId, searcherCutoffValuesSearchLevel );
		
		boolean defaultCutoffsExactlyMatchAnnTypeDataToSearchData =
				defaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult.isDefaultCutoffsExactlyMatchAnnTypeDataToSearchData();
		
		//////////////////////
		/////   Start building the SQL
		StringBuilder sqlSB = new StringBuilder( 1000 );
		sqlSB.append( SQL_FIRST_PART );
		///////   Add fields to result from best PSM annotation values
		{
			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs
				//  Add Field retrieval for each PSM cutoff
				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {
					sqlSB.append( " , " );
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id " );
					sqlSB.append( " AS "  );
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( "_annotation_type_id " );
					sqlSB.append( " , " );
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".best_psm_value_for_ann_type_id " );
					sqlSB.append( " AS "  );
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( "_best_psm_value_for_ann_type_id " );
				}
			}
		}
		sqlSB.append( SQL_FROM_PART );
		{
			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs
				//  Add inner join for each PSM cutoff
				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {
					sqlSB.append( " INNER JOIN " );
					sqlSB.append( " unified_rp__search__rep_pept__best_psm_value_generic_lookup AS psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( " ON "  );
					sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.search_id = "  );
					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );
					sqlSB.append( " AND " );
					sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.reported_peptide_id = "  );
					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );
				}
			}
		}
		{
			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs
				//  Add inner join for each Peptide cutoff
				for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {
					sqlSB.append( " INNER JOIN " );
					sqlSB.append( " srch__rep_pept__annotation AS srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( " ON "  );
					sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.search_id = "  );
					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );
					sqlSB.append( " AND " );
					sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.reported_peptide_id = "  );
					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );
				}
			}
		}
		//////////
		sqlSB.append( SQL_MAIN_WHERE_START );
		//////////
		// Process PSM Cutoffs for WHERE
		{
			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs
				int counter = 0; 
				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {
					AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();
					counter++;
					sqlSB.append( " AND " );
					sqlSB.append( " ( " );
					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );
					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );
					sqlSB.append( "psm_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".best_psm_value_for_ann_type_id " );
					if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
						String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
						log.error( msg );
						throw new Exception(msg);
					}
					if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {
						sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );
					} else {
						sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );
					}
					sqlSB.append( " ? " );
					sqlSB.append( " ) " );
				}
			} else {
				sqlSB.append( " AND " );
				sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.psm_num_at_default_cutoff > 0 " );
			}
		}
		//  Process Peptide Cutoffs for WHERE
		{
			if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs
				int counter = 0; 
				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {
					AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();
					counter++;
					sqlSB.append( " AND " );
					sqlSB.append( " ( " );
					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );
					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );
					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".value_double " );
					if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
						String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();
						log.error( msg );
						throw new Exception(msg);
					}
					if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {
						sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );
					} else {
						sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );
					}
					sqlSB.append( "? " );
					sqlSB.append( " ) " );
				}
			} else {
				//   Only Default Peptide Cutoffs chosen so criteria simply the Peptides where the defaultPeptideCutoffs is yes
				
				//  WARNING:  This code is currently not run for set value of USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES
				
				//  WARNING   This is possibly still WRONG and needs testing before using.
				
				//  For certain inputs, the right value to search for is: Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE
				
				sqlSB.append( " AND " );
				sqlSB.append( " unified_rp__search__rep_pept__generic_lookup.peptide_meets_default_cutoffs = '" );

				if ( peptideCutoffValuesList.isEmpty() ) {
					sqlSB.append( Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE.value() );
				} else {
					sqlSB.append( Yes_No__NOT_APPLICABLE_Enum.YES.value() );
				}
				sqlSB.append( "' " );			}
		}		
		sqlSB.append( SQL_LAST_PART );
		final String sql = sqlSB.toString();
		//////////////////////////////////
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			pstmt = conn.prepareStatement( sql );
			int paramCounter = 0;
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			paramCounter++;
			pstmt.setInt( paramCounter, reportedPeptideId );
			// Process PSM Cutoffs for WHERE
			{
				if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
						|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {
					//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs
					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {
						AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();
						paramCounter++;
						pstmt.setInt( paramCounter, searchId );
						paramCounter++;
						pstmt.setInt( paramCounter, srchPgmFilterablePsmAnnotationTypeDTO.getId() );
						paramCounter++;
						pstmt.setDouble( paramCounter, searcherCutoffValuesPsmAnnotationLevel.getAnnotationCutoffValue() );
					}
				}
			}
			// Process Peptide Cutoffs for WHERE
			{
				if ( ( ( ! defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) )
						|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {
					//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs
					for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {
						AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();
						paramCounter++;
						pstmt.setInt( paramCounter, searchId );
						paramCounter++;
						pstmt.setInt( paramCounter, srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId() );
						paramCounter++;
						pstmt.setDouble( paramCounter, searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationCutoffValue() );
					}
				}
			}
			rs = pstmt.executeQuery();
			ReportedPeptideDTO reportedPeptideDTO = null;
			while( rs.next() ) {
				ReportedPeptidesForMergedPeptidePageWrapper wrappedReportedPeptideData = new ReportedPeptidesForMergedPeptidePageWrapper();
				ReportedPeptidesForMergedPeptidePage reportedPeptideData = new ReportedPeptidesForMergedPeptidePage();
				wrappedReportedPeptideData.setReportedPeptidesForMergedPeptidePage( reportedPeptideData );
				
				WebReportedPeptide webReportedPeptide = new WebReportedPeptide();
				reportedPeptideData.setWebReportedPeptide( webReportedPeptide );
				
				reportedPeptideData.setSearchId( search.getProjectSearchId() );
				reportedPeptideData.setSearchName( search.getName() );
//				int reportedPeptideId = rs.getInt( "reported_peptide_id" );
				
				webReportedPeptide.setSearch( search );
				webReportedPeptide.setSearchId( search.getSearchId() );
				webReportedPeptide.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );
				webReportedPeptide.setReportedPeptideId( reportedPeptideId );
//				item.setUnifiedReportedPeptideId( reportedPeptideData.getUnifiedReportedPeptideId() );
				
				//  These counts are only valid for PSM and Peptide at default cutoffs
				if ( defaultCutoffsExactlyMatchAnnTypeDataToSearchData ) {
					//  associated PSM data
					int numPsmsForDefaultCutoffs = rs.getInt( "psm_num_at_default_cutoff" );
					if ( ! rs.wasNull() ) {
						webReportedPeptide.setNumPsms( numPsmsForDefaultCutoffs );
					}
					int numUniquePsmsForDefaultCutoffs = rs.getInt( "num_unique_psm_at_default_cutoff" );
					if ( ! rs.wasNull() ) {
						webReportedPeptide.setNumUniquePsms( numUniquePsmsForDefaultCutoffs );
					}
				}
				if ( peptideCutoffsAnnotationTypeDTOList.size() > 1 
						|| psmCutoffsAnnotationTypeDTOList.size() > 1 ) {
					if ( reportedPeptideData.getNumPSMs() <= 0 ) {
						//  !!!!!!!   Number of PSMs is zero this this isn't really a peptide that meets the cutoffs
						continue;  //  EARY LOOP ENTRY EXIT
					}
				}
				if ( ( defaultCutoffsExactlyMatchAnnTypeDataToSearchData )
					&& ( USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {
				} else {
					//  Get PSM best values from DB query, since psm best value table was joined
					Map<Integer, AnnotationDataBaseDTO> bestPsmAnnotationDTOFromQueryMap =
							getPSMBestValuesFromDBQuery( rs, psmCutoffsAnnotationTypeDTOList );
					wrappedReportedPeptideData.setPsmAnnotationDTOMap( bestPsmAnnotationDTOFromQueryMap );
				}
				results.add( wrappedReportedPeptideData );
			}
		} catch ( Exception e ) {
			String msg = "Exception in search( SearchDTO search, double psmCutoff, double peptideCutoff, ReportedPeptideDTO peptide ), sql: " + sql;
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
		return results;
	}
	
	/**
	 * Get PSM best values from DB query, since psm best value table was joined
	 * 
	 * @param rs
	 * @param psmCutoffsAnnotationTypeDTOList
	 * @return
	 * @throws SQLException
	 */
	private Map<Integer, AnnotationDataBaseDTO> getPSMBestValuesFromDBQuery( 
			ResultSet rs,
			List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList
			) throws SQLException { 
		Map<Integer, AnnotationDataBaseDTO> bestPsmAnnotationDTOFromQueryMap = new HashMap<>();
		//  Add inner join for each PSM cutoff
		for ( int counter = 1; counter <= psmCutoffsAnnotationTypeDTOList.size(); counter++ ) {
			PsmAnnotationDTO item = new PsmAnnotationDTO();
			String annotationTypeIdField = PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS + counter + "_annotation_type_id";
			String valueDoubleField = PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS + counter + "_best_psm_value_for_ann_type_id";
			item.setAnnotationTypeId( rs.getInt( annotationTypeIdField ) );
			item.setValueDouble( rs.getDouble( valueDoubleField ) );
			item.setValueString( Double.toString( item.getValueDouble() ) );
			bestPsmAnnotationDTOFromQueryMap.put( item.getAnnotationTypeId(),  item );
		}
		return bestPsmAnnotationDTOFromQueryMap;
	}
}
