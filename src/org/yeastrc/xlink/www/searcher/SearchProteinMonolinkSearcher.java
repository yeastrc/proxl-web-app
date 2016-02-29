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
//import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinMonolink;
import org.yeastrc.xlink.www.objects.SearchProteinMonolinkWrapper;

public class SearchProteinMonolinkSearcher {

	private static final Logger log = Logger.getLogger( SearchProteinMonolinkSearcher.class );
	
	private SearchProteinMonolinkSearcher() { }
	private static final SearchProteinMonolinkSearcher _INSTANCE = new SearchProteinMonolinkSearcher();
	public static SearchProteinMonolinkSearcher getInstance() { return _INSTANCE; }
	

	
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
	
	
	

	///  These are not used everywhere, most places the string values are hard coded in the SQL

	private final String PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS = "best_psm_value_tbl_";
	

	private final String PEPTIDE_BEST_VALUE_FILTER_TABLE_ALIAS = "best_rep_pept_value_tbl_";
	
	
	
	/////////////////////////////////////////////////////////////////
	
	
//	String sql = "SELECT bestPSMQValue, bestPeptideQValue " +
//			"FROM search_monolink_lookup WHERE search_id = ? AND bestPSMQValue <= ? AND  ( bestPeptideQValue <= ? OR bestPeptideQValue IS NULL )  AND "
//			+ "nrseq_id = ? AND protein_position = ?";	


	private final String SQL_SEARCH_ON_SEARCH_ID_MONOLINK_FIRST_PART = 

			"SELECT"
//					+ " search_monolink_generic_lookup.nrseq_id, "
//					+ " search_monolink_generic_lookup.protein_position, "

			+ " search_monolink_generic_lookup.num_psm_at_default_cutoff, "
			+ " search_monolink_generic_lookup.num_linked_peptides_at_default_cutoff, "
			+ " search_monolink_generic_lookup.num_unique_peptides_linked_at_default_cutoff ";


	private final String SQL_SEARCH_ON_SEARCH_ID_MONO_LINK_FROM_START = 			
			
			" FROM search_monolink_generic_lookup";


	private static final String SQL_SEARCH_ON_SEARCH_ID_MONOLINK_WHERE_START =  
			" WHERE search_monolink_generic_lookup.search_id = ? "
			+ " AND search_monolink_generic_lookup.nrseq_id = ?"
			+ " AND search_monolink_generic_lookup.protein_position = ? ";
			
	private static final String SQL_SEARCH_ON_SEARCH_ID_MONOLINK_ORDER_BY =   
			" ORDER BY search_monolink_generic_lookup.nrseq_id, "
			+ " search_monolink_generic_lookup.protein_position ";


	
	
