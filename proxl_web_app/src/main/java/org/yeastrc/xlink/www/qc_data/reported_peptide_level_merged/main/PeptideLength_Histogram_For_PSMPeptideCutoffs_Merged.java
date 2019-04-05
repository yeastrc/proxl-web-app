package org.yeastrc.xlink.www.qc_data.reported_peptide_level_merged.main;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.QCPageQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.qc_data.a_enums.ForDownload_Enum;
import org.yeastrc.xlink.www.qc_data.reported_peptide_level.main.PeptideLength_Histogram_For_PSMPeptideCutoffs;
import org.yeastrc.xlink.www.qc_data.reported_peptide_level_merged.objects.PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results;
import org.yeastrc.xlink.www.qc_data.reported_peptide_level_merged.objects.PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results.PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType;
import org.yeastrc.xlink.www.qc_data.reported_peptide_level_merged.objects.PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results.PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForSearchId;
import org.yeastrc.xlink.www.qc_data.utils.BoxPlotUtils;
import org.yeastrc.xlink.www.qc_data.utils.BoxPlotUtils.GetBoxPlotValuesResult;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

/**
 * 
 *
 */
public class PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged {

	private static final Logger log = LoggerFactory.getLogger( PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged.class);

	/**
	 * private constructor
	 */
	private PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged(){}
	public static PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged getInstance( ) throws Exception {
		PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged instance = new PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged();
		return instance;
	}
	

	/**
	 * Response from call to getPeptideLength_Histogram_For_PSMPeptideCutoffs_Merged(...)
	 *
	 */
	public static class PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Method_Response {

		PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results peptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results;
		
		/**
		 * Lists of peptideLength mapped by search id then link type
		 * Map<[link type], Map<[Search Id],List<[Peptide Length]>>>
		 */
		Map<String,Map<Integer,List<Integer>>> allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType;

		/**
		 * map of counts mapped by peptideLength mapped by search id then link type
		 * Map<[link type], Map<[Search Id],Map<[Peptide Length],[Count]>>>
		 */
		Map<String,Map<Integer,Map<Integer,MutableInt>>> countsKeyPeptideLength_KeySearchId_KeyLinkType;
		
		/**
		 * Lists of peptideLength mapped by search id then link type
		 * Map<[link type], Map<[Search Id],List<[Peptide Length]>>>
		 * @return
		 */
		public Map<String, Map<Integer, List<Integer>>> getAllSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType() {
			return allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType;
		}

		public void setAllSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType(
				Map<String, Map<Integer, List<Integer>>> allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType) {
			this.allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType = allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType;
		}

		public PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results getPeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results() {
			return peptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results;
		}

		public void setPeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results(
				PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results peptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results) {
			this.peptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results = peptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results;
		}

		/**
		 * map of counts mapped by peptideLength mapped by search id then link type
		 * Map<[link type], Map<[Search Id],Map<[Peptide Length],[Count]>>>
		 * @return
		 */
		public Map<String, Map<Integer, Map<Integer, MutableInt>>> getCountsKeyPeptideLength_KeySearchId_KeyLinkType() {
			return countsKeyPeptideLength_KeySearchId_KeyLinkType;
		}

		public void setCountsKeyPeptideLength_KeySearchId_KeyLinkType(
				Map<String, Map<Integer, Map<Integer, MutableInt>>> countsKeyPeptideLength_KeySearchId_KeyLinkType) {
			this.countsKeyPeptideLength_KeySearchId_KeyLinkType = countsKeyPeptideLength_KeySearchId_KeyLinkType;
		}
	}
		
	/**
	 * @param forDownload
	 * @param qcPageQueryJSONRoot
	 * @param searches
	 * @return
	 * @throws Exception
	 */
	public PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Method_Response getPeptideLength_Histogram_For_PSMPeptideCutoffs_Merged( 			
			ForDownload_Enum forDownload,
			QCPageQueryJSONRoot qcPageQueryJSONRoot, 
			List<SearchDTO> searches ) throws Exception {

		Collection<Integer> searchIds = new HashSet<>();
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
			searchIdsListDeduppedSorted.add( search.getSearchId() );
			mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
		}

