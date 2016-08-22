package org.yeastrc.xlink.www.proxl_xml_file_import.webservices;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.proxl_xml_file_import.dao.ProxlXMLFileImportTracking_Base_DAO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportStatus;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.proxl_xml_file_import.dao.ProxlXMLFileImportTracking_ForWebAppDAO;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.webservices.ProjectListForCurrentUserService;




@Path("/proxl_xml_file_import")
public class ProxlXMLFileImportTrackingItemDeleteItemService {



	private static final Logger log = Logger.getLogger(ProjectListForCurrentUserService.class);


	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/cancelQueuedImport") 
	public CancelQueuedImportResult cancelQueuedImport(   
			@FormParam( "tracking_id" ) Integer trackingId,
			@FormParam( "status_id" ) Integer statusId,
			@Context HttpServletRequest request ) throws Exception {


		try {

			if ( trackingId == null ) {

				String msg = "missing tracking_id ";

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}

			if ( trackingId == 0 ) {

				String msg = "Provided tracking_id is zero, is = " + trackingId;

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}
			
			

			if ( statusId == null ) {

				String msg = "missing status_id ";

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}

			if ( statusId == 0 ) {

				String msg = "Provided status_id is zero, is = " + statusId;

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}
			
			ProxlXMLFileImportStatus proxlXMLFileImportStatus = null;
			
			try {
				
				proxlXMLFileImportStatus = ProxlXMLFileImportStatus.fromValue( statusId );
				
			} catch ( Exception e ) {
				

				String msg = "Provided status_id is not a valid status id, is = " + statusId;

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}
			

			if ( proxlXMLFileImportStatus != ProxlXMLFileImportStatus.QUEUED
					&& proxlXMLFileImportStatus != ProxlXMLFileImportStatus.RE_QUEUED ) {

				String msg = "Provided status_id is not a valid status for this service, is = " + statusId;

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}

			// Get the session first.  
			//			HttpSession session = request.getSession();

			//   Get the project id for this tracking id


			ProxlXMLFileImportTracking_ForWebAppDAO proxlXMLFileImportTracking_ForWebAppDAO = ProxlXMLFileImportTracking_ForWebAppDAO.getInstance();

			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO = 
					ProxlXMLFileImportTracking_Base_DAO.getInstance().getItem( trackingId );

			if ( proxlXMLFileImportTrackingDTO == null ) {

				log.warn( "tracking_id is not in database: " + trackingId );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}



			int projectId = proxlXMLFileImportTrackingDTO.getProjectId();



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

			CancelQueuedImportResult cancelQueuedImportResult = 
					checkDBForMarkDeleteQueuedRecord( proxlXMLFileImportStatus, proxlXMLFileImportTrackingDTO );

			if ( cancelQueuedImportResult != null ) {

				//  Either no update needed since marked for deletion or status not queued

				return cancelQueuedImportResult;
			}
			
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			
			if ( userSessionObject == null ) {
				
				
			}
			
			int authUserId = userSessionObject.getUserDBObject().getAuthUser().getId();


			boolean recordUpdated =
					proxlXMLFileImportTracking_ForWebAppDAO
					.updateMarkedForDeletionForIdStatus( 
							true /* markedForDeletion */, proxlXMLFileImportStatus, trackingId, authUserId );

			
			if ( recordUpdated ) {

				cancelQueuedImportResult = new CancelQueuedImportResult();

				cancelQueuedImportResult.success = true;

				return cancelQueuedImportResult;
			}

			//  No record updated so re-fetch the record to get current data for checking

			proxlXMLFileImportTrackingDTO = 
					ProxlXMLFileImportTracking_Base_DAO.getInstance().getItem( trackingId );

			cancelQueuedImportResult = 
					checkDBForMarkDeleteQueuedRecord( proxlXMLFileImportStatus, proxlXMLFileImportTrackingDTO );


			if ( cancelQueuedImportResult != null ) {

				//  Either no update needed since marked for deletion or status not queued

				return cancelQueuedImportResult;
			}

			//  If got here, this is a system error

			String msg = "cancelQueuedImport(...) trackingId" + trackingId 
					+ ".  Record not updated but status is queued.";

			log.error( msg );


			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);



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
	 * @param proxlXMLFileImportTrackingDTO
	 * @return null if next update the db
	 */
	private CancelQueuedImportResult checkDBForMarkDeleteQueuedRecord( 
			
			ProxlXMLFileImportStatus proxlXMLFileImportStatusInServiceRequest,
			
			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO ) {


		if ( proxlXMLFileImportTrackingDTO.getStatus() != proxlXMLFileImportStatusInServiceRequest ) {

			//  error since no longer queued

			CancelQueuedImportResult cancelQueuedImportResult = new CancelQueuedImportResult();
			
			cancelQueuedImportResult.success = false;
			
			if ( proxlXMLFileImportStatusInServiceRequest == ProxlXMLFileImportStatus.QUEUED ) {

				cancelQueuedImportResult.statusNotQueued = true;
			} else {
			
				cancelQueuedImportResult.statusNotRequeued = true;
			}
			
			return cancelQueuedImportResult; // early return;
		}


		if ( proxlXMLFileImportTrackingDTO.isMarkedForDeletion() ) {

			//  already marked for deletion so ok

			CancelQueuedImportResult cancelQueuedImportResult = new CancelQueuedImportResult();
			
			cancelQueuedImportResult.success = true;
			cancelQueuedImportResult.alreadyMarkedForDeletion = true;

			return cancelQueuedImportResult; // early return;
		}
		
		return null;
	}

	/**
	 * 
	 *
	 */
	private static class CancelQueuedImportResult {

		boolean success;
		boolean statusNotQueued;
		boolean statusNotRequeued;
		boolean alreadyMarkedForDeletion;
		
		@SuppressWarnings("unused")
		public boolean isSuccess() {
			return success;
		}
		@SuppressWarnings("unused")
		public void setSuccess(boolean success) {
			this.success = success;
		}
		@SuppressWarnings("unused")
		public boolean isStatusNotQueued() {
			return statusNotQueued;
		}
		@SuppressWarnings("unused")
		public void setStatusNotQueued(boolean statusNotQueued) {
			this.statusNotQueued = statusNotQueued;
		}
		@SuppressWarnings("unused")
		public boolean isAlreadyMarkedForDeletion() {
			return alreadyMarkedForDeletion;
		}
		@SuppressWarnings("unused")
		public void setAlreadyMarkedForDeletion(boolean alreadyMarkedForDeletion) {
			this.alreadyMarkedForDeletion = alreadyMarkedForDeletion;
		}
		@SuppressWarnings("unused")
		public boolean isStatusNotRequeued() {
			return statusNotRequeued;
		}
		@SuppressWarnings("unused")
		public void setStatusNotRequeued(boolean statusNotRequeued) {
			this.statusNotRequeued = statusNotRequeued;
		}
	}
	
	
	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////


	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/removeFailedImport") 
	public RemoveFailedImport removeFailedImport(   
			@FormParam( "tracking_id" ) Integer trackingId,
			@Context HttpServletRequest request ) throws Exception {


		try {

			if ( trackingId == null ) {

				String msg = "missing tracking_id ";

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}

			if ( trackingId == 0 ) {

				String msg = "Provided tracking_id is zero, is = " + trackingId;

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}


			// Get the session first.  
			//			HttpSession session = request.getSession();

			//   Get the project id for this tracking id


			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO = 
					ProxlXMLFileImportTracking_Base_DAO.getInstance().getItem( trackingId );

			if ( proxlXMLFileImportTrackingDTO == null ) {

				log.warn( "tracking_id is not in database: " + trackingId );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}



			int projectId = proxlXMLFileImportTrackingDTO.getProjectId();



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

			RemoveFailedImport removeFailedImportResult = checkDBForMarkDeleteFailedRecord(proxlXMLFileImportTrackingDTO);

			if ( removeFailedImportResult != null ) {

				//  Either no update needed since marked for deletion or status not failed

				return removeFailedImportResult;
			}
			

			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			
			if ( userSessionObject == null ) {
				
				
			}
			
			int authUserId = userSessionObject.getUserDBObject().getAuthUser().getId();
			
			boolean recordUpdated =
					ProxlXMLFileImportTracking_ForWebAppDAO.getInstance()
					.updateMarkedForDeletionForIdStatus( 
							true /* markedForDeletion */, ProxlXMLFileImportStatus.FAILED, trackingId, authUserId );

			if ( recordUpdated ) {

				removeFailedImportResult = new RemoveFailedImport();

				removeFailedImportResult.success = true;

				return removeFailedImportResult;
			}

			//  No record updated so re-fetch the record to get current data for checking

			proxlXMLFileImportTrackingDTO = 
					ProxlXMLFileImportTracking_Base_DAO.getInstance().getItem( trackingId );

			removeFailedImportResult = checkDBForMarkDeleteFailedRecord(proxlXMLFileImportTrackingDTO);


			if ( removeFailedImportResult != null ) {

				//  Either no update needed since marked for deletion or status not failed

				return removeFailedImportResult;
			}

			//  If got here, this is a system error

			String msg = "removeFailedImport(...) trackingId" + trackingId 
					+ ".  Record not updated but status is failed.";

			log.error( msg );


			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);



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
	 * @param proxlXMLFileImportTrackingDTO
	 * @return null if next update the db
	 */
	private RemoveFailedImport checkDBForMarkDeleteFailedRecord( ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO ) {


		if ( proxlXMLFileImportTrackingDTO.getStatus() != ProxlXMLFileImportStatus.FAILED ) {

			//  error since no longer failed

			RemoveFailedImport removeFailedImportResult = new RemoveFailedImport();
			
			removeFailedImportResult.success = false;
			removeFailedImportResult.statusNotFailed = true;
			
			return removeFailedImportResult; // early return;
		}


		if ( proxlXMLFileImportTrackingDTO.isMarkedForDeletion() ) {

			//  already marked for deletion so ok

			RemoveFailedImport removeFailedImportResult = new RemoveFailedImport();
			
			removeFailedImportResult.success = true;
			removeFailedImportResult.alreadyMarkedForDeletion = true;

			return removeFailedImportResult; // early return;
		}
		
		return null;
	}

	/**
	 * 
	 *
	 */
	private static class RemoveFailedImport {

		boolean success;
		boolean statusNotFailed;
		boolean alreadyMarkedForDeletion;
		
		@SuppressWarnings("unused")
		public boolean isSuccess() {
			return success;
		}
		@SuppressWarnings("unused")
		public void setSuccess(boolean success) {
			this.success = success;
		}
		@SuppressWarnings("unused")
		public boolean isStatusNotFailed() {
			return statusNotFailed;
		}
		@SuppressWarnings("unused")
		public void setStatusNotFailed(boolean statusNotFailed) {
			this.statusNotFailed = statusNotFailed;
		}
		@SuppressWarnings("unused")
		public boolean isAlreadyMarkedForDeletion() {
			return alreadyMarkedForDeletion;
		}
		@SuppressWarnings("unused")
		public void setAlreadyMarkedForDeletion(boolean alreadyMarkedForDeletion) {
			this.alreadyMarkedForDeletion = alreadyMarkedForDeletion;
		}
		
	}

	

	
	///////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////


	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/removeCompletedImport") 
	public RemoveCompletedImport removeCompletedImport(   
			@FormParam( "tracking_id" ) Integer trackingId,
			@Context HttpServletRequest request ) throws Exception {


		try {

			if ( trackingId == null ) {

				String msg = "missing tracking_id ";

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}

			if ( trackingId == 0 ) {

				String msg = "Provided tracking_id is zero, is = " + trackingId;

				log.error( msg );

				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);
			}


			// Get the session first.  
			//			HttpSession session = request.getSession();

			//   Get the project id for this tracking id


			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO = 
					ProxlXMLFileImportTracking_Base_DAO.getInstance().getItem( trackingId );

			if ( proxlXMLFileImportTrackingDTO == null ) {

				log.warn( "tracking_id is not in database: " + trackingId );

				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}



			int projectId = proxlXMLFileImportTrackingDTO.getProjectId();



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

			RemoveCompletedImport removeCompletedImportResult = checkDBForMarkDeleteCompletedRecord(proxlXMLFileImportTrackingDTO);

			if ( removeCompletedImportResult != null ) {

				//  Either no update needed since marked for deletion or status not failed

				return removeCompletedImportResult;
			}
			

			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			
			if ( userSessionObject == null ) {
				
				
			}
			
			int authUserId = userSessionObject.getUserDBObject().getAuthUser().getId();
			
			boolean recordUpdated =
					ProxlXMLFileImportTracking_ForWebAppDAO.getInstance()
					.updateMarkedForDeletionForIdStatus( 
							true /* markedForDeletion */, ProxlXMLFileImportStatus.COMPLETE, trackingId, authUserId );

			if ( recordUpdated ) {

				removeCompletedImportResult = new RemoveCompletedImport();

				removeCompletedImportResult.success = true;

				return removeCompletedImportResult;
			}

			//  No record updated so re-fetch the record to get current data for checking

			proxlXMLFileImportTrackingDTO = 
					ProxlXMLFileImportTracking_Base_DAO.getInstance().getItem( trackingId );

			removeCompletedImportResult = checkDBForMarkDeleteCompletedRecord(proxlXMLFileImportTrackingDTO);


			if ( removeCompletedImportResult != null ) {

				//  Either no update needed since marked for deletion or status not failed

				return removeCompletedImportResult;
			}

			//  If got here, this is a system error

			String msg = "removeCompletedImport(...) trackingId" + trackingId 
					+ ".  Record not updated but status is failed.";

			log.error( msg );


			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);



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
	 * @param proxlXMLFileImportTrackingDTO
	 * @return null if next update the db
	 */
	private RemoveCompletedImport checkDBForMarkDeleteCompletedRecord( ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO ) {


		if ( proxlXMLFileImportTrackingDTO.getStatus() != ProxlXMLFileImportStatus.COMPLETE ) {

			//  error since no longer failed

			RemoveCompletedImport removeCompletedImportResult = new RemoveCompletedImport();
			
			removeCompletedImportResult.success = false;
			removeCompletedImportResult.statusNotCompleted = true;
			
			return removeCompletedImportResult; // early return;
		}


		if ( proxlXMLFileImportTrackingDTO.isMarkedForDeletion() ) {

			//  already marked for deletion so ok

			RemoveCompletedImport removeCompletedImportResult = new RemoveCompletedImport();
			
			removeCompletedImportResult.success = true;
			removeCompletedImportResult.alreadyMarkedForDeletion = true;

			return removeCompletedImportResult; // early return;
		}
		
		return null;
	}

	/**
	 * 
	 *
	 */
	private static class RemoveCompletedImport {

		boolean success;
		boolean statusNotCompleted;
		boolean alreadyMarkedForDeletion;
		
		@SuppressWarnings("unused")
		public boolean isSuccess() {
			return success;
		}
		@SuppressWarnings("unused")
		public void setSuccess(boolean success) {
			this.success = success;
		}
		@SuppressWarnings("unused")
		public boolean isStatusNotCompleted() {
			return statusNotCompleted;
		}
		@SuppressWarnings("unused")
		public void setStatusNotCompleted(boolean statusNotCompleted) {
			this.statusNotCompleted = statusNotCompleted;
		}
		@SuppressWarnings("unused")
		public boolean isAlreadyMarkedForDeletion() {
			return alreadyMarkedForDeletion;
		}
		@SuppressWarnings("unused")
		public void setAlreadyMarkedForDeletion(boolean alreadyMarkedForDeletion) {
			this.alreadyMarkedForDeletion = alreadyMarkedForDeletion;
		}
		
	}
}