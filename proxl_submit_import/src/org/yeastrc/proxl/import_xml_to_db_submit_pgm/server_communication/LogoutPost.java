package org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.JSONStringCharsetConstants;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportServerReponseException;

/**
 * 
 *
 */
public class LogoutPost {
	

	private static final Logger log = Logger.getLogger(LogoutPost.class);


	private static final LogoutPost instance = new LogoutPost();

	private LogoutPost() { }
	public static LogoutPost getInstance() { return instance; }

	public static final String SUB_URL = "/user/logout";
	
	
	public void logoutPost( String baseURL, HttpClient httpclient ) throws Exception {
		
		String url = baseURL + SUB_URL;

		HttpPost post = null;
		List<NameValuePair> nameValuePairs = null;
		HttpResponse response = null;

		InputStream responseInputStream = null;
		
		try {

			post = new HttpPost( url );

			nameValuePairs = new ArrayList<NameValuePair>(5);

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			response = httpclient.execute(post);
			
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

			if ( log.isDebugEnabled() ) {
				
				System.out.println( "RESPONSE:" );
				System.out.println( responseAsString );
			}
			
			

			
			//  Throws ProxlSubImportServerReponseException if  httpStatusCode != HttpStatus.SC_OK (200)
			A_ProcessHTTP_StatusCode.getInstance().processHTTP_StatusCode( httpStatusCode, url );
			
			


		} catch ( ProxlSubImportServerReponseException e ) {
			
			// Already reported so do not report
			
			throw e;
			

		} catch (Exception e) {

			log.error("Failed to logout.", e );
			throw e;

		} finally { 

			if ( responseInputStream != null ) {
				responseInputStream.close();
			}
		}
		
	}

}
