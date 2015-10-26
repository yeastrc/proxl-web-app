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
import org.yeastrc.xlink.www.searcher.PeptideMergedWebPageSearcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchModMassDistinctSearcher;
import org.yeastrc.xlink.www.constants.DynamicModificationsSelectionConstants;
import org.yeastrc.xlink.www.constants.QueryCriteriaValueCountsFieldValuesConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.QueryCriteriaValueCountsDAO;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.objects.SearchBooleanWrapper;
import org.yeastrc.xlink.www.objects.SearchCount;
import org.yeastrc.xlink.www.objects.VennDiagramDataToJSON;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptide;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.GenerateVennDiagramDataToJSON;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.TestAllWebLinkTypesSelected;
import org.yeastrc.xlink.www.webapp_timing.WebappTiming;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ViewMergedSearchPeptidesAction extends Action {
	
	private static final Logger log = Logger.getLogger(ViewMergedSearchPeptidesAction.class);

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
			MergedSearchViewPeptidesForm form = (MergedSearchViewPeptidesForm)actionForm;
			request.setAttribute( "mergedSearchViewCrosslinkPeptideForm", form );



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

			
			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After Auth" );
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

			request.setAttribute( "searches", searches );
			
			
			GetSearchDetailsData.getInstance().getSearchDetailsData( searches, request );
			


			double psmQValueCutoff = form.getPsmQValueCutoff();		
			double peptideQValueCutoff = form.getPeptideQValueCutoff();	


			
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PSM_Q_VALUE_FIELD_VALUE, Double.toString( psmQValueCutoff ) );
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PEPTIDE_Q_VALUE_FIELD_VALUE, Double.toString( peptideQValueCutoff ) );


			List<String> linkTypes = new ArrayList<>();

			
			for ( String linkType : form.getLinkType() ) {

				linkTypes.add( linkType );
			}
			
			if ( TestAllWebLinkTypesSelected.getInstance().testAllWebLinkTypesSelected( linkTypes ) ) {
				
				linkTypes = null;  //  Indicates all types selected
			}

			
			
			List<Double> modMassDistinctForSearchesList = SearchModMassDistinctSearcher.getInstance().getDistinctDynamicModMassesForSearchId( searchIds );

			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After get Distinct Dynamic Mod Masses For SearchId" );
			}
			
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
			
			

			//  For outermost top level table, show the field "Best Peptide Q-Value"
			boolean showTopLevelBestPeptideQValue = false;
			

			List<WebMergedReportedPeptide> links = 
					PeptideMergedWebPageSearcher.getInstance().search( searches, psmQValueCutoff, peptideQValueCutoff, linkTypes, modMassSelections );

			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After get Links with PeptideMergedWebPageSearcher.getInstance().search(...)" );
			}
			
			
			for ( WebMergedReportedPeptide link : links ) {
				

				List<SearchBooleanWrapper> searchContainsPeptide = new ArrayList<SearchBooleanWrapper>( searches.size() );
				for( SearchDTO search : searches ) {
					if( link.getSearches().contains( search ) ) {
						searchContainsPeptide.add( new SearchBooleanWrapper( search, true ) );
					} else {
						searchContainsPeptide.add( new SearchBooleanWrapper( search, false ) );
					}					
				}
				

				/// Check if any top level link "Best Q-Value" is not null
				
				if ( link.getBestPeptideQValue() != null ) {
					
					showTopLevelBestPeptideQValue = true;
				}

				link.setSearchContainsPeptide( searchContainsPeptide );
			}
			

			request.setAttribute( "showTopLevelBestPeptideQValue", showTopLevelBestPeptideQValue );

			
			request.setAttribute( "peptideListSize", links.size() );
			request.setAttribute( "peptideList", links );


			request.setAttribute( "psmQValueCutoff",  psmQValueCutoff );
			request.setAttribute( "peptideQValueCutoff",  peptideQValueCutoff );

			request.setAttribute( "queryString", request.getQueryString() );


			// build the JSON data structure for searches
			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
			ByteArrayOutputStream responseJSONBAOS = new ByteArrayOutputStream( 100000 );
			mapper.writeValue( responseJSONBAOS, searches ); // where first param can be File, OutputStream or Writer
			request.setAttribute( "searchJSON", responseJSONBAOS.toString() );

			

			VennDiagramDataToJSON vennDiagramDataToJSON =
					GenerateVennDiagramDataToJSON.createVennDiagramDataToJSON( links, searches );
			
			if ( vennDiagramDataToJSON != null ) {

				ByteArrayOutputStream vennDiagramDataJSONBAOS = new ByteArrayOutputStream( 100000 );
				mapper.writeValue( vennDiagramDataJSONBAOS, vennDiagramDataToJSON ); // where first param can be File, OutputStream or Writer
				request.setAttribute( "vennDiagramDataToJSON", vennDiagramDataJSONBAOS.toString() );
			}

			
			
			// get the counts for the number of links for each search, save to map, save to request
			Map<Integer, MutableInt> searchCounts = new TreeMap<Integer, MutableInt>();
			
			for( IMergedSearchLink link : links ) {
				
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
	
	
}
