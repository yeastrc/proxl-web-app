package org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.protein_and_coverage_pages;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesAnnotationLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.actions.ProteinsMergedProteinsCommon;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.MinimumPSMsConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappNoDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.linked_positions.CrosslinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.LinkedPositions_FilterExcludeLinksWith_Param;
import org.yeastrc.xlink.www.linked_positions.LooplinkLinkedPositions;
import org.yeastrc.xlink.www.no_data_validation.ThrowExceptionOnNoDataConfig;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId;
import org.yeastrc.xlink.www.web_utils.MarshalObjectToJSON;
import org.yeastrc.xlink.www.web_utils.TaxonomiesForSearchOrSearches;
import org.yeastrc.xlink.www.web_utils.XLinkWebAppUtils;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.cache_results_in_memory.DataPagesMain_GetDataWebservices_CacheResults_InMemory;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.cache_results_in_memory.DataPagesMain_GetDataWebservices_CacheResults_InMemory.DataType;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.protein_and_coverage_pages.Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager.Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager_Result;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;

/**
 * Common code called from Protein_crosslink_SingleSearch_PageData_Webservice and Protein_looplink_SingleSearch_PageData_Webservice
 * 
 * package private class
 *
 */
class Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice {

	private static final Logger log = LoggerFactory.getLogger( Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice.class );

	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 */
	static final int VERSION_FOR_CACHING = 1;
	
	
	/**
	 * Calling from Crosslink or Looplink
	 *
	 */
	enum CrosslinkLooplink { CROSSLINK, LOOPLINK }

	private Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice() {}
	public static Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice getNewInstance() {
		return new Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice();
	}

	/**
	 * @param requestJSONBytes
	 * @param crosslinkLooplink
	 * @param request
	 * @return
	 */
	byte[] processRequest( byte[] requestJSONBytes, CrosslinkLooplink crosslinkLooplink, HttpServletRequest request ) {

		if ( requestJSONBytes == null || requestJSONBytes.length == 0 ) {
			String msg = "requestJSONBytes is null or requestJSONBytes is empty";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					//		    	        .entity(  )
					.build()
					);
		}

		//  throws new WebApplicationException BAD_REQUEST if parse error
		WebserviceRequest webserviceRequest = // class defined in this class
				Unmarshal_RestRequest_JSON_ToObject.getInstance()
				.getObjectFromJSONByteArray( requestJSONBytes, WebserviceRequest.class );

