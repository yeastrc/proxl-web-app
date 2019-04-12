package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.qc_data.scan_ms1_all_scan_intensity_heatmap.main.MS1_All_IntensityHeatmapImage;
import org.yeastrc.xlink.www.qc_data.scan_ms1_all_scan_intensity_heatmap.main.MS1_All_IntensityHeatmapImage.MS1_All_IntensityHeatmapImageResult;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchIdScanFileIdCombinedRecordExistsSearcher;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

/**
 * MS1 All scans Intensity Heatmap Image
 * 
 * Input is project_search_id and scan_file_id
 * 
 *
 */
public class QC_Scan_MS1_All_IntensityHeatmapImageAction extends Action {

	private static final Logger log = LoggerFactory.getLogger( QC_Scan_MS1_All_IntensityHeatmapImageAction.class);

	@Override
	public ActionForward execute( ActionMapping mapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response ) throws Exception {

		String requestQueryString = request.getQueryString();
		
		String scanFileIdString = null;
		String projectSearchIdString = null;
		String requestedImageWidthString = null;

		int scanFileId = 0;
		int projectSearchId = 0;
		Integer requestedImageWidth = null;

		boolean requestedImageWidthProvided = false;

		try {
			scanFileIdString = request.getParameter( "scan_file_id" );
			projectSearchIdString = request.getParameter( "project_search_id" );
			requestedImageWidthString = request.getParameter( "image_width" );

			if ( StringUtils.isEmpty( scanFileIdString ) ) {
				log.warn( "parameter scan_file_id is empty" );
				response.sendError( 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode(), 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
				return null;
			}
			if ( StringUtils.isEmpty( projectSearchIdString ) ) {
				log.warn( "parameter project_search_id is empty" );
				response.sendError( 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode(), 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
				return null;
			}

			try {
				scanFileId = Integer.parseInt( scanFileIdString );
			} catch ( Exception e ) {
				log.warn( "Failed to parse parameter scan_file_id: " + scanFileIdString );
				response.sendError( 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode(), 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
				return null;
			}
			try {
				projectSearchId = Integer.parseInt( projectSearchIdString );
			} catch ( Exception e ) {
				log.warn( "Failed to parse parameter project_search_id: " + projectSearchIdString );
				response.sendError( 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode(), 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
				return null;
			}
			if ( StringUtils.isNotEmpty( requestedImageWidthString ) ) {
				try {
					requestedImageWidth = Integer.parseInt( requestedImageWidthString );

					requestedImageWidthProvided = true;

				} catch ( Exception e ) {
					log.warn( "Failed to parse parameter image_width: " + requestedImageWidthString );
					response.sendError( 
							WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode(), 
							WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
					return null;
				}
				if ( ! MS1_All_IntensityHeatmapImage.getInstance().isRequestedImageWidthAllowed( requestedImageWidth ) ) {
					log.warn( "value for parameter image_width is not an allowed value: " + requestedImageWidthString );
					response.sendError( 
							WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode(), 
							WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
					return null;
				}
			}

			//   Auth Check
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsCollection = new HashSet<Integer>( );
			projectSearchIdsCollection.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsCollection );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search id: " + projectSearchId;
				log.error( msg );
				if ( requestedImageWidthProvided ) {
					response.sendError( 
							WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE.getStatusCode(), 
							WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT );
					return null;
				} else {
					return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
				}
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				if ( requestedImageWidthProvided ) {
					response.sendError( 
							WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE.getStatusCode(), 
							WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT );
					return null;
				} else {
					return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
				}
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				if ( requestedImageWidthProvided ) {
					response.sendError( 
							WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE.getStatusCode(), 
							WebServiceErrorMessageConstants.NO_SESSION_TEXT );
					return null;
				} else {
					return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
				}
			}
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			//  Test access to the project id
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				if ( requestedImageWidthProvided ) {
					response.sendError( 
							WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE.getStatusCode(), 
							WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT );
					return null;
				} else {
					return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
				}
			}

			///    Done Processing Auth Check and Auth Level
			//////////////////////////////

			Integer searchId =
					MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
			if ( searchId == null ) {
				String msg = ": No searchId found for projectSearchId: " + projectSearchId;
				log.warn( msg );
				if ( requestedImageWidthProvided ) {
					response.sendError( 
							WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode(), 
							WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
					return null;
				} else {
					throw new ProxlWebappDataException( msg );
				}
			}

			//  Is scan file id valid for this search id
			if ( ! SearchIdScanFileIdCombinedRecordExistsSearcher.getInstance()
					.recordExistsForSearchIdScanFileIdCombined( searchId, scanFileId ) ) {
				String msg = "Scan file id not valid for search id. projectSearchId: " + projectSearchId
						+ ", searchId: " + searchId + ", scanFileId: " + scanFileId;
				log.warn( msg );
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					response.sendError( 
							WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode(), 
							WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
					return null;
				} else {
					throw new ProxlWebappDataException( msg );
				}
			}

			//		get and return the heatmap image from file

			//		File imageFile = 
			//				new File ( "/data/web_tools/Tomcat_8.0_Port_8080/webapps/mike/ms1_intensitiesAsImage_heatmap_2_From_ImageIO_Using_RasterImage_Shrunk_Width_1000px.png" );
			//		
			//		byte[] imageFileContents = new byte[ (int) imageFile.length() ];
			//		FileInputStream fis = new FileInputStream( imageFile );
			//		fis.read( imageFileContents );

			//  Get image.  
			//		requestedImageWidth is only for allowed values
			//		(See MS1_All_IntensityHeatmapImage.ALLOWED_REQUESTED_IMAGE_WIDTHS)

			MS1_All_IntensityHeatmapImageResult ms1_All_IntensityHeatmapImageResult =
					MS1_All_IntensityHeatmapImage.getInstance()
					.getHeatmap( scanFileId, requestedImageWidth, requestQueryString );

			byte[] imageAsBytes = ms1_All_IntensityHeatmapImageResult.getImageAsBytes();

			if ( imageAsBytes == null ) {
				log.warn( "No data for scanFileId: " + scanFileId );
			}

			response.setContentType( "image/png" );
			response.setContentLength( imageAsBytes.length );

			BufferedOutputStream bos = null;

			try {
				ServletOutputStream out = response.getOutputStream();
				bos = new BufferedOutputStream(out);
				out.write( imageAsBytes );
			} finally {
				try {
					if ( bos != null ) {
						bos.close();
					}
				} catch ( Exception ex ) {
					log.error( "bos.close():Exception " + ex.toString(), ex );
				}
				try {
					response.flushBuffer();
				} catch ( Exception ex ) {

					log.error( "response.flushBuffer():Exception " + ex.toString(), ex );
				}
			}
			return null;

		} catch ( Exception e ) {
			String msg = "Exception:  RemoteAddr: " + request.getRemoteAddr()  
			+ ", scanFileIdString: " + scanFileIdString
			+ ", projectSearchIdString: " + projectSearchIdString
			+ ", Exception caught: " + e.toString()
			;
			log.error( msg, e );
			response.sendError( 
					WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE.getStatusCode(), 
					WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT );
			return null;
		}
	}

}

