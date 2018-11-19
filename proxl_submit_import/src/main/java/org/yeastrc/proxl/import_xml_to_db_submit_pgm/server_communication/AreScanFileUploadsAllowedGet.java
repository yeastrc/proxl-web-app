package org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication;

import java.io.InputStream;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.JSONStringCharsetConstants;



import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportServerReponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class AreScanFileUploadsAllowedGet {
	

	private static final Logger log = Logger.getLogger(AreScanFileUploadsAllowedGet.class);


	private static final AreScanFileUploadsAllowedGet instance = new AreScanFileUploadsAllowedGet();

	private AreScanFileUploadsAllowedGet() { }
	public static AreScanFileUploadsAllowedGet getInstance() { return instance; }

	public static final String SUB_URL = "/file_import_proxl_xml_scans/scanFilesAllowed";
	

	
	public ScanFilesAllowedResult areScanFileUploadsAllowedGet( String baseURL, HttpClient httpclient ) throws Exception {
		
		ScanFilesAllowedResult scanFilesAllowedResult = new ScanFilesAllowedResult();

		String url = baseURL + SUB_URL;
		
		HttpGet httpGet = null;
		HttpResponse response = null;

		InputStream responseInputStream = null;
		
		ScanFilesAllowedResponse scanFilesAllowedResponse = null;
		
		try {

			httpGet = new HttpGet( url );

			response = httpclient.execute(httpGet);
			
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
				
				System.out.println( SUB_URL + " RESPONSE:" );
				System.out.println( responseAsString );
			}
			
			
			//  Throws ProxlSubImportServerReponseException if  httpStatusCode != HttpStatus.SC_OK (200)
			A_ProcessHTTP_StatusCode.getInstance().processHTTP_StatusCode( httpStatusCode, url );
			
			

			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			
			scanFilesAllowedResponse = jacksonJSON_Mapper.readValue( responseBytesJustData, ScanFilesAllowedResponse.class );
			

			if ( ! scanFilesAllowedResponse.statusSuccess ) {
				
				
			}
			

			if ( ! scanFilesAllowedResponse.scanFilesAllowed ) {
				
				
			}
			
			scanFilesAllowedResult.statusSuccess = scanFilesAllowedResponse.statusSuccess;
			
			scanFilesAllowedResult.scanFilesAllowed = scanFilesAllowedResponse.scanFilesAllowed;
			
			return scanFilesAllowedResult;


		} catch ( ProxlSubImportServerReponseException e ) {
			
			// Already reported so do not report
			
			throw e;
			
		} catch (Exception e) {

			log.error("Failed.", e );
			throw e;

		} finally { 

			if ( responseInputStream != null ) {
				responseInputStream.close();
			}
		}
		
	}

	public static class ScanFilesAllowedResult {

		boolean statusSuccess;

		boolean scanFilesAllowed;
		
		

		public boolean isStatusSuccess() {
			return statusSuccess;
		}

		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}

		public boolean isScanFilesAllowed() {
			return scanFilesAllowed;
		}

		public void setScanFilesAllowed(boolean scanFilesAllowed) {
			this.scanFilesAllowed = scanFilesAllowed;
		}
	}

	public static class ScanFilesAllowedResponse {
		
		boolean statusSuccess;

		boolean scanFilesAllowed;

		public boolean isStatusSuccess() {
			return statusSuccess;
		}

		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}

		public boolean isScanFilesAllowed() {
			return scanFilesAllowed;
		}

		public void setScanFilesAllowed(boolean scanFilesAllowed) {
			this.scanFilesAllowed = scanFilesAllowed;
		} 
		
	}

}
