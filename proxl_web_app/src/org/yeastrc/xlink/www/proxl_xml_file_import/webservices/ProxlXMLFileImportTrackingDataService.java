package org.yeastrc.xlink.www.proxl_xml_file_import.webservices;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.yeastrc.auth.dao.AuthUserDAO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dao.ProxlXMLFileImportTrackingSingleFileDAO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dao.ProxlXMLFileImportTracking_Base_DAO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingRunDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingSingleFileDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingStatusValLkupDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportFileType;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.proxl_xml_file_import.dao.ProxlXMLFileImportTrackingStatusValuesLookupDAO;
import org.yeastrc.xlink.www.proxl_xml_file_import.display_objects.ProxlXMLFileImportTrackingDisplay;
import org.yeastrc.xlink.www.proxl_xml_file_import.searchers.ProxlXMLFileImportTrackingRun_LatestForParent_Searcher;
import org.yeastrc.xlink.www.proxl_xml_file_import.searchers.ProxlXMLFileImportTracking_All_Searcher;
import org.yeastrc.xlink.www.proxl_xml_file_import.searchers.ProxlXMLFileImportTracking_PendingCount_Searcher;
import org.yeastrc.xlink.www.proxl_xml_file_import.searchers.ProxlXMLFileImportTracking_PendingTrackingIdsAllProjects_Searcher;
import org.yeastrc.xlink.www.proxl_xml_file_import.utils.IsProxlXMLFileImportFullyConfigured;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtCentralWebappWebserviceAccess;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataRequest;
import org.yeastrc.xlink.www.user_mgmt_webapp_access.UserMgmtGetUserDataResponse;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;


@Path("/proxl_xml_file_import")
public class ProxlXMLFileImportTrackingDataService {
	
