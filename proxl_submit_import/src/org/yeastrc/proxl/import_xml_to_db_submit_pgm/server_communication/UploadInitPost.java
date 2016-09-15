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
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.JSONStringCharsetConstants;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportServerReponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class UploadInitPost {
	

	private static final Logger log = Logger.getLogger(UploadInitPost.class);


	private static final UploadInitPost instance = new UploadInitPost();

	private UploadInitPost() { }
	public static UploadInitPost getInstance() { return instance; }

	public static final String SUB_URL = "/proxl_xml_file_import/uploadInit";
	
	public static final String PROJECT_ID_FORM_FIELD = "project_id";
	public static final String SUBMITTER_SAME_MACHINE_FORM_FIELD = "submitter_same_machine";


	private static final String SUBMITTER_SAME_MACHINE_TRUE = Boolean.TRUE.toString();

	
	public UploadInitResult uploadInitPost( 
			String projectIdString, 
			boolean submitterSameMachine, 
			String baseURL, 
			HttpClient httpclient ) throws Exception {
		
		String url = baseURL + SUB_URL;

		HttpPost post = null;
		List<NameValuePair> nameValuePairs = null;
		HttpResponse response = null;

		InputStream responseInputStream = null;
		
		
		UploadInitResponse uploadInitResponse = null;
		
		try {

			post = new HttpPost( url );

			nameValuePairs = new ArrayList<NameValuePair>(5);

			nameValuePairs.add(new BasicNameValuePair( PROJECT_ID_FORM_FIELD, projectIdString ) );
			
			if ( submitterSameMachine ) {
				
				nameValuePairs.add(new BasicNameValuePair( SUBMITTER_SAME_MACHINE_FORM_FIELD, SUBMITTER_SAME_MACHINE_TRUE ) );
			}

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
				
				System.out.println( "UploadInitPost response: " );
				System.out.println( responseAsString );		
			}

			
			//  Throws ProxlSubImportServerReponseException if  httpStatusCode != HttpStatus.SC_OK (200)
			A_ProcessHTTP_StatusCode.getInstance().processHTTP_StatusCode( httpStatusCode );
			


			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			
			uploadInitResponse = jacksonJSON_Mapper.readValue( responseBytesJustData, UploadInitResponse.class );
			
			
			if ( ! uploadInitResponse.isStatusSuccess() ) {
				
				String msg = "status response not true";
				log.error( msg );
//				throw new Exception(msg);
			}
			
			UploadInitResult uploadInitResult = new UploadInitResult();
			
			uploadInitResult.statusSuccess = uploadInitResponse.statusSuccess;
			uploadInitResult.projectLocked = uploadInitResponse.projectLocked;
			uploadInitResult.uploadKey = uploadInitResponse.uploadKey;
			uploadInitResult.uploadTempSubdir = uploadInitResponse.uploadTempSubdir;

			return uploadInitResult;

		} catch ( ProxlSubImportServerReponseException e ) {
			
			// Already reported so do not report
			
			throw e;
			
		} catch (Exception e) {

			log.error("Failed Upload Init.", e );
			throw e;

		} finally { 

			if ( responseInputStream != null ) {
				responseInputStream.close();
			}
		}
		
	}
	
	
	public static class UploadInitResult {

		private String uploadKey;
		

		private boolean statusSuccess;

		private boolean projectLocked; 
		
		//  Added for processing submit from same machine
		
		private String uploadTempSubdir;
		
		

		public String getUploadKey() {
			return uploadKey;
		}

		public void setUploadKey(String uploadKey) {
			this.uploadKey = uploadKey;
		}

		public boolean isStatusSuccess() {
			return statusSuccess;
		}

		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}

		public boolean isProjectLocked() {
			return projectLocked;
		}

		public void setProjectLocked(boolean projectLocked) {
			this.projectLocked = projectLocked;
		}

		public String getUploadTempSubdir() {
			return uploadTempSubdir;
		}

		public void setUploadTempSubdir(String uploadTempSubdir) {
			this.uploadTempSubdir = uploadTempSubdir;
		}
	}

	/**
	 * Must match class in web app class ProxlXMLFileImportUploadInitService
	 *
	 */
	public static class UploadInitResponse {
		
		private String uploadKey;
		

		private boolean statusSuccess;

		private boolean projectLocked; 
		
		//  Added for processing submit from same machine
		
		private String uploadTempSubdir;
		
		
		public String getUploadKey() {
			return uploadKey;
		}

		public void setUploadKey(String uploadKey) {
			this.uploadKey = uploadKey;
		}

		public boolean isStatusSuccess() {
			return statusSuccess;
		}

		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}

		public boolean isProjectLocked() {
			return projectLocked;
		}

		public void setProjectLocked(boolean projectLocked) {
			this.projectLocked = projectLocked;
		}

		public String getUploadTempSubdir() {
			return uploadTempSubdir;
		}

		public void setUploadTempSubdir(String uploadTempSubdir) {
			this.uploadTempSubdir = uploadTempSubdir;
		}



	}

}
