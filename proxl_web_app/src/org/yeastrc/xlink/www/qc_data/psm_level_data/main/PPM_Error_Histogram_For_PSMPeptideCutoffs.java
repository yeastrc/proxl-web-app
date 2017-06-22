package org.yeastrc.xlink.www.qc_data.psm_level_data.main;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.StaticModDAO;
import org.yeastrc.xlink.dto.StaticModDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.www.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.PsmDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PPM_Error_Histogram_For_PSMPeptideCutoffs_Result;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PPM_Error_Histogram_For_PSMPeptideCutoffs_Result.PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket;
import org.yeastrc.xlink.www.qc_data.psm_level_data.objects.PPM_Error_Histogram_For_PSMPeptideCutoffs_Result.PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptDynamicModSearcher;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;
import org.yeastrc.xlink.www.web_utils.PSMMassCalculator;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Compute PPM Error Histogram
 */
public class PPM_Error_Histogram_For_PSMPeptideCutoffs {

	private static final Logger log = Logger.getLogger(PPM_Error_Histogram_For_PSMPeptideCutoffs.class);
	
	/**
	 * private constructor
	 */
	private PPM_Error_Histogram_For_PSMPeptideCutoffs(){}
	public static PPM_Error_Histogram_For_PSMPeptideCutoffs getInstance( ) throws Exception {
		PPM_Error_Histogram_For_PSMPeptideCutoffs instance = new PPM_Error_Histogram_For_PSMPeptideCutoffs();
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
	public PPM_Error_Histogram_For_PSMPeptideCutoffs_Result getPPM_Error_Histogram_For_PSMPeptideCutoffs( 
			String filterCriteriaJSON, 
			List<Integer> projectSearchIdsListDeduppedSorted,
			List<SearchDTO> searches, 
			Map<Integer, SearchDTO> searchesMapOnSearchId ) throws Exception {

		Collection<Integer> searchIds = new HashSet<>();
//		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
		for ( SearchDTO search : searches ) {
			searchIds.add( search.getSearchId() );
			searchIdsListDeduppedSorted.add( search.getSearchId() );
//			mapProjectSearchIdToSearchId.put( search.getProjectSearchId(), search.getSearchId() );
		}

		Map<String, List<Double>> ppmErrorListForLinkType_ByLinkType = 
				createppmErrorListForLinkType_ByLinkTypeMap( filterCriteriaJSON, searches, searchIds );
		
		//  Combine the Dimer into the Unlinked
		
		List<Double> ppmErrorListForDimer = ppmErrorListForLinkType_ByLinkType.get( XLinkUtils.DIMER_TYPE_STRING );
		if ( ppmErrorListForDimer != null && ( ! ppmErrorListForDimer.isEmpty() ) ) {
			List<Double> ppmErrorListForUnlinked = ppmErrorListForLinkType_ByLinkType.get( XLinkUtils.UNLINKED_TYPE_STRING );
			ppmErrorListForUnlinked.addAll( ppmErrorListForDimer );
		}
		
		PPM_Error_Histogram_For_PSMPeptideCutoffs_Result result = 
				getPPM_Error_Histogram_For_PSMPeptideCutoffs_Result( ppmErrorListForLinkType_ByLinkType );
		
		return result;
	}
	
	
	/**
	 * @param ppmErrorListForLinkType_ByLinkType
	 * @return
	 */
	private PPM_Error_Histogram_For_PSMPeptideCutoffs_Result getPPM_Error_Histogram_For_PSMPeptideCutoffs_Result(
			Map<String,List<Double>> ppmErrorListForLinkType_ByLinkType ) {
		
		Map<String,PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> resultsByLinkTypeMap = new HashMap<>();

		for ( Map.Entry<String,List<Double>> ppmErrorListForLinkTypeEntry : ppmErrorListForLinkType_ByLinkType.entrySet() ) {
			String linkType = ppmErrorListForLinkTypeEntry.getKey();
			PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType resultsForLinkType =
					getPPM_Error_HistogramData_ForLinkType( ppmErrorListForLinkTypeEntry.getValue() );
			resultsForLinkType.setLinkType( linkType );
			resultsByLinkTypeMap.put( linkType, resultsForLinkType );
		}
		
		List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList = new ArrayList<>( 5 );
		
		//  copy map to array for output, in a specific order
		
		addToOutputListForLinkType( XLinkUtils.CROSS_TYPE_STRING, dataForChartPerLinkTypeList, resultsByLinkTypeMap );
		addToOutputListForLinkType( XLinkUtils.LOOP_TYPE_STRING, dataForChartPerLinkTypeList, resultsByLinkTypeMap );
		addToOutputListForLinkType( XLinkUtils.UNLINKED_TYPE_STRING, dataForChartPerLinkTypeList, resultsByLinkTypeMap );
		

		PPM_Error_Histogram_For_PSMPeptideCutoffs_Result result = new PPM_Error_Histogram_For_PSMPeptideCutoffs_Result();
		result.setDataForChartPerLinkTypeList( dataForChartPerLinkTypeList );
		
		return result;
	}
	
	private void addToOutputListForLinkType( 
			String linkType, 
			List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> dataForChartPerLinkTypeList, 
			Map<String,PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType> resultsByLinkTypeMap ) {
		PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType item = resultsByLinkTypeMap.get( linkType );
		if ( item != null ) {
			dataForChartPerLinkTypeList.add( item );
		}
	}
	

	/**
	 * @param ppmErrorList
	 * @return
	 */
	private PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType getPPM_Error_HistogramData_ForLinkType( 
			List<Double> ppmErrorList ) {
		
		int numScans = ppmErrorList.size();
		boolean firstOverallpreMZEntry = true;
		//  Find max and min values
		double ppmErrorMin = Double.MAX_VALUE;
		double ppmErrorMax =  Double.MIN_VALUE;
		for ( double ppmErrorEntry : ppmErrorList ) {
			if ( firstOverallpreMZEntry  ) {
				firstOverallpreMZEntry = false;
				ppmErrorMin = ppmErrorEntry;
				ppmErrorMax = ppmErrorEntry;
			} else {
				if ( ppmErrorEntry < ppmErrorMin ) {
					ppmErrorMin = ppmErrorEntry;
				}
				if ( ppmErrorEntry > ppmErrorMax ) {
					ppmErrorMax = ppmErrorEntry;
				}
			}
		}
		double ppmErrorMaxMinusMin = ppmErrorMax - ppmErrorMin;
		
		double ppmErrorMinAsDouble = ppmErrorMin;
		double ppmErrorMaxMinusMinAsDouble = ppmErrorMaxMinusMin;
		
		//  Process data into bins
		int binCount = (int) ( Math.sqrt( ppmErrorList.size() ) );
		int[] ppmErrorCounts = new int[ binCount ];
		double binSizeAsDouble = ( ppmErrorMaxMinusMinAsDouble ) / binCount;
		
		for ( double ppmErrorEntry : ppmErrorList ) {
			double preMZFraction = ( ppmErrorEntry - ppmErrorMinAsDouble ) / ppmErrorMaxMinusMinAsDouble;
			int bin = (int) ( (  preMZFraction ) * binCount );
			if ( bin < 0 ) {
				bin = 0;
			} else if ( bin >= binCount ) {
				bin = binCount - 1;
			} 
			ppmErrorCounts[ bin ]++;
		}
		
		List<PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket> chartBuckets = new ArrayList<>();
		double binHalf = binSizeAsDouble / 2 ;
		//  Take the data in the bins and  create "buckets" in the format required for the charting API
		for ( int binIndex = 0; binIndex < ppmErrorCounts.length; binIndex++ ) {
			PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket chartBucket = new PPM_Error_Histogram_For_PSMPeptideCutoffsResultsChartBucket();
			chartBuckets.add( chartBucket );
			int preMZCount = ppmErrorCounts[ binIndex ];
			double binStart = ( ( binIndex * binSizeAsDouble ) ) + ppmErrorMinAsDouble;
			chartBucket.setBinStart( binStart );
			double binEnd = ( ( binIndex + 1 ) * binSizeAsDouble ) + ppmErrorMinAsDouble;
			chartBucket.setBinEnd( binEnd );
			double binMiddleDouble = binStart + binHalf;
			chartBucket.setBinCenter( binMiddleDouble );
			chartBucket.setCount( preMZCount );
		}
		
		PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType result = new PPM_Error_Histogram_For_PSMPeptideCutoffsResultsForLinkType();
		
		result.setChartBuckets( chartBuckets );
		result.setNumScans( numScans );
		result.setPpmErrorMax( ppmErrorMax );
		result.setPpmErrorMin( ppmErrorMin );
		
		return result;
	}
	
	
	
	/**
	 * @param filterCriteriaJSON
	 * @param searches
	 * @param searchIds
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws Exception
	 * @throws ProxlWebappDataException
	 */
	private Map<String, List<Double>> createppmErrorListForLinkType_ByLinkTypeMap(
			String filterCriteriaJSON, 
			List<SearchDTO> searches,
			Collection<Integer> searchIds)
			throws JsonParseException, JsonMappingException, IOException, Exception, ProxlWebappDataException {
		
		
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
		
		
		//   Map of List of PPM Error by Link Type
		
		Map<String,List<Double>> ppmErrorListForLinkType_ByLinkType = new HashMap<>();
		
		//  Populate countForLinkType_ByLinkType for selected link types
		if ( mergedPeptideQueryJSONRoot.getLinkTypes() == null || mergedPeptideQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		} else {
			for ( String linkTypeFromWeb : mergedPeptideQueryJSONRoot.getLinkTypes() ) {
				List<Double> ppmErrorListForLinkType = new ArrayList<>();
				if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkTypeFromWeb ) ) {
					ppmErrorListForLinkType_ByLinkType.put( XLinkUtils.CROSS_TYPE_STRING, ppmErrorListForLinkType );
				} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkTypeFromWeb ) ) {
					ppmErrorListForLinkType_ByLinkType.put( XLinkUtils.LOOP_TYPE_STRING, ppmErrorListForLinkType );
				} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkTypeFromWeb ) ) {
					ppmErrorListForLinkType_ByLinkType.put( XLinkUtils.UNLINKED_TYPE_STRING, ppmErrorListForLinkType );
					ppmErrorListForLinkType_ByLinkType.put( XLinkUtils.DIMER_TYPE_STRING, ppmErrorListForLinkType );
				} else {
					String msg = "linkType is invalid, linkType: " + linkTypeFromWeb;
					log.error( msg );
					throw new Exception( msg );
				}
			}
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
			
			//  Get static mods for search id
			List<StaticModDTO> staticModDTOList = StaticModDAO.getInstance().getStaticModDTOForSearchId( searchId );
			
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
						.getForSearchIdReportedPeptideId( searchId, reportedPeptideId );
				
				
				
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
				
				// get from map for link type
				List<Double> ppmErrorListForLinkType = ppmErrorListForLinkType_ByLinkType.get( linkType );
				
				if ( ppmErrorListForLinkType == null ) {
					String msg = "In processing Reported Peptides, link type not found: " + linkType;
					log.error( msg );
					throw new Exception(msg);
				}

				//  Collect the peptides and dynamic mods
				
				PeptideDTO peptide_1 =  null;
				PeptideDTO peptide_2 =  null;
				
				List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList_1 = null;
				List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList_2 = null;
				
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
					
