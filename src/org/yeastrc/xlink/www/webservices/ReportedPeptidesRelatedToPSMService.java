package org.yeastrc.xlink.www.webservices;

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
import org.yeastrc.xlink.dto.SearchReportedPeptideAnnotationDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.ReportedPeptidesRelatedToPSMServiceResult;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWebserviceWrapper;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideAnnotationDataSearcher;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.DeserializeCutoffForWebservices;




@Path("/reportedPeptidesRelatedToPSMService")
public class ReportedPeptidesRelatedToPSMService {

	private static final Logger log = Logger.getLogger(ReportedPeptidesRelatedToPSMService.class);
	
	

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/get") 
	public ReportedPeptidesRelatedToPSMServiceResult get( @QueryParam( "search_id" ) Integer searchId,
										  @QueryParam( "psmPeptideCutoffsForSearchId" ) String psmPeptideCutoffsForSearchId_JSONString,
										  @QueryParam( "psm_id" ) Integer psmId,
										  @QueryParam( "scan_id" ) Integer scanId,
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
			


			CutoffValuesSearchLevel cutoffValuesSearchLevel = 
					DeserializeCutoffForWebservices.getInstance().deserialize_JSON_ToCutoffSearchLevel( psmPeptideCutoffsForSearchId_JSONString );

			
			
			
			ReportedPeptidesRelatedToPSMServiceResult reportedPeptidesRelatedToPSMServiceResult = 
					getReportedPeptidesRelatedToPSMData( cutoffValuesSearchLevel, searchId, psmId, scanId , searchIdsCollection );

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
	 * @param searchId
	 * @param psmId
	 * @param scanId
	 * @param searcherCutoffValuesSearchLevel
	 * @return
	 * @throws Exception
	 */
	private ReportedPeptidesRelatedToPSMServiceResult getReportedPeptidesRelatedToPSMData( CutoffValuesSearchLevel cutoffValuesSearchLevel,
			Integer searchId,
			int psmId,
			int scanId,
			Collection<Integer> searchIdsCollection ) throws Exception {
		


		//  Get Annotation Type records for PSM and Peptide
		
		
		//  Get  Annotation Type records for PSM
		
		//    Filterable annotations
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgmFilterablePsmAnnotationTypeDTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIdsCollection );
		
		
		Map<Integer, AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOMap = 
				srchPgmFilterablePsmAnnotationTypeDTOListPerSearchIdMap.get( searchId );
		
		if ( srchPgmFilterablePsmAnnotationTypeDTOMap == null ) {
			
			//  No records were found, probably an error   TODO
			
			srchPgmFilterablePsmAnnotationTypeDTOMap = new HashMap<>();
		}
		
		
		
		/////////////

		//  Get  Annotation Type records for Reported Peptides
		
		Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgmFilterableReportedPeptideAnnotationTypeDTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Filterable_ForSearchIds( searchIdsCollection );
		
		
		Map<Integer, AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = 
				srchPgmFilterableReportedPeptideAnnotationTypeDTOListPerSearchIdMap.get( searchId );
		
		if ( srchPgmFilterableReportedPeptideAnnotationTypeDTOMap == null ) {
			
			//  No records were found, allowable for Reported Peptides
			
			srchPgmFilterableReportedPeptideAnnotationTypeDTOMap = new HashMap<>();
		}


		//    Descriptive annotations
		

		Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgmDescriptiveReportedPeptideAnnotationTypeDTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Peptide_Descriptive_ForSearchIds( searchIdsCollection );
		
		
		Map<Integer, AnnotationTypeDTO> srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap = 
				srchPgmDescriptiveReportedPeptideAnnotationTypeDTOListPerSearchIdMap.get( searchId );
		
		if ( srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap == null ) {
			
			//  No records were found, probably an error   TODO
			
			srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap = new HashMap<>();
		}
		
		

		//  Copy cutoff data to searcher cutoff data
		
		
		Z_CutoffValuesObjectsToOtherObjects_PerSearchResult z_CutoffValuesObjectsToOtherObjects_PerSearchResult = 
				Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesSearchLevel( 
						searchIdsCollection, cutoffValuesSearchLevel );
		

		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = z_CutoffValuesObjectsToOtherObjects_PerSearchResult.getSearcherCutoffValuesSearchLevel();
		
		
		//  Get Reported Peptides:
		
		List<WebReportedPeptideWebserviceWrapper> webReportedPeptideWebserviceWrapperList = 
				ReportedPeptidesForAssociatedScanId_From_PsmId_SearchId_Searcher.getInstance()
				.reportedPeptideRecordsForAssociatedScanId( psmId, scanId, searchId, searcherCutoffValuesSearchLevel ); 

		
		///////////
		
		ReportedPeptidesRelatedToPSMServiceResult reportedPeptidesRelatedToPSMServiceResult =
				getAnnotationDataAndSort( 
						searchId, 
						srchPgmFilterableReportedPeptideAnnotationTypeDTOMap, 
						srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap, 
						webReportedPeptideWebserviceWrapperList );
		
		
		
		return reportedPeptidesRelatedToPSMServiceResult;
	}
	
	
	
	
	/**
	 * @param searchId
	 * @param srchPgmFilterableReportedPeptideAnnotationTypeDTOMap
	 * @param srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap
	 * @param webReportedPeptideWebserviceWrapperList
	 * @return
	 * @throws Exception
	 */
	private ReportedPeptidesRelatedToPSMServiceResult getAnnotationDataAndSort(
			
			int searchId,
			Map<Integer, AnnotationTypeDTO> srchPgmFilterableReportedPeptideAnnotationTypeDTOMap,
			Map<Integer, AnnotationTypeDTO> srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap,
			List<WebReportedPeptideWebserviceWrapper> webReportedPeptideWebserviceWrapperList 
			
			) throws Exception {
		
		
		


		
		Set<Integer> annotationTypeIdsForAnnotationDataRetrieval = new HashSet<>(); 

		/////////////
		
		//   Create Annotation List for Sort Order and sort it on Sort order
		

		final List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_SortOrder_List =
				new ArrayList<>( srchPgmFilterableReportedPeptideAnnotationTypeDTOMap.size() );
		
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgmFilterableReportedPeptideAnnotationTypeDTOMap.entrySet() ) {
			
			AnnotationTypeDTO item = entry.getValue();
			
			annotationTypeIdsForAnnotationDataRetrieval.add( item.getId() );
			
			if ( item.getAnnotationTypeFilterableDTO() != null 
					&& item.getAnnotationTypeFilterableDTO().getSortOrder() != null ) {
				
				reportedPeptide_AnnotationTypeDTO_SortOrder_List.add( item );
			}
		}

