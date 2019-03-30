package org.yeastrc.xlink.base.spectrum.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.base.spectrum.common.dto.Peak;

public class StringToPeaks {

    private static final Logger log = LoggerFactory.getLogger( StringToPeaks.class);


	private static final int MZ_INT_STRING_SIZE = 40;


	/**
	 * @param peakListAsString
	 * @return
	 */
	public static List<Peak> peakStringToList( String peakListAsString ) {
		
		final String method = "mzIntStringToList(...)";
		
		List<Peak> mzIntList = new ArrayList<Peak>( peakListAsString.length() / MZ_INT_STRING_SIZE );
		
		String[] mzIntLines = peakListAsString.split("\n");
		
		for ( int i = 0; i < mzIntLines.length; i++ ) {
			
			String mzIntLine = mzIntLines[ i ];
			
			String[] mzIntLineSplit = mzIntLines[ i ].split("\\s+");
			
			if ( mzIntLineSplit.length != 2 ) {
				
				String msg = method + "mzIntLine split by tab not == 2 elements, mzIntLine = |" + mzIntLine + "|.";
				
				log.error( msg );
				
				throw new RuntimeException( msg );
			}
			
			String mzString = mzIntLineSplit[ 0 ];
			String intensityString = mzIntLineSplit[ 1 ];
			
			double mz = 0;
			
			try {
				mz = Double.parseDouble( mzString );
			} catch ( Exception e ) {
				String msg = method + "Failed to parse mz, mz = |" + mzString + "|, error = " + e.toString();
				log.error( msg, e);
				throw new RuntimeException( msg, e );
			}
			
			float intensity = 0;
			
			try {
				intensity = Float.parseFloat( intensityString );
			} catch ( Exception e ) {
				String msg = method + "Failed to parse intensity, intensity = |" + intensityString + "|, error = " + e.toString();
				
				log.error( msg, e);
				throw new RuntimeException( msg, e );
			}
			
			Peak mzInt = new Peak( mz, intensity );
			
			mzIntList.add( mzInt );
		}
		
		return mzIntList;
	}

}
