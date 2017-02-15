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
import org.yeastrc.xlink.www.objects.GetMonolinkReportedPeptidesServiceResult;
import org.yeastrc.xlink.www.objects.SearchPeptideMonolink;
import org.yeastrc.xlink.www.objects.SearchPeptideMonolinkAnnDataWrapper;
import org.yeastrc.xlink.www.objects.SearchPeptideMonolinkWebserviceResult;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchPeptideMonolink_LinkedPosition_Searcher;
import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.annotation_display.DeserializeAnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
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
 * Get Monolink Reported Peptide 
 *
 */
@Path("/data")
public class ReportedPeptides_Monolink_Service {

	private static final Logger log = Logger.getLogger(ReportedPeptides_Monolink_Service.class);
	
	/**
	 * @param searchId
	 * @param psmPeptideCutoffsForProjectSearchId_JSONString
	 * @param annTypeIdDisplayJSON_PerSearch_JSONString
	 * @param proteinId
	 * @param proteinPosition
	 * @param request
	 * @return
	 * @throws WebApplicationException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getMonolinkReportedPeptides") 
	public GetMonolinkReportedPeptidesServiceResult getMonolinkReportedPeptides( 
			@QueryParam( "project_search_id" ) Integer projectSearchId,
			@QueryParam( "psmPeptideCutoffsForProjectSearchId" ) String psmPeptideCutoffsForProjectSearchId_JSONString,
			@QueryParam( "peptideAnnTypeDisplayPerSearch" ) String annTypeIdDisplayJSON_PerSearch_JSONString,
			@QueryParam( "protein_id" ) Integer proteinId,
			@QueryParam( "protein_position" ) Integer proteinPosition,
			@Context HttpServletRequest request )
	throws WebApplicationException {

		if ( projectSearchId == null ) {
			String msg = "Provided project_search_id is null or project_search_id is missing";
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
		if ( proteinId == null ) {
			String msg = "Provided protein_id is null or protein_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( proteinPosition == null ) {
			String msg = "Provided protein_position is null or protein_position is missing";
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
			//  Test access to the project id
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

			SearchDTO searchDTO = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			
			if ( searchDTO == null ) {
				String msg = ": No search found for projectSearchId: " + projectSearchId;
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
			    	        .build()
			    	        );
			}
			
			Integer searchId = searchDTO.getSearchId();
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			searchIdsCollection.add( searchId );
			
			//   Get PSM and Peptide Cutoff data from JSON
			CutoffValuesSearchLevel cutoffValuesSearchLevel = 
					DeserializeCutoffForWebservices.getInstance().deserialize_JSON_ToCutoffSearchLevel( psmPeptideCutoffsForProjectSearchId_JSONString );
			//////////////
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
			
			//  Get Peptide data
			List<SearchPeptideMonolinkAnnDataWrapper> searchPeptideMonolinkList = 
					SearchPeptideMonolink_LinkedPosition_Searcher.getInstance()
					.searchOnSearchProteinMonolink( 
							searchDTO, 
							searcherCutoffValuesSearchLevel, 
							proteinId, 
							proteinPosition );
			
			GetMonolinkReportedPeptidesServiceResult getMonolinkReportedPeptidesServiceResult =
					getAnnotationDataAndSort(
							searchPeptideMonolinkList,
							searchId, 
							cutoffValuesSearchLevel,
							searcherCutoffValuesSearchLevel,
							annTypeIdDisplayJSON_PerSearch
							 );
			
			return getMonolinkReportedPeptidesServiceResult;
			
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
	 * @param searchPeptideMonolinkWrappedList
	 * @param searchId
	 * @param cutoffValuesSearchLevel
	 * @param searcherCutoffValuesSearchLevel
	 * @param annTypeIdDisplayJSON_PerSearch
	 * @return
	 * @throws Exception
	 */
	private GetMonolinkReportedPeptidesServiceResult getAnnotationDataAndSort(
			List<SearchPeptideMonolinkAnnDataWrapper> searchPeptideMonolinkWrappedList, 
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
						searchPeptideMonolinkWrappedList, 
						searcherCutoffValuesSearchLevel, 
						peptideDisplayAnnTypeIdList );
		
		//  Build output list of ReportedPeptideWebDisplay
		List<SearchPeptideMonolinkWebserviceResult> searchPeptideMonolinkListOutput = new ArrayList<>( searchPeptideMonolinkWrappedList.size() );
		for ( SearchPeptideMonolinkAnnDataWrapper searchPeptideMonolinkWrapped : searchPeptideMonolinkWrappedList ) {
			SearchPeptideMonolink searchPeptideMonolink = searchPeptideMonolinkWrapped.getSearchPeptideMonolink();
			SearchPeptideMonolinkWebserviceResult searchPeptideMonolinkWebserviceResult = 
					new SearchPeptideMonolinkWebserviceResult( searchPeptideMonolink );
			//  Put Annotation data on the link
			SearchPeptideWebserviceCommonCode.getInstance()
			.putPeptideAndPSMDataOnWebserviceResultLinkOject( 
					searchPeptideWebserviceCommonCodeGetDataResult, 
					searchPeptideMonolinkWrapped, 
					searchPeptideMonolinkWebserviceResult );
			searchPeptideMonolinkListOutput.add( searchPeptideMonolinkWebserviceResult );
		}
		
		///////////////
		GetMonolinkReportedPeptidesServiceResult getMonolinkReportedPeptidesServiceResult = new GetMonolinkReportedPeptidesServiceResult();
		getMonolinkReportedPeptidesServiceResult.setPeptideAnnotationDisplayNameDescriptionList( 
				searchPeptideWebserviceCommonCodeGetDataResult.getPeptideAnnotationDisplayNameDescriptionList() );
		getMonolinkReportedPeptidesServiceResult.setPsmAnnotationDisplayNameDescriptionList( 
				searchPeptideWebserviceCommonCodeGetDataResult.getPsmAnnotationDisplayNameDescriptionList() );
		getMonolinkReportedPeptidesServiceResult.setSearchPeptideMonolinkList( searchPeptideMonolinkListOutput );
		
		return getMonolinkReportedPeptidesServiceResult;	
	}
}
