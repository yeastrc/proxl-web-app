package org.yeastrc.xlink.www.actions;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.nav_links_image_structure.PopulateRequestDataForImageAndStructureNavLinks;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.IMergedSearchLink;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;

import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchTaxonomySearcher;
import org.yeastrc.xlink.www.actions.ProteinsMergedCommonPageDownload.ProteinsMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.Struts_Config_Parameter_Values_Constants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.GetCutoffPageDisplayRoot;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.SearchCount;
import org.yeastrc.xlink.www.objects.VennDiagramDataToJSON;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.GenerateVennDiagramDataToJSON;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.XLinkWebAppUtils;
import org.yeastrc.xlink.www.webapp_timing.WebappTiming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * Used for Merged Crosslinks and Merged Looplinks pages
 *
 */
public class ViewMergedSearchProteinsAction extends Action {

	private static final Logger log = Logger.getLogger(ViewMergedSearchProteinsAction.class);


	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute( ActionMapping mapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response )
					throws Exception {


		//  Detect which Struts action mapping was called by examining the value of the "parameter" attribute
		//     accessed by calling mapping.getParameter()

		String strutsActionMappingParameter = mapping.getParameter();



		WebappTiming webappTiming = null;

		if ( log.isDebugEnabled() ) {

			webappTiming = WebappTiming.getInstance( log );

			request.setAttribute( "webappTiming", webappTiming );
		}

		try {


			// our form
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm)actionForm;
			
			request.setAttribute( "mergedSearchViewCrosslinkProteinForm", form );

			request.setAttribute( "strutsActionForm", form );


			// Get the session first.  
			//			HttpSession session = request.getSession();




			int[] searchIds = form.getSearchIds();


			if ( searchIds.length == 0 ) {

				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}


			//   Get the project id for these searches

			Collection<Integer> searchIdsSet = new HashSet<Integer>( );

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


			///    Done Processing Auth Check and Auth Level


			//////////////////////////////


			//  Jackson JSON Mapper object for JSON deserialization and serialization

			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object





			request.setAttribute( "searchIds", searchIds );


			List<SearchDTO> searches = new ArrayList<SearchDTO>();

			Map<Integer, SearchDTO> searchesMapOnId = new HashMap<>();


			for( int searchId : form.getSearchIds() ) {

				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );

				if ( search == null ) {

					String msg = "Search id '" + searchId + "' not found in the database. User taken to home page.";

					log.warn( msg );

					//  Search not found, the data on the page they are requesting does not exist.
					//  The data on the user's previous page no longer reflects what is in the database.
					//  Take the user to the home page

					return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
				}

				searches.add( search );
				
				searchesMapOnId.put( searchId, search );
			}