	/**
	 * @param search
	 * @param searcherCutoffValuesSearchLevel
	 * @param protein
	 * @param position
	 * @return
	 * @throws Exception
	 */
	public SearchProteinMonolinkWrapper searchOnSearch( 
			SearchDTO search, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel, 
			NRProteinDTO protein, 
			int position ) throws Exception {
	
		SearchProteinMonolinkWrapper wrappedLink = null;
		

		
		int searchId = search.getId();



		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList =	null;
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = null;

		if ( searcherCutoffValuesSearchLevel != null ) {

			peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
			psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
		}


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
		
		//////////////////////////////////
		

		final String sql = getSQL(
				peptideCutoffValuesList, 
				psmCutoffValuesList,
				onlyDefaultPeptideCutoffs, 
				onlyDefaultPsmCutoffs, 
				SQL_SEARCH_ON_SEARCH_ID_MONOLINK_FIRST_PART,
				SQL_SEARCH_ON_SEARCH_ID_MONO_LINK_FROM_START,
				SQL_SEARCH_ON_SEARCH_ID_MONOLINK_WHERE_START, 
				SQL_SEARCH_ON_SEARCH_ID_MONOLINK_ORDER_BY );
		

		
		
		//////////////////////////////////
		
		
		
			
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			
			pstmt = conn.prepareStatement( sql );

			int paramCounter = 0;
			

			//   For:  search_monolink_generic_lookup.search_id = ? AND nrseq_id = ? AND protein_position = ? ";

			paramCounter++;
			pstmt.setInt( paramCounter, searchId );

			paramCounter++;
			pstmt.setInt( paramCounter, protein.getNrseqId() );
			paramCounter++;
			pstmt.setInt( paramCounter, position );

			
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

			if( rs.next() ) {
				
				wrappedLink = new SearchProteinMonolinkWrapper();
				
				SearchProteinMonolink link = new SearchProteinMonolink();
				wrappedLink.setSearchProteinMonolink( link );
				
				
				link.setSearch( search );
				
				link.setProtein( new SearchProtein( search, protein ) );
				
				link.setProteinPosition( position );
				
				
				
				link.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );


				//  These counts are only valid for PSM and Peptide at default cutoffs

				if ( onlyDefaultPsmCutoffs && onlyDefaultPeptideCutoffs ) {
					
					link.setNumPsms( rs.getInt( "num_psm_at_default_cutoff" ) );
					link.setNumPeptides( rs.getInt( "num_linked_peptides_at_default_cutoff" ) );
					link.setNumUniquePeptides( rs.getInt( "num_unique_peptides_linked_at_default_cutoff" ) );
				}

				

				if ( ( onlyDefaultPsmCutoffs && onlyDefaultPeptideCutoffs )
					&& ( USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {
										
					
				} else {
					

					//  Get PSM best values from DB query, since psm best value table was joined

					Map<Integer, AnnotationDataBaseDTO> bestPsmAnnotationDTOFromQueryMap =
							getPSMBestValuesFromDBQuery( rs, psmCutoffsAnnotationTypeDTOList );

					wrappedLink.setPsmAnnotationDTOMap( bestPsmAnnotationDTOFromQueryMap );
					

					//  Get Peptide best values from DB query, since peptide best value table was joined

					Map<Integer, AnnotationDataBaseDTO> bestPeptideAnnotationDTOFromQueryMap =
							getPeptideBestValuesFromDBQuery( rs, peptideCutoffsAnnotationTypeDTOList );
					
					wrappedLink.setPeptideAnnotationDTOMap( bestPeptideAnnotationDTOFromQueryMap );
				}
				
				
				if( rs.next() )
					throw new Exception( "Should only have gotten one row..." );
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
		
		return wrappedLink;
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


	//  Get Peptide best values from DB query, since peptide value table was joined

	/**
	 * @param rs
	 * @param peptideCutoffsAnnotationTypeDTOList
	 * @return
	 * @throws SQLException
	 */
	private Map<Integer, AnnotationDataBaseDTO> getPeptideBestValuesFromDBQuery( 


			ResultSet rs,

			List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList

			) throws SQLException { 

		Map<Integer, AnnotationDataBaseDTO> searchedForPeptideAnnotationDTOFromQueryMap = new HashMap<>();

		//  Add inner join for each Peptide cutoff

		for ( int counter = 1; counter <= peptideCutoffsAnnotationTypeDTOList.size(); counter++ ) {

			SearchReportedPeptideAnnotationDTO item = new SearchReportedPeptideAnnotationDTO();

			String annotationTypeIdField = PEPTIDE_BEST_VALUE_FILTER_TABLE_ALIAS + counter + "_annotation_type_id";

			String valueDoubleField = PEPTIDE_BEST_VALUE_FILTER_TABLE_ALIAS + counter + "_best_peptide_value_string_for_ann_type_id";
			String valueStringField = PEPTIDE_BEST_VALUE_FILTER_TABLE_ALIAS + counter + "_best_peptide_value_string_for_ann_type_id";
		
			item.setAnnotationTypeId( rs.getInt( annotationTypeIdField ) );

			item.setValueDouble( rs.getDouble( valueDoubleField ) );
			item.setValueString( rs.getString( valueStringField ) );

			searchedForPeptideAnnotationDTOFromQueryMap.put( item.getAnnotationTypeId(), item );

		}
		
		return searchedForPeptideAnnotationDTOFromQueryMap;
	}



	//////////////////////////////////////////////////////////////////////////////////////
	
	


	/**
	 * @param peptideCutoffValuesList
	 * @param psmCutoffValuesList
	 * @param onlyDefaultPeptideCutoffs
	 * @param onlyDefaultPsmCutoffs
	 * @param sqlFirstPart
	 * @param sqlWhereStart
	 * @param sqlOrderBy
	 * @return
	 * @throws Exception 
	 */
	public String getSQL(
			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList,
			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList,
			boolean onlyDefaultPeptideCutoffs,
			boolean onlyDefaultPsmCutoffs, 
			String sqlFirstPart,
			String sqlFromStart, 
			String sqlWhereStart, 
			String sqlOrderBy) throws Exception {
		


		//////////////////////
		
		/////   Start building the SQL
		
		

		
		StringBuilder sqlSB = new StringBuilder( 1000 );
		
		


		sqlSB.append( sqlFirstPart );
		

		
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
		
		///////   Add fields to result from best Peptide annotation values

		{
			

			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

				
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs


				//  Add inner join for each Peptide cutoff

				for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " , " );

					sqlSB.append( PEPTIDE_BEST_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id " );
					sqlSB.append( " AS "  );
					sqlSB.append( PEPTIDE_BEST_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( "_annotation_type_id " );

					sqlSB.append( " , " );

					sqlSB.append( PEPTIDE_BEST_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".best_peptide_value_for_ann_type_id " );
					sqlSB.append( " AS "  );
					sqlSB.append( PEPTIDE_BEST_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( "_best_peptide_value_for_ann_type_id " );

					sqlSB.append( " , " );
					
					sqlSB.append( PEPTIDE_BEST_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".best_peptide_value_string_for_ann_type_id " );
					sqlSB.append( " AS "  );
					sqlSB.append( PEPTIDE_BEST_VALUE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( "_best_peptide_value_string_for_ann_type_id " );
					
				}
			}
		}


		sqlSB.append( sqlFromStart );
		
		
		{
			
			
			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

				
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs



				//  Add inner join for each PSM cutoff

				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );

					sqlSB.append( " search_monolink_best_psm_value_generic_lookup AS best_psm_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					
					sqlSB.append( " search_monolink_generic_lookup.search_id = "  );

					sqlSB.append( "best_psm_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );

					sqlSB.append( " search_monolink_generic_lookup.nrseq_id = "  );

					sqlSB.append( "best_psm_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".nrseq_id" );

					sqlSB.append( " AND " );

					sqlSB.append( " search_monolink_generic_lookup.protein_position = "  );

					sqlSB.append( "best_psm_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".protein_position" );

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

					sqlSB.append( " search_monolink_best_peptide_value_generic_lookup AS best_rep_pept_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					
					sqlSB.append( " search_monolink_generic_lookup.search_id = "  );

					sqlSB.append( "best_rep_pept_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );

					sqlSB.append( " search_monolink_generic_lookup.nrseq_id = "  );

					sqlSB.append( "best_rep_pept_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".nrseq_id" );

					sqlSB.append( " AND " );

					sqlSB.append( " search_monolink_generic_lookup.protein_position = "  );

					sqlSB.append( "best_rep_pept_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".protein_position" );

				}
			}
		}
		

		
		//////////
		
		sqlSB.append( sqlWhereStart );
		
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


					sqlSB.append( "best_psm_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );

					sqlSB.append( "best_psm_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );

					sqlSB.append( "best_psm_value_tbl_" );
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


				sqlSB.append( " search_monolink_generic_lookup.num_psm_at_default_cutoff > 0 " );
				
			}
		}
		
		//  Process Peptide Cutoffs for WHERE

		{

			if ( ( ( ! onlyDefaultPsmCutoffs ) || ( ! onlyDefaultPeptideCutoffs ) )
					|| ( ! USE_PEPTIDE_PSM_DEFAULTS_TO_SKIP_JOIN_ANNOTATION_DATA_VALUES_TABLES ) ) {

				
				//  Non-Default PSM or Peptide cutoffs so have to query on the cutoffs

				int counter = 0; 

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

					AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();

					counter++;

					sqlSB.append( " AND " );

					sqlSB.append( " ( " );


					sqlSB.append( "best_rep_pept_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );

					sqlSB.append( "best_rep_pept_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );

					sqlSB.append( "best_rep_pept_value_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".best_peptide_value_for_ann_type_id " );

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

					sqlSB.append( " ? " );

					sqlSB.append( " ) " );
				}

				
				
			} else {

				//   Only Default Peptide Cutoffs chosen so criteria simply the Peptides where the defaultPeptideCutoffs is yes

				sqlSB.append( " AND " );


				sqlSB.append( " search_monolink_generic_lookup.num_linked_peptides_at_default_cutoff > 0 " );

			}
		}		
		
		
		sqlSB.append( sqlOrderBy );
		
		
		
		
		final String sql = sqlSB.toString();
		
		return sql;
		
		
	}
	
}