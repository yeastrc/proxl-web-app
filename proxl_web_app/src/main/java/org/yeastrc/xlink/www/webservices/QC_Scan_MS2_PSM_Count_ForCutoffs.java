package org.yeastrc.xlink.www.webservices;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.form_query_json_objects.QCPageQueryJSONRoot;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.qc_data.a_request_json_root.QCPageRequestJSONRoot;
import org.yeastrc.xlink.www.qc_data.scan_level_data.main.MS2_Counts_Where_PSMs_MeetsCutoffs;
import org.yeastrc.xlink.www.qc_data.scan_level_data.objects.MS2_Counts_Where_PSMs_MeetsCutoffsResults;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;

/**
 * Number of MS2/PSM records that have PSMs that meet cutoffs
 * 
 * Input is project_search_id and scan_file_id
 * 
 * In order to use this with more than one project_search_id, 
 * the SQL query would require a DISTINCT to not count more than one PSM for a given scan.
 *
 */
@Path("/qc/dataPage")
public class QC_Scan_MS2_PSM_Count_ForCutoffs {

	private static final Logger log = LoggerFactory.getLogger( QC_Scan_MS2_PSM_Count_ForCutoffs.class);

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/ms2Counts") 
	public WebserviceResult_getQC_Scan_MS2_PSM_Count_ForCutoffs
		getQC_Scan_MS2_PSM_Count_ForCutoffs( 
				byte[] requestJSONBytes,
				@Context HttpServletRequest request ) {

		if ( requestJSONBytes == null || requestJSONBytes.length == 0 ) {
			String msg = "requestJSONBytes is null or requestJSONBytes is empty";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity(  )
		    	        .build()
		    	        );
		}
		
		QCPageRequestJSONRoot qcPageRequestJSONRoot = 
				Unmarshal_RestRequest_JSON_ToObject.getInstance()
				.getObjectFromJSONByteArray(requestJSONBytes, QCPageRequestJSONRoot.class );
		
		List<Integer> projectSearchIdList = qcPageRequestJSONRoot.getProjectSearchIds();
		QCPageQueryJSONRoot qcPageQueryJSONRoot = qcPageRequestJSONRoot.getQcPageQueryJSONRoot();
		Integer scanFileId = qcPageRequestJSONRoot.getScanFileId();

		try {
			if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
				String msg = ": Provided projectSearchIds not provided or is empty";
				log.warn( msg );
				throw new WebApplicationException(
						Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
						.build()
						);
			}
			if ( projectSearchIdList.size() != 1 ) {
				String msg = ": Provided projectSearchIds not size 1";
				log.warn( msg );
				throw new WebApplicationException(
						Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
						.build()
						);
			}
			if ( scanFileId == null || scanFileId == 0 ) {
				String msg = ": Provided scan_file_id is zero or wasn't provided";
				log.warn( msg );
				throw new WebApplicationException(
						Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
						.build()
						);
			}
			
			Integer projectSearchId = projectSearchIdList.get(0);
			
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsCollection = new HashSet<Integer>( );
			projectSearchIdsCollection.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsCollection );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchId: " + projectSearchId;
				log.warn( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			//		UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					//  No User session 
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			////////   Auth complete
			//////////////////////////////////////////

			Integer searchId =
					MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
			if ( searchId == null ) {
				String msg = ": No searchId found for projectSearchId: " + projectSearchId;
				log.warn( msg );
				throw new WebApplicationException(
						Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
						.build()
						);
			}

			MS2_Counts_Where_PSMs_MeetsCutoffsResults ms2_Counts_Where_PSMs_MeetsCutoffsResults =
					MS2_Counts_Where_PSMs_MeetsCutoffs.getInstance()
					.getMS2_Counts_Where_PSMs_MeetsCutoffs(
							qcPageQueryJSONRoot,
							projectSearchId, searchId, scanFileId );

			WebserviceResult_getQC_Scan_MS2_PSM_Count_ForCutoffs webserviceResult = 
					new WebserviceResult_getQC_Scan_MS2_PSM_Count_ForCutoffs();

			webserviceResult.crosslinkCount = ms2_Counts_Where_PSMs_MeetsCutoffsResults.getCrosslinkCount();
			webserviceResult.looplinkCount = ms2_Counts_Where_PSMs_MeetsCutoffsResults.getLooplinkCount();
			webserviceResult.unlinkedCount = ms2_Counts_Where_PSMs_MeetsCutoffsResults.getUnlinkedCount();

			return webserviceResult;
		} catch( WebApplicationException e ) {
			throw e;
		} catch( Exception e ) {
			String msg = "Exception processing getQC_Scan_MS2_PSM_Count_ForCutoffs(...): ";
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status(WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE)  //  return 400 error
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT )
					.build()
					);
		}
	}
	
	/**
	 * 
	 *
	 */
	public static class WebserviceResult_getQC_Scan_MS2_PSM_Count_ForCutoffs {
		
		private long crosslinkCount;
		private long looplinkCount;
		/**
		 * includes dimers
		 */
		private long unlinkedCount;
		
		public long getCrosslinkCount() {
			return crosslinkCount;
		}
		public void setCrosslinkCount(long crosslinkCount) {
			this.crosslinkCount = crosslinkCount;
		}
		public long getLooplinkCount() {
			return looplinkCount;
		}
		public void setLooplinkCount(long looplinkCount) {
			this.looplinkCount = looplinkCount;
		}
		public long getUnlinkedCount() {
			return unlinkedCount;
		}
		public void setUnlinkedCount(long unlinkedCount) {
			this.unlinkedCount = unlinkedCount;
		}
		
	}
}
