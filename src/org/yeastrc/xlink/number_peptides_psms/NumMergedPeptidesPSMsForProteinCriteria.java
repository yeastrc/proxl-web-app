package org.yeastrc.xlink.number_peptides_psms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.constants.Database_OneTrueZeroFalse_Constants;
import org.yeastrc.xlink.db.DBConnectionFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.Yes_No__NOT_APPLICABLE_Enum;
import org.yeastrc.xlink.exceptions.ProxlBaseDataException;
import org.yeastrc.xlink.searcher_constants.SearcherGeneralConstants;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.searchers.PsmCountForSearchIdReportedPeptideIdSearcher;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * Version of NumPeptidesPSMsForProteinCriteriaSearcher for Merged Proteins
 *
 */
public class NumMergedPeptidesPSMsForProteinCriteria {

	private NumMergedPeptidesPSMsForProteinCriteria() { }
	public static NumMergedPeptidesPSMsForProteinCriteria getInstance() { return new NumMergedPeptidesPSMsForProteinCriteria(); }

	private static final Logger log = Logger.getLogger(NumMergedPeptidesPSMsForProteinCriteria.class);
	
	
	private static final String SQL_LINK_TABLE_ALIAS = "link_table";
	

	private final String PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS = "psm_fltrbl_tbl_";
	

	
	
	///////////////////////////////////////////////////////////////

	private final String SQL_CROSSLINK_TABLE_NAME = "crosslink";

	private final String SQL_CROSSLINK_WHERE_FOR_LINK_TABLE = 

			"  " + SQL_LINK_TABLE_ALIAS + ".nrseq_id_1 = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".nrseq_id_2 = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".protein_1_position = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".protein_2_position = ? ";
	

	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this crosslink
	 * 
	 * @param searchIds
	 * @param searcherCutoffValuesRootLevel
	 * @param nrseqId_protein_1
	 * @param nrseqId_protein_2
	 * @param position_protein_1
	 * @param position_protein_2
	 * @return
	 * @throws Exception
	 */
	public NumPeptidesPSMsForProteinCriteriaResult getNumPeptidesPSMsForCrosslink( 
			List<SearchDTO> searches,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel,
			int nrseqId_protein_1,
			int nrseqId_protein_2,
			int position_protein_1,
			int position_protein_2

			) throws Exception {
		
		
		

		int[] linkTableParams = {
				
				nrseqId_protein_1,
				nrseqId_protein_2,
				position_protein_1,
				position_protein_2
		};
				
		return getCount( 
				searches, 
				searcherCutoffValuesRootLevel, 
				linkTableParams, 
				XLinkUtils.CROSS_TYPE_STRING, 
				SQL_CROSSLINK_TABLE_NAME, 
				SQL_CROSSLINK_WHERE_FOR_LINK_TABLE ) ;
		
	}

	////////////////////////////////////////////////////////////////////
	
	//////////   LOOPLINK
	
	///////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////////
	

	private final String SQL_LOOPLINK_TABLE_NAME = "looplink";

	private final String SQL_LOOPLINK_WHERE_FOR_LINK_TABLE = 

	"  " + SQL_LINK_TABLE_ALIAS + ".nrseq_id = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".protein_position_1 = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".protein_position_2 = ? ";

	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this looplink
	 * 
	 * @param searches
	 * @param searcherCutoffValuesRootLevel
	 * @param nrseqId_protein
	 * @param protein_position_1
	 * @param protein_position_2
	 * @return
	 * @throws Exception
	 */
	public NumPeptidesPSMsForProteinCriteriaResult getNumPeptidesPSMsForLooplink( 

			List<SearchDTO> searches,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel,
			int nrseqId_protein,
			int protein_position_1,
			int protein_position_2

			 ) throws Exception {
		
		int[] linkTableParams = {
				
				nrseqId_protein,
				protein_position_1,
				protein_position_2
		};
		

		return getCount( 
				searches, 
				searcherCutoffValuesRootLevel, 
				linkTableParams, 
				XLinkUtils.LOOP_TYPE_STRING, 
				SQL_LOOPLINK_TABLE_NAME, 
				SQL_LOOPLINK_WHERE_FOR_LINK_TABLE ) ;
		
	}

	
	
