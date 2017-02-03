package org.yeastrc.xlink.www.webservices;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ReportedPeptidesForMergedPeptidePage;
import org.yeastrc.xlink.www.objects.ReportedPeptidesForMergedPeptidePageWrapper;
import org.yeastrc.xlink.www.objects.ReportedPeptidesPerSearchForMergedPeptidePageResult;
import org.yeastrc.xlink.www.objects.ReportedPeptidesPerSearchForMergedPeptidePageResultEntry;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher;
import org.yeastrc.xlink.www.searcher.ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher;
import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSONRoot;
import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.annotation_display.DeserializeAnnTypeIdDisplayJSONRoot;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode.SearchPeptideWebserviceCommonCodeGetDataResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Path("/data")
public class ReportedPeptidesForUnifiedPeptIdMergedPeptidePageService {

	private static final Logger log = Logger.getLogger(ReportedPeptidesForUnifiedPeptIdMergedPeptidePageService.class);
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getReportedPeptidesForUnifiedPeptId") 
	public ReportedPeptidesPerSearchForMergedPeptidePageResult getViewerData( 
			@QueryParam( "search_ids" ) List<Integer> projectSearchIdList,
			@QueryParam( "unified_reported_peptide_id" ) Integer unifiedReportedPeptideId,
			@QueryParam( "psmPeptideCutoffsForSearchIds" ) String psmPeptideCutoffsForSearchIds_JSONString,
			@QueryParam( "annTypeDisplay" ) String annTypeDisplay_JSONString,
			@Context HttpServletRequest request )
					throws Exception {
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided searchIds is null or searchIds is missing or searchIds list is empty";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		if ( unifiedReportedPeptideId == null ) {
			String msg = "Provided unified_reported_peptide_id is null or unified_reported_peptide_id is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		if ( StringUtils.isEmpty( psmPeptideCutoffsForSearchIds_JSONString ) ) {
			String msg = "Provided psmPeptideCutoffsForSearchIds is null or psmPeptideCutoffsForSearchIds is missing";
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
			Set<Integer> projectSearchIdsCollection = new HashSet<Integer>( );
			projectSearchIdsCollection.addAll( projectSearchIdList );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsCollection );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int projectSearchId : projectSearchIdList ) {
					msg += projectSearchId + ", ";
				}				
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
			
			//   Get PSM and Peptide Cutoff data from JSON
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization
			CutoffValuesRootLevel cutoffValuesRootLevel = null;
			try {
				cutoffValuesRootLevel = jacksonJSON_Mapper.readValue( psmPeptideCutoffsForSearchIds_JSONString, CutoffValuesRootLevel.class );
			} catch ( JsonParseException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForSearchIds_JSONString', JsonParseException.  psmPeptideCutoffsForSearchIds_JSONString: " + psmPeptideCutoffsForSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			} catch ( JsonMappingException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForSearchIds_JSONString', JsonMappingException.  psmPeptideCutoffsForSearchIds_JSONString: " + psmPeptideCutoffsForSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			} catch ( IOException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForSearchIds_JSONString', IOException.  psmPeptideCutoffsForSearchIds_JSONString: " + psmPeptideCutoffsForSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			}
			
			//    Get PSM annotation type ids to include or exclude from display
			
			AnnTypeIdDisplayJSONRoot annTypeIdDisplayRoot = null;
			if ( StringUtils.isNotEmpty( annTypeDisplay_JSONString ) ) {
				annTypeIdDisplayRoot =
						DeserializeAnnTypeIdDisplayJSONRoot.getInstance().deserializeAnnTypeIdDisplayJSONRoot( annTypeDisplay_JSONString );
			}
			
			ReportedPeptidesPerSearchForMergedPeptidePageResult results =
					getPeptideData( cutoffValuesRootLevel, annTypeIdDisplayRoot, projectSearchIdsCollection, unifiedReportedPeptideId );
			return results;
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
	 * @param cutoffValuesRootLevel
	 * @param searchIdsCollection
	 * @param unifiedReportedPeptideId
	 * @return
	 * @throws Exception
	 */
	private ReportedPeptidesPerSearchForMergedPeptidePageResult getPeptideData( 
			CutoffValuesRootLevel cutoffValuesRootLevel,
			AnnTypeIdDisplayJSONRoot annTypeIdDisplayRoot,
			Set<Integer> projectSearchIdsSet,
			int unifiedReportedPeptideId ) throws Exception {

		Set<Integer> searchIdsSet = new HashSet<>( projectSearchIdsSet.size() );
		List<SearchDTO> searchList = new ArrayList<>( projectSearchIdsSet.size() );
		
		for ( Integer projectSearchId : projectSearchIdsSet ) {
			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			if ( search == null ) {
				String msg = ": No search found for projectSearchId: " + projectSearchId;
				log.warn( msg );
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
			    	        .build()
			    	        );
			}
			Integer searchId = search.getSearchId();
			searchIdsSet.add( searchId );
			searchList.add( search );
		}
		
		Collections.sort( searchList, new Comparator<SearchDTO>() {
			@Override
			public int compare(SearchDTO o1, SearchDTO o2) {
				return o1.getSearchId() - o2.getSearchId();
			}
		});

		//  Main part of returned result
		Map<Integer, ReportedPeptidesPerSearchForMergedPeptidePageResultEntry> reportedPeptidesPerSearchIdMap = new HashMap<>();
		
		Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
				Z_CutoffValuesObjectsToOtherObjectsFactory
				.createSearcherCutoffValuesRootLevel( searchIdsSet, cutoffValuesRootLevel );
		
		SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
				cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
		
		//  Process for each search id:
		for ( SearchDTO search : searchList ) {
			
			int eachProjectSearchIdToProcess = search.getProjectSearchId();
			int eachSearchIdToProcess = search.getSearchId();
			
			String searchIdAsString = Integer.toString( eachSearchIdToProcess );
			
			List<ReportedPeptidesForMergedPeptidePage> reportedPepidesListOutput = new ArrayList<>();

			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel =
					searcherCutoffValuesRootLevel.getPerSearchCutoffs( eachProjectSearchIdToProcess );
			if ( searcherCutoffValuesSearchLevel == null ) {
				String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + eachProjectSearchIdToProcess;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE)  //  return 400 error
			    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT + msg )
			    	        .build()
			    	        );
			}

			////////////////////
			//  Get User Selected Peptide Annotation type ids to display
			List<Integer> peptideAnnotationTypeIdsToDisplay = null;
			if ( annTypeIdDisplayRoot != null && annTypeIdDisplayRoot.getSearches() != null ) {
				AnnTypeIdDisplayJSON_PerSearch annTypeIdDisplayJSON_PerSearch =
						annTypeIdDisplayRoot.getSearches().get( searchIdAsString );
				if ( annTypeIdDisplayJSON_PerSearch != null ) {
					peptideAnnotationTypeIdsToDisplay = annTypeIdDisplayJSON_PerSearch.getPeptide();
				}
			}
			
			//  First get list of reported peptide ids for unifiedReportedPeptideId and search id
			List<Integer>  reportedPeptideIdList = 
					ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher.getInstance()
					.getReportedPeptideIdsForSearchIdsAndUnifiedReportedPeptideId( eachSearchIdToProcess, unifiedReportedPeptideId );
			
			//  Sort on reported peptide id
			Collections.sort( reportedPeptideIdList );
			SearchPeptideWebserviceCommonCodeGetDataResult searchPeptideWebserviceCommonCodeGetDataResult = null;
			
			//  Process each search id, reported peptide id pair
			for ( int reportedPeptideId : reportedPeptideIdList ) {
				//  Get Peptide data
				List<ReportedPeptidesForMergedPeptidePageWrapper> peptideWebDisplayList = 
						ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher.getInstance().getReportedPeptidesWebDisplay( search, eachSearchIdToProcess, reportedPeptideId, searcherCutoffValuesSearchLevel );

				//  Get Annotation Data for links and Sort Links
				searchPeptideWebserviceCommonCodeGetDataResult =
						SearchPeptideWebserviceCommonCode.getInstance()
						.getPeptideAndPSMDataForLinksAndSortLinks( 
								eachSearchIdToProcess, 
								peptideWebDisplayList, 
								searcherCutoffValuesSearchLevel, 
								peptideAnnotationTypeIdsToDisplay );

				//  Copy the links out of the wrappers for output - and Copy searched for peptide and psm annotations to link

				//  Get Peptide Annotation data
				for ( ReportedPeptidesForMergedPeptidePageWrapper webDisplayItemWrapper : peptideWebDisplayList ) {
					
					ReportedPeptidesForMergedPeptidePage webDisplayItem = webDisplayItemWrapper.getReportedPeptidesForMergedPeptidePage();
					
					//  Put Annotation data on the link
					SearchPeptideWebserviceCommonCode.getInstance()
					.putPeptideAndPSMDataOnWebserviceResultLinkOject( 
							searchPeptideWebserviceCommonCodeGetDataResult, 
							webDisplayItemWrapper, // input
							webDisplayItem );  // updated output

					reportedPepidesListOutput.add( webDisplayItem );
					
				}  //  END:   for ( ReportedPeptidesForMergedPeptidePage webDisplayItem : peptideWebDisplayList ) {
			}   //   END  for ( ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult item : resultList ) {
			
			if ( ! reportedPepidesListOutput.isEmpty() ) {
				
				//   Only add entry to per searches map if there are records for that search
				ReportedPeptidesPerSearchForMergedPeptidePageResultEntry serviceResultEntry = new ReportedPeptidesPerSearchForMergedPeptidePageResultEntry();
				serviceResultEntry.setPeptideAnnotationDisplayNameDescriptionList( searchPeptideWebserviceCommonCodeGetDataResult.getPeptideAnnotationDisplayNameDescriptionList() );
				serviceResultEntry.setPsmAnnotationDisplayNameDescriptionList( searchPeptideWebserviceCommonCodeGetDataResult.getPsmAnnotationDisplayNameDescriptionList() );
				serviceResultEntry.setReportedPepides( reportedPepidesListOutput );
				reportedPeptidesPerSearchIdMap.put( eachProjectSearchIdToProcess, serviceResultEntry );
			}
		}  //  END:  for each search id
		
		///////////
		
		ReportedPeptidesPerSearchForMergedPeptidePageResult serviceResult = new ReportedPeptidesPerSearchForMergedPeptidePageResult();
		serviceResult.setReportedPeptidesPerSearchIdMap( reportedPeptidesPerSearchIdMap );
		return serviceResult;
	}
	
}
