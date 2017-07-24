package org.yeastrc.xlink.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.Logger;

/**
 * Use GZip to compress and uncompress a byte[]
 *
 */
public class ZipUnzipByteArray {

	private static final Logger log = Logger.getLogger( ZipUnzipByteArray.class );
	
	//  private constructor
	private ZipUnzipByteArray() {}
	
	public static ZipUnzipByteArray getInstance() {
		return new ZipUnzipByteArray();
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 * @throws IOException 
	 */
	public byte[] zipByteArray( byte[] input ) throws IOException {
		if ( input == null ) {
			String msg = "input cannot be null.";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		ByteArrayOutputStream baos =  new ByteArrayOutputStream( input.length );
		GZIPOutputStream osGZIP = null;
		try {
			osGZIP = new GZIPOutputStream( baos );
			osGZIP.write( input );
		} finally {
			if ( osGZIP != null ) {
				osGZIP.close(); // Always close GZIPOutputStream
			}
		}
		byte[] result = baos.toByteArray();
		return result;
	}

	/**
	 * 
	 * @param input
	 * @return
	 * @throws IOException 
	 */
	public byte[] unzipByteArray( byte[] input ) throws IOException {
		if ( input == null ) {
			String msg = "input cannot be null.";
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		int inputLength = input.length;
		int inputLengthTimes10 = inputLength * 10;
		if ( inputLengthTimes10 < inputLength ) {
			log.warn( "inputLengthTimes10 < inputLength.  Setting inputLengthTimes10 to inputLength" );
			inputLengthTimes10 = inputLength;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream( input );
		ByteArrayOutputStream baos =  new ByteArrayOutputStream( input.length * 10 );
		GZIPInputStream inputStreamGZIP = null;
		try {
			inputStreamGZIP = new GZIPInputStream( bais );
			byte[] buffer = new byte[ 32000 ];
			int readCount = 0;
			while ( ( readCount = inputStreamGZIP.read( buffer ) ) != -1 ) {
				baos.write( buffer, 0, readCount );
			}
		} finally {
			if ( inputStreamGZIP != null ) {
				inputStreamGZIP.close(); // Always close GZIPInputStream
			}
		}
		baos.close();
		byte[] result = baos.toByteArray();
		int resultLength = result.length;
		if ( log.isDebugEnabled() ) {
			log.debug( "inputLength: " + inputLength + ", resultLength: " + resultLength );
		}
		return result;
	}
}
