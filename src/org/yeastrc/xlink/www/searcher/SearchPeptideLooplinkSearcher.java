package org.yeastrc.xlink.www.searcher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ReportedPeptideDAO;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.objects.SearchPeptideLooplink;
import org.yeastrc.xlink.www.objects.SearchPeptideLooplinkAnnDataWrapper;



public class SearchPeptideLooplinkSearcher {
	
	private static final Logger log = Logger.getLogger( SearchPeptideLooplinkSearcher.class );

	private SearchPeptideLooplinkSearcher() { }
	private static final SearchPeptideLooplinkSearcher _INSTANCE = new SearchPeptideLooplinkSearcher();
	public static SearchPeptideLooplinkSearcher getInstance() { return _INSTANCE; }
	

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
	
	
	
	

	private final String PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS = "psm_best_value_tbl_";
	
	

	private final String SQL_FIRST_PART = 
			

			"SELECT unified_rp__rep_pept__search__generic_lookup.reported_peptide_id, "
			
			+ " unified_rp__rep_pept__search__generic_lookup.link_type, "
			+ " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff ";
	

	private final String SQL_MAIN_FROM_START = 

			" FROM "

			+ " unified_rp__rep_pept__search__generic_lookup "

			+ " INNER JOIN looplink ON unified_rp__rep_pept__search__generic_lookup.sample_psm_id = looplink.psm_id";


	private final String SQL_MAIN_WHERE_START = 
					
			" WHERE unified_rp__rep_pept__search__generic_lookup.search_id = ? "
			+ " AND unified_rp__rep_pept__search__generic_lookup.link_type = '" + XLinkUtils.LOOP_TYPE_STRING + "' "
			+ " AND looplink.nrseq_id = ?  "
			+ " AND looplink.protein_position_1 = ? AND looplink.protein_position_2 = ?  ";

	
	private final String SQL_LAST_PART = 

			" GROUP BY unified_rp__rep_pept__search__generic_lookup.reported_peptide_id ";

			// Sort in Java	
	
	
	/**
	 * Get all looplink peptides corresponding to the given Criteria
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param proteinId
	 * @param proteinPosition1
	 * @param proteinPosition2
	 * @return
	 * @throws Exception
	 */
	public List<SearchPeptideLooplinkAnnDataWrapper> searchOnSearchProteinLooplink( 
			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int proteinId,
			int proteinPosition1,
			int proteinPosition2
			
			) throws Exception {
		
		List<SearchPeptideLooplinkAnnDataWrapper> wrappedLinks = new ArrayList<>();

		
		
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

		

		////////////
		
		//  All cutoffs are default?
		
		boolean onlyDefaultPeptideCutoffs = false;
		
		boolean onlyDefaultPsmCutoffs = false;
		
		
		if ( ! peptideCutoffValuesList.isEmpty()  ) {

			//   Check if any Peptide Cutoffs are default filters
			
			onlyDefaultPeptideCutoffs = true;


			for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

				if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {

					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
					log.error( msg );
					throw new Exception(msg);
				}

				if ( ! item.annotationValueMatchesDefault() ) {

					//  Non-default filter value found so set to false

					onlyDefaultPeptideCutoffs = false;
					break;
				}
			}
		}


		if ( ! psmCutoffValuesList.isEmpty()  ) {

			//   Check if all Psm Cutoffs are default values
			
			onlyDefaultPsmCutoffs = true;

			for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {

				if ( ! item.annotationValueMatchesDefault() ) {

					onlyDefaultPsmCutoffs = false;
					break;
				}
			}
		}		
		
		
		

		//////////////////////
		
		/////   Start building the SQL
		
		

		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		
		


		sqlSB.append( SQL_FIRST_PART );
		
		

		///////   Add fields to result from best PSM annotation values
		
