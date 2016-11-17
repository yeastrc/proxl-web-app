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
import org.apache.log4j.Logger;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.GetCrosslinkReportedPeptidesServiceResult;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslinkAnnDataWrapper;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslinkWebserviceResult;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchPeptideCrosslink_ForCrosslinkPeptideWS_Searcher;
import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.annotation_display.DeserializeAnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.DeserializeCutoffForWebservices;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode.SearchPeptideWebserviceCommonCodeGetDataResult;


/**
 * 
 *
 */
@Path("/data")
public class ReportedPeptides_Crosslink_Service {
	
	private static final Logger log = Logger.getLogger(ReportedPeptides_Crosslink_Service.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCrosslinkReportedPeptides") 
	public GetCrosslinkReportedPeptidesServiceResult getCrosslinkReportedPeptides( 
			@QueryParam( "search_id" ) Integer searchId,
			@QueryParam( "psmPeptideCutoffsForSearchId" ) String psmPeptideCutoffsForSearchId_JSONString,
			@QueryParam( "peptideAnnTypeDisplayPerSearch" ) String annTypeIdDisplayJSON_PerSearch_JSONString,
			@QueryParam( "protein_1_id" ) Integer protein1Id,
			@QueryParam( "protein_2_id" ) Integer protein2Id,
			@QueryParam( "protein_1_position" ) Integer protein1Position,
			@QueryParam( "protein_2_position" ) Integer protein2Position,
			@Context HttpServletRequest request )
	throws Exception {

		if ( searchId == null ) {
			String msg = "Provided search_id is null or search_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( psmPeptideCutoffsForSearchId_JSONString ) ) {
			String msg = "Provided psmPeptideCutoffsForSearchId is null or psmPeptideCutoffsForSearchId is missing";
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
			// Get the session first.  
//			HttpSession session = request.getSession();
//			if ( searchIds.isEmpty() ) {
//				
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
			//   Get the project id for this search
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			searchIdsCollection.add( searchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search id: " + searchId;
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
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			//  Test access to the project id
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			////////   Auth complete
			//////////////////////////////////////////
			
			//   Get PSM and Peptide Cutoff data from JSON
			CutoffValuesSearchLevel cutoffValuesSearchLevel = 
					DeserializeCutoffForWebservices.getInstance().deserialize_JSON_ToCutoffSearchLevel( psmPeptideCutoffsForSearchId_JSONString );
			
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
			
			//  Get Peptide data from DATABASE    from database
			List<SearchPeptideCrosslinkAnnDataWrapper> searchPeptideCrosslinkList = 
					SearchPeptideCrosslink_ForCrosslinkPeptideWS_Searcher.getInstance()
					.searchOnSearchProteinCrosslink( 
							searchId, searcherCutoffValuesSearchLevel, 
							protein1Id, protein2Id, protein1Position, protein2Position );
			
			//  Get Annotation data for links
			GetCrosslinkReportedPeptidesServiceResult getCrosslinkReportedPeptidesServiceResult =
					getAnnotationDataAndSort( 
							searchPeptideCrosslinkList,
							searchId, 
							cutoffValuesSearchLevel,
							searcherCutoffValuesSearchLevel,
							annTypeIdDisplayJSON_PerSearch );
			
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
			AnnTypeIdDisplayJSON_PerSearch annTypeIdDisplayJSON_PerSearch
			) throws Exception {
		
		List<Integer> peptideDisplayAnnTypeIdList = null;
		if ( annTypeIdDisplayJSON_PerSearch != null ) {
			peptideDisplayAnnTypeIdList = annTypeIdDisplayJSON_PerSearch.getPeptide();
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