	////////////////////////////////////////////////////////
	
	/////////////    MONOLINK

	///////////////////////////////////////////////////



	////////////////////////////////////////////////////////////////////////////////////
	

	private final String SQL_MONOLINK_TABLE_NAME = "monolink";

	private final String SQL_MONOLINK_WHERE_FOR_LINK_TABLE = 

	"  " + SQL_LINK_TABLE_ALIAS + ".nrseq_id = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".protein_position = ? ";


	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this monolink
	 * 
	 * @param searches
	 * @param searcherCutoffValuesRootLevel
	 * @param nrseqId_protein
	 * @param protein_position
	 * @return
	 * @throws Exception
	 */
	public NumPeptidesPSMsForProteinCriteriaResult getNumPeptidesPSMsForMonolink( 

			List<SearchDTO> searches,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel,
			int nrseqId_protein,
			int protein_position

			 ) throws Exception {


		int[] linkTableParams = {
				
				nrseqId_protein,
				protein_position
		};
		

		return getCount( 
				searches, 
				searcherCutoffValuesRootLevel, 
				linkTableParams, 
				null /* link type */, 
				SQL_MONOLINK_TABLE_NAME, 
				SQL_MONOLINK_WHERE_FOR_LINK_TABLE ) ;
		

	}

	
	//////////////////////////////////////////////////
	
	//////////////      DIMER

	private final String SQL_DIMER_TABLE_NAME = "dimer";

	private final String SQL_DIMER_WHERE_FOR_LINK_TABLE = 

	"  " + SQL_LINK_TABLE_ALIAS + ".nrseq_id_1 = ? "
			+ " AND " + SQL_LINK_TABLE_ALIAS + ".nrseq_id_2 = ? ";

	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this dimer
	 * 
	 * @param searches
	 * @param searcherCutoffValuesRootLevel
	 * @param nrseqId_protein_1
	 * @param nrseqId_protein_2
	 * @return
	 * @throws Exception
	 */
	public NumPeptidesPSMsForProteinCriteriaResult getNumPeptidesPSMsForDimer( 
			List<SearchDTO> searches,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel,
			int nrseqId_protein_1,
			int nrseqId_protein_2

			) throws Exception {
		
		int[] linkTableParams = {
				
				nrseqId_protein_1,
				nrseqId_protein_2
		};

		return getCount( 
				searches, 
				searcherCutoffValuesRootLevel, 
				linkTableParams, 
				XLinkUtils.DIMER_TYPE_STRING, 
				SQL_DIMER_TABLE_NAME, 
				SQL_DIMER_WHERE_FOR_LINK_TABLE ) ;
	}


	
	////////////////////////////////////////////////////////
	
	/////////////    UNLINKED

	///////////////////////////////////////////////////


	private final String SQL_UNLINKED_TABLE_NAME = "unlinked";

	private final String SQL_UNLINKED_WHERE_FOR_LINK_TABLE = 

	"  " + SQL_LINK_TABLE_ALIAS + ".nrseq_id = ? ";

	/**
	 * Get the number of peptides (pair of peptides) that UNIQUELY identified the pair of proteins+positions represented by this unlinked
	 * 
	 * @param searches
	 * @param searcherCutoffValuesRootLevel
	 * @param nrseqId_protein
	 * @return
	 * @throws Exception
	 */
	public NumPeptidesPSMsForProteinCriteriaResult getNumPeptidesPSMsForUnlinked( 

			List<SearchDTO> searches,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel,
			int nrseqId_protein

			 ) throws Exception {
		
		int[] linkTableParams = {
				
				nrseqId_protein
		};

		return getCount( 
				searches, 
				searcherCutoffValuesRootLevel, 
				linkTableParams, 
				XLinkUtils.UNLINKED_TYPE_STRING, 
				SQL_UNLINKED_TABLE_NAME, 
				SQL_UNLINKED_WHERE_FOR_LINK_TABLE ) ;
	}



	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	
	
