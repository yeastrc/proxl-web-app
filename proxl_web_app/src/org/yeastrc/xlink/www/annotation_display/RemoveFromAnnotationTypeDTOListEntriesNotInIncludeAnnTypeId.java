package org.yeastrc.xlink.www.annotation_display;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;

/**
 * Remove from AnnotationTypeDTO List not on Include AnnTypeId  
 *
 */
public class RemoveFromAnnotationTypeDTOListEntriesNotInIncludeAnnTypeId {


	private static final Logger log = Logger.getLogger(RemoveFromAnnotationTypeDTOListEntriesNotInIncludeAnnTypeId.class);

	private RemoveFromAnnotationTypeDTOListEntriesNotInIncludeAnnTypeId() { }
	public static RemoveFromAnnotationTypeDTOListEntriesNotInIncludeAnnTypeId getInstance() { return new RemoveFromAnnotationTypeDTOListEntriesNotInIncludeAnnTypeId(); }
	
	
	/**
	 * Remove from AnnotationTypeDTO List not on Include AnnTypeId
	 * 
	 * @param annTypeIdDisplayPsmOrPeptide
	 * @param annotationTypeDTO_List
	 */
	public void removeFromAnnotationTypeDTOListEntriesNotInIncludeAnnTypeId(
			
			AnnTypeIdDisplayJSON_PsmPeptide annTypeIdDisplayPsmOrPeptide,
			List<AnnotationTypeDTO> annotationTypeDTO_List
			) {
		

		/////////////////////////
		
		//   Remove from AnnotationTypeDTO List not on Include AnnTypeId
		//   ( the entries not in annTypeIdDisplayPsmOrPeptide.getInclAnnTypeId() )
		
		if ( annTypeIdDisplayPsmOrPeptide != null 
				&& annTypeIdDisplayPsmOrPeptide.getInclAnnTypeId() != null
				&& annTypeIdDisplayPsmOrPeptide.getInclAnnTypeId().length != 0 ) {
			
			Iterator<AnnotationTypeDTO> annotationTypeDTO_ListIter = 
					annotationTypeDTO_List.iterator();
			
			while ( annotationTypeDTO_ListIter.hasNext() ) {
					
				AnnotationTypeDTO annotationTypeDTO = annotationTypeDTO_ListIter.next();				
				
				boolean found = false;
				for ( int includeAnnTypeId : annTypeIdDisplayPsmOrPeptide.getInclAnnTypeId() ) {
					
					if ( includeAnnTypeId == annotationTypeDTO.getId() ) {
						
						found = true;
						break;
					}
				}
				
				if ( ! found ) {
					annotationTypeDTO_ListIter.remove();
				}
			}
		}
	}

}
