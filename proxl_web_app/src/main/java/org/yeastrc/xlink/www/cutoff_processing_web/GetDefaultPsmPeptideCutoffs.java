package org.yeastrc.xlink.www.cutoff_processing_web;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.dto.CutoffsAppliedOnImportDTO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDBDataOutOfSyncException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesAnnotationLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.project_level_default_cutoffs.ProjectLevelDefaultCutoffs_Cache;
import org.yeastrc.xlink.www.project_level_default_cutoffs.ProjectLevelDefaultCutoffs_Cache.ProjectLevelDefaultCutoffs_Cache_Result;
import org.yeastrc.xlink.www.project_level_default_cutoffs.ProjectLevelDefaultCutoffs_Cache.ProjectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName;
import org.yeastrc.xlink.www.project_level_default_cutoffs.ProjectLevelDefaultCutoffs_Cache.ProjectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName;
import org.yeastrc.xlink.www.project_level_default_cutoffs.ProjectLevelDefaultCutoffs_Cache.ProjectLevelDefaultCutoffs_Cache_Result_Per_Type;
import org.yeastrc.xlink.www.search_programs_per_search_utils.GetSearchProgramsPerSearchData;
import org.yeastrc.xlink.www.searcher.CutoffsAppliedOnImportSearcher;
/**
 * 
 *
 */
public class GetDefaultPsmPeptideCutoffs {
	
	private static final Logger log = LoggerFactory.getLogger(  GetDefaultPsmPeptideCutoffs.class );
	
	//  private constructor
	private GetDefaultPsmPeptideCutoffs() { }
	/**
	 * @return newly created instance
	 */
	public static GetDefaultPsmPeptideCutoffs getInstance() { 
		return new GetDefaultPsmPeptideCutoffs(); 
	}
	
