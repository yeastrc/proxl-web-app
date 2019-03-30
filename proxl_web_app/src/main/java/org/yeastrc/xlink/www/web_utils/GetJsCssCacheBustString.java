package org.yeastrc.xlink.www.web_utils;

//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;

/**
 * 
 *
 */
public class GetJsCssCacheBustString {

//	private static final Logger log = LoggerFactory.getLogger( GetJsCssCacheBustString.class);

	private GetJsCssCacheBustString() { }
	public static GetJsCssCacheBustString getInstance() { return new GetJsCssCacheBustString(); }
	
	/**
	 * @return
	 */
	public String getJsCssCacheBustString() {
		
		long currentTime = System.currentTimeMillis();
		
		String currentTimeHexString = Long.toHexString( currentTime );
		
		return currentTimeHexString;
	}

}
