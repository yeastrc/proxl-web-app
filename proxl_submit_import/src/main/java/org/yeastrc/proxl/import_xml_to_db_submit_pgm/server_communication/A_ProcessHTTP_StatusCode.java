package org.yeastrc.proxl.import_xml_to_db_submit_pgm.server_communication;

import org.apache.http.HttpStatus;

//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;



import org.yeastrc.proxl.import_xml_to_db_submit_pgm.config.ConfigParams;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportServerReponseException;

/**
 * 
 *
 */
public class A_ProcessHTTP_StatusCode {


//	private static final Logger log = LoggerFactory.getLogger( A_ProcessHTTP_StatusCode.class);


	private static final A_ProcessHTTP_StatusCode instance = new A_ProcessHTTP_StatusCode();

	private A_ProcessHTTP_StatusCode() { }
	public static A_ProcessHTTP_StatusCode getInstance() { return instance; }

	
	/**
	 * Throws ProxlSubImportServerReponseException if not ok
	 * @param httpStatusCode
	 * @throws ProxlSubImportServerReponseException - thrown if httpStatusCode != HttpStatus.SC_OK
	 */
	public void processHTTP_StatusCode( int httpStatusCode, String webserviceURL ) throws ProxlSubImportServerReponseException {
		

		if ( httpStatusCode == HttpStatus.SC_OK ) {
			
			return;
		}
		

		if ( httpStatusCode == HttpStatus.SC_BAD_REQUEST ) {
			
			// 400 error
			
			System.err.println( "Request to server contains incorrect parameters (400 code)." );
			System.err.println( "Confirm that this is the correct version of the program" );
			System.err.println( "Server webservice URL: " + webserviceURL );

			System.err.println( "If this error continues, contact the administrator of your Proxl Instance." );
			
			throw new ProxlSubImportServerReponseException();
		}
		
		

		if ( httpStatusCode == HttpStatus.SC_UNAUTHORIZED ) {
			
			// 401 error - used for no session
			
			System.err.println( "The login session to the server has expired (401 code)." );
			System.err.println( "Please try again." );
			System.err.println( "Server webservice URL: " + webserviceURL );

			System.err.println( "If this error continues, contact the administrator of your Proxl Instance." );
			
			throw new ProxlSubImportServerReponseException();
		}
		

		if ( httpStatusCode == HttpStatus.SC_FORBIDDEN ) {
			
			// 403 error
			
			System.err.println( "Request to server is forbidden (403 code)." );
			System.err.println( "Confirm that you are allowed access to the server." );
			System.err.println( "Server webservice URL: " + webserviceURL );

			System.err.println( "If this error continues, contact the administrator of your Proxl Instance." );
			
			throw new ProxlSubImportServerReponseException();
		}
		

		if ( httpStatusCode == HttpStatus.SC_NOT_FOUND ) {
			
			// 404 error
			
			System.err.println( "Server is not found at this URL (404 code)." );
			System.err.println( "Server webservice URL: " + webserviceURL );
			System.err.println( "Server URL from configuration: " + ConfigParams.getInstance().getProxlWebAppUrl() );
			
			System.err.println( "Confirm that this URL is correct or contact the administrator of your Proxl Instance." );
			
			throw new ProxlSubImportServerReponseException();
		}
		

		if ( httpStatusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR ) {
			
			// 500 error
			
			System.err.println( "Server had an error (500 code)." );
			System.err.println( "Please try again." );
			System.err.println( "Server webservice URL: " + webserviceURL );

			System.err.println( "If this error continues, contact the administrator of your Proxl Instance." );
			
			throw new ProxlSubImportServerReponseException();
		}

		System.err.println( "Server returned an unexpected status code: " + httpStatusCode + "." );
		System.err.println( "Server webservice URL: " + webserviceURL );
		System.err.println( "Please contact the administrator of your Proxl Instance." );

		throw new ProxlSubImportServerReponseException();

		
	}
}
