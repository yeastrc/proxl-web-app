package org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.apache.http.client.HttpClient;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.SendToServerConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class UploadFilePost {
	
	private static final Logger log = LoggerFactory.getLogger( UploadFilePost.class);

	private static final UploadFilePost instance = new UploadFilePost();
	private UploadFilePost() { }
	public static UploadFilePost getInstance() { return instance; }

	public static final String SUB_URL = "/uploadProxlXmlOrScanFileForImport.do?";

	/**
	 * @param uploadFile
	 * @param fileIndex
	 * @param fileType
	 * @param projectIdString
	 * @param uploadKey
	 * @param baseURL
	 * @param httpclient
	 * @return
	 * @throws Exception
	 */
	public UploadFileResult uploadFilePost(
			
			String jsessionIdCookieResponse,
			
			File uploadFile,
			int fileIndex,
			int fileType,
			String projectIdString,
			String uploadKey,
			String baseURL, 
			HttpClient httpclient ) throws Exception {
		
		String filename = uploadFile.getName();
		
		long numberOfBytesToSend = uploadFile.length();
		
		String uploadFileWPathCanonical = uploadFile.getCanonicalPath();
		String uploadFileWPathAbsolute = uploadFile.getAbsolutePath();
		
		String url = baseURL + SUB_URL;


		//   Create object for connecting to server
		URL urlObject;
		try {
			urlObject = new URL( url );
		} catch (MalformedURLException e) {
			throw e;
		}
		//   Open connection to server
		URLConnection urlConnection;
		try {
			urlConnection = urlObject.openConnection();
		} catch (IOException e) {
			throw e;
		}
		// Downcast URLConnection to HttpURLConnection to allow setting of HTTP parameters 
		if ( ! ( urlConnection instanceof HttpURLConnection ) ) {
			throw new RuntimeException( "if ( ! ( urlConnection instanceof HttpURLConnection ) ) {" );
		}
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) urlConnection;
		} catch (Exception e) {
			throw e;
		}
		//  Set HttpURLConnection properties

		//   Set Number of bytes to send, can be int or long
		//     ( Calling setFixedLengthStreamingMode(...) allows > 2GB to be sent 
		//       and HttpURLConnection does NOT buffer the sent bytes using ByteArrayOutputStream )
		httpURLConnection.setFixedLengthStreamingMode( numberOfBytesToSend );
		
		httpURLConnection.setRequestProperty( 
				SendToServerConstants.UPLOAD_FILE_HEADER_NAME_UPLOADED_FILENAME_W_PATH_CANONICAL, uploadFileWPathCanonical );
		httpURLConnection.setRequestProperty( 
				SendToServerConstants.UPLOAD_FILE_HEADER_NAME_UPLOADED_FILENAME_W_PATH_ABSOLUTE, uploadFileWPathAbsolute );

		httpURLConnection.setRequestProperty( 
				SendToServerConstants.UPLOAD_FILE_HEADER_NAME_UPLOAD_KEY , String.valueOf( uploadKey ) );
		httpURLConnection.setRequestProperty( 
				SendToServerConstants.UPLOAD_FILE_HEADER_NAME_PROJECT_ID , String.valueOf( projectIdString ) );
		httpURLConnection.setRequestProperty( 
				SendToServerConstants.UPLOAD_FILE_HEADER_NAME_FILE_INDEX , String.valueOf( fileIndex ) );
		httpURLConnection.setRequestProperty( 
				SendToServerConstants.UPLOAD_FILE_HEADER_NAME_FILE_TYPE , String.valueOf( fileType ) );
		httpURLConnection.setRequestProperty( 
				SendToServerConstants.UPLOAD_FILE_HEADER_NAME_FILENAME , String.valueOf( filename ) );


		
		httpURLConnection.setRequestProperty( "Cookie", jsessionIdCookieResponse );
		
		httpURLConnection.setDoOutput(true);
		// Send post request to server
		try {  //  Overall try/catch block to put "httpURLConnection.disconnect();" in the finally block

			httpURLConnection.connect();
		} finally {
			
		}
		//  Send bytes to server
		OutputStream outputStream = null;
		FileInputStream fileInputStream = null; // for when send file
		try {
			outputStream = httpURLConnection.getOutputStream();
				//  Send file contents to server
				fileInputStream = new FileInputStream( uploadFile );
				int byteArraySize = 5000;
				byte[] data = new byte[ byteArraySize ];
				while (true) {
					int bytesRead = fileInputStream.read( data );
					if ( bytesRead == -1 ) {  // end of input
						break;
					}
					if ( bytesRead > 0 ) {
						outputStream.write( data, 0, bytesRead );
					}
				}
			
		} catch ( IOException e ) {
			throw e;
		} finally {
			outputStream.close();
			fileInputStream.close();
		}

		int httpResponseCode = httpURLConnection.getResponseCode();

		//  Throws ProxlSubImportServerReponseException if  httpStatusCode != HttpStatus.SC_OK (200)
		A_ProcessHTTP_StatusCode.getInstance().processHTTP_StatusCode( httpResponseCode, url );
		
		//  Get response XML from server
		ByteArrayOutputStream outputStreamBufferOfServerResponse = new ByteArrayOutputStream( 1000000 );
		InputStream inputStream = null;
		try {
			inputStream = httpURLConnection.getInputStream();
			int nRead;
			byte[] data = new byte[ 16384 ];
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				outputStreamBufferOfServerResponse.write(data, 0, nRead);
			}
		} catch ( IOException e ) {
			throw e;
		} finally {
			if ( inputStream != null ) {
				try {
					inputStream.close();
				} catch ( IOException e ) {
					throw e;
				}
			}
		}
		byte[] serverResponseByteArray = outputStreamBufferOfServerResponse.toByteArray();

		String responseAsString = new String( serverResponseByteArray, StandardCharsets.UTF_8 );
		
		if ( log.isDebugEnabled() ) {
			
			System.out.println( "UploadInitPost response: " );
			System.out.println( responseAsString );
		}

		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

		JSON_Servlet_Response_Object uploadFileResponse = null;
		
		uploadFileResponse = jacksonJSON_Mapper.readValue( serverResponseByteArray, JSON_Servlet_Response_Object.class );
		
		if ( ! uploadFileResponse.statusSuccess ) {
			
			String msg = "Send File status response not true";
			System.err.println( msg );

			if ( ! uploadFileResponse.proxlXMLFilerootXMLNodeIncorrect ) {
				
				System.err.println( "Proxl  XML file uploaded but file not formatted correctly" );
			}
//			throw new Exception(msg);
		}
		
		
		
		UploadFileResult uploadFileResult = new UploadFileResult();
		uploadFileResult.statusSuccess = uploadFileResponse.statusSuccess;
		return uploadFileResult;
			
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
