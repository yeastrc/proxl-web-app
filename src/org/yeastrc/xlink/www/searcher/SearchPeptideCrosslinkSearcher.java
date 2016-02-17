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
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslinkAnnDataWrapper;

public class SearchPeptideCrosslinkSearcher {
	
	private static final Logger log = Logger.getLogger(SearchPeptideCrosslinkSearcher.class);

	private SearchPeptideCrosslinkSearcher() { }
	private static final SearchPeptideCrosslinkSearcher _INSTANCE = new SearchPeptideCrosslinkSearcher();
	public static SearchPeptideCrosslinkSearcher getInstance() { return _INSTANCE; }
	


	
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
	

//	String sql = "SELECT a.reported_peptide_id AS reported_peptide_id, a.q_value AS q_value, count(*) AS num_psms, min(b.q_value) AS best_psm_q_value " +
//			  
//					" FROM search_reported_peptide AS a " +
//					" INNER JOIN psm AS b ON (a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id) " +
//					" INNER JOIN crosslink AS c ON b.id = c.psm_id " +
//
//					" WHERE a.search_id = ? AND ( a.q_value <= ? OR a.q_value IS NULL )   AND b.q_value <=? AND b.type = ? " +
//							" AND c.nrseq_id_1 = ? AND c.nrseq_id_2 = ? AND c.protein_1_position = ? AND c.protein_2_position = ? " +
//					
//					" GROUP BY a.reported_peptide_id " +
//					
//					" ORDER BY a.q_value, a.reported_peptide_id";		



	private final String SQL_FIRST_PART = 
			

			"SELECT unified_rp__rep_pept__search__generic_lookup.reported_peptide_id, "
			
			+ " unified_rp__rep_pept__search__generic_lookup.link_type, "
			+ " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff ";

	private final String SQL_MAIN_FROM_START = 
			
			" FROM "

			+ " unified_rp__rep_pept__search__generic_lookup "

			+ " INNER JOIN crosslink ON unified_rp__rep_pept__search__generic_lookup.sample_psm_id = crosslink.psm_id";


	private final String SQL_MAIN_WHERE_START = 
					
			" WHERE unified_rp__rep_pept__search__generic_lookup.search_id = ? "
			+ " AND unified_rp__rep_pept__search__generic_lookup.link_type = '" + XLinkUtils.CROSS_TYPE_STRING + "' "
			+ " AND crosslink.nrseq_id_1 = ? AND crosslink.nrseq_id_2 = ? "
			+ " AND crosslink.protein_1_position = ? AND crosslink.protein_2_position = ?  ";

	
	private final String SQL_LAST_PART = 

			" GROUP BY unified_rp__rep_pept__search__generic_lookup.reported_peptide_id ";

//			+ " ORDER BY unified_rp__rep_pept__search__generic_lookup.reported_peptide_id"; // Sort in Java	
	
	
	
	
	
