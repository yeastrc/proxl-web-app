package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.GetCrosslinkReportedPeptidesServiceResult;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslinkAnnDataWrapper;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslinkWebserviceResult;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchPeptideCrosslink_ForCrosslinkPeptideWS_Searcher;
import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.annotation_display.DeserializeAnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ExcludeLinksWith_JSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_Deserialize_ExcludeLinksWith_JSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.DeserializeCutoffForWebservices;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode.SearchPeptideWebserviceCommonCodeGetDataResult;


/**
 * 
 *
 */
@Path("/data")
public class ReportedPeptides_Crosslink_Service {
	
	private static final Logger log = LoggerFactory.getLogger( ReportedPeptides_Crosslink_Service.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCrosslinkReportedPeptides") 
	public GetCrosslinkReportedPeptidesServiceResult getCrosslinkReportedPeptides( 
			@QueryParam( "project_search_id" ) Integer projectSearchId,
			@QueryParam( "psmPeptideCutoffsForProjectSearchId" ) String psmPeptideCutoffsForProjectSearchId_JSONString,
			@QueryParam( "peptideAnnTypeDisplayPerSearch" ) String annTypeIdDisplayJSON_PerSearch_JSONString,
			@QueryParam( "excludeLinksWith_Root" ) String excludeLinksWith_Root_JSONString,
			@QueryParam( "protein_1_id" ) Integer protein1Id,
			@QueryParam( "protein_2_id" ) Integer protein2Id,
			@QueryParam( "protein_1_position" ) Integer protein1Position,
			@QueryParam( "protein_2_position" ) Integer protein2Position,
			@Context HttpServletRequest request )
	throws Exception {

		if ( projectSearchId == null ) {
			String msg = "Provided search_id is null or search_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( psmPeptideCutoffsForProjectSearchId_JSONString ) ) {
			String msg = "Provided psmPeptideCutoffsForProjectSearchId is null or psmPeptideCutoffsForProjectSearchId is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		///////////////////////
		if ( protein1Id == null ) {
			String msg = "Provided protein_1_id is null or protein_1_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( protein2Id == null ) {
			String msg = "Provided protein_2_id is null or protein_2_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( protein1Position == null ) {
			String msg = "Provided protein_1_position is null or protein_1_position is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( protein2Position == null ) {
			String msg = "Provided protein_2_position is null or protein_2_position is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		try {
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsCollection = new HashSet<Integer>( );
			projectSearchIdsCollection.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsCollection );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search id: " + projectSearchId;
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					//  No User session 
					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////

			Integer searchId =
					MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
			
			if ( searchId == null ) {
				String msg = ": No searchId found for projectSearchId: " + projectSearchId;
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
			    	        .build()
			    	        );
			}
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );

			searchIdsCollection.add( searchId );
			
			//   Get PSM and Peptide Cutoff data from JSON
			CutoffValuesSearchLevel cutoffValuesSearchLevel = 
					DeserializeCutoffForWebservices.getInstance().deserialize_JSON_ToCutoffSearchLevel( psmPeptideCutoffsForProjectSearchId_JSONString );
			
			//  Copy cutoff data to searcher cutoff data
			Z_CutoffValuesObjectsToOtherObjects_PerSearchResult z_CutoffValuesObjectsToOtherObjects_PerSearchResult = 
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesSearchLevel( 
							searchIdsCollection, cutoffValuesSearchLevel );
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = z_CutoffValuesObjectsToOtherObjects_PerSearchResult.getSearcherCutoffValuesSearchLevel();

			//    Get Peptide annotation type ids to include for display
			AnnTypeIdDisplayJSON_PerSearch annTypeIdDisplayJSON_PerSearch = null;
			if ( StringUtils.isNotEmpty( annTypeIdDisplayJSON_PerSearch_JSONString ) ) {
				annTypeIdDisplayJSON_PerSearch =
						DeserializeAnnTypeIdDisplayJSON_PerSearch.getInstance()
						.deserializeAnnTypeIdDisplayJSON_PerSearch( annTypeIdDisplayJSON_PerSearch_JSONString );
			}

			//  Exclude Links With User Selections:
			ExcludeLinksWith_JSONRoot excludeLinksWith_JSONRoot = null;
			if ( StringUtils.isNotEmpty( excludeLinksWith_Root_JSONString ) ) {
				excludeLinksWith_JSONRoot = 
						Z_Deserialize_ExcludeLinksWith_JSONRoot.getInstance()
						.deserialize_JSON_ToExcludeLinksWith_JSONRoot( excludeLinksWith_Root_JSONString );
			}
						
			//  Get Peptide data from DATABASE    from database
			List<SearchPeptideCrosslinkAnnDataWrapper> searchPeptideCrosslinkList = 
					SearchPeptideCrosslink_ForCrosslinkPeptideWS_Searcher.getInstance()
					.searchOnSearchProteinCrosslink( 
							projectSearchId, 
							searcherCutoffValuesSearchLevel, 
							protein1Id, 
							protein2Id, 
							protein1Position, 
							protein2Position );
			
			//  Get Annotation data for links
			GetCrosslinkReportedPeptidesServiceResult getCrosslinkReportedPeptidesServiceResult =
					getAnnotationDataAndSort( 
							searchPeptideCrosslinkList,
							searchId, 
							cutoffValuesSearchLevel,
							searcherCutoffValuesSearchLevel,
							annTypeIdDisplayJSON_PerSearch,
							excludeLinksWith_JSONRoot );
			
			return getCrosslinkReportedPeptidesServiceResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}

