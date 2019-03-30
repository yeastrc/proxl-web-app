package org.yeastrc.xlink.www.qc_data.summary_statistics_merged.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.WebProteinPosition;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.summary_statistics_merged.main.QC_SummaryCounts_Merged_CachedResultManager.QC_SummaryCounts_Merged_CachedResultManager_Result;
import org.yeastrc.xlink.www.qc_data.summary_statistics_merged.objects.QC_SummaryCounts_Merged_Results;
import org.yeastrc.xlink.www.qc_data.summary_statistics_merged.objects.QC_SummaryCounts_Merged_Results.QC_SummaryCountsResultsPerLinkType_Merged;
import org.yeastrc.xlink.www.qc_data.summary_statistics_merged.objects.QC_SummaryCounts_Merged_Results.QC_SummaryCountsResults_PerSearchId_Merged;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class QC_SummaryCounts_Merged {

	private static final Logger log = LoggerFactory.getLogger( QC_SummaryCounts_Merged.class);

	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 */
	static final int VERSION_FOR_CACHING = 1;
	
	
	public enum ForDownload { YES, NO }
	
	
	/**
	 * private constructor
	 */
	private QC_SummaryCounts_Merged(){}
	public static QC_SummaryCounts_Merged getInstance( ) throws Exception {
		QC_SummaryCounts_Merged instance = new QC_SummaryCounts_Merged();
		return instance;
	}
	
	enum AddCombinedEntry { YES, NO }

	/**
	 * Response from call to getQC_SummaryCounts(...)
	 *
	 */
	public static class QC_SummaryCounts_Merged_Method_Response {

		private byte[] resultsAsBytes; //  summaryCounts_Merged_Results as JSON
		private QC_SummaryCounts_Merged_Results summaryCounts_Merged_Results;

		public byte[] getResultsAsBytes() {
			return resultsAsBytes;
		}
		public void setResultsAsBytes(byte[] resultsAsBytes) {
			this.resultsAsBytes = resultsAsBytes;
		}
		public QC_SummaryCounts_Merged_Results getSummaryCounts_Merged_Results() {
			return summaryCounts_Merged_Results;
		}
		public void setSummaryCounts_Merged_Results(QC_SummaryCounts_Merged_Results summaryCounts_Merged_Results) {
			this.summaryCounts_Merged_Results = summaryCounts_Merged_Results;
		}

	}
	
	/**
	 * @param forDownload
	 * @param postBody
	 * @param requestQueryString
	 * @param searches
	 * @return
	 * @throws Exception
	 */
	public QC_SummaryCounts_Merged_Method_Response getQC_SummaryCounts_Merged( 
			ForDownload forDownload,
			String filterCriteriaJSON,
			String requestQueryString,
			List<SearchDTO> searches ) throws Exception {


		String cacheKey = null;
		
		Collection<Integer> searchIds = new HashSet<>();
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
			searchIdsListDeduppedSorted.add( search.getSearchId() );
			mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
		}
		
		if ( forDownload != ForDownload.YES ) {
			
			//  Only if not download, get from Cache on disk
			
			cacheKey = requestQueryString + filterCriteriaJSON;
			
			QC_SummaryCounts_Merged_CachedResultManager_Result qc_SummaryCounts_Merged_CachedResultManager_Result =
					QC_SummaryCounts_Merged_CachedResultManager.getSingletonInstance()
					.retrieveDataFromCache(searchIdsListDeduppedSorted, cacheKey );
			if ( qc_SummaryCounts_Merged_CachedResultManager_Result != null ) {
				byte[] chartJSONAsBytes = qc_SummaryCounts_Merged_CachedResultManager_Result.getChartJSONAsBytes();
				if ( chartJSONAsBytes != null ) {
					
					//  Data in Cache on disk so return it
					
					QC_SummaryCounts_Merged_Method_Response response = new QC_SummaryCounts_Merged_Method_Response();
					response.resultsAsBytes = chartJSONAsBytes;
					
					return response;  // EARLY EXIT
				}
			}
		}

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		//   deserialize 
		MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot = null;
		try {
			mergedPeptideQueryJSONRoot = jacksonJSON_Mapper.readValue( filterCriteriaJSON, MergedPeptideQueryJSONRoot.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'postBody', JsonParseException. filterCriteriaJSON: " + filterCriteriaJSON; 
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'postBody', JsonMappingException. filterCriteriaJSON: " + filterCriteriaJSON;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to parse 'postBody', IOException. filterCriteriaJSON: " + filterCriteriaJSON;
			log.error( msg, e );
			throw e;
		}

		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( mergedPeptideQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = mergedPeptideQueryJSONRoot.getMods();
		////////////
		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = mergedPeptideQueryJSONRoot.getCutoffs();
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory
				.createSearcherCutoffValuesRootLevel( searchIds, cutoffValuesRootLevel );
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
				cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		
		//  Populate countForLinkType_ByLinkType for selected link types
		if ( mergedPeptideQueryJSONRoot.getLinkTypes() == null || mergedPeptideQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		}
		
		Map<Integer, PerSearchIdTempData> perSearchIdTempData_BySearchId = new HashMap<>();
		
		List<String> linkTypesList = new ArrayList<String>( mergedPeptideQueryJSONRoot.getLinkTypes().length );

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
			linkTypesList.add( linkType );
		}
		
		boolean foundData = false;
		
		
		for ( SearchDTO searchDTO : searches ) {
			Integer projectSearchId = searchDTO.getProjectSearchId();
			Integer searchId = searchDTO.getSearchId();
			
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
							searchDTO, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery, 
							PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );
			
			if ( ! wrappedLinksPerForSearch.isEmpty() ) {
				foundData = true;
			}
			
			Map<String,PerLinkTypeTempData> perLinkTypeTempData_ByLinkType = new HashMap<>();
			
			for ( String linkType : linkTypesList ) {
				PerLinkTypeTempData perLinkTypeTempData = new PerLinkTypeTempData();
				perLinkTypeTempData_ByLinkType.put( linkType, perLinkTypeTempData );
			}
			
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

			//  combine all perLinkTypeTempData.proteinSequenceVersionIds together for combinedCountsPerSearchEntry
			PerLinkTypeTempData combinedCountsPerSearchEntry = new PerLinkTypeTempData();
			
			for ( Map.Entry<String,PerLinkTypeTempData> entry : perLinkTypeTempData_ByLinkType.entrySet() ) {
				PerLinkTypeTempData perLinkTypeTempData = entry.getValue();
				combinedCountsPerSearchEntry.psmCount += perLinkTypeTempData.psmCount;
				combinedCountsPerSearchEntry.reportedPeptideIds.addAll( perLinkTypeTempData.reportedPeptideIds );
				combinedCountsPerSearchEntry.proteinSequenceVersionIds.addAll( perLinkTypeTempData.proteinSequenceVersionIds );
			}

			PerSearchIdTempData perSearchIdTempData = new PerSearchIdTempData();
			perSearchIdTempData.perLinkTypeTempData_ByLinkType = perLinkTypeTempData_ByLinkType;
			perSearchIdTempData.combinedCountsPerSearchEntry = combinedCountsPerSearchEntry;
			
			perSearchIdTempData_BySearchId.put(searchId, perSearchIdTempData );
		}
		
		
		List<String> linkTypesDisplayOrderList = new ArrayList<String>( linkTypesList.size() );
		
		if ( linkTypesList.contains( XLinkUtils.CROSS_TYPE_STRING ) ) {
			linkTypesDisplayOrderList.add( XLinkUtils.CROSS_TYPE_STRING );
		}
		if ( linkTypesList.contains( XLinkUtils.LOOP_TYPE_STRING ) ) {
			linkTypesDisplayOrderList.add( XLinkUtils.LOOP_TYPE_STRING );
		}
		if ( linkTypesList.contains( XLinkUtils.UNLINKED_TYPE_STRING ) ) {
			linkTypesDisplayOrderList.add( XLinkUtils.UNLINKED_TYPE_STRING );
		}
		

		QC_SummaryCounts_Merged_Results qc_SummaryCounts_Merged_Results = createResult( searches, perSearchIdTempData_BySearchId, linkTypesDisplayOrderList );
		qc_SummaryCounts_Merged_Results.setSearchIds( searchIdsListDeduppedSorted );
		qc_SummaryCounts_Merged_Results.setFoundData( foundData );
		
		QC_SummaryCounts_Merged_Method_Response response = new QC_SummaryCounts_Merged_Method_Response();
		response.summaryCounts_Merged_Results = qc_SummaryCounts_Merged_Results;
		
		if ( forDownload != ForDownload.YES && cacheKey != null ) {
			//  Not for download so create the JSON and cache it to disk
			response.resultsAsBytes = getResultsByteArray( qc_SummaryCounts_Merged_Results );
			
			QC_SummaryCounts_Merged_CachedResultManager.getSingletonInstance()
			.saveDataToCache( searchIdsListDeduppedSorted, response.resultsAsBytes, cacheKey );
		}
		
		return response;
	}

	/**
	 * @param resultsObject
	 * @param searchId
	 * @return
	 * @throws IOException
	 */
	private byte[] getResultsByteArray( QC_SummaryCounts_Merged_Results resultsObject ) throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream( );

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();
		//   serialize 
		try {
			jacksonJSON_Mapper.writeValue( baos, resultsObject );
		} catch ( JsonParseException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonParseException.  " ;
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonMappingException.  " ;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to serialize 'resultsObject', IOException. " ;
			log.error( msg, e );
			throw e;
		}
		
		return baos.toByteArray();
	}

	
	/**
	 * @param searches
	 * @param perSearchIdTempData_BySearchId
	 * @param linkTypesDisplayOrderList
	 * @return
	 * @throws ProxlWebappInternalErrorException
	 */
	private QC_SummaryCounts_Merged_Results createResult( 
			List<SearchDTO> searches, 
			Map<Integer, PerSearchIdTempData> perSearchIdTempData_BySearchId,
			List<String> linkTypesDisplayOrderList ) throws ProxlWebappInternalErrorException {
		
		List<QC_SummaryCountsResultsPerLinkType_Merged> psmCountPerLinkTypeList = new ArrayList<>( linkTypesDisplayOrderList.size() + 2 );
		List<QC_SummaryCountsResultsPerLinkType_Merged> reportedPeptideCountPerLinkTypeList = new ArrayList<>( linkTypesDisplayOrderList.size() + 2 );
		List<QC_SummaryCountsResultsPerLinkType_Merged> proteinCountPerLinkTypeList = new ArrayList<>( linkTypesDisplayOrderList.size() + 2 );

		//  index of Combined entry, added after entries per link type 
		int combinedEntryIndex = linkTypesDisplayOrderList.size();
		
		// method also adds combined entry at end
		populateCountPerLinkTypeList( linkTypesDisplayOrderList, psmCountPerLinkTypeList, searches, AddCombinedEntry.NO );
		populateCountPerLinkTypeList( linkTypesDisplayOrderList, reportedPeptideCountPerLinkTypeList, searches, AddCombinedEntry.NO );
		populateCountPerLinkTypeList( linkTypesDisplayOrderList, proteinCountPerLinkTypeList, searches, AddCombinedEntry.YES );

		for ( SearchDTO searchDTO : searches ) {
			Integer searchId = searchDTO.getSearchId();
			Integer projectSearchId = searchDTO.getProjectSearchId();
			
			PerSearchIdTempData perSearchIdTempData = perSearchIdTempData_BySearchId.get( searchId );
			if ( perSearchIdTempData == null ) {
				String msg = "Internal error, search id not found in perSearchIdTempData_BySearchId. searchId: " + searchId;
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}

			//  Process per link type
			Map<String,PerLinkTypeTempData> perLinkTypeTempData_ByLinkType = perSearchIdTempData.perLinkTypeTempData_ByLinkType;
			
			for ( int linkTypeIndex = 0; linkTypeIndex < linkTypesDisplayOrderList.size(); linkTypeIndex++ ) {
				String linkType = linkTypesDisplayOrderList.get( linkTypeIndex );
				PerLinkTypeTempData perLinkTypeTempData = perLinkTypeTempData_ByLinkType.get( linkType );
				if ( perLinkTypeTempData == null ) {
					String msg = "Internal error, linkType not found in perLinkTypeTempData_ByLinkType. linkType: " + linkType;
					log.error( msg );
					throw new ProxlWebappInternalErrorException(msg);
				}
				 // PSM Count entry add
				addPerSearchIdEntryForLinkType( psmCountPerLinkTypeList, searchId, projectSearchId, linkTypeIndex, perLinkTypeTempData.psmCount );
				// Reported Peptide Count entry add
				addPerSearchIdEntryForLinkType( reportedPeptideCountPerLinkTypeList, searchId, projectSearchId, linkTypeIndex, perLinkTypeTempData.reportedPeptideIds.size() );
				// Protein Count entry add
				addPerSearchIdEntryForLinkType( proteinCountPerLinkTypeList, searchId, projectSearchId, linkTypeIndex, perLinkTypeTempData.proteinSequenceVersionIds.size() );
			}
			
			//  Process Combined entry across link types
			PerLinkTypeTempData combinedCountsPerSearchEntry = perSearchIdTempData.combinedCountsPerSearchEntry;
			 // PSM Count entry add
//			addPerSearchIdEntryForCombined( psmCountPerLinkTypeList, searchId, projectSearchId, combinedEntryIndex, combinedCountsPerSearchEntry.psmCount );
			// Reported Peptide Count entry add
//			addPerSearchIdEntryForCombined( reportedPeptideCountPerLinkTypeList, searchId, projectSearchId, combinedEntryIndex, combinedCountsPerSearchEntry.reportedPeptideIds.size() );
			// Protein Count entry add
			addPerSearchIdEntryForCombined( proteinCountPerLinkTypeList, searchId, projectSearchId, combinedEntryIndex, combinedCountsPerSearchEntry.proteinSequenceVersionIds.size() );
		}

		QC_SummaryCounts_Merged_Results result = new QC_SummaryCounts_Merged_Results();

		result.setPsmCountPerLinkTypeList( psmCountPerLinkTypeList );
		result.setReportedPeptideCountPerLinkTypeList( reportedPeptideCountPerLinkTypeList );
		result.setProteinCountPerLinkTypeList( proteinCountPerLinkTypeList );
		return result;
	}
	
	
	/**
	 * @param countPerLinkTypeList
	 * @param searchId
	 * @param linkTypeIndex
	 * @param count
	 */
	public void addPerSearchIdEntryForLinkType(
			List<QC_SummaryCountsResultsPerLinkType_Merged> countPerLinkTypeList, 
			Integer searchId,
			Integer projectSearchId,
			int linkTypeIndex, 
			int count) {
		
		QC_SummaryCountsResultsPerLinkType_Merged countPerLinkTypeEntry = countPerLinkTypeList.get( linkTypeIndex );
		QC_SummaryCountsResults_PerSearchId_Merged countsPerSearchId = new QC_SummaryCountsResults_PerSearchId_Merged();
		countsPerSearchId.setSearchId(searchId);
		countsPerSearchId.setCount( count );
		countPerLinkTypeEntry.getCountPerSearchIdMap_KeyProjectSearchId().put( projectSearchId, countsPerSearchId );
	}

	/**
	 * @param countPerLinkTypeList
	 * @param searchId
	 * @param combinedEntryIndex
	 * @param count
	 */
	public void addPerSearchIdEntryForCombined(
			List<QC_SummaryCountsResultsPerLinkType_Merged> countPerLinkTypeList, 
			Integer searchId,
			Integer projectSearchId,
			int combinedEntryIndex,
			int count) {

		QC_SummaryCountsResultsPerLinkType_Merged countPerLinkTypeEntry = countPerLinkTypeList.get( combinedEntryIndex );
		QC_SummaryCountsResults_PerSearchId_Merged countsPerSearchId = new QC_SummaryCountsResults_PerSearchId_Merged();
		countsPerSearchId.setSearchId(searchId);
		countsPerSearchId.setCount( count );
		countPerLinkTypeEntry.getCountPerSearchIdMap_KeyProjectSearchId().put( projectSearchId, countsPerSearchId );
	}


	/**
	 * method also adds combined entry at end
	 * 
	 * @param linkTypesDisplayOrderList
	 * @param countPerLinkTypeList
	 * @param searches
	 */
	private void populateCountPerLinkTypeList( 
			List<String> linkTypesDisplayOrderList, 
			List<QC_SummaryCountsResultsPerLinkType_Merged> countPerLinkTypeList, 
			List<SearchDTO> searches,
			AddCombinedEntry addCombinedEntry ) {
		
		for ( String linkType : linkTypesDisplayOrderList ) {
			QC_SummaryCountsResultsPerLinkType_Merged qc_SummaryCountsResultsPerLinkType_Merged = new QC_SummaryCountsResultsPerLinkType_Merged();
			qc_SummaryCountsResultsPerLinkType_Merged.setLinkType( linkType );
			qc_SummaryCountsResultsPerLinkType_Merged.setCountPerSearchIdMap_KeyProjectSearchId( new HashMap<>() );
			countPerLinkTypeList.add( qc_SummaryCountsResultsPerLinkType_Merged );
		}

		if ( addCombinedEntry == AddCombinedEntry.YES ) {
			//  Add Combined Entry
			QC_SummaryCountsResultsPerLinkType_Merged qc_SummaryCountsResultsPerLinkType_Merged = new QC_SummaryCountsResultsPerLinkType_Merged();
			qc_SummaryCountsResultsPerLinkType_Merged.setCombinedEntry( true );
			qc_SummaryCountsResultsPerLinkType_Merged.setCountPerSearchIdMap_KeyProjectSearchId( new HashMap<>() );
			countPerLinkTypeList.add( qc_SummaryCountsResultsPerLinkType_Merged );
		}
	}
	

	/**
	 * 
	 *
	 */
	private class PerSearchIdTempData {
		Map<String,PerLinkTypeTempData> perLinkTypeTempData_ByLinkType;
		PerLinkTypeTempData combinedCountsPerSearchEntry;
	}
	
	/**
	 * 
	 *
	 */
	private class PerLinkTypeTempData {

		int psmCount = 0;
		Set<Integer> reportedPeptideIds = new HashSet<>();
		Set<Integer> proteinSequenceVersionIds = new HashSet<>();
	}


}