	private NumPeptidesPSMsForProteinCriteriaResult getCount( 
			
			List<SearchDTO> searches,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel,
			int[] linkTableParams,
			String linkTypeString,  // null for monolink
			String linkTableName,
			String sqlWhereForlinkTable
			 ) throws Exception {

		if ( linkTableParams == null ) {
			
			String msg = "linkTableParams cannot be null";
			log.error( msg );
			throw new IllegalArgumentException(msg);
		}
		
		int peptideCount = 0;
		int uniquePeptideCount = 0;
		int psmCount = 0;
		
		Map<Integer, List<SinglePeptidePerSearchData>> reportedPeptideDataMappedOnId = new HashMap<>();
		
		///////////////
		
		// dedup and sort search ids
		
		Set<Integer> searchIdsSet = new HashSet<>(  );
		
		for ( SearchDTO search : searches ) {
			
			searchIdsSet.add( search.getId() );
		}
		
		List<Integer> searchIdsDedupSortedList = new ArrayList<>( searchIdsSet );
		
		Collections.sort( searchIdsDedupSortedList );
		
		///////////////

		ParamsPreProcesingAllSearchesResult paramsPreProcesingAllSearchesResult  = paramsPreProcesing( searchIdsDedupSortedList, searcherCutoffValuesRootLevel );

		for ( ParamsPreProcesingResult paramsPreProcesingResult : paramsPreProcesingAllSearchesResult.paramsPreProcesingResultPerSearchList ) {

			int searchId = paramsPreProcesingResult.searchId;
			
			SearchDTO search = null;

			for ( SearchDTO searchItem : searches ) {
				
				if ( searchItem.getId() == searchId ) {
					
					search = searchItem;
					break;
				}
			}
			
			
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
					searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );
			
			List<SinglePeptidePerSearchData> resultsForSingleSearch = 
					getPeptidePSMDataforSingleSearch( 
							search,
							paramsPreProcesingResult, 
							searcherCutoffValuesSearchLevel, 
							linkTableParams, 
							linkTypeString, 
							linkTableName, 
							sqlWhereForlinkTable );
			
			for ( SinglePeptidePerSearchData singlePeptidePerSearchData : resultsForSingleSearch ) {

				List<SinglePeptidePerSearchData> singlePeptidePerSearchDataList =
						reportedPeptideDataMappedOnId.get( singlePeptidePerSearchData.reportedPeptideId );
				
				if ( singlePeptidePerSearchDataList == null ) {
					
					singlePeptidePerSearchDataList = new ArrayList<>();
					reportedPeptideDataMappedOnId.put( singlePeptidePerSearchData.reportedPeptideId, singlePeptidePerSearchDataList );
				}
				
				singlePeptidePerSearchDataList.add(singlePeptidePerSearchData);
			}
		}
		
		for ( Map.Entry<Integer, List<SinglePeptidePerSearchData>> entry : reportedPeptideDataMappedOnId.entrySet() ) {
			
			List<SinglePeptidePerSearchData> singlePeptidePerSearchDataList = entry.getValue();
			
			peptideCount++;
			
			boolean peptideUnique = true;

			for ( SinglePeptidePerSearchData singlePeptidePerSearchData : singlePeptidePerSearchDataList ) {
			
				if ( ! singlePeptidePerSearchData.isPeptideUnique ) {
					
					peptideUnique = false;
				}
				
				psmCount += singlePeptidePerSearchData.psmCountForReportedPeptideSearchId;
			}
			
			if ( peptideUnique ) {
				uniquePeptideCount++;
			}
			
		}
		
		
		
		NumPeptidesPSMsForProteinCriteriaResult result = new NumPeptidesPSMsForProteinCriteriaResult();
		
		result.setNumPeptides(peptideCount);
		result.setNumUniquePeptides(uniquePeptideCount);
		result.setNumPSMs(psmCount);

