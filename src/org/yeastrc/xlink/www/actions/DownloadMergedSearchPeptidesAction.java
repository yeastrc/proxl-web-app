package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
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
import org.yeastrc.xlink.www.objects.AnnDisplayNameDescPeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnValuePeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.WebMergedProteinPosition;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptide;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.actions.PeptidesMergedCommonPageDownload.PeptidesMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.XLinkWebAppUtils;




public class DownloadMergedSearchPeptidesAction extends Action {
	
	private static final Logger log = Logger.getLogger(DownloadMergedSearchPeptidesAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		
		
		try {

			// our form
			MergedSearchViewPeptidesForm form = (MergedSearchViewPeptidesForm)actionForm;
			request.setAttribute( "mergedSearchViewCrosslinkPeptideForm", form );


			// Get the session first.  
//			HttpSession session = request.getSession();


			int[] searchIds = form.getSearchIds();
			
			
			if ( searchIds.length == 0 ) {
				
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}


			//   Get the project id for these searches

			Set<Integer> searchIdsSet = new HashSet<Integer>( );

			for ( int searchId : searchIds ) {

				searchIdsSet.add( searchId );
			}


			List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searchIdsSet );

			Collections.sort( searchIdsListDeduppedSorted );


			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsSet );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				
				String msg = "No project ids for search ids: ";
				for ( int searchId : searchIds ) {

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






			request.setAttribute( "searchIds", searchIdsListDeduppedSorted );

			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			
			Map<Integer, SearchDTO> searchesMapOnId = new HashMap<>();

			for( int searchId : searchIdsListDeduppedSorted ) {

				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );

				if ( search == null ) {

					String msg = "Percolator search id '" + searchId + "' not found in the database. User taken to home page.";

					log.warn( msg );

					//  Search not found, the data on the page they are requesting does not exist.
					//  The data on the user's previous page no longer reflects what is in the database.
					//  Take the user to the home page

					return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
				}

				searches.add( search );
				
				searchesMapOnId.put( searchId, search );
			}




			OutputStreamWriter writer = null;
			
			try {
				

				
				////////     Get Merged Peptides


				PeptidesMergedCommonPageDownloadResult peptidesMergedCommonPageDownloadResult =
						PeptidesMergedCommonPageDownload.getInstance()
						.getWebMergedPeptideRecords(
								form,
								searchIdsListDeduppedSorted,
								searches,
								searchesMapOnId,
								searchIdsSet );
				


				// generate file name
				String filename = "xlinks-peptides-search-";
				filename += StringUtils.join( form.getSearchIds(), '-' );

				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");
				filename += "-" + fmt.print( dt );

				filename += ".txt";

				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);

				

				ServletOutputStream out = response.getOutputStream();

				BufferedOutputStream bos = new BufferedOutputStream(out);

				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );




				writer.write( "SEARCH ID(S)\tTYPE\tPEPTIDE 1\tPOSITION\tMODS\tPEPTIDE 2\tPOSITION\tMODS\tPROTEIN 1\tPROTEIN 2\tNUM PSMS" );
				
				
				for ( AnnDisplayNameDescPeptPsmListsPair annDisplayNameDescPeptPsmListsPair : peptidesMergedCommonPageDownloadResult.getPeptidePsmAnnotationNameDescListsForEachSearch() ) {
				
					for ( AnnotationDisplayNameDescription peptideAnnotationDisplayNameDescription : annDisplayNameDescPeptPsmListsPair.getPeptideAnnotationNameDescriptionList() ) {
					
						writer.write( "\tPeptide Value:" );
						writer.write( peptideAnnotationDisplayNameDescription.getDisplayName() );
						writer.write( "(SEARCH ID: " );
						writer.write( Integer.toString( annDisplayNameDescPeptPsmListsPair.getSearchId() ) );
						writer.write( ")" );
					}
				
					for ( AnnotationDisplayNameDescription psmAnnotationDisplayNameDescription : annDisplayNameDescPeptPsmListsPair.getPeptideAnnotationNameDescriptionList() ) {
					
						writer.write( "\tBest PSM Value:" );
						writer.write( psmAnnotationDisplayNameDescription.getDisplayName() );
						writer.write( "(SEARCH ID: " );
						writer.write( Integer.toString( annDisplayNameDescPeptPsmListsPair.getSearchId() ) );
						writer.write( ")" );
					}
				}
				
				writer.write( "\n" );


				for( WebMergedReportedPeptide link : peptidesMergedCommonPageDownloadResult.getWebMergedReportedPeptideList() ) {

					

					List<WebMergedProteinPosition> peptide1ProteinPositions = link.getPeptide1ProteinPositions();
					List<WebMergedProteinPosition> peptide2ProteinPositions = link.getPeptide2ProteinPositions();
					
					String peptide1ProteinPositionsString = XLinkWebAppUtils.getPeptideProteinPositionsString( peptide1ProteinPositions );
					String peptide2ProteinPositionsString = XLinkWebAppUtils.getPeptideProteinPositionsString( peptide2ProteinPositions );


					
					List<Integer> searchIdsForLink = new ArrayList<Integer>( link.getSearches().size() );
					for( SearchDTO r : link.getSearches() ) { 
						searchIdsForLink.add( r.getId() ); 
					}
					Collections.sort( searchIdsForLink );

					writer.write( StringUtils.join( searchIdsForLink, "," ) );
					writer.write( "\t" );
					
					
					writer.write( link.getLinkType() );
					writer.write( "\t" );
					writer.write( link.getPeptide1().getSequence() );
					writer.write( "\t" );
					writer.write( link.getPeptide1Position() );
					writer.write( "\t" );
					writer.write( link.getModsStringPeptide1() );
					writer.write( "\t" );
					if ( link.getPeptide2() != null ) {
						writer.write( link.getPeptide2().getSequence() );
					}
					writer.write( "\t" );
					writer.write( link.getPeptide2Position() );
					writer.write( "\t" );
					writer.write( link.getModsStringPeptide2() );
					writer.write( "\t" );
					writer.write( peptide1ProteinPositionsString );
					writer.write( "\t" );
					writer.write( peptide2ProteinPositionsString );
					
					
					writer.write( "\t" );
					writer.write( Integer.toString( link.getNumPsms() ) );

					for ( AnnValuePeptPsmListsPair annValuePeptPsmListsPair : link.getPeptidePsmAnnotationValueListsForEachSearch() ) {
					
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
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}
	
}