			//  Populate request objects for Standard Header Display

			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );



			// sort our searches by ID
			Collections.sort( searches, new Comparator<SearchDTO>() {
				public int compare( SearchDTO r1, SearchDTO r2 ) {
					return r1.getId() - r2.getId();
				}
			});


			
			///////////////


			// build list of taxonomies to show in exclusion list
			request.setAttribute("taxonomies", SearchTaxonomySearcher.getInstance().getTaxonomies( searches ) );

			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After Taxonomy Searcher:  SearchTaxonomySearcher.getInstance().getTaxonomies( search )" );
			}
			
			

			//			CutoffPageDisplayRoot cutoffPageDisplayRoot =

			//  Set attribute in request object for Page Display of cutoffs
			GetCutoffPageDisplayRoot.getInstance().getCutoffPageDisplayRoot( searchIdsSet, request );


			

			
			ProteinsMergedCommonPageDownload.ForCrosslinksOrLooplinkOrBoth forCrosslinksOrLooplinkOrBoth = null;

			if ( Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__CROSSLINK.equals( strutsActionMappingParameter ) ) {

				forCrosslinksOrLooplinkOrBoth = ProteinsMergedCommonPageDownload.ForCrosslinksOrLooplinkOrBoth.CROSSLINKS;

			} else if ( Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__LOOPLINK.equals( strutsActionMappingParameter ) ) {

				forCrosslinksOrLooplinkOrBoth = ProteinsMergedCommonPageDownload.ForCrosslinksOrLooplinkOrBoth.LOOPLINKS;
				
			} else {
				
				String msg = "Struts Config Parameter value is not valid: " + strutsActionMappingParameter;
				log.error( msg );
				throw new Exception( msg );
			}


			
			ProteinsMergedCommonPageDownloadResult proteinsMergedCommonPageDownloadResult =
					ProteinsMergedCommonPageDownload.getInstance()
					.getCrosslinksAndLooplinkWrapped(
							form,
							forCrosslinksOrLooplinkOrBoth,
							searchIdsListDeduppedSorted,
							searches,
							searchesMapOnId );


			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "ProteinsMergedCommonPageDownload.getCrosslinksAndLooplinkWrapped()" );
			}
			

			request.setAttribute( "peptidePsmAnnotationNameDescListsForEachSearch", proteinsMergedCommonPageDownloadResult.getPeptidePsmAnnotationNameDescListsForEachSearch() );


			request.setAttribute( "looplinks", proteinsMergedCommonPageDownloadResult.getWrappedLoopLinks() );
			
			request.setAttribute( "crosslinks", proteinsMergedCommonPageDownloadResult.getWrappedCrossLinks() );
			

			List<MergedSearchProteinCrosslink> crosslinks = proteinsMergedCommonPageDownloadResult.getCrosslinks();
			List<MergedSearchProteinLooplink> looplinks = proteinsMergedCommonPageDownloadResult.getLooplinks();

			

			/////////////////////

			// all possible proteins for Crosslinks and Looplinks across all searches (for "Exclude Protein" list on web page)

			Map<Integer,Set<Integer>> allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds = 
					proteinsMergedCommonPageDownloadResult.getAllProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds();

		


			//////////////////////////////
			
			//////    Process list of all proteins for Crosslinks and Looplinks (before filtering)
			
			/////                 List used for "Exclude Protein" list on web page
			
			
			List<MergedSearchProtein> allProteinsForCrosslinksAndLooplinksUnfilteredList = new ArrayList<>( allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.size() );

			for ( Map.Entry<Integer, Set<Integer>> entry : allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.entrySet() ) {

				Integer proteinId = entry.getKey();
				Set<Integer> searchIdsForProtein = entry.getValue();
				
				List<SearchDTO> searchesForProtein = new ArrayList<>( searchIdsForProtein.size() );
				
				for ( Integer searchIdForProtein : searchIdsForProtein ) {
					
					SearchDTO searchForProtein = searchesMapOnId.get( searchIdForProtein );
					
					if ( searchForProtein == null ) {
						
						String msg = "Processing searchIdsForProtein, no search found in searchesMapOnId for searchIdForProtein : " + searchIdForProtein;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					
					searchesForProtein.add(searchForProtein);
				}
			
				NRProteinDTO nrProteinDTO = new NRProteinDTO();
				nrProteinDTO.setNrseqId( proteinId );

				MergedSearchProtein mergedSearchProtein = new MergedSearchProtein( searchesForProtein, nrProteinDTO );
				
				int mergedSearchProteinTaxonomyId = mergedSearchProtein.getNrProtein().getTaxonomyId(); 

				if ( proteinsMergedCommonPageDownloadResult.getExcludeTaxonomy_Ids_Set_UserInput().contains( mergedSearchProteinTaxonomyId ) ) {
					
					//////////  Taxonomy Id in list of excluded taxonomy ids so drop the record
					
					continue;  //   EARLY Continue
				}
				
				allProteinsForCrosslinksAndLooplinksUnfilteredList.add( mergedSearchProtein );
			}
		
			
			Collections.sort( allProteinsForCrosslinksAndLooplinksUnfilteredList, new SortMergedSearchProtein() );

			request.setAttribute( "allProteinsForCrosslinksAndLooplinksUnfilteredList", allProteinsForCrosslinksAndLooplinksUnfilteredList );
			


			request.setAttribute( "numCrosslinks", crosslinks.size() );
			
			request.setAttribute( "numLooplinks", looplinks.size() );
			
			request.setAttribute( "numLinks", looplinks.size() + crosslinks.size() );




			request.setAttribute( "queryString", request.getQueryString() );
			request.setAttribute( "searches", searches );

			GetSearchDetailsData.getInstance().getSearchDetailsData( searches, request );

			request.setAttribute( "numDistinctLinks",  XLinkWebAppUtils.getNumUDRs( crosslinks, looplinks ) );



			///////////////////////////

			//   These next parts ... as specified by whether crosslinks or looplinks is copied into linksCrosslinksOrLoopLinks

			List<? extends IMergedSearchLink> linksCrosslinksOrLoopLinks = null;



			// For  Struts config action mapping:     parameter="crosslink"

			if ( Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__CROSSLINK.equals( strutsActionMappingParameter ) ) {

				linksCrosslinksOrLoopLinks = crosslinks;

			} else 
				//  Struts config action mapping:     parameter="looplink"

				if ( Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__LOOPLINK.equals( strutsActionMappingParameter ) ) {

					linksCrosslinksOrLoopLinks = looplinks;

				} else {

					String msg = "Value for Struts Action 'parameter' attribute is not recognized. Value is: " + strutsActionMappingParameter;
					log.error( msg );

					throw new Exception(msg);
				}






			VennDiagramDataToJSON vennDiagramDataToJSON =
					GenerateVennDiagramDataToJSON.createVennDiagramDataToJSON( linksCrosslinksOrLoopLinks, searches );


			if ( vennDiagramDataToJSON != null ) {

				String vennDiagramDataToJSONString = jacksonJSON_Mapper.writeValueAsString( vennDiagramDataToJSON );

				request.setAttribute( "vennDiagramDataToJSON", vennDiagramDataToJSONString );
			}


			//////////////////////////////////////////////

			// get the counts for the number of links for each search, save to map, save to request


			//  Temp Map searchCounts to use in next step

			Map<Integer, MutableInt> searchCounts = new TreeMap<Integer, MutableInt>();

			//  Populate Temp Map  searchCounts

			for( IMergedSearchLink link : linksCrosslinksOrLoopLinks ) {

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

			//   Take values in Temp Map searchCounts and put them in a list in Search Id order with Search Data

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


			// get the counts for the number of links for each search, save to map, save to request

			//////////////////////////////////////////////



			/////////////////////

			//  clear out form so value doesn't go back on the page in the form

			form.setQueryJSON( "" );


			/////////////////////

			////  Put Updated queryJSON on the page

			{

				try {

					String queryJSONToPage = jacksonJSON_Mapper.writeValueAsString( proteinsMergedCommonPageDownloadResult.getProteinQueryJSONRoot() );

					//  Set queryJSON in request attribute to put on page outside of form

					request.setAttribute( "queryJSONToForm", queryJSONToPage );




				} catch ( JsonProcessingException e ) {

					String msg = "Failed to write as JSON 'queryJSONToForm', JsonProcessingException.   queryString" +  request.getQueryString();
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );

				} catch ( Exception e ) {

					String msg = "Failed to write as JSON 'queryJSONToForm', Exception.   queryString" +  request.getQueryString();
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				}

			}


			//  Create data for Links for Image and Structure pages and put in request

			PopulateRequestDataForImageAndStructureNavLinks.getInstance()
			.populateRequestDataForImageAndStructureNavLinksForProtein( proteinsMergedCommonPageDownloadResult.getProteinQueryJSONRoot(), projectId, authAccessLevel, form, request );



			if ( webappTiming != null ) {

				webappTiming.markPoint( "Before send to JSP" );
			}


			return mapping.findForward( "Success" );


		} catch ( ProxlWebappDataException e ) {

			String msg = "Exception processing request data";

			log.error( msg, e );

			return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );


		} catch ( Exception e ) {

			String msg = "Exception caught: " + e.toString();

			log.error( msg, e );

			throw e;
		}
	}



	/////////////////////////////////////////////////


	private class SortMergedSearchProtein implements Comparator<MergedSearchProtein> {

		public int compare(MergedSearchProtein o1, MergedSearchProtein o2) {
			try { 
				return o1.getNameLowerCase().compareTo(o2.getNameLowerCase()); 
			}
			catch( Exception e ) { 
				return 0; 
			}
		}
	}


}
