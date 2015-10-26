package org.yeastrc.xlink.www.user_web_utils;

import org.apache.commons.lang.StringUtils;
import org.yeastrc.xlink.www.constants.HeaderStringLengthLimitsConstants;

/**
 * 
 *
 */
public class TruncateProjectTitleForDisplay {
	
	private static final String TRUNCATED_STRING_INDICATOR_APPENDED = "...";

	/**
	 * @param projectTitle
	 * @return
	 */
	public static String truncateProjectTitleForHeader( String projectTitle ) {
		
		if ( StringUtils.isEmpty( projectTitle ) ) {
			
			throw new IllegalArgumentException( "projectTitle cannot be null or empty" );
		}
		
		if ( projectTitle.length() > HeaderStringLengthLimitsConstants.TITLE_MAX_DISPLAY_LENGTH ) {
			
			projectTitle = projectTitle.substring(0, HeaderStringLengthLimitsConstants.TITLE_MAX_DISPLAY_LENGTH );
			
			projectTitle += TRUNCATED_STRING_INDICATOR_APPENDED;
			
		}
		
		return projectTitle;
	}
	
	/**
	 * @param projectTitle
	 * @return
	 */
	public static String truncateProjectTitleForHeaderNonUser( String projectTitle ) {
		
		if ( StringUtils.isEmpty( projectTitle ) ) {
			
			throw new IllegalArgumentException( "projectTitle cannot be null or empty" );
		}
		
		if ( projectTitle.length() > HeaderStringLengthLimitsConstants.TITLE_MAX_DISPLAY_NON_USER_LENGTH ) {
			
			projectTitle = projectTitle.substring(0, HeaderStringLengthLimitsConstants.TITLE_MAX_DISPLAY_NON_USER_LENGTH );
			
			projectTitle += TRUNCATED_STRING_INDICATOR_APPENDED;
			
		}
		
		return projectTitle;
	}
}
