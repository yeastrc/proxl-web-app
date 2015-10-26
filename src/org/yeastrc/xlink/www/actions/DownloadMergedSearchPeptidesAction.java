package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.WebMergedProteinPosition;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptide;
import org.yeastrc.xlink.www.searcher.PeptideMergedWebPageSearcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchModMassDistinctSearcher;
import org.yeastrc.xlink.www.constants.DynamicModificationsSelectionConstants;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
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
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			
			for ( int searchId : searchIds ) {

				searchIdsCollection.add( searchId );
			}
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			
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
			

			
			String project_id_from_query_string = request.getParameter( WebConstants.PARAMETER_PROJECT_ID );
			

			if ( StringUtils.isEmpty( project_id_from_query_string ) ) {

				//  copy the project from the searches to the URL and redirect to that new URL.
				
				String getRequestURI = request.getRequestURI();
				
				String getQueryString = request.getQueryString();
				
				String newURL = getRequestURI + "?" + WebConstants.PARAMETER_PROJECT_ID + "=" + projectId + "&" + getQueryString;

				if ( log.isInfoEnabled() ) {
					
					log.info( "Redirecting to new URL to add '" + WebConstants.PARAMETER_PROJECT_ID + "=" + projectId + "' to query string.  new URL: " + newURL );
				}
				
				response.sendRedirect( newURL );
				
				return null;
			}
			
			
			

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



			


			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			for( int searchId : form.getSearchIds() ) {
				
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
			}


			


			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );


			// generate file name
			String filename = "xlinks-peptides-search-";
			filename += StringUtils.join( form.getSearchIds(), '-' );

			DateTime dt = new DateTime();
			DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");
			filename += "-" + fmt.print( dt );

			filename += ".txt";

			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);

			

			OutputStreamWriter writer = null;
			
			try {


				ServletOutputStream out = response.getOutputStream();

				BufferedOutputStream bos = new BufferedOutputStream(out);

				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );




				writer.write( "TYPE\tPEPTIDE 1\tPOSITION\tMODS\tPEPTIDE 2\tPOSITION\tMODS\tPROTEIN 1\tPROTEIN 2\tBEST PSM Q-VALUE\tNUM PSMS\n" );


				List<String> linkTypes = new ArrayList<>();
				
				for ( String linkType : form.getLinkType() ) {

					linkTypes.add( linkType );
				}

				double psmQValueCutoff = form.getPsmQValueCutoff();		
				double peptideQValueCutoff = form.getPeptideQValueCutoff();	

				
				///////////////////////////////////////////
				
				
				List<Double> modMassDistinctForSearchesList = 
						SearchModMassDistinctSearcher.getInstance().getDistinctDynamicModMassesForSearchId( searchIds );
				
				List<String> modMassStringsList = new ArrayList<>( modMassDistinctForSearchesList.size() );
				
				for ( Double modMass : modMassDistinctForSearchesList ) {
					
					String modMassAsString = modMass.toString();
					modMassStringsList.add( modMassAsString );
				}
				
				
				request.setAttribute( "modMassFilterList", modMassStringsList );
				
				
				
				
				String[] formModMassSelections = form.getModMassFilter();
				

				String[] modMassSelections = formModMassSelections;
				
				if ( formModMassSelections == null ) {
					
					//  Page loaded from link on different page so 
					//   populate formModMassSelections with all values so all check boxes will be checked.
					
					String[] newFormModMassSelections = new String[ modMassStringsList.size() + 1 ]; 

					newFormModMassSelections[ 0 ] = DynamicModificationsSelectionConstants.NO_DYNAMIC_MODIFICATIONS_SELECTION_ITEM;
					
					int index = 1;
					for ( String modMassString : modMassStringsList ) {
						
						newFormModMassSelections[ index ] = modMassString;
						index++;
					}

					form.setModMassFilter( newFormModMassSelections );

					
					modMassSelections = null; // for SQL query so not filter on mod mass
					
				} else {
				

					//  If all values are checked, set mod mass selector to null to not filter on mod mass 

					boolean allFormModMassSelectionsChecked = true;

					for ( String modMassString : modMassStringsList ) {

						boolean modMassStringChecked = false;

						for ( String formModMassSelection : formModMassSelections ) {

							if ( modMassString.equals( formModMassSelection ) ) {
								modMassStringChecked = true;
								break;
							}
						}

						if ( ! modMassStringChecked ) {

							allFormModMassSelectionsChecked = false;
							break;
						}
					}

					for ( String formModMassSelection : formModMassSelections ) {

						boolean modMassStringChecked = false;
						
						if ( DynamicModificationsSelectionConstants.NO_DYNAMIC_MODIFICATIONS_SELECTION_ITEM.equals( formModMassSelection ) ) {
							modMassStringChecked = true;
							break;
						}
						
						if ( ! modMassStringChecked ) {

							allFormModMassSelectionsChecked = false;
							break;
						}
					}



					if ( allFormModMassSelectionsChecked ) {

						modMassSelections = null; // for SQL query so not filter on mod mass
					}
				}			
				

				List<WebMergedReportedPeptide> links = 
						PeptideMergedWebPageSearcher.getInstance().search( searches, psmQValueCutoff, peptideQValueCutoff, linkTypes, modMassSelections );

				for( WebMergedReportedPeptide link : links ) {
					
					List<WebMergedProteinPosition> peptide1ProteinPositions = link.getPeptide1ProteinPositions();
					List<WebMergedProteinPosition> peptide2ProteinPositions = link.getPeptide2ProteinPositions();
					
					String peptide1ProteinPositionsString = XLinkWebAppUtils.getPeptideProteinPositionsString( peptide1ProteinPositions );
					String peptide2ProteinPositionsString = XLinkWebAppUtils.getPeptideProteinPositionsString( peptide2ProteinPositions );

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
					if ( link.getBestPeptideQValue() != null ) {
						writer.write( Double.toString( link.getBestPeptideQValue() ) );
					}
					writer.write( "\t" );
					writer.write( link.getNumPsms() + "\n" );

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
