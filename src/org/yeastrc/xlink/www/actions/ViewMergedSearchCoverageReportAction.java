package org.yeastrc.xlink.www.actions;


import java.io.ByteArrayOutputStream;
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
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.searcher.MergedSearchProteinSearcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchTaxonomySearcher;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.QueryCriteriaValueCountsFieldValuesConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.QueryCriteriaValueCountsDAO;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.ProteinCoverageData;
import org.yeastrc.xlink.www.searcher.ProteinCoverageSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ViewMergedSearchCoverageReportAction extends Action {
	
	private static final Logger log = Logger.getLogger(ViewMergedSearchCoverageReportAction.class);

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




			double psmQValueCutoff = form.getPsmQValueCutoff();		
			double peptideQValueCutoff = form.getPeptideQValueCutoff();
			boolean filterNonUniquePeptides = form.isFilterNonUniquePeptides();
			boolean filterOnlyOnePSM = form.isFilterOnlyOnePSM();
			boolean filterOnlyOnePeptide = form.isFilterOnlyOnePeptide();

			
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PSM_Q_VALUE_FIELD_VALUE, Double.toString( psmQValueCutoff ) );
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PEPTIDE_Q_VALUE_FIELD_VALUE, Double.toString( peptideQValueCutoff ) );

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

			List<ProteinCoverageData> pcd = pcs.getProteinCoverageData();
			request.setAttribute( "proteinCoverageData", pcd );



			// Set values for general page functionality
			request.setAttribute( "queryString", request.getQueryString() );
			request.setAttribute( "searches", searches );
			
			GetSearchDetailsData.getInstance().getSearchDetailsData( searches, request );
	


			// build the JSON data structure for searches
			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
			ByteArrayOutputStream responseJSONBAOS = new ByteArrayOutputStream( 100000 );
			mapper.writeValue( responseJSONBAOS, searches ); // where first param can be File, OutputStream or Writer
			request.setAttribute( "searchJSON", responseJSONBAOS.toString() );





			// code for handling which proteins and species to show for exclusion filters
			Collection<Integer> types = new HashSet<Integer>();
			types.add( XLinkUtils.TYPE_CROSSLINK );
			types.add( XLinkUtils.TYPE_LOOPLINK );
			types.add( XLinkUtils.TYPE_DIMER );
			types.add( XLinkUtils.TYPE_MONOLINK );
			types.add( XLinkUtils.TYPE_UNLINKED );

			Collection<MergedSearchProtein> prProteins = MergedSearchProteinSearcher.getInstance().getProteinsWithLinkType(searches, types, psmQValueCutoff, peptideQValueCutoff);
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
