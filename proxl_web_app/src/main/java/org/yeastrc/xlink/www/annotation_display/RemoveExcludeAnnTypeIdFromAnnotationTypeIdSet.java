package org.yeastrc.xlink.www.annotation_display;
import java.util.Set;
//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
/**
 * Remove exclude AnnTypeId from the annotationTypeIds Set
 *
 */
public class RemoveExcludeAnnTypeIdFromAnnotationTypeIdSet {
	
//	private static final Logger log = LoggerFactory.getLogger( RemoveExcludeAnnTypeIdFromAnnotationTypeIdSet.class);
	private RemoveExcludeAnnTypeIdFromAnnotationTypeIdSet() { }
	public static RemoveExcludeAnnTypeIdFromAnnotationTypeIdSet getInstance() { return new RemoveExcludeAnnTypeIdFromAnnotationTypeIdSet(); }
	/**
	 * remove Exclude Ann Type Id From annotationTypeIds Set
	 * 
	 * @param annTypeIdDisplayPsmOrPeptide
	 * @param annotationTypeIds
	 */
	public void removeExcludeAnnTypeIdFromAnnotationTypeIdSet(
			AnnTypeIdDisplayJSON_PsmPeptide annTypeIdDisplayPsmOrPeptide,
			Set<Integer> annotationTypeIds ) {
		/////////////////////////
		//   Remove from annotationTypeIds  the excluded annotation type ids
		//   ( the entries in annTypeIdDisplayPsmOrPeptide.getExclAnnTypeId() )
		if ( annTypeIdDisplayPsmOrPeptide != null 
				&& annTypeIdDisplayPsmOrPeptide.getExclAnnTypeId() != null
				&& annTypeIdDisplayPsmOrPeptide.getExclAnnTypeId().length != 0 ) {
			for ( int excludeAnnTypeId : annTypeIdDisplayPsmOrPeptide.getExclAnnTypeId() ) {
				annotationTypeIds.remove( excludeAnnTypeId );
			}
		}
	}
}
