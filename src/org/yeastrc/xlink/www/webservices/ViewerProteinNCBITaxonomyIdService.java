package org.yeastrc.xlink.www.webservices;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.NRProteinDAO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;


@Path("/proteinTaxonomyId")
public class ViewerProteinNCBITaxonomyIdService {

	private static final Logger log = Logger.getLogger(ViewerProteinNCBITaxonomyIdService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getDataForProtein") 
	public Map<Integer, Integer> getTaxonomyIdsDataForProteinIds( 
			@QueryParam( "project_id" ) Integer projectId,
			@QueryParam( "proteinIds" ) List<Integer>  proteinIds,
			@Context HttpServletRequest request )
	throws Exception {


		if ( projectId == null ) {

			String msg = "Provided project_id is null";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}

		if ( proteinIds == null || proteinIds.isEmpty() ) {

			String msg = "Provided proteinIds is null or empty";

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		
		try {
			

			// Get the session first.  
			HttpSession session = request.getSession();


			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			


			Map<Integer, Integer> proteinIdsTaxonomyIdsMap = new HashMap<Integer, Integer>();
			
			for ( Integer proteinId : proteinIds ) {

				//  don't load duplicates;

				if ( ! proteinIdsTaxonomyIdsMap.containsKey( proteinId ) ) {

					// get proteins to get sequences for protein id
					NRProteinDTO protein = NRProteinDAO.getInstance().getNrProtein( proteinId );

					int taxonomyId = protein.getTaxonomyId();

					proteinIdsTaxonomyIdsMap.put( proteinId, taxonomyId );
				}
			}
			
			return proteinIdsTaxonomyIdsMap;
			
		} catch ( WebApplicationException e ) {

			throw e;
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}	
	}
	
	
}
