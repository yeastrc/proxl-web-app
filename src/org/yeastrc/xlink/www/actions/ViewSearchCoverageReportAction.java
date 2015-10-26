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
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.SearchTaxonomySearcher;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.QueryCriteriaValueCountsFieldValuesConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.QueryCriteriaValueCountsDAO;
import org.yeastrc.xlink.www.forms.SearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.ProteinCoverageData;
import org.yeastrc.xlink.www.searcher.ProteinCoverageSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;

public class ViewSearchCoverageReportAction extends Action {
	
	private static final Logger log = Logger.getLogger(ViewSearchCoverageReportAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		
		try {

			// our form
			SearchViewProteinsForm form = (SearchViewProteinsForm)actionForm;
			request.setAttribute( "searchViewCrosslinkProteinForm", form );


			// Get the session first.  
			HttpSession session = request.getSession();

			
			int searchId = form.getSearchId();

			
			
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



			
			SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
			request.setAttribute( "search", search );

			GetSearchDetailsData.getInstance().getSearchDetailsData( search, request );
			
			
		
			
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );



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
			types.add( XLinkUtils.TYPE_DIMER );
			types.add( XLinkUtils.TYPE_MONOLINK );
			types.add( XLinkUtils.TYPE_UNLINKED );

			Collection<SearchProtein> prProteins = SearchProteinSearcher.getInstance().getProteinsWithLinkType(search, types, psmQValueCutoff, peptideQValueCutoff);
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



			// Get the protein coverage report data
			ProteinCoverageSearcher pcs = new ProteinCoverageSearcher();

			pcs.setExcludedProteinIds( form.getExcludeProtein() );
			pcs.setExcludedTaxonomyIds( form.getExcludeTaxonomy() );
			pcs.setFilterNonUniquePeptides( filterNonUniquePeptides );
			pcs.setFilterOnlyOnePSM( filterOnlyOnePSM );
			pcs.setFilterOnlyOnePeptide( filterOnlyOnePeptide );
			pcs.setPeptideQValueCutoff( peptideQValueCutoff );
			pcs.setPsmQValueCutoff( psmQValueCutoff );

			Collection<SearchDTO> searches = new ArrayList<SearchDTO>();
			searches.add( search );

			pcs.setSearches( searches );

			List<ProteinCoverageData> pcd = pcs.getProteinCoverageData();
			request.setAttribute( "proteinCoverageData", pcd );




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
