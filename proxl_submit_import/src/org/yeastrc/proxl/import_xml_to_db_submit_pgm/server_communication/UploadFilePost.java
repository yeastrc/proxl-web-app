package org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.JSONStringCharsetConstants;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.SendToServerConstants;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportServerReponseException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class UploadFilePost {
	

	private static final Logger log = Logger.getLogger(UploadFilePost.class);


	private static final UploadFilePost instance = new UploadFilePost();

	private UploadFilePost() { }
	public static UploadFilePost getInstance() { return instance; }

	public static final String SUB_URL = "/uploadFileForImport?";

	
	
	
	
	public UploadFileResult uploadFilePost(
			
			File uploadFile,
			int fileIndex,
			int fileType,
			String projectIdString,
			String uploadKey,
			String baseURL, 
			HttpClient httpclient ) throws Exception {
		
		String filename = uploadFile.getName();
		
		String filenameURIEncoded = URLEncoder.encode( filename, SendToServerConstants.ENCODING_CHARACTER_SET );
		
		String url = baseURL + SUB_URL
				+ "upload_key=" +  uploadKey
				+ "&project_id=" + projectIdString
				+ "&file_index=" + fileIndex
				+ "&file_type=" + fileType
				+ "&filename=" + filenameURIEncoded;
		
		

		HttpPost post = null;
		HttpResponse response = null;

		InputStream responseInputStream = null;
		
		
		JSON_Servlet_Response_Object uploadFileResponse = null;
		
		try {

			post = new HttpPost( url );
			
			FileBody fileBody = new FileBody( uploadFile, ContentType.DEFAULT_BINARY );
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addPart( SendToServerConstants.UPLOAD_FILE_FORM_NAME, fileBody);
			HttpEntity httpEntity = builder.build();
			
			post.setEntity( httpEntity );

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
			
			uploadFileResponse = jacksonJSON_Mapper.readValue( responseBytesJustData, JSON_Servlet_Response_Object.class );
			
			
			if ( ! uploadFileResponse.statusSuccess ) {
				
				String msg = "status response not true";
				log.error( msg );
//				throw new Exception(msg);
			}
			
			UploadFileResult uploadFileResult = new UploadFileResult();
			
			uploadFileResult.statusSuccess = uploadFileResponse.statusSuccess;

			return uploadFileResult;

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
	
	
	public static class UploadFileResult {

		private boolean statusSuccess;

		public boolean isStatusSuccess() {
			return statusSuccess;
		}

		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}

	}

	/**
	 * Must match class in web app class UploadFileForImportServlet
	 *
	 */

	private static class JSON_Servlet_Response_Object {

		
		
		private boolean statusSuccess;

		//  These are populated for FileSizeLimitExceededException exception
		private boolean fileSizeLimitExceeded;
		private long maxSize;
		private String maxSizeFormatted;
		
		private boolean uploadFile_fieldNameInvalid;
		private boolean moreThanOneuploadedFile;
		private boolean filenameInFormNotMatchFilenameInQueryString;
		private boolean noUploadedFile;
		
		private boolean uploadKeyNotValid;
		
		private boolean proxlXMLFileFailsInitialParse;
		private boolean proxlXMLFilerootXMLNodeIncorrect;
		
		private boolean scanFileNotAllowed;

		private boolean scanFilenameSuffixNotValid;
		private boolean scanFileFailsInitialParse;
		private boolean scanFilerootXMLNodeIncorrect;
		
		private boolean projectLocked; 
		
		
		
		@SuppressWarnings("unused")
		public boolean isStatusSuccess() {
			return statusSuccess;
		}
		@SuppressWarnings("unused")
		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}

		@SuppressWarnings("unused")
		public boolean isFileSizeLimitExceeded() {
			return fileSizeLimitExceeded;
		}
		@SuppressWarnings("unused")
		public void setFileSizeLimitExceeded(boolean fileSizeLimitExceeded) {
			this.fileSizeLimitExceeded = fileSizeLimitExceeded;
		}
		@SuppressWarnings("unused")
		public long getMaxSize() {
			return maxSize;
		}
		@SuppressWarnings("unused")
		public void setMaxSize(long maxSize) {
			this.maxSize = maxSize;
		}
		@SuppressWarnings("unused")
		public String getMaxSizeFormatted() {
			return maxSizeFormatted;
		}
		@SuppressWarnings("unused")
		public void setMaxSizeFormatted(String maxSizeFormatted) {
			this.maxSizeFormatted = maxSizeFormatted;
		}
		@SuppressWarnings("unused")
		public boolean isUploadFile_fieldNameInvalid() {
			return uploadFile_fieldNameInvalid;
		}
		@SuppressWarnings("unused")
		public void setUploadFile_fieldNameInvalid(boolean uploadFile_fieldNameInvalid) {
			this.uploadFile_fieldNameInvalid = uploadFile_fieldNameInvalid;
		}
		@SuppressWarnings("unused")
		public boolean isMoreThanOneuploadedFile() {
			return moreThanOneuploadedFile;
		}
		@SuppressWarnings("unused")
		public void setMoreThanOneuploadedFile(boolean moreThanOneuploadedFile) {
			this.moreThanOneuploadedFile = moreThanOneuploadedFile;
		}
		@SuppressWarnings("unused")
		public boolean isFilenameInFormNotMatchFilenameInQueryString() {
			return filenameInFormNotMatchFilenameInQueryString;
		}
		@SuppressWarnings("unused")
		public void setFilenameInFormNotMatchFilenameInQueryString(
				boolean filenameInFormNotMatchFilenameInQueryString) {
			this.filenameInFormNotMatchFilenameInQueryString = filenameInFormNotMatchFilenameInQueryString;
		}
		@SuppressWarnings("unused")
		public boolean isNoUploadedFile() {
			return noUploadedFile;
		}
		@SuppressWarnings("unused")
		public void setNoUploadedFile(boolean noUploadedFile) {
			this.noUploadedFile = noUploadedFile;
		}
		@SuppressWarnings("unused")
		public boolean isProjectLocked() {
			return projectLocked;
		}
		@SuppressWarnings("unused")
		public void setProjectLocked(boolean projectLocked) {
			this.projectLocked = projectLocked;
		}
		@SuppressWarnings("unused")
		public boolean isProxlXMLFileFailsInitialParse() {
			return proxlXMLFileFailsInitialParse;
		}
		@SuppressWarnings("unused")
		public void setProxlXMLFileFailsInitialParse(
				boolean proxlXMLFileFailsInitialParse) {
			this.proxlXMLFileFailsInitialParse = proxlXMLFileFailsInitialParse;
		}
		@SuppressWarnings("unused")
		public boolean isProxlXMLFilerootXMLNodeIncorrect() {
			return proxlXMLFilerootXMLNodeIncorrect;
		}
		@SuppressWarnings("unused")
		public void setProxlXMLFilerootXMLNodeIncorrect(
				boolean proxlXMLFilerootXMLNodeIncorrect) {
			this.proxlXMLFilerootXMLNodeIncorrect = proxlXMLFilerootXMLNodeIncorrect;
		}
		@SuppressWarnings("unused")
		public boolean isUploadKeyNotValid() {
			return uploadKeyNotValid;
		}
		@SuppressWarnings("unused")
		public void setUploadKeyNotValid(boolean uploadKeyNotValid) {
			this.uploadKeyNotValid = uploadKeyNotValid;
		}
		@SuppressWarnings("unused")
		public boolean isScanFilenameSuffixNotValid() {
			return scanFilenameSuffixNotValid;
		}
		@SuppressWarnings("unused")
		public void setScanFilenameSuffixNotValid(boolean scanFilenameSuffixNotValid) {
			this.scanFilenameSuffixNotValid = scanFilenameSuffixNotValid;
		}
		@SuppressWarnings("unused")
		public boolean isScanFileFailsInitialParse() {
			return scanFileFailsInitialParse;
		}
		@SuppressWarnings("unused")
		public void setScanFileFailsInitialParse(boolean scanFileFailsInitialParse) {
			this.scanFileFailsInitialParse = scanFileFailsInitialParse;
		}
		@SuppressWarnings("unused")
		public boolean isScanFilerootXMLNodeIncorrect() {
			return scanFilerootXMLNodeIncorrect;
		}
		@SuppressWarnings("unused")
		public void setScanFilerootXMLNodeIncorrect(boolean scanFilerootXMLNodeIncorrect) {
			this.scanFilerootXMLNodeIncorrect = scanFilerootXMLNodeIncorrect;
		}
		@SuppressWarnings("unused")
		public boolean isScanFileNotAllowed() {
			return scanFileNotAllowed;
		}
		@SuppressWarnings("unused")
		public void setScanFileNotAllowed(boolean scanFileNotAllowed) {
			this.scanFileNotAllowed = scanFileNotAllowed;
		}

	}

}
