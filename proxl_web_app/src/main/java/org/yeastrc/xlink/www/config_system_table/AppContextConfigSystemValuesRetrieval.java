package org.yeastrc.xlink.www.config_system_table;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.captcha_google_api.IsGoogleRecaptchaConfigured;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.IsProxlXMLFileImportFullyConfigured;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.IsScanFileImportAllowedViaWebSubmit;
import org.yeastrc.xlink.www.web_utils.IsTermsOfServiceEnabled;

/**
 * For placement in the Application context of the web app
 * for getting specific config_system configuration values
 * 
 */
public class AppContextConfigSystemValuesRetrieval {
	
	private static final Logger log = LoggerFactory.getLogger( AppContextConfigSystemValuesRetrieval.class);
	/**
	 * @return
	 */
	public String getFooterCenterOfPageHTML() {
		try {
			return ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysConstants.FOOTER_CENTER_OF_PAGE_HTML_KEY );
		} catch ( Exception e ) {
			String msg = "Exception getting configSystem value for getFooterCenterOfPageHTML()";
			log.error( msg, e );
			return "UNABLE_TO_RETRIEVE_VALUE";
		}
	}
	/**
	 * @return
	 */
	public String getGoogleAnalyticsTrackingCode() {
		try {
			return ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysConstants.GOOGLE_ANALYTICS_TRACKING_CODE_KEY );
		} catch ( Exception e ) {
			String msg = "Exception getting configSystem value for getGoogleAnalyticsTrackingCode()";
			log.error( msg, e );
			return null;
		}
	}
	/**
	 * @return
	 */
	public boolean isGoogleRecaptchaConfigured() {
		return IsGoogleRecaptchaConfigured.getInstance().isGoogleRecaptchaConfigured();
	}
	/**
	 * @return
	 */
	public String getGoogleRecaptchaSiteCode() {
		try {
			String siteKey = ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysConstants.GOOGLE_RECAPTCHA_SITE_KEY_KEY );
			if ( siteKey != null ) {
				siteKey = siteKey.trim();
			}
			return siteKey;
		} catch ( Exception e ) {
			String msg = "Exception getting configSystem value for getGoogleRecaptchaSiteCode()";
			log.error( msg, e );
			return null;
		}
	}
	/**
	 * @return
	 */
	public boolean isProxlXMLFileImportFullyConfigured() {
		return IsProxlXMLFileImportFullyConfigured.getInstance().isProxlXMLFileImportFullyConfigured();
	}
	/**
	 * @return
	 */
	public boolean isScanFileImportAllowedViaWebSubmit() {
		return IsScanFileImportAllowedViaWebSubmit.getInstance().isScanFileImportAllowedViaWebSubmit();
	}
	/**
	 * @return
	 */
	public boolean isTermsOfServiceEnabled() {
		return IsTermsOfServiceEnabled.getInstance().isTermsOfServiceEnabled();
	}
}
