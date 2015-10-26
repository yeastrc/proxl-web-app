package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.searcher.MergedSearchProteinCrosslinkSearcher;
import org.yeastrc.xlink.www.searcher.MergedSearchProteinLooplinkSearcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;


public class DownloadMergedSearchProteinsAction extends Action {
	
	private static final Logger log = Logger.getLogger(DownloadMergedSearchProteinsAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		try {
			
			// our form
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm)actionForm;


			// Get the session first.  
			HttpSession session = request.getSession();


			
			//   Get the project id for these searches
			
			
			int[] searchIdsFromForm = form.getSearchIds();
			
			
			if ( searchIdsFromForm.length == 0 ) {
				
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}

			
			//   Get the project id for this search
			
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
			
			for( int searchId : searchIdsFromForm ) {
				
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
			String filename = "xlinks-proteins-search-";
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

				List<MergedSearchProteinCrosslink> links = MergedSearchProteinCrosslinkSearcher.getInstance().search( searches, psmQValueCutoff, peptideQValueCutoff );

				// Filter out links if requested
				if( filterNonUniquePeptides || filterOnlyOnePSM || filterOnlyOnePeptide
						|| ( form.getExcludeTaxonomy() != null && form.getExcludeTaxonomy().length > 0 )  
						|| ( form.getExcludeProtein() != null && form.getExcludeProtein().length > 0 ) ) {
					
					
					List<MergedSearchProteinCrosslink> linksCopy = new ArrayList<MergedSearchProteinCrosslink>();
					linksCopy.addAll( links );

					for( MergedSearchProteinCrosslink link : linksCopy ) {

						// did they request to removal of non unique peptides?
						if( filterNonUniquePeptides ) {
							if( link.getNumUniqueLinkedPeptides() < 1 ) {
								links.remove( link );
								continue;
							}
						}

//
//					
//						link.getNumPsms() <= 1 WILL NOT WORK if more than one search since it is across all searches
//					
//					// did they request to removal of links with only one PSM?
						if( filterOnlyOnePSM ) {

							boolean foundSearchWithMoreThanOnePSM = false;

							Map<SearchDTO, SearchProteinCrosslink> searchCrosslinks = link.getSearchProteinCrosslinks();

							for ( Map.Entry<SearchDTO, SearchProteinCrosslink> searchEntry : searchCrosslinks.entrySet() ) {

								SearchProteinCrosslink searchProteinCrosslink = searchEntry.getValue();

								int psmCountForSearchId = searchProteinCrosslink.getNumPsms();

								if ( psmCountForSearchId > 1 ) {

									foundSearchWithMoreThanOnePSM = true;
									break;
								}
							}

							if (  ! foundSearchWithMoreThanOnePSM ) {
								links.remove( link );
								continue;
							}

						}

						// did they request to removal of links with only one Reported Peptide?
						if( filterOnlyOnePeptide ) {

							boolean foundSearchWithMoreThanOneReportedPeptide = false;

							Map<SearchDTO, SearchProteinCrosslink> searchCrosslinks = link.getSearchProteinCrosslinks();

							for ( Map.Entry<SearchDTO, SearchProteinCrosslink> searchEntry : searchCrosslinks.entrySet() ) {

								SearchProteinCrosslink searchProteinCrosslink = searchEntry.getValue();

								int peptideCountForSearchId = searchProteinCrosslink.getNumLinkedPeptides();

								if ( peptideCountForSearchId > 1 ) {

									foundSearchWithMoreThanOneReportedPeptide = true;
									break;
								}
							}

							if (  ! foundSearchWithMoreThanOneReportedPeptide ) {
								links.remove( link );
								continue;
							}
						}
						
						
						// did they request removal of certain taxonomy IDs?
						if( form.getExcludeTaxonomy() != null && form.getExcludeTaxonomy().length > 0 ) {
							for( int tid : form.getExcludeTaxonomy() ) {
								if( link.getProtein1().getNrProtein().getTaxonomyId() == tid ||
										link.getProtein2().getNrProtein().getTaxonomyId() == tid ) {
									links.remove( link );
									continue;
								}
							}
						}

						// did they request removal of certain protein IDs?
						if( form.getExcludeProtein() != null && form.getExcludeProtein().length > 0 ) {
							for( int pid : form.getExcludeProtein() ) {
								if( link.getProtein1().getNrProtein().getNrseqId() == pid ||
										link.getProtein2().getNrProtein().getNrseqId() == pid ) {
									links.remove( link );
									continue;
								}
							}
						}
					}
				}
				
				




				List<MergedSearchProteinLooplink> llinks = MergedSearchProteinLooplinkSearcher.getInstance().search( searches, psmQValueCutoff, peptideQValueCutoff );

				// Filter out links if requested
				if( filterNonUniquePeptides || filterOnlyOnePSM || filterOnlyOnePeptide
						|| ( form.getExcludeTaxonomy() != null && form.getExcludeTaxonomy().length > 0 ) 
						|| ( form.getExcludeProtein() != null && form.getExcludeProtein().length > 0 ) ) {
					
					
					List<MergedSearchProteinLooplink> linksCopy = new ArrayList<MergedSearchProteinLooplink>();
					linksCopy.addAll( llinks );

					for( MergedSearchProteinLooplink link : linksCopy ) {

						// did they request to removal of non unique peptides?
						if( filterNonUniquePeptides ) {
							if( link.getNumUniquePeptides() < 1 ) {
								llinks.remove( link );
								continue;
							}
						}


						
//							link.getNumPsms() <= 1 WILL NOT WORK if more than one search since it is across all searches
//						
//						// did they request to removal of links with only one PSM?
						if( filterOnlyOnePSM ) {
							
							boolean foundSearchWithMoreThanOnePSM = false;
							

							Map<SearchDTO, SearchProteinLooplink> searchLooplinks = link.getSearchProteinLooplinks();

							for ( Map.Entry<SearchDTO, SearchProteinLooplink> searchEntry : searchLooplinks.entrySet() ) {

								SearchProteinLooplink searchProteinLooplink = searchEntry.getValue();

								int psmCountForSearchId = searchProteinLooplink.getNumPsms();

								if ( psmCountForSearchId > 1 ) {

									foundSearchWithMoreThanOnePSM = true;
									break;
								}
							}
							
							if (  ! foundSearchWithMoreThanOnePSM ) {
								llinks.remove( link );
								continue;
							}

						}
						
						
						// did they request to removal of links with only one Reported Peptide?
						if( filterOnlyOnePeptide ) {
							
							boolean foundSearchWithMoreThanOneReportedPeptide = false;

							Map<SearchDTO, SearchProteinLooplink> searchLooplinks = link.getSearchProteinLooplinks();

							for ( Map.Entry<SearchDTO, SearchProteinLooplink> searchEntry : searchLooplinks.entrySet() ) {

								SearchProteinLooplink searchProteinLooplink = searchEntry.getValue();

								int peptideCountForSearchId = searchProteinLooplink.getNumPeptides();

								if ( peptideCountForSearchId > 1 ) {

									foundSearchWithMoreThanOneReportedPeptide = true;
									break;
								}
							}
							
							if (  ! foundSearchWithMoreThanOneReportedPeptide ) {
								llinks.remove( link );
								continue;
							}
						}
						
						
						// did they request removal of certain taxonomy IDs?
						if( form.getExcludeTaxonomy() != null && form.getExcludeTaxonomy().length > 0 ) {
							for( int tid : form.getExcludeTaxonomy() ) {
								if( link.getProtein().getNrProtein().getTaxonomyId() == tid ) {
									llinks.remove( link );
									continue;
								}
							}
						}

						// did they request removal of certain protein IDs?
						if( form.getExcludeProtein() != null && form.getExcludeProtein().length > 0 ) {
							for( int pid : form.getExcludeProtein() ) {
								if( link.getProtein().getNrProtein().getNrseqId() == pid ) {
									llinks.remove( link );
									continue;
								}
							}
						}
					}
				}
				
				

				writer.write( "SEARCH ID(S)\tTYPE\tPROTEIN 1\tPOSITION\tPROTEIN 2\tPOSITION\tNUM PSMS\tNUM PEPTIDES\tNUM UNIQUE PEPTIDES\tBEST PEPTIDE Q-VALUE\tBEST PSM Q-VALUE\n" );


				for( MergedSearchProteinCrosslink link : links ) {
					
					StringBuilder line = new StringBuilder();

					List<Integer> searchIds = new ArrayList<Integer>( link.getSearches().size() );
					for( SearchDTO r : link.getSearches() ) { searchIds.add( r.getId() ); }
					Collections.sort( searchIds );

					line.append( StringUtils.join( searchIds, "," ) );
					line.append( "\t" );
					line.append( "CROSSLINK\t" );
					line.append( link.getProtein1().getName() );
					line.append( "\t" );
					line.append( link.getProtein1Position() );
					line.append( "\t" );
					line.append( link.getProtein2().getName() );
					line.append( "\t" );
					line.append( link.getProtein2Position() );
					line.append( "\t" );
					line.append( link.getNumPsms() );
					line.append( "\t" );
					line.append( link.getNumLinkedPeptides() );
					line.append( "\t" );
					line.append( link.getNumUniqueLinkedPeptides() );
					line.append( "\t" );
					if ( link.getBestPeptideQValue() != null ) {
						line.append( link.getBestPeptideQValue() );
					}
					line.append( "\t" );
					line.append( link.getBestPSMQValue() );
					line.append( "\n" );

					writer.write( line.toString() );
				}





				for( MergedSearchProteinLooplink link : llinks ) {
					
					StringBuffer line = new StringBuffer();

					List<Integer> searchIds = new ArrayList<Integer>( link.getSearches().size() );
					for( SearchDTO r : link.getSearches() ) { searchIds.add( r.getId() ); }
					Collections.sort( searchIds );

					line.append( StringUtils.join( searchIds, "," ) );
					line.append( "\t" );
					line.append( "LOOPLINK" );
					line.append( "\t" );
					line.append( link.getProtein().getName() );
					line.append( "\t" );
					line.append( link.getProteinPosition1() );
					line.append( "\t" );
					line.append( link.getProtein().getName() );
					line.append( "\t" );
					line.append( link.getProteinPosition2() );
					line.append( "\t" );
					line.append( link.getNumPsms() );
					line.append( "\t" );
					line.append( link.getNumPeptides() );
					line.append( "\t" );
					line.append( link.getNumUniquePeptides() );
					line.append( "\t" );
					if ( link.getBestPeptideQValue() != null ) {
						line.append( link.getBestPeptideQValue() );
					}
					line.append( "\t" );
					line.append( link.getBestPSMQValue() );
					line.append( "\n" );

					writer.write( line.toString() );
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
