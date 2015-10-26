package org.yeastrc.xlink.www.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchPeptideSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinCrosslinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinLooplinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.SearchTaxonomySearcher;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.constants.QueryCriteriaValueCountsFieldValuesConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.QueryCriteriaValueCountsDAO;
import org.yeastrc.xlink.www.forms.SearchViewProteinsForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.XLinkWebAppUtils;
import org.yeastrc.xlink.www.webapp_timing.WebappTiming;

public class ViewSearchCrosslinkProteinsAction extends Action {
	
	private static final Logger log = Logger.getLogger(ViewSearchCrosslinkProteinsAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {


		WebappTiming webappTiming = null;
		
		if ( log.isDebugEnabled() ) {

			webappTiming = WebappTiming.getInstance( log );
			
			request.setAttribute( "webappTiming", webappTiming );
		}
		
		try {

			// our form
			SearchViewProteinsForm form = (SearchViewProteinsForm)actionForm;
			request.setAttribute( "searchViewCrosslinkProteinForm", form );

			int searchId = form.getSearchId();


			// Get the session first.  
			HttpSession session = request.getSession();

			

			
			
			//   Get the project id for this search
			
			Collection<Integer> searchIds = new HashSet<>();
			
			searchIds.add( searchId );
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIds );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				
				String msg = "No project ids for search id: " + searchId;
				
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


			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After Auth" );
			}
			


			SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
			request.setAttribute( "search", search );

			GetSearchDetailsData.getInstance().getSearchDetailsData( search, request );
			
			
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );

			
			boolean showStructureLink = true;
			
			if ( authAccessLevel.isAssistantProjectOwnerAllowed()
					|| authAccessLevel.isAssistantProjectOwnerIfProjectNotLockedAllowed() ) {
				
				
			} else {
				
				//  Public access user:
				
				showStructureLink = AnyPDBFilesForProjectId.getInstance().anyPDBFilesForProjectId( projectId );
			}
			
			request.setAttribute( WebConstants.REQUEST_SHOW_STRUCTURE_LINK, showStructureLink );
			



