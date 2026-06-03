package org.yeastrc.auth.utils;

import java.security.SecureRandom;

//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;

/**
 * 
 * Generate a Random String for Public Access Code and Invite Code
 */
public class GenerateRandomStringForCode {

//	private static final Logger log = LoggerFactory.getLogger( GenerateRandomStringForCode.class);
	private GenerateRandomStringForCode() { }
	private static final GenerateRandomStringForCode _INSTANCE = new GenerateRandomStringForCode();
	public static GenerateRandomStringForCode getInstance() { return _INSTANCE; }
	
	private static final int minKeyLength = 62;
	private static final int maxKeyLength = 65;
	
	

	/**
	 *  Alphabet of allowed characters: digits 0-9 plus lower case consonants.
	 *
	 *  Vowels ( a e i o u y ) are excluded so codes cannot spell words.
	 *  Lower case 'l' and upper case 'I' are excluded since they look alike in many fonts.
	 *  Number 0 is excluded since can look like upper case 'O'.
	 *  Number 1 is excluded since can look like lower case 'l'.
	 *  Removed more letters and added capitol letters.
	 *  All characters are URL / email safe (codes are placed in links).
	 */
	private static final String ALLOWED_CHARS_NO_NUMBERS__STRING =  "bcdfghjkmnpqrstxBCDFGHJKMNPRSTX";
	
	private static final char[] ALLOWED_CHARS_NO_NUMBERS =
			ALLOWED_CHARS_NO_NUMBERS__STRING.toCharArray();

	private static final char[] ALLOWED_CHARS =
			( "23456789" + ALLOWED_CHARS_NO_NUMBERS__STRING ).toCharArray();


	private static final SecureRandom secureRandom = new SecureRandom();

	/**
	 * Generate random string in length between minKeyLength and maxKeyLength
	 * @return
	 */
	public String generateRandomStringForCode() {

		//  Length uniformly in [ minKeyLength, maxKeyLength ]
		int outputKeyLength = minKeyLength + secureRandom.nextInt( maxKeyLength - minKeyLength + 1 );

		StringBuilder randomStringSB = new StringBuilder( outputKeyLength );

		//  First character is NOT a number
		
		//  nextInt( bound ) is unbiased so each allowed character is equally likely
		randomStringSB.append( ALLOWED_CHARS_NO_NUMBERS[ secureRandom.nextInt( ALLOWED_CHARS_NO_NUMBERS.length ) ] );
		
		for ( int i = 1; i < outputKeyLength; i++ ) {
			//  nextInt( bound ) is unbiased so each allowed character is equally likely
			randomStringSB.append( ALLOWED_CHARS[ secureRandom.nextInt( ALLOWED_CHARS.length ) ] );
		}

		String randomString = randomStringSB.substring( 0, outputKeyLength );
		
		return randomString;
	}
}