		return result;
		
	}
	
	
	
	
	private List<SinglePeptidePerSearchData> getPeptidePSMDataforSingleSearch( 
			
			SearchDTO search,
			ParamsPreProcesingResult paramsPreProcesingResult,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			int[] linkTableParams,
			String linkTypeString,  // null for monolink
			String linkTableName,
			String sqlWhereForlinkTable
			
			) throws Exception {
		
		
		List<SinglePeptidePerSearchData> resultsForSingleSearch = new ArrayList<>();
		
		int searchId = paramsPreProcesingResult.searchId;
		

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;


		//////////////////////

		/////   Start building the SQL

		String sql = null;


		sql = getSQL( 
				paramsPreProcesingResult, 
				linkTypeString, 
				linkTableName, 
				sqlWhereForlinkTable );
		
		try {

			conn = DBConnectionFactory.getConnection( DBConnectionFactory.CROSSLINKS );

			pstmt = conn.prepareStatement( sql );

			int paramCounter = 0;


			//  Subquery Join for link table
			
			for ( int linkTableParam : linkTableParams ) {

				paramCounter++;
				pstmt.setInt( paramCounter,  linkTableParam );
			}
			
			paramCounter++;
			pstmt.setInt( paramCounter,  searchId );


			// Process PSM And Peptide Cutoffs for WHERE

			//  Must be last

			setCutoffParams( searchId, 
					paramsPreProcesingResult,
					pstmt, 
					paramCounter );

			rs = pstmt.executeQuery();
			
			int prevReportedPeptideId = -999;

			while( rs.next() ) {

				int reportedPeptideId = rs.getInt( "reported_peptide_id" );
				
				if ( reportedPeptideId == prevReportedPeptideId ) {
					
					String msg = "reported peptide already processed: " + reportedPeptideId
							+ ", This reported peptide id is in the DB with different peptide ids";
					log.error( msg );
					throw new ProxlBaseDataException(msg);
				}

				boolean allRelatedPeptidesUniqueForSearch = false;

				int allRelatedPeptidesUniqueForSearchInt = rs.getInt( "related_peptides_unique_for_search" );

				if ( Database_OneTrueZeroFalse_Constants.DATABASE_FIELD_TRUE == allRelatedPeptidesUniqueForSearchInt ) {
					allRelatedPeptidesUniqueForSearch = true;
				}

				int psmCountForReportedPeptideSearchId = rs.getInt( "psm_num_at_default_cutoff" );

				if ( ! paramsPreProcesingResult.onlyDefaultPsmCutoffs ) {

					psmCountForReportedPeptideSearchId = 
						PsmCountForSearchIdReportedPeptideIdSearcher.getInstance()
						.getPsmCountForSearchIdReportedPeptideId( reportedPeptideId, searchId, searcherCutoffValuesSearchLevel );
				}
				
				if ( psmCountForReportedPeptideSearchId > 0 ) {

					SinglePeptidePerSearchData singlePeptidePerSearchData = new SinglePeptidePerSearchData();

					singlePeptidePerSearchData.searchId = searchId;
					singlePeptidePerSearchData.reportedPeptideId = reportedPeptideId;
					singlePeptidePerSearchData.psmCountForReportedPeptideSearchId += psmCountForReportedPeptideSearchId;
					singlePeptidePerSearchData.isPeptideUnique = allRelatedPeptidesUniqueForSearch;
					
					resultsForSingleSearch.add( singlePeptidePerSearchData );
				}
				
				prevReportedPeptideId = reportedPeptideId;
			}

		} catch ( Exception e ) {

			String msg = "Exception in internal getCount( ... ): sql: " + sql;

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
		
		return resultsForSingleSearch;
	}
	
	
	

	
	private class ParamsPreProcesingResult {

		int searchId;
		
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList;
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList;

		Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;
		boolean onlyDefaultPsmCutoffs = true;
		
	}
	
	
	
	private class ParamsPreProcesingAllSearchesResult {

		List<ParamsPreProcesingResult> paramsPreProcesingResultPerSearchList;
		
	}
	
	private class SinglePeptidePerSearchData {
		
		int searchId;
		int reportedPeptideId;
		int psmCountForReportedPeptideSearchId;
		boolean isPeptideUnique;

		
	}
	


	/**
	 * @param searcherCutoffValuesRootLevel
	 * @return
	 * @throws Exception
	 */
	private ParamsPreProcesingAllSearchesResult paramsPreProcesing( List<Integer> searchIdsDedupSortedList, SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel ) throws Exception {
		
		
		
		//  Copy cutoff values to lists (need to guarantee order since process same objects in multiple places)

		List<ParamsPreProcesingResult> paramsPreProcesingResultPerSearchList = new ArrayList<>( searchIdsDedupSortedList.size() );
		
		for ( Integer searchId : searchIdsDedupSortedList ) {

			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );

			if ( searcherCutoffValuesSearchLevel == null ) {

				String msg = "Unable to get cutoffs for search id: " + searchId;
				log.error( msg );
				throw new Exception(msg);
			}

			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

			////////////

			//  All cutoffs are default?

			Yes_No__NOT_APPLICABLE_Enum   defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE;

			boolean onlyDefaultPsmCutoffs = true;



			//   Check if any Peptide Cutoffs are default filters

			for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

				if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO() == null ) {

					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + item.getAnnotationTypeDTO().getId();
					log.error( msg );
					throw new Exception(msg);
				}

				if ( item.getAnnotationTypeDTO().getAnnotationTypeFilterableDTO().isDefaultFilter() ) {

					defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.YES;
					break;
				}
			}


			//   Check if all Peptide Cutoffs are default values

			for ( SearcherCutoffValuesAnnotationLevel item : peptideCutoffValuesList ) {

				if ( ! item.annotationValueMatchesDefault() ) {

					defaultPeptideCutoffs = Yes_No__NOT_APPLICABLE_Enum.NO;
					break;
				}
			}

			//   Check if all Psm Cutoffs are default values

			for ( SearcherCutoffValuesAnnotationLevel item : psmCutoffValuesList ) {

				if ( ! item.annotationValueMatchesDefault() ) {

					onlyDefaultPsmCutoffs = false;
					break;
				}
			}
			
			ParamsPreProcesingResult paramsPreProcesingResult = new ParamsPreProcesingResult();
			
			paramsPreProcesingResult.searchId = searchId;

			paramsPreProcesingResult.peptideCutoffValuesList = peptideCutoffValuesList;
			paramsPreProcesingResult.psmCutoffValuesList = psmCutoffValuesList;

			paramsPreProcesingResult.defaultPeptideCutoffs = defaultPeptideCutoffs;
			paramsPreProcesingResult.onlyDefaultPsmCutoffs = onlyDefaultPsmCutoffs;
			
			paramsPreProcesingResultPerSearchList.add( paramsPreProcesingResult );
		}
		
		ParamsPreProcesingAllSearchesResult paramsPreProcesingAllSearchesResult = new ParamsPreProcesingAllSearchesResult();
		
		paramsPreProcesingAllSearchesResult.paramsPreProcesingResultPerSearchList = paramsPreProcesingResultPerSearchList;
		
		return paramsPreProcesingAllSearchesResult;
	}
	

	
	
	
	/**
	 * 
	 * 
	 * @param paramsPreProcesingResult
	 * @param linkTypeString
	 * @param linkTableName
	 * @param sqlWhereForlinkTable
	 * @return
	 * @throws Exception
	 */
	private String getSQL(
			
			ParamsPreProcesingResult paramsPreProcesingResult,
			
			String linkTypeString, // null for monolink
			
			String linkTableName, 
			String sqlWhereForlinkTable
			 ) throws Exception {
		
		
		
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = paramsPreProcesingResult.peptideCutoffValuesList;
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = paramsPreProcesingResult.psmCutoffValuesList;
		Yes_No__NOT_APPLICABLE_Enum defaultPeptideCutoffs = paramsPreProcesingResult.defaultPeptideCutoffs;
		boolean onlyDefaultPsmCutoffs = paramsPreProcesingResult.onlyDefaultPsmCutoffs;
		
		StringBuilder sqlSB = new StringBuilder( 1000 );

		sqlSB.append( "SELECT distinct unified_rp__rep_pept__search__generic_lookup.reported_peptide_id AS reported_peptide_id "  );
		
		sqlSB.append( " , unified_rp__rep_pept__search__generic_lookup.related_peptides_unique_for_search " );
		sqlSB.append( " , unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff " );
		
		

		sqlSB.append( " FROM unified_rp__rep_pept__search__generic_lookup  " );
		
		//  Join with link table subquery
		
		sqlSB.append( "INNER JOIN " );
		
		sqlSB.append( " ( " );
		sqlSB.append( "SELECT distinct  "  );
		sqlSB.append( SQL_LINK_TABLE_ALIAS );
		sqlSB.append( ".psm_id " );
				
		sqlSB.append( " FROM " );
		sqlSB.append( linkTableName );
		sqlSB.append( " AS " );
		sqlSB.append( SQL_LINK_TABLE_ALIAS );
		sqlSB.append( " WHERE " );
		sqlSB.append( sqlWhereForlinkTable );

		sqlSB.append( " ) AS " );

		sqlSB.append( SQL_LINK_TABLE_ALIAS );

		
		sqlSB.append( " ON unified_rp__rep_pept__search__generic_lookup.sample_psm_id = " );
		sqlSB.append( SQL_LINK_TABLE_ALIAS );
		sqlSB.append( ".psm_id " );
		
		

		{

			if ( ! onlyDefaultPsmCutoffs ) { //  Can only use this if psm table or a lookup has a flag for "met default cutoffs"
				

				//  Add inner join for each PSM cutoff

				for ( int counter = 1; counter <= psmCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );

					sqlSB.append( " unified_rp__rep_pept__search__best_psm_value_generic_lookup AS " );
					
					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.search_id = "  );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = "  );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );
				}

			}
		
		}
		
		
		{
			
			if ( defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {

				//  Non-Default PSM cutoffs so have to query on the cutoffs

				//  Add inner join for each Peptide cutoff

				for ( int counter = 1; counter <= peptideCutoffValuesList.size(); counter++ ) {

					sqlSB.append( " INNER JOIN " );

					sqlSB.append( " srch__rep_pept__annotation AS srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );

					sqlSB.append( " ON "  );

					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.search_id = "  );

					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id" );

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.reported_peptide_id = "  );

					sqlSB.append( "srch__rep_pept_fltrbl_tbl_" );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".reported_peptide_id" );

				}
			}
		}
		

		///////////
		
		sqlSB.append( " WHERE unified_rp__rep_pept__search__generic_lookup.search_id = ? " );
		
		
		//////////
		

		// Process PSM Cutoffs for WHERE

		{

			
			if ( onlyDefaultPsmCutoffs ) {
				
				//   Only Default PSM Cutoffs chosen so criteria simply the Peptides where the PSM count for the default cutoffs is > zero
				

				sqlSB.append( " AND " );


				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.psm_num_at_default_cutoff > 0 " );

				
			} else {

				
				//  Non-Default PSM cutoffs so have to query on the cutoffs

				int counter = 0; 

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesPsmAnnotationLevel : psmCutoffValuesList ) {



					AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = searcherCutoffValuesPsmAnnotationLevel.getAnnotationTypeDTO();

					counter++;

					sqlSB.append( " AND " );

					sqlSB.append( " ( " );


					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".search_id = ? AND " );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
					sqlSB.append( Integer.toString( counter ) );
					sqlSB.append( ".annotation_type_id = ? AND " );

					sqlSB.append( PSM_BEST_VALUE_FOR_PEPTIDE_FILTER_TABLE_ALIAS );
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
			}
		}
		

		//  Process Peptide Cutoffs for WHERE

		{

			if ( defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE ) {

				//  No WHERE criteria for defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NOT_APPLICABLE
				
				//     There are no Peptide cutoffs to apply
				
				
			
			} else if ( defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.YES ) {

				//   Only Default Peptide Cutoffs chosen so criteria simply the Peptides where the defaultPeptideCutoffs is yes

				sqlSB.append( " AND " );


				sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.peptide_meets_default_cutoffs = '" );
				sqlSB.append( Yes_No__NOT_APPLICABLE_Enum.YES.value() );
				sqlSB.append( "' " );
				
				if ( linkTypeString != null ) {

					sqlSB.append( " AND " );


					sqlSB.append( " unified_rp__rep_pept__search__generic_lookup.link_type = '" );
					sqlSB.append( linkTypeString );
					sqlSB.append( "' " );
					
//					if ( log.isInfoEnabled() ) {
//						log.info( "Using unified_rp__rep_pept__search__generic_lookup.link_type = '" + linkTypeString + "' ");
//					}
				}

				
			} else if ( defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {

				
				//  Non-Default Peptide cutoffs so have to query on the cutoffs

				int counter = 0; 

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

					AnnotationTypeDTO AnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();

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
					
					if ( AnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {
						
						String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + AnnotationTypeDTO.getId();
						log.error( msg );
						throw new Exception(msg);
					}
					

					if ( AnnotationTypeDTO.getAnnotationTypeFilterableDTO().getFilterDirectionType() == FilterDirectionType.ABOVE ) {

						sqlSB.append( SearcherGeneralConstants.SQL_END_BIGGER_VALUE_BETTER );

					} else {

						sqlSB.append( SearcherGeneralConstants.SQL_END_SMALLER_VALUE_BETTER );

					}

					sqlSB.append( "? " );

					sqlSB.append( " ) " );
				}
			}
		}
		
		sqlSB.append( " ORDER BY reported_peptide_id " );
		
		
		
		String sql = sqlSB.toString();
		

//		if ( linkTypeString != null ) {
//
//			
//			if ( log.isInfoEnabled() ) {
//				log.info( "Using unified_rp__rep_pept__search__generic_lookup.link_type = '" + linkTypeString + "'. "
//						+ " SQL:  " + sql );
//			}
//		}
		
		
		return sql;
	}
	
	

	/**
	 * Get Peptide count Set PreparedStatement Params for All Types
	 * 
	 * @param searchIds
	 * @param peptideCutoffValuesList
	 * @param psmCutoffValuesList
	 * @param defaultPeptideCutoffs
	 * @param pstmt
	 * @param paramCounter
	 * @throws SQLException
	 */
	private void setCutoffParams(
			int searchId,
			ParamsPreProcesingResult paramsPreProcesingResult,
			PreparedStatement pstmt, 
			int paramCounter) throws SQLException {

		
		List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = paramsPreProcesingResult.peptideCutoffValuesList;
		List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = paramsPreProcesingResult.psmCutoffValuesList;
		Yes_No__NOT_APPLICABLE_Enum defaultPeptideCutoffs = paramsPreProcesingResult.defaultPeptideCutoffs;
		boolean onlyDefaultPsmCutoffs = paramsPreProcesingResult.onlyDefaultPsmCutoffs;

		
		// Process PSM Cutoffs for WHERE


		{
			
			if ( ! onlyDefaultPsmCutoffs ) {
				
				//  PSM Cutoffs are not the default 
				

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

			if ( defaultPeptideCutoffs == Yes_No__NOT_APPLICABLE_Enum.NO ) {
				
				//  Non-Default Peptide cutoffs so have to query on the cutoffs

				for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesReportedPeptideAnnotationLevel : peptideCutoffValuesList ) {

					AnnotationTypeDTO AnnotationTypeDTO = searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationTypeDTO();

					paramCounter++;
					pstmt.setInt( paramCounter, searchId );

					paramCounter++;
					pstmt.setInt( paramCounter, AnnotationTypeDTO.getId() );

					paramCounter++;
					pstmt.setDouble( paramCounter, searcherCutoffValuesReportedPeptideAnnotationLevel.getAnnotationCutoffValue() );
				}
			}
		}
	}
	
	

}
