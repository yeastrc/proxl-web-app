package org.yeastrc.xlink.www.qc_data.reported_peptide_level.main;

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
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.www.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.reported_peptide_level.objects.PeptideLength_Histogram_For_PSMPeptideCutoffsResults;
import org.yeastrc.xlink.www.qc_data.reported_peptide_level.objects.PeptideLength_Histogram_For_PSMPeptideCutoffsResults.PeptideLength_Histogram_For_PSMPeptideCutoffsResultsChartBucket;
import org.yeastrc.xlink.www.qc_data.reported_peptide_level.objects.PeptideLength_Histogram_For_PSMPeptideCutoffsResults.PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Get Peptide Length Histogram where associated Reported Peptides meet criteria, including Reported Peptide and PSM cutoffs
 *
 */
public class PeptideLength_Histogram_For_PSMPeptideCutoffs {
	
	private static final Logger log = Logger.getLogger(PeptideLength_Histogram_For_PSMPeptideCutoffs.class);
	
	/**
	 * private constructor
	 */
	private PeptideLength_Histogram_For_PSMPeptideCutoffs(){}
	public static PeptideLength_Histogram_For_PSMPeptideCutoffs getInstance( ) throws Exception {
		PeptideLength_Histogram_For_PSMPeptideCutoffs instance = new PeptideLength_Histogram_For_PSMPeptideCutoffs();
		return instance;
	}
		
	/**
	 * @param filterCriteriaJSON
	 * @param projectSearchIdsListDeduppedSorted
	 * @param searches
	 * @param searchesMapOnSearchId
	 * @return
	 * @throws Exception
	 */
	public PeptideLength_Histogram_For_PSMPeptideCutoffsResults getPeptideLength_Histogram_For_PSMPeptideCutoffs( 			
			String filterCriteriaJSON, 
			List<Integer> projectSearchIdsListDeduppedSorted,
			List<SearchDTO> searches, 
			Map<Integer, SearchDTO> searchesMapOnSearchId ) throws Exception {

		Collection<Integer> searchIds = new HashSet<>();
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
		Set<String> selectedLinkTypes = new HashSet<>();
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
			searchIdsListDeduppedSorted.add( search.getSearchId() );
			mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
		}

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
		
		//  Get Lists of peptideLength mapped by link type
		Map<String,List<Integer>> allSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType = 
				getAllSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType(searches, linkTypesForDBQuery, modsForDBQuery, searcherCutoffValuesRootLevel, selectedLinkTypes);
		
		
		Map<String, PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType> peptideLengthInChartBuckets_KeyedOnLinkType = 
				getPeptideLengthInChartBuckets_KeyedOnLinkType( allSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType );
		
		PeptideLength_Histogram_For_PSMPeptideCutoffsResults peptideLength_Histogram_For_PSMPeptideCutoffsResults = new PeptideLength_Histogram_For_PSMPeptideCutoffsResults();
		
		//  copy map to array for output, in a specific order
		List<PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType> resultsPerLinkTypeList = new ArrayList<>( 3 );
		
		addToOutputListForLinkType( XLinkUtils.CROSS_TYPE_STRING, selectedLinkTypes, resultsPerLinkTypeList, peptideLengthInChartBuckets_KeyedOnLinkType );
		addToOutputListForLinkType( XLinkUtils.LOOP_TYPE_STRING, selectedLinkTypes, resultsPerLinkTypeList, peptideLengthInChartBuckets_KeyedOnLinkType );
		addToOutputListForLinkType( XLinkUtils.UNLINKED_TYPE_STRING, selectedLinkTypes, resultsPerLinkTypeList, peptideLengthInChartBuckets_KeyedOnLinkType );
		
		peptideLength_Histogram_For_PSMPeptideCutoffsResults.setDataForChartPerLinkTypeList( resultsPerLinkTypeList );
		
