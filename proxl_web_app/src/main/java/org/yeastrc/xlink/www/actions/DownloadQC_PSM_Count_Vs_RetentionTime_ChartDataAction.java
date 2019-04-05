package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.form_query_json_objects.QCPageQueryJSONRoot;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.qc_data.a_enums.ForDownload_Enum;
import org.yeastrc.xlink.www.qc_data.a_request_json_root.QCPageRequestJSONRoot;
import org.yeastrc.xlink.www.qc_data.utils.QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot;
import org.yeastrc.xlink.www.qc_plots.scan_retention_time.CreateScanRetentionTimeQCPlotData;
import org.yeastrc.xlink.www.qc_plots.scan_retention_time.CreateScanRetentionTimeQCPlotData.CreateScanRetentionTimeQCPlotData_Result;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.Single_ScanRetentionTime_ScanNumber_SubResponse;
import org.yeastrc.xlink.dao.ScanFileDAO;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.SingleRequestJSONStringFieldForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

/**
 * 
 * Download data for QC Chart PSM - PSM Count Vs Retention Time Data
 * 
 * Only 1 project search id is allowed
 * 
 */
public class DownloadQC_PSM_Count_Vs_RetentionTime_ChartDataAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( DownloadQC_PSM_Count_Vs_RetentionTime_ChartDataAction.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {

			// our form
			SingleRequestJSONStringFieldForm form = (SingleRequestJSONStringFieldForm)actionForm;
		
			//  Form Parameter Name.  JSON encoded data
			String requestJSONString = form.getRequestJSONString();

			if ( StringUtils.isEmpty( requestJSONString ) ) {
				//  Invalid request, searches across projects
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			
			Integer projectSearchId = null;

			//  Must be size 1 if scanFileAll not populated, if empty when scanFileAll is populated, lookup scanfile ids for projectSearchId
			List<Integer> scanFileIdList = null;
			// If populated, assumed to be true
			String scanFileAllString = null; 

			boolean scanFileAll = false;
			
			QCPageRequestJSONRoot qcPageRequestJSONRoot = null;
			QCPageQueryJSONRoot qcPageQueryJSONRoot = null;
		
			try {
				qcPageRequestJSONRoot =
						QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot.getInstance().deserializeRequestJSON_To_QCPageRequestJSONRoot( requestJSONString );
			} catch ( Exception e ) {
				String msg = "Request rejected: parse request failed";
				log.warn( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			
			List<Integer> projectSearchIds = qcPageRequestJSONRoot.getProjectSearchIds();
			qcPageQueryJSONRoot = qcPageRequestJSONRoot.getQcPageQueryJSONRoot();
			
			if ( projectSearchIds == null || projectSearchIds.isEmpty() ) {
				log.warn( "Request rejected: projectSearchIds == null || projectSearchIds.isEmpty()" );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			//   !!!!  Only 1 project search id is allowed
			if ( projectSearchIds.size() > 1 ) {
				log.warn( "Request rejected: projectSearchIds.size() > 1.  Only 1 project search id is allowed" );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
		
			projectSearchId = projectSearchIds.get( 0 );

			scanFileIdList = qcPageRequestJSONRoot.getScanFileIdList();
			// If populated, assumed to be true
			scanFileAllString = qcPageRequestJSONRoot.getScanFileAllString(); 
			
			//  Ignored
//			retentionTimeInMinutesCutoff = qcPageRequestJSONRoot.getRetentionTimeInMinutesCutoff();
			
			qcPageQueryJSONRoot = qcPageRequestJSONRoot.getQcPageQueryJSONRoot();

			if ( StringUtils.isEmpty( scanFileAllString ) ) {
				if ( scanFileIdList == null || scanFileIdList.isEmpty() ) {
					String msg = "scanFileId is not provided and scanFileAll is empty or not provided";
					log.error( msg );
					return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
				}
				if ( scanFileIdList.size() > 1 ) {
					// scanFileId must be size 1 if scanFileAll not populated
					String msg = "More than 1 scanFileId and scanFileAll is empty or not provided";
					log.error( msg );
					return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
				}
			}
		
			if ( StringUtils.isNotEmpty( scanFileAllString ) ) {
				scanFileAll = true;
			}
			
			//  Start Auth
			
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int searchId : projectSearchIds ) {
				projectSearchIdsSet.add( searchId );
			}
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
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
			
			int projectSearchId_InprojectSearchIds = projectSearchId;
			
//			List<SearchDTO> searches = new ArrayList<SearchDTO>( projectSearchIds.length );
//			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();
//			List<Integer> searchIds = new ArrayList<>( projectSearchIds.length );
			
			Set<Integer> projectSearchIdsAlreadyProcessed = new HashSet<>();
			
//			for( int projectSearchId_InprojectSearchIds : projectSearchIds ) {
//				if ( projectSearchIdsAlreadyProcessed.contains( projectSearchId_InprojectSearchIds ) ) {
//					// ALready processed this projectSearchId, this must be a duplicate
//					continue; //  EARLY CONTINUE
//				}
				projectSearchIdsAlreadyProcessed.add( projectSearchId_InprojectSearchIds );
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId_InprojectSearchIds );
				if ( search == null ) {
					String msg = "projectSearchId '" + projectSearchId_InprojectSearchIds + "' not found in the database. User taken to home page.";
					log.warn( msg );
					//  Search not found, the data on the page they are requesting does not exist.
					//  The data on the user's previous page no longer reflects what is in the database.
					//  Take the user to the home page
					return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
				}
				
				int searchId = search.getSearchId();
				
//				searches.add( search );
//				searchesMapOnSearchId.put( search.getSearchId(), search );
//				searchIds.add( search.getSearchId() );
//			}
//			Collections.sort( searchIds );
			
			OutputStreamWriter writer = null;
			try {
				
				////////     Get Download Data
				
				CreateScanRetentionTimeQCPlotData_Result createScanRetentionTimeQCPlotData_Result = 
						CreateScanRetentionTimeQCPlotData.getInstance()
						.create( ForDownload_Enum.YES,  
								projectSearchId, 
								scanFileIdList, 
								scanFileAll, 
								qcPageQueryJSONRoot, 
								null /* retentionTimeInSecondsCutoff */ );
				
				String scanFilename = null;
				
				if ( ! scanFileAll ) {
					scanFilename = ScanFileDAO.getInstance().getScanFilenameById( scanFileIdList.get( 0 ) );
				}
				
				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");

				// generate file name
				String filename = "proxl-qc-psm-count-vs-retention-time-" 
						+ "-search-"
//						+ StringUtils.join( searchIds, '-' )
						+ searchId
						+ "-" + fmt.print( dt )
						+ ".txt";
				
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				ServletOutputStream out = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(out);
				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );

				final String GROUP_ALL_MS2_SCANS = "ALL MS2 SCANS";
				final String FILTERED_PSMs = "FILTERED PSMs";

				//  Write header line
				writer.write( "RETENTION TIME (Seconds) (Not filtered for Max X or Max Y)\tGROUP ("
						+ "'" + GROUP_ALL_MS2_SCANS 
						+ "' OR '" + FILTERED_PSMs + "')\tSEARCH ID" );
				
				if ( scanFilename != null ) {
					writer.write( "\t  (scan filename: " + scanFilename + ")" );
				}
				
				writer.write( "\n" );
				
				List<BigDecimal> retentionTimeForPSMsthatMeetCriteriaList = createScanRetentionTimeQCPlotData_Result.getRetentionTimeForPSMsthatMeetCriteriaList();
				List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanRetentionTime_AllScansExcludeScanLevel_1_List = createScanRetentionTimeQCPlotData_Result.getScanRetentionTime_AllScansExcludeScanLevel_1_List();

				//  output filtered PSMs
				for ( BigDecimal retentionTime : retentionTimeForPSMsthatMeetCriteriaList ) {
					writer.write( retentionTime.toString() );
					writer.write( "\t" );
					writer.write( FILTERED_PSMs );
					writer.write( "\t" );
					writer.write( Integer.toString( searchId ) );
					writer.write( "\t" );
					writer.write( "\n" );
				}
				
				//  output All MS2 Scans
				for ( Single_ScanRetentionTime_ScanNumber_SubResponse single_ScanRetentionTime_ScanNumber_SubResponse : scanRetentionTime_AllScansExcludeScanLevel_1_List ) {
					writer.write( Float.toString( single_ScanRetentionTime_ScanNumber_SubResponse.getRetentionTime() ) );
					writer.write( "\t" );
					writer.write( GROUP_ALL_MS2_SCANS );
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
