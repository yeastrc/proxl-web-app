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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.actions.ProteinsAllCommonAll.ProteinSingleEntry;
import org.yeastrc.xlink.www.actions.ProteinsAllCommonAll.ProteinsAllCommonAllResult;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.www.nav_links_image_structure.PopulateRequestDataForImageAndStructureAndQC_NavLinks;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.PeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.form_utils.GetProteinQueryJSONRootFromFormData;
import org.yeastrc.xlink.www.forms.SearchViewProteinsForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId;
import org.yeastrc.xlink.www.web_utils.GetAnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.ProteinListingTooltipConfigUtil;
import org.yeastrc.xlink.www.web_utils.TaxonomiesForSearchOrSearches;
import org.yeastrc.xlink.www.web_utils.URLEncodeDecodeAURL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Used for Showing all proteins on a page 
 * 
 * Separate from Crosslink Proteins and Looplink Proteins pages 
 *
 */
public class ViewSearchProteinsAllAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( ViewSearchProteinsAllAction.class);

	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			// our form
			SearchViewProteinsForm form = (SearchViewProteinsForm)actionForm;
			request.setAttribute( "searchViewProteinsForm", form );
			int projectSearchId = form.getProjectSearchIdSingle();
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsSet = new HashSet<>();
			projectSearchIdsSet.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search id: " + projectSearchId;
				log.error( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			request.setAttribute( "projectId", projectId ); 
			request.setAttribute( "project_id", projectId );
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
			
			///    Done Processing Auth Check and Auth Level

			request.setAttribute( "projectSearchId", projectSearchId );
			
			//////////////////////////////
			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			
			//  Populate request objects for Standard Header Display
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );
			//  Populate request objects for Protein Name Tooltip JS
			ProteinListingTooltipConfigUtil.getInstance().putProteinListingTooltipConfigForPage( projectSearchIdsSet, request );
			request.setAttribute( "queryString",  request.getQueryString() );
			
			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			if ( search == null ) {
				String msg = ": No searchId found for projectSearchId: " + projectSearchId;
				log.warn( msg );
				throw new ProxlWebappDataException( msg );
			}
			request.setAttribute( "search", search );
			int searchId = search.getSearchId();
			Collection<Integer> searchIdsSet = new HashSet<>();
			searchIdsSet.add( searchId );
			Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
			mapProjectSearchIdToSearchId.put( projectSearchId, searchId );
			
			// build list of taxonomies to show in exclusion list
			Map<Integer, String> taxonomies = 
					TaxonomiesForSearchOrSearches.getInstance().getTaxonomiesForSearchIdForAllLinkTypes( search.getSearchId() );
			request.setAttribute("taxonomies", taxonomies );
			
			//   Get Query JSON from the form and if not empty, deserialize it
			ProteinQueryJSONRoot proteinQueryJSONRoot = 
					GetProteinQueryJSONRootFromFormData.getInstance()
					.getProteinQueryJSONRootFromFormData( form, projectSearchIdsSet, searchIdsSet, mapProjectSearchIdToSearchId );
			////////////
			//  Copy Exclude Taxonomy and Exclude Protein Sets for lookup
			Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
			Set<Integer> excludeproteinSequenceVersionIds_Set_UserInput = new HashSet<>();
			if ( proteinQueryJSONRoot.getExcludeTaxonomy() != null ) {
				for ( Integer taxonomyId : proteinQueryJSONRoot.getExcludeTaxonomy() ) {
					excludeTaxonomy_Ids_Set_UserInput.add( taxonomyId );
				}
			}
			//  First convert the protein sequence ids that come from the JS code to standard integers and put
			//   in the property excludeproteinSequenceVersionIds
			ProteinsMergedProteinsCommon.getInstance().processExcludeproteinSequenceVersionIdsFromJS( proteinQueryJSONRoot );
			if ( proteinQueryJSONRoot.getExcludeproteinSequenceVersionIds() != null ) {
				for ( Integer proteinId : proteinQueryJSONRoot.getExcludeproteinSequenceVersionIds() ) {
					excludeproteinSequenceVersionIds_Set_UserInput.add( proteinId );
				}
			}
			
			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			////////   Generic Param processing
			//  Get Annotation Type records for PSM and Peptide
			//  Get  Annotation Type records for PSM
			Map<Integer, Map<Integer, AnnotationTypeDTO>> 
			srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIdsSet );
			Map<Integer, AnnotationTypeDTO> srchPgm_Filterable_Psm_AnnotationType_DTOMap = 
					srchPgm_Filterable_Psm_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			if ( srchPgm_Filterable_Psm_AnnotationType_DTOMap == null ) {
				//  No records were found, probably an error   TODO
				srchPgm_Filterable_Psm_AnnotationType_DTOMap = new HashMap<>();
			}
			//  Get  Annotation Type records for Reported Peptides
			Map<Integer, Map<Integer, AnnotationTypeDTO>> 
			srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap =
					GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsSet );
			Map<Integer, AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = 
					srchPgm_Filterable_ReportedPeptide_AnnotationType_DTOListPerSearchIdMap.get( searchId );
			if ( srchPgmFilterableReportedPeptideAnnotationTypeDTOMap == null ) {
				//  No records were found, allowable for Reported Peptides
				srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = new HashMap<>();
			}
			
			//  Populate request objects for Standard Search Display
			GetSearchDetailsData.getInstance().getSearchDetailsData( search, request );
			//  Populate request objects for User Selection of Annotation Data Display
			GetAnnotationDisplayUserSelectionDetailsData.getInstance().getSearchDetailsData( search, request );
			//  Populate request objects for excludeLinksWith_Remove_NonUniquePSMs_Checkbox_Fragment.jsp
			ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems.getInstance().excludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems( search, request );

			String projectSearchIdAsString = Integer.toString( projectSearchId );
			
			CutoffValuesSearchLevel cutoffValuesSearchLevel = proteinQueryJSONRoot.getCutoffs().getSearches().get( projectSearchIdAsString );
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = null;
			if ( cutoffValuesSearchLevel == null ) {
				//  Create empty object for default values
				searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
			} else {
				Z_CutoffValuesObjectsToOtherObjects_PerSearchResult z_CutoffValuesObjectsToOtherObjects_PerSearchResult = 
						Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesSearchLevel( searchIdsSet, cutoffValuesSearchLevel );
				searcherCutoffValuesSearchLevel = z_CutoffValuesObjectsToOtherObjects_PerSearchResult.getSearcherCutoffValuesSearchLevel();
				if ( searcherCutoffValuesSearchLevel == null ) {
					//  Create empty object for default values
					searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
				}
			}
			////////////////////////////////////////////////
			//////    
			//  Create list of Peptide and Best PSM annotation names to display as column headers
			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
					searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();
			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
					searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();
			List<AnnotationTypeDTO> peptideCutoffsAnnotationTypeDTOList = new ArrayList<>( peptideCutoffValuesList.size() );
			List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOList = new ArrayList<>( psmCutoffValuesList.size() );
			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : peptideCutoffValuesList ) {
				peptideCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
			}
			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {
				psmCutoffsAnnotationTypeDTOList.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
			}

			ProteinsAllCommonAllResult proteinsAllCommonAllResult =
					ProteinsAllCommonAll.getInstance().getProteinSingleEntryList(
							null /* onlyReturnThisproteinSequenceVersionId */, 
							search, 
							searchId,
							proteinQueryJSONRoot, 
							excludeTaxonomy_Ids_Set_UserInput, 
							excludeproteinSequenceVersionIds_Set_UserInput,
							searcherCutoffValuesSearchLevel );

			List<ProteinSingleEntry> proteinSingleEntryList = proteinsAllCommonAllResult.getProteinSingleEntryList();
			Set<SearchProtein> searchProteinUnfilteredForSearch = proteinsAllCommonAllResult.getSearchProteinUnfilteredForSearch();

			////   Prepare for Web display
			
			//  presort to have consistent final sort order where annotation data is matching.
			Collections.sort( proteinSingleEntryList, new Comparator<ProteinSingleEntry>() {
				@Override
				public int compare(ProteinSingleEntry o1,
						ProteinSingleEntry o2) {
					return o1.getProteinSequenceVersionId() - o2.getProteinSequenceVersionId();
				}
			} );
			//      Get Annotation data and Sort by Annotation data
			SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt sortLooplinksResult =
					SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.getInstance()
					.sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId(
							searchId, 
							proteinSingleEntryList, 
							peptideCutoffsAnnotationTypeDTOList, 
							psmCutoffsAnnotationTypeDTOList );
			request.setAttribute( "peptideAnnotationDisplayNameDescriptionList", sortLooplinksResult.getPeptideAnnotationDisplayNameDescriptionList() );
			request.setAttribute( "psmAnnotationDisplayNameDescriptionList", sortLooplinksResult.getPsmAnnotationDisplayNameDescriptionList() );
			
			///////////////////////////////////////////////////////
			Collection<SearchProtein> proteinsForExclusionListCopy = new HashSet<SearchProtein>();
			proteinsForExclusionListCopy.addAll( searchProteinUnfilteredForSearch );
			// build a collection of protein IDs to include
			for( SearchProtein prp : proteinsForExclusionListCopy ) {
				// did they request removal of certain taxonomy IDs?
				if( proteinQueryJSONRoot.getExcludeTaxonomy() != null && proteinQueryJSONRoot.getExcludeTaxonomy().length > 0 ) {
					boolean excludeOnProtein =
							ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
							.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
									excludeTaxonomy_Ids_Set_UserInput, 
									prp.getProteinSequenceVersionObject(), 
									searchId );
					if ( excludeOnProtein ) {
						searchProteinUnfilteredForSearch.remove( prp );
					}
				}
			}
			
			List<SearchProtein> sortedProteinsForExclusionList = new ArrayList<SearchProtein>();
			sortedProteinsForExclusionList.addAll( searchProteinUnfilteredForSearch );
			Collections.sort( sortedProteinsForExclusionList, new SortSearchProtein() );
			request.setAttribute( "proteins", sortedProteinsForExclusionList );
			request.setAttribute( "numProteins", proteinSingleEntryList.size() );
