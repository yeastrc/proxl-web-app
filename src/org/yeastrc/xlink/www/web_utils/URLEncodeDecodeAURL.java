package org.yeastrc.xlink.www.web_utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class URLEncodeDecodeAURL {
	
	private static final String ENCODING_CHARACTER_SET = "UTF-8";

	/**
	 * @param url
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String urlEncodeAURL( String url ) throws UnsupportedEncodingException {
		
		String encodedURL = URLEncoder.encode( url, ENCODING_CHARACTER_SET );
		
		return encodedURL;
	}
	

	/**
	 * @param encodedURL
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String urlDecodeAURL( String encodedURL ) throws UnsupportedEncodingException {
		
		String url = URLDecoder.decode( encodedURL, ENCODING_CHARACTER_SET );
		
		return url;
	}
}
