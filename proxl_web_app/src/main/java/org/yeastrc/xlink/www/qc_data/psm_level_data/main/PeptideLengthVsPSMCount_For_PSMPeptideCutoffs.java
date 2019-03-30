package org.yeastrc.xlink.www.qc_data.psm_level_data.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults.PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsChartBucket;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults.PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId;
import org.yeastrc.xlink.www.searcher_via_cached_data.request_objects_for_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Get Peptide Length Vs PSM Count where associated Reported Peptides meet criteria, including Reported Peptide and PSM cutoffs
 *
 */
public class PeptideLengthVsPSMCount_For_PSMPeptideCutoffs {

	private static final Logger log = LoggerFactory.getLogger( PeptideLengthVsPSMCount_For_PSMPeptideCutoffs.class);

	public enum ForDownload { YES, NO }
	
	/**
	 * private constructor
	 */
	private PeptideLengthVsPSMCount_For_PSMPeptideCutoffs(){}
	public static PeptideLengthVsPSMCount_For_PSMPeptideCutoffs getInstance( ) throws Exception {
		PeptideLengthVsPSMCount_For_PSMPeptideCutoffs instance = new PeptideLengthVsPSMCount_For_PSMPeptideCutoffs();
		return instance;
	}


	/**
	 * Response from call to getPeptideLengthVsPSMCount_For_PSMPeptideCutoffs(...)
	 *
	 */
	public static class PeptideLengthVsPSMCount_For_PSMPeptideCutoffs_Method_Response {
		
		PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults;
		
		Map<String, Map<Integer,MutableInt>> psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType;

		public PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults getPeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults() {
			return peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults;
		}

		public void setPeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults(
				PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults) {
			this.peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults = peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults;
		}

		public Map<String, Map<Integer, MutableInt>> getPsmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType() {
			return psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType;
		}

		public void setPsmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType(
				Map<String, Map<Integer, MutableInt>> psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType) {
			this.psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType = psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType;
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
	public PeptideLengthVsPSMCount_For_PSMPeptideCutoffs_Method_Response getPeptideLengthVsPSMCount_For_PSMPeptideCutoffs( 			
			ForDownload forDownload,
			String filterCriteriaJSON, 
			SearchDTO search ) throws Exception {

		Collection<Integer> searchIds = new HashSet<>();
		
		Set<String> selectedLinkTypes = new HashSet<>();
		
		searchIds.add( search.getSearchId() );
		
//		List<SearchDTO> searches = new ArrayList<>( 1 );
//		searches.add( search );

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

		//  Populate countForLinkType_ByLinkType for selected link types
		if ( mergedPeptideQueryJSONRoot.getLinkTypes() == null || mergedPeptideQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		} 
		String[] linkTypesFromURL = mergedPeptideQueryJSONRoot.getLinkTypes();
		
		//  Create link types in lower case for display and upper case for being like the selection from web page if came from other place
		List<String> linkTypesList = new ArrayList<String>( linkTypesFromURL.length );
		{
			String[] linkTypesFromURLUpdated = new String[ linkTypesFromURL.length ];
			int linkTypesFromURLUpdatedIndex = 0;

			for ( String linkTypeFromWeb : linkTypesFromURL ) {
				String linkTypeRequestUpdated = null;
				String linkTypeDisplay = null;
				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkTypeFromWeb ) || XLinkUtils.CROSS_TYPE_STRING.equals( linkTypeFromWeb ) ) {
					linkTypeRequestUpdated = PeptideViewLinkTypesConstants.CROSSLINK_PSM;
					linkTypeDisplay = XLinkUtils.CROSS_TYPE_STRING;
				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkTypeFromWeb ) || XLinkUtils.LOOP_TYPE_STRING.equals( linkTypeFromWeb ) ) {
					linkTypeRequestUpdated = PeptideViewLinkTypesConstants.LOOPLINK_PSM;
					linkTypeDisplay = XLinkUtils.LOOP_TYPE_STRING;
				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkTypeFromWeb ) || XLinkUtils.UNLINKED_TYPE_STRING.equals( linkTypeFromWeb ) ) {
					linkTypeRequestUpdated = PeptideViewLinkTypesConstants.UNLINKED_PSM;
					linkTypeDisplay = XLinkUtils.UNLINKED_TYPE_STRING;
				} else {
					String msg = "linkType is invalid, linkTypeFromWeb: " + linkTypeFromWeb;
					log.error( msg );
					throw new Exception( msg );
				}
				linkTypesList.add( linkTypeDisplay );
				linkTypesFromURLUpdated[ linkTypesFromURLUpdatedIndex ] = linkTypeRequestUpdated;
				linkTypesFromURLUpdatedIndex++;
			}
			linkTypesFromURL = linkTypesFromURLUpdated;
			mergedPeptideQueryJSONRoot.setLinkTypes( linkTypesFromURLUpdated );
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

