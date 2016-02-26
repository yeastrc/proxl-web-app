package org.yeastrc.xlink.www.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.searcher.PeptideWebPageSearcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchModMassDistinctSearcher;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideAnnotationDataSearcher;
import org.yeastrc.xlink.dto.AnnotationDataBaseDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.www.nav_links_image_structure.PopulateRequestDataForImageAndStructureNavLinks;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AnnotationTypeDTOListForSearchId;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ViewSearchPeptidesPageDataRoot;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SortAnnotationDTORecords;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataDefaultDisplayInDisplayOrder;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataInSortOrder;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.GetDefaultPsmPeptideCutoffs;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.PeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.forms.SearchViewPeptidesForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetProteinListingTooltipConfigData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
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
//			request.setAttribute( "searchViewCrosslinkPeptideForm", form );
			
			request.setAttribute( "strutsActionForm", form );


			int searchId = form.getSearchId();
			
			viewSearchPeptidesPageDataRoot.setSearchId( searchId );


			// Get the session first.  
//			HttpSession session = request.getSession();
			
			
			
			
			//   Get the project id for this search
			
			Collection<Integer> searchIdsSet = new HashSet<>();
			
			searchIdsSet.add( searchId );
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsSet );
			
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

			
			//  Populate request objects for Standard Header Display
			
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );

			//  Populate request objects for Protein Name Tooltip JS
			
			GetProteinListingTooltipConfigData.getInstance().getProteinListingTooltipConfigData( request );


			SearchDTO search = SearchDAO.getInstance().getSearch( searchId );

			
			
			//  Populate request objects for Standard Search Display

			GetSearchDetailsData.getInstance().getSearchDetailsData( search, request );
			
			
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
				
			} else {
				
				
				//  Query JSON in the form is empty so create an empty object that will be populated.
				
				
				peptideQueryJSONRoot = new PeptideQueryJSONRoot();
				
				
				CutoffValuesRootLevel cutoffValuesRootLevel =
						GetDefaultPsmPeptideCutoffs.getInstance()
						.getDefaultPsmPeptideCutoffs( searchIdsSet );
				
				peptideQueryJSONRoot.setCutoffs( cutoffValuesRootLevel );
				
				
				
				
				//  First need to check if the Query JSON was empty because the URL is a "Pre Generic" URL. 
				
				
				//   TODO   Probably need to convert old Query Params into the new Query Format 
				
				// TODO  For PSM and Peptide Q Value cutoffs, need to get Annotation Type Ids and add objects to peptideQueryJSONRoot

//				Double psmQValueCutoff = form.getPsmQValueCutoff();		
//				Double peptideQValueCutoff = form.getPeptideQValueCutoff();	
				
				
//				if ( psmQValueCutoff != null ) {
					
					
					//   One idea is to move this to a Pre-generic form and action that this action will forward to and then that action will 
					//     build a new URL with Query JSOn and redirect back to this action with that URL.
					
					
					
					//  The URL is a "Pre Generic" URL.  Need to populate the Query JSON Object from the data in the old form 
					
//
//					//  Process link types in form
//					
//					String[] linkTypesFromForm = form.getLinkType();
//					
//					if ( linkTypesFromForm != null ) {
//
//						boolean allWebLinkTypesSelected = false;
//
//						boolean webLinkTypeSelected_CROSSLINK = false;
//						boolean webLinkTypeSelected_LOOPLINK = false;
//						boolean webLinkTypeSelected_UNLINKED = false;
//
//						boolean webLinkTypeSelected_PREVIOUS_VALUE_MONOLINK_PSM = false;
//						boolean webLinkTypeSelected_PREVIOUS_VALUE_NO_LINK_PSM = false;
//
//						List<String> linkTypesFromFormList = new ArrayList<>( linkTypesFromForm.length ); 
//
//						for ( String linkType : linkTypesFromForm ) {
//
//							linkTypesFromFormList.add(linkType);
//						}
//
//						Iterator<String> linkTypeIter = linkTypesFromFormList.iterator();
//
//						while ( linkTypeIter.hasNext() ) {
//
//							String linkType = linkTypeIter.next();
//
//
//							if ( PeptideViewLinkTypesConstants.CROSSLINK_PSM.equals( linkType ) ) {
//
//								webLinkTypeSelected_CROSSLINK = true;
//
//							} else if ( PeptideViewLinkTypesConstants.LOOPLINK_PSM.equals( linkType ) ) {
//
//								webLinkTypeSelected_LOOPLINK = true;
//
//							} else if ( PeptideViewLinkTypesConstants.UNLINKED_PSM.equals( linkType ) ) {
//
//								webLinkTypeSelected_UNLINKED = true;
//
//
//								//  Process OLD values:
//
//							} else if ( PeptideViewLinkTypesConstants.PREVIOUS_VALUE_MONOLINK_PSM.equals( linkType ) ) {
//
//								//							webLinkTypeSelected_PREVIOUS_VALUE_MONOLINK_PSM = true;
//
//								linkTypeIter.remove();  //  Remove since value is no longer valid
//
//							} else if ( PeptideViewLinkTypesConstants.PREVIOUS_VALUE_NO_LINK_PSM.equals( linkType ) ) {
//
//								//							webLinkTypeSelected_PREVIOUS_VALUE_NO_LINK_PSM = true;
//
//								linkTypeIter.remove();  //  Remove since value is no longer valid
//
//
//
//							} else {
//
//								String msg = "linkType is invalid, linkType: " + linkType;
//
//								log.error( linkType );
//
//								throw new Exception( msg );
//							}
//						}
//
//						//  Removed this since would have to get these values back into the form object so that the 
//						//   web page would properly reflect the values processed.
//
//						if ( webLinkTypeSelected_CROSSLINK 
//								&& webLinkTypeSelected_LOOPLINK
//								&& webLinkTypeSelected_PREVIOUS_VALUE_MONOLINK_PSM 
//								&& webLinkTypeSelected_PREVIOUS_VALUE_NO_LINK_PSM ) {
//
//							//  Previously selected all values, so set equivalent current selections
//
//							webLinkTypeSelected_UNLINKED = true;
//
//							linkTypesFromFormList.add( PeptideViewLinkTypesConstants.UNLINKED_PSM );
//						}
//						
//						
//						linkTypesFromForm = (String[]) linkTypesFromFormList.toArray();
//					}
//					
//
//					peptideQueryJSONRoot.setLinkTypes( linkTypesFromForm );
//					
//					
//					//  Process dynamic mod masses in form
//					
//					String[] formModMassSelections = form.getModMassFilter();
//					
//
//					String[] modMassSelections = formModMassSelections;
//					
//					if ( formModMassSelections == null ) {
//						
//						//  Page loaded from link on different page so 
//						//   populate formModMassSelections with all values so all check boxes will be checked.
//						
//						String[] newFormModMassSelections = new String[ modMassStringsList.size() + 1 ]; 
//
//						newFormModMassSelections[ 0 ] = DynamicModificationsSelectionConstants.NO_DYNAMIC_MODIFICATIONS_SELECTION_ITEM;
//						
//						int index = 1;
//						for ( String modMassString : modMassStringsList ) {
//							
//							newFormModMassSelections[ index ] = modMassString;
//							index++;
//						}
//
//						modMassSelections = null; // for SQL query so not filter on mod mass
//						
//					} else {
//					
//
//						//  If all values are checked, set mod mass selector to null to not filter on mod mass 
//
//						boolean allFormModMassSelectionsChecked = true;
//
//						for ( String modMassString : modMassStringsList ) {
//
//							boolean modMassStringChecked = false;
//
//							for ( String formModMassSelection : formModMassSelections ) {
//
//								if ( modMassString.equals( formModMassSelection ) ) {
//									modMassStringChecked = true;
//									break;
//								}
//							}
//
//							if ( ! modMassStringChecked ) {
//
//								allFormModMassSelectionsChecked = false;
//								break;
//							}
//						}
//
//
//						for ( String formModMassSelection : formModMassSelections ) {
//
//							boolean modMassStringChecked = false;
//							
//							if ( DynamicModificationsSelectionConstants.NO_DYNAMIC_MODIFICATIONS_SELECTION_ITEM.equals( formModMassSelection ) ) {
//								modMassStringChecked = true;
//								break;
//							}
//							
//							if ( ! modMassStringChecked ) {
//
//								allFormModMassSelectionsChecked = false;
//								break;
//							}
//						}
//
//
//						if ( allFormModMassSelectionsChecked ) {
//
//							modMassSelections = null; // for SQL query so not filter on mod mass
//						}
//					}			
//					
//					peptideQueryJSONRoot.setMods( modMassSelections );
//					
//
//					
//					
//					//  TODO  Incomplete !!!!!!!!!!!!!
//					
//					//  TODO  Process 
////								Double psmQValueCutoff = form.getPsmQValueCutoff();		
////								Double peptideQValueCutoff = form.getPeptideQValueCutoff();	
//
//					
//					
					
//				}   //  END if Before Generic Form
				
				
				
				
				
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
			
			
			
			
			



			/////////////
			
			//   Get Peptide Annotation Types List sorted on Sort Order 
			

			Map<Integer, AnnotationTypeDTOListForSearchId> peptideAnnotationTypeDTO_SortOrder_MainMap =
					GetAnnotationTypeDataInSortOrder.getInstance()
					.getPeptide_AnnotationTypeDataInSortOrder( searchIdsSet );


			if ( peptideAnnotationTypeDTO_SortOrder_MainMap.size() != 1 ) {
				
				String msg = "getPeptide_AnnotationTypeDataInSortOrder returned other than 1 entry at searchId level ";
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			

			/////////////

			//   Get Peptide Annotation Types List sorted on Display Order 

			Map<Integer, AnnotationTypeDTOListForSearchId> peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap = 
					GetAnnotationTypeDataDefaultDisplayInDisplayOrder.getInstance()
					.getPeptide_AnnotationTypeDataDefaultDisplayInDisplayOrder( searchIdsSet );

			
			if ( peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.isEmpty() ) {

				String msg = "getPeptide_AnnotationTypeDataDefaultDisplayInDisplayOrder returned empty Map at searchId level, searchId: " + searchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}


			if ( peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.size() != 1 ) {

				String msg = "getPeptide_AnnotationTypeDataDefaultDisplayInDisplayOrder returned other than 1 entry at searchId level , searchId: " + searchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}

			
			
			///////////////////////////////////////
			
			

			String searchIdAsString = Integer.toString( searchId );

			CutoffValuesSearchLevel cutoffValuesSearchLevel = peptideQueryJSONRoot.getCutoffs().getSearches().get( searchIdAsString );

//			if ( cutoffValuesSearchLevel == null ) {
//
//				String msg = "Unable to get cutoffValuesSearchLevel for search id: " + searchIdAsString;
//				log.error( msg );
//				throw new ProxlWebappDataException(msg);
//			}

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

			AnnotationTypeDTOListForSearchId peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_Main =
					peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.get( searchId );

			if ( peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_Main == null ) {
				
				String msg = "peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_Main == null for searchId: " + searchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			
			AnnotationTypeDTOListForSearchId peptideAnnotationTypeDTO_DefaultDisplay_SortOrder_Main =
					peptideAnnotationTypeDTO_SortOrder_MainMap.get( searchId );

			if ( peptideAnnotationTypeDTO_DefaultDisplay_SortOrder_Main == null ) {
				
				String msg = "peptideAnnotationTypeDTO_DefaultDisplay_SortOrder_Main == null for searchId: " + searchId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			
			
			final List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List = 
					peptideAnnotationTypeDTO_DefaultDisplay_DisplayOrder_Main.getAnnotationTypeDTOList();

			final List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_SortOrder_List = 
					peptideAnnotationTypeDTO_DefaultDisplay_SortOrder_Main.getAnnotationTypeDTOList();
			
			

			List<SearcherCutoffValuesAnnotationLevel> psmCutoffValuesList = 
					searcherCutoffValuesSearchLevel.getPsmPerAnnotationCutoffsList();

			////////////////////////

			///    Get PSM AnnotationDTO Sorted In Display order 


			final List<AnnotationTypeDTO> psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted = new ArrayList<>( psmCutoffValuesList.size() );

			for ( SearcherCutoffValuesAnnotationLevel searcherCutoffValuesAnnotationLevel : psmCutoffValuesList ) {

				psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.add( searcherCutoffValuesAnnotationLevel.getAnnotationTypeDTO() );
			}

			SortAnnotationDTORecords.getInstance()
			.sortPsmAnnotationTypeDTOForBestPsmAnnotations_AnnotationDisplayOrder( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted );


			


			//////////////////////////////////////////////////////////////

			//  Get Peptides from DATABASE

			List<WebReportedPeptideWrapper> wrappedlinks =
					PeptideWebPageSearcher.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff( 
							search, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery );

			
			
			
			//////////////////////////////


			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesList = 
					searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();

			

			//////////////////////

			//  Get set of peptide annotation type ids to retrieve annotation data for


			Set<Integer> peptideAnnotationTypeIdsForAnnotationDataRetrieval = new HashSet<>(); 


			for ( AnnotationTypeDTO annotationTypeItem : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {

				peptideAnnotationTypeIdsForAnnotationDataRetrieval.add( annotationTypeItem.getId() );
			}

			for ( AnnotationTypeDTO annotationTypeItem : reportedPeptide_AnnotationTypeDTO_SortOrder_List ) {

				peptideAnnotationTypeIdsForAnnotationDataRetrieval.add( annotationTypeItem.getId() );
			}

			//  Remove Peptide Annotation type Ids that are in the filter list since the searcher for the peptides will return those

			for ( SearcherCutoffValuesAnnotationLevel peptideCutoffValue : peptideCutoffValuesList ) {

				peptideAnnotationTypeIdsForAnnotationDataRetrieval.remove( peptideCutoffValue.getAnnotationTypeId() );
			}



			//////////////////////////////////////////

			//  Get Peptide Annotation data for Sort and Display

			for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedlinks ) {

				WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();


				Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap = webReportedPeptideWrapper.getPeptideAnnotationDTOMap();

				if ( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List != null 
						&& ( ! reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.isEmpty() ) ) {

					List<SearchReportedPeptideAnnotationDTO> searchReportedPeptideFilterableAnnotationDataList = 
							SearchReportedPeptideAnnotationDataSearcher.getInstance()
							.getSearchReportedPeptideAnnotationDTOList( 
									searchId, webReportedPeptide.getReportedPeptideId(), peptideAnnotationTypeIdsForAnnotationDataRetrieval );


					for ( SearchReportedPeptideAnnotationDTO searchReportedPeptideFilterableAnnotationDataItem : searchReportedPeptideFilterableAnnotationDataList ) {

						peptideAnnotationDTOMap.put( searchReportedPeptideFilterableAnnotationDataItem.getAnnotationTypeId(), searchReportedPeptideFilterableAnnotationDataItem );
					}
				}



			}

			/////////////////////////////////////////
			
			///   Create sets of annotation type ids that were searched for but are not displayed by default.
			///   Those annotation values will be displayed after the default, in name order
			
			Set<Integer> peptideAnnotationTypesSearchedFor = new HashSet<>();
			
			List<SearcherCutoffValuesAnnotationLevel> peptideCutoffValuesPerAnnotationIdList =
					searcherCutoffValuesSearchLevel.getPeptidePerAnnotationCutoffsList();

			
			for (  SearcherCutoffValuesAnnotationLevel peptideCutoffEntry : peptideCutoffValuesPerAnnotationIdList ) {

				int annTypeId = peptideCutoffEntry.getAnnotationTypeId();
				peptideAnnotationTypesSearchedFor.add( annTypeId );
			}

			// Remove annotation type ids that are in default display

			for ( AnnotationTypeDTO item : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {

				peptideAnnotationTypesSearchedFor.remove( item.getId() );
			}

			//  Get AnnotationTypeDTO for ids not in default display and sort in name order
			
			List<AnnotationTypeDTO> peptideAnnotationTypesToAddFromQuery = new ArrayList<>();
			
			if ( ! peptideAnnotationTypesSearchedFor.isEmpty() ) {
				
				//   Add in Peptide annotation types the user searched for
				
				Map<Integer, Map<Integer, AnnotationTypeDTO>> peptideFilterableAnnotationTypesForSearchIds =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsSet );

				Map<Integer, AnnotationTypeDTO> peptideFilterableAnnotationTypesForSearchId =
						peptideFilterableAnnotationTypesForSearchIds.get( searchId );
				
				for ( Integer peptideAnnotationTypeToAdd : peptideAnnotationTypesSearchedFor ) {
				
					AnnotationTypeDTO annotationTypeDTO = peptideFilterableAnnotationTypesForSearchId.get( peptideAnnotationTypeToAdd );

					if ( annotationTypeDTO == null ) {
						
						
					}
					
					peptideAnnotationTypesToAddFromQuery.add( annotationTypeDTO );
				}
				
				// sort on ann type name
				Collections.sort( peptideAnnotationTypesToAddFromQuery, new Comparator<AnnotationTypeDTO>() {

					@Override
					public int compare(AnnotationTypeDTO o1,
							AnnotationTypeDTO o2) {

						return o1.getName().compareTo( o2.getName() );
					}
				} );
			}
			
			//   Add the searched for but not in default display AnnotationTypeDTO 
			//   to the default display list.
			//   The annotation data will be loaded from the DB in the searcher since they were searched for
			
			for ( AnnotationTypeDTO annotationTypeDTO : peptideAnnotationTypesToAddFromQuery ) {
				
				reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.add( annotationTypeDTO );
			}


			/////////////////////

			//   Copy Annotation Display Name and Descriptions to output lists, used for table headers in the HTML

			List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList = new ArrayList<>( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.size() );
			List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList = new ArrayList<>( psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted.size() );

			for ( AnnotationTypeDTO item : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {

				AnnotationDisplayNameDescription output = new AnnotationDisplayNameDescription();

				output.setDisplayName( item.getName() );
				output.setDescription( item.getDescription() );

				peptideAnnotationDisplayNameDescriptionList.add(output);
			}


			for ( AnnotationTypeDTO item : psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {

				AnnotationDisplayNameDescription output = new AnnotationDisplayNameDescription();

				output.setDisplayName( item.getName() );
				output.setDescription( item.getDescription() );

				psmAnnotationDisplayNameDescriptionList.add(output);
			}

			

			viewSearchPeptidesPageDataRoot.setPeptideAnnotationDisplayNameDescriptionList( peptideAnnotationDisplayNameDescriptionList );
			viewSearchPeptidesPageDataRoot.setPsmAnnotationDisplayNameDescriptionList( psmAnnotationDisplayNameDescriptionList );

			
			List<WebReportedPeptide> links = null;
			
			{
				
						

				//////////////////////////////////////////

				//  Sort Peptides on sort order
				
				WebReportedPeptideWrapperSorter webReportedPeptideWrapperSorter = new WebReportedPeptideWrapperSorter();
				
				webReportedPeptideWrapperSorter.reportedPeptide_AnnotationTypeDTO_SortOrder_List = 
						reportedPeptide_AnnotationTypeDTO_SortOrder_List;

				Collections.sort( wrappedlinks, webReportedPeptideWrapperSorter );
				
				
				




				//  Copy the links out of the wrappers for output - and Copy searched for peptide and psm annotations to link

				links = new ArrayList<>( wrappedlinks.size() );

				for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedlinks ) {

					WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();


					//  Copy searched for peptide and psm annotations to link


					List<String> peptideAnnotationValueList = new ArrayList<>( psmCutoffValuesList.size() );

					List<String> psmAnnotationValueList = new ArrayList<>( psmCutoffValuesList.size() );

					Map<Integer, AnnotationDataBaseDTO> peptideAnnotationDTOMap = webReportedPeptideWrapper.getPeptideAnnotationDTOMap();

					Map<Integer, AnnotationDataBaseDTO> psmAnnotationDTOMap = webReportedPeptideWrapper.getPsmAnnotationDTOMap();


					if (  peptideAnnotationDTOMap == null ) {

						String msg = "  webReportedPeptideWrapper.getPeptideAnnotationDTOMap() is null ";
						log.error( msg );
						throw new ProxlWebappDataException(msg);
					}
					if ( psmAnnotationDTOMap == null ) {

						String msg = "  webReportedPeptideWrapper.getPsmAnnotationDTOMap() is null ";
						log.error( msg );
						throw new ProxlWebappDataException(msg);
					}

					for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {

						AnnotationDataBaseDTO annotationDataBaseDTO = peptideAnnotationDTOMap.get( annotationTypeDTO.getId() );

						if ( annotationDataBaseDTO == null ) {

							String msg = "Unable to find annotation data for type id: " + annotationTypeDTO.getId();
							log.error( msg );
							throw new ProxlWebappDataException(msg);
						}

						peptideAnnotationValueList.add( annotationDataBaseDTO.getValueString() );
					}




					// Add sorted Best PSM data to webDisplayItem from webDisplayItemWrapper

					for ( AnnotationTypeDTO annotationTypeDTO : psmCutoffsAnnotationTypeDTOListAnnotationDisplayOrderSorted ) {

						AnnotationDataBaseDTO annotationDataBaseDTO = psmAnnotationDTOMap.get( annotationTypeDTO.getId() );

						if ( annotationDataBaseDTO == null ) {

							String msg = "Unable to find annotation data for type id: " + annotationTypeDTO.getId();
							log.error( msg );
							throw new ProxlWebappDataException(msg);
						}

						psmAnnotationValueList.add( annotationDataBaseDTO.getValueString() );
					}





					webReportedPeptide.setPeptideAnnotationValueList( peptideAnnotationValueList );
					webReportedPeptide.setPsmAnnotationValueList( psmAnnotationValueList );






					links.add( webReportedPeptide );
				}

			}


			

			viewSearchPeptidesPageDataRoot.setPeptideListSize( links.size() );
			viewSearchPeptidesPageDataRoot.setPeptideList( links );
			

			/////////////////////////////////
			
			
			if ( ! search.isNoScanData() ) {
			
				viewSearchPeptidesPageDataRoot.setShowNumberUniquePSMs( true );
			}


			
			request.setAttribute( "queryString",  request.getQueryString() );
			request.setAttribute( "mergedQueryString", request.getQueryString().replaceAll( "searchId=", "searchIds=" ) );


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
			
			PopulateRequestDataForImageAndStructureNavLinks.getInstance()
			.populateRequestDataForImageAndStructureNavLinksForPeptide( peptideQueryJSONRoot, projectId, authAccessLevel, form, request );
			

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

	
	
	
	
	////////////////////////////////////////
	
	////////   Sorter Class to Sort Peptides

	/**
	 * 
	 *
	 */
	private class WebReportedPeptideWrapperSorter implements Comparator<WebReportedPeptideWrapper> {

		List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_SortOrder_List;

		@Override
		public int compare(WebReportedPeptideWrapper o1, WebReportedPeptideWrapper o2) {

			//  Loop through the annotation types (sorted on sort order), comparing the values

			for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_SortOrder_List ) {

				int typeId = annotationTypeDTO.getId();

				AnnotationDataBaseDTO o1_WebReportedPeptide = o1.getPeptideAnnotationDTOMap().get( typeId );
				if ( o1_WebReportedPeptide == null ) {

					String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
					log.error( msg );
					throw new RuntimeException(msg);
				}

				double o1Value = o1_WebReportedPeptide.getValueDouble();


				AnnotationDataBaseDTO o2_WebReportedPeptide = o2.getPeptideAnnotationDTOMap().get( typeId );
				if ( o2_WebReportedPeptide == null ) {

					String msg = "Unable to get Peptide Filterable Annotation data for type id: " + typeId;
					log.error( msg );
					throw new RuntimeException(msg);
				}

				double o2Value = o2_WebReportedPeptide.getValueDouble();

				if ( o1Value != o2Value ) {

					if ( o1Value < o2Value ) {

						return -1;
					} else {
						return 1;
					}
				}

			}

			//  If everything matches, sort on reported peptide id

			return o1.getWebReportedPeptide().getReportedPeptideId() - o2.getWebReportedPeptide().getReportedPeptideId();
		}
	}
}