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
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AnnotationTypeDTOListForSearchId;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.PsmsServiceResult;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmAnnotationDataSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataDefaultDisplayInDisplayOrder;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataInSortOrder;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.DeserializeCutoffForWebservices;




@Path("/data")
public class PsmsService {

	private static final Logger log = Logger.getLogger(PsmsService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getPsms") 
	public PsmsServiceResult getViewerData( @QueryParam( "search_id" ) Integer searchId,
										  @QueryParam( "project_id" ) Integer projectIdParam,
										  @QueryParam( "reported_peptide_id" ) Integer reportedPeptideId,
										  @QueryParam( "psmPeptideCutoffsForSearchId" ) String psmPeptideCutoffsForSearchId_JSONString,
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
		
		if ( reportedPeptideId == null ) {

			String msg = "Provided reported_peptide_id is null or reported_peptide_id is missing";

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

			
			
			//  Get PSMs for cutoffs and other data
			
			PsmsServiceResult psmsServiceResult = getPsmData( cutoffValuesSearchLevel, searchId, reportedPeptideId, searchIdsCollection );
			
			return psmsServiceResult;

			
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

	
	//////////////////////////////////////
	
	
	
	private PsmsServiceResult getPsmData( 
			
			CutoffValuesSearchLevel cutoffValuesSearchLevel,
			Integer searchId,
			Integer reportedPeptideId,
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
		
		//    Descriptive annotations
		

		Map<Integer, Map<Integer, AnnotationTypeDTO>> srchPgmDescriptivePsmAnnotationTypeDTOListPerSearchIdMap =
				GetAnnotationTypeData.getInstance().getAll_Psm_Descriptive_ForSearchIds( searchIdsCollection );
		
		
		Map<Integer, AnnotationTypeDTO> srchPgmDescriptivePsmAnnotationTypeDTOMap = 
				srchPgmDescriptivePsmAnnotationTypeDTOListPerSearchIdMap.get( searchId );
		
		if ( srchPgmDescriptivePsmAnnotationTypeDTOMap == null ) {
			
			//  No records were found, probably an error   TODO
			
			srchPgmDescriptivePsmAnnotationTypeDTOMap = new HashMap<>();
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
		

		


		
		//////////
		
		
		//  Copy cutoff data to searcher cutoff data
		
		
		Z_CutoffValuesObjectsToOtherObjects_PerSearchResult z_CutoffValuesObjectsToOtherObjects_PerSearchResult = 
				Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesSearchLevel( 
						searchIdsCollection, cutoffValuesSearchLevel );
		

		SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = z_CutoffValuesObjectsToOtherObjects_PerSearchResult.getSearcherCutoffValuesSearchLevel();
		
		
		
		//  Get PSM data

		List<PsmWebDisplayWebServiceResult> psmWebDisplayList = 
				PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( searchId, reportedPeptideId, searcherCutoffValuesSearchLevel );
				

		
		
		
		
		
		
		
		
		
		
		
		PsmsServiceResult psmsServiceResult =
				getAnnotationDataAndSort( searchId, searchIdsCollection, srchPgmFilterablePsmAnnotationTypeDTOMap, srchPgmDescriptivePsmAnnotationTypeDTOMap, psmWebDisplayList);
		
		
		
		return psmsServiceResult;
	}


	/////////////////////////////////////////////////////////////////


	/**
	 * @param searchIdsCollection
	 * @param srchPgmFilterablePsmAnnotationTypeDTOMap
	 * @param srchPgmDescriptivePsmAnnotationTypeDTOMap
	 * @param psmWebDisplayList
	 * @return
	 * @throws Exception
	 */
	private PsmsServiceResult getAnnotationDataAndSort(
			
			Integer searchId,
			Collection<Integer> searchIdsCollection,
			
			Map<Integer, AnnotationTypeDTO> srchPgmFilterablePsmAnnotationTypeDTOMap,
			Map<Integer, AnnotationTypeDTO> srchPgmDescriptivePsmAnnotationTypeDTOMap,
			List<PsmWebDisplayWebServiceResult> psmWebDisplayList 
			
			) throws Exception {
		
		
		
		/////////////
		
		//   Create PSM Annotation List sorted on Sort Order 
		

		Map<Integer, AnnotationTypeDTOListForSearchId> annotationTypeDTO_SortOrder_MainMap =
				GetAnnotationTypeDataInSortOrder.getInstance().getPsmAnnotationTypeDataInSortOrder( searchIdsCollection );
		
		if ( annotationTypeDTO_SortOrder_MainMap.size() != 1 ) {
			
			String msg = "getPsmAnnotationTypeDataInSortOrder returned other than 1 entry at searchId level ";
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		
		final List<AnnotationTypeDTO> psm_AnnotationTypeDTO_SortOrder_List = 
				annotationTypeDTO_SortOrder_MainMap.get( searchId ).getAnnotationTypeDTOList();
				

		
		/////////////
		
		//   Create PSM Annotation List sorted on Display Order 
		
		Map<Integer, AnnotationTypeDTOListForSearchId> annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap = 
				GetAnnotationTypeDataDefaultDisplayInDisplayOrder.getInstance()
				.getPsmAnnotationTypeDataDefaultDisplayInDisplayOrder( searchIdsCollection );
		

		if ( annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.size() != 1 ) {
			
			String msg = "getPsmAnnotationTypeDataDefaultDisplayInDisplayOrder returned other than 1 entry at searchId level ";
			log.error( msg );
			throw new ProxlWebappDataException( msg );
		}
		
		final List<AnnotationTypeDTO> psm_AnnotationTypeDTO_DefaultDisplay_List = 
				annotationTypeDTO_DefaultDisplay_DisplayOrder_MainMap.get( searchId ).getAnnotationTypeDTOList();
				
		
		//  Get set of annotation type ids for getting annotation data
		
		Set<Integer> annotationTypeIdsForGettingAnnotationData = new HashSet<>();

		for ( AnnotationTypeDTO annotationTypeDTO : psm_AnnotationTypeDTO_SortOrder_List ) {
			
			annotationTypeIdsForGettingAnnotationData.add( annotationTypeDTO.getId() );
		}
		
		for ( AnnotationTypeDTO annotationTypeDTO : psm_AnnotationTypeDTO_DefaultDisplay_List ) {
			
			annotationTypeIdsForGettingAnnotationData.add( annotationTypeDTO.getId() );
		}
		
		
		//  Get Annotation data
		
		//  Internal holder
		
		List<InternalPsmWebDisplayHolder> psmWebDisplayHolderList = new ArrayList<>( psmWebDisplayList.size() );
		
		for ( PsmWebDisplayWebServiceResult psmWebDisplayItem : psmWebDisplayList ) {
		
			int psmId = psmWebDisplayItem.getPsmDTO().getId();

			Map<Integer, PsmAnnotationDTO> psmAnnotationDTOMapOnTypeId = new HashMap<>();
			

			//  Process annotation type ids to get annotation data

			{
				List<PsmAnnotationDTO> psmAnnotationDataList = 
						PsmAnnotationDataSearcher.getInstance().getPsmAnnotationDTOList( psmId, annotationTypeIdsForGettingAnnotationData );

				for ( PsmAnnotationDTO psmAnnotationDataItem : psmAnnotationDataList ) {

					psmAnnotationDTOMapOnTypeId.put( psmAnnotationDataItem.getAnnotationTypeId(), psmAnnotationDataItem );
				}
			}
			

			InternalPsmWebDisplayHolder internalPsmWebDisplayHolder = new InternalPsmWebDisplayHolder();
			
			internalPsmWebDisplayHolder.psmWebDisplay = psmWebDisplayItem;
			
			internalPsmWebDisplayHolder.psmAnnotationDTOMapOnTypeId = psmAnnotationDTOMapOnTypeId;
			
			psmWebDisplayHolderList.add( internalPsmWebDisplayHolder );
		}

		
		//  Sort PSMs on sort order
		
		Collections.sort( psmWebDisplayHolderList, new Comparator<InternalPsmWebDisplayHolder>() {

			@Override
			public int compare(InternalPsmWebDisplayHolder o1, InternalPsmWebDisplayHolder o2) {
				
				//  Loop through the annotation types (sorted on sort order), comparing the values

				for ( AnnotationTypeDTO annotationTypeDTO : psm_AnnotationTypeDTO_SortOrder_List ) {
					
					int typeId = annotationTypeDTO.getId();
					
					PsmAnnotationDTO o1_PsmAnnotationDTO = o1.psmAnnotationDTOMapOnTypeId.get( typeId );
					if ( o1_PsmAnnotationDTO == null ) {
						
						String msg = "Unable to get PSM Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					
					double o1Value = o1_PsmAnnotationDTO.getValueDouble();
					

					PsmAnnotationDTO o2_PsmAnnotationDTO = o2.psmAnnotationDTOMapOnTypeId.get( typeId );
					if ( o2_PsmAnnotationDTO == null ) {
						
						String msg = "Unable to get PSM Filterable Annotation data for type id: " + typeId;
						log.error( msg );
						throw new RuntimeException(msg);
					}
					
					double o2Value = o2_PsmAnnotationDTO.getValueDouble();
					
					if ( o1Value != o2Value ) {
						
						if ( o1Value < o2Value ) {
							
							return -1;
						} else {
							return 1;
						}
					}
					
				}
				
				//  If everything matches, sort on psm.id
				
				return o1.psmWebDisplay.getPsmDTO().getId() - o2.psmWebDisplay.getPsmDTO().getId();
			}
		});
		

		
		
		
		//  Build output list of PsmWebDisplay
		
		List<PsmWebDisplayWebServiceResult> psmWebDisplayListOutput = new ArrayList<>( psmWebDisplayHolderList.size() );
		
		for ( InternalPsmWebDisplayHolder internalPsmWebDisplayHolder : psmWebDisplayHolderList ) {
			
			
			//  Get annotations
			
			List<String> psmAnnotationValueList = new ArrayList<>( psm_AnnotationTypeDTO_DefaultDisplay_List.size() );
			
			
			for ( AnnotationTypeDTO annotationTypeDTO : psm_AnnotationTypeDTO_DefaultDisplay_List ) {
			
				PsmAnnotationDTO psmAnnotationDTO = 
						internalPsmWebDisplayHolder.psmAnnotationDTOMapOnTypeId.get( annotationTypeDTO.getId() );

				if ( psmAnnotationDTO == null ) {
					
					String msg = "ERROR.  Cannot find AnnotationDTO for type id: " + annotationTypeDTO.getId();
					log.error( msg );
					throw new Exception(msg);
				}
				
				psmAnnotationValueList.add( psmAnnotationDTO.getValueString() );
			}
			
			
			PsmWebDisplayWebServiceResult psmWebDisplay = internalPsmWebDisplayHolder.psmWebDisplay;
			
			psmWebDisplay.setPsmAnnotationValueList( psmAnnotationValueList  );
			
			
			psmWebDisplayListOutput.add( psmWebDisplay );
		}
		

		
		
		
		

		//  Get PSM column headers data
		
		List<AnnotationDisplayNameDescription> annotationDisplayNameDescriptionList = new ArrayList<>( psm_AnnotationTypeDTO_DefaultDisplay_List.size() );
		
		for ( AnnotationTypeDTO annotationTypeDTO : psm_AnnotationTypeDTO_DefaultDisplay_List ) {
			
			AnnotationDisplayNameDescription annotationDisplayNameDescription = new AnnotationDisplayNameDescription();
			
			annotationDisplayNameDescription.setDisplayName( annotationTypeDTO.getName() );
			annotationDisplayNameDescription.setDescription( annotationTypeDTO.getDescription() );
			
			annotationDisplayNameDescriptionList.add( annotationDisplayNameDescription );
			
		}

		
		
		///////////

		PsmsServiceResult psmsServiceResult = new PsmsServiceResult();
		
		psmsServiceResult.setAnnotationDisplayNameDescriptionList( annotationDisplayNameDescriptionList );
		
		psmsServiceResult.setPsmWebDisplayList( psmWebDisplayListOutput );
		

		return psmsServiceResult;
	}
	
	
	
	/**
	 * 
	 *
	 */
	private static class InternalPsmWebDisplayHolder {
		
		PsmWebDisplayWebServiceResult psmWebDisplay;
		
		Map<Integer, PsmAnnotationDTO> psmAnnotationDTOMapOnTypeId;
		
	}
	
}
