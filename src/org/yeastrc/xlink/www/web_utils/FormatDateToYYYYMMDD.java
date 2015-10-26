package org.yeastrc.xlink.www.web_utils;

import java.text.SimpleDateFormat;
import java.util.Date;

//import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.DateFormatConstants;

/**
 * 
 *
 */
public class FormatDateToYYYYMMDD {

//	private static final Logger log = Logger.getLogger(FormatDateToYYYYMMDD.class);
	

	//  private constructor
	private FormatDateToYYYYMMDD() { }
	
	/**
	 * @return newly created instance
	 */
	public static FormatDateToYYYYMMDD getInstance() { 
		return new FormatDateToYYYYMMDD(); 
	}
	
	
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat( DateFormatConstants.DATE_FORMAT_YYYY_MM_DD );
	
	/**
	 * @param date
	 * @return
	 */
	public String formatDateToYYYY_MM_DD( Date date ) {
		
		
		String formattedDate = simpleDateFormat.format( date );
		
		return formattedDate;
	}
}
