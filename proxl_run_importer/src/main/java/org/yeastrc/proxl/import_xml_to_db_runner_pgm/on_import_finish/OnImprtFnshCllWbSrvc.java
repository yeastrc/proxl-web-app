package org.yeastrc.proxl.import_xml_to_db_runner_pgm.on_import_finish;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.config.ImporterRunnerConfigData;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.constants.JSONStringCharsetConstants;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.http_reponse_code_validation.A_ProcessHTTP_StatusCode;

import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Call a web service on the Proxl Web app when finish running a single Import
 *
 */
public class OnImprtFnshCllWbSrvc {

	private static Logger log = Logger.getLogger( OnImprtFnshCllWbSrvc.class );
	
	
	public static final String SUB_URL = "/services/runImport/onFinish";


	/**
	 * private constructor
	 */
	private OnImprtFnshCllWbSrvc() { }

	/**
	 * @return newly created instance
	 */
	public static OnImprtFnshCllWbSrvc getInstance() { 
		return new OnImprtFnshCllWbSrvc(); 
	}
	
	/**
	 * @throws Exception 
	 * @param trackingId
	 * @param runId
	 * @throws IOException
	 * @throws  
	 */
	public void callProxlWebServiceOnSingleImportFinish( int trackingId, int runId ) throws Exception {
		
		String baseURL = ImporterRunnerConfigData.getProxlWebAppBaseURL();
		
		if ( StringUtils.isEmpty( baseURL) ) {
			
			//  No URL to connect to to notify that import is finished
			
			return;  //  EARLY EXIT
		}
		
		byte[] request_JSON = null;
		
		try {
			
			OnFinishRequest request = new OnFinishRequest();
			
			request.trackingId = trackingId;
			request.runId = runId;
			request.sdFSOdsjaklOWQJLwuiroKXLNOqklklzuxo = true;
			


			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object

			request_JSON = mapper.writeValueAsBytes( request );

			
		} catch ( Exception e ) {
			
			String msg = "Error encoding data to send to web app notifying web app that run import completed.";
			log.error( msg, e );

			throw e;
		}

		CloseableHttpClient httpclient = null;

		try {

			//  Create Apache HTTP Client instance for connecting to the web app on the server

			httpclient = HttpClients.createDefault();

			String url = baseURL + SUB_URL;

			HttpPost post = null;
			HttpResponse response = null;

			InputStream responseInputStream = null;
			
				post = new HttpPost( url );
				
				ByteArrayEntity byteArrayEntity = new ByteArrayEntity( request_JSON, ContentType.APPLICATION_JSON );

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
				A_ProcessHTTP_StatusCode.getInstance().processHTTP_StatusCode( httpStatusCode, baseURL );
				
				


				ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
				
				OnFinishResult onFinishResult = jacksonJSON_Mapper.readValue( responseBytesJustData, OnFinishResult.class );
				
				
				if ( ! onFinishResult.isStatus() ) {
					
					String msg = "status response not true";
					log.error( msg );
//					throw new Exception(msg);
				}
				
		} catch (Exception e ) {
			
			String msg = "Error notifying web app that run import completed.";
			log.error( msg, e );
					
			throw e;

		} finally {

			try {

				// When HttpClient instance is no longer needed,
				// shut down the connection manager to ensure
				// immediate deallocation of all system resources
				httpclient.close();

			} catch (Exception e ) {

				String msg = "Error closing connection for notifying web app that run import completed.";
				log.error( msg, e );

				throw e;
			}
		}
	}
	

	/**
	 * Keep in sync with the Web App code
	 *
	 */
	private static class OnFinishRequest {
		
		@SuppressWarnings("unused")
		private Integer trackingId;
		@SuppressWarnings("unused")
		private Integer runId;
		/**
		 * Garbage variable that has to be guessed
		 */
		@SuppressWarnings("unused")
		private Boolean sdFSOdsjaklOWQJLwuiroKXLNOqklklzuxo;
		
		@SuppressWarnings("unused")
		public void setTrackingId(Integer trackingId) {
			this.trackingId = trackingId;
		}
		@SuppressWarnings("unused")
		public void setRunId(Integer runId) {
			this.runId = runId;
		}
		@SuppressWarnings("unused")
		public void setSdFSOdsjaklOWQJLwuiroKXLNOqklklzuxo(
				Boolean sdFSOdsjaklOWQJLwuiroKXLNOqklklzuxo) {
			this.sdFSOdsjaklOWQJLwuiroKXLNOqklklzuxo = sdFSOdsjaklOWQJLwuiroKXLNOqklklzuxo;
		}
		@SuppressWarnings("unused")
		public Integer getTrackingId() {
			return trackingId;
		}
		@SuppressWarnings("unused")
		public Integer getRunId() {
			return runId;
		}
		@SuppressWarnings("unused")
		public Boolean getSdFSOdsjaklOWQJLwuiroKXLNOqklklzuxo() {
			return sdFSOdsjaklOWQJLwuiroKXLNOqklklzuxo;
		}
	}
	
	/**
	 * Keep in sync with the Web App code
	 *
	 */
	private static class OnFinishResult {

		private boolean status;
		

		@SuppressWarnings("unused")
		public void setStatus(boolean status) {
			this.status = status;
		}

//		@SuppressWarnings("unused")
		public boolean isStatus() {
			return status;
		}
	}
	
}
