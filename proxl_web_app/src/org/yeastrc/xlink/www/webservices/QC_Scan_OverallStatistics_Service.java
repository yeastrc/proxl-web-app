package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collections;
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
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchIdScanFileIdCombinedRecordExistsSearcher;
import org.yeastrc.xlink.dao.ScanFileStatisticsDAO;
import org.yeastrc.xlink.dto.ScanFileStatisticsDTO;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

@Path("/qc/dataPage")
public class QC_Scan_OverallStatistics_Service {
	
	private static final Logger log = Logger.getLogger(QC_Scan_OverallStatistics_Service.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getScanOverallStatistics") 
	public QC_Scan_OverallStatistics_WebserviceResult getScanOverallStatistics( 
			@QueryParam( "project_search_id" ) List<Integer> projectSearchIdList,
			@QueryParam( "scan_file_id" ) Integer scanFileId,
			@Context HttpServletRequest request )
	throws Exception {

		QC_Scan_OverallStatistics_WebserviceResult webserviceResult = null;
		
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided project_search_id is null or project_search_id is missing";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}

		if ( scanFileId == null ) {
			String msg = "Provided scan_file_id is null";
			log.warn( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for this search
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int projectSearchId : projectSearchIdList ) {
				projectSearchIdsSet.add( projectSearchId );
			}
			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdsListDeduppedSorted );

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
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this search id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////
			
			//  Confirm the scan file id is in one of the project search ids
			
			boolean scanPsmRecordFoundForScanFileIdProjectSearchId = false;
			
			for ( Integer projectSearchId : projectSearchIdList ) {
				Integer searchId =
						MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
				if ( searchId == null ) {
					String msg = ": No searchId found for projectSearchId: " + projectSearchId;
					log.warn( msg );
					throw new WebApplicationException(
							Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
							.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
							.build()
							);
				}

				if ( SearchIdScanFileIdCombinedRecordExistsSearcher.getInstance()
						.recordExistsForSearchIdScanFileIdCombined( searchId, scanFileId ) ) {
					scanPsmRecordFoundForScanFileIdProjectSearchId = true;
					break;
				}
			}
			
			if ( ! scanPsmRecordFoundForScanFileIdProjectSearchId ) {
				@SuppressWarnings("unchecked")
				String msg = "Provided scan_file_id not for for any provided project search ids.  "
						+ "scan_file_id: " + scanFileId + ", projectSearchIds: " + StringUtils.join( projectSearchIdList );
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}

			ScanFileStatisticsDTO scanFileStatisticsDTO = 
					ScanFileStatisticsDAO.getInstance().getScanFileStatisticsDTOForScanFileId( scanFileId ); 
			
			if ( scanFileStatisticsDTO == null ) {
				webserviceResult = new QC_Scan_OverallStatistics_WebserviceResult();
				webserviceResult.setHaveData(false);
			} else {
				webserviceResult = new QC_Scan_OverallStatistics_WebserviceResult( scanFileStatisticsDTO );
			}
			
			
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
		
		
		return webserviceResult;
	}
	
	/**
	 * Webservice returned object
	 *
	 */
	public static class QC_Scan_OverallStatistics_WebserviceResult {

		private boolean haveData;
		private int scanFileId;
		private long ms_1_ScanCount;
		private double ms_1_ScanIntensitiesSummed;
		private long ms_2_ScanCount;
		private double ms_2_ScanIntensitiesSummed;

		// Constructors
		public QC_Scan_OverallStatistics_WebserviceResult() { }
		public QC_Scan_OverallStatistics_WebserviceResult( ScanFileStatisticsDTO scanFileStatisticsDTO ) {
			super();
			this.haveData = true;
			this.scanFileId = scanFileStatisticsDTO.getScanFileId();
			this.ms_1_ScanCount = scanFileStatisticsDTO.getMs_1_ScanCount();
			this.ms_1_ScanIntensitiesSummed = scanFileStatisticsDTO.getMs_1_ScanIntensitiesSummed();
			this.ms_2_ScanCount = scanFileStatisticsDTO.getMs_2_ScanCount();
			this.ms_2_ScanIntensitiesSummed = scanFileStatisticsDTO.getMs_2_ScanIntensitiesSummed();
		}
		
		public int getScanFileId() {
			return scanFileId;
		}
		public void setScanFileId(int scanFileId) {
			this.scanFileId = scanFileId;
		}
		public long getMs_1_ScanCount() {
			return ms_1_ScanCount;
		}
		public void setMs_1_ScanCount(long ms_1_ScanCount) {
			this.ms_1_ScanCount = ms_1_ScanCount;
		}
		public double getMs_1_ScanIntensitiesSummed() {
			return ms_1_ScanIntensitiesSummed;
		}
		public void setMs_1_ScanIntensitiesSummed(double ms_1_ScanIntensitiesSummed) {
			this.ms_1_ScanIntensitiesSummed = ms_1_ScanIntensitiesSummed;
		}
		public long getMs_2_ScanCount() {
			return ms_2_ScanCount;
		}
		public void setMs_2_ScanCount(long ms_2_ScanCount) {
			this.ms_2_ScanCount = ms_2_ScanCount;
		}
		public double getMs_2_ScanIntensitiesSummed() {
			return ms_2_ScanIntensitiesSummed;
		}
		public void setMs_2_ScanIntensitiesSummed(double ms_2_ScanIntensitiesSummed) {
			this.ms_2_ScanIntensitiesSummed = ms_2_ScanIntensitiesSummed;
		}
		public boolean isHaveData() {
			return haveData;
		}
		public void setHaveData(boolean haveData) {
			this.haveData = haveData;
		}
		
	}
}