//					staticModDTOList
					
					List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList = 
							SrchRepPeptPeptDynamicModSearcher.getInstance()
							.getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( srchRepPeptPeptideDTO.getId() );

					if ( peptide_1 == null ) {
						peptide_1 = peptide;
					} else if ( peptide_2 == null ) {
						peptide_2 = peptide;
					} else {
						String msg = 
								"peptide_1 and peptide_2 already have values"
								+ ", for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
								+ ", for reportedPeptideId: " + reportedPeptideId
								+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}


					if ( srchRepPeptPeptDynamicModDTOList_1 == null ) {
						srchRepPeptPeptDynamicModDTOList_1 = srchRepPeptPeptDynamicModDTOList;
					} else if ( srchRepPeptPeptDynamicModDTOList_2 == null ) {
						srchRepPeptPeptDynamicModDTOList_1 = srchRepPeptPeptDynamicModDTOList;
					} else {
						String msg = 
								"srchRepPeptPeptDynamicModDTOList_1 and srchRepPeptPeptDynamicModDTOList_2 already have values"
								+ ", for srchRepPeptPeptideDTO.id: " + srchRepPeptPeptideDTO.getId()
								+ ", for reportedPeptideId: " + reportedPeptideId
								+ ", searchId: " + searchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
				}
				

				// process PSMs for this Reported Peptide

				List<PsmWebDisplayWebServiceResult> psmWebDisplayList = 
						PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( searchId, reportedPeptideId, searcherCutoffValuesSearchLevel );

				for ( PsmWebDisplayWebServiceResult psmWebDisplayWebServiceResult : psmWebDisplayList ) {

//					psmWebDisplayWebServiceResult.getRetentionTime();

					BigDecimal scanPreMZ = psmWebDisplayWebServiceResult.getPreMZ(); // from scan table
					double scanPreMZasDouble = scanPreMZ.doubleValue();

					PsmDTO psmDTO = psmWebDisplayWebServiceResult.getPsmDTO();
					Integer charge = psmDTO.getCharge();
					BigDecimal linkerMass = psmDTO.getLinkerMass();
					
					Double linkerMassAsDouble = null;
					
					if ( linkerMass != null ) {
						linkerMassAsDouble = linkerMass.doubleValue();
					}
					
					if ( charge != null && scanPreMZ != null ) {

						//  Compute PPM Error
						
						//  Calculate M/Z from sequence(s), ...
						
						double mzCalculated =
								PSMMassCalculator.calculateMZForPSM( 
										peptide_1, 
										peptide_2, 
										staticModDTOList, 
										srchRepPeptPeptDynamicModDTOList_1, 
										srchRepPeptPeptDynamicModDTOList_2, 
										charge, 
										linkerMassAsDouble );

						//  Only for testing.  Ran and did not throw exception
//						double mzCalculatedFromPsmDTO =
//								PSMMassCalculator.calculateMZForPSM( psmDTO );
//						
//						if ( mzCalculated != mzCalculatedFromPsmDTO ) {
//							String msg = "mzCalculated != mzCalculatedFromPsmDTO.  "
//									+ "mzCalculated: " + mzCalculated
//									+ ", mzCalculatedFromPsmDTO: " + mzCalculatedFromPsmDTO;
//							log.error( msg );
//							throw new ProxlWebappInternalErrorException(msg);
//						}
						
						//  Compare preMZ to computed mass, applying charge, linkerMass(if not null)
						
						double ppmError = ( scanPreMZasDouble - mzCalculated ) / mzCalculated * 1000000;
						
//						if ( ppmError > 600 && linkType.equals( XLinkUtils.UNLINKED_TYPE_STRING ) ) {
//							
//							log.error( "ppmError > 600. ppmError: " + ppmError
//									+ "\n mzCalculated: " + mzCalculated
//									+ "\n linkType: " + linkType
//									+ "\n scanPreMZasDouble: " + scanPreMZasDouble 
//									+ "\n peptide_1: " + peptide_1 
//									+ "\n peptide_2: " + peptide_2
//									+ "\n srchRepPeptPeptDynamicModDTOList_1: " + srchRepPeptPeptDynamicModDTOList_1
//									+ "\n srchRepPeptPeptDynamicModDTOList_2: " + srchRepPeptPeptDynamicModDTOList_2
//									+ "\n charge: " + charge
//									+ "\n linkerMassAsDouble: " + linkerMassAsDouble
//									+ "\n staticModDTOList: " + staticModDTOList
//									
//									);
//							throw new Exception( "FORCE EXCEPTION");
//						}
						
						
						ppmErrorListForLinkType.add( ppmError );
						
					}


				}
				
			}
		}
		return ppmErrorListForLinkType_ByLinkType;
	}
	
	
	
