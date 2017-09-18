package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchIdScanFileIdCombinedRecordExistsSearcher;
import org.yeastrc.xlink.dao.ScanFileDAO;
import org.yeastrc.xlink.dao.ScanFileMS_1_IntensityBinnedSummedDataDAO;
import org.yeastrc.xlink.dto.ScanFileMS_1_IntensityBinnedSummedDataDTO;
import org.yeastrc.xlink.ms1_binned_summed_intensities.main.MS1_BinnedSummedIntensitiesProcessing;
import org.yeastrc.xlink.ms1_binned_summed_intensities.objects.MS1_IntensitiesBinnedSummedMapToJSONRoot;
import org.yeastrc.xlink.utils.ZipUnzipByteArray;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

/**
 * 
 * Download data for QC Chart Scan File MS1 VS Retention Time Data
 */
public class DownloadQC_MS1_VS_RetentionTime_ChartDataAction extends Action {
	
	private static final Logger log = Logger.getLogger(DownloadQC_MS1_VS_RetentionTime_ChartDataAction.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		String scanFileIdString = null;
		String projectSearchIdString = null;

		int scanFileId = 0;
		int projectSearchId = 0;
		
		try {
			// Get the session first.  
//			HttpSession session = request.getSession();
			
			scanFileIdString = request.getParameter( "scan_file_id" );
			projectSearchIdString = request.getParameter( "project_search_id" );


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
			
			//   Get the project id for these searches
			Set<Integer> projectSearchIds = new HashSet<Integer>( );
			projectSearchIds.add( projectSearchId );

			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIds );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int searchId : projectSearchIds ) {
					msg += searchId + ", ";
				}
				log.error( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			request.setAttribute( "projectId", projectId ); 
			///////////////////////
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			//  Test access to the project id
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
			
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////

			//  Confirm the scan file id is in one of the project search ids
			
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

			if ( ! SearchIdScanFileIdCombinedRecordExistsSearcher.getInstance()
					.recordExistsForSearchIdScanFileIdCombined( searchId, scanFileId ) ) {
				String msg = "Provided scan_file_id not for for provided project search id.  "
						+ "scan_file_id: " + scanFileId + ", projectSearchId " + projectSearchId;
				log.warn( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			
			String scanFilename = ScanFileDAO.getInstance().getScanFilenameById( scanFileId );
			if ( scanFilename == null ) {
				String msg = "Provided scan_file_id not found.  "
						+ "scan_file_id: " + scanFileId;
				log.warn( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}

			//  Copied from class Scan_MS_1_IonCurrent_Histograms

			ScanFileMS_1_IntensityBinnedSummedDataDTO scanFileMS_1_IntensityBinnedSummedDataDTO =
					ScanFileMS_1_IntensityBinnedSummedDataDAO.getFromScanFileId( scanFileId );
			if ( scanFileMS_1_IntensityBinnedSummedDataDTO == null ) {
				String msg = "No ScanFileMS_1_IntensityBinnedSummedDataDTO data found for provided scan_file_id.  "
						+ "scan_file_id: " + scanFileId;
				log.warn( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			
			byte[] dataJSON_Gzipped = scanFileMS_1_IntensityBinnedSummedDataDTO.getDataJSON_Gzipped();
			byte[] dataJSON = ZipUnzipByteArray.getInstance().unzipByteArray( dataJSON_Gzipped );

			MS1_IntensitiesBinnedSummedMapToJSONRoot ms1_IntensitiesBinnedSummedMapToJSONRoot =
					MS1_BinnedSummedIntensitiesProcessing.getInstance().getMainObjectFromBytes( dataJSON );

			Map<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap = ms1_IntensitiesBinnedSummedMapToJSONRoot.getMs1_IntensitiesBinnedSummedMap();

			//  Sum Intensities by Retention Time 

			Map<Long, MutableDouble> ms1_IntensitiesBinnedSummedMappedByRetentionTime = new HashMap<>();
			
			for ( Map.Entry<Long, Map<Long, Double>> retentionTimeEntry : ms1_IntensitiesBinnedSummedMap.entrySet() ) {
				double intensitySummedForRetentionTime = 0;
				for ( Map.Entry<Long, Double> m_Over_Z_Entry : retentionTimeEntry.getValue().entrySet() ) {
					//  intensityForBin is for Retention Time / M over Z bin
					double intensityForBin = m_Over_Z_Entry.getValue();
					intensitySummedForRetentionTime += intensityForBin;
				}
				ms1_IntensitiesBinnedSummedMappedByRetentionTime.put( 
						retentionTimeEntry.getKey(), new MutableDouble( intensitySummedForRetentionTime ) );
			}
			
			//  Put in list to sort on key
			List<Map.Entry<Long, MutableDouble>> ms1_IntensitiesBinnedSummedMappedByRetentionTime_List = new ArrayList<>( ms1_IntensitiesBinnedSummedMappedByRetentionTime.size() );
			for ( Map.Entry<Long, MutableDouble> entry : ms1_IntensitiesBinnedSummedMappedByRetentionTime.entrySet() ) {
				ms1_IntensitiesBinnedSummedMappedByRetentionTime_List.add( entry );
			}
			Collections.sort( ms1_IntensitiesBinnedSummedMappedByRetentionTime_List, new Comparator<Map.Entry<Long, MutableDouble>>() {
				@Override
				public int compare(Entry<Long, MutableDouble> o1, Entry<Long, MutableDouble> o2) {
					if ( o1.getKey() < o2.getKey() ) {
						return -1;
					} else if ( o1.getKey() > o2.getKey() ) {
						return 1;
					}
					return 0;
				}
			});
			
			
			OutputStreamWriter writer = null;
			try {

				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");

				// generate file name
				String filename = "proxl-qc-summary-scan-ms1-vs-retention-time-search-"
						+ searchId
						+ "-scan-"
						+ scanFileId
						+ "-" + fmt.print( dt )
						+ ".txt";
				
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				ServletOutputStream out = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(out);
				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );
								
				//  Write scan filename
				
				writer.write ( "Scan filename: " );
				writer.write ( scanFilename );
				writer.write( "\n" );
				
				//  Write header line
				writer.write( "ION CURRENT SUMMED FOR BIN\tRETENTION TIME BIN START (>=)\tRETENTION TIME BIN END (<)\tSEARCH ID" );
				writer.write( "\n" );
				
				for ( Map.Entry<Long, MutableDouble> entry : ms1_IntensitiesBinnedSummedMappedByRetentionTime_List ) {
					writer.write( entry.getValue().toString() );  //  summed ion current
					writer.write( "\t" );
					writer.write( Long.toString( entry.getKey() ) ); // bin start
					writer.write( "\t" );
					writer.write( Long.toString( entry.getKey().longValue() + 1 ) ); // bin end
					writer.write( "\t" );
					writer.write( Integer.toString( searchId ) );
					writer.write( "\t" );
					writer.write( "\n" );
				}
			} finally {
				try {
					if ( writer != null ) {
						writer.close();
					}
				} catch ( Exception ex ) {
					log.error( "writer.close():Exception " + ex.toString(), ex );
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
					+ ", Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}
