package org.yeastrc.xlink.www.qc_data.summary_statistics.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.WebProteinPosition;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.summary_statistics.main.QC_SummaryCounts_CachedResultManager.QC_SummaryCounts_CachedResultManager_Result;
import org.yeastrc.xlink.www.qc_data.summary_statistics.objects.QC_SummaryCountsResults;
import org.yeastrc.xlink.www.qc_data.summary_statistics.objects.QC_SummaryCountsResults.QC_SummaryCountsResultsPerLinkType;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData;
import org.yeastrc.xlink.www.searcher_utils.DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class QC_SummaryCounts {

	private static final Logger log = Logger.getLogger(QC_SummaryCounts.class);
	
	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 */
	static final int VERSION_FOR_CACHING = 2;
	
	
	public enum ForDownload { YES, NO }
	
	/**
	 * private constructor
	 */
	private QC_SummaryCounts(){}
	public static QC_SummaryCounts getInstance( ) throws Exception {
		QC_SummaryCounts instance = new QC_SummaryCounts();
		return instance;
	}

	/**
	 * Response from call to getQC_SummaryCounts(...)
	 *
	 */
	public static class QC_SummaryCounts_Method_Response {

		private byte[] resultsAsBytes; //  summaryCountsResults as JSON
		private QC_SummaryCountsResults summaryCountsResults;

		public byte[] getResultsAsBytes() {
			return resultsAsBytes;
		}
		public void setResultsAsBytes(byte[] resultsAsBytes) {
			this.resultsAsBytes = resultsAsBytes;
		}
		public QC_SummaryCountsResults getSummaryCountsResults() {
			return summaryCountsResults;
		}
		public void setSummaryCountsResults(QC_SummaryCountsResults summaryCountsResults) {
			this.summaryCountsResults = summaryCountsResults;
		}
	}
	
	
	/**
	 * @param filterCriteriaJSON
	 * @param projectSearchIdsListDeduppedSorted
	 * @param searches
	 * @param searchesMapOnSearchId
	 * @return
	 * @throws Exception
	 */
	public QC_SummaryCounts_Method_Response getQC_SummaryCounts( 
			String requestQueryString, // query string from request URL
			ForDownload forDownload,
			String filterCriteriaJSON, 
			SearchDTO search ) throws Exception {

		List<Integer> searchIdsList = new ArrayList<>( 1 );
		searchIdsList.add( search.getSearchId() );

		MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder =
				getMergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder( filterCriteriaJSON, searchIdsList );

		MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot = mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder.mergedPeptideQueryJSONRoot;
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder.searcherCutoffValuesRootLevel;
		
		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( mergedPeptideQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = mergedPeptideQueryJSONRoot.getMods();

		//  Only applicable if search cutoffs are defaults
		boolean searchOnlyHasDefaultCutoffs = get_searchOnlyHasDefaultCutoffs( search, mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder);

		if ( forDownload != ForDownload.YES ) {
			//  Only if not for download
			if ( searchOnlyHasDefaultCutoffs ) {
				byte[] resultsAsBytes = 
						retrieveDataFromCacheAndMatchCutoffs( search, mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder, requestQueryString );

				if ( resultsAsBytes != null ) {
					//  Have Cached data so return it
					QC_SummaryCounts_Method_Response methodResponse = new QC_SummaryCounts_Method_Response();
					methodResponse.resultsAsBytes = resultsAsBytes;
					
					return methodResponse;  //  EARLY RETURN
				}
			}
		}		
		
		Map<String,PerLinkTypeTempData> perLinkTypeTempData_ByLinkType = new HashMap<>();
		
		//  Populate countForLinkType_ByLinkType for selected link types
		if ( mergedPeptideQueryJSONRoot.getLinkTypes() == null || mergedPeptideQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		} else {
			for ( String linkTypeFromWeb : mergedPeptideQueryJSONRoot.getLinkTypes() ) {
				String linkType = null;
				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkTypeFromWeb ) ) {
					linkType = XLinkUtils.CROSS_TYPE_STRING;
				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkTypeFromWeb ) ) {
					linkType = XLinkUtils.LOOP_TYPE_STRING;
				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkTypeFromWeb ) ) {
					linkType = XLinkUtils.UNLINKED_TYPE_STRING;
				} else {
					String msg = "linkType is invalid, linkTypeFromWeb: " + linkTypeFromWeb;
					log.error( msg );
					throw new Exception( msg );
				}
				PerLinkTypeTempData perLinkTypeTempData = new PerLinkTypeTempData();
				perLinkTypeTempData_ByLinkType.put( linkType, perLinkTypeTempData );
			}
		}
		
		Integer projectSearchId = search.getProjectSearchId();
		Integer searchId = search.getSearchId();

		//  Get cutoffs for this project search id
		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
				searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
		if ( searcherCutoffValuesSearchLevel == null ) {
			String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}

		///////////////////////////////////////////////
		//  Get peptides for this search from the DATABASE
		List<WebReportedPeptideWrapper> wrappedLinksPerForSearch =
				PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
						search, searcherCutoffValuesSearchLevel, 
						linkTypesForDBQuery,
						modsForDBQuery, 
						PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );

		for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedLinksPerForSearch ) {
			WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
			int reportedPeptideId = webReportedPeptide.getReportedPeptideId();

			String linkType = null;

			if ( webReportedPeptide.getSearchPeptideCrosslink() != null ) {
				//  Process a crosslink
				linkType = XLinkUtils.CROSS_TYPE_STRING;
			} else if ( webReportedPeptide.getSearchPeptideLooplink() != null ) {
				//  Process a looplink
				linkType = XLinkUtils.LOOP_TYPE_STRING;
			} else if ( webReportedPeptide.getSearchPeptideUnlinked() != null ) {
				//  Process a unlinked
				linkType = XLinkUtils.UNLINKED_TYPE_STRING;
			} else if ( webReportedPeptide.getSearchPeptideDimer() != null ) {
				//  Process a dimer
				linkType = XLinkUtils.UNLINKED_TYPE_STRING;  //  Lump in with unlinked reported peptides
			} else {
				String msg = 
						"Link type unkown"
								+ " for reportedPeptideId: " + reportedPeptideId
								+ ", searchId: " + searchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}

			// get object from map for link type
			PerLinkTypeTempData perLinkTypeTempData = perLinkTypeTempData_ByLinkType.get( linkType );
			if ( perLinkTypeTempData == null ) {
				String msg = "In updating for link type, link type not found: " + linkType;
				log.error( msg );
				throw new Exception(msg);
			}

			//  Update perLinkTypeTempData for this reported peptide entry
			perLinkTypeTempData.psmCount += webReportedPeptide.getNumPsms();
			perLinkTypeTempData.reportedPeptideIds.add( reportedPeptideId );

			Set<Integer> proteinSequenceVersionIds = perLinkTypeTempData.proteinSequenceVersionIds;

			List<WebProteinPosition> peptide_1_ProteinPositionsList = webReportedPeptide.getPeptide1ProteinPositions();
			if ( peptide_1_ProteinPositionsList != null ) {
				for ( WebProteinPosition webProteinPosition : peptide_1_ProteinPositionsList ) {
					int proteinSequenceVersionId = webProteinPosition.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
					proteinSequenceVersionIds.add( proteinSequenceVersionId );
				}
			}
			List<WebProteinPosition> peptide_2_ProteinPositionsList = webReportedPeptide.getPeptide2ProteinPositions();
			if ( peptide_2_ProteinPositionsList != null ) {
				for ( WebProteinPosition webProteinPosition : peptide_2_ProteinPositionsList ) {
					int proteinSequenceVersionId = webProteinPosition.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
					proteinSequenceVersionIds.add( proteinSequenceVersionId );
				}
			}

		}
		
		//  compute uniqueproteinSequenceVersionIdCountAllLinkTypes.  combine all perLinkTypeTempData.proteinSequenceVersionIds together
		Set<Integer> all_proteinSequenceVersionIds = new HashSet<>();
		for ( Map.Entry<String,PerLinkTypeTempData> entry : perLinkTypeTempData_ByLinkType.entrySet() ) {
			all_proteinSequenceVersionIds.addAll( entry.getValue().proteinSequenceVersionIds );
		}
		
		
