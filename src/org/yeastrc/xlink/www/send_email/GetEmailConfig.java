package org.yeastrc.xlink.www.send_email;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.dao.ConfigSystemDAO;

public class GetEmailConfig {

	private static final Logger log = Logger.getLogger(GetEmailConfig.class);
	
	/**
	 * @return true if valid
	 * @throws Exception - If data not valid or error getting data
	 */
	public static boolean validateEmailConfig() throws Exception {
		
		String webserviceURL = null;
		String smtpServerURL = null;
		String fromAddress = null;

		try {
			webserviceURL = GetEmailConfig.getWebserviceURL();
			smtpServerURL = GetEmailConfig.getSmtpServerURL();
			fromAddress = GetEmailConfig.getFromAddress();

		} catch (Exception e ) {

			String msg = "Exception getting email config for validation";
			log.error( msg, e );
			
			throw e;
		}
		
		if ( StringUtils.isEmpty( webserviceURL ) && StringUtils.isEmpty( smtpServerURL ) ) {

			String msg = "Config for '" + ConfigSystemsKeysConstants.EMAIL_WEBSERVICE_URL_KEY
					+ "' and '" + ConfigSystemsKeysConstants.EMAIL_SMTP_SERVER_URL_KEY
					+ "' cannot both be empty.";
			log.error( msg );
			
			throw new Exception(msg);
		}
		
		
		if ( StringUtils.isEmpty( fromAddress ) ) {

			String msg = "Config for '" + ConfigSystemsKeysConstants.EMAIL_FROM_ADDRESS_URL_KEY
					+ "' cannot be empty.";
			log.error( msg );
			
			throw new Exception(msg);
		}

		return true;
	}
	
	public static String getWebserviceURL() throws Exception {
		
		String webserviceURL =
		ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.EMAIL_WEBSERVICE_URL_KEY );
		
		return webserviceURL;
	}
	
	public static String getSmtpServerURL() throws Exception {
		
		String smtpServerURL =
		ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.EMAIL_SMTP_SERVER_URL_KEY );
		
		return smtpServerURL;
	}
	
	public static String getFromAddress() throws Exception {
		
		String fromAddress =
		ConfigSystemDAO.getInstance().getConfigValueForConfigKey( ConfigSystemsKeysConstants.EMAIL_FROM_ADDRESS_URL_KEY );
		
		return fromAddress;
	}
	

}
