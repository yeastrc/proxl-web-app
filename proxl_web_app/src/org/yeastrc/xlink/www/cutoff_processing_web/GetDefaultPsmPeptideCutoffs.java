package org.yeastrc.xlink.www.cutoff_processing_web;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesAnnotationLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;

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
		
		
		CutoffValuesRootLevel cutoffValuesRootLevel =  new CutoffValuesRootLevel();

		processPeptides( srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap, cutoffValuesRootLevel );
		
		processPSMs( srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap, cutoffValuesRootLevel );
		
		return cutoffValuesRootLevel;
	}
	
	
	
	/**
	 * @param srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap
	 * @param cutoffValuesRootLevel
	 * @throws Exception 
	 */
	private void processPeptides( 

			Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap,
			CutoffValuesRootLevel cutoffValuesRootLevel
			) throws Exception {


		Map<String, CutoffValuesSearchLevel> cutoffValuesSearchesMap = cutoffValuesRootLevel.getSearches();

		//  Match Peptide values: process to add default cutoffs for type ids 

		for ( Map.Entry<Integer, Map<Integer, AnnotationTypeDTO>> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap_Entry :
			srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap.entrySet() ) {

			Integer reportedPeptideAnnotationTypesSearchId = srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap_Entry.getKey();

			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap = 
					srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap_Entry.getValue();

			//  cutoffValuesSearchesMap is map with searchId (as String) as key.

			CutoffValuesSearchLevel cutoffValuesSearchLevelEntry =
					cutoffValuesSearchesMap.get( reportedPeptideAnnotationTypesSearchId.toString() );

			if ( cutoffValuesSearchLevelEntry == null ) {

				cutoffValuesSearchLevelEntry = new CutoffValuesSearchLevel();

				cutoffValuesSearchesMap.put( reportedPeptideAnnotationTypesSearchId.toString(), cutoffValuesSearchLevelEntry );
				
				cutoffValuesSearchLevelEntry.setSearchId( reportedPeptideAnnotationTypesSearchId );
			}

			Map<String,CutoffValuesAnnotationLevel> peptideCutoffValuesMap = cutoffValuesSearchLevelEntry.getPeptideCutoffValues();

			for ( Map.Entry<Integer, AnnotationTypeDTO> entry :  srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOMap.entrySet() ) {

				AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO = entry.getValue();

				if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {

					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();
					log.error( msg );
					throw new Exception(msg);
				}

				if ( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().isDefaultFilter() ) {

					int typeId = srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();

					String typeIdString = Integer.toString( typeId );

					CutoffValuesAnnotationLevel peptideCutoffValuesEntryForTypeId = new CutoffValuesAnnotationLevel();
					peptideCutoffValuesMap.put( typeIdString, peptideCutoffValuesEntryForTypeId );

					peptideCutoffValuesEntryForTypeId.setId(typeId);
					peptideCutoffValuesEntryForTypeId.setValue( srchPgmFilterableReportedPeptideAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getDefaultFilterValueString() );
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
			CutoffValuesRootLevel cutoffValuesRootLevel
			) throws Exception {


		Map<String, CutoffValuesSearchLevel> cutoffValuesSearchesMap = cutoffValuesRootLevel.getSearches();


		//  Match PSM values: process to add default cutoffs for type ids 

		for ( Map.Entry<Integer, Map<Integer, AnnotationTypeDTO>> srchPgm_Filterable_Psm_AnnotationType_DTOMap_Entry :
			srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.entrySet() ) {

			Integer psmAnnotationTypesSearchId = srchPgm_Filterable_Psm_AnnotationType_DTOMap_Entry.getKey();

			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOMap = 
					srchPgm_Filterable_Psm_AnnotationType_DTOMap_Entry.getValue();

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

				AnnotationTypeDTO srchPgmFilterablePsmAnnotationTypeDTO = entry.getValue();


				if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO() == null ) {

					String msg = "ERROR: Annotation type data must contain Filterable DTO data.  Annotation type id: " + srchPgmFilterablePsmAnnotationTypeDTO.getId();
					log.error( msg );
					throw new Exception(msg);
				}

				if ( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().isDefaultFilter() ) {

					int typeId = srchPgmFilterablePsmAnnotationTypeDTO.getId();

					String typeIdString = Integer.toString( typeId );

					CutoffValuesAnnotationLevel psmCutoffValuesEntryForTypeId = new CutoffValuesAnnotationLevel();
					psmCutoffValuesMap.put( typeIdString, psmCutoffValuesEntryForTypeId );

					psmCutoffValuesEntryForTypeId.setId(typeId);
					psmCutoffValuesEntryForTypeId.setValue( srchPgmFilterablePsmAnnotationTypeDTO.getAnnotationTypeFilterableDTO().getDefaultFilterValueString() );
				}
			}
		}
	}


}