		return peptideLength_Histogram_For_PSMPeptideCutoffsResults;
	}
	

	/**
	 * @param linkType
	 * @param countForLinkTypeList
	 * @param countForLinkType_ByLinkType
	 */
	private void addToOutputListForLinkType( 
			String linkType, 
			Set<String> selectedLinkTypes, 
			List<PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType> resultsPerLinkTypeList,
			Map<String, PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType> peptideLengthInChartBuckets_KeyedOnLinkType ) {
		
		if ( ! selectedLinkTypes.contains( linkType ) ) {
			//  link type not selected
			return; // EARLY EXIT
		}
		
		PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType valueForLinkType = 
				peptideLengthInChartBuckets_KeyedOnLinkType.get( linkType );
		
		if ( valueForLinkType == null ) {
			//  No data for link type so create empty entry
			valueForLinkType = new PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType();
			valueForLinkType.setLinkType( linkType );
			resultsPerLinkTypeList.add( valueForLinkType );
			return; // EARLY EXIT
		}
		
		resultsPerLinkTypeList.add( valueForLinkType );
	}
	
	/**
	 * @param allSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType
	 * @return
	 */
	private Map<String, PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType>getPeptideLengthInChartBuckets_KeyedOnLinkType( 
			Map<String,List<Integer>> allSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType ) {
		
		Map<String, PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType> peptideLengthInChartBuckets_KeyedOnLinkType = new HashMap<>();
		
		for ( Map.Entry<String,List<Integer>> entry : allSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType.entrySet() ) {
			List<Integer> peptideLengthList = entry.getValue();
			
			//  Find max and min values
			int peptideLengthMin = 0;
			int peptideLengthMax =  0;
			boolean firstOverallpeptideLengthEntry = true;
			for ( int peptideLength : peptideLengthList ) {
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
			
			PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType peptideLength_HistogramData =
					getpeptideLength_HistogramData( peptideLengthList, peptideLengthMin, peptideLengthMax );
			peptideLength_HistogramData.setLinkType( entry.getKey() );
			peptideLength_HistogramData.setPeptideLengthMin( peptideLengthMin );
			peptideLength_HistogramData.setPeptideLengthMax( peptideLengthMax );
			
			peptideLengthInChartBuckets_KeyedOnLinkType.put( entry.getKey(), peptideLength_HistogramData );
		}
		
		
		return peptideLengthInChartBuckets_KeyedOnLinkType;
	}

	/**
	 * @param peptideLengthList
	 * @return
	 */
	private PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType getpeptideLength_HistogramData( 
			List<Integer> peptideLengthList,
			int peptideLengthMin, 
			int peptideLengthMax ) {
		
		int peptideLengthMaxMinusMin = peptideLengthMax - peptideLengthMin;
		
		int peptideLengthMinAsDouble = peptideLengthMin;
		double peptideLengthMaxMinusMinAsDouble = peptideLengthMaxMinusMin;
		
		//  Process data into bins
		int binCount = (int) ( Math.sqrt( peptideLengthList.size() ) );
		int[] peptideLengthCounts = new int[ binCount ];
		double binSizeAsDouble = ( peptideLengthMaxMinusMinAsDouble ) / binCount;
		
		for ( Integer peptideLength : peptideLengthList ) {
			double peptideLengthFraction = ( peptideLength.doubleValue() - peptideLengthMinAsDouble ) / peptideLengthMaxMinusMinAsDouble;
			int bin = (int) ( (  peptideLengthFraction ) * binCount );
			if ( bin < 0 ) {
				bin = 0;
			} else if ( bin >= binCount ) {
				bin = binCount - 1;
			} 
			peptideLengthCounts[ bin ]++;
		}
		
		List<PeptideLength_Histogram_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets = new ArrayList<>();
		double binHalf = binSizeAsDouble / 2 ;
		//  Take the data in the bins and  create "buckets" in the format required for the charting API
		for ( int binIndex = 0; binIndex < peptideLengthCounts.length; binIndex++ ) {
			PeptideLength_Histogram_For_PSMPeptideCutoffsResultsChartBucket chartBucket = new PeptideLength_Histogram_For_PSMPeptideCutoffsResultsChartBucket();
			chartBuckets.add( chartBucket );
			int peptideLengthCount = peptideLengthCounts[ binIndex ];
			double binStartDouble = ( ( binIndex * binSizeAsDouble ) ) + peptideLengthMin;
			int binStart = (int)Math.round( binStartDouble );
			if ( binIndex == 0 && binStartDouble < 0.1 ) {
				binStart = 0;
			}
			chartBucket.setBinStart( binStart );
			int binEnd = (int)Math.round( ( binIndex + 1 ) * binSizeAsDouble ) - 1 + peptideLengthMin;
			if ( binIndex == peptideLengthCounts.length - 1 ) {
				binEnd++;  // increment since last entry at binEnd + 1
			}
			chartBucket.setBinEnd( binEnd );
			double binMiddleDouble = binStartDouble + binHalf;
			chartBucket.setBinCenter( binMiddleDouble );
			chartBucket.setCount( peptideLengthCount );
		}
		
		PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType result = new PeptideLength_Histogram_For_PSMPeptideCutoffsResultsForLinkType();
		
		result.setChartBuckets( chartBuckets );
		result.setNumReportedPeptides( peptideLengthList.size() );
		result.setPeptideLengthMax( peptideLengthMax );
		result.setPeptideLengthMin( peptideLengthMin );
		
		return result;
	}
	
	

	/**
	 * @param searches
	 * @param linkTypesForDBQuery
	 * @param modsForDBQuery
	 * @param searcherCutoffValuesRootLevel
	 * @return Map of lists of protein lengths, keyed on link type
	 * @throws ProxlWebappDataException
	 * @throws Exception
	 */
	private Map<String,List<Integer>> getAllSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType(
			List<SearchDTO> searches, 
			String[] linkTypesForDBQuery, 
			String[] modsForDBQuery,
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel,
			Set<String> selectedLinkTypes ) throws ProxlWebappDataException, Exception {
		
		/**
		 * Map <{Link Type},List<{peptideLength}>>
		 */
		Map<String,List<Integer>> allSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType = new HashMap<>();

		for ( String linkType : selectedLinkTypes ) {
			List<Integer> peptideLengthList = new ArrayList<>();
			allSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType.put( linkType, peptideLengthList );
		}
		
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
				List<SrchRepPeptPeptideDTO> srchRepPeptPeptideDTOList =
						SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher.getInstance()
						.getForSearchIdReportedPeptideId( searchId, reportedPeptideId );;
				
				if ( webReportedPeptide.getSearchPeptideCrosslink() != null ) {
					//  Process a crosslink
					linkType = XLinkUtils.CROSS_TYPE_STRING;
					
					//  validation for crosslink
					if ( srchRepPeptPeptideDTOList.size() != 2 ) {
						String msg = "For Crosslink: List<SrchRepPeptPeptideDTO> results.size() != 2. SearchId: " + searchId
						+ ", ReportedPeptideId: " + reportedPeptideId ;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
						if ( srchRepPeptPeptideDTO.getPeptidePosition_1()  == null 
								|| srchRepPeptPeptideDTO.getPeptidePosition_1() == SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Crosslink: srchRepPeptPeptideDTO.getPeptidePosition_1() not populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						if ( srchRepPeptPeptideDTO.getPeptidePosition_2() != null
								&& srchRepPeptPeptideDTO.getPeptidePosition_2() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Crosslink: srchRepPeptPeptideDTO.getPeptidePosition_2() is populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
					}

				} else if ( webReportedPeptide.getSearchPeptideLooplink() != null ) {
					//  Process a looplink
					linkType = XLinkUtils.LOOP_TYPE_STRING;
					
					//  validation for looplink
					if ( srchRepPeptPeptideDTOList.size() != 1 ) {
						String msg = "For Looplink: List<SrchRepPeptPeptideDTO> results.size() != 1. SearchId: " + searchId
						+ ", ReportedPeptideId: " + reportedPeptideId ;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
						if ( srchRepPeptPeptideDTO.getPeptidePosition_1()  == null 
								|| srchRepPeptPeptideDTO.getPeptidePosition_1() == SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Looplink: srchRepPeptPeptideDTO.getPeptidePosition_1() not populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						if ( srchRepPeptPeptideDTO.getPeptidePosition_2() == null
								|| srchRepPeptPeptideDTO.getPeptidePosition_2() == SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Looplink: srchRepPeptPeptideDTO.getPeptidePosition_2() not populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
					}

				} else if ( webReportedPeptide.getSearchPeptideUnlinked() != null ) {
					//  Process a unlinked
					linkType = XLinkUtils.UNLINKED_TYPE_STRING;
					
					//  validation for unlinked
					if ( srchRepPeptPeptideDTOList.size() != 1 ) {
						String msg = "For Unlinked: List<SrchRepPeptPeptideDTO> results.size() != 1. SearchId: " + searchId
						+ ", ReportedPeptideId: " + reportedPeptideId ;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
						if ( srchRepPeptPeptideDTO.getPeptidePosition_1()  != null 
								&& srchRepPeptPeptideDTO.getPeptidePosition_1() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Unlinked: srchRepPeptPeptideDTO.getPeptidePosition_1() is populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						if ( srchRepPeptPeptideDTO.getPeptidePosition_2() != null
								&& srchRepPeptPeptideDTO.getPeptidePosition_2() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Unlinked: srchRepPeptPeptideDTO.getPeptidePosition_2() is populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
					}
				} else if ( webReportedPeptide.getSearchPeptideDimer() != null ) {
					//  Process a dimer
					linkType = XLinkUtils.UNLINKED_TYPE_STRING;  //  Lump in with unlinked reported peptides

					//  validation for dimer
					if ( srchRepPeptPeptideDTOList.size() != 2 ) {
						String msg = "For Dimer: List<SrchRepPeptPeptideDTO> results.size() != 2. SearchId: " + searchId
						+ ", ReportedPeptideId: " + reportedPeptideId ;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
						if ( srchRepPeptPeptideDTO.getPeptidePosition_1()  != null 
								&& srchRepPeptPeptideDTO.getPeptidePosition_1() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Dimer: srchRepPeptPeptideDTO.getPeptidePosition_1() is populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						if ( srchRepPeptPeptideDTO.getPeptidePosition_2() != null
								&& srchRepPeptPeptideDTO.getPeptidePosition_2() != SrchRepPeptPeptideDTO.PEPTIDE_POSITION_NOT_SET ) {
							String msg = 
									"For Dimer: srchRepPeptPeptideDTO.getPeptidePosition_2() is populated "
									+ " for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
					}
				} else {
					String msg = 
							"Link type unkown"
							+ " for reportedPeptideId: " + reportedPeptideId
							+ ", searchId: " + searchId;
					log.error( msg );
					throw new ProxlWebappDataException( msg );
				}
				
				// get object from map for link type
				List<Integer> peptideLengthsListForLinkType = allSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType.get( linkType );
				if ( peptideLengthsListForLinkType == null ) {
					String msg = "In adding peptide length for link type, link type not found: " + linkType;
					log.error( msg );
					throw new Exception(msg);
				}
				
				int peptideLength = 0; //  peptide length is sum of peptides for reproted peptide

				//  process srchRepPeptPeptideDTOList (Each peptide mapped to the reported peptide)
				for ( SrchRepPeptPeptideDTO srchRepPeptPeptideDTO : srchRepPeptPeptideDTOList ) {
					// get PeptideDTO, caching locally in peptideDTO_MappedById
					PeptideDTO peptide = peptideDTO_MappedById.get( srchRepPeptPeptideDTO.getPeptideId() );
					if ( peptide == null ) {
						peptide = PeptideDAO.getInstance().getPeptideDTOFromDatabase( srchRepPeptPeptideDTO.getPeptideId() );
						//  To directly retrieve from DB:  PeptideDAO.getInstance().getPeptideDTOFromDatabaseActual( id )
						if ( peptide == null ) {
							String msg = 
									"PeptideDTO not found in DB for id: " + srchRepPeptPeptideDTO.getPeptideId()
									+ ", for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
									+ ", for reportedPeptideId: " + reportedPeptideId
									+ ", searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						peptideDTO_MappedById.put( srchRepPeptPeptideDTO.getPeptideId(), peptide );
					}
					//  peptide length is sum of peptides for reproted peptide
					peptideLength += peptide.getSequence().length();
				}
				peptideLengthsListForLinkType.add( peptideLength );
			}
		}

		return allSearchesCombinedPeptideLengthList_Map_KeyedOnLinkType;
	}

}