		List<Integer> projectSearchIdList = webserviceRequest.projectSearchIds;
		ProteinQueryJSONRoot proteinQueryJSONRoot = webserviceRequest.proteinQueryJSONRoot;

		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided projectSearchIds is null or projectSearchIds is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		if ( projectSearchIdList.size() != 1 ) {
			String msg = "Only 1 project_search_id is accepted";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		if ( proteinQueryJSONRoot == null ) {
			String msg = "proteinQueryJSONRoot is null or proteinQueryJSONRoot is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}

		int projectSearchId = projectSearchIdList.get( 0 );

		try {
			//   Get the project id for this search
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.add( projectSearchId );

			List<Integer> projectIdsFromProjectSearchIds = 
					ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromProjectSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for project search ids: " + StringUtils.join( projectSearchIdList );
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			if ( projectIdsFromProjectSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_ACROSS_PROJECTS_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			int projectId = projectIdsFromProjectSearchIds.get( 0 );
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			//			UserSession userSession = accessAndSetupWebSessionResult.getUserSession();
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			//  Test access to the project id
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
			
			

			{  //  Check Cached response in RAM
				if ( crosslinkLooplink == CrosslinkLooplink.CROSSLINK ) {
					byte[] cachedWebserviceResponseJSONAsBytes = 
							DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
							.getData( DataType.PROTEINS_CROSSLINKS_SINGLE_SEARCH, requestJSONBytes );
					if ( cachedWebserviceResponseJSONAsBytes != null ) {
						//  Have Cached response so just return it
						return cachedWebserviceResponseJSONAsBytes;  // EARLY RETURN !!!!!!!!!!!!!!
					}
				}
				if ( crosslinkLooplink == CrosslinkLooplink.LOOPLINK ) {
					byte[] cachedWebserviceResponseJSONAsBytes = 
							DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
							.getData( DataType.PROTEINS_LOOPLINKS_SINGLE_SEARCH, requestJSONBytes );
					if ( cachedWebserviceResponseJSONAsBytes != null ) {
						//  Have Cached response so just return it
						return cachedWebserviceResponseJSONAsBytes;  // EARLY RETURN !!!!!!!!!!!!!!
					}
				}
			}
			
			{
				//  Next check Cached response on disk
				Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager_Result result = 
						Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager.getSingletonInstance()
						.retrieveDataFromCache( crosslinkLooplink, projectSearchIdList, requestJSONBytes );
				
				byte[] cachedWebserviceResponseJSONAsBytes = result.getWebserviceResponseJSONAsBytes();
				if ( cachedWebserviceResponseJSONAsBytes != null ) {
					//  Have Cached response so just return it

					if ( crosslinkLooplink == CrosslinkLooplink.CROSSLINK ) {
						{  //  Cache response to RAM
							DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
							.putData( DataType.PROTEINS_CROSSLINKS_SINGLE_SEARCH, requestJSONBytes, cachedWebserviceResponseJSONAsBytes );
						}
					}
					if ( crosslinkLooplink == CrosslinkLooplink.LOOPLINK ) {
						{  //  Cache response to RAM
							DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
							.putData( DataType.PROTEINS_LOOPLINKS_SINGLE_SEARCH, requestJSONBytes, cachedWebserviceResponseJSONAsBytes );
						}
					}
					
					return cachedWebserviceResponseJSONAsBytes;  // EARLY RETURN !!!!!!!!!!!!!!
				}
			}
			
			

			SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
			if ( search == null ) {
				String msg = "projectSearchId '" + projectSearchId + "' not found in the database. User taken to home page.";
				log.warn( msg );
				//  Search not found, the data on the page they are requesting does not exist.
				throw new WebApplicationException(
						Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
						.entity( msg )
						.build()
						);			
			}
			Integer searchId = search.getSearchId();
			Collection<Integer> searchIdsSet = new HashSet<>();
			searchIdsSet.add( searchId );
			Map<Integer,Integer> mapProjectSearchIdToSearchId = new HashMap<>();
			mapProjectSearchIdToSearchId.put( projectSearchId, searchId );
			
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
			//////     Code common to Crosslinks and Looplinks
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
			
			/////////////////////////////////////////////////////////////
			//////////////////   Get Crosslinks data from DATABASE  from database
			
			//  auto populate param object from proteinQueryJSONRoot
			LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param = new LinkedPositions_FilterExcludeLinksWith_Param( proteinQueryJSONRoot );
			
			List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
					CrosslinkLinkedPositions.getInstance()
					.getSearchProteinCrosslinkWrapperList( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
			
			//  If configured, throw exception if no peptides found
			if ( ThrowExceptionOnNoDataConfig.getInstance().isThrowExceptionNoData() ) {
				if ( wrappedCrosslinks.isEmpty() ) {
					String msg = "No Crosslinks found and config set for ThrowExceptionNoData";
					log.error( msg );
					throw new ProxlWebappNoDataException( msg );
				}
			}
			/////////////////////////////////////////////////////////////
			//////////////////   Get Looplinks data from DATABASE   from database
			List<SearchProteinLooplinkWrapper> wrappedLooplinks = 
					LooplinkLinkedPositions.getInstance()
					.getSearchProteinLooplinkWrapperList( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );

			//  If configured, throw exception if no peptides found
			if ( ThrowExceptionOnNoDataConfig.getInstance().isThrowExceptionNoData() ) {
				if ( wrappedLooplinks.isEmpty() ) {
					String msg = "No Looplinks found and config set for ThrowExceptionNoData";
					log.error( msg );
					throw new ProxlWebappNoDataException( msg );
				}
			}
			// all possible proteins included in this search for this type
			Collection<SearchProtein> prProteins = new HashSet<SearchProtein>();
			for ( SearchProteinCrosslinkWrapper  searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
				SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
				prProteins.add( searchProteinCrosslink.getProtein1() );
				prProteins.add( searchProteinCrosslink.getProtein2() );
			}
			for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
				SearchProteinLooplink searchProteinLooplink = searchProteinLooplinkWrapper.getSearchProteinLooplink();
				prProteins.add( searchProteinLooplink.getProtein() );
			}
			//////////////////////////////////////////////////////////////////
			// Filter out links if requested  ---  Filtering for proteinQueryJSONRoot.isRemoveNonUniquePSMs() is performed in CrosslinkLinkedPositions and TODO
			if( proteinQueryJSONRoot.isFilterNonUniquePeptides() 
					|| proteinQueryJSONRoot.getMinPSMs() != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT
					|| proteinQueryJSONRoot.isFilterOnlyOnePeptide()
					|| ( proteinQueryJSONRoot.getExcludeTaxonomy() != null && proteinQueryJSONRoot.getExcludeTaxonomy().length > 0 ) ||
					( ! excludeproteinSequenceVersionIds_Set_UserInput.isEmpty() ) ) {
				
				///////  Output Lists, Results After Filtering
				List<SearchProteinCrosslinkWrapper> wrappedCrosslinksAfterFilter = new ArrayList<>( wrappedCrosslinks.size() );
				List<SearchProteinLooplinkWrapper> wrappedLooplinksAfterFilter = new ArrayList<>( wrappedLooplinks.size() );
				
				///  Filter CROSSLINKS
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
					SearchProteinCrosslink link = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					// did they request to removal of non unique peptides?
					if( proteinQueryJSONRoot.isFilterNonUniquePeptides()  ) {
						if( link.getNumUniqueLinkedPeptides() < 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
				
					// did they request to removal of links with less than a specified number of PSMs?
					if( proteinQueryJSONRoot.getMinPSMs() != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT ) {
						int psmCountForSearchId = link.getNumPsms();
						if ( psmCountForSearchId < proteinQueryJSONRoot.getMinPSMs() ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one Reported Peptide?
					if( proteinQueryJSONRoot.isFilterOnlyOnePeptide() ) {
						int peptideCountForSearchId = link.getNumLinkedPeptides();
						if ( peptideCountForSearchId <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain taxonomy IDs?
					if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
						boolean excludeOnProtein_1 =
								ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										link.getProtein1().getProteinSequenceVersionObject(), 
										searchId );
						boolean excludeOnProtein_2 =
								ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										link.getProtein2().getProteinSequenceVersionObject(), 
										searchId );
						if ( excludeOnProtein_1 || excludeOnProtein_2 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain protein IDs?
					if( ! excludeproteinSequenceVersionIds_Set_UserInput.isEmpty() ) {
						int proteinId_1 = link.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						int proteinId_2 = link.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						if ( excludeproteinSequenceVersionIds_Set_UserInput.contains( proteinId_1 ) 
								|| excludeproteinSequenceVersionIds_Set_UserInput.contains( proteinId_2 ) ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}		
					wrappedCrosslinksAfterFilter.add( searchProteinCrosslinkWrapper );
				}
				
				///  Filter LOOPLINKS
				for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
					SearchProteinLooplink link = searchProteinLooplinkWrapper.getSearchProteinLooplink();
					// did they request to removal of non unique peptides?
					if( proteinQueryJSONRoot.isFilterNonUniquePeptides()  ) {
						if( link.getNumUniquePeptides() < 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one PSM?
					if( proteinQueryJSONRoot.getMinPSMs() != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT  ) {
						int psmCountForSearchId = link.getNumPsms();
						if ( psmCountForSearchId < proteinQueryJSONRoot.getMinPSMs() ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did they request to removal of links with only one Reported Peptide?
					if( proteinQueryJSONRoot.isFilterOnlyOnePeptide() ) {
						int peptideCountForSearchId = link.getNumPeptides();
						if ( peptideCountForSearchId <= 1 ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain taxonomy IDs?
					if( ! excludeTaxonomy_Ids_Set_UserInput.isEmpty() ) {
						boolean excludeOnProtein =
								ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
								.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
										excludeTaxonomy_Ids_Set_UserInput, 
										link.getProtein().getProteinSequenceVersionObject(), 
										searchId );
						if ( excludeOnProtein ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did user request removal of certain protein IDs?
					if( ! excludeproteinSequenceVersionIds_Set_UserInput.isEmpty() ) {
						int proteinId = link.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						if ( excludeproteinSequenceVersionIds_Set_UserInput.contains( proteinId ) ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}									
					wrappedLooplinksAfterFilter.add( searchProteinLooplinkWrapper );
				}
				
				//  Copy new filtered lists to original input variable names to overlay them
				wrappedCrosslinks = wrappedCrosslinksAfterFilter;
				wrappedLooplinks = wrappedLooplinksAfterFilter;
			}
			
			

			//////////////////////////////////////////////////////////////////
			///////////////////////////
			///   Process Crosslinks and Looplinks to get annotations and sort.
			
			//  Set from Crosslink or Looplink processing
			SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt srtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt = null;
			
			//              Process Crosslinks or Looplinks, depending on which page is being displayed
			
			List<SearchProteinCrosslink> crosslinks = null;
			if ( crosslinkLooplink != CrosslinkLooplink.CROSSLINK ) {
				//  NOT Crosslink page:    Struts config action mapping:    NOT parameter="crosslink"
				//  Simply unwrap the crosslinks  
				crosslinks = new ArrayList<>( wrappedCrosslinks.size() );
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
					crosslinks.add( searchProteinCrosslinkWrapper.getSearchProteinCrosslink() );
				}
			} else {
				//	For  Struts config action mapping:     parameter="crosslink"
				//  Order so:  ( protein_1 name < protein_2 name) or ( protein_1 name == protein_2 name and pos1 <= pos2 )
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
					SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					int protein1CompareProtein2 = searchProteinCrosslink.getProtein1().getName().compareToIgnoreCase( searchProteinCrosslink.getProtein2().getName() );
					if ( protein1CompareProtein2 > 0 
							|| ( protein1CompareProtein2 == 0
								&& searchProteinCrosslink.getProtein1Position() > searchProteinCrosslink.getProtein2Position() ) ) {
						//  Protein_1 name > protein_2 name or ( Protein_1 name == protein_2 name and pos1 > pos2 )
						//  so re-order
						SearchProtein searchProteinTemp = searchProteinCrosslink.getProtein1();
						searchProteinCrosslink.setProtein1( searchProteinCrosslink.getProtein2() );
						searchProteinCrosslink.setProtein2( searchProteinTemp );
						int linkPositionTemp = searchProteinCrosslink.getProtein1Position();
						searchProteinCrosslink.setProtein1Position( searchProteinCrosslink.getProtein2Position() );
						searchProteinCrosslink.setProtein2Position( linkPositionTemp );
					}
				}
				//  Sort "wrappedCrosslinks" since removed ORDER BY from SQL
				Collections.sort( wrappedCrosslinks, new Comparator<SearchProteinCrosslinkWrapper>() {
					@Override
					public int compare(SearchProteinCrosslinkWrapper o1,
							SearchProteinCrosslinkWrapper o2) {
						if ( o1.getSearchProteinCrosslink().getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId()
								!= o2.getSearchProteinCrosslink().getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
							return o1.getSearchProteinCrosslink().getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId() 
									- o2.getSearchProteinCrosslink().getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						}
						if ( o1.getSearchProteinCrosslink().getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId()
								!= o2.getSearchProteinCrosslink().getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
							return o1.getSearchProteinCrosslink().getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId() 
									- o2.getSearchProteinCrosslink().getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						}
						if ( o1.getSearchProteinCrosslink().getProtein1Position()
								!= o2.getSearchProteinCrosslink().getProtein1Position() ) {
							return o1.getSearchProteinCrosslink().getProtein1Position() 
									- o2.getSearchProteinCrosslink().getProtein1Position();
						}
						return o1.getSearchProteinCrosslink().getProtein2Position() 
								- o2.getSearchProteinCrosslink().getProtein2Position();
					}
				} );
				////   Crosslinks  - Prepare for Web display
				//      Get Annotation data and Sort by Annotation data
				srtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt =
						SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.getInstance()
						.sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId(
								searchId, 
								wrappedCrosslinks, 
								peptideCutoffsAnnotationTypeDTOList, 
								psmCutoffsAnnotationTypeDTOList );
				
				crosslinks = new ArrayList<>( wrappedCrosslinks.size() );
				//  Copy of out wrapper for processing below
				for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
					SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
					crosslinks.add( searchProteinCrosslink );
				}
			}
			/////////////////////////////////////////////////////////////
			//////////////////   Get Looplinks data
			List<SearchProteinLooplink> looplinks = null;
			if ( crosslinkLooplink != CrosslinkLooplink.LOOPLINK ) {
				//  NOT Looplink page:    Struts config action mapping:    NOT parameter="looplink"
				//  Simply unwrap the looplinks  
				looplinks = new ArrayList<>( wrappedLooplinks.size() );
				for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
					looplinks.add( searchProteinLooplinkWrapper.getSearchProteinLooplink() );
				}
			} else {
				//	For  Struts config action mapping:     parameter="looplink"
				////   Looplinks  - Prepare for Web display
				//      Get Annotation data and Sort by Annotation data
				looplinks = new ArrayList<>( wrappedLooplinks.size() );
				Collections.sort( wrappedLooplinks, new Comparator<SearchProteinLooplinkWrapper>() {
					@Override
					public int compare(SearchProteinLooplinkWrapper o1,
							SearchProteinLooplinkWrapper o2) {
						if ( o1.getSearchProteinLooplink().getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId()
								!= o2.getSearchProteinLooplink().getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
							return o1.getSearchProteinLooplink().getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() 
									- o2.getSearchProteinLooplink().getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
						}
						if ( o1.getSearchProteinLooplink().getProteinPosition1()
								!= o2.getSearchProteinLooplink().getProteinPosition1() ) {
							return o1.getSearchProteinLooplink().getProteinPosition1() 
									- o2.getSearchProteinLooplink().getProteinPosition1();
						}
						return o1.getSearchProteinLooplink().getProteinPosition2() 
								- o2.getSearchProteinLooplink().getProteinPosition2();
					}
				} );
				//      Get Annotation data and Sort by Annotation data
				srtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt =
						SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.getInstance()
						.sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId(
								searchId, 
								wrappedLooplinks, 
								peptideCutoffsAnnotationTypeDTOList, 
								psmCutoffsAnnotationTypeDTOList );

				/////////////////////////////////
				//  Copy of out wrapper for output
				for ( SearchProteinLooplinkWrapper searchProteinLooplinkWrapper : wrappedLooplinks ) {
					SearchProteinLooplink searchProteinLooplink = searchProteinLooplinkWrapper.getSearchProteinLooplink();
					looplinks.add( searchProteinLooplink );
				}
			}			
			///////////////////////////////////////////////////////
			Collection<SearchProtein> prProteins2 = new HashSet<SearchProtein>();
			prProteins2.addAll( prProteins );
			// build a collection of protein IDs to include
			for( SearchProtein prp : prProteins2 ) {
				// did they request removal of certain taxonomy IDs?
				if( proteinQueryJSONRoot.getExcludeTaxonomy() != null && proteinQueryJSONRoot.getExcludeTaxonomy().length > 0 ) {
					boolean excludeOnProtein =
							ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
							.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
									excludeTaxonomy_Ids_Set_UserInput, 
									prp.getProteinSequenceVersionObject(), 
									searchId );
					if ( excludeOnProtein ) {
						prProteins.remove( prp );
					}
//					for( int taxonomyIdToExclude : proteinQueryJSONRoot.getExcludeTaxonomy() ) {
//						
//						if( taxonomyIdToExclude == prp.getProteinSequenceVersionObject().getTaxonomyId() ) {
//							prProteins.remove( prp );
//							break;
//						}
//					}
				}
			}
			int numDistinctLinks =  XLinkWebAppUtils.getNumUDRs( crosslinks, looplinks );


			List<WebserviceResponse_ProteinExcludeEntry> proteinExcludeEntries = new ArrayList<>( prProteins.size() );
			{
				//  Create Protein Exclude entries
				for ( SearchProtein searchProtein : prProteins ) {
					try {
						WebserviceResponse_ProteinExcludeEntry webserviceResponse_ProteinExcludeEntry = new WebserviceResponse_ProteinExcludeEntry();
						webserviceResponse_ProteinExcludeEntry.setProteinSequenceVersionId( searchProtein.getProteinSequenceVersionObject().getProteinSequenceVersionId() );
						String proteinName = searchProtein.getName();
						if ( proteinName == null || proteinName.length() == 0 ) {
							//  Skip since no protein name
							continue;  // EARLY CONTINUE
						}
						webserviceResponse_ProteinExcludeEntry.setProteinName( proteinName );
						proteinExcludeEntries.add( webserviceResponse_ProteinExcludeEntry );
					} catch ( Exception e ) {
						// eat exception
					}
				}
				Collections.sort( proteinExcludeEntries, new Comparator<WebserviceResponse_ProteinExcludeEntry>() {
					@Override
					public int compare(WebserviceResponse_ProteinExcludeEntry o1, WebserviceResponse_ProteinExcludeEntry o2) {
						//  Compare Protein Name, if equal, Protein Sequence Version Id
						int compareProteinNameLowerCase = o1.proteinName.compareToIgnoreCase( o2.proteinName );
						if ( compareProteinNameLowerCase != 0 ) {
							return compareProteinNameLowerCase;
						}
						if ( o1.proteinSequenceVersionId < o2.proteinSequenceVersionId ) {
							return -1;
						}
						if ( o1.proteinSequenceVersionId > o2.proteinSequenceVersionId ) {
							return 1;
						}
						return 0;
					}
				} );
				
			}
			
			// build list of taxonomies to show in exclusion list
			Map<Integer, String> taxonomies = TaxonomiesForSearchOrSearches.getInstance().getTaxonomiesForSearchId( search.getSearchId() );

			WebserviceResult webserviceResult = new WebserviceResult();

			webserviceResult.projectSearchId = projectSearchId;
			
			if ( crosslinkLooplink == CrosslinkLooplink.CROSSLINK ) {

				List<WebserviceResponse_CrosslinkProteinEntry> crosslinksForResponse = new ArrayList<>( crosslinks.size() );
				
				for ( SearchProteinCrosslink crosslinkEntry : crosslinks ) {
					
					WebserviceResponse_CrosslinkProteinEntry responseEntry = new WebserviceResponse_CrosslinkProteinEntry();
					
					//  Protein 1
					responseEntry.proteinSequenceVersionId_1 = crosslinkEntry.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId();
					try {
						responseEntry.proteinName_1 = crosslinkEntry.getProtein1().getName();
					} catch ( Exception e ) {
						//  Skip if fail
						continue; // EARLY CONTINUE
					}
					responseEntry.linkPosition_1 = crosslinkEntry.getProtein1Position();
					
					//  Protein 2
					responseEntry.proteinSequenceVersionId_2 = crosslinkEntry.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId();
					try {
						responseEntry.proteinName_2 = crosslinkEntry.getProtein2().getName();
					} catch ( Exception e ) {
						//  Skip if fail
						continue; // EARLY CONTINUE
					}
					responseEntry.linkPosition_2 = crosslinkEntry.getProtein2Position();
					
					//  Other:
					responseEntry.numPsms = crosslinkEntry.getNumPsms();
					responseEntry.numLinkedPeptides = crosslinkEntry.getNumLinkedPeptides();
					responseEntry.numUniqueLinkedPeptides = crosslinkEntry.getNumUniqueLinkedPeptides();
					
					responseEntry.psmAnnotationValueList = crosslinkEntry.getPsmAnnotationValueList();
					responseEntry.peptideAnnotationValueList = crosslinkEntry.getPeptideAnnotationValueList();
					
					crosslinksForResponse.add( responseEntry );
				}
					
				webserviceResult.crosslinks = crosslinksForResponse;
			}
			if ( crosslinkLooplink == CrosslinkLooplink.LOOPLINK ) {
				
				List<WebserviceResponse_LooplinkProteinEntry> looplinksForResponse = new ArrayList<>( looplinks.size() );
				
				for ( SearchProteinLooplink looplinkEntry : looplinks ) {
					
					WebserviceResponse_LooplinkProteinEntry responseEntry = new WebserviceResponse_LooplinkProteinEntry();
					
					//  Protein
					responseEntry.proteinSequenceVersionId = looplinkEntry.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
					try {
						responseEntry.proteinName = looplinkEntry.getProtein().getName();
					} catch ( Exception e ) {
						//  Skip if fail
						continue; // EARLY CONTINUE
					}
					responseEntry.linkPosition_1 = looplinkEntry.getProteinPosition1();
					responseEntry.linkPosition_2 = looplinkEntry.getProteinPosition2();
					
					//  Other:
					responseEntry.numPsms = looplinkEntry.getNumPsms();
					responseEntry.numPeptides = looplinkEntry.getNumPeptides();
					responseEntry.numUniquePeptides = looplinkEntry.getNumUniquePeptides();
					
					responseEntry.psmAnnotationValueList = looplinkEntry.getPsmAnnotationValueList();
					responseEntry.peptideAnnotationValueList = looplinkEntry.getPeptideAnnotationValueList();
					
					looplinksForResponse.add( responseEntry );
				}
				
				webserviceResult.looplinks = looplinksForResponse;	
			}
			
			webserviceResult.proteinExcludeEntries = proteinExcludeEntries;
			
			webserviceResult.numCrosslinks = crosslinks.size();
			webserviceResult.numLooplinks = looplinks.size();
			webserviceResult.numLinks = looplinks.size() + crosslinks.size();
			webserviceResult.numDistinctLinks = numDistinctLinks;
			
			webserviceResult.taxonomies = taxonomies;
			
			webserviceResult.peptideAnnotationDisplayNameDescriptionList =
					srtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt.getPeptideAnnotationDisplayNameDescriptionList();
			webserviceResult.psmAnnotationDisplayNameDescriptionList =
					srtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt.getPsmAnnotationDisplayNameDescriptionList();

			byte[] webserviceResultByteArray = MarshalObjectToJSON.getInstance().getJSONByteArray( webserviceResult );

			{  //  Cache response to RAM
				if ( crosslinkLooplink == CrosslinkLooplink.CROSSLINK ) {
					DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
					.putData( DataType.PROTEINS_CROSSLINKS_SINGLE_SEARCH, requestJSONBytes, webserviceResultByteArray );
				}
				if ( crosslinkLooplink == CrosslinkLooplink.LOOPLINK ) {
					DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
					.putData( DataType.PROTEINS_LOOPLINKS_SINGLE_SEARCH, requestJSONBytes, webserviceResultByteArray );
				}
			}
			{
				//  Cache response to disk
				Protein_crosslink_looplink_Common_SingleSearch_PageData_Webservice_CachedResultManager.getSingletonInstance()
				.saveDataToCache( crosslinkLooplink, projectSearchIdList, webserviceResultByteArray, requestJSONBytes );
			}

			return webserviceResultByteArray;

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
	 * 
	 *
	 */
	public static class WebserviceRequest {

		private List<Integer> projectSearchIds;
		private ProteinQueryJSONRoot proteinQueryJSONRoot;

		public void setProjectSearchIds(List<Integer> projectSearchIds) {
			this.projectSearchIds = projectSearchIds;
		}
		public void setProteinQueryJSONRoot(ProteinQueryJSONRoot proteinQueryJSONRoot) {
			this.proteinQueryJSONRoot = proteinQueryJSONRoot;
		}
	}

	/**
	 * 
	 *
	 */
	public static class WebserviceResult {

		private int projectSearchId;

		//  Only 1 of these 2 lists will be populated
		private List<WebserviceResponse_CrosslinkProteinEntry> crosslinks;
		private List<WebserviceResponse_LooplinkProteinEntry> looplinks;
		
		private int numCrosslinks;
		private int numLooplinks;
		private int numLinks;
		private int numDistinctLinks;
		

		// build list of taxonomies to show in exclusion list
		Map<Integer, String> taxonomies;
		
		private List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList;
		private List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList;
		
		private List<WebserviceResponse_ProteinExcludeEntry> proteinExcludeEntries;
		
		public int getProjectSearchId() {
			return projectSearchId;
		}
		public List<WebserviceResponse_CrosslinkProteinEntry> getCrosslinks() {
			return crosslinks;
		}
		public List<WebserviceResponse_LooplinkProteinEntry> getLooplinks() {
			return looplinks;
		}
		public int getNumCrosslinks() {
			return numCrosslinks;
		}
		public int getNumLooplinks() {
			return numLooplinks;
		}
		public int getNumLinks() {
			return numLinks;
		}
		public int getNumDistinctLinks() {
			return numDistinctLinks;
		}
		public Map<Integer, String> getTaxonomies() {
			return taxonomies;
		}
		public List<AnnotationDisplayNameDescription> getPeptideAnnotationDisplayNameDescriptionList() {
			return peptideAnnotationDisplayNameDescriptionList;
		}
		public List<AnnotationDisplayNameDescription> getPsmAnnotationDisplayNameDescriptionList() {
			return psmAnnotationDisplayNameDescriptionList;
		}
		public List<WebserviceResponse_ProteinExcludeEntry> getProteinExcludeEntries() {
			return proteinExcludeEntries;
		}
	}
	

	/**
	 * Entry for Crosslink Protein
	 *
	 */
	public static class WebserviceResponse_CrosslinkProteinEntry {

		private int proteinSequenceVersionId_1;
		private String proteinName_1;
		private int linkPosition_1;

		private int proteinSequenceVersionId_2;
		private String proteinName_2;
		private int linkPosition_2;
		
		private int numPsms;
		private int numLinkedPeptides;
		private int numUniqueLinkedPeptides;
		
		private List<String> psmAnnotationValueList;
		private List<String> peptideAnnotationValueList;
		
		public int getProteinSequenceVersionId_1() {
			return proteinSequenceVersionId_1;
		}
		public String getProteinName_1() {
			return proteinName_1;
		}
		public int getLinkPosition_1() {
			return linkPosition_1;
		}
		public int getProteinSequenceVersionId_2() {
			return proteinSequenceVersionId_2;
		}
		public String getProteinName_2() {
			return proteinName_2;
		}
		public int getLinkPosition_2() {
			return linkPosition_2;
		}
		public int getNumPsms() {
			return numPsms;
		}
		public int getNumLinkedPeptides() {
			return numLinkedPeptides;
		}
		public int getNumUniqueLinkedPeptides() {
			return numUniqueLinkedPeptides;
		}
		public List<String> getPsmAnnotationValueList() {
			return psmAnnotationValueList;
		}
		public List<String> getPeptideAnnotationValueList() {
			return peptideAnnotationValueList;
		}
		
	}

	/**
	 * Entry for Looplink Protein
	 *
	 */
	public static class WebserviceResponse_LooplinkProteinEntry {

		private int proteinSequenceVersionId;
		private String proteinName;
		private int linkPosition_1;
		private int linkPosition_2;
		
		private int numPsms;
		private int numPeptides;
		private int numUniquePeptides;
		
		private List<String> psmAnnotationValueList;
		private List<String> peptideAnnotationValueList;
		
		public int getProteinSequenceVersionId() {
			return proteinSequenceVersionId;
		}
		public String getProteinName() {
			return proteinName;
		}
		public int getLinkPosition_1() {
			return linkPosition_1;
		}
		public int getLinkPosition_2() {
			return linkPosition_2;
		}
		public int getNumPsms() {
			return numPsms;
		}
		public int getNumPeptides() {
			return numPeptides;
		}
		public int getNumUniquePeptides() {
			return numUniquePeptides;
		}
		public List<String> getPsmAnnotationValueList() {
			return psmAnnotationValueList;
		}
		public List<String> getPeptideAnnotationValueList() {
			return peptideAnnotationValueList;
		}
	}
	
	/**
	 * Entry for Protein Exclude Selection control
	 *
	 */
	public static class WebserviceResponse_ProteinExcludeEntry {

		private int proteinSequenceVersionId;
		private String proteinName;
		
		public int getProteinSequenceVersionId() {
			return proteinSequenceVersionId;
		}
		public void setProteinSequenceVersionId(int proteinSequenceVersionId) {
			this.proteinSequenceVersionId = proteinSequenceVersionId;
		}
		public String getProteinName() {
			return proteinName;
		}
		public void setProteinName(String proteinName) {
			this.proteinName = proteinName;
		}
	}
	
}
