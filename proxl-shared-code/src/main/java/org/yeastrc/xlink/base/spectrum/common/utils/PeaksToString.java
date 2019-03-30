package org.yeastrc.xlink.base.spectrum.common.utils;

import java.util.List;

//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;

import org.yeastrc.xlink.base.spectrum.common.dto.Peak;

public class PeaksToString {

//    private static final Logger log = LoggerFactory.getLogger( PeaksToString.class);


	private static final int MZ_INT_STRING_SIZE = 40;

	/**
	 * @param peaks
	 * @return
	 */
	public static String peaksToString( List<Peak> peaks ) {


		StringBuilder outSB = new StringBuilder( peaks.size() * MZ_INT_STRING_SIZE );

		for ( Peak peak : peaks ) {

			outSB.append( String.valueOf( peak.getMz() ) );
			outSB.append( " " );
			outSB.append( String.valueOf( peak.getIntensity() ) );
			outSB.append( "\n" );
		}


		String out = outSB.toString();

		return out;


	}
}
