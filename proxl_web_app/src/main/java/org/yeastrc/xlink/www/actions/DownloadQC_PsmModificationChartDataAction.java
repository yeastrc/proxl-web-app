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
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.qc_data.modification_statistics_merged.main.QC_PSM_CountsPerModification_Merged;
import org.yeastrc.xlink.www.qc_data.modification_statistics_merged.objects.QC_PSM_CountsPerModification_Merged_Results;
import org.yeastrc.xlink.www.qc_data.modification_statistics_merged.objects.QC_PSM_CountsPerModification_Merged_Results.QC_PSM_CountsPerModificationResultsPerLinkType_Merged;
import org.yeastrc.xlink.www.qc_data.modification_statistics_merged.objects.QC_PSM_CountsPerModification_Merged_Results.QC_PSM_CountsPerModificationResultsPerModMass_Merged;
import org.yeastrc.xlink.www.qc_data.modification_statistics_merged.objects.QC_PSM_CountsPerModification_Merged_Results.QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

/**
 * 
 * Download data for QC Chart PSM Modification Data
 */
public class DownloadQC_PsmModificationChartDataAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( DownloadQC_PsmModificationChartDataAction.class);
	
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
				QC_PSM_CountsPerModification_Merged_Results qc_PSM_CountsPerModification_Merged_Results =
						QC_PSM_CountsPerModification_Merged.getInstance()
						.getQC_PSM_CountsPerModification_Merged( form.getQueryJSON(), searches );
				
//				if ( ! qc_PSM_CountsPerModification_Merged_Results.isFoundData() ) {
//					
//					
//				}
				
				List<QC_PSM_CountsPerModificationResultsPerLinkType_Merged> resultsPerLinkTypeList = qc_PSM_CountsPerModification_Merged_Results.getResultsPerLinkTypeList();
				
				List<String> linkTypesList = new ArrayList<>( resultsPerLinkTypeList.size() );
				for ( QC_PSM_CountsPerModificationResultsPerLinkType_Merged resultPerLinkType : resultsPerLinkTypeList ) {
					linkTypesList.add( resultPerLinkType.getLinkType() );
				}

				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");

				// generate file name
				String filename = "proxl-qc-psm-modification-" 
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
				writer.write( "FRACTION\tCOUNT\tTOTAL COUNT\tMOD MASS\tLINK TYPE\tSEARCH ID" );
				writer.write( "\n" );
				
				for ( QC_PSM_CountsPerModificationResultsPerLinkType_Merged resultPerLinkType : resultsPerLinkTypeList ) {
					String linkType = resultPerLinkType.getLinkType();
//					boolean dataFound;
					List<QC_PSM_CountsPerModificationResultsPerModMass_Merged> resultsPerModMassList = resultPerLinkType.getResultsPerModMassList();
					for ( QC_PSM_CountsPerModificationResultsPerModMass_Merged dataForChartPerModMassValue : resultsPerModMassList ) {
						String modMassLabel = dataForChartPerModMassValue.getModMassLabel();
						if ( dataForChartPerModMassValue.isNoModMass() ) {
							modMassLabel = "None"; // hard coded in JS
						}
						Map<Integer, QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged> countPerSearchIdMap_KeyProjectSearchId = dataForChartPerModMassValue.getCountPerSearchIdMap_KeyProjectSearchId();

						for ( SearchDTO search : searches ) {
							int searchId = search.getSearchId();
							Integer projectSearchId = search.getProjectSearchId();
							QC_PSM_CountsPerModificationResults_Per_ModMass_SearchId_Merged countPerSearchId =
									countPerSearchIdMap_KeyProjectSearchId.get( projectSearchId );
							if ( countPerSearchId != null ) {
								
								double fraction = countPerSearchId.getCount() / (double) countPerSearchId.getTotalCount();
								writer.write( Double.toString( fraction ) );
								writer.write( "\t" );
								writer.write( Long.toString( countPerSearchId.getCount() ) );
								writer.write( "\t" );
								writer.write( Long.toString( countPerSearchId.getTotalCount() ) );
								writer.write( "\t" );
								writer.write( modMassLabel );
								writer.write( "\t" );
								writer.write( linkType );
								writer.write( "\t" );
								writer.write( Integer.toString( searchId ) );
								writer.write( "\t" );
								writer.write( "\n" );
							}
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
