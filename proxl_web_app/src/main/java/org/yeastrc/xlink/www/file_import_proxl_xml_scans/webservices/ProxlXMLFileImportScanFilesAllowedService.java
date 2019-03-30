package org.yeastrc.xlink.www.file_import_proxl_xml_scans.webservices;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.file_import_proxl_xml_scans.utils.IsScanFileImportAllowedViaWebSubmit;

/**
 * Are scan files allowed to be uploaded  
 * 
 */
@Path("/file_import_proxl_xml_scans")
public class ProxlXMLFileImportScanFilesAllowedService {

	private static final Logger log = LoggerFactory.getLogger( ProxlXMLFileImportScanFilesAllowedService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/scanFilesAllowed")
	public ScanFilesAllowedResponse  scanFilesAllowed( 
			@Context HttpServletRequest request ) throws Exception {
		try {
			ScanFilesAllowedResponse scanFilesAllowedResponse = new ScanFilesAllowedResponse();
			scanFilesAllowedResponse.statusSuccess = true;
			scanFilesAllowedResponse.scanFilesAllowed = 
					IsScanFileImportAllowedViaWebSubmit.getInstance().isScanFileImportAllowedViaWebSubmit() ;
			return scanFilesAllowedResponse;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	/**
	 * 
	 *
	 */
	public static class ScanFilesAllowedResponse {
		private boolean statusSuccess;
		private boolean scanFilesAllowed;
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
