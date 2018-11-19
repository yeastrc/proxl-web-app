package org.yeastrc.xlink.www.qc_data.modification_statistics.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.log4j.Logger;
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
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.qc_data.modification_statistics.objects.QC_PSM_CountsPerModificationResults;
import org.yeastrc.xlink.www.qc_data.modification_statistics.objects.QC_PSM_CountsPerModificationResults.QC_PSM_CountsPerModificationResultsPerLinkType;
import org.yeastrc.xlink.www.qc_data.modification_statistics.objects.QC_PSM_CountsPerModificationResults.QC_PSM_CountsPerModificationResultsPerModification;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;
import org.yeastrc.xlink.www.web_utils.DynamicModsFilterSelectionFromUserPreprocessing;
import org.yeastrc.xlink.www.web_utils.DynamicModsFilterSelectionFromUserPreprocessing.DynamicModsFilterSelectionFromUserPreprocessingResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class QC_PSM_CountsPerModification {

	private static final Logger log = Logger.getLogger(QC_PSM_CountsPerModification.class);
	
	/**
	 * private constructor
	 */
	private QC_PSM_CountsPerModification(){}
	public static QC_PSM_CountsPerModification getInstance( ) throws Exception {
		QC_PSM_CountsPerModification instance = new QC_PSM_CountsPerModification();
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
	public QC_PSM_CountsPerModificationResults getQC_PSM_CountsPerModification( 
			String filterCriteriaJSON, 
			List<Integer> projectSearchIdsListDeduppedSorted,
			List<SearchDTO> searches, 
			Map<Integer, SearchDTO> searchesMapOnSearchId ) throws Exception {

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
				PerLinkTypeTempData perLinkTypeTempData = perLinkTypeTempData_ByLinkType.get( linkType );
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
		}

		
//		//  copy map to array for output, in a specific order
		List<QC_PSM_CountsPerModificationResultsPerLinkType> resultsPerLinkTypeList = new ArrayList<>( perLinkTypeTempData_ByLinkType.size() );
		createReultObjectPerLinkTypeAndAddToOutputListForLinkType( XLinkUtils.CROSS_TYPE_STRING, resultsPerLinkTypeList, perLinkTypeTempData_ByLinkType, dynamicModsFilterSelectionFromUserPreprocessingResult );
		createReultObjectPerLinkTypeAndAddToOutputListForLinkType( XLinkUtils.LOOP_TYPE_STRING, resultsPerLinkTypeList, perLinkTypeTempData_ByLinkType, dynamicModsFilterSelectionFromUserPreprocessingResult );
		createReultObjectPerLinkTypeAndAddToOutputListForLinkType( XLinkUtils.UNLINKED_TYPE_STRING, resultsPerLinkTypeList, perLinkTypeTempData_ByLinkType, dynamicModsFilterSelectionFromUserPreprocessingResult );
		
		QC_PSM_CountsPerModificationResults result = new QC_PSM_CountsPerModificationResults();
		result.setResultsPerLinkTypeList( resultsPerLinkTypeList );
		
		return result;
	}
	
	
	/**
	 * @param linkType
	 * @param resultsPerLinkTypeList
	 * @param perLinkTypeTempData_ByLinkType
	 */
	private void createReultObjectPerLinkTypeAndAddToOutputListForLinkType( 
			String linkType, 
			List<QC_PSM_CountsPerModificationResultsPerLinkType> resultsPerLinkTypeList , 
			Map<String,PerLinkTypeTempData> perLinkTypeTempData_ByLinkType,
			DynamicModsFilterSelectionFromUserPreprocessingResult dynamicModsFilterSelectionFromUserPreprocessingResult ) {
		
		PerLinkTypeTempData perLinkTypeTempData = perLinkTypeTempData_ByLinkType.get( linkType );
		if ( perLinkTypeTempData != null ) {
			QC_PSM_CountsPerModificationResultsPerLinkType qc_SummaryCountsResultsPerLinkType = new QC_PSM_CountsPerModificationResultsPerLinkType();
			qc_SummaryCountsResultsPerLinkType.setLinkType( linkType );
			qc_SummaryCountsResultsPerLinkType.setTotalPSMCountForLinkType( perLinkTypeTempData.psmCount );
			
			if ( dynamicModsFilterSelectionFromUserPreprocessingResult.isModMassSelectionsIncludesNoModifications() ) {
				qc_SummaryCountsResultsPerLinkType.setPsmCountNoMods( perLinkTypeTempData.psmCountNoMods );
			}
			
			List<Double> modMassSelectionsWithoutNoMods = dynamicModsFilterSelectionFromUserPreprocessingResult.getModMassSelectionsWithoutNoModsDouble();
			
			if ( modMassSelectionsWithoutNoMods != null ) {

				List<QC_PSM_CountsPerModificationResultsPerModification> countPerModMassList = new ArrayList<>( modMassSelectionsWithoutNoMods.size() + 1 );
				
				// Copy count per mod mass
				Map<Double, MutableInt> modCountMappedOnModMass = perLinkTypeTempData.modCountMappedOnModMass;
			
				for ( Double modMassSelection : modMassSelectionsWithoutNoMods ) {

					QC_PSM_CountsPerModificationResultsPerModification perModEntry = new QC_PSM_CountsPerModificationResultsPerModification();
					perModEntry.setLabel( modMassSelection.toString() );
			
					MutableInt count = modCountMappedOnModMass.get( modMassSelection );
					if ( count != null ) {
						perModEntry.setCount( count.intValue() );
					}
					countPerModMassList.add( perModEntry );
				}
				qc_SummaryCountsResultsPerLinkType.setCountPerModMassList( countPerModMassList );
			}

			resultsPerLinkTypeList.add( qc_SummaryCountsResultsPerLinkType );
		}
	}
	
	/**
	 * 
	 *
	 */
	private class PerLinkTypeTempData {
		
		int psmCount = 0;
		int psmCountNoMods = 0;
		Map<Double, MutableInt> modCountMappedOnModMass = new HashMap<>();
	}

}
