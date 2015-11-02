package org.yeastrc.xlink.www.actions;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.IMergedSearchLink;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.searcher.MergedSearchProteinCrosslinkSearcher;
import org.yeastrc.xlink.www.searcher.MergedSearchProteinLooplinkSearcher;
import org.yeastrc.xlink.www.searcher.MergedSearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchTaxonomySearcher;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.QueryCriteriaValueCountsFieldValuesConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.QueryCriteriaValueCountsDAO;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.objects.SearchBooleanWrapper;
import org.yeastrc.xlink.www.objects.SearchCount;
import org.yeastrc.xlink.www.objects.VennDiagramDataToJSON;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.GenerateVennDiagramDataToJSON;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.XLinkWebAppUtils;
import org.yeastrc.xlink.www.webapp_timing.WebappTiming;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ViewMergedSearchLooplinkProteinsAction extends Action {
	
	private static final Logger log = Logger.getLogger(ViewMergedSearchLooplinkProteinsAction.class);

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
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm)actionForm;
			request.setAttribute( "mergedSearchViewCrosslinkProteinForm", form );



			// Get the session first.  
//			HttpSession session = request.getSession();

			


			int[] searchIds = form.getSearchIds();
			
			
			if ( searchIds.length == 0 ) {
				
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}

			
			//   Get the project id for this search
			
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
			

			request.setAttribute( "searchIds", searchIds );
			

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



			
			boolean showStructureLink = true;
			
			if ( authAccessLevel.isAssistantProjectOwnerAllowed()
					|| authAccessLevel.isAssistantProjectOwnerIfProjectNotLockedAllowed() ) {
				
				
			} else {
				
				//  Public access user:
				
				showStructureLink = AnyPDBFilesForProjectId.getInstance().anyPDBFilesForProjectId( projectId );
			}
			
			request.setAttribute( WebConstants.REQUEST_SHOW_STRUCTURE_LINK, showStructureLink );
			


			// sort our searches by ID
			Collections.sort( searches, new Comparator<SearchDTO>() {
				public int compare( SearchDTO r1, SearchDTO r2 ) {
					return r1.getId() - r2.getId();
				}
			});

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
			//Collection<MergedSearchProtein> prProteins = MergedSearchProteinSearcher.getInstance().getProteinsWithLinkType(searches, XLinkUtils.TYPE_LOOPLINK, psmQValueCutoff, peptideQValueCutoff);

			Collection<Integer> types = new HashSet<Integer>();
			types.add( XLinkUtils.TYPE_CROSSLINK );
			types.add( XLinkUtils.TYPE_LOOPLINK );

			Collection<MergedSearchProtein> prProteins = MergedSearchProteinSearcher.getInstance().getProteinsWithLinkType(searches, types, psmQValueCutoff, peptideQValueCutoff);
			

			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After Get Proteins Searcher:  MergedSearchProteinSearcher.getInstance().getProteinsWithLinkType( ... )" );
			}
			
			Collection<MergedSearchProtein> prProteins2 = new HashSet<MergedSearchProtein>();
			prProteins2.addAll( prProteins );

			// build a collection of protein IDs to include
			for( MergedSearchProtein prp : prProteins2 ) {

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

			List<MergedSearchProtein> sortedProteins = new ArrayList<MergedSearchProtein>();
			sortedProteins.addAll( prProteins );
			Collections.sort( sortedProteins, new SortMergedSearchProtein() );

			request.setAttribute( "proteins", sortedProteins );

			request.setAttribute( "psmQValueCutoff",  psmQValueCutoff );
			request.setAttribute( "peptideQValueCutoff",  peptideQValueCutoff );

			// build list of taxonomies to show in exclusion list
			request.setAttribute("taxonomies", SearchTaxonomySearcher.getInstance().getTaxonomies( searches ) );


			List<MergedSearchProteinLooplink> looplinks = MergedSearchProteinLooplinkSearcher.getInstance().search( searches, psmQValueCutoff, peptideQValueCutoff );

			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After Looplink Searcher:  MergedSearchProteinLooplinkSearcher.getInstance().search( ... )" );
			}

			List<MergedSearchProteinCrosslink> crosslinks = MergedSearchProteinCrosslinkSearcher.getInstance().search( searches, psmQValueCutoff, peptideQValueCutoff );
			
			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After Crosslink Searcher:  MergedSearchProteinCrosslinkSearcher.getInstance().search( ... )" );
			}

			// Filter out links if requested
			if( filterNonUniquePeptides || filterOnlyOnePSM || filterOnlyOnePeptide
					|| ( form.getExcludeTaxonomy() != null && form.getExcludeTaxonomy().length > 0 )
					|| ( form.getExcludeProtein() != null && form.getExcludeProtein().length > 0 ) ) {
				
				
				List<MergedSearchProteinLooplink> looplinksCopy = new ArrayList<MergedSearchProteinLooplink>();
				looplinksCopy.addAll( looplinks );

				for( MergedSearchProteinLooplink link : looplinksCopy ) {

					// did they request to removal of non unique peptides?
					if( filterNonUniquePeptides ) {
						if( link.getNumUniquePeptides() < 1 ) {
							looplinks.remove( link );
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
							looplinks.remove( link );
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


				List<MergedSearchProteinCrosslink> crosslinksCopy = new ArrayList<MergedSearchProteinCrosslink>();
				crosslinksCopy.addAll( crosslinks );
				for( MergedSearchProteinCrosslink link : crosslinksCopy ) {

					// did they request to removal of non unique peptides?
					if( filterNonUniquePeptides ) {
						if( link.getNumUniqueLinkedPeptides() < 1 ) {
							crosslinks.remove( link );
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
							crosslinks.remove( link );
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
							crosslinks.remove( link );
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

			}

			//  For outermost top level table, show the field "Best Peptide Q-Value"
			boolean showTopLevelBestPeptideQValue = false;
			

			List<MergedSearchProteinLooplinkWrapper> wrappedLinks = new ArrayList<MergedSearchProteinLooplinkWrapper>( looplinks.size() );
			for( MergedSearchProteinLooplink link : looplinks ) {
				MergedSearchProteinLooplinkWrapper wrapper = new MergedSearchProteinLooplinkWrapper();

				List<SearchBooleanWrapper> booleanWrapper = new ArrayList<SearchBooleanWrapper>( searches.size() );
				for( SearchDTO search : searches ) {
					if( link.getSearches().contains( search ) ) {
						booleanWrapper.add( new SearchBooleanWrapper( search, true ) );
					} else {
						booleanWrapper.add( new SearchBooleanWrapper( search, false ) );
					}					
				}
				

				/// Check if any top level link "Best Peptide Q-Value" is not null
				
				if ( link.getBestPeptideQValue() != null ) {
					
					showTopLevelBestPeptideQValue = true;
				}
				

				wrapper.setMergedSearchPeptideLooplink( link );
				wrapper.setSearchContainsLooplink( booleanWrapper );

				wrappedLinks.add( wrapper );
			}
			

			request.setAttribute( "showTopLevelBestPeptideQValue", showTopLevelBestPeptideQValue );


			request.setAttribute( "numCrosslinks", crosslinks.size() );
			request.setAttribute( "numLooplinks", looplinks.size() );
			request.setAttribute( "numLinks", looplinks.size() + crosslinks.size() );
			request.setAttribute( "looplinks", wrappedLinks );
			request.setAttribute( "queryString", request.getQueryString() );
			request.setAttribute( "searches", searches );
			
			GetSearchDetailsData.getInstance().getSearchDetailsData( searches, request );
	
			request.setAttribute( "numDistinctLinks",  XLinkWebAppUtils.getNumUDRs( crosslinks, looplinks ) );


			// build the JSON data structure for searches
			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
			ByteArrayOutputStream responseJSONBAOS = new ByteArrayOutputStream( 100000 );
			mapper.writeValue( responseJSONBAOS, searches ); // where first param can be File, OutputStream or Writer
			request.setAttribute( "searchJSON", responseJSONBAOS.toString() );


			VennDiagramDataToJSON vennDiagramDataToJSON =
					GenerateVennDiagramDataToJSON.createVennDiagramDataToJSON( looplinks, searches );

			if ( vennDiagramDataToJSON != null ) {
				
				ByteArrayOutputStream vennDiagramDataJSONBAOS = new ByteArrayOutputStream( 100000 );
				mapper.writeValue( vennDiagramDataJSONBAOS, vennDiagramDataToJSON ); // where first param can be File, OutputStream or Writer
				request.setAttribute( "vennDiagramDataToJSON", vennDiagramDataJSONBAOS.toString() );
			}

			
			// get the counts for the number of links for each search, save to map, save to request
			Map<Integer, MutableInt> searchCounts = new TreeMap<Integer, MutableInt>();
			
			for( IMergedSearchLink link : looplinks ) {
				
				for( SearchDTO search : link.getSearches() ) {
					
					Integer searchId = search.getId();
					
					MutableInt searchCount = searchCounts.get( searchId );
					
					if ( searchCount == null ) {
						
						searchCount = new MutableInt( 1 );
						searchCounts.put( searchId, searchCount );
						
					} else {
						
						searchCount.increment();
					}
				}
			}
			
			List<SearchCount> SearchCountList = new ArrayList<>();
			
			for ( SearchDTO search : searches  ) {
				
				int searchId = search.getId();
				
				MutableInt searchCountMapValue = searchCounts.get( searchId );
				
				SearchCount searchCount = new SearchCount();
				SearchCountList.add(searchCount);
				
				searchCount.setSearchId( searchId );
				
				if ( searchCountMapValue != null ) {
					searchCount.setCount( searchCountMapValue.intValue() );
				} else {
					
					searchCount.setCount( 0 );
				}
				
			}
			
			
			
			request.setAttribute( "searchCounts", SearchCountList );
			
			
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
	
    public class SortMergedSearchProtein implements Comparator<MergedSearchProtein> {
        public int compare(MergedSearchProtein o1, MergedSearchProtein o2) {
            try { return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()); }
            catch( Exception e ) { return 0; }
        }
    }
}
