package org.yeastrc.xlink.www.annotation_display;
import java.util.Set;
//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
/**
 * Add Include AnnTypeId to the annotationTypeIds Set
 *
 */
public class AddIncludeAnnTypeIdToAnnotationTypeIdSet {
	
//	private static final Logger log = LoggerFactory.getLogger( AddIncludeAnnTypeIdToAnnotationTypeIdSet.class);
	private AddIncludeAnnTypeIdToAnnotationTypeIdSet() { }
	public static AddIncludeAnnTypeIdToAnnotationTypeIdSet getInstance() { return new AddIncludeAnnTypeIdToAnnotationTypeIdSet(); }
	/**
	 * Add Include AnnTypeId to the annotationTypeIds Set
	 * 
	 * @param annTypeIdDisplayPsmOrPeptide
	 * @param annotationTypeIds
	 */
	public void addIncludeAnnTypeIdToAnnotationTypeIdSet(
			AnnTypeIdDisplayJSON_PsmPeptide annTypeIdDisplayPsmOrPeptide,
			Set<Integer> annotationTypeIds
			) {
		/////////////////////////
		//   Add to annotationTypeIds  the Include annotation type ids
		//   ( the entries in annTypeIdDisplayPsmOrPeptide.getInclAnnTypeId() )
		if ( annTypeIdDisplayPsmOrPeptide != null 
				&& annTypeIdDisplayPsmOrPeptide.getInclAnnTypeId() != null
				&& annTypeIdDisplayPsmOrPeptide.getInclAnnTypeId().length != 0 ) {
			for ( int includeAnnTypeId : annTypeIdDisplayPsmOrPeptide.getInclAnnTypeId() ) {
				annotationTypeIds.add( includeAnnTypeId );
			}
		}
	}
}