	private static final Logger log = Logger.getLogger(ProxlXMLFileImportTrackingDataService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/trackingDataList")
	public ListImportsAndGetPendingCountResponse listImportsAndGetPendingCount( 
			@QueryParam( "project_id" ) Integer projectId,
			@Context HttpServletRequest request ) throws Exception {
		try {
			if ( projectId == null ) {
				String msg = "missing project_id ";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			if ( projectId == 0 ) {
				String msg = "Provided project_id is zero, is = " + projectId;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for this search
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
			if ( ! authAccessLevel.isProjectOwnerAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  If NOT Proxl XML File Import is Fully Configured, 
			if ( ! IsProxlXMLFileImportFullyConfigured.getInstance().isProxlXMLFileImportFullyConfigured() ) {
				String msg = "Proxl XML File Import is NOT Fully Configured ";
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			ListImportsAndGetPendingCountResponse listImportsAndGetPendingCountResponse = 
					getProxlXMLFileImportingDataForPage( projectId );
			return listImportsAndGetPendingCountResponse;
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
	 * If user is Researcher or better and Proxl XML File Import is Fully Configured, 
	 * get submitted Proxl XML files
	 * 
	 * @param request
	 * @param projectId
	 * @throws Exception
	 * @throws ProxlWebappInternalErrorException
	 */
	private ListImportsAndGetPendingCountResponse getProxlXMLFileImportingDataForPage(int projectId)
			throws Exception, ProxlWebappInternalErrorException {
		List<ProxlXMLFileImportTrackingDisplay> pendingItemsList = new ArrayList<>();
		List<ProxlXMLFileImportTrackingDisplay> historyItemsList = new ArrayList<>();
		List<Integer> completeSuccessTrackingIdList = new ArrayList<>();
		ArrayList<Integer> pendingTrackingIdsAllProjectsList = null;
		List<ProxlXMLFileImportTrackingDTO> proxlXMLFileImportTrackingList = 
				ProxlXMLFileImportTracking_All_Searcher.getInstance().getAllForWebDisplayForProject( projectId );
		if ( ! proxlXMLFileImportTrackingList.isEmpty() ) {
			DateFormat dateTimeFormat = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG );
			NumberFormat numberFormat = NumberFormat.getInstance();
			List<ProxlXMLFileImportTrackingStatusValLkupDTO>  statusTextList = 
					ProxlXMLFileImportTrackingStatusValuesLookupDAO.getInstance().getAll();
			//  Put statuses in a Map
			Map<Integer, String> statusTextKeyedOnId = new HashMap<>();
			for ( ProxlXMLFileImportTrackingStatusValLkupDTO  statusTextItem : statusTextList ) {
				statusTextKeyedOnId.put( statusTextItem.getId(), statusTextItem.getStatusDisplayText() );
			}
			/////////////
			//   Copy Tracking Records to internal holder to match up with other data
			List<InternalHolder> internalHolderList = new ArrayList<>( proxlXMLFileImportTrackingList.size() ); 
			//  At the same time, check for tracking records have status QUEUED or RE_QUEUED
			//     get all tracking id for status QUEUED or RE_QUEUED
			//     so can put queue position on the display objects
			// check for any tracking records have status QUEUED or RE_QUEUED
			boolean foundQueuedOrReQueued = false;
			for ( ProxlXMLFileImportTrackingDTO trackingItem : proxlXMLFileImportTrackingList ) {
				InternalHolder internalHolder = new InternalHolder();
				internalHolderList.add( internalHolder );
				internalHolder.trackingItem = trackingItem;
				if ( trackingItem.getStatus() == ProxlXMLFileImportStatus.QUEUED
						|| trackingItem.getStatus() == ProxlXMLFileImportStatus.RE_QUEUED ) {
					foundQueuedOrReQueued = true;
				}
				if ( trackingItem.getStatus() == ProxlXMLFileImportStatus.FAILED ) {
					ProxlXMLFileImportTrackingRunDTO latestRunForTrackingItem =
							ProxlXMLFileImportTrackingRun_LatestForParent_Searcher.getInstance()
							.getLatestRunForProxlXMLFileImportTrackingDTO( trackingItem.getId() );
					if ( latestRunForTrackingItem == null ) {
						String msg = "Proxl XML Import Tracking Processing: Failed to get latest run for Tracking Item status FAILED. "
								+ "Tracking Item Id: " + trackingItem.getId();
						log.error( msg );
						throw new ProxlWebappInternalErrorException( msg );
					}
					if ( latestRunForTrackingItem.getRunStatus() != ProxlXMLFileImportStatus.FAILED ) {
						String msg = "Proxl XML Import Tracking Processing: Latest run status is not FAILED for Tracking Item status FAILED. "
								+ "Tracking Item Id: " + trackingItem.getId();
						log.error( msg );
						throw new ProxlWebappInternalErrorException( msg );
					}
					internalHolder.latestRunForTrackingItem = latestRunForTrackingItem;
				}
			}
			if ( foundQueuedOrReQueued ) {
				//  Found status QUEUED or RE_QUEUED so get tracking ids for all
				pendingTrackingIdsAllProjectsList =
						ProxlXMLFileImportTracking_PendingTrackingIdsAllProjects_Searcher.getInstance()
						.getPendingTrackingIdsAllProjects();
			}
			////////
			//   Main processing of tracking records
			for ( InternalHolder internalHolder : internalHolderList ) {
				ProxlXMLFileImportTrackingDTO trackingItem = internalHolder.trackingItem;
				ProxlXMLFileImportTrackingDisplay displayItem = new ProxlXMLFileImportTrackingDisplay();
				//  Set Pending queue position
				if ( trackingItem.getStatus() == ProxlXMLFileImportStatus.QUEUED
						|| trackingItem.getStatus() == ProxlXMLFileImportStatus.RE_QUEUED ) {
					if ( pendingTrackingIdsAllProjectsList == null || pendingTrackingIdsAllProjectsList.isEmpty() ) {
						String msg = "pendingTrackingIdsAllProjectsList is null or empty when tracking status is QUEUED or RE_QUEUED."
								+ "  trackingItem id: " + trackingItem.getId();
						log.error( msg );
						throw new ProxlWebappInternalErrorException(msg);
					}
					int queueIndex = pendingTrackingIdsAllProjectsList.indexOf( trackingItem.getId() );
					if ( queueIndex < 0 )  {
						//  Was not found in all pending.  Must no longer be pending. Get from DB again
						trackingItem = ProxlXMLFileImportTracking_Base_DAO.getInstance().getItem( trackingItem.getId() );
						if ( trackingItem.getStatus() == ProxlXMLFileImportStatus.QUEUED
								|| trackingItem.getStatus() == ProxlXMLFileImportStatus.RE_QUEUED ) {
							String msg = "Tracking item is not in all pending for tracking status QUEUED or RE_QUEUED"
									+ " after re-get from DB."
									+ "  trackingItem id: " + trackingItem.getId();
							log.error( msg );
							throw new ProxlWebappInternalErrorException(msg);
						}
					} else {
						int queuePosition = queueIndex + 1; // add 1 since queueIndex is zero based
						displayItem.setQueuePosition( queuePosition );
						displayItem.setQueuePositionFmt( numberFormat.format( queuePosition ) );
					}
				}
				displayItem.setTrackingId( trackingItem.getId() );
				displayItem.setStatusEnum( trackingItem.getStatus() );
				String statusText = statusTextKeyedOnId.get( trackingItem.getStatus().value() );
				if ( statusText == null ) {
					String msg = "Proxl XML Import Tracking Processing: Failed to get status text for status id: " 
							+ trackingItem.getStatus().value();
					log.error( msg );
					throw new ProxlWebappInternalErrorException( msg );
				}
				displayItem.setStatus( statusText );
				if ( trackingItem.getStatus() == ProxlXMLFileImportStatus.FAILED ) {
					displayItem.setStatusFailedMsg( internalHolder.latestRunForTrackingItem.getDataErrorText() );
				}
				List<ProxlXMLFileImportTrackingSingleFileDTO> fileDataList = 
						ProxlXMLFileImportTrackingSingleFileDAO.getInstance()
						.getForTrackingId( trackingItem.getId() );
				if ( fileDataList.isEmpty() ) {
					String msg = "Proxl XML Import Tracking Processing: no files found for tracking id: " 
							+ trackingItem.getId();
					log.error( msg );
					throw new ProxlWebappInternalErrorException( msg );
				}
				ProxlXMLFileImportTrackingSingleFileDTO proxlXMLFileEntry = null;
				List<ProxlXMLFileImportTrackingSingleFileDTO> scanFileEntryList = new ArrayList<>();
				for ( ProxlXMLFileImportTrackingSingleFileDTO fileDataEntry : fileDataList ) {
					if ( fileDataEntry.getFileType() == ProxlXMLFileImportFileType.PROXL_XML_FILE ) {
						proxlXMLFileEntry = fileDataEntry;
					} else if ( fileDataEntry.getFileType() == ProxlXMLFileImportFileType.SCAN_FILE ) {
						scanFileEntryList.add( fileDataEntry );
					} else {
						String msg = "Proxl XML Import Tracking Processing: Unknown file type for single file id: " 
								+ fileDataEntry.getId();
						log.error( msg );
						throw new ProxlWebappInternalErrorException( msg );
					}
				}
				if ( proxlXMLFileEntry == null ) {
					String msg = "Proxl XML Import Tracking Processing: proxlXMLFileEntry not found for tracking id: " 
							+ trackingItem.getId();
					log.error( msg );
					throw new ProxlWebappInternalErrorException( msg );
				}
				String uploadedFilename = proxlXMLFileEntry.getFilenameInUpload();
				displayItem.setUploadedFilename( uploadedFilename );
				List<String> scanFilenames = new ArrayList<>( scanFileEntryList.size() );
				for ( ProxlXMLFileImportTrackingSingleFileDTO scanFileEntry : scanFileEntryList ) {
					String scanFilename = scanFileEntry.getFilenameInUpload();
					scanFilenames.add( scanFilename );
				}
				String scanfileNamesCommaDelim = StringUtils.join( scanFilenames, ", " );
				displayItem.setScanFilenames( scanFilenames );
				displayItem.setScanfileNamesCommaDelim( scanfileNamesCommaDelim );
				if ( trackingItem.getRecordInsertDateTime() != null ) {
					displayItem.setImportSubmitDateTime( dateTimeFormat.format( trackingItem.getRecordInsertDateTime() ) );
				}
				if ( trackingItem.getStatus() == ProxlXMLFileImportStatus.STARTED 
						|| trackingItem.getStatus() == ProxlXMLFileImportStatus.COMPLETE
						|| trackingItem.getStatus() == ProxlXMLFileImportStatus.FAILED ) {
					if ( trackingItem.getImportStartDateTime() != null ) {
						displayItem.setImportStartDateTime( dateTimeFormat.format( trackingItem.getImportStartDateTime() ) );
					}
				}
				if ( trackingItem.getStatus() == ProxlXMLFileImportStatus.COMPLETE
						|| trackingItem.getStatus() == ProxlXMLFileImportStatus.FAILED ) {
					if ( trackingItem.getImportEndDateTime() != null ) {
						displayItem.setImportEndDateTime( dateTimeFormat.format( trackingItem.getImportEndDateTime() ) );
					}
				}
				String searchName = trackingItem.getSearchName();
				displayItem.setSearchName( searchName );
				int authUserId = trackingItem.getAuthUserId();
				//  Get full user data
				//  Get User Mgmt User Id for authUserId
				Integer userMgmtUserId = AuthUserDAO.getInstance().getUserMgmtUserIdForId( authUserId );
				if ( userMgmtUserId == null ) {
					String msg = "Failed to get userMgmtUserId for Proxl auth user id: " + authUserId;
					log.warn( msg );
			        return null;  //  Early Exit
				}
				UserMgmtGetUserDataRequest userMgmtGetUserDataRequest = new UserMgmtGetUserDataRequest();
				userMgmtGetUserDataRequest.setUserId( userMgmtUserId );
				UserMgmtGetUserDataResponse userMgmtGetUserDataResponse = 
						UserMgmtCentralWebappWebserviceAccess.getInstance().getUserData( userMgmtGetUserDataRequest );
				if ( ! userMgmtGetUserDataResponse.isSuccess() ) {
					String msg = "Failed to get Full user data from User Mgmt Webapp for user id: " + authUserId
							+ ", userMgmtUserId: " + userMgmtUserId;
					log.error( msg );
					throw new ProxlWebappInternalErrorException( msg );
				}
				String nameOfUploadUser = userMgmtGetUserDataResponse.getFirstName() + " " + userMgmtGetUserDataResponse.getLastName();
				displayItem.setNameOfUploadUser( nameOfUploadUser );
				if ( trackingItem.getStatus() == ProxlXMLFileImportStatus.COMPLETE
						|| trackingItem.getStatus() == ProxlXMLFileImportStatus.FAILED ) {
					historyItemsList.add( displayItem );
				} else {
					pendingItemsList.add( displayItem );
				}
				if ( trackingItem.getStatus() == ProxlXMLFileImportStatus.COMPLETE ) {
					completeSuccessTrackingIdList.add( trackingItem.getId() );
				}
			}
		}
		Collections.sort( completeSuccessTrackingIdList );
		int pendingCount = 
				ProxlXMLFileImportTracking_PendingCount_Searcher.getInstance().getPendingCountForProject( projectId );
		ListImportsAndGetPendingCountResponse listImportsAndGetPendingCountResponse = new ListImportsAndGetPendingCountResponse();
		listImportsAndGetPendingCountResponse.pendingCount = pendingCount;
		listImportsAndGetPendingCountResponse.pendingItemsList = pendingItemsList;
		listImportsAndGetPendingCountResponse.historyItemsList = historyItemsList;
		listImportsAndGetPendingCountResponse.completeSuccessTrackingIdList = completeSuccessTrackingIdList;
		return listImportsAndGetPendingCountResponse;
	}
	
	/**
	 * 
	 *
	 */
	private static class InternalHolder {
		ProxlXMLFileImportTrackingDTO trackingItem;
		ProxlXMLFileImportTrackingRunDTO latestRunForTrackingItem;
	}
	
	/**
	 * 
	 *
	 */
	public static class ListImportsAndGetPendingCountResponse {
		int pendingCount;
		List<ProxlXMLFileImportTrackingDisplay> pendingItemsList;
		List<ProxlXMLFileImportTrackingDisplay> historyItemsList;
		List<Integer> completeSuccessTrackingIdList;
		public int getPendingCount() {
			return pendingCount;
		}
		public void setPendingCount(int pendingCount) {
			this.pendingCount = pendingCount;
		}
		public List<ProxlXMLFileImportTrackingDisplay> getPendingItemsList() {
			return pendingItemsList;
		}
		public void setPendingItemsList(
				List<ProxlXMLFileImportTrackingDisplay> pendingItemsList) {
			this.pendingItemsList = pendingItemsList;
		}
		public List<ProxlXMLFileImportTrackingDisplay> getHistoryItemsList() {
			return historyItemsList;
		}
		public void setHistoryItemsList(
				List<ProxlXMLFileImportTrackingDisplay> historyItemsList) {
			this.historyItemsList = historyItemsList;
		}
		public List<Integer> getCompleteSuccessTrackingIdList() {
			return completeSuccessTrackingIdList;
		}
		public void setCompleteSuccessTrackingIdList(
				List<Integer> completeSuccessTrackingIdList) {
			this.completeSuccessTrackingIdList = completeSuccessTrackingIdList;
		}
	}
}
