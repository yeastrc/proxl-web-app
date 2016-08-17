package org.yeastrc.xlink.www.proxl_xml_file_import.webservices;

import java.text.DateFormat;
import java.util.ArrayList;
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

import org.apache.log4j.Logger;
import org.yeastrc.xlink.base.proxl_xml_file_import.dao.ProxlXMLFileImportTrackingSingleFileDAO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingSingleFileDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingStatusValLkupDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.enum_classes.ProxlXMLFileImportFileType;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.XLinkUserDAO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.proxl_xml_file_import.dao.ProxlXMLFileImportTrackingStatusValuesLookupDAO;
import org.yeastrc.xlink.www.proxl_xml_file_import.display_objects.ProxlXMLFileImportTrackingDisplay;
import org.yeastrc.xlink.www.proxl_xml_file_import.searchers.ProxlXMLFileImportTracking_All_Searcher;
import org.yeastrc.xlink.www.proxl_xml_file_import.utils.IsProxlXMLFileImportFullyConfigured;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.webservices.ProjectListForCurrentUserService;


@Path("/proxl_xml_file_import")
public class ProxlXMLFileImportTrackingDataService {

	

	private static final Logger log = Logger.getLogger(ProjectListForCurrentUserService.class);


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/trackingDataList")
	public List<ProxlXMLFileImportTrackingDisplay>  listProjects( 
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

			if ( ! authAccessLevel.isAssistantProjectOwnerAllowed() ) {

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
			
			
			List<ProxlXMLFileImportTrackingDisplay> displayList = getProxlXMLFileImportingDataForPage( projectId );
			
			return displayList;

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
	private List<ProxlXMLFileImportTrackingDisplay> getProxlXMLFileImportingDataForPage(int projectId)
			throws Exception, ProxlWebappInternalErrorException {
		

		List<ProxlXMLFileImportTrackingDisplay> displayList = new ArrayList<>();
		
		
		
		List<ProxlXMLFileImportTrackingDTO> proxlXMLFileImportTrackingList = 
				ProxlXMLFileImportTracking_All_Searcher.getInstance().getAllForWebDisplayForProject( projectId );

		if ( ! proxlXMLFileImportTrackingList.isEmpty() ) {
			

			DateFormat dateFormat = DateFormat.getDateInstance();
			
			
			List<ProxlXMLFileImportTrackingStatusValLkupDTO>  statusTextList = 
					ProxlXMLFileImportTrackingStatusValuesLookupDAO.getInstance().getAll();
			
			//  Put statuses in a Map
			Map<Integer, String> statusTextKeyedOnId = new HashMap<>();
			
			for ( ProxlXMLFileImportTrackingStatusValLkupDTO  statusTextItem : statusTextList ) {
				
				statusTextKeyedOnId.put( statusTextItem.getId(), statusTextItem.getStatusDisplayText() );
			}
			
			
			
			
			for ( ProxlXMLFileImportTrackingDTO trackingItem : proxlXMLFileImportTrackingList ) {
				
				ProxlXMLFileImportTrackingDisplay displayItem = new ProxlXMLFileImportTrackingDisplay();
				
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

//						scanFileEntryList.add( fileDataEntry );  // TODO  REMOVE
//						scanFileEntryList.add( fileDataEntry );  // TODO  REMOVE
//						scanFileEntryList.add( fileDataEntry );  // TODO  REMOVE
						
					} else if ( fileDataEntry.getFileType() == ProxlXMLFileImportFileType.SCAN_FILE ) {
						
						scanFileEntryList.add( fileDataEntry );
						
					} else {
						
						
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
				
				
				displayItem.setScanFilenames( scanFilenames );
				
				String uploadDateTimeString = null;
				
				if ( trackingItem.getUploadDateTime() != null ) {

					dateFormat.format( trackingItem.getUploadDateTime() );
				}
				
				displayItem.setUploadDateTime( uploadDateTimeString );
				
				String searchName = trackingItem.getSearchName();
				
				displayItem.setSearchName( searchName );
				
				
				int authUserId = trackingItem.getAuthUserId();
				
				XLinkUserDTO xlinkUserDTO =
						XLinkUserDAO.getInstance().getXLinkUserDTOForAuthUserId( authUserId );

				if ( xlinkUserDTO == null ) {

					String msg = "xlinkUserDTO not found for authUserId: " + authUserId 
							+ " ,tracking id: " 
							+ trackingItem.getId()
							+ ", number of files returned: " + fileDataList.size();
					log.error( msg );
					throw new ProxlWebappInternalErrorException( msg );
				}
				
				String nameOfUploadUser = xlinkUserDTO.getFirstName() + " " + xlinkUserDTO.getLastName();
				
				displayItem.setNameOfUploadUser( nameOfUploadUser );
				
				
				displayList.add( displayItem );
			}
			
		}
		
		return displayList;
	}


}
