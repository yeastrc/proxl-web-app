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
import org.yeastrc.xlink.www.objects.AnnDisplayNameDescPeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnValuePeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.actions.ProteinsAllMergedCommonPageDownload.AllProteinsMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.actions.ProteinsAllMergedCommonPageDownload.MergedProteinSingleEntry;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
/**
 * 
 *
 */
public class DownloadMergedSearchProteinsAllAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( DownloadMergedSearchProteinsAllAction.class);
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			// our form
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm)actionForm;
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for these searches
			int[] projectSearchIds = form.getProjectSearchId();
			if ( projectSearchIds.length == 0 ) {
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int projectSearchId : projectSearchIds ) {
				projectSearchIdsSet.add( projectSearchId );
			}
			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdsListDeduppedSorted );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int projectSearchId : projectSearchIds ) {
					msg += projectSearchId + ", ";
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
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );
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

			List<SearchDTO> searches = new ArrayList<SearchDTO>( projectSearchIdsListDeduppedSorted.size() );
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();
			int[] searchIdsArray = new int[ projectSearchIdsListDeduppedSorted.size() ];
			int searchIdsArrayIndex = 0;
			List<Integer> searchIds = new ArrayList<>( projectSearchIdsListDeduppedSorted.size() );
			
			for( int projectSearchId : projectSearchIdsListDeduppedSorted ) {
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = "search id '" + projectSearchId + "' not found in the database. User taken to home page.";
					log.warn( msg );
					//  Search not found, the data on the page they are requesting does not exist.
					//  The data on the user's previous page no longer reflects what is in the database.
					//  Take the user to the home page
					return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
				}
				searches.add( search );
				searchesMapOnSearchId.put( search.getSearchId(), search );
				searchIdsArray[ searchIdsArrayIndex ] = search.getSearchId();
				searchIdsArrayIndex++;
				searchIds.add( search.getSearchId() );
			}
			// Sort searches list
			Collections.sort( searches, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getSearchId() - o2.getSearchId();
				}
			});
			Collections.sort( searchIds );
			
			OutputStreamWriter writer = null;
			try {
				//  Get Merged Proteins, All Types dependent on user selection
				AllProteinsMergedCommonPageDownloadResult allProteinsMergedCommonPageDownloadResult =
						ProteinsAllMergedCommonPageDownload.getInstance()
						.getAllProteinsWrapped(
								form,
								projectSearchIdsListDeduppedSorted,
								searches,
								searchesMapOnSearchId  );
				
				// generate file name
				String filename = "xlinks-proteins-all-search-";
				filename += StringUtils.join( searchIdsArray, '-' );
				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");
				filename += "-" + fmt.print( dt );
				filename += ".txt";
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				ServletOutputStream out = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(out);
				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );
				writer.write( "SEARCH ID(S)\tPROTEIN\tNUM PSMS\tNUM PEPTIDES\tNUM UNIQUE PEPTIDES" );
				for ( AnnDisplayNameDescPeptPsmListsPair annDisplayNameDescPeptPsmListsPair : allProteinsMergedCommonPageDownloadResult.getPeptidePsmAnnotationNameDescListsForEachSearch() ) {
					for ( AnnotationDisplayNameDescription peptideAnnotationDisplayNameDescription : annDisplayNameDescPeptPsmListsPair.getPeptideAnnotationNameDescriptionList() ) {
						writer.write( "\tPeptide Value:" );
						writer.write( peptideAnnotationDisplayNameDescription.getDisplayName() );
						writer.write( "(SEARCH ID: " );
						writer.write( Integer.toString( annDisplayNameDescPeptPsmListsPair.getSearchId() ) );
						writer.write( ")" );
					}
					for ( AnnotationDisplayNameDescription psmAnnotationDisplayNameDescription : annDisplayNameDescPeptPsmListsPair.getPsmAnnotationNameDescriptionList() ) {
						writer.write( "\tBest PSM Value:" );
						writer.write( psmAnnotationDisplayNameDescription.getDisplayName() );
						writer.write( "(SEARCH ID: " );
						writer.write( Integer.toString( annDisplayNameDescPeptPsmListsPair.getSearchId() ) );
						writer.write( ")" );
					}
				}
				writer.write( "\n" );
				for( MergedProteinSingleEntry protein : allProteinsMergedCommonPageDownloadResult.getProteins() ) {
					List<Integer> searchIdsForLink = new ArrayList<Integer>( protein.getSearches().size() );
					for( SearchDTO r : protein.getSearches() ) { 
						searchIdsForLink.add( r.getSearchId() ); 
					}
					Collections.sort( searchIdsForLink );
					writer.write( StringUtils.join( searchIdsForLink, "," ) );
					writer.write( "\t" );
					writer.write( protein.getProtein().getName() );
					writer.write( "\t" );
					writer.write( Integer.toString( protein.getNumPsms() ) );
					writer.write( "\t" );
					writer.write( Integer.toString( protein.getNumPeptides() ) );
					writer.write( "\t" );
					writer.write( Integer.toString( protein.getNumUniquePeptides() ) );
					for ( AnnValuePeptPsmListsPair annValuePeptPsmListsPair : protein.getPeptidePsmAnnotationValueListsForEachSearch() ) {
						for ( String peptideAnnotationValue : annValuePeptPsmListsPair.getPeptideAnnotationValueList() ) {
							writer.write( "\t" );
							writer.write( peptideAnnotationValue );
						}
						for ( String psmAnnotationValue : annValuePeptPsmListsPair.getPsmAnnotationValueList() ) {
							writer.write( "\t" );
							writer.write( psmAnnotationValue );
						}
					}
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
