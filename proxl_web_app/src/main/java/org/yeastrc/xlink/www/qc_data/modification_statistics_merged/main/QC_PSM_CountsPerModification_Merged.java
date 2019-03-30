package org.yeastrc.xlink.www.qc_data.modification_statistics_merged.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dao.UnifiedRepPepDynamicModLookupDAO;
import org.yeastrc.xlink.dao.UnifiedRepPepMatchedPeptideLookupDAO;
import org.yeastrc.xlink.dto.UnifiedRepPepDynamicModLookupDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepMatchedPeptideLookupDTO;
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
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.modification_statistics_merged.objects.QC_PSM_CountsPerModification_Merged_Results;
import org.yeastrc.xlink.www.qc_data.modification_statistics_merged.objects.QC_PSM_CountsPerModification_Merged_Results.QC_PSM_CountsPerModificationResultsPerLinkType_Merged;
import org.yeastrc.xlink.www.qc_data.modification_statistics_merged.objects.QC_PSM_CountsPerModification_Merged_Results.QC_PSM_CountsPerModificationResultsPerModMass_Merged;
import org.yeastrc.xlink.www.qc_data.modification_statistics_merged.objects.QC_PSM_CountsPerModification_Merged_Results.QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.web_utils.DynamicModsFilterSelectionFromUserPreprocessing;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;
import org.yeastrc.xlink.www.web_utils.DynamicModsFilterSelectionFromUserPreprocessing.DynamicModsFilterSelectionFromUserPreprocessingResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class QC_PSM_CountsPerModification_Merged {


	private static final Logger log = LoggerFactory.getLogger( QC_PSM_CountsPerModification_Merged.class);
	
	/**
	 * private constructor
	 */
	private QC_PSM_CountsPerModification_Merged(){}
	public static QC_PSM_CountsPerModification_Merged getInstance( ) throws Exception {
		QC_PSM_CountsPerModification_Merged instance = new QC_PSM_CountsPerModification_Merged();
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
	public QC_PSM_CountsPerModification_Merged_Results getQC_PSM_CountsPerModification_Merged( 
			String filterCriteriaJSON, 
			List<SearchDTO> searches ) throws Exception {

		Collection<Integer> searchIds = new HashSet<>();
		Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
		List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searches.size() );
		
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

		//  Populate countForLinkType_ByLinkType for selected link types
		if ( mergedPeptideQueryJSONRoot.getLinkTypes() == null || mergedPeptideQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		} 
		String[] linkTypesFromURL = mergedPeptideQueryJSONRoot.getLinkTypes();
		
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
		
		DynamicModsFilterSelectionFromUserPreprocessingResult dynamicModsFilterSelectionFromUserPreprocessingResult =
				DynamicModsFilterSelectionFromUserPreprocessing.getInstance().getDynamicModsFilterHandleAllSelected( modsForDBQuery, searchIds );
		
		if ( mergedPeptideQueryJSONRoot.getLinkTypes() == null || mergedPeptideQueryJSONRoot.getLinkTypes().length == 0 ) {
			String msg = "At least one linkType is required";
			log.error( msg );
			throw new Exception( msg );
		}
		
		Map<Integer, Step_1_ResultsPerSearchIdTempData> perSearchIdTempData_BySearchId = new HashMap<>();
				
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
			
			Map<String,Step_1_ResultsPerSearchIdPerLinkTypeTempData> perLinkTypeTempData_ByLinkType = new HashMap<>();
			
			for ( String linkType : linkTypesList ) {
				Step_1_ResultsPerSearchIdPerLinkTypeTempData perLinkTypeTempData = new Step_1_ResultsPerSearchIdPerLinkTypeTempData();
				perLinkTypeTempData_ByLinkType.put( linkType, perLinkTypeTempData );
			}
			
			for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedLinksPerForSearch ) {
				WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
				int reportedPeptideId = webReportedPeptide.getReportedPeptideId();
				int unifiedReportedPeptideId = webReportedPeptide.getUnifiedReportedPeptideId();
				
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
				Step_1_ResultsPerSearchIdPerLinkTypeTempData perLinkTypeTempData = perLinkTypeTempData_ByLinkType.get( linkType );
				if ( perLinkTypeTempData == null ) {
					String msg = "In updating for link type, link type not found: " + linkType;
					log.error( msg );
					throw new Exception(msg);
				}
				
				//  Get Dynamic mods for this unified reported peptide id
				
				Set<Double> dynamicMods = new HashSet<>();

				List<UnifiedRepPepMatchedPeptideLookupDTO> unifiedRpMatchedPeptideDTOList =
						UnifiedRepPepMatchedPeptideLookupDAO.getInstance()
						.getUnifiedRpMatchedPeptideDTOForUnifiedReportedPeptideId( unifiedReportedPeptideId );

				for ( UnifiedRepPepMatchedPeptideLookupDTO unifiedRepPepMatchedPeptideLookupDTO : unifiedRpMatchedPeptideDTOList ) {
				
					List<UnifiedRepPepDynamicModLookupDTO> unifiedRpDynamicModList = 
							UnifiedRepPepDynamicModLookupDAO.getInstance()
							.getUnifiedRpDynamicModDTOForMatchedPeptideId( unifiedRepPepMatchedPeptideLookupDTO.getId() );

					for ( UnifiedRepPepDynamicModLookupDTO unifiedRpDynamicMod : unifiedRpDynamicModList ) {
						dynamicMods.add( unifiedRpDynamicMod.getMass() );
					}
				}
				
				int psmCountForReportedPeptide = webReportedPeptide.getNumPsms();
				
				//  Update perLinkTypeTempData for this reported peptide entry
				perLinkTypeTempData.psmCount += psmCountForReportedPeptide;
				
				if ( dynamicMods.isEmpty() ) {
					perLinkTypeTempData.psmCountNoMods += psmCountForReportedPeptide;
				} else {
					Map<Double, MutableInt> modCountMappedOnModMass = perLinkTypeTempData.modCountMappedOnModMass;

					for ( Double dynamicMod : dynamicMods ) {
						MutableInt psmCountForModMass = modCountMappedOnModMass.get( dynamicMod );
						if ( psmCountForModMass == null ) {
							psmCountForModMass = new MutableInt( psmCountForReportedPeptide );
							modCountMappedOnModMass.put( dynamicMod, psmCountForModMass );
						} else {
							psmCountForModMass.add( psmCountForReportedPeptide );
						}
					}
				}
			}

			Step_1_ResultsPerSearchIdTempData perSearchIdTempData = new Step_1_ResultsPerSearchIdTempData();
			perSearchIdTempData.perLinkTypeTempData_ByLinkType = perLinkTypeTempData_ByLinkType;
			
			perSearchIdTempData_BySearchId.put(searchId, perSearchIdTempData );
		}

		
		Map<String,Step_2_ResultsPer_LinkType_TempData> step_2_ResultsPer_LinkType__perLinkType = 
				createStep_2_Result( perSearchIdTempData_BySearchId );


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
		
		QC_PSM_CountsPerModification_Merged_Results result = 
				createFinalOutputResult( step_2_ResultsPer_LinkType__perLinkType, 
						searches, 
						linkTypesDisplayOrderList, 
						dynamicModsFilterSelectionFromUserPreprocessingResult );
		
		result.setSearchIds( searchIdsListDeduppedSorted );
		result.setFoundData( foundData );
		
		return result;
	}
	
	///////////////////////////////////////
	
	//   Create Step_2_ result

	/**
	 * @param searches
	 * @param perSearchIdTempData_BySearchId
	 * @param linkTypesDisplayOrderList
	 * @return
	 * @throws ProxlWebappInternalErrorException
	 */
	private Map<String,Step_2_ResultsPer_LinkType_TempData>   createStep_2_Result ( 
			Map<Integer, Step_1_ResultsPerSearchIdTempData> perSearchIdTempData_BySearchId ) throws ProxlWebappInternalErrorException {

		Map<String,Step_2_ResultsPer_LinkType_TempData> step_2_ResultsPer_LinkType__perLinkType = new HashMap<>();
		
		for ( Map.Entry<Integer, Step_1_ResultsPerSearchIdTempData> perSearchIdTempData_BySearchIdEntry : perSearchIdTempData_BySearchId.entrySet() ) {
			
			Integer searchId = perSearchIdTempData_BySearchIdEntry.getKey();
			Step_1_ResultsPerSearchIdTempData step_1_ResultsPerSearchIdTempData = perSearchIdTempData_BySearchIdEntry.getValue();
			
			Map<String,Step_1_ResultsPerSearchIdPerLinkTypeTempData> perLinkTypeTempData_ByLinkType =
					step_1_ResultsPerSearchIdTempData.perLinkTypeTempData_ByLinkType;
			
			for ( Map.Entry<String,Step_1_ResultsPerSearchIdPerLinkTypeTempData> perLinkTypeTempData_ByLinkTypeEntry : perLinkTypeTempData_ByLinkType.entrySet() ) {
				String linkType = perLinkTypeTempData_ByLinkTypeEntry.getKey();
				Step_1_ResultsPerSearchIdPerLinkTypeTempData step_1_ResultsPerSearchIdPerLinkTypeTempData = perLinkTypeTempData_ByLinkTypeEntry.getValue();
				Map<Double, MutableInt> modCountMappedOnModMass = step_1_ResultsPerSearchIdPerLinkTypeTempData.modCountMappedOnModMass;
				
				Step_2_ResultsPer_LinkType_TempData step_2_ResultsPer_LinkType_TempData = step_2_ResultsPer_LinkType__perLinkType.get( linkType );
				if ( step_2_ResultsPer_LinkType_TempData == null ) {
					step_2_ResultsPer_LinkType_TempData = new Step_2_ResultsPer_LinkType_TempData();
					step_2_ResultsPer_LinkType__perLinkType.put( linkType, step_2_ResultsPer_LinkType_TempData );
				}
				
				step_2_ResultsPer_LinkType_TempData.totalCount_BySearchId.put( searchId, step_1_ResultsPerSearchIdPerLinkTypeTempData.psmCount ); // totalCount
				
				Map<Step_2_ResultsPer_ModMass_TempData,Step_2_ResultsPer_ModMass_TempData> perModMassTempData_ByModMass =
						step_2_ResultsPer_LinkType_TempData.perModMassTempData_ByModMass;
				
				if ( step_1_ResultsPerSearchIdPerLinkTypeTempData.psmCountNoMods > 0 ) {
					//  Add No Mods entry 
					addStep_2_Result_PerModMassEntry_PerSearchIdEntry(
							step_1_ResultsPerSearchIdPerLinkTypeTempData.psmCountNoMods, // count
							true,  //  itemForNoMods
							null, // modMass
							searchId,
							step_1_ResultsPerSearchIdPerLinkTypeTempData, 
							perModMassTempData_ByModMass );
					step_2_ResultsPer_LinkType_TempData.foundDataForLinkType = true;
				}
				
				for ( Map.Entry<Double, MutableInt> modCountMappedOnModMassEntry : modCountMappedOnModMass.entrySet() ) {
					Double modMass = modCountMappedOnModMassEntry.getKey();
					int count = modCountMappedOnModMassEntry.getValue().intValue();
					//  Add entry for mod mass and count 
					addStep_2_Result_PerModMassEntry_PerSearchIdEntry(
							count, // count
							false,  //  itemForNoMods
							modMass, // modMass
							searchId,
							step_1_ResultsPerSearchIdPerLinkTypeTempData, 
							perModMassTempData_ByModMass );
					if ( count > 0 ) {
						step_2_ResultsPer_LinkType_TempData.foundDataForLinkType = true;
					}
				}
				
			}
		}

		return step_2_ResultsPer_LinkType__perLinkType;
	}
	
	/**
	 * @param itemForNoMods
	 * @param modMass
	 * @param searchId
	 * @param step_1_ResultsPerSearchIdPerLinkTypeTempData
	 * @param perModMassTempData_ByModMass
	 */
	public void addStep_2_Result_PerModMassEntry_PerSearchIdEntry(
			int count,
			boolean itemForNoMods,
			Double modMass,
			Integer searchId,
			Step_1_ResultsPerSearchIdPerLinkTypeTempData step_1_ResultsPerSearchIdPerLinkTypeTempData,
			Map<Step_2_ResultsPer_ModMass_TempData, Step_2_ResultsPer_ModMass_TempData> perModMassTempData_ByModMass ) {
		
		// object to use as key for .get(...)
		Step_2_ResultsPer_ModMass_TempData step_2_ResultsPer_ModMass_TempData_FOR_GET = new Step_2_ResultsPer_ModMass_TempData();
		step_2_ResultsPer_ModMass_TempData_FOR_GET.itemForNoMods = itemForNoMods;
		step_2_ResultsPer_ModMass_TempData_FOR_GET.modMass = modMass;
		// object returned from Map
		Step_2_ResultsPer_ModMass_TempData step_2_ResultsPer_ModMass_TempData_FROM_MAP =  
				perModMassTempData_ByModMass.get( step_2_ResultsPer_ModMass_TempData_FOR_GET );
		if ( step_2_ResultsPer_ModMass_TempData_FROM_MAP == null ) {
			//  Re-use object for get to put in the map
			step_2_ResultsPer_ModMass_TempData_FROM_MAP = step_2_ResultsPer_ModMass_TempData_FOR_GET;
			perModMassTempData_ByModMass.put( step_2_ResultsPer_ModMass_TempData_FOR_GET, step_2_ResultsPer_ModMass_TempData_FROM_MAP );
			step_2_ResultsPer_ModMass_TempData_FROM_MAP.perSearchIdTempData_BySearchId = new HashMap<>();
		}
		Step_2_ResultsPer_SearchId_TempData step_2_ResultsPer_SearchId_TempData = new Step_2_ResultsPer_SearchId_TempData();
		step_2_ResultsPer_ModMass_TempData_FROM_MAP.perSearchIdTempData_BySearchId.put( searchId, step_2_ResultsPer_SearchId_TempData );
		step_2_ResultsPer_SearchId_TempData.count = count;
	}
	
	
	
	///////////////////////////////////////
	
	//   Create final output result

	/**
	 * @param step_2_ResultsPer_LinkType__perLinkType
	 * @param searches
	 * @param linkTypesDisplayOrderList
	 * @param dynamicModsFilterSelectionFromUserPreprocessingResult
	 * @return
	 * @throws ProxlWebappInternalErrorException
	 */
	private QC_PSM_CountsPerModification_Merged_Results   createFinalOutputResult ( 
			Map<String,Step_2_ResultsPer_LinkType_TempData> step_2_ResultsPer_LinkType__perLinkType,
			List<SearchDTO> searches,
			List<String> linkTypesDisplayOrderList,
			DynamicModsFilterSelectionFromUserPreprocessingResult dynamicModsFilterSelectionFromUserPreprocessingResult ) throws ProxlWebappInternalErrorException {
		
		List<QC_PSM_CountsPerModificationResultsPerLinkType_Merged> resultsPerLinkTypeList = new ArrayList<>( linkTypesDisplayOrderList.size() );

		for ( String linkType : linkTypesDisplayOrderList ) {
			
			List<QC_PSM_CountsPerModificationResultsPerModMass_Merged> resultsPerModMassList = new ArrayList<>( dynamicModsFilterSelectionFromUserPreprocessingResult.getModMassSelectionsWithoutNoModsDouble().size() + 1 );
			
			Step_2_ResultsPer_LinkType_TempData perLinkTypeTempData = step_2_ResultsPer_LinkType__perLinkType.get( linkType );
			if ( perLinkTypeTempData == null ) {
				String msg = "Internal error, linkType not found in step_2_ResultsPer_LinkType__perLinkType. linkType: " + linkType;
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			
			Map<Step_2_ResultsPer_ModMass_TempData,Step_2_ResultsPer_ModMass_TempData> perModMassTempData_ByModMass = perLinkTypeTempData.perModMassTempData_ByModMass;
			Map<Integer,Integer> totalCountPerLinkType_BySearchId = perLinkTypeTempData.totalCount_BySearchId;
		
			if ( dynamicModsFilterSelectionFromUserPreprocessingResult.isModMassSelectionsIncludesNoModifications() ) {
				//  Create entry for no modification
				QC_PSM_CountsPerModificationResultsPerModMass_Merged result_perModMass = 
						finalResult_Create_PerModMass_PerSearchId(
								searches, 
								perModMassTempData_ByModMass, 
								totalCountPerLinkType_BySearchId, 
								true, // itemForNoMods 
								null // modMass
								);
				resultsPerModMassList.add( result_perModMass );
			}
			
			//  Add entry for each Mod mass in user selection
			for ( Double modMass : dynamicModsFilterSelectionFromUserPreprocessingResult.getModMassSelectionsWithoutNoModsDouble() ) {
				
				QC_PSM_CountsPerModificationResultsPerModMass_Merged result_perModMass = 
						finalResult_Create_PerModMass_PerSearchId(
								searches, 
								perModMassTempData_ByModMass, 
								totalCountPerLinkType_BySearchId, 
								false, // itemForNoMods 
								modMass // modMass
								);
				resultsPerModMassList.add( result_perModMass );
			}
			
			QC_PSM_CountsPerModificationResultsPerLinkType_Merged result_perLinkType = new QC_PSM_CountsPerModificationResultsPerLinkType_Merged();
			resultsPerLinkTypeList.add( result_perLinkType );
			result_perLinkType.setLinkType( linkType );
			result_perLinkType.setResultsPerModMassList( resultsPerModMassList );
			result_perLinkType.setFoundDataForLinkType( perLinkTypeTempData.foundDataForLinkType );
		}
		QC_PSM_CountsPerModification_Merged_Results result = new QC_PSM_CountsPerModification_Merged_Results();

		result.setResultsPerLinkTypeList( resultsPerLinkTypeList );
		
		return result;
	}
	
	/**
	 * @param searches
	 * @param perModMassTempData_ByModMass
	 * @param totalCountPerLinkType_BySearchId
	 * @param itemForNoMods
	 * @param modMass
	 * @return
	 */
	public QC_PSM_CountsPerModificationResultsPerModMass_Merged finalResult_Create_PerModMass_PerSearchId(
			List<SearchDTO> searches,
			Map<Step_2_ResultsPer_ModMass_TempData,
			Step_2_ResultsPer_ModMass_TempData> perModMassTempData_ByModMass,
			Map<Integer, Integer> totalCountPerLinkType_BySearchId, 
			boolean itemForNoMods,
			Double modMass ) {
		
		String modMassLabel = null;
		if ( modMass != null ) {
			modMassLabel = modMass.toString();
		}
		
		QC_PSM_CountsPerModificationResultsPerModMass_Merged perModMass = new QC_PSM_CountsPerModificationResultsPerModMass_Merged();
		perModMass.setNoModMass( itemForNoMods );
		perModMass.setModMassLabel( modMassLabel );
		
		Map<Integer, QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged> countPerSearchIdMap_KeyProjectSearchId = new HashMap<>();
		perModMass.setCountPerSearchIdMap_KeyProjectSearchId( countPerSearchIdMap_KeyProjectSearchId );
		
		// object to use as key for .get(...)
		Step_2_ResultsPer_ModMass_TempData step_2_ResultsPer_ModMass_TempData_FOR_GET = new Step_2_ResultsPer_ModMass_TempData();
		step_2_ResultsPer_ModMass_TempData_FOR_GET.itemForNoMods = itemForNoMods;
		step_2_ResultsPer_ModMass_TempData_FOR_GET.modMass = modMass;
		// object returned from Map
		Step_2_ResultsPer_ModMass_TempData step_2_ResultsPer_ModMass_TempData_FROM_MAP =  
				perModMassTempData_ByModMass.get( step_2_ResultsPer_ModMass_TempData_FOR_GET );
		
		if ( step_2_ResultsPer_ModMass_TempData_FROM_MAP != null ) {
			Map<Integer,Step_2_ResultsPer_SearchId_TempData> perSearchIdTempData_BySearchId =
					step_2_ResultsPer_ModMass_TempData_FROM_MAP.perSearchIdTempData_BySearchId;
			for ( SearchDTO search : searches ) {
				Integer searchId = search.getSearchId();
				Integer projectSearchId = search.getProjectSearchId();

				Integer totalCountPerLinkType = totalCountPerLinkType_BySearchId.get( searchId );
				
				QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged per_ModMass_SearchId = new QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged();
				per_ModMass_SearchId.setSearchId( searchId  );
				if ( totalCountPerLinkType != null ) {
					per_ModMass_SearchId.setTotalCount( totalCountPerLinkType );
				}
				Step_2_ResultsPer_SearchId_TempData step_2_ResultsPer_SearchId_TempData = perSearchIdTempData_BySearchId.get( searchId );
				if ( step_2_ResultsPer_SearchId_TempData != null ) {
					per_ModMass_SearchId.setCount( step_2_ResultsPer_SearchId_TempData.count );
				}
				countPerSearchIdMap_KeyProjectSearchId.put( projectSearchId, per_ModMass_SearchId );
			}
		} else {
			//  Create entries for all search ids with count zero
			for ( SearchDTO search : searches ) {
				Integer searchId = search.getSearchId();
				Integer projectSearchId = search.getProjectSearchId();

				Integer totalCountPerLinkType = totalCountPerLinkType_BySearchId.get( searchId );
				
				QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged per_ModMass_SearchId = new QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged();
				per_ModMass_SearchId.setSearchId( searchId  );
				if ( totalCountPerLinkType != null ) {
					per_ModMass_SearchId.setTotalCount( totalCountPerLinkType );
				}
				per_ModMass_SearchId.setCount( 0 );
				countPerSearchIdMap_KeyProjectSearchId.put( projectSearchId, per_ModMass_SearchId );
			}
		}
		
		return perModMass;
	}
	
	

	///////////////////
	
	///////  Step_1_ results:  Used for storing results from step_1_ processing of query results
	
	/**
	 * Used for storing results from step_1_ processing of query results per search id
	 *
	 */
	private static class Step_1_ResultsPerSearchIdTempData {
		Map<String,Step_1_ResultsPerSearchIdPerLinkTypeTempData> perLinkTypeTempData_ByLinkType;
	}
	
	/**
	 * Used for storing results from step_1_ processing of query results per search id, per link type
	 *
	 */
	private static class Step_1_ResultsPerSearchIdPerLinkTypeTempData {
		
		int psmCount = 0;
		int psmCountNoMods = 0;
		Map<Double, MutableInt> modCountMappedOnModMass = new HashMap<>();
	}
	

	///////////////////
	
	///////  Step_2_ results:  Used for storing results from combining results by link type, mod mass, then search id
	
	/**
	 * Used for storing results from step_2_ processing of step_1_ results, data by link type
	 *
	 */
	private static class Step_2_ResultsPer_LinkType_TempData {
		Map<Step_2_ResultsPer_ModMass_TempData,Step_2_ResultsPer_ModMass_TempData> perModMassTempData_ByModMass = new HashMap<>();
		Map<Integer,Integer> totalCount_BySearchId = new HashMap<>();
		boolean foundDataForLinkType;
	}

	/**
	 * Used for storing results from step_2_ processing of step_1_ results, data by Mod Mass
	 *
	 */
	private static class Step_2_ResultsPer_ModMass_TempData {
		boolean itemForNoMods; // Used in equals and hashcode
		Double modMass;        // Used in equals and hashcode
		Map<Integer,Step_2_ResultsPer_SearchId_TempData> perSearchIdTempData_BySearchId;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (itemForNoMods ? 1231 : 1237);
			result = prime * result + ((modMass == null) ? 0 : modMass.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Step_2_ResultsPer_ModMass_TempData other = (Step_2_ResultsPer_ModMass_TempData) obj;
			if (itemForNoMods != other.itemForNoMods)
				return false;
			if (modMass == null) {
				if (other.modMass != null)
					return false;
			} else if (!modMass.equals(other.modMass))
				return false;
			return true;
		}
		
	}

	/**
	 * Used for storing results from step_2_ processing of step_1_ results, data by search id
	 *
	 */
	private static class Step_2_ResultsPer_SearchId_TempData {
		int count;
	}
	

}
