package org.yeastrc.xlink.www.actions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchModMassDistinctSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.nav_links_image_structure.PopulateRequestDataForImageAndStructureAndQC_NavLinks;
import org.yeastrc.xlink.www.no_data_validation.ThrowExceptionOnNoDataConfig;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinPosition;
import org.yeastrc.xlink.www.objects.ViewSearchPeptidesPageDataRoot;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSONRoot;
import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.constants.MinimumPSMsConstants;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.GetDefaultPsmPeptideCutoffs;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappNoDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.PeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.form_utils.Update__A_QueryBase_JSONRoot__ForCurrentSearchIds;
import org.yeastrc.xlink.www.forms.SearchViewPeptidesForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems;
import org.yeastrc.xlink.www.web_utils.GetAnnotationDisplayUserSelectionDetailsData;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.IsShowDownloadLinks_Skyline_SetRequestParameters;
import org.yeastrc.xlink.www.web_utils.ProteinListingTooltipConfigUtil;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode.SearchPeptideWebserviceCommonCodeGetDataResult;
import org.yeastrc.xlink.www.webapp_timing.WebappTiming;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 
 *
 */
public class ViewSearchPeptidesAction extends Action {
	
	private static final Logger log = Logger.getLogger(ViewSearchPeptidesAction.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
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
		// Root object of everything placed on the ViewSearchPeptides page by the JSP
		ViewSearchPeptidesPageDataRoot viewSearchPeptidesPageDataRoot = new ViewSearchPeptidesPageDataRoot();
		request.setAttribute( "viewSearchPeptidesPageDataRoot", viewSearchPeptidesPageDataRoot );
		try {
			// our form
			SearchViewPeptidesForm form = (SearchViewPeptidesForm)actionForm;
			request.setAttribute( "strutsActionForm", form );
			int projectSearchId = form.getProjectSearchIdSingle();
			viewSearchPeptidesPageDataRoot.setProjectSearchId( projectSearchId );
			// Get the session first.  
//			HttpSession session = request.getSession();
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsSet = new HashSet<>();
			projectSearchIdsSet.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchId: " + projectSearchId;
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
			if ( webappTiming != null ) {
				webappTiming.markPoint( "After Auth" );
			}
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////

			request.setAttribute( "projectSearchId", projectSearchId );
			
			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
			
			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			if ( search == null ) {
				String msg = ": No searchId found for projectSearchId: " + projectSearchId;
				log.warn( msg );
				throw new ProxlWebappDataException( msg );
			}
			int searchId = search.getSearchId();
			viewSearchPeptidesPageDataRoot.setSearchId( searchId );
			
			Collection<Integer> searchIdsSet = new HashSet<>();
			searchIdsSet.add( searchId );
			Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
			mapProjectSearchIdToSearchId.put( projectSearchId, searchId );
			
			//  Populate request objects for Standard Header Display
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );
			//  Populate request objects for Protein Name Tooltip JS
			ProteinListingTooltipConfigUtil.getInstance().putProteinListingTooltipConfigForPage( projectSearchIdsSet, request );
			//  Populate request objects for Standard Search Display
			GetSearchDetailsData.getInstance().getSearchDetailsData( search, request );
			//  Populate request objects for User Selection of Annotation Data Display
			GetAnnotationDisplayUserSelectionDetailsData.getInstance().getSearchDetailsData( search, request );
			//  Populate request objects for excludeLinksWith_Remove_NonUniquePSMs_Checkbox_Fragment.jsp
			ExcludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems.getInstance().excludeLinksWith_Remove_NonUniquePSMs_Checkbox_PopRequestItems( search, request );
			
			//  Populates request attribute
			IsShowDownloadLinks_Skyline_SetRequestParameters.getInstance().isShowDownloadLinks_Skyline_SetRequestParameters( searchIdsSet, request );
			
			///  Get list of all possible Dynamic Mod Masses.  Do here so if convert existing Query Param Data, have it here.
			int[] searchIdsArray = { searchId };
			List<Double> modMassDistinctForSearchesList = SearchModMassDistinctSearcher.getInstance().getDistinctDynamicModMassesForSearchId( searchIdsArray );
			if ( webappTiming != null ) {
				webappTiming.markPoint( "After get Distinct Dynamic Mod Masses For SearchId" );
			}
			List<String> modMassFilterList = new ArrayList<>( modMassDistinctForSearchesList.size() );
			for ( Double modMass : modMassDistinctForSearchesList ) {
				String modMassAsString = modMass.toString();
				modMassFilterList.add( modMassAsString );
			}
			viewSearchPeptidesPageDataRoot.setModMassFilterList( modMassFilterList );
			
			//   Get Query JSON from the form and if not empty, deserialize it
			String queryJSONFromForm = form.getQueryJSON();
			PeptideQueryJSONRoot peptideQueryJSONRoot = null;
			if ( StringUtils.isNotEmpty( queryJSONFromForm ) ) {
				try {
					peptideQueryJSONRoot = jacksonJSON_Mapper.readValue( queryJSONFromForm, PeptideQueryJSONRoot.class );
				} catch ( JsonParseException e ) {
					String msg = "Failed to parse 'queryJSONFromForm', JsonParseException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				} catch ( JsonMappingException e ) {
					String msg = "Failed to parse 'queryJSONFromForm', JsonMappingException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				} catch ( IOException e ) {
					String msg = "Failed to parse 'queryJSONFromForm', IOException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				}

				//  Update peptideQueryJSONRoot for current search ids and project search ids
				Update__A_QueryBase_JSONRoot__ForCurrentSearchIds.getInstance()
				.update__A_QueryBase_JSONRoot__ForCurrentSearchIds( peptideQueryJSONRoot, mapProjectSearchIdToSearchId );
				
			} else {
				//  Query JSON in the form is empty so create an empty object that will be populated.
				peptideQueryJSONRoot = new PeptideQueryJSONRoot();
				CutoffValuesRootLevel cutoffValuesRootLevel =
						GetDefaultPsmPeptideCutoffs.getInstance()
						.getDefaultPsmPeptideCutoffs( projectSearchIdsSet, searchIdsSet, mapProjectSearchIdToSearchId );
				peptideQueryJSONRoot.setCutoffs( cutoffValuesRootLevel );
			}   //   END  ELSE of  if ( StringUtils.isNotEmpty( queryJSONFromForm ) ) {
			
			//   Update Link Type to default to Crosslink if no value was set
			String[] linkTypesInForm = peptideQueryJSONRoot.getLinkTypes();
			if ( linkTypesInForm == null || linkTypesInForm.length == 0 ) {
				String[] linkTypesCrosslink = { PeptideViewLinkTypesConstants.CROSSLINK_PSM };
				linkTypesInForm = linkTypesCrosslink;
				peptideQueryJSONRoot.setLinkTypes( linkTypesInForm );
			}
			/////////////////////////////////
			//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
			String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( linkTypesInForm );
			//   Mods for DB Query
			String[] modsForDBQuery = peptideQueryJSONRoot.getMods();
			
			///////////////////////////////////////////////////////
			String projectSearchIdAsString = Integer.toString( projectSearchId );
			//  Get Cutoff values at search level
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = null;
			CutoffValuesSearchLevel cutoffValuesSearchLevel = null;
			CutoffValuesRootLevel cutoffValuesRootLevel = peptideQueryJSONRoot.getCutoffs();
			if ( cutoffValuesRootLevel != null ) {
				if ( cutoffValuesRootLevel.getSearches() != null ) {
					cutoffValuesSearchLevel = peptideQueryJSONRoot.getCutoffs().getSearches().get( projectSearchIdAsString );
				}
			}
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
			
			////////////////////
			//  Get User Selected Peptide Annotation type ids to display
			List<Integer> peptideAnnotationTypeIdsToDisplay = null;
			AnnTypeIdDisplayJSONRoot annTypeIdDisplayRoot =	peptideQueryJSONRoot.getAnnTypeIdDisplay();
			if ( annTypeIdDisplayRoot != null && annTypeIdDisplayRoot.getSearches() != null ) {
				AnnTypeIdDisplayJSON_PerSearch annTypeIdDisplayJSON_PerSearch =
						annTypeIdDisplayRoot.getSearches().get( projectSearchIdAsString );
				if ( annTypeIdDisplayJSON_PerSearch != null ) {
					peptideAnnotationTypeIdsToDisplay = annTypeIdDisplayJSON_PerSearch.getPeptide();
				}
			}
			
			//////////////////////////////////////////////////////////////
			//  Get Peptides from DATABASE
			List<WebReportedPeptideWrapper> wrappedlinks =
					PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
							search, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery, 
							PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );
			
			//////////////////////////////////////////////////////////////////
			
			// Filter out links if requested, and Update PSM counts if "remove non-unique PSMs" selected 
			
			if( peptideQueryJSONRoot.getMinPSMs() != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT 
					|| peptideQueryJSONRoot.isRemoveNonUniquePSMs()
					|| peptideQueryJSONRoot.isRemoveIntraProteinLinks() ) {
				
				///////  Output Lists, Results After Filtering
				List<WebReportedPeptideWrapper> wrappedlinksAfterFilter = new ArrayList<>( wrappedlinks.size() );

				///  Filter links
				for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedlinks ) {
					WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
					// did the user request to removal of links with only Non-Unique PSMs?
					if( peptideQueryJSONRoot != null && peptideQueryJSONRoot.isRemoveNonUniquePSMs()  ) {
						//  Update webReportedPeptide object to remove non-unique PSMs
						webReportedPeptide.updateNumPsmsToNotInclude_NonUniquePSMs();
						if ( webReportedPeptide.getNumPsms() <= 0 ) {
							// The number of PSMs after update is now zero
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did the user request to removal of links with less than a specified number of PSMs?
					if( peptideQueryJSONRoot.getMinPSMs() != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT ) {
						int psmCountForSearchId = webReportedPeptide.getNumPsms();
						if ( psmCountForSearchId < peptideQueryJSONRoot.getMinPSMs() ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did the user request to removal of links Intra Protein Link
					if( peptideQueryJSONRoot.isRemoveIntraProteinLinks() ) {
						
						SearchPeptideCrosslink searchPeptideCrosslink = webReportedPeptide.getSearchPeptideCrosslink();
						if ( searchPeptideCrosslink != null ) {
							
							boolean foundPeptide_1_protein_In_Peptide_2_proteins = false;
							
							List<SearchProteinPosition> peptide_1_Proteins = searchPeptideCrosslink.getPeptide1ProteinPositions();
							List<SearchProteinPosition> peptide_2_Proteins = searchPeptideCrosslink.getPeptide2ProteinPositions();
							for ( SearchProteinPosition peptide_1_Protein : peptide_1_Proteins ) {
							
								for ( SearchProteinPosition peptide_2_Protein : peptide_2_Proteins ) {
									
									if (       peptide_1_Protein.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() 
											== peptide_2_Protein.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
										
										//  Found Same ProteinSequenceVersionId in Proteins for Peptide 1 and Peptide 2
										foundPeptide_1_protein_In_Peptide_2_proteins = true;
										break;
									}
								}
								if ( foundPeptide_1_protein_In_Peptide_2_proteins ) {
									break;
								}
							}
							if ( foundPeptide_1_protein_In_Peptide_2_proteins ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
					}
					
					wrappedlinksAfterFilter.add( webReportedPeptideWrapper );
				}

				wrappedlinks = wrappedlinksAfterFilter;
			}

			//  If configured, throw exception if no peptides found
			if ( ThrowExceptionOnNoDataConfig.getInstance().isThrowExceptionNoData() ) {
				if ( wrappedlinks.isEmpty() ) {
					String msg = "No Peptides found and config set for ThrowExceptionNoData";
					log.error( msg );
					throw new ProxlWebappNoDataException( msg );
				}
			}
			
			//  Get Annotation Data for links and Sort Links
			SearchPeptideWebserviceCommonCodeGetDataResult searchPeptideWebserviceCommonCodeGetDataResult =
					SearchPeptideWebserviceCommonCode.getInstance()
					.getPeptideAndPSMDataForLinksAndSortLinks( 
							searchId, 
							wrappedlinks, 
							searcherCutoffValuesSearchLevel, 
							peptideAnnotationTypeIdsToDisplay );
			//  Copy the links out of the wrappers for output - and Copy searched for peptide and psm annotations to link
			List<WebReportedPeptide> links = new ArrayList<>( wrappedlinks.size() );
			for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedlinks ) {
				WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
				//  Put Annotation data on the link
				SearchPeptideWebserviceCommonCode.getInstance()
				.putPeptideAndPSMDataOnWebserviceResultLinkOject( 
						searchPeptideWebserviceCommonCodeGetDataResult, 
						webReportedPeptideWrapper, // input
						webReportedPeptide );  // updated output
				links.add( webReportedPeptide );
			}
			viewSearchPeptidesPageDataRoot.setPeptideListSize( links.size() );
			viewSearchPeptidesPageDataRoot.setPeptideList( links );
			viewSearchPeptidesPageDataRoot.setPeptideAnnotationDisplayNameDescriptionList(
					searchPeptideWebserviceCommonCodeGetDataResult.getPeptideAnnotationDisplayNameDescriptionList() );
			viewSearchPeptidesPageDataRoot.setPsmAnnotationDisplayNameDescriptionList( 
					searchPeptideWebserviceCommonCodeGetDataResult.getPsmAnnotationDisplayNameDescriptionList() );

			if ( search.isHasScanData() ) {
				viewSearchPeptidesPageDataRoot.setShowNumberUniquePSMs( true );
			}
			request.setAttribute( "queryString",  request.getQueryString() );
			/////////////////////
			//  clear out form so value doesn't go back on the page in the form
			form.setQueryJSON( "" );
			////  Put Updated queryJSON on the page
			{
				try {
					String queryJSONToForm = jacksonJSON_Mapper.writeValueAsString( peptideQueryJSONRoot );
					//  Set queryJSON in request attribute to put on page outside of form
					viewSearchPeptidesPageDataRoot.setQueryJSONToForm( queryJSONToForm );
				} catch ( JsonProcessingException e ) {
					String msg = "Failed to write as JSON 'queryJSONToForm', JsonProcessingException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				} catch ( Exception e ) {
					String msg = "Failed to write as JSON 'queryJSONToForm', Exception.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw new ProxlWebappDataException( msg, e );
				}
			}
			//  Create data for Links for Image and Structure pages and put in request
			PopulateRequestDataForImageAndStructureAndQC_NavLinks.getInstance()
			.populateRequestDataForImageAndStructureAndQC_NavLinksForPeptide( peptideQueryJSONRoot, projectId, authAccessLevel, form, request );
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
}