//			request.setAttribute( "numDistinctLinks", numDistinctLinks );
			request.setAttribute( "proteinsMainList", proteinSingleEntryList );
			/////////////////////
			//  clear out form so value doesn't go back on the page in the form
			form.setQueryJSON( "" );
			/////////////////////
			////  Put Updated queryJSON on the page
			{
				try {
					String queryJSONToForm = jacksonJSON_Mapper.writeValueAsString( proteinQueryJSONRoot );
					//  Set queryJSON in request attribute to put on page outside of form
					request.setAttribute( "queryJSONToForm", queryJSONToForm );
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
			/////////////////////
			////  Put queryJSON for Peptide Link on the page
			{
				try {
					PeptideQueryJSONRoot peptideQueryJSONRoot = new PeptideQueryJSONRoot();
					peptideQueryJSONRoot.setCutoffs( proteinQueryJSONRoot.getCutoffs() );
					peptideQueryJSONRoot.setMinPSMs( proteinQueryJSONRoot.getMinPSMs() );
					String[] peptidePageLinkTypes = null;
//					String[] peptidePageLinkTypes = new String[ 1 ];
//					if ( Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__CROSSLINK.equals( strutsActionMappingParameter ) ) {
//						peptidePageLinkTypes[ 0 ] = XLinkUtils.CROSS_TYPE_STRING_UPPERCASE; 
//					} else if ( Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__LOOPLINK.equals( strutsActionMappingParameter ) ) {
//						peptidePageLinkTypes[ 0 ] = XLinkUtils.LOOP_TYPE_STRING_UPPERCASE; 
//					}
					peptideQueryJSONRoot.setLinkTypes( peptidePageLinkTypes );
					String peptideQueryJSONRootJSONString = jacksonJSON_Mapper.writeValueAsString( peptideQueryJSONRoot );
					//  Create URI Encoded JSON for passing to Image and Structure pages in hash 
					String peptideQueryJSONRootJSONStringURIEncoded = URLEncodeDecodeAURL.urlEncodeAURL( peptideQueryJSONRootJSONString );
					request.setAttribute( "peptidePageQueryJSON", peptideQueryJSONRootJSONStringURIEncoded );
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
			PopulateRequestDataForImageAndStructureAndQC_NavLinks.getInstance()
			.populateRequestDataForImageAndStructureNavLinksForProtein( proteinQueryJSONRoot, projectId, authAccessLevel, form, request );

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

	
	//////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////
    /**
     * 
     *
     */
    public class SortSearchProtein implements Comparator<SearchProtein> {
        public int compare(SearchProtein o1, SearchProtein o2) {
            try { return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()); }
            catch( Exception e ) { return 0; }
        }
    }
}