			double psmQValueCutoff = form.getPsmQValueCutoff();		
			double peptideQValueCutoff = form.getPeptideQValueCutoff();
			boolean filterNonUniquePeptides = form.isFilterNonUniquePeptides();
			boolean filterOnlyOnePSM = form.isFilterOnlyOnePSM();
			boolean filterOnlyOnePeptide = form.isFilterOnlyOnePeptide();

			
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PSM_Q_VALUE_FIELD_VALUE, Double.toString( psmQValueCutoff ) );
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PEPTIDE_Q_VALUE_FIELD_VALUE, Double.toString( peptideQValueCutoff ) );

			
			// all possible proteins included in this search for this type

			Collection<Integer> types = new HashSet<Integer>();
			types.add( XLinkUtils.TYPE_CROSSLINK );
			types.add( XLinkUtils.TYPE_LOOPLINK );

			Collection<SearchProtein> prProteins = SearchProteinSearcher.getInstance().getProteinsWithLinkType(search, types, psmQValueCutoff, peptideQValueCutoff);
			

			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After Protein Searcher:  SearchProteinSearcher.getInstance().getProteinsWithLinkType(...)" );
			}
			
			
			Collection<SearchProtein> prProteins2 = new HashSet<SearchProtein>();
			prProteins2.addAll( prProteins );

			// build a collection of protein IDs to include
			for( SearchProtein prp : prProteins2 ) {

				// did they request removal of certain taxonomy IDs?
				if( form.getExcludeTaxonomy() != null && form.getExcludeTaxonomy().length > 0 ) {
					for( int tid : form.getExcludeTaxonomy() ) {
						if( tid == prp.getNrProtein().getTaxonomyId() ) {
							prProteins.remove( prp );
							break;
						}
					}
				}
			}

			List<SearchProtein> sortedProteins = new ArrayList<SearchProtein>();
			sortedProteins.addAll( prProteins );
			Collections.sort( sortedProteins, new SortSearchProtein() );

			request.setAttribute( "proteins", sortedProteins );

			request.setAttribute( "psmQValueCutoff",  psmQValueCutoff );
			request.setAttribute( "peptideQValueCutoff",  peptideQValueCutoff );
			request.setAttribute( "queryString",  request.getQueryString() );
			request.setAttribute( "mergedQueryString", request.getQueryString().replaceAll( "searchId=", "searchIds=" ) );

			// build list of taxonomies to show in exclusion list
			request.setAttribute("taxonomies", SearchTaxonomySearcher.getInstance().getTaxonomies( search ) );

			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After Taxonomy Searcher:  SearchTaxonomySearcher.getInstance().getTaxonomies( search )" );
			}
			


			List<SearchProteinCrosslink> crosslinks = SearchProteinCrosslinkSearcher.getInstance().search( search, psmQValueCutoff, peptideQValueCutoff );
			

			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After Crosslink Searcher:  SearchProteinCrosslinkSearcher.getInstance().search( ... )" );
			}
			
			
			List<SearchProteinLooplink> looplinks = SearchProteinLooplinkSearcher.getInstance().search( search, psmQValueCutoff, peptideQValueCutoff );
			

			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After Looplink Searcher:  SearchProteinLooplinkSearcher.getInstance().search( ... )" );
			}
			

			// Filter out links if requested
			if( filterNonUniquePeptides || filterOnlyOnePSM || filterOnlyOnePeptide
					|| ( form.getExcludeTaxonomy() != null && form.getExcludeTaxonomy().length > 0 ) ||
					( form.getExcludeProtein() != null && form.getExcludeProtein().length > 0 ) ) {
				
				List<SearchProteinCrosslink> crosslinksCopy = new ArrayList<SearchProteinCrosslink>();
				crosslinksCopy.addAll( crosslinks );

				for( SearchProteinCrosslink link : crosslinksCopy ) {

					// did they request to removal of non unique peptides?
					if( filterNonUniquePeptides ) {
						if( link.getNumUniqueLinkedPeptides() < 1 ) {
							
							if ( ! crosslinks.remove( link ) ) {
								
								//  remove failed
//								int z = 0;
							}
							continue;
						}
					}
					

//
//					
//						link.getNumPsms() <= 1 WILL NOT WORK if more than one search since it is across all searches
//					
//					// did they request to removal of links with only one PSM?
					if( filterOnlyOnePSM ) {

						int psmCountForSearchId = link.getNumPsms();

						if ( psmCountForSearchId <= 1 ) {
							
							if ( ! crosslinks.remove( link ) ) {
								
								//  remove failed
//								int z = 0;
							}
							continue;
						}

					}
					
					// did they request to removal of links with only one Reported Peptide?
					if( filterOnlyOnePeptide ) {
						
						int peptideCountForSearchId = link.getNumLinkedPeptides();

						if ( peptideCountForSearchId <= 1 ) {
							
							if ( ! crosslinks.remove( link ) ) {
								
								//  remove failed
//								int z = 0;
							}
							continue;
						}

					}
					

					// did they request removal of certain taxonomy IDs?
					if( form.getExcludeTaxonomy() != null && form.getExcludeTaxonomy().length > 0 ) {
						for( int tid : form.getExcludeTaxonomy() ) {
							if( link.getProtein1().getNrProtein().getTaxonomyId() == tid ||
									link.getProtein2().getNrProtein().getTaxonomyId() == tid ) {
								crosslinks.remove( link );
								continue;
							}
						}
					}

					// did they request removal of certain protein IDs?
					if( form.getExcludeProtein() != null && form.getExcludeProtein().length > 0 ) {
						for( int pid : form.getExcludeProtein() ) {
							if( link.getProtein1().getNrProtein().getNrseqId() == pid ||
									link.getProtein2().getNrProtein().getNrseqId() == pid ) {
								crosslinks.remove( link );
								continue;
							}
						}
					}
				}

				List<SearchProteinLooplink> looplinksCopy = new ArrayList<SearchProteinLooplink>();
				looplinksCopy.addAll( looplinks );
				for( SearchProteinLooplink link : looplinksCopy ) {

					// did they request to removal of non unique peptides?
					if( filterNonUniquePeptides ) {
						if( link.getNumUniquePeptides() < 1 ) {
							looplinks.remove( link );
							continue;
						}
					}
					
					// did they request to removal of non unique peptides?
					if( filterNonUniquePeptides ) {
						if( link.getNumUniquePeptides() < 1 ) {
							looplinks.remove( link );
							continue;
						}
					}

					
//					
//						link.getNumPsms() <= 1 WILL NOT WORK if more than one search since it is across all searches
//					
//					// did they request to removal of links with only one PSM?
					if( filterOnlyOnePSM ) {

						int psmCountForSearchId = link.getNumPsms();

						if ( psmCountForSearchId <= 1 ) {
							looplinks.remove( link );
							continue;
						}

					}
					
					// did they request to removal of links with only one Reported Peptide?
					if( filterOnlyOnePeptide ) {
						
						int peptideCountForSearchId = link.getNumPeptides();

						if ( peptideCountForSearchId <= 1 ) {
							looplinks.remove( link );
							continue;
						}

					}

					// did they request removal of certain taxonomy IDs?
					if( form.getExcludeTaxonomy() != null && form.getExcludeTaxonomy().length > 0 ) {
						for( int tid : form.getExcludeTaxonomy() ) {
							if( link.getProtein().getNrProtein().getTaxonomyId() == tid ) {
								looplinks.remove( link );
								continue;
							}
						}
					}

					// did they request removal of certain protein IDs?
					if( form.getExcludeProtein() != null && form.getExcludeProtein().length > 0 ) {
						for( int pid : form.getExcludeProtein() ) {
							if( link.getProtein().getNrProtein().getNrseqId() == pid ) {
								looplinks.remove( link );
								continue;
							}
						}
					}
				}

			}


			//  For outermost top level table, show the field "Best Peptide Q-Value"
			boolean showTopLevelBestPeptideQValue = false;

			for ( SearchProteinCrosslink link : crosslinks ) {
				
				if ( link.getBestPeptideQValue() != null ) {
					
					showTopLevelBestPeptideQValue = true;
					break;
				}
			}


			request.setAttribute( "showTopLevelBestPeptideQValue", showTopLevelBestPeptideQValue );

			
			request.setAttribute( "numCrosslinks", crosslinks.size() );
			request.setAttribute( "numLooplinks", looplinks.size() );
			request.setAttribute( "numLinks", looplinks.size() + crosslinks.size() );
			request.setAttribute( "numDistinctLinks",  XLinkWebAppUtils.getNumUDRs( crosslinks, looplinks ) );

			request.setAttribute( "crosslinks", crosslinks );

			SearchPeptideSearcher prps = new SearchPeptideSearcher();
			prps.setPeptideQValueCutoff( peptideQValueCutoff );
			prps.setPsmQValueCutoff( psmQValueCutoff );
			prps.setSearch( search );

			request.setAttribute( "totalPeptides", prps.getNumDistinctPeptidesForSearch( XLinkUtils.TYPE_CROSSLINK ) );


			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After SearchPeptideSearcher Searcher:  SearchPeptideSearcher.getNumDistinctPeptidesForSearch( ... )" );
			}
			
			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "Before send to JSP" );
			}
			
			
			return mapping.findForward( "Success" );


		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}
	
    public class SortSearchProtein implements Comparator<SearchProtein> {
        public int compare(SearchProtein o1, SearchProtein o2) {
            try { return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()); }
            catch( Exception e ) { return 0; }
        }
    }
}
