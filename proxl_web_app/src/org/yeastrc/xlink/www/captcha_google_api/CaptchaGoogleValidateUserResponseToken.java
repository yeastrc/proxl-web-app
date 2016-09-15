package org.yeastrc.xlink.www.captcha_google_api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.JSONStringCharsetConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * 
 *
 */
public class CaptchaGoogleValidateUserResponseToken {

	private static final Logger log = Logger.getLogger(CaptchaGoogleValidateUserResponseToken.class);


	private static final CaptchaGoogleValidateUserResponseToken instance = new CaptchaGoogleValidateUserResponseToken();

	private CaptchaGoogleValidateUserResponseToken() { }
	public static CaptchaGoogleValidateUserResponseToken getInstance() { return instance; }

	
	private static final String URL = "https://www.google.com/recaptcha/api/siteverify";
	
	private static final String FORM_PARAM_SECRET_KEY = "secret";
	private static final String FORM_PARAM_USER_RESPONSE_TOKEN = "response";
	private static final String FORM_PARAM_USER_REMOTE_IP = "remoteip";
	
//	secret	Required. The shared key between your site and ReCAPTCHA.
//	response	Required. The user response token provided by the reCAPTCHA to the user and provided to your site on.
//	remoteip	Optional. The user's IP address.
	
	
	
	/**
	 * @param userResponseToken
	 * @param userRemoteIP
	 * @return
	 * @throws Exception
	 */
	public boolean isCaptchaUserResponseTokenValid( String userResponseToken, String userRemoteIP ) throws Exception {
		
		

		HttpClient client = null;
		HttpPost post = null;
		List<NameValuePair> nameValuePairs = null;
		HttpResponse response = null;

		InputStream responseInputStream = null;
		
		ValidationResponse validationResponse = null;

		try {

			String secretKey = ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysConstants.GOOGLE_RECAPTCHA_SECRET_KEY_KEY );
			

			if ( secretKey == null || StringUtils.isNotEmpty( secretKey.trim() ) ) {
				
				
				
			}
				
			
			client = new DefaultHttpClient();

			post = new HttpPost( URL );

			nameValuePairs = new ArrayList<NameValuePair>(5);

			nameValuePairs.add(new BasicNameValuePair( FORM_PARAM_SECRET_KEY, secretKey ) );
			nameValuePairs.add(new BasicNameValuePair(FORM_PARAM_USER_RESPONSE_TOKEN, userResponseToken ) );
			nameValuePairs.add(new BasicNameValuePair(FORM_PARAM_USER_REMOTE_IP, userRemoteIP ) );

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			response = client.execute(post);
			
			int httpStatusCode = response.getStatusLine().getStatusCode();

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
						
			
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			
//			validationResponse = jacksonJSON_Mapper.readValue( responseInputStream, ValidationResponse.class );

			validationResponse = jacksonJSON_Mapper.readValue( responseBytesJustData, ValidationResponse.class );
			
			
			if ( httpStatusCode != HttpStatus.SC_OK ) {
				
				String msg = "Failed to validate.  Http Response Status code: " + httpStatusCode ;
				
				log.error( msg );
				
				throw new Exception(msg);
			}


			if ( validationResponse.errorCodes != null && validationResponse.errorCodes.length > 0 ) {
				
				StringBuilder allErrorCodesSB = new StringBuilder( 1000 );
				
				allErrorCodesSB.append( "Error codes returned from Google Captcha: " );
				
				boolean firstErrorCode = true;
				
				for (  String errorCode : validationResponse.errorCodes ) {
					
					if ( firstErrorCode ) {
					
						firstErrorCode = false;
					} else {
						
						allErrorCodesSB.append( ", ");
					}
					
					allErrorCodesSB.append( "\"" );
					allErrorCodesSB.append( errorCode );
					allErrorCodesSB.append( "\"" );
				}
				
				String allErrorCodes = allErrorCodesSB.toString();
				
				String msg = "Google Captcha returned error codes (all listed, comma delimited): " + allErrorCodes 
						+ "    Full response string: " + responseAsString;
				
				log.error( msg );
				
				throw new ProxlWebappInternalErrorException(msg);
			}


		} catch (Exception e) {

			log.error("Failed to validate.", e );
			throw e;

		} finally { 

			if ( responseInputStream != null ) {
				responseInputStream.close();
			}
		}

		return validationResponse.success;
		
	}
	
	//  Possible strings for "error-codes"  from https://developers.google.com/recaptcha/docs/verify
	
	//	missing-input-secret	The secret parameter is missing.
	//	invalid-input-secret	The secret parameter is invalid or malformed.
	//	missing-input-response	The response parameter is missing.
	//	invalid-input-response	The response parameter is invalid or malformed.
	
	
	/**
	 * 
	 *
	 */
	private static class ValidationResponse {
		
		private boolean success;
		private String challenge_ts;
		private String hostname;
		
		@JsonProperty("error-codes")
		private String[] errorCodes;
		
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public String getChallenge_ts() {
			return challenge_ts;
		}
		public void setChallenge_ts(String challenge_ts) {
			this.challenge_ts = challenge_ts;
		}
		public String getHostname() {
			return hostname;
		}
		public void setHostname(String hostname) {
			this.hostname = hostname;
		}
		public String[] getErrorCodes() {
			return errorCodes;
		}
		public void setErrorCodes(String[] errorCodes) {
			this.errorCodes = errorCodes;
		}
		
//		"success": true|false,
//		  "challenge_ts": timestamp,  // timestamp of the challenge load (ISO format yyyy-MM-dd'T'HH:mm:ssZZ)
//		  "hostname": string,         // the hostname of the site where the reCAPTCHA was solved
//		  "error-codes": [...]        // optional
		
	}

}
