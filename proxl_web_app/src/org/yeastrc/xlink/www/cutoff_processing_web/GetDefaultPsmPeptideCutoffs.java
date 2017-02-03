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
	 * @param projectSearchIds
	 * @return
	 * @throws Exception
	 */
	public CutoffValuesRootLevel getDefaultPsmPeptideCutoffs(
			Collection<Integer> projectSearchIds,
			Collection<Integer> searchIds,
			Map<Integer,Integer> mapProjectSearchIdToSearchId
			) throws Exception {
		
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
		
		for ( Map.Entry<Integer,Integer> entry : mapProjectSearchIdToSearchId.entrySet() ) {
			Integer projectSearchId = entry.getKey();
			Integer searchId = entry.getValue();
			Map<Integer, AnnotationTypeDTO> reportedPeptide_AnnotationType_DTOMap = 
					reportedPeptide_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			Map<Integer, AnnotationTypeDTO> psm_AnnotationType_DTOMap = 
					psm_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			if ( reportedPeptide_AnnotationType_DTOMap != null 
					|| psm_AnnotationType_DTOMap != null ) {
				CutoffValuesSearchLevel cutoffValuesSearchLevelEntry = new CutoffValuesSearchLevel();
				cutoffValuesSearchesMap.put( projectSearchId.toString(), cutoffValuesSearchLevelEntry );
				cutoffValuesSearchLevelEntry.setSearchId( projectSearchId );
				//  Get Cutoff on Import records
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
							cutoffValuesMap );
				}
				if ( psm_AnnotationType_DTOMap != null ) {
					Map<String,CutoffValuesAnnotationLevel> cutoffValuesMap = cutoffValuesSearchLevelEntry.getPsmCutoffValues();
					processPeptidesOrPSM( 
							psm_AnnotationType_DTOMap,
							cutoffsAppliedOnImportKeyedAnnTypeId,
							cutoffValuesMap );
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
			Map<String,CutoffValuesAnnotationLevel> cutoffValuesMap
			) throws Exception {
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry :  annotationType_DTOMap.entrySet() ) {
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