		{
			

			
			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
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

					sqlSB.append( " , " );
					
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".best_psm_value_string_for_ann_type_id " );
					sqlSB.append( " AS "  );
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( "_best_psm_value_string_for_ann_type_id " );


				}
			}
		}
		
		
		
		

		sqlSB.append( SQL_MAIN_FROM_START );
		

		{
			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

				
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs


				//  Add inner join for each PSM cutoff

				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );
					
					sqlSB.append( " unified_rp__rep_pept__search__best_psm_value_generic_lookup AS psm_best_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.search_id = "  );

					sqlSB.append( "psm_best_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = "  );

					sqlSB.append( "psm_best_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );

				}
			}
		}
		
		
		{
			
			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

				
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs


				//  Add inner join for each Peptide cutoff

				for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );
			
					sqlSB.append( " unified_rp__rep_pept__search__peptide_fltbl_value_generic_lookup AS pept_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.search_id = "  );

					sqlSB.append( "pept_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );



					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = "  );

					sqlSB.append( "pept_value_tbl_" );
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
			
			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

				
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs

				int counter = 0; 

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {


					AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

					counter++;

					sqlSB.append( " AND " );

					sqlSB.append( " ( " );


					sqlSB.append( "psm_best_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );

					sqlSB.append( "psm_best_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );

					sqlSB.append( "psm_best_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".best_psm_value_for_ann_type_id " );

					if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() 
							== FilterDirectionType.ABOVE ) {

						sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

					} else {

						sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

					}

					sqlSB.append( " ? " );

					sqlSB.append( " ) " );
				}
				
			} else {

				sqlSB.append( " AND " );


				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff > 0 " );


			}
		}
		
		//  Process Peptide Cutoffs for WHERE

		{


			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

				int counter = 0; 

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

					AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();

					counter++;

					sqlSB.append( " AND " );

					sqlSB.append( " ( " );


					sqlSB.append( "pept_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );

					sqlSB.append( "pept_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );

					sqlSB.append( "pept_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".peptide_value_for_ann_type_id " );

					if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() 
							== FilterDirectionType.ABOVE ) {

						sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

					} else {

						sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

					}

					sqlSB.append( "? " );

					sqlSB.append( " ) " );
				}
				

			} else {

				//   Only Default Peptide Cutoffs chosen so criteria simply the Peptides where the defaultPeptideCutoffs is yes

				sqlSB.append( " AND " );


				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.peptide_meets_default_cutoffs = '" );
				sqlSB.append( Yes_No__NOT_APPLICABLE_Enum.YES.value() );
				sqlSB.append( "' " );
				
			}
		}		
		
		
		
		sqlSB.append( SQL_LAST_PART );
		
		
		String sql = sqlSB.toString();
		
		
		
		//////////////////////////////////
		
		
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.PROXL );
			
			pstmt = conn.prepareStatement( sql );

//			" WHERE unified_rp__rep_pept__search__generic_lookup.search_id = ? "
//			+ " AND unified_rp__rep_pept__search__generic_lookup.link_type = '" + XLinkUtils.CROSS_TYPE_STRING + "' "
//			+ " AND looplink.nrseq_id = ?  "
//			+ " AND looplink.protein_position_1 = ? AND looplink.protein_position_2 = ?  ";


			int paramCounter = 0;
			
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			paramCounter++;
			pstmt.setInt( paramCounter, proteinId );
			paramCounter++;
			pstmt.setInt( paramCounter, proteinPosition1 );
			paramCounter++;
			pstmt.setInt( paramCounter, proteinPosition2 );


			// Process PSM Cutoffs for WHERE


			{

				if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
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
				if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
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
			
			
			SearchDTO searchDTO = null;
			
			searchDTO = SearchDAO.getInstance().getSearch( searchId );
			

			while( rs.next() ) {
				
				SearchPeptideLooplinkAnnDataWrapper wrappedLink = new SearchPeptideLooplinkAnnDataWrapper();
				
				SearchPeptideLooplink link = new SearchPeptideLooplink();
				
				wrappedLink.setSearchPeptideLooplink( link );
				
				link.setSearch( searchDTO );
				
				link.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );

				link.setReportedPeptide( ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( rs.getInt( "reported_peptide_id" ) ) );


				if ( onlyDefaultPsmCutoffs && onlyDefaultPeptideCutoffs ) {
					
					int numPsmsForDefaultCutoffs = rs.getInt( "psm_num_at_default_cutoff" );
					if ( ! rs.wasNull() ) {
					
						link.setNumPsms( numPsmsForDefaultCutoffs );
					}
				}
				

				if ( ( ( ! onlyDefaultPeptideCutoffs )|| ( ! onlyDefaultPsmCutoffs ) )
						|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

					//  Get PSM best values from DB query, since psm best value table was joined

					Map<Integer, AnnotationDataBaseDTO> bestPsmAnnotationDTOFromQueryMap =
							getPSMBestValuesFromDBQuery( rs, psmCutoffsAnnotationTypeDTOList );

					wrappedLink.setPsmAnnotationDTOMap( bestPsmAnnotationDTOFromQueryMap );
				}
				
				wrappedLinks.add( wrappedLink );
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
			
			if( conn != null ) {
				try { conn.close(); } catch( Throwable t ) { ; }
				conn = null;
			}
			
		}
		
		return wrappedLinks;
	}
	
	
	

	//  Get PSM best values from DB query, since psm best value table was joined

	/**
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
			String valueStringField = PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS + counter + "_best_psm_value_string_for_ann_type_id";

			item.setAnnotationTypeId( rs.getInt( annotationTypeIdField ) );

			item.setValueDouble( rs.getDouble( valueDoubleField ) );
			item.setValueString( rs.getString( valueStringField ) );

			bestPsmAnnotationDTOFromQueryMap.put( item.getAnnotationTypeId(),  item );

		}
		
		return bestPsmAnnotationDTOFromQueryMap;
	}

	
}
