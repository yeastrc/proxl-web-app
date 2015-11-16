package org.yeastrc.xlink.www.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.www.searcher.PeptideWebPageSearcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchModMassDistinctSearcher;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.constants.DynamicModificationsSelectionConstants;
import org.yeastrc.xlink.www.constants.QueryCriteriaValueCountsFieldValuesConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.QueryCriteriaValueCountsDAO;
import org.yeastrc.xlink.www.forms.SearchViewPeptidesForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.TestAllWebLinkTypesSelected;
import org.yeastrc.xlink.www.webapp_timing.WebappTiming;



public class ViewSearchPeptidesAction extends Action {
	
	private static final Logger log = Logger.getLogger(ViewSearchPeptidesAction.class);

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
			SearchViewPeptidesForm form = (SearchViewPeptidesForm)actionForm;
			request.setAttribute( "searchViewCrosslinkPeptideForm", form );

			int searchId = form.getSearchId();


			// Get the session first.  
//			HttpSession session = request.getSession();
			
			
			
			
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
				
				//  First remove any project_id with an empty value
				getQueryString = getQueryString.replace( "project_id=&", "" );
				
				
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

			List<String> linkTypes = new ArrayList<>();

			
			for ( String linkType : form.getLinkType() ) {

				linkTypes.add( linkType );
			}
			
			
			if ( TestAllWebLinkTypesSelected.getInstance().testAllWebLinkTypesSelected( linkTypes ) ) {
				
				linkTypes = null;  //  Indicates all types selected
			}

			
			
			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After Auth" );
			}
			
			int[] searchIdsArray = { searchId };
			
			List<Double> modMassDistinctForSearchesList = SearchModMassDistinctSearcher.getInstance().getDistinctDynamicModMassesForSearchId( searchIdsArray );

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
			

			
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PSM_Q_VALUE_FIELD_VALUE, Double.toString( psmQValueCutoff ) );
			QueryCriteriaValueCountsDAO.getInstance().saveOrIncrement( 
					QueryCriteriaValueCountsFieldValuesConstants.PEPTIDE_Q_VALUE_FIELD_VALUE, Double.toString( peptideQValueCutoff ) );

			
			List<WebReportedPeptide> links = 
					PeptideWebPageSearcher.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff( 
							search, psmQValueCutoff, peptideQValueCutoff, linkTypes, modMassSelections );

			if ( webappTiming != null ) {
				
				webappTiming.markPoint( "After get Links with searchOnSearchIdPsmCutoffPeptideCutoff" );
			}
			


			//  For outermost top level table, show the field "Peptide Q-Value"
			boolean showTopLevelPeptideQValue = false;

			for ( WebReportedPeptide link : links ) {
				
				if ( link.getqValue() != null ) {
					
					showTopLevelPeptideQValue = true;
					break;
				}
			}

			
			if ( ! search.isNoScanData() ) {
			
				request.setAttribute( "showNumberUniquePSMs", true );
			}

			request.setAttribute( "showTopLevelPeptideQValue", showTopLevelPeptideQValue );

			
			request.setAttribute( "peptideListSize", links.size() );
			request.setAttribute( "peptideList", links );

			request.setAttribute( "psmQValueCutoff",  psmQValueCutoff );
			request.setAttribute( "peptideQValueCutoff",  peptideQValueCutoff );
			request.setAttribute( "queryString",  request.getQueryString() );
			request.setAttribute( "mergedQueryString", request.getQueryString().replaceAll( "searchId=", "searchIds=" ) );



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
