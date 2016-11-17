package org.yeastrc.xlink.www.annotation_display;

import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Add Add AnnTypeId to the annotationTypeIds Set
 *
 */
public class AddAddAnnTypeIdToAnnotationTypeIdSet {


	private static final Logger log = Logger.getLogger(AddAddAnnTypeIdToAnnotationTypeIdSet.class);

	private AddAddAnnTypeIdToAnnotationTypeIdSet() { }
	public static AddAddAnnTypeIdToAnnotationTypeIdSet getInstance() { return new AddAddAnnTypeIdToAnnotationTypeIdSet(); }
	
	
	/**
	 * Add Add AnnTypeId to the annotationTypeIds Set
	 * 
	 * @param annTypeIdDisplayPsmOrPeptide
	 * @param annotationTypeIds
	 */
	public void addIncludeAnnTypeIdToAnnotationTypeIdSet(
			
			AnnTypeIdDisplayJSON_PsmPeptide annTypeIdDisplayPsmOrPeptide,
			Set<Integer> annotationTypeIds
			) {
		

		/////////////////////////
		
		//   Add to annotationTypeIds  the Added annotation type ids
		//   ( the entries in annTypeIdDisplayPsmOrPeptide.getAddAnnTypeId() )
		
		if ( annTypeIdDisplayPsmOrPeptide != null 
				&& annTypeIdDisplayPsmOrPeptide.getAddAnnTypeId() != null
				&& annTypeIdDisplayPsmOrPeptide.getAddAnnTypeId().length != 0 ) {
			
			for ( int includeAnnTypeId : annTypeIdDisplayPsmOrPeptide.getAddAnnTypeId() ) {
					
				annotationTypeIds.add( includeAnnTypeId );
			}
		}
	}

}
