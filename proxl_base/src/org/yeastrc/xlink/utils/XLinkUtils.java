package org.yeastrc.xlink.utils;

import java.util.Arrays;
import java.util.List;

//import org.apache.log4j.Logger;




public class XLinkUtils {
	
//	private static final Logger log = Logger.getLogger(XLinkUtils.class);

	public static final int TYPE_UNLINKED  = 0;
	public static final int TYPE_MONOLINK  = 1;
	public static final int TYPE_LOOPLINK  = 2;
	public static final int TYPE_DIMER     = 3;
	public static final int TYPE_CROSSLINK = 4;
	
	public static final String[] typeStrings = {
		"unlinked",
		"monolink",
		"looplink",
		"dimer",
		"crosslink"
	};
	
	private static final List<String> typesStringsAsList = Arrays.asList( typeStrings );
	
	public static int getTypeNumber( String type ) {
		return typesStringsAsList.indexOf( type );
	}
	
	
	
	public static final String CROSS_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_CROSSLINK ) ;
	public static final String LOOP_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_LOOPLINK ) ;
	public static final String UNLINKED_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_UNLINKED ) ;
	public static final String DIMER_TYPE_STRING = XLinkUtils.getTypeString( XLinkUtils.TYPE_DIMER ) ;
	
	
	public static final String CROSS_TYPE_STRING_UPPERCASE  = CROSS_TYPE_STRING.toUpperCase();
	public static final String LOOP_TYPE_STRING_UPPERCASE  = LOOP_TYPE_STRING.toUpperCase();
	public static final String DIMER_TYPE_STRING_UPPERCASE  = DIMER_TYPE_STRING.toUpperCase();
	public static final String UNLINKED_TYPE_STRING_UPPERCASE  = UNLINKED_TYPE_STRING.toUpperCase();

	

	/**
	 * Get the string representation of a type of linked peptide
	 * @param type The type, as defined in XLinkUtils
	 * @return
	 */
	public static String getTypeString( int type ) {

		if( type < 0 || type >= typeStrings.length )
			return "unknown";
		
		return typeStrings[ type ];		
	}
	
	
	
}
