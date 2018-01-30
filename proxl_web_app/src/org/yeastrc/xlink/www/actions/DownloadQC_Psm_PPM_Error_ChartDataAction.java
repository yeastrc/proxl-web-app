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
import org.yeastrc.xlink.www.qc_data.psm_error_estimates_merged.main.PPM_Error_Chart_For_PSMPeptideCutoffs_Merged;
import org.yeastrc.xlink.www.qc_data.psm_error_estimates_merged.main.PPM_Error_Chart_For_PSMPeptideCutoffs_Merged.PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Method_Response;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

/**
 * 
 * Download data for QC Chart PSM PPM Error Data
 */
public class DownloadQC_Psm_PPM_Error_ChartDataAction extends Action {
	
	private static final Logger log = Logger.getLogger(DownloadQC_Psm_PPM_Error_ChartDataAction.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			// our form
			MergedSearchViewPeptidesForm form = (MergedSearchViewPeptidesForm)actionForm;
			
			String requestQueryString = request.getQueryString();
			
			// Get the session first.  
//			HttpSession session = request.getSession();
			int[] projectSearchIds = form.getProjectSearchId();
			if ( projectSearchIds.length == 0 ) {
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
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
			
			List<SearchDTO> searches = new ArrayList<SearchDTO>( projectSearchIds.length );
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();
			List<Integer> searchIds = new ArrayList<>( projectSearchIds.length );
			
			Set<Integer> projectSearchIdsAlreadyProcessed = new HashSet<>();
			
			for( int projectSearchId : projectSearchIds ) {
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
				
				String filterCriteria_JSONString = form.getQueryJSON();

				PPM_Error_Chart_For_PSMPeptideCutoffs_Merged_Method_Response ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_Method_Response =
						PPM_Error_Chart_For_PSMPeptideCutoffs_Merged.getInstance()
						.getPPM_Error_Chart_For_PSMPeptideCutoffs_Merged(
								PPM_Error_Chart_For_PSMPeptideCutoffs_Merged.ForDownload.YES,
								filterCriteria_JSONString, 
								requestQueryString,
								searches );
				
				/**
				 * Lists of PPM Error mapped by search id then link type
				 * Map <{Link Type},,Map<<Search id>,List<{PPM Error}>>>
				 */
				Map<String,Map<Integer,List<Double>>> allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType =
						ppm_Error_Chart_For_PSMPeptideCutoffs_Merged_Method_Response.getAllSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType();

				List<String> linkTypesList = new ArrayList<>( allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType.size() );
				for ( String linkType : allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType.keySet() ) {
					linkTypesList.add( linkType );
				}

				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");

				// generate file name
				String filename = "proxl-qc-psm-ppm-error-" 
						+ StringUtils.join( linkTypesList, '-' ) 
						+ "-search-"
						+ StringUtils.join( searchIds, '-' )
						+ "-" + fmt.print( dt )
						+ ".txt";
				
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				ServletOutputStream out = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(out);
				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );
								
				//  Write header line
				writer.write( "PPM ERROR\tLINK TYPE\tSEARCH ID" );
				writer.write( "\n" );
				
				/**
				 * Lists of preMZ mapped by search id then link type
				 * Map<[link type],Map<[Search id]>,List<[preMZ]>>>
				 */
//				Map<String,Map<Integer,List<BigDecimal>>> allSearchesCombinedPreMZList_Map_KeyedOnSearchId_KeyedOnLinkType =

				for ( Map.Entry<String,Map<Integer,List<Double>>> entryPerLinkType : allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId_KeyedOnLinkType.entrySet() ) {
					String linkType = entryPerLinkType.getKey();
					Map<Integer,List<Double>> allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId = entryPerLinkType.getValue();
					for ( Map.Entry<Integer,List<Double>> entryPerSearchId : allSearchesCombined_PPM_Error_List_Map_KeyedOnSearchId.entrySet() ) {
						int searchId = entryPerSearchId.getKey();
						List<Double> ppmErrorList = entryPerSearchId.getValue();
						for ( Double ppmError : ppmErrorList ) {
							writer.write( ppmError.toString() );
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
