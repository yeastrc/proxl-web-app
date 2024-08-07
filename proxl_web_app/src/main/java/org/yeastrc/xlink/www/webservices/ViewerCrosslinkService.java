package org.yeastrc.xlink.www.webservices;

import java.io.ByteArrayOutputStream;
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
//import javax.servlet.http.HttpSession;
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
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.linked_positions.CrosslinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.LinkedPositions_FilterExcludeLinksWith_Param;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.constants.MinimumPSMsConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.ImageViewerData;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerCrosslinkService_Results_Main_CachedResultManager;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerCrosslinkService_Results_Main_CachedResultManager.ViewerCrosslinkService_Results_Main_CachedResultManager_Result;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerCrosslinkService_Results_PsmCount_CachedResultManager;
import org.yeastrc.xlink.www.webservices_cache_response.ViewerCrosslinkService_Results_PsmCount_CachedResultManager.ViewerCrosslinkService_Results_PsmCount_CachedResultManager_Result;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/imageViewer")
public class ViewerCrosslinkService {
	
	private static final Logger log = LoggerFactory.getLogger( ViewerCrosslinkService.class);

	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 */
	public static final int VERSION_FOR_CACHING = 1;
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCrosslinkData") 
	public byte[]  getViewerData(
			@QueryParam( "projectSearchId" ) List<Integer> projectSearchIdList,
			@QueryParam( "psmPeptideCutoffsForProjectSearchIds" ) String psmPeptideCutoffsForProjectSearchIds_JSONString,
			@QueryParam( "minPSMs" ) Integer minPSMs,
			@QueryParam( "filterNonUniquePeptides" ) String filterNonUniquePeptidesString,
			@QueryParam( "filterOnlyOnePeptide" ) String filterOnlyOnePeptideString,
			@QueryParam( "removeNonUniquePSMs" ) String removeNonUniquePSMsString,
			@QueryParam( "excludeTaxonomy" ) List<Integer> excludeTaxonomy,
			@Context HttpServletRequest request )
	throws Exception {
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided projectSearchId is null or empty, projectSearchId = " + projectSearchIdList;
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( psmPeptideCutoffsForProjectSearchIds_JSONString ) ) {
			String msg = "Provided psmPeptideCutoffsForProjectSearchIds is null or psmPeptideCutoffsForProjectSearchIds is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		if ( minPSMs == null ) {
			String msg = "Provided minPSMs is null or minPSMs is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		
		try {
			//   Get the project id for this search
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.addAll( projectSearchIdList );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchIdList: ";
				for ( int projectSearchId : projectSearchIdList ) {
					msg += projectSearchId + ", ";
				}				
				log.warn( msg );
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

			String requestQueryString = request.getQueryString();
			
			List<Integer> projectSearchIdListDedupedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdListDedupedSorted );
			
			ViewerCrosslinkService_Results_Main_CachedResultManager_Result cachedResultManager_Result =
					ViewerCrosslinkService_Results_Main_CachedResultManager.getSingletonInstance()
					.retrieveDataFromCache( projectSearchIdListDedupedSorted, requestQueryString );
			
			if ( cachedResultManager_Result != null ) {

				byte[] resultsAsBytes = cachedResultManager_Result.getChartJSONAsBytes();
				if ( resultsAsBytes != null ) {
					
					//  Use JSON cached to disk
					return resultsAsBytes;  //  EARLY EXIT
				}
			}
			
			ImageViewerData ivd = new ImageViewerData();
			
			//   Get PSM and Peptide Cutoff data from JSON
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization

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
					return o1.getProjectSearchId() - o2.getProjectSearchId();
				}
			});
			
			CutoffValuesRootLevel cutoffValuesRootLevel = null;
			try {
				cutoffValuesRootLevel = jacksonJSON_Mapper.readValue( psmPeptideCutoffsForProjectSearchIds_JSONString, CutoffValuesRootLevel.class );
			} catch ( JsonParseException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', JsonParseException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			} catch ( JsonMappingException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', JsonMappingException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			} catch ( IOException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', IOException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			}
			
			Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( 
							searchIdsSet, cutoffValuesRootLevel );
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();

			////////////
			//  Copy Exclude Taxonomy Set for lookup
			Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
			if( excludeTaxonomy != null ) { 
				excludeTaxonomy_Ids_Set_UserInput.addAll( excludeTaxonomy );
			}
			boolean filterNonUniquePeptides = false;
			if( filterNonUniquePeptidesString != null && filterNonUniquePeptidesString.equals( "on" ) )
				filterNonUniquePeptides = true;
			boolean filterOnlyOnePeptide = false;
			if( "on".equals( filterOnlyOnePeptideString ) )
				filterOnlyOnePeptide = true;
			boolean removeNonUniquePSMs = false;
			if( "on".equals( removeNonUniquePSMsString ) )
				removeNonUniquePSMs = true;
			
			List<Integer> searchIdsListSorted = new ArrayList<Integer>( searchIdsSet );
			Collections.sort( searchIdsListSorted );

			LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param = new LinkedPositions_FilterExcludeLinksWith_Param();
			linkedPositions_FilterExcludeLinksWith_Param.setRemoveNonUniquePSMs( removeNonUniquePSMs );

			Map<Integer, List<SearchProteinCrosslinkWrapper>> wrappedCrosslinks_MappedOnSearchId = new HashMap<>();
			for ( SearchDTO searchDTO : searchList ) {
				int projectSearchId = searchDTO.getProjectSearchId();
				int searchId = searchDTO.getSearchId();
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
				if ( searcherCutoffValuesSearchLevel == null ) {
					String msg = "Provided search_id " + projectSearchId + " is not found in psm peptide cutoffs.";
					log.error( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
						CrosslinkLinkedPositions.getInstance()
						.getSearchProteinCrosslinkWrapperList( searchDTO, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );

				// Filter out links if requested
				if( filterNonUniquePeptides 
						|| minPSMs != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT
						|| filterOnlyOnePeptide 
						|| ( excludeTaxonomy != null && excludeTaxonomy.size() > 0 )  ) {
					///////  Output Lists, Results After Filtering
					List<SearchProteinCrosslinkWrapper> wrappedCrosslinksAfterFilter = new ArrayList<>( wrappedCrosslinks.size() );
					for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
						SearchProteinCrosslink link = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
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
						// did they request to removal of non unique peptides?
						if( filterNonUniquePeptides  ) {
							if( link.getNumUniqueLinkedPeptides() < 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of links with less than a specified number of PSMs?
						if( minPSMs != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT ) {
							int psmCountForSearchId = link.getNumPsms();
							if ( psmCountForSearchId < minPSMs ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of links with only one Reported Peptide?
						if( filterOnlyOnePeptide ) {
							int peptideCountForSearchId = link.getNumLinkedPeptides();
							if ( peptideCountForSearchId <= 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						wrappedCrosslinksAfterFilter.add( searchProteinCrosslinkWrapper );
					}
					//  Copy new filtered list to original input variable name to overlay it
					wrappedCrosslinks = wrappedCrosslinksAfterFilter;
				}
				wrappedCrosslinks_MappedOnSearchId.put( projectSearchId, wrappedCrosslinks );
			}
			// build the JSON data structure for crosslinks
			//  Build a Map of maps of <from prot id>, <to prot id>, <from prot position>, <to prot position>, <set of search ids>
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>>> proteinLinkPositions = new HashMap<>();
			for ( Map.Entry<Integer, List<SearchProteinCrosslinkWrapper>> wrappedCrosslinks_MappedOnSearchId_Entry :
				wrappedCrosslinks_MappedOnSearchId.entrySet() ) {
				Integer searchIdForEntry = wrappedCrosslinks_MappedOnSearchId_Entry.getKey();
				List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = wrappedCrosslinks_MappedOnSearchId_Entry.getValue();
				for ( SearchProteinCrosslinkWrapper wrappedCrosslink : wrappedCrosslinks ) {
					SearchProteinCrosslink searchProteinCrosslink = wrappedCrosslink.getSearchProteinCrosslink();
					addToProteinLinkPositions(  
							searchIdForEntry,
							searchProteinCrosslink.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId(), // fromProtId
							searchProteinCrosslink.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId(), // toProtId
							searchProteinCrosslink.getProtein1Position(), // fromProtPosition
							searchProteinCrosslink.getProtein2Position(),  // toProtPosition
							proteinLinkPositions  //  Map to Add to
							);
					//  Add a second time with prot and pos 1 and 2 switched
					addToProteinLinkPositions(  
							searchIdForEntry,
							searchProteinCrosslink.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId(), // fromProtId
							searchProteinCrosslink.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId(), // toProtId
							searchProteinCrosslink.getProtein2Position(), // fromProtPosition
							searchProteinCrosslink.getProtein1Position(),  // toProtPosition
							proteinLinkPositions  //  Map to Add to
							);
				}
			}
			ivd.setProteinLinkPositions( proteinLinkPositions );
			

			byte[] resultJSONasBytes = getResultsByteArray( ivd );
			
			ViewerCrosslinkService_Results_Main_CachedResultManager.getSingletonInstance()
			.saveDataToCache( projectSearchIdListDedupedSorted, resultJSONasBytes, requestQueryString );
			
			return resultJSONasBytes;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}

	/**
	 * @param searchId
	 * @param fromProtId
	 * @param toProtId
	 * @param fromProtPosition
	 * @param toProtPosition
	 * @param proteinLinkPositions
	 */
	private void addToProteinLinkPositions(  
			Integer searchId,
			Integer fromProtId,
			Integer toProtId,
			Integer fromProtPosition,
			Integer toProtPosition,
			//  Map of maps of <from prot id>, <to prot id>, <from prot position>, <to prot position>, <set of search ids>
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>>> proteinLinkPositions  //  Map to Add to
			) {
		Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>> map_keyed_by_toProtId =
				proteinLinkPositions.get( fromProtId );
		if ( map_keyed_by_toProtId == null ) {
			map_keyed_by_toProtId = new HashMap<>();
			proteinLinkPositions.put( fromProtId, map_keyed_by_toProtId );
		}
		Map<Integer, Map<Integer, Set<Integer>>> map_keyed_by_fromProtPosition = map_keyed_by_toProtId.get( toProtId );
		if ( map_keyed_by_fromProtPosition == null ) {
			map_keyed_by_fromProtPosition = new HashMap<>();
			map_keyed_by_toProtId.put( toProtId, map_keyed_by_fromProtPosition );
		}
		Map<Integer, Set<Integer>> map_keyed_by_toProtPosition = map_keyed_by_fromProtPosition.get( fromProtPosition );
		if ( map_keyed_by_toProtPosition == null ) {
			map_keyed_by_toProtPosition = new HashMap<>();
			map_keyed_by_fromProtPosition.put( fromProtPosition, map_keyed_by_toProtPosition );
		}
		Set<Integer> searchIdSet = map_keyed_by_toProtPosition.get( toProtPosition );
		if ( searchIdSet == null ) {
			searchIdSet = new HashSet<>();
			map_keyed_by_toProtPosition.put( toProtPosition, searchIdSet );
		}
		searchIdSet.add( searchId );
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCrosslinkPSMCounts") 
	public byte[] getPSMCounts( 
			@QueryParam( "projectSearchId" ) List<Integer> projectSearchIdList,
			@QueryParam( "psmPeptideCutoffsForProjectSearchIds" ) String psmPeptideCutoffsForProjectSearchIds_JSONString,
			@QueryParam( "minPSMs" ) Integer minPSMs,
			@QueryParam( "filterNonUniquePeptides" ) String filterNonUniquePeptidesString,
			@QueryParam( "filterOnlyOnePeptide" ) String filterOnlyOnePeptideString,
			@QueryParam( "removeNonUniquePSMs" ) String removeNonUniquePSMsString,
			@QueryParam( "excludeTaxonomy" ) List<Integer> excludeTaxonomy,
			@Context HttpServletRequest request )
	throws Exception {
		if ( projectSearchIdList == null || projectSearchIdList.isEmpty() ) {
			String msg = "Provided projectSearchId is null or empty, projectSearchId = " + projectSearchIdList;
			log.error( msg );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		if ( StringUtils.isEmpty( psmPeptideCutoffsForProjectSearchIds_JSONString ) ) {
			String msg = "Provided psmPeptideCutoffsForProjectSearchIds is null or psmPeptideCutoffsForProjectSearchIds is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		if ( minPSMs == null ) {
			String msg = "Provided minPSMs is null or minPSMs is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}
		
		try {
			//   Get the project id for this search
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.addAll( projectSearchIdList );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for projectSearchIdList: ";
				for ( int projectSearchId : projectSearchIdList ) {
					msg += projectSearchId + ", ";
				}				
				log.warn( msg );
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

			String requestQueryString = request.getQueryString();
			
			List<Integer> projectSearchIdListDedupedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdListDedupedSorted );
			
			ViewerCrosslinkService_Results_PsmCount_CachedResultManager_Result cachedResultManager_Result =
					ViewerCrosslinkService_Results_PsmCount_CachedResultManager.getSingletonInstance()
					.retrieveDataFromCache( projectSearchIdListDedupedSorted, requestQueryString );
			
			if ( cachedResultManager_Result != null ) {

				byte[] resultsAsBytes = cachedResultManager_Result.getChartJSONAsBytes();
				if ( resultsAsBytes != null ) {
					
					//  Use JSON cached to disk
					return resultsAsBytes;  //  EARLY EXIT
				}
			}

			ImageViewerData ivd = new ImageViewerData();
			
			//   Get PSM and Peptide Cutoff data from JSON
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization

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
					return o1.getProjectSearchId() - o2.getProjectSearchId();
				}
			});
			
			CutoffValuesRootLevel cutoffValuesRootLevel = null;
			try {
				cutoffValuesRootLevel = jacksonJSON_Mapper.readValue( psmPeptideCutoffsForProjectSearchIds_JSONString, CutoffValuesRootLevel.class );
			} catch ( JsonParseException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', JsonParseException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			} catch ( JsonMappingException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', JsonMappingException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			} catch ( IOException e ) {
				String msg = "Failed to parse 'psmPeptideCutoffsForProjectSearchIds_JSONString', IOException.  psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			}
			
			Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( 
							searchIdsSet, cutoffValuesRootLevel );
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
			
			////////////
			//  Copy Exclude Taxonomy Set for lookup
			Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
			if( excludeTaxonomy != null ) { 
				excludeTaxonomy_Ids_Set_UserInput.addAll( excludeTaxonomy );
			}
			boolean filterNonUniquePeptides = false;
			if( filterNonUniquePeptidesString != null && filterNonUniquePeptidesString.equals( "on" ) )
				filterNonUniquePeptides = true;
			boolean filterOnlyOnePeptide = false;
			if( "on".equals( filterOnlyOnePeptideString ) )
				filterOnlyOnePeptide = true;
			boolean removeNonUniquePSMs = false;
			if( "on".equals( removeNonUniquePSMsString ) )
				removeNonUniquePSMs = true;

			LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param = new LinkedPositions_FilterExcludeLinksWith_Param();
			linkedPositions_FilterExcludeLinksWith_Param.setRemoveNonUniquePSMs( removeNonUniquePSMs );
			
			Map<Integer, List<SearchProteinCrosslinkWrapper>> wrappedCrosslinks_MappedOnSearchId = new HashMap<>();
			for ( SearchDTO searchDTO : searchList ) {
				int projectSearchId = searchDTO.getProjectSearchId();
				int searchId = searchDTO.getSearchId();
				
				SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
				if ( searcherCutoffValuesSearchLevel == null ) {
					String msg = "Provided search_id " + projectSearchId + " is not found in psm peptide cutoffs.";
					log.error( msg );
				    throw new WebApplicationException(
				    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
				    	        .entity( msg )
				    	        .build()
				    	        );
				}
				
				List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
						CrosslinkLinkedPositions.getInstance()
						.getSearchProteinCrosslinkWrapperList( searchDTO, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );

				// Filter out links if requested
				if( filterNonUniquePeptides 
						|| minPSMs != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT
						|| filterOnlyOnePeptide 
						|| ( excludeTaxonomy != null && excludeTaxonomy.size() > 0 )  ) {
					///////  Output Lists, Results After Filtering
					List<SearchProteinCrosslinkWrapper> wrappedCrosslinksAfterFilter = new ArrayList<>( wrappedCrosslinks.size() );
					for ( SearchProteinCrosslinkWrapper searchProteinCrosslinkWrapper : wrappedCrosslinks ) {
						SearchProteinCrosslink link = searchProteinCrosslinkWrapper.getSearchProteinCrosslink();
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
						// did they request to removal of non unique peptides?
						if( filterNonUniquePeptides  ) {
							if( link.getNumUniqueLinkedPeptides() < 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of links with less than a specified number of PSMs?
						if( minPSMs != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT ) {
							int psmCountForSearchId = link.getNumPsms();
							if ( psmCountForSearchId < minPSMs ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						// did they request to removal of links with only one Reported Peptide?
						if( filterOnlyOnePeptide ) {
							int peptideCountForSearchId = link.getNumLinkedPeptides();
							if ( peptideCountForSearchId <= 1 ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
						wrappedCrosslinksAfterFilter.add( searchProteinCrosslinkWrapper );
					}
					//  Copy new filtered list to original input variable name to overlay it
					wrappedCrosslinks = wrappedCrosslinksAfterFilter;
				}
				wrappedCrosslinks_MappedOnSearchId.put( searchId, wrappedCrosslinks );
			}
			// build the JSON data structure for crosslinks
			//  Build a Map of maps of <from prot id>, <to prot id>, <from prot position>, <to prot position>, <psm count>
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>> proteinLinkPositionPsmCount = new HashMap<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>>();
			for ( Map.Entry<Integer, List<SearchProteinCrosslinkWrapper>> wrappedCrosslinks_MappedOnSearchId_Entry :
				wrappedCrosslinks_MappedOnSearchId.entrySet() ) {
				List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = wrappedCrosslinks_MappedOnSearchId_Entry.getValue();
				for ( SearchProteinCrosslinkWrapper wrappedCrosslink : wrappedCrosslinks ) {
					SearchProteinCrosslink searchProteinCrosslink = wrappedCrosslink.getSearchProteinCrosslink();
					Integer numPsms = searchProteinCrosslink.getNumPsms();
					addToProteinLinkPositionPsmCount(  
							numPsms,
							searchProteinCrosslink.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId(), // fromProtId
							searchProteinCrosslink.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId(), // toProtId
							searchProteinCrosslink.getProtein1Position(), // fromProtPosition
							searchProteinCrosslink.getProtein2Position(),  // toProtPosition
							proteinLinkPositionPsmCount  //  Map to Add to
							);
					//  Add a second time with prot and pos 1 and 2 switched
					addToProteinLinkPositionPsmCount(
							numPsms,
							searchProteinCrosslink.getProtein2().getProteinSequenceVersionObject().getProteinSequenceVersionId(), // fromProtId
							searchProteinCrosslink.getProtein1().getProteinSequenceVersionObject().getProteinSequenceVersionId(), // toProtId
							searchProteinCrosslink.getProtein2Position(), // fromProtPosition
							searchProteinCrosslink.getProtein1Position(),  // toProtPosition
							proteinLinkPositionPsmCount  //  Map to Add to
							);
				}
			}
			ivd.setCrosslinkPSMCounts( proteinLinkPositionPsmCount );

			byte[] resultJSONasBytes = getResultsByteArray( ivd );
			
			ViewerCrosslinkService_Results_PsmCount_CachedResultManager.getSingletonInstance()
			.saveDataToCache( projectSearchIdListDedupedSorted, resultJSONasBytes, requestQueryString );
			
			return resultJSONasBytes;
			
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
	 * @param psmCount
	 * @param fromProtId
	 * @param toProtId
	 * @param fromProtPosition
	 * @param toProtPosition
	 * @param proteinLinkPositions
	 */
	private void addToProteinLinkPositionPsmCount(  
			Integer psmCount,
			Integer fromProtId,
			Integer toProtId,
			Integer fromProtPosition,
			Integer toProtPosition,
			//  Map of maps of <from prot id>, <to prot id>, <from prot position>, <to prot position>, <psm count>
			Map<Integer, Map<Integer, Map<Integer, Map<Integer, Integer>>>> proteinLinkPositions  //  Map to Add to
			) {
		Map<Integer, Map<Integer, Map<Integer, Integer>>> map_keyed_by_toProtId =
				proteinLinkPositions.get( fromProtId );
		if ( map_keyed_by_toProtId == null ) {
			map_keyed_by_toProtId = new HashMap<>();
			proteinLinkPositions.put( fromProtId, map_keyed_by_toProtId );
		}
		Map<Integer, Map<Integer, Integer>> map_keyed_by_fromProtPosition = map_keyed_by_toProtId.get( toProtId );
		if ( map_keyed_by_fromProtPosition == null ) {
			map_keyed_by_fromProtPosition = new HashMap<>();
			map_keyed_by_toProtId.put( toProtId, map_keyed_by_fromProtPosition );
		}
		Map<Integer, Integer> map_keyed_by_toProtPosition = map_keyed_by_fromProtPosition.get( fromProtPosition );
		if ( map_keyed_by_toProtPosition == null ) {
			map_keyed_by_toProtPosition = new HashMap<>();
			map_keyed_by_fromProtPosition.put( fromProtPosition, map_keyed_by_toProtPosition );
		}
		map_keyed_by_toProtPosition.put( toProtPosition, psmCount );
	}
	

	/**
	 * @param resultsObject
	 * @param searchId
	 * @return
	 * @throws IOException
	 */
	private byte[] getResultsByteArray( ImageViewerData resultsObject ) throws IOException {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream( );

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();
		//   serialize 
		try {
			jacksonJSON_Mapper.writeValue( baos, resultsObject );
		} catch ( JsonParseException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonParseException.  " ;
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonMappingException.  " ;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to serialize 'resultsObject', IOException. " ;
			log.error( msg, e );
			throw e;
		}
		
		return baos.toByteArray();
	}
	
}
