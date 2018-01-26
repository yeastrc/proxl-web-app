package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.main.PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.main.PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs.PPMErrorRetentionTimePair;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates.main.PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs.PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_Method_Response;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

/**
 * 
 * Download data for QC Chart PSM PPM Error Vs Retention Time Data
 */
public class DownloadQC_Psm_PPM_Error_VS_RT_ChartDataAction extends Action {
	
	private static final Logger log = Logger.getLogger(DownloadQC_Psm_PPM_Error_VS_RT_ChartDataAction.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			String requestQueryString = request.getQueryString();
			
			// our form
			MergedSearchViewPeptidesForm form = (MergedSearchViewPeptidesForm)actionForm;
			// Get the session first.  
//			HttpSession session = request.getSession();
			int[] projectSearchIds = form.getProjectSearchId();
			if ( projectSearchIds.length == 0 ) {
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			if ( projectSearchIds.length > 1 ) {
				String msg = "Only 1 project search id is allowed.";
				log.error( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}

			int projectSearchId = projectSearchIds[ 0 ];
			
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.add( projectSearchId );
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

			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			if ( search == null ) {
				String msg = "projectSearchId '" + projectSearchId + "' not found in the database. User taken to home page.";
				log.warn( msg );
				//  Search not found, the data on the page they are requesting does not exist.
				//  The data on the user's previous page no longer reflects what is in the database.
				//  Take the user to the home page
				return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
			}
			int searchId = search.getSearchId();
			
			OutputStreamWriter writer = null;
			try {
				
				////////     Get Download Data
				
				String filterCriteria_JSONString = form.getQueryJSON();

				PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_Method_Response ppm_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_Method_Response =
						PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs.getInstance()
						.getPPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs( 
								requestQueryString,
								PPM_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs.ForDownload.YES,
								filterCriteria_JSONString, search );
				
				/**
				 * Map<[link type]...>
				 */
				Map<String, List<PPMErrorRetentionTimePair>> ppmErrorListForLinkType_ByLinkType =
						ppm_Error_Vs_RT_ScatterPlot_For_PSMPeptideCutoffs_Method_Response.getPpmErrorListForLinkType_ByLinkType();

				
				List<String> linkTypesList = new ArrayList<>( ppmErrorListForLinkType_ByLinkType.size() );
				for ( String linkType : ppmErrorListForLinkType_ByLinkType.keySet() ) {
					linkTypesList.add( linkType );
				}

				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");

				// generate file name
				String filename = "proxl-qc-psm-ppm-error-vs-retention-time-" 
						+ StringUtils.join( linkTypesList, '-' ) 
						+ "-search-"
						+ searchId
						+ "-" + fmt.print( dt )
						+ ".txt";
				
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				ServletOutputStream out = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(out);
				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );
								
				//  Write header line
				writer.write( "PPM ERROR\tRETENTION TIME(seconds)\tLINK TYPE\tSEARCH ID" );
				writer.write( "\n" );
				
				/**
				 * Map<[link type]...>
				 */
//				Map<String, List<PPMErrorRetentionTimePair>> ppmErrorListForLinkType_ByLinkType =

				for ( Map.Entry<String,List<PPMErrorRetentionTimePair>> entryPerLinkType : ppmErrorListForLinkType_ByLinkType.entrySet() ) {
					String linkType = entryPerLinkType.getKey();
					List<PPMErrorRetentionTimePair> ppmErrorList = entryPerLinkType.getValue();
					for ( PPMErrorRetentionTimePair ppmErrorRTPair : ppmErrorList ) {
						writer.write( Double.toString( ppmErrorRTPair.getPpmError() ) );
						writer.write( "\t" );
						writer.write( ppmErrorRTPair.getRetentionTime().toString() ); //  In Minutes
						writer.write( "\t" );
						writer.write( linkType );
						writer.write( "\t" );
						writer.write( Integer.toString( searchId ) );
						writer.write( "\t" );
						writer.write( "\n" );
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
