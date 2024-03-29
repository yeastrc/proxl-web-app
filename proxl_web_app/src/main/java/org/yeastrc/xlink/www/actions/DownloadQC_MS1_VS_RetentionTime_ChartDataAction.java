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
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchIdScanFileIdCombinedRecordExistsSearcher;
import org.yeastrc.xlink.www.spectral_storage_service_interface.Call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice;
import org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto.MS1_IntensitiesBinnedSummedMapRoot;
import org.yeastrc.xlink.dao.ScanFileDAO;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.forms.SingleRequestJSONStringFieldForm;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.UnmarshalJSON_ToObject;

/**
 * 
 * Download data for QC Chart Scan File MS1 VS Retention Time Data
 */
public class DownloadQC_MS1_VS_RetentionTime_ChartDataAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( DownloadQC_MS1_VS_RetentionTime_ChartDataAction.class);

	/**
	 * Deserialize the JSON in the request to this
	 */
	public static class DownloadRequest {
		private Integer projectSearchId;
		private Integer scanFileId;
		public void setScanFileId(Integer scanFileId) {
			this.scanFileId = scanFileId;
		}
		public void setProjectSearchId(Integer projectSearchId) {
			this.projectSearchId = projectSearchId;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		Integer scanFileId = null;
		Integer projectSearchId = null;
		
		try {
			// our form
			SingleRequestJSONStringFieldForm form = (SingleRequestJSONStringFieldForm)actionForm;
			
			//  Form Parameter Name.  JSON encoded data
			String requestJSONString = form.getRequestJSONString();

			if ( StringUtils.isEmpty( requestJSONString ) ) {
				log.warn( "parameter requestJSONString is empty" );
				response.sendError( 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode(), 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
				return null;
			}
			
			DownloadRequest downloadRequest = null;
			try {
				downloadRequest =
						UnmarshalJSON_ToObject.getInstance().getObjectFromJSONString( requestJSONString, DownloadRequest.class );
			} catch ( Exception e ) {
				String msg = "parse request failed";
				log.warn( msg );
				response.sendError( 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode(), 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
				return null;
			}
			
			scanFileId = downloadRequest.scanFileId;
			projectSearchId = downloadRequest.projectSearchId;
			
			if ( scanFileId == null ) {
				log.warn( "parameter scanFileId is empty" );
				response.sendError( 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode(), 
						WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
				return null;
			}
			if ( projectSearchId == null ) {
				log.warn( "parameter projectSearchId is empty" );
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
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					//  No User session 
					return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
				}
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

			MS1_IntensitiesBinnedSummedMapRoot  ms1_IntensitiesBinnedSummedMapRoot = getMS1_IntensitiesBinnedSummedMapRoot( scanFileId );
			
			if ( ms1_IntensitiesBinnedSummedMapRoot == null ) {
				String msg = "No ms1_IntensitiesBinnedSummedMapRoot data found for provided scan_file_id.  "
						+ "scan_file_id: " + scanFileId;
				log.warn( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			
			Map<Long, Map<Long, Double>> ms1_IntensitiesBinnedSummedMap = ms1_IntensitiesBinnedSummedMapRoot.getMs1_IntensitiesBinnedSummedMap();

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
				writer.write( "ION CURRENT SUMMED FOR BIN\tRETENTION TIME BIN START (Seconds) (>=)\tRETENTION TIME BIN END (Seconds) (<)\tSEARCH ID" );
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

	/**
	 * @param scanFileId
	 * @return null if not in db
	 * @throws Exception
	 */
	private MS1_IntensitiesBinnedSummedMapRoot getMS1_IntensitiesBinnedSummedMapRoot( int scanFileId ) throws Exception {

		//  Get from Spectral Storage Service

		String spectralStorageAPIKey = ScanFileDAO.getInstance().getSpectralStorageAPIKeyById( scanFileId );

		if ( spectralStorageAPIKey == null ) {
			log.error( "No spectralStorageAPIKey value in scan file table for scanFileId: " + scanFileId );
			return null;  // EARLY RETURN
		}

		MS1_IntensitiesBinnedSummedMapRoot ms1_IntensitiesBinnedSummedMapRoot_FromSpectralStorage =
				Call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice.getSingletonInstance()
				.getScanPeakIntensityBinnedOn_RT_MZFromSpectralStorageService( spectralStorageAPIKey );

		if ( ms1_IntensitiesBinnedSummedMapRoot_FromSpectralStorage == null ) {

			log.error( "No data in Spectral Storage for ms1_IntensitiesBinnedSummedMapRoot_FromSpectralStorage. scanFileId: " + scanFileId 
					+ ", spectralStorageAPIKey: "
					+ spectralStorageAPIKey );

			return null;  // EARLY RETURN
		}

		return ms1_IntensitiesBinnedSummedMapRoot_FromSpectralStorage;
	}
}
