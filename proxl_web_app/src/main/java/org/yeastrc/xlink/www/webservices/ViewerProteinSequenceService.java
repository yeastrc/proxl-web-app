package org.yeastrc.xlink.www.webservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.factories.ProteinSequenceVersionObjectFactory;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

@Path("/proteinSequence")
public class ViewerProteinSequenceService {

	private static final Logger log = LoggerFactory.getLogger( ViewerProteinSequenceService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getDataForProtein") 
	public Map<Integer, String> getSequencesDataForProteinIds( 
			@QueryParam( "project_id" ) Integer projectId,
			@QueryParam( "proteinIdsToGetSequence" ) List<Integer>  proteinIdsToGetSequence,
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
		if ( proteinIdsToGetSequence == null || proteinIdsToGetSequence.isEmpty() ) {
			String msg = "Provided proteinIdsToGetSequence is null or empty";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		try {
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			Map<Integer, String> proteinIdsSequencesMap = new HashMap<Integer, String>();
			for ( Integer proteinId : proteinIdsToGetSequence ) {
				//  don't load duplicates;
				if ( ! proteinIdsSequencesMap.containsKey( proteinId ) ) {
					// get proteins to get sequences for protein id
					ProteinSequenceVersionObject protein = ProteinSequenceVersionObjectFactory.getProteinSequenceVersionObject( proteinId );
					String proteinSequence = protein.getProteinSequenceObject().getSequence();
					proteinIdsSequencesMap.put( proteinId, proteinSequence );
				}
			}
			return proteinIdsSequencesMap;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}	
	}
}
