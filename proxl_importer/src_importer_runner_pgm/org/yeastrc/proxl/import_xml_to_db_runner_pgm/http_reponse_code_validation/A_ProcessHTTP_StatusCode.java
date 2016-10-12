package org.yeastrc.proxl.import_xml_to_db_runner_pgm.http_reponse_code_validation;

import org.apache.http.HttpStatus;
import org.yeastrc.proxl.import_xml_to_db_runner_pgm.exceptions.ProxlSubImportServerReponseException;

import org.apache.log4j.Logger;


/**
 * 
 *
 */
public class A_ProcessHTTP_StatusCode {


	private static final Logger log = Logger.getLogger(A_ProcessHTTP_StatusCode.class);


	private static final A_ProcessHTTP_StatusCode instance = new A_ProcessHTTP_StatusCode();

	private A_ProcessHTTP_StatusCode() { }
	public static A_ProcessHTTP_StatusCode getInstance() { return instance; }

	
	/**
	 * Throws ProxlSubImportServerReponseException if not ok
	 * @param httpStatusCode
	 * @throws ProxlSubImportServerReponseException - thrown if httpStatusCode != HttpStatus.SC_OK
	 */
	public void processHTTP_StatusCode( int httpStatusCode, String baseURL ) throws ProxlSubImportServerReponseException {
		

		if ( httpStatusCode == HttpStatus.SC_OK ) {
			
			return;
		}
		

		if ( httpStatusCode == HttpStatus.SC_BAD_REQUEST ) {
			
			// 400 error
			
			log.error( "Request to server contains incorrect parameters (400 code)." );
			log.error( "Confirm that this is the correct version of the program" );

			log.error( "If this error continues, contact the administrator of your Proxl Instance." );
			
			throw new ProxlSubImportServerReponseException();
		}
		
		

		if ( httpStatusCode == HttpStatus.SC_UNAUTHORIZED ) {
			
			// 401 error - used for no session
			
			log.error( "The login session to the server has expired (401 code)." );
			log.error( "Please try again." );

			log.error( "If this error continues, contact the administrator of your Proxl Instance." );
			
			throw new ProxlSubImportServerReponseException();
		}
		

		if ( httpStatusCode == HttpStatus.SC_FORBIDDEN ) {
			
			// 403 error
			
			log.error( "Request to server is forbidden (403 code)." );
			log.error( "Confirm that you are allowed access to the server." );

			log.error( "If this error continues, contact the administrator of your Proxl Instance." );
			
			throw new ProxlSubImportServerReponseException();
		}
		

		if ( httpStatusCode == HttpStatus.SC_NOT_FOUND ) {
			
			// 404 error
			
			log.error( "Server is not found at this URL (404 code)." );
			log.error( "Server URL: " + baseURL );
			
			log.error( "Confirm that this URL is correct or contact the administrator of your Proxl Instance." );
			
			throw new ProxlSubImportServerReponseException();
		}
		

		if ( httpStatusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR ) {
			
			// 500 error
			
			log.error( "Server had an error (500 code)." );
			log.error( "Please try again." );

			log.error( "If this error continues, contact the administrator of your Proxl Instance." );
			
			throw new ProxlSubImportServerReponseException();
		}

		log.error( "Server returned an unexpected status code: " + httpStatusCode + "." );
		log.error( "Please contact the administrator of your Proxl Instance." );

		throw new ProxlSubImportServerReponseException();

		
	}
}