	/**
	 * @param searchPeptideCrosslinkWrappedList
	 * @param searchId
	 * @param cutoffValuesSearchLevel
	 * @param searcherCutoffValuesSearchLevel
	 * @param annTypeIdDisplayJSON_PerSearch
	 * @return
	 * @throws Exception
	 */
	private GetCrosslinkReportedPeptidesServiceResult getAnnotationDataAndSort(
			List<SearchPeptideCrosslinkAnnDataWrapper> searchPeptideCrosslinkWrappedList,
			int searchId,
			CutoffValuesSearchLevel cutoffValuesSearchLevel,
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel,
			AnnTypeIdDisplayJSON_PerSearch annTypeIdDisplayJSON_PerSearch,
			ExcludeLinksWith_JSONRoot excludeLinksWith_JSONRoot
			) throws Exception {
		
		List<Integer> peptideDisplayAnnTypeIdList = null;
		if ( annTypeIdDisplayJSON_PerSearch != null ) {
			peptideDisplayAnnTypeIdList = annTypeIdDisplayJSON_PerSearch.getPeptide();
		}

		if ( excludeLinksWith_JSONRoot != null && excludeLinksWith_JSONRoot.isRemoveNonUniquePSMs() ) {
			
			// Update to Remove non-unique PSMs from PSM counts and any Reported Peptides with zero PSMs after updating PSM count 
			
			List<SearchPeptideCrosslinkAnnDataWrapper> searchPeptideCrosslinkWrappedList_Filtered = new ArrayList<>( searchPeptideCrosslinkWrappedList.size() ); 
			
			for ( SearchPeptideCrosslinkAnnDataWrapper searchPeptideCrosslinkWrapped : searchPeptideCrosslinkWrappedList ) {
				SearchPeptideCrosslink searchPeptideCrosslink = searchPeptideCrosslinkWrapped.getSearchPeptideCrosslink();
				//  Update webReportedPeptide object to remove non-unique PSMs
				searchPeptideCrosslink.updateNumPsmsToNotInclude_NonUniquePSMs();
				if ( searchPeptideCrosslink.getNumPsms() <= 0 ) {
					// The number of PSMs after update is now zero
					//  Skip to next entry in list, dropping this entry from output list
					continue;  // EARLY CONTINUE
				}
				searchPeptideCrosslinkWrappedList_Filtered.add( searchPeptideCrosslinkWrapped );
			}
			searchPeptideCrosslinkWrappedList = searchPeptideCrosslinkWrappedList_Filtered;
			
		}
		
		//  Get Annotation Data for links and Sort Links
		SearchPeptideWebserviceCommonCodeGetDataResult searchPeptideWebserviceCommonCodeGetDataResult =
				SearchPeptideWebserviceCommonCode.getInstance()
				.getPeptideAndPSMDataForLinksAndSortLinks( 
						searchId, 
						searchPeptideCrosslinkWrappedList, 
						searcherCutoffValuesSearchLevel, 
						peptideDisplayAnnTypeIdList );
		
		//  Build output list of SearchPeptideCrosslinkWebserviceResult
		List<SearchPeptideCrosslinkWebserviceResult> searchPeptideCrosslinkWebserviceResultListOutput = new ArrayList<>( searchPeptideCrosslinkWrappedList.size() );
		for ( SearchPeptideCrosslinkAnnDataWrapper searchPeptideCrosslinkWrapped : searchPeptideCrosslinkWrappedList ) {
			SearchPeptideCrosslink searchPeptideCrosslink = searchPeptideCrosslinkWrapped.getSearchPeptideCrosslink();
			SearchPeptideCrosslinkWebserviceResult searchPeptideCrosslinkWebserviceResult = 
					new SearchPeptideCrosslinkWebserviceResult( searchPeptideCrosslink );
			//  Put Annotation data on the link
			SearchPeptideWebserviceCommonCode.getInstance()
			.putPeptideAndPSMDataOnWebserviceResultLinkOject( 
					searchPeptideWebserviceCommonCodeGetDataResult, 
					searchPeptideCrosslinkWrapped, 
					searchPeptideCrosslinkWebserviceResult);
			searchPeptideCrosslinkWebserviceResultListOutput.add( searchPeptideCrosslinkWebserviceResult );
		}
		
		//  Build result and return it
		GetCrosslinkReportedPeptidesServiceResult getCrosslinkReportedPeptidesServiceResult = new GetCrosslinkReportedPeptidesServiceResult();
		getCrosslinkReportedPeptidesServiceResult.setPeptideAnnotationDisplayNameDescriptionList( 
				searchPeptideWebserviceCommonCodeGetDataResult.getPeptideAnnotationDisplayNameDescriptionList() );
		getCrosslinkReportedPeptidesServiceResult.setPsmAnnotationDisplayNameDescriptionList( 
				searchPeptideWebserviceCommonCodeGetDataResult.getPsmAnnotationDisplayNameDescriptionList() );
		getCrosslinkReportedPeptidesServiceResult.setSearchPeptideCrosslinkList( searchPeptideCrosslinkWebserviceResultListOutput );
		return getCrosslinkReportedPeptidesServiceResult;
	}
}