		//  Sort Ann Type records on sort order
		
		
		Collections.sort( reportedPeptide_AnnotationTypeDTO_SortOrder_List, new Comparator<AnnotationTypeDTO>() {

			@Override
			public int compare(AnnotationTypeDTO o1, AnnotationTypeDTO o2) {

				return o1.getAnnotationTypeFilterableDTO().getSortOrder() - o2.getAnnotationTypeFilterableDTO().getSortOrder();
			}
		});
		
		
		/////////////
		
		//   Create Annotation List for Display Order and sort it on Display order
		
		
		final List<AnnotationTypeDTO> reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List =
				new ArrayList<>( srchPgmFilterableReportedPeptideAnnotationTypeDTOMap.size() + srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap.size() );
		
		for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgmFilterableReportedPeptideAnnotationTypeDTOMap.entrySet() ) {
			
			AnnotationTypeDTO item = entry.getValue();
			
			annotationTypeIdsForAnnotationDataRetrieval.add( item.getId() );
			
			if ( item.isDefaultVisible() ) {
				
				reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.add( item );
			}
		}

		for ( Map.Entry<Integer, AnnotationTypeDTO> entry : srchPgmDescriptiveReportedPeptideAnnotationTypeDTOMap.entrySet() ) {
			
			AnnotationTypeDTO item = entry.getValue();
			
			annotationTypeIdsForAnnotationDataRetrieval.add( item.getId() );
			
			if ( item.isDefaultVisible() ) {
				
				reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.add( item );
			}
		}
		

		//  Sort Ann type records on display order
		
		
		Collections.sort( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List, new Comparator<AnnotationTypeDTO>() {

			@Override
			public int compare(AnnotationTypeDTO o1, AnnotationTypeDTO o2) {

				return o1.getDisplayOrder() - o2.getDisplayOrder();
			}
		});
		

		
		

		
		//  Get Annotation data
		
		//  Internal holder
		
		List<InternalReportedPeptideWebDisplayHolder> reportedPeptideWebDisplayHolder = new ArrayList<>( webReportedPeptideWebserviceWrapperList.size() );
		
		for ( WebReportedPeptideWebserviceWrapper webReportedPeptideWebserviceWrapperItem : webReportedPeptideWebserviceWrapperList ) {
		
			int reportedPeptideId = webReportedPeptideWebserviceWrapperItem.getReportedPeptide_Id();

			Map<Integer, SearchReportedPeptideAnnotationDTO> annotationDTOMapOnTypeId = new HashMap<>();


			//  Process annotation type ids to get annotation data

			{
				List<SearchReportedPeptideAnnotationDTO> searchReportedPeptideAnnotationDataList = 
						SearchReportedPeptideAnnotationDataSearcher.getInstance().getSearchReportedPeptideAnnotationDTOList( searchId, reportedPeptideId, annotationTypeIdsForAnnotationDataRetrieval );

				for ( SearchReportedPeptideAnnotationDTO searchReportedPeptideAnnotationDataItem : searchReportedPeptideAnnotationDataList ) {

					annotationDTOMapOnTypeId.put( searchReportedPeptideAnnotationDataItem.getAnnotationTypeId(), searchReportedPeptideAnnotationDataItem );
				}
			}

			InternalReportedPeptideWebDisplayHolder internalReportedPeptideWebDisplayHolder = new InternalReportedPeptideWebDisplayHolder();

			internalReportedPeptideWebDisplayHolder.webReportedPeptideWebserviceWrapper = webReportedPeptideWebserviceWrapperItem;

			internalReportedPeptideWebDisplayHolder.annotationDTOMapOnTypeId = annotationDTOMapOnTypeId;

			reportedPeptideWebDisplayHolder.add( internalReportedPeptideWebDisplayHolder );
		}



		//  Sort Peptide records on sort order

		Collections.sort( reportedPeptideWebDisplayHolder, new Comparator<InternalReportedPeptideWebDisplayHolder>() {

			@Override
			public int compare(InternalReportedPeptideWebDisplayHolder o1, InternalReportedPeptideWebDisplayHolder o2) {

				//  Loop through the annotation types (sorted on sort order), comparing the values

				for ( AnnotationTypeDTO srchPgmFilterableReportedPeptideAnnotationTypeDTO : reportedPeptide_AnnotationTypeDTO_SortOrder_List ) {

					int typeId = srchPgmFilterableReportedPeptideAnnotationTypeDTO.getId();

					SearchReportedPeptideAnnotationDTO o1_SearchReportedPeptideAnnotationDTO = o1.annotationDTOMapOnTypeId.get( typeId );
					if ( o1_SearchReportedPeptideAnnotationDTO == null ) {

						String msg = "Unable to get Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}

					double o1Value = o1_SearchReportedPeptideAnnotationDTO.getValueDouble();


					SearchReportedPeptideAnnotationDTO o2_SearchReportedPeptideAnnotationDTO = o2.annotationDTOMapOnTypeId.get( typeId );
					if ( o2_SearchReportedPeptideAnnotationDTO == null ) {

						String msg = "Unable to get Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}

					double o2Value = o2_SearchReportedPeptideAnnotationDTO.getValueDouble();

					if ( o1Value != o2Value ) {

						if ( o1Value < o2Value ) {

							return -1;
						} else {
							return 1;
						}
					}

				}

				//  If everything matches, sort on reported peptide id

				try {
					return o1.webReportedPeptideWebserviceWrapper.getReportedPeptide_Id() - o2.webReportedPeptideWebserviceWrapper.getReportedPeptide_Id();
				} catch (Exception e) {
					
					throw new RuntimeException( e );
				}
			}
		});


		//  Build output list of ReportedPeptideWebDisplay

		List<WebReportedPeptideWebserviceWrapper> webReportedPeptideWebserviceWrapperListOutput = new ArrayList<>( reportedPeptideWebDisplayHolder.size() );

		for ( InternalReportedPeptideWebDisplayHolder internalReportedPeptideWebDisplayHolder : reportedPeptideWebDisplayHolder ) {


			//  Get annotations

			List<String> annotationValues = new ArrayList<>( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.size() );


			for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {

				SearchReportedPeptideAnnotationDTO psmFilterableAnnotationDTO = 
						internalReportedPeptideWebDisplayHolder.annotationDTOMapOnTypeId.get( annotationTypeDTO.getId() );

				if ( psmFilterableAnnotationDTO == null ) {

					String msg = "ERROR.  Cannot AnnotationDTO for type id: " + annotationTypeDTO.getId();
					log.error( msg );
					throw new Exception(msg);
				}

				annotationValues.add( psmFilterableAnnotationDTO.getValueString() );
			}


			WebReportedPeptideWebserviceWrapper webReportedPeptideWebserviceWrapper = internalReportedPeptideWebDisplayHolder.webReportedPeptideWebserviceWrapper;

			webReportedPeptideWebserviceWrapper.setAnnotationValues( annotationValues );


			webReportedPeptideWebserviceWrapperListOutput.add( webReportedPeptideWebserviceWrapper );
		}







		//  Get column headers data

		List<AnnotationDisplayNameDescription> annotationDisplayNameDescriptionList = 
				new ArrayList<>( reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List.size() );


		for ( AnnotationTypeDTO annotationTypeDTO : reportedPeptide_AnnotationTypeDTO_DefaultDisplay_List ) {

			AnnotationDisplayNameDescription annotationDisplayNameDescription = new AnnotationDisplayNameDescription();

			annotationDisplayNameDescription.setDisplayName( annotationTypeDTO.getName() );
			annotationDisplayNameDescription.setDescription( annotationTypeDTO.getDescription() );

			annotationDisplayNameDescriptionList.add( annotationDisplayNameDescription );
		}


		///////////////

		ReportedPeptidesRelatedToPSMServiceResult reportedPeptidesRelatedToPSMServiceResult = new ReportedPeptidesRelatedToPSMServiceResult();

		reportedPeptidesRelatedToPSMServiceResult.setAnnotationDisplayNameDescriptionList( annotationDisplayNameDescriptionList );
		reportedPeptidesRelatedToPSMServiceResult.setWebReportedPeptideWebserviceWrapperList( webReportedPeptideWebserviceWrapperList );

		return reportedPeptidesRelatedToPSMServiceResult;
	}
	


	/**
	 * 
	 *
	 */
	private static class InternalReportedPeptideWebDisplayHolder {
		
		WebReportedPeptideWebserviceWrapper webReportedPeptideWebserviceWrapper;
		
		Map<Integer, SearchReportedPeptideAnnotationDTO> annotationDTOMapOnTypeId;
		
	}
	
}
