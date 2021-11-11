package org.yeastrc.xlink.www.send_email;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.dao.ConfigSystemDAO;

/**
 * 
 *
 */
public class GetEmailConfig {
	
	private static final Logger log = LoggerFactory.getLogger( GetEmailConfig.class);
	
	/**
	 * @return
	 * @throws Exception
	 */
	public static String getFromAddress() throws Exception {
		String fromAddress =
		ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.EMAIL_FROM_ADDRESS_URL_KEY );
		return fromAddress;
	}
}
