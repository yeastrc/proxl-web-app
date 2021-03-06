package org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
/**
 * 
 * Only Sort on Best Peptide and Best PSM values
 */
public class SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId {
	
	private static final Logger log = LoggerFactory.getLogger( SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.class);
	
	public static SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId getInstance() {
		return new SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId();
	}
	//  constructor
	private SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId() { }
	
	/**
	 * Sort on Best Peptide and Best PSM values for Proteins
	 * 
	 * @param searchId
	 * @param sortDisplayRecordsWrapperBaseList
	 * @param peptideCutoffsAnnotationTypeDTOList
	 * @param psmCutoffsAnnotationTypeDTOList
	 * @return
	 * @throws ProxlWebappDataException
	 * @throws Exception
	 */
	public SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId(
			int searchId,
			List<? extends SortDisplayRecordsWrapperBase> sortDisplayRecordsWrapperBaseList,
			final List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList,
			final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList
			) throws ProxlWebappDataException, Exception {
		
		SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt result =
				new SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt();
		//  Make local copies of annotation type lists for sorting
		final List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOListRecordSortOrderSorted = new ArrayList<>( peptideCutoffsAnnotationTypeDTOList );
		final List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted = new ArrayList<>( peptideCutoffsAnnotationTypeDTOList );
		final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListRecordSortOrderSorted = new ArrayList<>( psmCutoffsAnnotationTypeDTOList );
		final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted = new ArrayList<>( psmCutoffsAnnotationTypeDTOList );
		SortAnnotationDTORecords.getInstance()
		.sortPeptideAnnotationTypeDTOForBestPeptideAnnotations_RecordsSortOrder( peptideCutoffsAnnotationTypeDTOListRecordSortOrderSorted );
		SortAnnotationDTORecords.getInstance()
		.sortPeptideAnnotationTypeDTOForBestPeptideAnnotations_AnnotationDisplayOrder( peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );
		SortAnnotationDTORecords.getInstance()
		.sortPsmAnnotationTypeDTOForBestPsmAnnotations_RecordsSortOrder( psmCutoffsAnnotationTypeDTOListRecordSortOrderSorted );
		SortAnnotationDTORecords.getInstance()
		.sortPsmAnnotationTypeDTOForBestPsmAnnotations_AnnotationDisplayOrder( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );
		
		///////////////
		//  Sort Records on sort order
		Collections.sort( sortDisplayRecordsWrapperBaseList, new Comparator<SortDisplayRecordsWrapperBase>() {
			@Override
			public int compare(SortDisplayRecordsWrapperBase o1, SortDisplayRecordsWrapperBase o2) {
				//  Loop through the Peptide annotation types (sorted ), comparing the values
				for ( AnnotationTypeDTO annotationTypeDTO : peptideCutoffsAnnotationTypeDTOListRecordSortOrderSorted ) {
					int typeId = annotationTypeDTO.getId();
					AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();
					if ( annotationTypeFilterableDTO == null ) {
						String msg = "Peptide AnnotationTypeFilterableDTO == null for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					FilterDirectionType annTypeFilterDirectionType = annotationTypeFilterableDTO.getFilterDirectionType();
					if ( annTypeFilterDirectionType == null ) {
						String msg = "Peptide FilterDirectionType == null for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					AnnotationDataBaseDTO o1_AnnotationDataBaseDTO = o1.getPeptideAnnotationDTOMap().get( typeId );
					if ( o1_AnnotationDataBaseDTO == null ) {
						String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					double o1Value = o1_AnnotationDataBaseDTO.getValueDouble();
					AnnotationDataBaseDTO o2_AnnotationDataBaseDTO = o2.getPeptideAnnotationDTOMap().get( typeId );
					if ( o2_AnnotationDataBaseDTO == null ) {
						String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					double o2Value = o2_AnnotationDataBaseDTO.getValueDouble();
					if ( o1Value != o2Value ) {
						if ( annTypeFilterDirectionType == FilterDirectionType.ABOVE ) {
							if ( o1Value > o2Value ) {
								return -1;
							} else {
								return 1;
							}
						} else {
							if ( o1Value < o2Value ) {
								return -1;
							} else {
								return 1;
							}
						}
					}
				}
				//  Loop through the PSM annotation types (sorted ), comparing the values
				for ( AnnotationTypeDTO annotationTypeDTO : psmCutoffsAnnotationTypeDTOListRecordSortOrderSorted ) {
					int typeId = annotationTypeDTO.getId();
					AnnotationTypeFilterableDTO annotationTypeFilterableDTO = annotationTypeDTO.getAnnotationTypeFilterableDTO();
					if ( annotationTypeFilterableDTO == null ) {
						String msg = "PSM AnnotationTypeFilterableDTO == null for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					FilterDirectionType annTypeFilterDirectionType = annotationTypeFilterableDTO.getFilterDirectionType();
					if ( annTypeFilterDirectionType == null ) {
						String msg = "PSM FilterDirectionType == null for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					 //  Map keyed on annotation type id of annotation data 
					Map<Integer, AnnotationDataBaseDTO> o1_psmAnnotationDTOMap = o1.getPsmAnnotationDTOMap();
					Map<Integer, AnnotationDataBaseDTO> o2_psmAnnotationDTOMap = o2.getPsmAnnotationDTOMap();
					AnnotationDataBaseDTO o1_AnnotationDataBaseDTO = o1_psmAnnotationDTOMap.get( typeId );
					if ( o1_AnnotationDataBaseDTO == null ) {
						String msg = "Unable to get PSM Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					double o1Value = o1_AnnotationDataBaseDTO.getValueDouble();
					AnnotationDataBaseDTO o2_AnnotationDataBaseDTO = o2_psmAnnotationDTOMap.get( typeId );
					if ( o2_AnnotationDataBaseDTO == null ) {
						String msg = "Unable to get PSM Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					double o2Value = o2_AnnotationDataBaseDTO.getValueDouble();
					if ( o1Value != o2Value ) {
						if ( annTypeFilterDirectionType == FilterDirectionType.ABOVE ) {
							if ( o1Value > o2Value ) {
								return -1;
							} else {
								return 1;
							}
						} else {
							if ( o1Value < o2Value ) {
								return -1;
							} else {
								return 1;
							}
						}
					}
				}
				//  If everything matches, sort on provided sort value
				return o1.getFinalSortOrderKey() - o2.getFinalSortOrderKey();
			}
		});
		
		////////////////
		//  Copy annotation values to output lists
		for ( SortDisplayRecordsWrapperBase wrapperItem : sortDisplayRecordsWrapperBaseList ) {
			List<String> peptideAnnotationValueList = new ArrayList<>();
			wrapperItem.setPeptideAnnotationValueList( peptideAnnotationValueList );
			//  Loop through the Peptide annotation types (sorted ), comparing the values
			for ( AnnotationTypeDTO annotationTypeDTO : peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
				int typeId = annotationTypeDTO.getId();
				AnnotationDataBaseDTO annotationDataBaseDTO = wrapperItem.getPeptideAnnotationDTOMap().get( typeId );
				if ( annotationDataBaseDTO == null ) {
					String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
					log.error( msg );
					throw new RuntimeException(msg);
				}
				peptideAnnotationValueList.add( annotationDataBaseDTO.getValueString() );
			}
			List<String> psmAnnotationValueList = new ArrayList<>();
			wrapperItem.setPsmAnnotationValueList( psmAnnotationValueList );
			//  Loop through the PSM annotation types (sorted ), comparing the values
			for ( AnnotationTypeDTO annotationTypeDTO : psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
				int typeId = annotationTypeDTO.getId();
				AnnotationDataBaseDTO annotationDataBaseDTO = wrapperItem.getPsmAnnotationDTOMap().get( typeId );
				if ( annotationDataBaseDTO == null ) {
					String msg = "Unable to get PSM Filterable Annotation data for type id: " + typeId;
					log.error( msg );
					throw new RuntimeException(msg);
				}
				psmAnnotationValueList.add( annotationDataBaseDTO.getValueString() );
			}
		}
		////////////////
		//   Copy Annotation Display Name and Descriptions to output lists, used for table headers in the HTML
		List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList = new ArrayList<>( peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size() );
		List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList = new ArrayList<>( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size() );
		for ( AnnotationTypeDTO item : peptideCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
			AnnotationDisplayNameDescription output = new AnnotationDisplayNameDescription();
			output.setDisplayName( item.getName() );
			output.setDescription( item.getDescription() );
			peptideAnnotationDisplayNameDescriptionList.add(output);
		}
		for ( AnnotationTypeDTO item : psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {
			AnnotationDisplayNameDescription output = new AnnotationDisplayNameDescription();
			output.setDisplayName( item.getName() );
			output.setDescription( item.getDescription() );
			psmAnnotationDisplayNameDescriptionList.add(output);
		}
		result.setPeptideAnnotationDisplayNameDescriptionList( peptideAnnotationDisplayNameDescriptionList );
		result.setPsmAnnotationDisplayNameDescriptionList( psmAnnotationDisplayNameDescriptionList );
		
		return result;
	}
}
