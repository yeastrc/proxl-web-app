package org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication;

import java.io.InputStream;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.JSONStringCharsetConstants;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportServerReponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class UploadSubmitPost {
	

	private static final Logger log = Logger.getLogger(UploadSubmitPost.class);


	private static final UploadSubmitPost instance = new UploadSubmitPost();

	private UploadSubmitPost() { }
	public static UploadSubmitPost getInstance() { return instance; }

	public static final String SUB_URL = "/proxl_xml_file_import/uploadSubmit";

	
	public UploadSubmitResult uploadSubmitPost( 
			byte[] uploadSubmitRequest_JSON, 
			String baseURL, 
			HttpClient httpclient ) throws Exception {
		
		String url = baseURL + SUB_URL;

		HttpPost post = null;
		HttpResponse response = null;

		InputStream responseInputStream = null;
		
		
		UploadSubmitResponse uploadSubmitResponse = null;
		
		try {

			post = new HttpPost( url );
			
			ByteArrayEntity byteArrayEntity = new ByteArrayEntity( uploadSubmitRequest_JSON, ContentType.APPLICATION_JSON );

			post.setEntity( byteArrayEntity );

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
				
				System.out.println( "UploadSubmitPost response: " );
				System.out.println( responseAsString );
			}	


			
			//  Throws ProxlSubImportServerReponseException if  httpStatusCode != HttpStatus.SC_OK (200)
			A_ProcessHTTP_StatusCode.getInstance().processHTTP_StatusCode( httpStatusCode );
			
			


			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			
			uploadSubmitResponse = jacksonJSON_Mapper.readValue( responseBytesJustData, UploadSubmitResponse.class );
			
			
			if ( ! uploadSubmitResponse.isStatusSuccess() ) {
				
				String msg = "status response not true";
				log.error( msg );
//				throw new Exception(msg);
			}
			
			UploadSubmitResult uploadSubmitResult = new UploadSubmitResult();
			
			uploadSubmitResult.statusSuccess = uploadSubmitResponse.statusSuccess;
			uploadSubmitResult.projectLocked = uploadSubmitResponse.projectLocked;
			uploadSubmitResult.submittedScanFileNotAllowed = uploadSubmitResponse.submittedScanFileNotAllowed;
			uploadSubmitResult.importerSubDir = uploadSubmitResponse.importerSubDir;

			return uploadSubmitResult;

			
			
		} catch ( ProxlSubImportServerReponseException e ) {
			
			// Already reported so do not report
			
			throw e;
			
		} catch (Exception e) {

			log.error("Failed Upload Submit.", e );
			throw e;

		} finally { 

			if ( responseInputStream != null ) {
				responseInputStream.close();
			}
		}
		
	}

	/////////////////////////////////
	
	/////   Class for webservice response
	
	/**
	 * Must match class in web app class ProxlXMLFileImportUploadSubmitService
	 *
	 */
	public static class UploadSubmitResponse {

		private boolean statusSuccess;

		private boolean projectLocked;

		private boolean submittedScanFileNotAllowed;
		
		private String importerSubDir;
		

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

		public boolean isSubmittedScanFileNotAllowed() {
			return submittedScanFileNotAllowed;
		}

		public void setSubmittedScanFileNotAllowed(boolean submittedScanFileNotAllowed) {
			this.submittedScanFileNotAllowed = submittedScanFileNotAllowed;
		}

		public String getImporterSubDir() {
			return importerSubDir;
		}

		public void setImporterSubDir(String importerSubDir) {
			this.importerSubDir = importerSubDir;
		}
		
	}
	
	
	

	/**
	 * 
	 *
	 */
	public static class UploadSubmitResult {

		private boolean statusSuccess;

		private boolean projectLocked;

		private boolean submittedScanFileNotAllowed;
		
		private String importerSubDir;
		

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

		public boolean isSubmittedScanFileNotAllowed() {
			return submittedScanFileNotAllowed;
		}

		public void setSubmittedScanFileNotAllowed(boolean submittedScanFileNotAllowed) {
			this.submittedScanFileNotAllowed = submittedScanFileNotAllowed;
		}

		public String getImporterSubDir() {
			return importerSubDir;
		}

		public void setImporterSubDir(String importerSubDir) {
			this.importerSubDir = importerSubDir;
		}

		
	}

}