//	/**
//	 * @param peptideSequence
//	 * @param staticModDTOList
//	 * @return positions ("1" based), null if staticModDTOList is null or empty
//	 * @throws Exception
//	 */
//	private Set<Integer> getStaticModsPeptidePositionsForPeptideSequenceStaticModList( String peptideSequence, List<StaticModDTO> staticModDTOList ) throws Exception {
//		if ( staticModDTOList == null || staticModDTOList.isEmpty() ) {
//			return null;  //  EARLY EXIT 
//		}
//		Set<Integer> results = new HashSet<>();
//		int staticModIndex = -2; // arbitrary initial value
//		for ( StaticModDTO staticModDTO : staticModDTOList ) {
//			int startSearchIndex = 0;
//			while ( ( staticModIndex = peptideSequence.indexOf( staticModDTO.getResidue(), startSearchIndex ) ) != -1 ) {
//				int staticModPosition = staticModIndex + 1; // add "1" to index to get position which is "1" based
//				results.add( staticModPosition );
//				startSearchIndex = staticModIndex + 1; // move startSearchIndex to after found staticModIndex
//			}
//		}
//		return results;
//	}
//	
//	/**
//	 * @param srchRepPeptPeptideId
//	 * @return positions ("1" based)
//	 * @throws Exception
//	 */
//	private Set<Integer> getDynamicModsPeptidePositionsForSrchRepPeptPeptide( int srchRepPeptPeptideId ) throws Exception {
//		Set<Integer> results = new HashSet<>();
//		List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList = 
//				SrchRepPeptPeptDynamicModSearcher.getInstance()
//				.getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( srchRepPeptPeptideId );
//		
//		for ( SrchRepPeptPeptDynamicModDTO dynamicModRecord : srchRepPeptPeptDynamicModDTOList ) {
//				results.add( dynamicModRecord.getPosition() );
//		}
//		
//		return results;
//	}

}