		//  Populate countForLinkType_ByLinkType for selected link types
		if ( qcPageQueryJSONRoot.getLinkTypes() == null || qcPageQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		} 
		String[] linkTypesFromURL = qcPageQueryJSONRoot.getLinkTypes();
		
		if ( linkTypesFromURL == null || linkTypesFromURL.length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}
		
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
			qcPageQueryJSONRoot.setLinkTypes( linkTypesFromURLUpdated );
		}

		///////////////////////////////////////////////////
		//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
		String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( qcPageQueryJSONRoot.getLinkTypes() );
		//   Mods for DB Query
		String[] modsForDBQuery = qcPageQueryJSONRoot.getMods();
		////////////
		/////   Searcher cutoffs for all searches
		CutoffValuesRootLevel cutoffValuesRootLevel = qcPageQueryJSONRoot.getCutoffs();
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory
				.createSearcherCutoffValuesRootLevel( searchIds, cutoffValuesRootLevel );
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
				cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
				
		//  Get Lists of peptideLength mapped by search id then link type
		//  Map<[link type], Map<[Search Id],List<[Peptide Length]>>>
		Map<String,Map<Integer,List<Integer>>> allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType = 
				getAllSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType( searches, linkTypesForDBQuery, modsForDBQuery, searcherCutoffValuesRootLevel, qcPageQueryJSONRoot );
		
		PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Method_Response methodResponse = new PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Method_Response();
		
		methodResponse.allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType = allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType;
		
		if ( forDownload == ForDownload_Enum.YES ) {
			
			Map<String,Map<Integer,Map<Integer,MutableInt>>> countsKeyPeptideLength_KeySearchId_KeyLinkType = 
					remapForCountsPerPeptideLength( allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType );
			
			methodResponse.countsKeyPeptideLength_KeySearchId_KeyLinkType = countsKeyPeptideLength_KeySearchId_KeyLinkType;
			return methodResponse;  //  EARLY RETURN
		}
		
		PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results peptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results = 
				getPerChartData_KeyedOnLinkType( 
						allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType, 
						linkTypesList, 
						searches );

		methodResponse.peptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results = peptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results;
		
		return methodResponse;
	}
	

	/**
	 * @param allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType
	 * @return
	 */
	private Map<String,Map<Integer,Map<Integer,MutableInt>>> remapForCountsPerPeptideLength(
			/**
			 * Lists of peptideLength mapped by search id then link type
			 * Map<[link type], Map<[Search Id],List<[Peptide Length]>>>
			 */
			Map<String,Map<Integer,List<Integer>>> allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType
			) {
		
		Map<String,Map<Integer,Map<Integer,MutableInt>>> countsKeyPeptideLength_KeySearchId_KeyLinkType = new HashMap<>();
		
		for ( Map.Entry<String,Map<Integer,List<Integer>>> entryByLinkType : allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType.entrySet() ) {
			Map<Integer,Map<Integer,MutableInt>> countsKeyPeptideLength_KeySearchId = new HashMap<>();
			countsKeyPeptideLength_KeySearchId_KeyLinkType.put( entryByLinkType.getKey() , countsKeyPeptideLength_KeySearchId );
			for ( Map.Entry<Integer,List<Integer>> entryBySearchId : entryByLinkType.getValue().entrySet() ) {
				Map<Integer,MutableInt> countsKeyPeptideLength = new HashMap<>();
				countsKeyPeptideLength_KeySearchId.put( entryBySearchId.getKey(), countsKeyPeptideLength );
				for ( Integer peptideLength : entryBySearchId.getValue() ) {
					MutableInt countForPeptideLength = countsKeyPeptideLength.get( peptideLength );
					if ( countForPeptideLength == null ) {
						countsKeyPeptideLength.put( peptideLength, new MutableInt( 1 ) );
					} else {
						countForPeptideLength.increment();
					}
				}
			}
		}
		return countsKeyPeptideLength_KeySearchId_KeyLinkType;
	}
	
	
	/**
	 * @param allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType
	 * @param linkTypesList
	 * @param searchIdsListDeduppedSorted
	 * @return
	 * @throws ProxlWebappInternalErrorException 
	 */
	private PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results getPerChartData_KeyedOnLinkType( 
			Map<String,Map<Integer,List<Integer>>> allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType, 
			List<String> linkTypesList,
			List<SearchDTO> searches ) throws ProxlWebappInternalErrorException {
		
		List<PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList = new ArrayList<>( linkTypesList.size() );

		for ( String linkType : linkTypesList ) {
			Map<Integer,List<Integer>> allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId =
					allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType.get( linkType );
			
			if ( allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId == null ) {
				PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType resultForLinkType =  new PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType();
				resultForLinkType.setLinkType( linkType );
				resultForLinkType.setDataFound( false );
				dataForChartPerLinkTypeList.add( resultForLinkType );
			} else {
				PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType resultForLinkType =
						getSingleChartData_ForLinkType( allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId, searches );
				resultForLinkType.setLinkType( linkType );
				dataForChartPerLinkTypeList.add( resultForLinkType );
			}
		}
		
		PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results results = new PeptideLength_Histogram_For_PSMPeptideCutoffs_Merged_Results();
		results.setDataForChartPerLinkTypeList( dataForChartPerLinkTypeList );
		
		return results;
	}
	
	/**
	 * @param allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId
	 * @throws ProxlWebappInternalErrorException 
	 */
	private PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType getSingleChartData_ForLinkType( 
			Map<Integer,List<Integer>> allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId,
			List<SearchDTO> searches ) throws ProxlWebappInternalErrorException {
		
		//   Create output object for creating a chart
		
		boolean dataFound = false;
		
		Map<Integer, PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForSearchId> dataForChartPerSearchIdMap_KeyProjectSearchId = new HashMap<>();
		
		for ( SearchDTO search : searches ) {
			Integer searchId = search.getSearchId();
			Integer projectSearchId = search.getProjectSearchId();
					
			List<Integer> peptideLengthList = allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId.get( searchId );
			if ( peptideLengthList == null ) {
				PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForSearchId resultForSearchId = new PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForSearchId();
				resultForSearchId.setSearchId( searchId );
				resultForSearchId.setDataFound( false );
				dataForChartPerSearchIdMap_KeyProjectSearchId.put( projectSearchId, resultForSearchId );
			} else {
				dataFound = true;
				PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForSearchId resultForSearchId = 
						getSingleChartData_ForSearchId( peptideLengthList );
				resultForSearchId.setSearchId( searchId );
				resultForSearchId.setDataFound( true );
				dataForChartPerSearchIdMap_KeyProjectSearchId.put( projectSearchId, resultForSearchId );
			}
		}
		
		PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType result = new PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType();
		result.setDataForChartPerSearchIdMap_KeyProjectSearchId( dataForChartPerSearchIdMap_KeyProjectSearchId );
		result.setDataFound( dataFound );
		
		return result;
	}

	

	/**
	 * @param allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId
	 * @throws ProxlWebappInternalErrorException 
	 */
	private PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForSearchId getSingleChartData_ForSearchId( List<Integer> peptideLengthList ) throws ProxlWebappInternalErrorException {
		
		List<Double> values = new ArrayList<>( peptideLengthList.size() );
		
		for( Integer value : peptideLengthList ) {
			values.add( value.doubleValue() );
		}
		
		GetBoxPlotValuesResult getBoxPlotValuesResult =
				BoxPlotUtils.getInstance().getBoxPlotValues( values );
		
		double chartIntervalMax = getBoxPlotValuesResult.getChartIntervalMax();
		double chartIntervalMin = getBoxPlotValuesResult.getChartIntervalMin();
		
		// Add the peptideLength that are not within max (highcutoff) and min (lowcutoff) to a list and send to web app
		List<Integer> peptideLength_outliers = new ArrayList<>( peptideLengthList.size() );
		for( Integer peptideLength : peptideLengthList ) {
			double peptideLengthDouble = peptideLength.doubleValue();
			if ( peptideLengthDouble < chartIntervalMin || peptideLengthDouble > chartIntervalMax ) {
				peptideLength_outliers.add( (int)peptideLengthDouble );
			}
		}
		
		PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForSearchId result = new PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForSearchId();
		
		result.setChartIntervalMax( getBoxPlotValuesResult.getChartIntervalMax() );
		result.setChartIntervalMin( getBoxPlotValuesResult.getChartIntervalMin() );
		result.setFirstQuartile( getBoxPlotValuesResult.getFirstQuartile() );
		result.setThirdQuartile( getBoxPlotValuesResult.getThirdQuartile() );
		result.setMedian( getBoxPlotValuesResult.getMedian() );
		
		result.setPeptideLengths_outliers( peptideLength_outliers );
		
		result.setDataFound(true);
		
		return result;
	}
			
	///////////////////////////////////
	
	/**
	 * Get Lists of peptideLength mapped by search id then link type
	 * @param searches
	 * @param linkTypesForDBQuery
	 * @param modsForDBQuery
	 * @param searcherCutoffValuesRootLevel
	 * @throws ProxlWebappDataException
	 * @throws Exception
	 */
	private Map<String,Map<Integer,List<Integer>>> getAllSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType(
			List<SearchDTO> searches, 
			String[] linkTypesForDBQuery, 
			String[] modsForDBQuery,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel,
			QCPageQueryJSONRoot qcPageQueryJSONRoot ) throws ProxlWebappDataException, Exception {
		
		//  Get Lists of peptideLength mapped by search id then link type
		/**
		 * Map <{Link Type},,Map<<Search id>,List<{peptideLength}>>>
		 */
		Map<String,Map<Integer,List<Integer>>> allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType = new HashMap<>();

		//  Cache peptideDTO ById locally
		Map<Integer,PeptideDTO> peptideDTO_MappedById = new HashMap<>();

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
			
			// Map <{Link Type},List<{peptideLength}>>
			Map<String, List<Integer>> peptideLengthList_Map_KeyedOnLinkType = 
					PeptideLength_Histogram_For_PSMPeptideCutoffs.getInstance()
					.getPeptideLengths_SingleSearch(
							linkTypesForDBQuery, 
							modsForDBQuery, 
							peptideDTO_MappedById, 
							searchDTO, 
							searchId, 
							searcherCutoffValuesSearchLevel,
							qcPageQueryJSONRoot );
			
			//  Link Type includes 'dimer' which has be combined with 'unlinked'
			combineDimerListIntoUnlinkedList( peptideLengthList_Map_KeyedOnLinkType );
			
			for ( Map.Entry<String,List<Integer>> peptideLengthList_Map_Entry : peptideLengthList_Map_KeyedOnLinkType.entrySet() ) {
				String linkType = peptideLengthList_Map_Entry.getKey();
				Map<Integer,List<Integer>> allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId = 
						allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType.get( linkType );
				if ( allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId == null ) {
					allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId = new HashMap<>();
					allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType.put( linkType, allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId );
				}
				allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId.put( searchId, peptideLengthList_Map_Entry.getValue() );
			}
		}
		
		return allSearchesCombinedPeptideLengthList_Map_KeyedOnSearchId_KeyedOnLinkType;
	}


	/**
	 * Combine Dimer Counts Into Unlinked Counts
	 * @param chargeCountMap_KeyedOnLinkType_KeyedOnChargeValue
	 */
	private void combineDimerListIntoUnlinkedList( Map<String,List<Integer>> peptideLengthList_Map_KeyedOnLinkType ) {
		
		List<Integer> dimerValuesList = peptideLengthList_Map_KeyedOnLinkType.get( XLinkUtils.DIMER_TYPE_STRING );
		if ( dimerValuesList == null ) {
			//  No Dimer values so skip
			return;  //  EARLY EXIT
		}
		
		List<Integer> unlinkedValuesList = peptideLengthList_Map_KeyedOnLinkType.get( XLinkUtils.UNLINKED_TYPE_STRING );
		if ( unlinkedValuesList == null ) {
			//  No Unlinked values so simply copy dimer to unlinked and remove dimer
			peptideLengthList_Map_KeyedOnLinkType.put( XLinkUtils.UNLINKED_TYPE_STRING, dimerValuesList );
			peptideLengthList_Map_KeyedOnLinkType.remove( XLinkUtils.DIMER_TYPE_STRING );
			return;  //  EARLY EXIT
		}
		
		unlinkedValuesList.addAll( dimerValuesList );
		peptideLengthList_Map_KeyedOnLinkType.remove( XLinkUtils.DIMER_TYPE_STRING );
		
	}
	


}
