package org.yeastrc.xlink.www.webservices;

import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dao.ProxlXMLFileImportTrackingRun_Base_DAO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dao.ProxlXMLFileImportTracking_Base_DAO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingRunDTO;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.internal_services.SendEmailForRunImportFinishInternalService;
/**
 * Service called when run import has completed for one entry in the import tracking table
 *
 */
@Path("/runImport")
public class RunImportFinishService {
	
	private static final Logger log = LoggerFactory.getLogger( RunImportFinishService.class);
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/onFinish")
	public OnFinishResult onFinish( OnFinishRequest webserviceRequest,
			@Context HttpServletRequest request ) throws Exception {
		if ( webserviceRequest == null ) {
			String msg = "webserviceRequest == null: " ;
			log.warn( msg );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  return 400 error
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
					.build()
					);	
		}
		if ( webserviceRequest.trackingId == null ) {
			String msg = "webserviceRequest.trackingId == null: " ;
			log.warn( msg );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  return 400 error
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
					.build()
					);	
		}
		if ( webserviceRequest.runId == null ) {
			String msg = "webserviceRequest.runId == null: " ;
			log.warn( msg );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  return 400 error
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
					.build()
					);	
		}
		if ( webserviceRequest.sdFSOdsjaklOWQJLwuiroKXLNOqklklzuxo == null ) {
			String msg = "webserviceRequest.sdFSOdsjaklOWQJLwuiroKXLNOqklklzuxo == null: " ;
			log.warn( msg );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  return 400 error
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
					.build()
					);	
		}
		if ( webserviceRequest.sdFSOdsjaklOWQJLwuiroKXLNOqklklzuxo == false ) {
			String msg = "webserviceRequest.sdFSOdsjaklOWQJLwuiroKXLNOqklklzuxo == false: " ;
			log.warn( msg );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  return 400 error
					.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
					.build()
					);	
		}
		try {
			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO =
					ProxlXMLFileImportTracking_Base_DAO.getInstance().getItem( webserviceRequest.trackingId );
			if ( proxlXMLFileImportTrackingDTO == null ) {
				String msg = "proxlXMLFileImportTrackingDTO == null: webserviceRequest.trackingId: " 
						+ webserviceRequest.trackingId ;
				log.warn( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  return 400 error
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
						.build()
						);	
			}
			ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO =
					ProxlXMLFileImportTrackingRun_Base_DAO.getInstance().getItem( webserviceRequest.runId );
			if ( proxlXMLFileImportTrackingRunDTO == null ) {
				String msg = "proxlXMLFileImportTrackingRunDTO == null: webserviceRequest.runId: "
						+ webserviceRequest.runId ;
				log.warn( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  return 400 error
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
						.build()
						);	
			}
			SendEmailForRunImportFinishInternalService.getInstance()
			.sendEmailForRunImportFinishInternalService( proxlXMLFileImportTrackingDTO, proxlXMLFileImportTrackingRunDTO );
			OnFinishResult webserviceResult = new OnFinishResult();
			webserviceResult.status = true;
			return webserviceResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);			
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
	 * Keep in sync with the Run Importer code
	 *
	 */
	private static class OnFinishRequest {
		private Integer trackingId;
		private Integer runId;
		/**
		 * Garbage variable that has to be guessed
		 */
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
	}
	
	/**
	 * Keep in sync with the Run Importer code
	 *
	 */
	private static class OnFinishResult {
		private boolean status;
		@SuppressWarnings("unused")
		public void setStatus(boolean status) {
			this.status = status;
		}
		@SuppressWarnings("unused")
		public boolean isStatus() {
			return status;
		}
	}
}
