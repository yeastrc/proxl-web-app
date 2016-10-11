package org.yeastrc.xlink.www.cutoff_processing_web;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.dto.CutoffsAppliedOnImportDTO;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesAnnotationLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.searcher.CutoffsAppliedOnImportSearcher;

/**
 * 
 *
 */
public class GetDefaultPsmPeptideCutoffs {

	private static final Logger log = Logger.getLogger( GetDefaultPsmPeptideCutoffs.class );


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
	 * @param srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap
	 * @param srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap
	 * @return
	 * @throws Exception
	 */
	public CutoffValuesRootLevel getDefaultPsmPeptideCutoffs(

			Collection<Integer> searchIds
			
			) throws Exception {

		

		//  Get Annotation Type records for PSM and Peptide
		
		
		//  Get  Annotation Type records for PSM
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> 
		srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIds );
		
		
		//  Get  Annotation Type records for Reported Peptides
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> 
		srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIds );
		
		//  Get Cutoff on Import records
		
		Map<Integer, Map<Integer, CutoffsAppliedOnImportDTO>> cutoffsAppliedOnImportKeyedOnSearchIdAnnTypeId = new HashMap<>();
				
		for ( Integer searchId : searchIds ) {

			List<CutoffsAppliedOnImportDTO> cutoffsAppliedOnImportList = 
					CutoffsAppliedOnImportSearcher.getInstance().getCutoffsAppliedOnImportDTOForSearchId( searchId );
			
			Map<Integer, CutoffsAppliedOnImportDTO> cutoffsAppliedOnImportKeyedAnnTypeId = new HashMap<>();
			cutoffsAppliedOnImportKeyedOnSearchIdAnnTypeId.put(searchId, cutoffsAppliedOnImportKeyedAnnTypeId);

			for ( CutoffsAppliedOnImportDTO cutoffsAppliedOnImport : cutoffsAppliedOnImportList ) {

				cutoffsAppliedOnImportKeyedAnnTypeId.put( cutoffsAppliedOnImport.getAnnotationTypeId(), cutoffsAppliedOnImport );
			}
		}

		
		CutoffValuesRootLevel cutoffValuesRootLevel =  new CutoffValuesRootLevel();

		processPeptides( 
				srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap,
				cutoffsAppliedOnImportKeyedOnSearchIdAnnTypeId,
				cutoffValuesRootLevel );
		
		processPSMs( 
				srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap, 
				cutoffsAppliedOnImportKeyedOnSearchIdAnnTypeId,
				cutoffValuesRootLevel );
		
		return cutoffValuesRootLevel;
	}
	
	
	
	/**
	 * @param srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap
	 * @param cutoffValuesRootLevel
	 * @throws Exception 
	 */
	private void processPeptides( 

			Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap,
			Map<Integer, Map<Integer, CutoffsAppliedOnImportDTO>> cutoffsAppliedOnImportKeyedOnSearchIdAnnTypeId,
			CutoffValuesRootLevel cutoffValuesRootLevel
			) throws Exception {


		Map<String, CutoffValuesSearchLevel> cutoffValuesSearchesMap = cutoffValuesRootLevel.getSearches();

		//  Match Peptide values: process to add default cutoffs for type ids 

		for ( Map.Entry<Integer, Map<Integer, AnnotationTypeDTO>> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap_Entry :
			srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap.entrySet() ) {

			Integer reportedPeptideAnnotationTypesSearchId = srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap_Entry.getKey();

			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap = 
					srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap_Entry.getValue();
			
			
			Map<Integer, CutoffsAppliedOnImportDTO> cutoffsAppliedOnImportKeyedAnnTypeId =
					cutoffsAppliedOnImportKeyedOnSearchIdAnnTypeId.get( reportedPeptideAnnotationTypesSearchId );

			
			//  cutoffValuesSearchesMap is map with searchId (as String) as key.

			CutoffValuesSearchLevel cutoffValuesSearchLevelEntry =
					cutoffValuesSearchesMap.get( reportedPeptideAnnotationTypesSearchId.toString() );

			if ( cutoffValuesSearchLevelEntry == null ) {

				cutoffValuesSearchLevelEntry = new CutoffValuesSearchLevel();

				cutoffValuesSearchesMap.put( reportedPeptideAnnotationTypesSearchId.toString(), cutoffValuesSearchLevelEntry );
				
				cutoffValuesSearchLevelEntry.setSearchId( reportedPeptideAnnotationTypesSearchId );
			}

			Map<String,CutoffValuesAnnotationLevel> cutoffValuesMap = cutoffValuesSearchLevelEntry.getPeptideCutoffValues();

			for ( Map.Entry<Integer, AnnotationTypeDTO> entry :  srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap.entrySet() ) {

				AnnotationTypeDTO annotationTypeDTO = entry.getValue();
				
				AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();

				if ( annotationTypeFilterableDTO == null ) {

					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + annotationTypeDTO.getId();
					log.error( msg );
					throw new Exception(msg);
				}
				
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
					cutoffValuesEntryForTypeId.setValue( annotationTypeFilterableDTO.getDefaultFilterValueString() );
					
					if ( cutoffsAppliedOnImportDTO != null ) {
						
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
	}


	/**
	 * @param srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap
	 * @param cutoffValuesRootLevel
	 * @throws Exception 
	 */
	private void processPSMs( 

			Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap,
			Map<Integer, Map<Integer, CutoffsAppliedOnImportDTO>> cutoffsAppliedOnImportKeyedOnSearchIdAnnTypeId,
			CutoffValuesRootLevel cutoffValuesRootLevel
			) throws Exception {


		Map<String, CutoffValuesSearchLevel> cutoffValuesSearchesMap = cutoffValuesRootLevel.getSearches();


		//  Match PSM values: process to add default cutoffs for type ids 

		for ( Map.Entry<Integer, Map<Integer, AnnotationTypeDTO>> srchPgm_Filterable_Psm_AnnotationType_DTOMap_Entry :
			srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.entrySet() ) {

			Integer psmAnnotationTypesSearchId = srchPgm_Filterable_Psm_AnnotationType_DTOMap_Entry.getKey();

			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOMap = 
					srchPgm_Filterable_Psm_AnnotationType_DTOMap_Entry.getValue();


			Map<Integer, CutoffsAppliedOnImportDTO> cutoffsAppliedOnImportKeyedAnnTypeId =
					cutoffsAppliedOnImportKeyedOnSearchIdAnnTypeId.get( psmAnnotationTypesSearchId );

			
			//  cutoffValuesSearchesMap is map with searchId (as String) as key.

			CutoffValuesSearchLevel cutoffValuesSearchLevelEntry =
					cutoffValuesSearchesMap.get( psmAnnotationTypesSearchId.toString() );

			if ( cutoffValuesSearchLevelEntry == null ) {

				cutoffValuesSearchLevelEntry = new CutoffValuesSearchLevel();

				cutoffValuesSearchesMap.put( psmAnnotationTypesSearchId.toString(), cutoffValuesSearchLevelEntry );
				
				cutoffValuesSearchLevelEntry.setSearchId( psmAnnotationTypesSearchId );
			}


			Map<String,CutoffValuesAnnotationLevel> psmCutoffValuesMap = cutoffValuesSearchLevelEntry.getPsmCutoffValues();

			for ( Map.Entry<Integer, AnnotationTypeDTO> entry :  srchPgm_Filterable_Psm_AnnotationType_DTOMap.entrySet() ) {

				AnnotationTypeDTO annotationTypeDTO = entry.getValue();

				AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();


				if ( annotationTypeFilterableDTO == null ) {

					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + annotationTypeDTO.getId();
					log.error( msg );
					throw new Exception(msg);
				}

				CutoffsAppliedOnImportDTO cutoffsAppliedOnImportDTO = null;
				
				if ( cutoffsAppliedOnImportKeyedAnnTypeId != null && ( ! cutoffsAppliedOnImportKeyedAnnTypeId.isEmpty() ) ) {
					
					cutoffsAppliedOnImportDTO = cutoffsAppliedOnImportKeyedAnnTypeId.get( annotationTypeDTO.getId() );
				}
				
				if ( annotationTypeFilterableDTO.isDefaultFilter() ) {

					int typeId = annotationTypeDTO.getId();

					String typeIdString = Integer.toString( typeId );

					CutoffValuesAnnotationLevel cutoffValuesEntryForTypeId = new CutoffValuesAnnotationLevel();
					psmCutoffValuesMap.put( typeIdString, cutoffValuesEntryForTypeId );

					cutoffValuesEntryForTypeId.setId(typeId);
					cutoffValuesEntryForTypeId.setValue( annotationTypeFilterableDTO.getDefaultFilterValueString() );
					
					if ( cutoffsAppliedOnImportDTO != null ) {
						
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

						int typeId = annotationTypeDTO.getId();

						String typeIdString = Integer.toString( typeId );

						CutoffValuesAnnotationLevel cutoffValuesEntryForTypeId = new CutoffValuesAnnotationLevel();
						psmCutoffValuesMap.put( typeIdString, cutoffValuesEntryForTypeId );

						cutoffValuesEntryForTypeId.setId(typeId);
						cutoffValuesEntryForTypeId.setValue( cutoffsAppliedOnImportDTO.getCutoffValueString() );
					}
					
				}
			}
		}
	}


}