		//  Get cutoffs for this project search id
		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
				searcherCutoffValuesRootLevel.getPerSearchCutoffs( search.getProjectSearchId() );
		if ( searcherCutoffValuesSearchLevel == null ) {
			String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + search.getProjectSearchId();
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		
		//  Populate countForLinkType_ByLinkType for selected link types
		if ( mergedPeptideQueryJSONRoot.getLinkTypes() == null || mergedPeptideQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		} else {
			for ( String linkType : mergedPeptideQueryJSONRoot.getLinkTypes() ) {
				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkType ) ) {
					selectedLinkTypes.add( XLinkUtils.CROSS_TYPE_STRING );
				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkType ) ) {
					selectedLinkTypes.add( XLinkUtils.LOOP_TYPE_STRING );
				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkType ) ) {
					selectedLinkTypes.add( XLinkUtils.UNLINKED_TYPE_STRING );
				} else {
					String msg = "linkType is invalid, linkType: " + linkType;
					log.error( msg );
					throw new Exception( msg );
				}
			}
		}
		
		PeptideLengthVsPSMCount_For_PSMPeptideCutoffs_Method_Response methodResponse = new PeptideLengthVsPSMCount_For_PSMPeptideCutoffs_Method_Response();
		
		//  Get peptideLength and PSM counts mapped by link type
		Map<String, Map<Integer,MutableInt>> psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType = 
				getPeptideLengths( linkTypesForDBQuery, modsForDBQuery, search, searcherCutoffValuesSearchLevel );
		
		methodResponse.psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType = psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType;
		
		if ( forDownload == ForDownload.YES ) {
			return methodResponse;  //  EARY RETURN
		}
					
		Map<String, PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType> peptideLengthInChartBuckets_KeyedOnLinkType = 
				getPeptideLengthInChartBuckets_KeyedOnLinkType( psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType );
		
		PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults peptideLength_Histogram_For_PSMPeptideCutoffsResults = new PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResults();
		
		//  copy map to array for output, in a specific order
		List<PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType> resultsPerLinkTypeList = new ArrayList<>( 3 );
		
		addToOutputListForLinkType( XLinkUtils.CROSS_TYPE_STRING, selectedLinkTypes, resultsPerLinkTypeList, peptideLengthInChartBuckets_KeyedOnLinkType );
		addToOutputListForLinkType( XLinkUtils.LOOP_TYPE_STRING, selectedLinkTypes, resultsPerLinkTypeList, peptideLengthInChartBuckets_KeyedOnLinkType );
		addToOutputListForLinkType( XLinkUtils.UNLINKED_TYPE_STRING, selectedLinkTypes, resultsPerLinkTypeList, peptideLengthInChartBuckets_KeyedOnLinkType );
		
		peptideLength_Histogram_For_PSMPeptideCutoffsResults.setDataForChartPerLinkTypeList( resultsPerLinkTypeList );
		
		methodResponse.peptideLengthVsPSMCount_For_PSMPeptideCutoffsResults = peptideLength_Histogram_For_PSMPeptideCutoffsResults;
		
		return methodResponse;
	}
	

	/**
	 * @param linkType
	 * @param countForLinkTypeList
	 * @param countForLinkType_ByLinkType
	 */
	private void addToOutputListForLinkType( 
			String linkType, 
			Set<String> selectedLinkTypes, 
			List<PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType> resultsPerLinkTypeList,
			Map<String, PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType> peptideLengthInChartBuckets_KeyedOnLinkType ) {
		
		if ( ! selectedLinkTypes.contains( linkType ) ) {
			//  link type not selected
			return; // EARLY EXIT
		}
		
		PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType valueForLinkType = 
				peptideLengthInChartBuckets_KeyedOnLinkType.get( linkType );
		
		if ( valueForLinkType == null ) {
			//  No data for link type so create empty entry
			valueForLinkType = new PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType();
			valueForLinkType.setLinkType( linkType );
			resultsPerLinkTypeList.add( valueForLinkType );
			return; // EARLY EXIT
		}
		
		resultsPerLinkTypeList.add( valueForLinkType );
	}
	
	/**
	 * @param psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType
	 * @return
	 */
	private Map<String, PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType> getPeptideLengthInChartBuckets_KeyedOnLinkType( 
			Map<String, Map<Integer,MutableInt>> psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType ) {
		
		Map<String, PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType> peptideLengthInChartBuckets_KeyedOnLinkType = new HashMap<>();
		
		for ( Map.Entry<String, Map<Integer,MutableInt>> psmCount_Map_KeyedOnPeptideLength_Map_Entry : psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType.entrySet() ) {
			Map<Integer,MutableInt> psmCount_Map_KeyedOnPpeptideLength = psmCount_Map_KeyedOnPeptideLength_Map_Entry.getValue();
			
			//  Find max and min values
			int peptideLengthMin = 0;
			int peptideLengthMax =  0;
			boolean firstOverallpeptideLengthEntry = true;
			for ( Map.Entry<Integer,MutableInt>  psmCount_Map_KeyedOnPpeptideLength_Entry : psmCount_Map_KeyedOnPpeptideLength.entrySet() ) {
				int peptideLength = psmCount_Map_KeyedOnPpeptideLength_Entry.getKey();
				if ( firstOverallpeptideLengthEntry  ) {
					firstOverallpeptideLengthEntry = false;
					peptideLengthMin = peptideLength;
					peptideLengthMax = peptideLength;
				} else {
					if ( peptideLength < peptideLengthMin ) {
						peptideLengthMin = peptideLength;
					}
					if ( peptideLength >peptideLengthMax ) {
						peptideLengthMax = peptideLength;
					}
				}
			}
			
			PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType peptideLength_HistogramData =
					getpeptideLength_HistogramData( psmCount_Map_KeyedOnPpeptideLength, peptideLengthMin, peptideLengthMax );
			peptideLength_HistogramData.setLinkType( psmCount_Map_KeyedOnPeptideLength_Map_Entry.getKey() );
			peptideLength_HistogramData.setPeptideLengthMin( peptideLengthMin );
			peptideLength_HistogramData.setPeptideLengthMax( peptideLengthMax );
			
			peptideLengthInChartBuckets_KeyedOnLinkType.put( psmCount_Map_KeyedOnPeptideLength_Map_Entry.getKey(), peptideLength_HistogramData );
		}
		
		
		return peptideLengthInChartBuckets_KeyedOnLinkType;
	}

	/**
	 * @param peptideLengthList
	 * @return
	 */
	private PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType getpeptideLength_HistogramData( 
			Map<Integer,MutableInt> psmCount_Map_KeyedOnPpeptideLength,
			int peptideLengthMin, 
			int peptideLengthMax ) {
		
		//  Whole number of peptide lengths in each bin.
		
		int peptideLengthRange = peptideLengthMax - peptideLengthMin + 1;
		
		//  Process data into bins
		int binCount = peptideLengthRange;
		
//		int binCount = (int) ( Math.sqrt( peptideLengthList.size() ) );
		int[] peptideLengthCounts = new int[ binCount ];
		
		for ( Map.Entry<Integer,MutableInt>  psmCount_Map_KeyedOnPpeptideLength_Entry : psmCount_Map_KeyedOnPpeptideLength.entrySet() ) {
			int peptideLength = psmCount_Map_KeyedOnPpeptideLength_Entry.getKey();
			int bin  = peptideLength - peptideLengthMin;
			if ( bin < 0 ) {
				bin = 0;
			} else if ( bin >= binCount ) {
				bin = binCount - 1;
			} 
			peptideLengthCounts[ bin ] = psmCount_Map_KeyedOnPpeptideLength_Entry.getValue().intValue();
		}
		
		List<PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets = new ArrayList<>( binCount );
		//  Take the data in the bins and  create "buckets" in the format required for the charting API
		for ( int binIndex = 0; binIndex < peptideLengthCounts.length; binIndex++ ) {
			PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsChartBucket chartBucket = new PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsChartBucket();
			chartBuckets.add( chartBucket );
			int psmCount = peptideLengthCounts[ binIndex ];
			int peptideLength = binIndex + peptideLengthMin;
			chartBucket.setPeptideLength( peptideLength );
			chartBucket.setPsmCount( psmCount );
		}
		
		PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType result = new PeptideLengthVsPSMCount_For_PSMPeptideCutoffsResultsForLinkType();
		
		result.setChartBuckets( chartBuckets );
		result.setPeptideLengthMax( peptideLengthMax );
		result.setPeptideLengthMin( peptideLengthMin );
		
		return result;
	}

	
	/**
	 * @param linkTypesForDBQuery
	 * @param modsForDBQuery
	 * @param peptideDTO_MappedById
	 * @param searchDTO
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 * @throws ProxlWebappDataException
	 */
	public Map<String, Map<Integer,MutableInt>> getPeptideLengths(
			String[] linkTypesForDBQuery, 
			String[] modsForDBQuery,
			SearchDTO searchDTO, 
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel ) throws Exception, ProxlWebappDataException {
		
		Map<String, Map<Integer,MutableInt>> psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType = new HashMap<>();
		
		//  Cache peptideDTO ById locally
		Map<Integer,PeptideDTO> peptideDTO_MappedById = new HashMap<>();
		
		///////////////////////////////////////////////
		//  Get peptides for this search from the DATABASE
		List<WebReportedPeptideWrapper> wrappedLinksPerForSearch =
				PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
						searchDTO, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery, 
						PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );

		for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedLinksPerForSearch ) {
			WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
			int reportedPeptideId = webReportedPeptide.getReportedPeptideId();
			
			String linkType = null;
			
			//  srchRepPeptPeptideDTOList: associated SrchRepPeptPeptideDTO for the link, one per associated peptide, populated per link type
			
			//  copied from SearchPeptideCrosslink, this way not load PeptideDTO in SearchPeptideCrosslink
			SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams = new SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams();
			srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setSearchId( searchDTO.getSearchId() );
			srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams.setReportedPeptideId( reportedPeptideId );
			SrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result =
					Cached_SrchRepPeptPeptideDTO_ForSrchIdRepPeptId.getInstance()
					.getSrchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result( srchRepPeptPeptideDTO_ForSrchIdRepPeptId_ReqParams );
			List<SrchRepPeptPeptideDTO> srchRepPeptPeptideDTOList = srchRepPeptPeptideDTO_ForSrchIdRepPeptId_Result.getSrchRepPeptPeptideDTOList();

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
						+ ", searchId: " + searchDTO.getSearchId();
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			
			// get object from map for link type
			Map<Integer,MutableInt> psmCount_Map_KeyedOnPpeptideLength_Map_ForLinkType = psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType.get( linkType );
			if ( psmCount_Map_KeyedOnPpeptideLength_Map_ForLinkType == null ) {
				psmCount_Map_KeyedOnPpeptideLength_Map_ForLinkType = new HashMap<>();
				psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType.put( linkType, psmCount_Map_KeyedOnPpeptideLength_Map_ForLinkType );
			}
			
			int peptideLength = 0; //  peptide length is sum of peptides for reproted peptide

			//  process srchRepPeptPeptideDTOList (Each peptide mapped to the reported peptide)
			for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
				// get PeptideDTO, caching locally in peptideDTO_MappedById
				PeptideDTO peptide = null;
				// get PeptideDTO, caching locally in peptideDTO_MappedById
				if ( peptideDTO_MappedById != null ) {
					peptide = peptideDTO_MappedById.get( srchRepPeptPeptideDTO.getPeptideId() );
				}
				if ( peptide == null ) {
					peptide = PeptideDAO.getInstance().getPeptideDTOFromDatabase( srchRepPeptPeptideDTO.getPeptideId() );
					//  To directly retrieve from DB:  PeptideDAO.getInstance().getPeptideDTOFromDatabaseActual( id )
					if ( peptide == null ) {
						String msg = 
								"PeptideDTO not found in DB for id: " + srchRepPeptPeptideDTO.getPeptideId()
								+ ", for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
								+ ", for reportedPeptideId: " + reportedPeptideId
								+ ", searchId: " + searchDTO.getSearchId();
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					peptideDTO_MappedById.put( srchRepPeptPeptideDTO.getPeptideId(), peptide );
				}
				//  peptide length is sum of peptides for reproted peptide
				peptideLength += peptide.getSequence().length();
			}
			
			Integer peptideLengthObj = peptideLength;
			int numPsms = webReportedPeptide.getNumPsms();
			
			MutableInt psmCount = psmCount_Map_KeyedOnPpeptideLength_Map_ForLinkType.get( peptideLengthObj );
			if ( psmCount == null ) {
				psmCount_Map_KeyedOnPpeptideLength_Map_ForLinkType.put( peptideLengthObj, new MutableInt( numPsms ) );
			} else {
				psmCount.add( numPsms );
			}
			
		}
		
		return psmCount_Map_KeyedOnPpeptideLength_Map_KeyedOnLinkType;
	}


}
