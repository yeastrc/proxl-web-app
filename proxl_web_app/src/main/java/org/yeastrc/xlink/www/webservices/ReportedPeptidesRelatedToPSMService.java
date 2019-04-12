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
import org.yeastrc.xlink.www.objects.ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem;
import org.yeastrc.xlink.www.objects.ReportedPeptidesRelatedToPSMServiceResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWebserviceWrapper;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher;
import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.annotation_display.DeserializeAnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.DeserializeCutoffForWebservices;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode.SearchPeptideWebserviceCommonCodeGetDataResult;

/**
 * Webservice to display the Reported Peptides related to a PSM that is not unique (Other PSMs are related to the same scan) 
 * 
 * Not updated with @QueryParam( "excludeLinksWith_Root" ) String excludeLinksWith_Root_JSONString,
 * since not reachable when PSMs excluded for non-unique.
 * 
 *  Also not apply other exclusions since they are not used when determining if PSMs are non-unique
 *
 */
@Path("/reportedPeptidesRelatedToPSMService")
public class ReportedPeptidesRelatedToPSMService {

	private static final Logger log = LoggerFactory.getLogger( ReportedPeptidesRelatedToPSMService.class);
	
	/**
	 * @param searchId
	 * @param psmPeptideCutoffsForProjectSearchId_JSONString
	 * @param annTypeIdDisplayJSON_PerSearch_JSONString
	 * @param psmId
	 * @param scanId
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get") 
	public ReportedPeptidesRelatedToPSMServiceResult get( @QueryParam( "project_search_id" ) Integer projectSearchId,
										  @QueryParam( "psmPeptideCutoffsForProjectSearchId" ) String psmPeptideCutoffsForProjectSearchId_JSONString,
										  @QueryParam( "peptideAnnTypeDisplayPerSearch" ) String annTypeIdDisplayJSON_PerSearch_JSONString,
										  @QueryParam( "psm_id" ) Integer psmId,
										  @QueryParam( "scan_id" ) Integer scanId,
										  @Context HttpServletRequest request )
	throws Exception {

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
		if ( psmId == null ) {
			String msg = "Provided psm_id is null or psm_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		if ( scanId == null ) {
			String msg = "Provided scan_id is null or scan_id is missing";
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}		
		try {
//			if ( searchIds.isEmpty() ) {
//				
//				throw new WebApplicationException(
//						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
//						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
//						.build()
//						);
//			}
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
			}			if ( projectIdsFromSearchIds.size() > 1 ) {
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
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
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

			//    Get Peptide annotation type ids to include for display
			AnnTypeIdDisplayJSON_PerSearch annTypeIdDisplayJSON_PerSearch = null;
			if ( StringUtils.isNotEmpty( annTypeIdDisplayJSON_PerSearch_JSONString ) ) {
				annTypeIdDisplayJSON_PerSearch =
						DeserializeAnnTypeIdDisplayJSON_PerSearch.getInstance()
						.deserializeAnnTypeIdDisplayJSON_PerSearch( annTypeIdDisplayJSON_PerSearch_JSONString );
			}
			
			//  Get Result
			ReportedPeptidesRelatedToPSMServiceResult reportedPeptidesRelatedToPSMServiceResult = 
					getReportedPeptidesRelatedToPSMData( 
							cutoffValuesSearchLevel, 
							annTypeIdDisplayJSON_PerSearch, 
							searchDTO, 
							psmId, 
							scanId , 
							searchIdsCollection );
			
			return reportedPeptidesRelatedToPSMServiceResult;
			
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
	 * @param cutoffValuesSearchLevel
	 * @param annTypeIdDisplayJSON_PerSearch
	 * @param searchId
	 * @param psmId
	 * @param scanId
	 * @param searchIdsCollection
	 * @return
	 * @throws Exception
	 */
	private ReportedPeptidesRelatedToPSMServiceResult getReportedPeptidesRelatedToPSMData( 
			CutoffValuesSearchLevel cutoffValuesSearchLevel,
			AnnTypeIdDisplayJSON_PerSearch annTypeIdDisplayJSON_PerSearch,
			SearchDTO searchDTO,
			int psmId,
			int scanId,
			Collection<Integer> searchIdsCollection ) throws Exception {

		int searchId = searchDTO.getSearchId();
		
		List<Integer> peptideDisplayAnnTypeIdList = null;
		if ( annTypeIdDisplayJSON_PerSearch != null ) {
			peptideDisplayAnnTypeIdList = annTypeIdDisplayJSON_PerSearch.getPeptide();
		}
		
		//  Copy cutoff data to searcher cutoff data
		Z_CutoffValuesObjectsToOtherObjects_PerSearchResult z_CutoffValuesObjectsToOtherObjects_PerSearchResult = 
				Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesSearchLevel( 
						searchIdsCollection, cutoffValuesSearchLevel );
		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = z_CutoffValuesObjectsToOtherObjects_PerSearchResult.getSearcherCutoffValuesSearchLevel();
		
		//  Get Reported Peptides From DB:
		List<ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem> reportedPeptideDBList = 
				ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher.getInstance()
				.reportedPeptideRecordsForAssociatedScanId( psmId, scanId, searchDTO, searcherCutoffValuesSearchLevel ); 
		
		//  Get Annotation Data for links and Sort Links
		SearchPeptideWebserviceCommonCodeGetDataResult searchPeptideWebserviceCommonCodeGetDataResult =
				SearchPeptideWebserviceCommonCode.getInstance()
				.getPeptideAndPSMDataForLinksAndSortLinks( 
						searchId, 
						reportedPeptideDBList, 
						searcherCutoffValuesSearchLevel, 
						peptideDisplayAnnTypeIdList );
		
		List<WebReportedPeptideWebserviceWrapper> webReportedPeptideWebserviceWrapperList = new ArrayList<>( reportedPeptideDBList.size() );
		
		for ( ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_SearcherResultItem dbItem : reportedPeptideDBList ) {

			WebReportedPeptideWebserviceWrapper webReportedPeptideWebserviceWrapper = new WebReportedPeptideWebserviceWrapper();
			webReportedPeptideWebserviceWrapper.setWebReportedPeptide( dbItem.getWebReportedPeptide() );
			//  Put Annotation data on the link
			SearchPeptideWebserviceCommonCode.getInstance()
			.putPeptideAndPSMDataOnWebserviceResultLinkOject( 
					searchPeptideWebserviceCommonCodeGetDataResult, 
					dbItem, 
					webReportedPeptideWebserviceWrapper);
			webReportedPeptideWebserviceWrapper.setWebReportedPeptide( dbItem.getWebReportedPeptide() );
			webReportedPeptideWebserviceWrapperList.add( webReportedPeptideWebserviceWrapper );
		}
		
		ReportedPeptidesRelatedToPSMServiceResult reportedPeptidesRelatedToPSMServiceResult = new ReportedPeptidesRelatedToPSMServiceResult();
		reportedPeptidesRelatedToPSMServiceResult.setPeptideAnnotationDisplayNameDescriptionList( searchPeptideWebserviceCommonCodeGetDataResult.getPeptideAnnotationDisplayNameDescriptionList() );
		reportedPeptidesRelatedToPSMServiceResult.setPsmAnnotationDisplayNameDescriptionList( searchPeptideWebserviceCommonCodeGetDataResult.getPsmAnnotationDisplayNameDescriptionList() );
		reportedPeptidesRelatedToPSMServiceResult.setWebReportedPeptideWebserviceWrapperList( webReportedPeptideWebserviceWrapperList );
		return reportedPeptidesRelatedToPSMServiceResult;
	}

}
