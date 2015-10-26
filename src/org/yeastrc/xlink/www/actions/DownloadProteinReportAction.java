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
import javax.servlet.http.HttpSession;

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
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.ProteinCoverageData;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ProteinCoverageSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

public class DownloadProteinReportAction extends Action {

	private static final Logger log = Logger.getLogger(DownloadProteinReportAction.class);
			
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {


		try {

			// our form
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm)actionForm;
			request.setAttribute( "mergedSearchViewCrosslinkProteinForm", form );



			// Get the session first.  
			HttpSession session = request.getSession();

			//   Get the project id for this search

			int[] searchIdsFromForm = form.getSearchIds();
			
			
			if ( searchIdsFromForm.length == 0 ) {
				
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}

			
			//   Get the project id for these searches
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			
			for ( int searchId : searchIdsFromForm ) {

				searchIdsCollection.add( searchId );
			}
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				
				String msg = "No project ids for search ids: ";
				for ( int searchId : searchIdsFromForm ) {

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



			


			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			List<Integer> searchIds = new ArrayList<Integer>();
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
				searchIds.add( searchId );
			}




			// generate file name
			String filename = "protein-coverage-report-";
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



				double psmQValueCutoff = form.getPsmQValueCutoff();		
				double peptideQValueCutoff = form.getPeptideQValueCutoff();
				boolean filterNonUniquePeptides = form.isFilterNonUniquePeptides();
				boolean filterOnlyOnePSM = form.isFilterOnlyOnePSM();
				boolean filterOnlyOnePeptide = form.isFilterOnlyOnePeptide();



				// Get the protein coverage report data
				ProteinCoverageSearcher pcs = new ProteinCoverageSearcher();

				pcs.setExcludedProteinIds( form.getExcludeProtein() );
				pcs.setExcludedTaxonomyIds( form.getExcludeTaxonomy() );
				pcs.setFilterNonUniquePeptides( filterNonUniquePeptides );
				pcs.setFilterOnlyOnePSM( filterOnlyOnePSM );
				pcs.setFilterOnlyOnePeptide( filterOnlyOnePeptide );
				pcs.setPeptideQValueCutoff( peptideQValueCutoff );
				pcs.setPsmQValueCutoff( psmQValueCutoff );
				pcs.setSearches( searches );

				List<ProteinCoverageData> pcdlist = pcs.getProteinCoverageData();

				writer.write( "# Protein Coverage Report For:\n");
				writer.write( "# Search(s): " + StringUtils.join( searchIds, ", " ) + "\n" );
				writer.write( "# PSM Q-value: " + psmQValueCutoff + "\n" );
				writer.write( "# Peptide Q-value: " +peptideQValueCutoff + "\n" );
				writer.write( "# Filter out non-unique peptides: " + filterNonUniquePeptides + "\n" );
				writer.write(
						"PROTEIN\t# RESIDUES\tSEQUENCE COVERAGE\t#LINKABLE RESIDUES"
						+ "\tLinkable Residues Covered\tLinkable Residues Coverage\t"
								+ "M+L+C RESIDUES\tM+L+C SEQUENCE COVERAGE\t"
								+ "L+C RESIDUES\tL+C SEQUENCE COVERAGE\t"
								+ "M RESIDUES\tM SEQUENCE COVERAGE\t"
								+ "L RESIDUES\tL SEQUENCE COVERAGE\t"
								+ "C RESIDUES\tC SEQUENCE COVERAGE\n"
						);


				for( ProteinCoverageData pcd : pcdlist ) {
					writer.write( pcd.getName() + "\t" );
					writer.write( pcd.getNumResidues() + "\t" );
					writer.write( pcd.getSequenceCoverage() + "\t" );
					writer.write( pcd.getNumLinkableResidues() + "\t" );
					
					writer.write( pcd.getNumLinkableResiduesCovered() + "\t" );
					writer.write( pcd.getLinkableResiduesCoverageFmt() + "\t" );

					writer.write( pcd.getNumMLCResidues() + "\t" );
					writer.write( pcd.getMLCSequenceCoverage() + "\t" );

					writer.write( pcd.getNumLCResidues() + "\t" );
					writer.write( pcd.getLCSequenceCoverage() + "\t" );


					writer.write( pcd.getMonolinkedResidues() + "\t" );
					writer.write( pcd.getMSequenceCoverage() + "\t" );

					writer.write( pcd.getLooplinkedResidues() + "\t" );
					writer.write( pcd.getLSequenceCoverage() + "\t" );

					writer.write( pcd.getCrosslinkedResidues() + "\t" );
					writer.write( pcd.getCSequenceCoverage() );

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
