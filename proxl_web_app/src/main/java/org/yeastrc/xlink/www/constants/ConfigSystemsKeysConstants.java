package org.yeastrc.xlink.www.constants;

import java.util.HashSet;
import java.util.Set;

/**
 * values for the config_key field of the config_system table
 *
 */
public class ConfigSystemsKeysConstants {
	
	
	//////   Any plain text inputs need to be added to textConfigKeys in the "static {}" at the bottom
	
	
	//   !!  Additional Config contants in ConfigSystemsKeysSharedConstants in src_shared_code
	
	
	////  User sign up procedure key  -  Specific Values allowed

	public static final String USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY = "user_signup_allow_without_invite";


	////  Google Recaptcha keys

	public static final String GOOGLE_RECAPTCHA_SITE_KEY_KEY = "google_recaptcha_site_key";

	public static final String GOOGLE_RECAPTCHA_SECRET_KEY_KEY = "google_recaptcha_secret_key";
	
	
	
	////  Webservice URL keys

	public static final String PROTEIN_ANNOTATION_WEBSERVICE_URL_KEY = "protein_annotation_webservice_url";

	public static final String PROTEIN_LISTING_FROM_SEQUENCE_TAXONOMY_WEBSERVICE_URL_KEY = 
			"protein_listing_from_sequence_taxonomy_webservice_url";

	/////////   Email Config
	

	//  SMTP Server:
	public static final String EMAIL_SMTP_SERVER_HOST_URL_KEY = "email_smtp_server_url";  //  Host
	public static final String EMAIL_SMTP_SERVER_PORT_KEY = "email_smtp_server_port";

	public static final String EMAIL_SMTP_SERVER_AUTH_USERNAME_KEY = "email_smtp_server_auth_username";
	public static final String EMAIL_SMTP_SERVER_AUTH_PASSWORD_KEY = "email_smtp_server_auth_password";

	
	
	public static final String EMAIL_FROM_ADDRESS_URL_KEY = "email_from_address";
	
	public static final String ADMIN_EMAIL_ADDRESS_KEY = "admin_email_address";

	/////////   Run Importer Status Emails Config
	
	public static final String RUN_IMPORT_EXTRA_EMAILS_TO_SEND_TO_KEY = "run_import_extra_emails_to_send_to";
	
	
	///   Page Content Config
	
	public static final String FOOTER_CENTER_OF_PAGE_HTML_KEY = "footer_center_of_page_html";
	public static final String GOOGLE_ANALYTICS_TRACKING_CODE_KEY = "google_analytics_tracking_code";
	
	//    Size of Caches for DB data config, see class CacheSizeConfigStringsConstants
	
	public static final String CACHE_SIZE_CONFIG_LABEL_KEY = "cache_size_config_label";

	//   Lists of config keys for validation on save
	
	public static final Set<String> textConfigKeys = new HashSet<>();
	
	static {
		
		textConfigKeys.add( USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY );
		
		textConfigKeys.add( GOOGLE_RECAPTCHA_SITE_KEY_KEY );
		textConfigKeys.add( GOOGLE_RECAPTCHA_SECRET_KEY_KEY );
		
		textConfigKeys.add( PROTEIN_ANNOTATION_WEBSERVICE_URL_KEY );
		textConfigKeys.add( PROTEIN_LISTING_FROM_SEQUENCE_TAXONOMY_WEBSERVICE_URL_KEY );
		
		textConfigKeys.add( EMAIL_FROM_ADDRESS_URL_KEY );
		textConfigKeys.add( ADMIN_EMAIL_ADDRESS_KEY );

		textConfigKeys.add( EMAIL_SMTP_SERVER_HOST_URL_KEY );
		textConfigKeys.add( EMAIL_SMTP_SERVER_PORT_KEY );
		textConfigKeys.add( EMAIL_SMTP_SERVER_AUTH_USERNAME_KEY );
		textConfigKeys.add( EMAIL_SMTP_SERVER_AUTH_PASSWORD_KEY );
		
		textConfigKeys.add( FOOTER_CENTER_OF_PAGE_HTML_KEY );
		textConfigKeys.add( GOOGLE_ANALYTICS_TRACKING_CODE_KEY );
	
		textConfigKeys.add( CACHE_SIZE_CONFIG_LABEL_KEY );
	}
	
}
