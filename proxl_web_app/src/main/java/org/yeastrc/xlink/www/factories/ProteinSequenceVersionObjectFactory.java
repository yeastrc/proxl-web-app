package org.yeastrc.xlink.www.factories;

import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;

/**
 * 
 *
 */
public class ProteinSequenceVersionObjectFactory {

	/**
	 * @param proteinSequenceVersionId
	 * @return
	 */
	public static ProteinSequenceVersionObject getProteinSequenceVersionObject( int proteinSequenceVersionId ) {
		
		return new ProteinSequenceVersionObject( proteinSequenceVersionId );
	}

}
