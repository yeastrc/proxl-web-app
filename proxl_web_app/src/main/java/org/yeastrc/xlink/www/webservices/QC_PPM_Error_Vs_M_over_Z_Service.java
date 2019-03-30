package org.yeastrc.xlink.www.webservices;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.main.PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.main.PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs.PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_Method_Response;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;


@Path("/qc/dataPage")
public class QC_PPM_Error_Vs_M_over_Z_Service {

	private static final Logger log = LoggerFactory.getLogger( QC_PPM_Error_Vs_M_over_Z_Service.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ppmErrorVsM_over_Z") 
	public byte[]
		getPPM_Error_Histogram_For_PSMPeptideCutoffs( @QueryParam( "project_search_id" ) List<Integer> projectSearchIdList,
										  @QueryParam( "filterCriteria" ) String filterCriteria_JSONString,
										  @Context HttpServletRequest request )
	throws Exception {
	
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided project_search_id is null or project_search_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( projectSearchIdList.size() != 1 ) {
			String msg = "Only 1 project_search_id is accepted";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( filterCriteria_JSONString ) ) {
			String msg = "Provided filterCriteria is null or filterCriteria is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}

		String requestQueryString = request.getQueryString();
		
		int projectSearchId = projectSearchIdList.get( 0 );
		
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for this search
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.add( projectSearchId );

			List<Integer> projectIdsFromProjectSearchIds = 
					ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromProjectSearchIds.isEmpty() ) {
				// should never happen
				@SuppressWarnings("unchecked")
				String msg = "No project ids for project search ids: " + StringUtils.join( projectSearchIdList );
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromProjectSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromProjectSearchIds.get( 0 );
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
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			//  Test access to the project id
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////

			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			if ( search == null ) {
				String msg = "projectSearchId '" + projectSearchId + "' not found in the database. User taken to home page.";
				log.warn( msg );
				//  Search not found, the data on the page they are requesting does not exist.
				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);			
			}
				
			PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_Method_Response ppm_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_Method_Response =
					PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs.getInstance()
					.getPPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs( 
							requestQueryString, 
							PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs.ForDownload.NO,
							filterCriteria_JSONString, search );

			return ppm_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_Method_Response.getResultsAsBytes();
			
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
	
//	/**
//	 * 
//	 *
//	 */
//	public static class WebserviceResult_getPPM_Error_Histogram_For_PSMPeptideCutoffs {
//		
//		PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_Result ppmErrorVsM_over_ZScatterPlotResult;
//
//		public PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_Result getPpmErrorVsM_over_ZScatterPlotResult() {
//			return ppmErrorVsM_over_ZScatterPlotResult;
//		}
//
//		public void setPpmErrorVsM_over_ZScatterPlotResult(
//				PPM_Error_Vs_M_over_Z_ScatterPlot_For_PSMPeptideCutoffs_Result ppmErrorVsM_over_ZScatterPlotResult) {
//			this.ppmErrorVsM_over_ZScatterPlotResult = ppmErrorVsM_over_ZScatterPlotResult;
//		}
//
//	}
}