//		//  copy map to array for output, in a specific order
		List<QC_SummaryCountsResultsPerLinkType> resultsPerLinkTypeList = new ArrayList<>( perLinkTypeTempData_ByLinkType.size() );
		createReultObjectPerLinkTypeAndAddToOutputListForLinkType( XLinkUtils.CROSS_TYPE_STRING, resultsPerLinkTypeList, perLinkTypeTempData_ByLinkType );
		createReultObjectPerLinkTypeAndAddToOutputListForLinkType( XLinkUtils.LOOP_TYPE_STRING, resultsPerLinkTypeList, perLinkTypeTempData_ByLinkType );
		createReultObjectPerLinkTypeAndAddToOutputListForLinkType( XLinkUtils.UNLINKED_TYPE_STRING, resultsPerLinkTypeList, perLinkTypeTempData_ByLinkType );
		
		QC_SummaryCountsResults qc_SummaryCountsResults = new QC_SummaryCountsResults();
		qc_SummaryCountsResults.setResultsPerLinkTypeList( resultsPerLinkTypeList );
		qc_SummaryCountsResults.setUniqueproteinSequenceVersionIdCountAllLinkTypes( all_proteinSequenceVersionIds.size() );

		byte[] resultAsJSONBytes = getResultsByteArray( qc_SummaryCountsResults, search.getSearchId() );
		
		if ( searchOnlyHasDefaultCutoffs ) {
			cacheResult( resultAsJSONBytes, search, requestQueryString );
		}
		
		QC_SummaryCounts_Method_Response qc_SummaryCounts_Method_Response = new QC_SummaryCounts_Method_Response();
		qc_SummaryCounts_Method_Response.summaryCountsResults = qc_SummaryCountsResults;
		qc_SummaryCounts_Method_Response.resultsAsBytes = resultAsJSONBytes;

		return qc_SummaryCounts_Method_Response;
	}
	

	/**
	 * @param chartJSONAsBytes
	 * @param search
	 * @param requestQueryString
	 * @throws Exception
	 */
	private void cacheResult( 
			byte[] chartJSONAsBytes, 
			SearchDTO search, 
			String requestQueryString ) throws Exception {

		QC_SummaryCounts_CachedResultManager.getSingletonInstance()
		.saveDataToCache( search.getProjectSearchId(), chartJSONAsBytes, requestQueryString );
	}

	/**
	 * @param resultsObject
	 * @param searchId
	 * @return
	 * @throws IOException
	 */
	private byte[] getResultsByteArray( QC_SummaryCountsResults resultsObject, int searchId ) throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream( );

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();
		//   serialize 
		try {
			jacksonJSON_Mapper.writeValue( baos, resultsObject );
		} catch ( JsonParseException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonParseException.  "
					+ ". searchId: " + searchId;
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonMappingException.  "
					+ ". searchId: " + searchId;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to serialize 'resultsObject', IOException.  "
					+ ". searchId: " + searchId;
			log.error( msg, e );
			throw e;
		}
		
		return baos.toByteArray();
	}

	/**
	 * @param searches
	 * @param mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder
	 * @return
	 * @throws Exception
	 */
	private byte[] retrieveDataFromCacheAndMatchCutoffs( SearchDTO search,
			MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder,
			String requestQueryString )
			throws Exception {

		QC_SummaryCounts_CachedResultManager_Result cachedDataResult =
				QC_SummaryCounts_CachedResultManager.getSingletonInstance()
				.retrieveDataFromCache( search.getProjectSearchId(), requestQueryString );

		if ( cachedDataResult == null ) {
			//  No Cached results so return null
			return null;  //  EARLY RETURN
		}
		
		byte[] chartJSONAsBytes = cachedDataResult.getChartJSONAsBytes();
		return chartJSONAsBytes;
	}

	/**
	 * @param search
	 * @param mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder
	 * @return
	 * @throws Exception
	 */
	private boolean get_searchOnlyHasDefaultCutoffs( SearchDTO search,
			MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder)
			throws Exception {
		

		boolean searchOnlyHasDefaultCutoffs = false;

		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = mergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder.searcherCutoffValuesRootLevel;

		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( search.getProjectSearchId() );
		DefaultCutoffsExactlyMatchAnnTypeDataToSearchDataResult result =
				DefaultCutoffsExactlyMatchAnnTypeDataToSearchData.getInstance()
				.defaultCutoffsExactlyMatchAnnTypeDataToSearchData( search.getSearchId(), searcherCutoffValuesSearchLevel );
		if ( result.isDefaultCutoffsExactlyMatchAnnTypeDataToSearchData() ) {
			searchOnlyHasDefaultCutoffs = true;
		}
		
		return searchOnlyHasDefaultCutoffs;
	}
	
	/**
	 * 
	 *
	 */
	private static class MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder {
		MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot;
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel;
	}
	
	/**
	 * @param filterCriteriaJSON
	 * @param searchIds
	 * @return
	 * @throws Exception
	 */
	private MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder 
		getMergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder( String filterCriteriaJSON, Collection<Integer> searchIds ) throws Exception {

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		//   deserialize 
		MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot = null;
		try {
			mergedPeptideQueryJSONRoot = jacksonJSON_Mapper.readValue( filterCriteriaJSON, MergedPeptideQueryJSONRoot.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'filterCriteriaJSON', JsonParseException.  filterCriteriaJSON: " + filterCriteriaJSON;
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'filterCriteriaJSON', JsonMappingException.  filterCriteriaJSON: " + filterCriteriaJSON;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to parse 'filterCriteriaJSON', IOException.  filterCriteriaJSON: " + filterCriteriaJSON;
			log.error( msg, e );
			throw e;
		}

		////////////
		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = mergedPeptideQueryJSONRoot.getCutoffs();
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory
				.createSearcherCutoffValuesRootLevel( searchIds, cutoffValuesRootLevel );
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
				cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		
		MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder holder = new MergedPeptideQueryJSONRoot_SearcherCutoffValuesRootLevel_Holder();
		holder.mergedPeptideQueryJSONRoot = mergedPeptideQueryJSONRoot;
		holder.searcherCutoffValuesRootLevel = searcherCutoffValuesRootLevel;
		
		return holder;
	}
	
	/**
	 * @param linkType
	 * @param resultsPerLinkTypeList
	 * @param perLinkTypeTempData_ByLinkType
	 */
	private void createReultObjectPerLinkTypeAndAddToOutputListForLinkType( 
			String linkType, 
			List<QC_SummaryCountsResultsPerLinkType> resultsPerLinkTypeList , 
			Map<String,PerLinkTypeTempData> perLinkTypeTempData_ByLinkType ) {
		
		PerLinkTypeTempData perLinkTypeTempData = perLinkTypeTempData_ByLinkType.get( linkType );
		if ( perLinkTypeTempData != null ) {
			QC_SummaryCountsResultsPerLinkType qc_SummaryCountsResultsPerLinkType = new QC_SummaryCountsResultsPerLinkType();
			qc_SummaryCountsResultsPerLinkType.setLinkType( linkType );
			qc_SummaryCountsResultsPerLinkType.setPsmCount( perLinkTypeTempData.psmCount );
			qc_SummaryCountsResultsPerLinkType.setUniqueReportedPeptideCount( perLinkTypeTempData.reportedPeptideIds.size() );
			qc_SummaryCountsResultsPerLinkType.setUniqueproteinSequenceVersionIdCount( perLinkTypeTempData.proteinSequenceVersionIds.size() );
			resultsPerLinkTypeList.add( qc_SummaryCountsResultsPerLinkType );
		}
	}
	
	private class PerLinkTypeTempData {
		
		int psmCount = 0;
		Set<Integer> reportedPeptideIds = new HashSet<>();
		Set<Integer> proteinSequenceVersionIds = new HashSet<>();
	}
	
}