	/**
	 * Get all crosslink peptides corresponding to the given Criteria
	 * 
	 * @param searchId
	 * @param searcherCutoffValuesSearchLevel
	 * @param protein1Id
	 * @param protein2Id
	 * @param protein1Position
	 * @param protein2Position
	 * @return
	 * @throws Exception
	 */
	public List<SearchPeptideCrosslinkAnnDataWrapper> searchOnSearchProteinCrosslink( 
			int searchId,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int protein1Id,
			int protein2Id,
			int protein1Position,
			int protein2Position
			
			) throws Exception {
	
		
		List<SearchPeptideCrosslinkAnnDataWrapper> wrappedLinks = new ArrayList<>();


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

				//   Only Default PSM Cutoffs chosen so criteria simply the Peptides where the PSM count for the default cutoffs is > zero
				

				sqlSB.append( " AND " );


				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff > 0 " );

				
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
						
			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
			
			
	

			pstmt = conn.prepareStatement( sql );
			


			int paramCounter = 0;
			
			
//	" WHERE unified_rp__rep_pept__search__generic_lookup.search_id = ? "
//	+ " AND unified_rp__rep_pept__search__generic_lookup.link_type = '" + XLinkUtils.CROSS_TYPE_STRING + "' "
//	+ " AND crosslink.nrseq_id_1 = ? AND crosslink.nrseq_id_2 = ? "
//	+ " AND crosslink.protein_1_position = ? AND crosslink.protein_2_position = ?  ";
			
			paramCounter++;
			pstmt.setInt( paramCounter, searchId );
			
			paramCounter++;
			pstmt.setInt( paramCounter, protein1Id );
			paramCounter++;
			pstmt.setInt( paramCounter, protein2Id );
			paramCounter++;
			pstmt.setInt( paramCounter, protein1Position );
			paramCounter++;
			pstmt.setInt( paramCounter, protein2Position );


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
				
				SearchPeptideCrosslinkAnnDataWrapper wrappedLink = new SearchPeptideCrosslinkAnnDataWrapper();
				
				SearchPeptideCrosslink link = new SearchPeptideCrosslink();
				
				wrappedLink.setSearchPeptideCrosslink( link );
				

				link.setSearch( searchDTO );
				
				link.setSearcherCutoffValuesSearchLevel( searcherCutoffValuesSearchLevel );
				
				link.setReportedPeptideId( rs.getInt( "reported_peptide_id" ) );
				
				
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

	
	
	////////////////////////////////////////////////////
	
	/**
	 * Get the crosslink peptides corresponding to the given parameters
	 * @param search The search we're searching
	 * @param psmCutoff The q-value cutoff to use for PSMs
	 * @param peptideCutoff The q-value cutoff to use for peptides
	 * @return
	 * @throws Exception
	 */
	public List<SearchPeptideCrosslink> searchOnSearchIdPsmCutoffPeptideCutoff( SearchDTO search, double psmCutoff, double peptideCutoff ) throws Exception {
		

		if ( true ) 
			throw new Exception( "Not updated for Generic " );
		
		
		List<SearchPeptideCrosslink> links = new ArrayList<SearchPeptideCrosslink>();
				
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		
//		final String sql = "SELECT a.reported_peptide_id, a.q_value, count(*) AS num_psms, min(b.q_value) AS best_psm_q_value " +
//				
//				" FROM search_reported_peptide AS a " +
//				" INNER JOIN psm AS b ON (a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id) " +
//				
//				  " WHERE a.search_id = ? AND ( a.q_value <= ? OR a.q_value IS NULL )   AND b.q_value <=? AND b.type = ? " +
//				  
//				  " GROUP BY a.reported_peptide_id " +
//				  
//				  " ORDER BY a.q_value, a.reported_peptide_id";
//		
//		
//		try {
//						
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//			
//			
//			pstmt = conn.prepareStatement( sql );
//			
//			final String typeString = XLinkUtils.getTypeString( XLinkUtils.TYPE_CROSSLINK ) ;
//			
//			pstmt.setInt( 1, search.getId() );
//			pstmt.setDouble( 2, peptideCutoff );
//			pstmt.setDouble( 3, psmCutoff );
//			pstmt.setString( 4, typeString );
//			
//			rs = pstmt.executeQuery();
//
//			while( rs.next() ) {
//				SearchPeptideCrosslink link = new SearchPeptideCrosslink();
//				
//				link.setSearch( search );
//				link.setPsmQValueCutoff( psmCutoff );
//				link.setPeptideQValueCutoff( peptideCutoff );
//				
//				link.setReportedPeptide( ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( rs.getInt( "reported_peptide_id" ) ) );
//
//				link.setQValue( rs.getDouble( "q_value" ) );
//				if ( rs.wasNull() ) {
//					link.setQValue( null );
//				}
//
//				link.setNumPsms( rs.getInt( "num_psms" ) );
//				link.setBestPsmQValue( rs.getDouble( "best_psm_q_value" ) );
//				
//				links.add( link );
//			}
//			
//		} catch ( Exception e ) {
//			
//			String msg = "Exception in search( SearchDTO search, double psmCutoff, double peptideCutoff ), sql: " + sql;
//			
//			log.error( msg, e );
//			
//			throw e;
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
		
		return links;
	}
	
//	
//	/**
//	 * Get the SearchPeptideCrosslink for the given peptide in the given search with the given search parameters
//	 * @param search
//	 * @param psmCutoff
//	 * @param peptideCutoff
//	 * @param reportedPeptideId
//	 * @return
//	 * @throws Exception
//	 */
//	public SearchPeptideCrosslink searchOnSearchIdPeptideIdPsmCutoffPeptideCutoff( SearchDTO search, double psmCutoff, double peptideCutoff, int reportedPeptideId ) throws Exception {
//		
//		if ( true ) 
//		throw new Exception( "Not updated for Generic " );
//		
//		SearchPeptideCrosslink link = null;
//				
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		
//		final String sql = "SELECT a.reported_peptide_id, a.q_value, count(*) AS num_psms, min(b.q_value) AS best_psm_q_value " +
//				" , psrp.svm_score, psrp.pep, psrp.p_value " +
//				" FROM search_reported_peptide AS a INNER JOIN psm AS b " +
//				" ON (a.search_id = b.search_id AND a.reported_peptide_id = b.reported_peptide_id) " +
//				
//				" LEFT OUTER JOIN percolator_search_reported_peptide AS psrp " +
//				" ON (a.search_id = psrp.search_id AND a.reported_peptide_id = psrp.reported_peptide_id) " +
//				
//				" WHERE a.reported_peptide_id = ? AND a.search_id = ? AND ( a.q_value <= ? OR a.q_value IS NULL )   AND b.q_value <=? AND b.type = ? " +
//				" GROUP BY a.reported_peptide_id "
//				+ " ORDER BY a.reported_peptide_id";
//		
//		try {
//						
//			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );
//
//			
//			pstmt = conn.prepareStatement( sql );
//
//			pstmt.setInt( 1, reportedPeptideId );
//			pstmt.setInt( 2, search.getId() );
//			pstmt.setDouble( 3, peptideCutoff );
//			pstmt.setDouble( 4, psmCutoff );
//			pstmt.setString( 5, XLinkUtils.getTypeString( XLinkUtils.TYPE_CROSSLINK ) );
//			
//			rs = pstmt.executeQuery();
//
//			if( rs.next() ) {
//				link = new SearchPeptideCrosslink();
////				
////				link.setSearch( search );
////				link.setPsmQValueCutoff( psmCutoff );
////				link.setPeptideQValueCutoff( peptideCutoff );
////				
////				link.setReportedPeptide ( ReportedPeptideDAO.getInstance().getReportedPeptideFromDatabase( rs.getInt( "reported_peptide_id" ) ) );
////
////				link.setQValue( rs.getDouble( "q_value" ) );
////				if ( rs.wasNull() ) {
////					link.setQValue( null );
////				}
////
////				link.setNumPsms( rs.getInt( "num_psms" ) );
////				link.setBestPsmQValue( rs.getDouble( "best_psm_q_value" ) );
////				
////				link.setPep( rs.getDouble( "pep" ) );
////				
////				if ( ! rs.wasNull() ) {
////					
////					link.setPepPopulated(true);
////				}
////				
////				
////				link.setSvmScore( rs.getDouble( "svm_score" ) );
////
////				if ( ! rs.wasNull() ) {
////					
////					link.setSvmScorePopulated(true);
////				}
////				
////				link.setpValue( rs.getDouble( "p_value" ) );
////				
////				if ( ! rs.wasNull() ) {
////					
////					link.setpValuePopulated(true);
////				}
//				
//				
//
//				if( rs.next() )
//					throw new Exception( "Got two instances of a peptide in a single search..." );
//				
//			}
//			
//		} catch ( Exception e ) {
//			
//			String msg = "Exception in search( SearchDTO search, double psmCutoff, double peptideCutoff, ReportedPeptideDTO peptide ), sql: " + sql;
//			
//			log.error( msg, e );
//			
//			throw e;
//			
//		} finally {
//			
//			// be sure database handles are closed
//			if( rs != null ) {
//				try { rs.close(); } catch( Throwable t ) { ; }
//				rs = null;
//			}
//			
//			if( pstmt != null ) {
//				try { pstmt.close(); } catch( Throwable t ) { ; }
//				pstmt = null;
//			}
//			
//			if( conn != null ) {
//				try { conn.close(); } catch( Throwable t ) { ; }
//				conn = null;
//			}
//			
//		}
//		
//		return link;
//	}
	
}
