package org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.JSONStringCharsetConstants;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportServerReponseException;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportUserDataException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class LoginPost {
	

	private static final Logger log = LoggerFactory.getLogger( LoginPost.class);


	private static final LoginPost instance = new LoginPost();

	private LoginPost() { }
	public static LoginPost getInstance() { return instance; }

	public static final String SUB_URL = "/user/login";
	
	public static final String USERNAME_FORM_FIELD = "username";
	public static final String PASSWORD_FORM_FIELD = "password";


	
	/**
	 * @param username
	 * @param password
	 * @param baseURL
	 * @param httpclient
	 * @return - JSESSIONID Cookie contents
	 * @throws Exception
	 */
	public String loginPost( String username, String password, String baseURL, HttpClient httpclient ) throws Exception {
		
		String url = baseURL + SUB_URL;
		
		String jsessionIdCookieResponse = null;

		HttpPost post = null;
		List<NameValuePair> nameValuePairs = null;
		HttpResponse response = null;

		InputStream responseInputStream = null;
		
		try {

			post = new HttpPost( url );

			nameValuePairs = new ArrayList<NameValuePair>(5);

			nameValuePairs.add(new BasicNameValuePair( USERNAME_FORM_FIELD, username ) );
			nameValuePairs.add(new BasicNameValuePair( PASSWORD_FORM_FIELD, password ) );

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			response = httpclient.execute(post);
			
			int httpStatusCode = response.getStatusLine().getStatusCode();

			{
				String lastCookieString = null;
				Header[] headers = response.getHeaders("Set-Cookie");
				for (Header h : headers) {
					String cookieString = h.getValue().toString();
					if ( cookieString != null && cookieString.contains( "JSESSIONID" ) ) {
						jsessionIdCookieResponse = cookieString;
					}
//					System.out.println( "In Login: Headers for 'Set-Cookie', cookieString: " + cookieString );  
				}
				if ( jsessionIdCookieResponse == null ) {
					//  If Cookie not found for JSESSIONID, just try the last cookie found
					System.out.println( "No cookie found containing 'JSESSIONID' so using the last cookie string. Header count: " 
							+ headers.length + ",  lastCookieString: " + lastCookieString );
					jsessionIdCookieResponse = lastCookieString;
				}
			}

			if ( log.isDebugEnabled() ) {

				log.debug("Send Email: Http Response Status code: " + httpStatusCode );
			}

			responseInputStream = response.getEntity().getContent();
			
			//  optional code for viewing response as string
			
			//  responseBytes must be large enough for the whole response, or code something to create larger array and copy to the larger array
			
			byte[] responseBytes = new byte[10000000];
			
			int responseBytesOffset = 0;
			int responseBytesLength = responseBytes.length;
			
			int totalBytesRead = 0;
			
			while (true) {

				int bytesRead = responseInputStream.read(responseBytes, responseBytesOffset, responseBytesLength );
			
				if ( bytesRead == -1 ) {
					
					break;
				}
				
				totalBytesRead += bytesRead;
				responseBytesOffset += bytesRead;
				responseBytesLength -= bytesRead;
			}
			
			byte[] responseBytesJustData = Arrays.copyOf(responseBytes, totalBytesRead);
			
			String responseAsString = new String(responseBytesJustData, JSONStringCharsetConstants.JSON_STRING_CHARSET_UTF_8 );
						

			if ( log.isDebugEnabled() ) {
				
				System.out.println( "RESPONSE:" );
				System.out.println( responseAsString );
			}
			
			

			
			//  Throws ProxlSubImportServerReponseException if  httpStatusCode != HttpStatus.SC_OK (200)
			A_ProcessHTTP_StatusCode.getInstance().processHTTP_StatusCode( httpStatusCode, url );
			
			


			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			
			LoginResponse loginResponse = 
					jacksonJSON_Mapper.readValue( responseBytesJustData, LoginResponse.class );
			
			if ( ! loginResponse.status ) {
				
				if ( loginResponse.invalidUserOrPassword ) {
					
					System.out.println( "Failed to log in:  Username or Password is invalid." );
					
				} else if ( loginResponse.disabledUser ) {
						
					System.out.println( "Failed to log in:  Username is disabled." );
					
				} else if ( loginResponse.termsOfServiceAcceptanceRequired ) {
					
					System.out.println( "Failed to log in:  User MUST log in on the web and accept the current Terms of Service." );
					
				} else {
					
					System.out.println( "Failed to log in for unknown reason." );
				}
				throw new ProxlSubImportUserDataException();
			}

		} catch ( ProxlSubImportServerReponseException e ) {
			
			// Already reported so do not report
			
			throw e;
			
		} catch ( ProxlSubImportUserDataException e ) {
			
			throw e;

		} catch (Exception e) {

			log.error("SYSTEM ERROR: Failed to login.", e );
			throw e;

		} finally { 

			if ( responseInputStream != null ) {
				responseInputStream.close();
			}
		}
		
		return jsessionIdCookieResponse;
	}
	
	
	/**
	 * This is returned from the web service LoginService
	 *
	 */
	public static class LoginResponse {

		private boolean status = false;
		
		private boolean invalidUserOrPassword = false;
		private boolean disabledUser = false;
		/**
		 * No Proxl account and not auto create account since invite required
		 */
		private boolean noProxlAccount = false;
		private boolean invalidInviteTrackingCode = false;

		private boolean termsOfServiceAcceptanceRequired = false;

		private String termsOfServiceText;
		private String termsOfServiceKey;
		
		public boolean isStatus() {
			return status;
		}
		public void setStatus(boolean status) {
			this.status = status;
		}
		public boolean isInvalidUserOrPassword() {
			return invalidUserOrPassword;
		}
		public void setInvalidUserOrPassword(boolean invalidUserOrPassword) {
			this.invalidUserOrPassword = invalidUserOrPassword;
		}
		public boolean isDisabledUser() {
			return disabledUser;
		}
		public void setDisabledUser(boolean disabledUser) {
			this.disabledUser = disabledUser;
		}
		public boolean isTermsOfServiceAcceptanceRequired() {
			return termsOfServiceAcceptanceRequired;
		}
		public void setTermsOfServiceAcceptanceRequired(
				boolean termsOfServiceAcceptanceRequired) {
			this.termsOfServiceAcceptanceRequired = termsOfServiceAcceptanceRequired;
		}
		public String getTermsOfServiceText() {
			return termsOfServiceText;
		}
		public void setTermsOfServiceText(String termsOfServiceText) {
			this.termsOfServiceText = termsOfServiceText;
		}
		public String getTermsOfServiceKey() {
			return termsOfServiceKey;
		}
		public void setTermsOfServiceKey(String termsOfServiceKey) {
			this.termsOfServiceKey = termsOfServiceKey;
		}
		public boolean isNoProxlAccount() {
			return noProxlAccount;
		}
		public void setNoProxlAccount(boolean noProxlAccount) {
			this.noProxlAccount = noProxlAccount;
		}
		public boolean isInvalidInviteTrackingCode() {
			return invalidInviteTrackingCode;
		}
		public void setInvalidInviteTrackingCode(boolean invalidInviteTrackingCode) {
			this.invalidInviteTrackingCode = invalidInviteTrackingCode;
		}
		
		
	}

}
