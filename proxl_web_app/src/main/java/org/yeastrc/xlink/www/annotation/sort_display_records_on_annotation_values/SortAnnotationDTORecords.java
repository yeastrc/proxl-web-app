package org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;

/**
 * 
 *
 */
public class SortAnnotationDTORecords {
	
//	private static final Logger log = LoggerFactory.getLogger( SortAnnotationDTORecords.class);
	public static SortAnnotationDTORecords getInstance() {
		return new SortAnnotationDTORecords();
	}
	//  constructor
	private SortAnnotationDTORecords() { }
	
	/**
	 * Sort Peptide AnnotationTypeDTO For Best Peptide Annotations
	 * 
	 * @param peptideAnnotationTypeDTOList
	 */
	public void sortPeptideAnnotationTypeDTOForBestPeptideAnnotations_RecordsSortOrder( final List<AnnotationTypeDTO> peptideAnnotationTypeDTOList ) {
		commonSortOnSortOrder( peptideAnnotationTypeDTOList );
	}
	/**
	 * Sort Peptide AnnotationTypeDTO For Best Peptide Annotations
	 * 
	 * @param peptideAnnotationTypeDTOList
	 */
	public void sortPeptideAnnotationTypeDTOForBestPeptideAnnotations_AnnotationDisplayOrder( final List<AnnotationTypeDTO> peptideAnnotationTypeDTOList ) {
		commonSortOnDisplayOrder( peptideAnnotationTypeDTOList );
	}
	/**
	 * Sort Psm AnnotationTypeDTO For Best Psm Annotations
	 * 
	 * @param psmAnnotationTypeDTOList
	 */
	public void sortPsmAnnotationTypeDTOForBestPsmAnnotations_RecordsSortOrder( final List<AnnotationTypeDTO> psmAnnotationTypeDTOList ) {
		commonSortOnSortOrder( psmAnnotationTypeDTOList );
	}
	/**
	 * Sort Psm AnnotationTypeDTO For Best Psm Annotations
	 * 
	 * @param psmAnnotationTypeDTOList
	 */
	public void sortPsmAnnotationTypeDTOForBestPsmAnnotations_AnnotationDisplayOrder( final List<AnnotationTypeDTO> psmAnnotationTypeDTOList ) {
		commonSortOnDisplayOrder( psmAnnotationTypeDTOList );
	}
	
	/**
	 * Sort AnnotationTypeDTO on Display Order
	 * 
	 * @param annotationTypeDTOList
	 */
	private void commonSortOnDisplayOrder( final List<AnnotationTypeDTO> annotationTypeDTOList ) {
		Collections.sort( annotationTypeDTOList, new Comparator<AnnotationTypeDTO>( ) {
			@Override
			public int compare(AnnotationTypeDTO o1, AnnotationTypeDTO o2) {
				if ( o1.getDisplayOrder() == null && o2.getDisplayOrder() == null ) {
					//  Neither record has display order so sort on annotation name
					return o1.getName().compareTo( o2.getName() );
				}
				if ( o1.getDisplayOrder() == null ) {
					//  o1 does not have display order so sort it after o2
					return 1;
				}
				if ( o2.getDisplayOrder() == null ) {
					//  o2 does not have display order so sort it after o1
					return -1;
				}
				//  Sort on display order
				return o1.getDisplayOrder() - o2.getDisplayOrder();
			}
		});
	}
	
	/**
	 * Sort AnnotationTypeDTO on Sort Order
	 * 
	 * @param annotationTypeDTOList
	 */
	private void commonSortOnSortOrder( final List<AnnotationTypeDTO> annotationTypeDTOList ) {
		Collections.sort( annotationTypeDTOList, new Comparator<AnnotationTypeDTO>( ) {
			@Override
			public int compare(AnnotationTypeDTO o1, AnnotationTypeDTO o2) {
				if ( ( o1.getAnnotationTypeFilterableDTO() == null
						|| o1.getAnnotationTypeFilterableDTO().getSortOrder() == null )
						&& ( o2.getAnnotationTypeFilterableDTO() == null
						|| o2.getAnnotationTypeFilterableDTO().getSortOrder() == null ) ) {
					//  Neither record has Sort Order so sort on annotation name
					return o1.getName().compareTo( o2.getName() );
				}
				if ( o1.getAnnotationTypeFilterableDTO() == null
						|| o1.getAnnotationTypeFilterableDTO().getSortOrder() == null ) {
					//  o1 does not have Sort order so sort it after o2
					return 1;
				}
				if ( o2.getAnnotationTypeFilterableDTO() == null
						|| o2.getAnnotationTypeFilterableDTO().getSortOrder() == null ) {
					//  o2 does not have Sort order so sort it after o1
					return -1;
				}
				//  Sort on Sort Order
				return o1.getAnnotationTypeFilterableDTO().getSortOrder()- o2.getAnnotationTypeFilterableDTO().getSortOrder();
			}
		});
	}
}
