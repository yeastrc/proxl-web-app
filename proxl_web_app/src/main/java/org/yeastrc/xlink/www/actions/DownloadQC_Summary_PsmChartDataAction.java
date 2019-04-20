package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.qc_data.a_enums.ForDownload_Enum;
import org.yeastrc.xlink.www.qc_data.a_request_json_root.QCPageRequestJSONRoot;
import org.yeastrc.xlink.www.qc_data.summary_statistics_merged.main.QC_SummaryCounts_Merged;
import org.yeastrc.xlink.www.qc_data.summary_statistics_merged.main.QC_SummaryCounts_Merged.QC_SummaryCounts_Merged_Method_Response;
import org.yeastrc.xlink.www.qc_data.summary_statistics_merged.objects.QC_SummaryCounts_Merged_Results;
import org.yeastrc.xlink.www.qc_data.summary_statistics_merged.objects.QC_SummaryCounts_Merged_Results.QC_SummaryCountsResultsPerLinkType_Merged;
import org.yeastrc.xlink.www.qc_data.summary_statistics_merged.objects.QC_SummaryCounts_Merged_Results.QC_SummaryCountsResults_PerSearchId_Merged;
import org.yeastrc.xlink.www.qc_data.utils.QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.SingleRequestJSONStringFieldForm;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

/**
 * 
 * Download data for QC Chart Summary PSM Data
 */
public class DownloadQC_Summary_PsmChartDataAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( DownloadQC_Summary_PsmChartDataAction.class);
	
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

			QCPageRequestJSONRoot qcPageRequestJSONRoot = null;
			try {
				qcPageRequestJSONRoot =
						QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot.getInstance().deserializeRequestJSON_To_QCPageRequestJSONRoot( requestJSONString );
			} catch ( Exception e ) {
				String msg = "parse request failed";
				log.warn( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			
			List<Integer> projectSearchIdList = qcPageRequestJSONRoot.getProjectSearchIds();
			QCPageQueryJSONRoot qcPageQueryJSONRoot = qcPageRequestJSONRoot.getQcPageQueryJSONRoot();
			
			if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
				String msg = "No projectSearchIdList or is empty";
				log.warn( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int searchId : projectSearchIdList ) {
				projectSearchIdsSet.add( searchId );
			}
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int searchId : projectSearchIdList ) {
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
			
			List<SearchDTO> searches = new ArrayList<SearchDTO>( projectSearchIdList.size() );
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();
			List<Integer> searchIds = new ArrayList<>( projectSearchIdList.size() );
			
			Set<Integer> projectSearchIdsAlreadyProcessed = new HashSet<>();
			
			for( int projectSearchId : projectSearchIdList ) {
				if ( projectSearchIdsAlreadyProcessed.contains( projectSearchId ) ) {
					// ALready processed this projectSearchId, this must be a duplicate
					continue; //  EARLY CONTINUE
				}
				projectSearchIdsAlreadyProcessed.add( projectSearchId );
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = "projectSearchId '" + projectSearchId + "' not found in the database. User taken to home page.";
					log.warn( msg );
					//  Search not found, the data on the page they are requesting does not exist.
					//  The data on the user's previous page no longer reflects what is in the database.
					//  Take the user to the home page
					return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
				}
				searches.add( search );
				searchesMapOnSearchId.put( search.getSearchId(), search );
				searchIds.add( search.getSearchId() );
			}
//			Collections.sort( searchIds );
			
			OutputStreamWriter writer = null;
			try {

				////////     Get Download Data

				QC_SummaryCounts_Merged_Method_Response qc_SummaryCounts_Merged_Method_Response =
						QC_SummaryCounts_Merged.getInstance()
						.getQC_SummaryCounts_Merged( 
								null /* requestJSONBytes */,
								ForDownload_Enum.YES,
								qcPageQueryJSONRoot,
								searches );
				
				QC_SummaryCounts_Merged_Results qc_SummaryCountsResults_Merged =
						qc_SummaryCounts_Merged_Method_Response.getSummaryCounts_Merged_Results();
				
				List<QC_SummaryCountsResultsPerLinkType_Merged> psmCountPerLinkTypeList = qc_SummaryCountsResults_Merged.getPsmCountPerLinkTypeList();
				
				List<String> linkTypesList = new ArrayList<>( psmCountPerLinkTypeList.size() );
				for ( QC_SummaryCountsResultsPerLinkType_Merged resultPerLinkType : psmCountPerLinkTypeList ) {
					linkTypesList.add( resultPerLinkType.getLinkType() );
				}

				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");

				// generate file name
				String filename = "proxl-qc-summary-psm-count-search-"
						+ StringUtils.join( searchIds, '-' )
						+ "-" + fmt.print( dt )
						+ ".txt";
				
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				ServletOutputStream out = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(out);
				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );
								
				//  Write header line
				writer.write( "COUNT\tCHARGE\tLINK TYPE\tSEARCH ID" );
				writer.write( "\n" );
				
				for ( QC_SummaryCountsResultsPerLinkType_Merged resultPerLinkType : psmCountPerLinkTypeList ) {
					String linkType = resultPerLinkType.getLinkType();
//					boolean dataFound;
					Map<Integer,QC_SummaryCountsResults_PerSearchId_Merged> countPerSearchIdMap_KeyProjectSearchId = resultPerLinkType.getCountPerSearchIdMap_KeyProjectSearchId();
					for ( SearchDTO search : searches ) {
						int searchId = search.getSearchId();
						Integer projectSearchId = search.getProjectSearchId();
						QC_SummaryCountsResults_PerSearchId_Merged countPerSearchId = 
								countPerSearchIdMap_KeyProjectSearchId.get( projectSearchId );
						if ( countPerSearchId != null ) {
							writer.write( Long.toString( countPerSearchId.getCount() ) );
							writer.write( "\t" );
							writer.write( linkType );
							writer.write( "\t" );
							writer.write( Integer.toString( searchId ) );
							writer.write( "\t" );
							writer.write( "\n" );
						}
					}
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
