package org.yeastrc.xlink.www.webservices;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpSession;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ConfigSystemDAO;
import org.yeastrc.xlink.www.dto.ConfigSystemDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;




@Path("/config")
public class ConfigService {

	private static final Logger log = Logger.getLogger(ConfigService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list") 
	public ConfigListResult getViewerData( 
										  @Context HttpServletRequest request )
	throws Exception {
		

		
		try {


			//  Restricted to users with ACCESS_LEVEL_ADMIN or better


			// Get the session first.  
//			HttpSession session = request.getSession();




			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}

//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();


			//  Test access at global level

			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();


			if ( ! authAccessLevel.isAdminAllowed() ) {

				//  No Access Allowed 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			////////   Auth complete

			//////////////////////////////////////////

			
			ConfigListResult configListResult = new ConfigListResult();
			
			List<ConfigSystemDTO> configList = ConfigSystemDAO.getInstance().getAll();
			
			configListResult.setConfigList( configList );
			
			return configListResult;

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


	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/save")
	public SaveResult save( SaveRequest saveRequest,
			@Context HttpServletRequest request ) throws Exception {


		
		try {


			//  Restricted to users with ACCESS_LEVEL_ADMIN or better


			// Get the session first.  
//			HttpSession session = request.getSession();




			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}

//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();


			//  Test access at global level

			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();


			if ( ! authAccessLevel.isAdminAllowed() ) {

				//  No Access Allowed 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			////////   Auth complete

			//////////////////////////////////////////

			
			
			
			
			
			SaveResult saveResult = new SaveResult();
			
			
			
			
			
			return saveResult;
			

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
	 * 
	 *
	 */
	private static class SaveRequest {
		
		
	}
	

	/**
	 * 
	 *
	 */
	private static class SaveResult {
		
		
	}
	
	/**
	 * 
	 *
	 */
	private static class ConfigListResult {
		
		private List<ConfigSystemDTO> configList;

		public List<ConfigSystemDTO> getConfigList() {
			return configList;
		}

		public void setConfigList(List<ConfigSystemDTO> configList) {
			this.configList = configList;
		}
	}
	
}
