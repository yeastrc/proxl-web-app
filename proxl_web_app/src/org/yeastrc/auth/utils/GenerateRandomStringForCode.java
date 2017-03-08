package org.yeastrc.auth.utils;

//import org.apache.log4j.Logger;

/**
 * 
 * Generate a Random String for Public Access Code and Invite Code
 */
public class GenerateRandomStringForCode {

//	private static final Logger log = Logger.getLogger(GenerateRandomStringForCode.class);
	
	private GenerateRandomStringForCode() { }
	private static final GenerateRandomStringForCode _INSTANCE = new GenerateRandomStringForCode();
	public static GenerateRandomStringForCode getInstance() { return _INSTANCE; }
	
	private static final int minKeyLength = 62;
	private static final int maxKeyLength = 65;
	private static final int minMaxKeyLengthDiff = maxKeyLength - minKeyLength;
	

	/**
	 * Generate random string in length between minKeyLength and maxKeyLength
	 * @return
	 */
	public String generateRandomStringForCode() {

		int outputKeyLength = minKeyLength + (int) ( minMaxKeyLengthDiff * Math.random() );
		if ( outputKeyLength > maxKeyLength ) {
			outputKeyLength = maxKeyLength;
		}
		StringBuilder randomStringSB = new StringBuilder( maxKeyLength * 2 );
		while ( true ) {
			double tosKeyMultiplier = Math.random();
			if ( tosKeyMultiplier < 0.5 ) {
				tosKeyMultiplier += 0.5;
			}
			long tosKeyLong = (long) ( System.currentTimeMillis() * tosKeyMultiplier );

			//  Convert to chars using digits and alpha chars a-y
			String encodedLong = Long.toString(tosKeyLong, 35);

			// Drop first 6 characters and last character
			String encodedLongExtract = encodedLong.substring( 6, encodedLong.length() - 1 );
			randomStringSB.append( encodedLongExtract );
			
			if ( randomStringSB.length() >= outputKeyLength ) {
				break;
			}
		}
		
		String randomString = randomStringSB.substring( 0, outputKeyLength );
		
		return randomString;
	}
}
