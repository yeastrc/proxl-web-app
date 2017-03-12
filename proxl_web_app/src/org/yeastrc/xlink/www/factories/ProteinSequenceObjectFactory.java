package org.yeastrc.xlink.www.factories;

import org.yeastrc.xlink.www.objects.ProteinSequenceObject;

/**
 * 
 *
 */
public class ProteinSequenceObjectFactory {

	/**
	 * @param proteinSequenceId
	 * @return
	 */
	public static ProteinSequenceObject getProteinSequenceObject( int proteinSequenceId ) {
		
		return new ProteinSequenceObject( proteinSequenceId );
	}

}