	/**
	 * Get CutoffValuesRootLevel Object for defaults
	 * @param projectId 
	 * @param projectSearchIds
	 * @return
	 * @throws Exception
	 */
	public CutoffValuesRootLevel getDefaultPsmPeptideCutoffs(
			int projectId,
			Collection<Integer> projectSearchIds,
			Collection<Integer> searchIds, Map<Integer,Integer> mapProjectSearchIdToSearchId
			) throws Exception {

		//  Retrieve this here so only retrieve once
		ProjectLevelDefaultCutoffs_Cache_Result projectLevelDefaultCutoffs_Cache_Result =
				ProjectLevelDefaultCutoffs_Cache.getSingletonInstance().getDefaultCutoffs_ForProjectId( projectId );
		
		ProjectLevelDefaultCutoffs_Cache_Result_Per_Type reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type = projectLevelDefaultCutoffs_Cache_Result.getReportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type();
		ProjectLevelDefaultCutoffs_Cache_Result_Per_Type psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type = projectLevelDefaultCutoffs_Cache_Result.getPsm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type();
		
		//  Get Annotation Type records for PSM and Peptide
		//  Get  Annotation Type records for PSM
		Map<Integer, Map<Integer, AnnotationTypeDTO>> 
		psm_AnnotationType_DTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIds );
		//  Get  Annotation Type records for Reported Peptides
		Map<Integer, Map<Integer, AnnotationTypeDTO>> 
		reportedPeptide_AnnotationType_DTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIds );
		
		//  Build cutoffValuesRootLevel
		CutoffValuesRootLevel cutoffValuesRootLevel =  new CutoffValuesRootLevel();
		Map<String, CutoffValuesSearchLevel> cutoffValuesSearchesMap = cutoffValuesRootLevel.getSearches();
		
		
		//  Cache retrieved SearchProgramsPerSearchDTO for duration of this call
		Map<Integer, SearchProgramsPerSearchDTO> searchProgramsPerSearchDTO_Map_KeyId = new HashMap<>();

		
		for ( Map.Entry<Integer,Integer> entry : mapProjectSearchIdToSearchId.entrySet() ) {
			
			Integer projectSearchId = entry.getKey();
			Integer searchId = entry.getValue();
			
			Map<Integer, AnnotationTypeDTO> reportedPeptide_AnnotationType_DTOMap = 
					reportedPeptide_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			Map<Integer, AnnotationTypeDTO> psm_AnnotationType_DTOMap = 
					psm_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			
			if ( reportedPeptide_AnnotationType_DTOMap != null || psm_AnnotationType_DTOMap != null ) {
				
				CutoffValuesSearchLevel cutoffValuesSearchLevelEntry = new CutoffValuesSearchLevel();
				
				cutoffValuesSearchesMap.put( projectSearchId.toString(), cutoffValuesSearchLevelEntry );
				
				cutoffValuesSearchLevelEntry.setProjectSearchId( projectSearchId );
				
				// Get the searchProgramsPerSearchIds for Search Programs that will be using Project Level Default Cutoff Values
				
				Set<Integer> searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs = 
						get_searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs( 
								reportedPeptide_AnnotationType_DTOMap, 
								psm_AnnotationType_DTOMap, 
								searchProgramsPerSearchDTO_Map_KeyId, 
								projectLevelDefaultCutoffs_Cache_Result );
										
				//  Get Cutoff on Import records (Not All Records were imported)
				List<CutoffsAppliedOnImportDTO> cutoffsAppliedOnImportList = 
						CutoffsAppliedOnImportSearcher.getInstance().getCutoffsAppliedOnImportDTOForSearchId( searchId );
				
				Map<Integer, CutoffsAppliedOnImportDTO> cutoffsAppliedOnImportKeyedAnnTypeId = new HashMap<>();
				
				for ( CutoffsAppliedOnImportDTO cutoffsAppliedOnImport : cutoffsAppliedOnImportList ) {
					cutoffsAppliedOnImportKeyedAnnTypeId.put( cutoffsAppliedOnImport.getAnnotationTypeId(), cutoffsAppliedOnImport );
				}
				if ( reportedPeptide_AnnotationType_DTOMap != null ) {
					Map<String,CutoffValuesAnnotationLevel> cutoffValuesMap = cutoffValuesSearchLevelEntry.getPeptideCutoffValues();
					processPeptidesOrPSM( 
							reportedPeptide_AnnotationType_DTOMap,
							cutoffsAppliedOnImportKeyedAnnTypeId,
							cutoffValuesMap,
							searchProgramsPerSearchDTO_Map_KeyId,
							reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type,
							searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs );
				}
				if ( psm_AnnotationType_DTOMap != null ) {
					Map<String,CutoffValuesAnnotationLevel> cutoffValuesMap = cutoffValuesSearchLevelEntry.getPsmCutoffValues();
					processPeptidesOrPSM( 
							psm_AnnotationType_DTOMap,
							cutoffsAppliedOnImportKeyedAnnTypeId,
							cutoffValuesMap,
							searchProgramsPerSearchDTO_Map_KeyId,
							psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type,
							searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs );
				}
			}
		}
		return cutoffValuesRootLevel;
	}

	/**
	 * @param annotationType_DTOMap
	 * @param cutoffsAppliedOnImportKeyedAnnTypeId
	 * @param cutoffValuesMap
	 * @throws Exception
	 */
	private void processPeptidesOrPSM( 
			Map<Integer, AnnotationTypeDTO> annotationType_DTOMap,
			Map<Integer, CutoffsAppliedOnImportDTO> cutoffsAppliedOnImportKeyedAnnTypeId,
			Map<String,CutoffValuesAnnotationLevel> cutoffValuesMap,
			Map<Integer, SearchProgramsPerSearchDTO> searchProgramsPerSearchDTO_Map_KeyId,
			ProjectLevelDefaultCutoffs_Cache_Result_Per_Type projectLevelDefaultCutoffs_Cache_Result_Per_Type,
			Set<Integer> searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs
			) throws Exception {
		
		//  First process Project Level Cutoffs.
		//  searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs:
		//		The searchProgramsPerSearchId that matched the ProjectLevelCutoffs
		//      and thus will be skipped in the processing in this method
		processPeptidesOrPSM_ApplyProjectLevelCutoffs( 
				annotationType_DTOMap, 
				cutoffsAppliedOnImportKeyedAnnTypeId, 
				cutoffValuesMap, 
				searchProgramsPerSearchDTO_Map_KeyId, 
				projectLevelDefaultCutoffs_Cache_Result_Per_Type,
				searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs );
		
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry :  annotationType_DTOMap.entrySet() ) {
			
			AnnotationTypeDTO annotationTypeDTO = entry.getValue();
			
			int searchProgramsPerSearchId = annotationTypeDTO.getSearchProgramsPerSearchId();
			if ( searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs.contains( searchProgramsPerSearchId ) ) {
				
				//  Have Project Level cutoffs for this searchProgramsPerSearchId ( Already Processed in processPeptidesOrPSM_ApplyProjectLevelCutoffs(...) )
				//    so skip the search level default cutoffs for this searchProgramsPerSearchId
				
				continue; // EARLY CONTINUE
			}

			AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();
			if ( annotationTypeFilterableDTO == null ) {
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + annotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			//  Get Cutoffs Applied at Import (Not all data was imported if not null)
			CutoffsAppliedOnImportDTO cutoffsAppliedOnImportDTO = null;
			if ( cutoffsAppliedOnImportKeyedAnnTypeId != null && ( ! cutoffsAppliedOnImportKeyedAnnTypeId.isEmpty() ) ) {
				cutoffsAppliedOnImportDTO = cutoffsAppliedOnImportKeyedAnnTypeId.get( annotationTypeDTO.getId() );
			}
			
			if ( annotationTypeFilterableDTO.isDefaultFilter() ) {
				int typeId = annotationTypeDTO.getId();
				String typeIdString = Integer.toString( typeId );
				CutoffValuesAnnotationLevel cutoffValuesEntryForTypeId = new CutoffValuesAnnotationLevel();
				cutoffValuesMap.put( typeIdString, cutoffValuesEntryForTypeId );
				cutoffValuesEntryForTypeId.setId(typeId);
				
				//  First set result cutoff to Ann Type Default Cutoff
				cutoffValuesEntryForTypeId.setValue( annotationTypeFilterableDTO.getDefaultFilterValueString() );
				
				if ( cutoffsAppliedOnImportDTO != null ) {
					//  Have Cutoff Applied on Import so use that instead if "More Restrictive"
					
					if ( annotationTypeFilterableDTO.getFilterDirectionType() == FilterDirectionType.ABOVE ) {
						if ( cutoffsAppliedOnImportDTO.getCutoffValueDouble() > annotationTypeFilterableDTO.getDefaultFilterValue() ) {
							cutoffValuesEntryForTypeId.setValue( cutoffsAppliedOnImportDTO.getCutoffValueString() );
						}
					} else {
						if ( cutoffsAppliedOnImportDTO.getCutoffValueDouble() < annotationTypeFilterableDTO.getDefaultFilterValue() ) {
							cutoffValuesEntryForTypeId.setValue( cutoffsAppliedOnImportDTO.getCutoffValueString() );
						}
					}
				}
			} else {
				if ( cutoffsAppliedOnImportDTO != null ) {
					//  Have Cutoff Applied on Import and Ann Type not Default filter so use Cutoff Applied on Import
					
					int typeId = annotationTypeDTO.getId();
					String typeIdString = Integer.toString( typeId );
					CutoffValuesAnnotationLevel cutoffValuesEntryForTypeId = new CutoffValuesAnnotationLevel();
					cutoffValuesMap.put( typeIdString, cutoffValuesEntryForTypeId );
					cutoffValuesEntryForTypeId.setId(typeId);
					cutoffValuesEntryForTypeId.setValue( cutoffsAppliedOnImportDTO.getCutoffValueString() );
				}
			}
		}
	}
	
	/////////////////////
	
	//  Cutoffs at Project Level

	/**
	 * @param annotationType_DTOMap
	 * @param cutoffsAppliedOnImportKeyedAnnTypeId
	 * @param cutoffValuesMap
	 * @param searchProgramsPerSearchDTO_Map_KeyId
	 * @param projectLevelDefaultCutoffs_Cache_Result_Per_Type
	 * @return Set of SearchProgramsPerSearchId that match the ProjectLevelCutoffs
	 * @throws Exception
	 */
	private void processPeptidesOrPSM_ApplyProjectLevelCutoffs( 
			Map<Integer, AnnotationTypeDTO> annotationType_DTOMap,
			Map<Integer, CutoffsAppliedOnImportDTO> cutoffsAppliedOnImportKeyedAnnTypeId,
			Map<String,CutoffValuesAnnotationLevel> cutoffValuesMap,
			Map<Integer, SearchProgramsPerSearchDTO> searchProgramsPerSearchDTO_Map_KeyId,
			ProjectLevelDefaultCutoffs_Cache_Result_Per_Type projectLevelDefaultCutoffs_Cache_Result_Per_Type,
			Set<Integer> searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs
			) throws Exception {
		
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry :  annotationType_DTOMap.entrySet() ) {
			
			AnnotationTypeDTO annotationTypeDTO = entry.getValue();
			
			String annotationTypeName = annotationTypeDTO.getName();

			AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();
			if ( annotationTypeFilterableDTO == null ) {
				String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + annotationTypeDTO.getId();
				log.error( msg );
				throw new Exception(msg);
			}
			
			Integer searchProgramsPerSearchId = annotationTypeDTO.getSearchProgramsPerSearchId();

			if ( ! searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs.contains( searchProgramsPerSearchId ) ) {
				//  No entry in Project Level Default Cutoffs for this searchProgramsPerSearchId (Search Program Name) so skip to next AnnotationTypeDTO entry
				
				continue;   // EARLY CONTINUE
			}

			SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = searchProgramsPerSearchDTO_Map_KeyId.get( searchProgramsPerSearchId );
			if ( searchProgramsPerSearchDTO == null ) {
				searchProgramsPerSearchDTO = GetSearchProgramsPerSearchData.getInstance().getSearchProgramsPerSearchDTO( searchProgramsPerSearchId );
				if ( searchProgramsPerSearchDTO == null ) {
					String msg = "No searchProgramsPerSearchDTO record found for searchProgramsPerSearchId: " + searchProgramsPerSearchId;
					log.error( msg );
					throw new ProxlWebappDBDataOutOfSyncException( msg );
				}
				searchProgramsPerSearchDTO_Map_KeyId.put( searchProgramsPerSearchId, searchProgramsPerSearchDTO );
			}
			String searchProgramName = searchProgramsPerSearchDTO.getName();
			
			ProjectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName projectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName =
					projectLevelDefaultCutoffs_Cache_Result_Per_Type.getForSearchProgramName( searchProgramName );
			
			if ( projectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName == null ) {
				//  No entry in Project Level Default Cutoffs for this searchProgramName so skip to next AnnotationTypeDTO entry
				
				continue;   // EARLY CONTINUE
			}

			ProjectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName projectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName =
					projectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName.getForAnnotationTypeName( annotationTypeName );

			//  Get Cutoffs Applied at Import (Not all data was imported if not null)
			CutoffsAppliedOnImportDTO cutoffsAppliedOnImportDTO = null;
			if ( cutoffsAppliedOnImportKeyedAnnTypeId != null && ( ! cutoffsAppliedOnImportKeyedAnnTypeId.isEmpty() ) ) {
				cutoffsAppliedOnImportDTO = cutoffsAppliedOnImportKeyedAnnTypeId.get( annotationTypeDTO.getId() );
			}
			
			if ( projectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName != null ) {
				
				//  Have Project Level Default Cutoff for this Annotation Type Name
				
				double projectLevelDefaultCutoff = projectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName.getProjectLevelCutoff();
				int typeId = annotationTypeDTO.getId();
				String typeIdString = Integer.toString( typeId );
				
				CutoffValuesAnnotationLevel cutoffValuesEntryForTypeId = new CutoffValuesAnnotationLevel();
				cutoffValuesMap.put( typeIdString, cutoffValuesEntryForTypeId );
				cutoffValuesEntryForTypeId.setId(typeId);
				//  First set result cutoff value to projectLevelDefaultCutoff
				cutoffValuesEntryForTypeId.setValue( String.valueOf( projectLevelDefaultCutoff ) );
				if ( cutoffsAppliedOnImportDTO != null ) {
					
					//  Have Cutoff Applied on Import so use that instead if "More Restrictive"
					
					if ( annotationTypeFilterableDTO.getFilterDirectionType() == FilterDirectionType.ABOVE ) {
						if ( cutoffsAppliedOnImportDTO.getCutoffValueDouble() > projectLevelDefaultCutoff ) {
							cutoffValuesEntryForTypeId.setValue( cutoffsAppliedOnImportDTO.getCutoffValueString() );
						}
					} else {
						if ( cutoffsAppliedOnImportDTO.getCutoffValueDouble() < projectLevelDefaultCutoff ) {
							cutoffValuesEntryForTypeId.setValue( cutoffsAppliedOnImportDTO.getCutoffValueString() );
						}
					}
				}
			} else {
				if ( cutoffsAppliedOnImportDTO != null ) {
					//  Have Cutoff Applied on Import and No Project Level Default Cutoff so use Cutoff Applied on Import
					
					int typeId = annotationTypeDTO.getId();
					String typeIdString = Integer.toString( typeId );
					CutoffValuesAnnotationLevel cutoffValuesEntryForTypeId = new CutoffValuesAnnotationLevel();
					cutoffValuesMap.put( typeIdString, cutoffValuesEntryForTypeId );
					cutoffValuesEntryForTypeId.setId(typeId);
					cutoffValuesEntryForTypeId.setValue( cutoffsAppliedOnImportDTO.getCutoffValueString() );
				}
			}
		}
	}


	/**
	 * Get the searchProgramsPerSearchIds for Search Programs that will be using Project Level Default Cutoff Values
	 * 
	 * @param annotationType_DTOMap
	 * @param projectLevelDefaultCutoffs_Cache_Result_Per_Type
	 * @return Set of SearchProgramsPerSearchId that match the ProjectLevelCutoffs
	 * @throws Exception
	 */
	private Set<Integer> get_searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs( 
			Map<Integer, AnnotationTypeDTO> reportedPeptide_AnnotationType_DTOMap,
			Map<Integer, AnnotationTypeDTO> psm_AnnotationType_DTOMap,
			Map<Integer, SearchProgramsPerSearchDTO> searchProgramsPerSearchDTO_Map_KeyId,
			ProjectLevelDefaultCutoffs_Cache_Result projectLevelDefaultCutoffs_Cache_Result
			) throws Exception {
		
		//  Return values
		Set<Integer> searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs = new HashSet<>();
		
		ProjectLevelDefaultCutoffs_Cache_Result_Per_Type reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type = projectLevelDefaultCutoffs_Cache_Result.getReportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type();
		ProjectLevelDefaultCutoffs_Cache_Result_Per_Type psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type = projectLevelDefaultCutoffs_Cache_Result.getPsm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type();
		
		//  Process for Reported Peptide Filters/Cutoffs
		get_searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs_PerType( 
				reportedPeptide_AnnotationType_DTOMap, 
				searchProgramsPerSearchDTO_Map_KeyId, 
				reportedPeptide_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type, 
				searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs );

		//  Process for PSM Filters/Cutoffs
		get_searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs_PerType( 
				psm_AnnotationType_DTOMap, 
				searchProgramsPerSearchDTO_Map_KeyId, 
				psm_ProjectLevelDefaultCutoffs_Cache_Result_Per_Type, 
				searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs );

		return searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs;
	}

	/**
	 * @param annotationType_DTOMap
	 * @param projectLevelDefaultCutoffs_Cache_Result_Per_Type
	 * @return Set of SearchProgramsPerSearchId that match the ProjectLevelCutoffs
	 * @throws Exception
	 */
	private void get_searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs_PerType( 
			Map<Integer, AnnotationTypeDTO> annotationType_DTOMap,
			Map<Integer, SearchProgramsPerSearchDTO> searchProgramsPerSearchDTO_Map_KeyId,
			ProjectLevelDefaultCutoffs_Cache_Result_Per_Type projectLevelDefaultCutoffs_Cache_Result_Per_Type,
			Set<Integer> searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs // Add to this Set in this method
			) throws Exception {
		
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry :  annotationType_DTOMap.entrySet() ) {
			
			AnnotationTypeDTO annotationTypeDTO = entry.getValue();

			String annotationTypeName = annotationTypeDTO.getName();
			
			Integer searchProgramsPerSearchId = annotationTypeDTO.getSearchProgramsPerSearchId();
			SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = searchProgramsPerSearchDTO_Map_KeyId.get( searchProgramsPerSearchId );
			if ( searchProgramsPerSearchDTO == null ) {
				searchProgramsPerSearchDTO = GetSearchProgramsPerSearchData.getInstance().getSearchProgramsPerSearchDTO( searchProgramsPerSearchId );
				if ( searchProgramsPerSearchDTO == null ) {
					String msg = "No searchProgramsPerSearchDTO record found for searchProgramsPerSearchId: " + searchProgramsPerSearchId;
					log.error( msg );
					throw new ProxlWebappDBDataOutOfSyncException( msg );
				}
				searchProgramsPerSearchDTO_Map_KeyId.put( searchProgramsPerSearchId, searchProgramsPerSearchDTO );
			}
			String searchProgramName = searchProgramsPerSearchDTO.getName();
			
			ProjectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName projectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName =
					projectLevelDefaultCutoffs_Cache_Result_Per_Type.getForSearchProgramName( searchProgramName );
			
			if ( projectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName == null ) {
				//  No entry in Project Level Default Cutoffs for this Search Program Name so skip to next AnnotationTypeDTO entry
				
				continue;   // EARLY CONTINUE
			}
			

			ProjectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName projectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName =
					projectLevelDefaultCutoffs_Cache_Result_Per_SearchProgramName.getForAnnotationTypeName( annotationTypeName );

			if ( projectLevelDefaultCutoffs_Cache_Result_Per_AnnotationTypeName == null ) {
				//  No entry in Project Level Default Cutoffs for this Search Program Name / type (Reported Peptide/PSM) / Annotation Name 
				//    so skip to next AnnotationTypeDTO entry

				continue;   // EARLY CONTINUE
			}

			//  Found Entry in Project Level Default Cutoffs for this Search Program Name / type (Reported Peptide/PSM) / Annotation Name 
			
			//  Will be using Project Level Default Cutoffs for this Search Program Name
			
			//  Add searchProgramsPerSearchId to result set
			searchProgramsPerSearchId_ThatMatch_ProjectLevelCutoffs.add( searchProgramsPerSearchId );
		}
	}
		
}
