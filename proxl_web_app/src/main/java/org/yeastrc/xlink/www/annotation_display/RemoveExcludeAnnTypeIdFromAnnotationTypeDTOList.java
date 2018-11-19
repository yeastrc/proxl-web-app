package org.yeastrc.xlink.www.annotation_display;

import java.util.Iterator;
import java.util.List;
//import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
/**
 * Remove exclude AnnTypeId from the AnnotationTypeDTO List
 *
 */
public class RemoveExcludeAnnTypeIdFromAnnotationTypeDTOList {
	
//	private static final Logger log = Logger.getLogger(RemoveExcludeAnnTypeIdFromAnnotationTypeDTOList.class);
	private RemoveExcludeAnnTypeIdFromAnnotationTypeDTOList() { }
	public static RemoveExcludeAnnTypeIdFromAnnotationTypeDTOList getInstance() { return new RemoveExcludeAnnTypeIdFromAnnotationTypeDTOList(); }
	/**
	 * remove Exclude Ann Type Id From AnnotationTypeDTO List
	 * 
	 * @param annTypeIdDisplayPsmOrPeptide
	 * @param annotationTypeDTO_List
	 */
	public void removeExcludeAnnTypeIdFromAnnotationTypeDTOList(
			AnnTypeIdDisplayJSON_PsmPeptide annTypeIdDisplayPsmOrPeptide,
			List<AnnotationTypeDTO> annotationTypeDTO_List ) {
		/////////////////////////
		//   Remove from annotationTypeDTO_List  the excluded annotation type ids
		//   ( the entries in annTypeIdDisplayPsmOrPeptide.getExclAnnTypeId() )
		if ( annTypeIdDisplayPsmOrPeptide != null 
				&& annTypeIdDisplayPsmOrPeptide.getExclAnnTypeId() != null
				&& annTypeIdDisplayPsmOrPeptide.getExclAnnTypeId().length != 0 ) {
			Iterator<AnnotationTypeDTO> annotationTypeDTO_ListIter = 
					annotationTypeDTO_List.iterator();
			while ( annotationTypeDTO_ListIter.hasNext() ) {
				AnnotationTypeDTO annotationTypeDTO = annotationTypeDTO_ListIter.next();				
				for ( int excludeAnnTypeId : annTypeIdDisplayPsmOrPeptide.getExclAnnTypeId() ) {
					if ( excludeAnnTypeId == annotationTypeDTO.getId() ) {
						annotationTypeDTO_ListIter.remove();
						break;
					}
				}
			}
		}
	}
}
