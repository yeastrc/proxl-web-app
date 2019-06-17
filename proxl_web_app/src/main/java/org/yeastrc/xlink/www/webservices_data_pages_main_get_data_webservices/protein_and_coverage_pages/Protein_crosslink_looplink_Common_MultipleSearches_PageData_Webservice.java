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
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.actions.ProteinsMergedCommonPageDownload;
import org.yeastrc.xlink.www.actions.ProteinsMergedCommonPageDownload.ForCrosslinksOrLooplinkOrBoth;
import org.yeastrc.xlink.www.actions.ProteinsMergedCommonPageDownload.ProteinsMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.objects.AnnDisplayNameDescPeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnValuePeptPsmListsPair;
import org.yeastrc.xlink.www.objects.IMergedSearchLink;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.objects.SearchCount;
import org.yeastrc.xlink.www.objects.VennDiagramDataToJSON;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId;
import org.yeastrc.xlink.www.web_utils.GenerateVennDiagramDataToJSON;
import org.yeastrc.xlink.www.web_utils.MarshalObjectToJSON;
import org.yeastrc.xlink.www.web_utils.TaxonomiesForSearchOrSearches;
import org.yeastrc.xlink.www.web_utils.XLinkWebAppUtils;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.cache_results_in_memory.DataPagesMain_GetDataWebservices_CacheResults_InMemory;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.cache_results_in_memory.DataPagesMain_GetDataWebservices_CacheResults_InMemory.DataType;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.protein_and_coverage_pages.Protein_crosslink_looplink_Common_MultipleSearches_PageData_Webservice_CachedResultManager.Protein_crosslink_looplink_Common_MultipleSearches_PageData_Webservice_CachedResultManager_Result;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;

/**
 * Common code called from Protein_Crosslink_MultipleSearches_PageData_Webservice and Protein_Looplink_MultipleSearches_PageData_Webservice
 * 
 * package private class
 *
 */
class Protein_crosslink_looplink_Common_MultipleSearches_PageData_Webservice {
	
	private static final Logger log = LoggerFactory.getLogger( Protein_crosslink_looplink_Common_MultipleSearches_PageData_Webservice.class );
	

	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 */
	static final int VERSION_FOR_CACHING = 1;
	
	
	private Protein_crosslink_looplink_Common_MultipleSearches_PageData_Webservice() {}
	public static Protein_crosslink_looplink_Common_MultipleSearches_PageData_Webservice getNewInstance() {
		return new Protein_crosslink_looplink_Common_MultipleSearches_PageData_Webservice();
	}

	/**
	 * @param requestJSONBytes
	 * @param crosslinkLooplink
	 * @param request
	 * @return
	 */
	byte[] processRequest( byte[] requestJSONBytes, ForCrosslinksOrLooplinkOrBoth forCrosslinksOrLooplinkOrBoth, HttpServletRequest request ) {

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

		List<Integer> projectSearchIdsList = webserviceRequest.projectSearchIds;
		ProteinQueryJSONRoot proteinQueryJSONRoot = webserviceRequest.proteinQueryJSONRoot;

		if ( projectSearchIdsList == null || projectSearchIdsList.isEmpty() ) {
			String msg = "Provided projectSearchIds is null or projectSearchIds is missing";
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

		
		try {
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			projectSearchIdsSet.addAll( projectSearchIdsList );

			List<Integer> projectIdsFromProjectSearchIds = 
					ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromProjectSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for project search ids: " + StringUtils.join( projectSearchIdsList );
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
				if ( forCrosslinksOrLooplinkOrBoth == ForCrosslinksOrLooplinkOrBoth.CROSSLINKS ) {
					byte[] cachedWebserviceResponseJSONAsBytes = 
							DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
							.getData( DataType.PROTEINS_CROSSLINKS_MULTIPLE_SEARCHES, requestJSONBytes );
					if ( cachedWebserviceResponseJSONAsBytes != null ) {
						//  Have Cached response so just return it
						return cachedWebserviceResponseJSONAsBytes;  // EARLY RETURN !!!!!!!!!!!!!!
					}
				}
				if ( forCrosslinksOrLooplinkOrBoth == ForCrosslinksOrLooplinkOrBoth.LOOPLINKS ) {
					byte[] cachedWebserviceResponseJSONAsBytes = 
							DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
							.getData( DataType.PROTEINS_LOOPLINKS_MULTIPLE_SEARCHES, requestJSONBytes );
					if ( cachedWebserviceResponseJSONAsBytes != null ) {
						//  Have Cached response so just return it
						return cachedWebserviceResponseJSONAsBytes;  // EARLY RETURN !!!!!!!!!!!!!!
					}
				}
			}
			
			{
				//  Next check Cached response on disk
				Protein_crosslink_looplink_Common_MultipleSearches_PageData_Webservice_CachedResultManager_Result result = 
						Protein_crosslink_looplink_Common_MultipleSearches_PageData_Webservice_CachedResultManager.getSingletonInstance()
						.retrieveDataFromCache( forCrosslinksOrLooplinkOrBoth, projectSearchIdsList, requestJSONBytes );
				
				byte[] cachedWebserviceResponseJSONAsBytes = result.getWebserviceResponseJSONAsBytes();
				if ( cachedWebserviceResponseJSONAsBytes != null ) {
					//  Have Cached response so just return it
					
					if ( forCrosslinksOrLooplinkOrBoth == ForCrosslinksOrLooplinkOrBoth.CROSSLINKS ) {
						DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
						.putData( DataType.PROTEINS_CROSSLINKS_MULTIPLE_SEARCHES, requestJSONBytes, cachedWebserviceResponseJSONAsBytes );
					}
					if ( forCrosslinksOrLooplinkOrBoth == ForCrosslinksOrLooplinkOrBoth.LOOPLINKS ) {
						DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
						.putData( DataType.PROTEINS_LOOPLINKS_MULTIPLE_SEARCHES, requestJSONBytes, cachedWebserviceResponseJSONAsBytes );
					}
					
					return cachedWebserviceResponseJSONAsBytes;  // EARLY RETURN !!!!!!!!!!!!!!
				}
			}
		
			
			//////////////////////////////////////////////////////
			////////////////////////////////////////////////////////

			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdsListDeduppedSorted );

			Set<Integer> projectSearchIdsProcessedFromForm = new HashSet<>(); // add each projectSearchId as process in loop next
			
			List<SearchDTO> searches = new ArrayList<SearchDTO>( projectSearchIdsListDeduppedSorted.size() );
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();
			Collection<Integer> searchIdsSet = new HashSet<>();
			int[] searchIdsArray = new int[ projectSearchIdsListDeduppedSorted.size() ];
			int searchIdsArrayIndex = 0;
			for ( int projectSearchId : projectSearchIdsList ) {
				if ( projectSearchIdsProcessedFromForm.add( projectSearchId ) ) {
					//  Haven't processed this projectSearchId yet in this loop so process it now
					SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
					if ( search == null ) {
						String msg = "projectSearchId '" + projectSearchId + "' not found in the database. 400 Error.";
						log.warn( msg );
						throw new WebApplicationException(
								Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
								.entity( "projectSearchId error" )
								.build()
								);
					}
					searches.add( search );
					searchesMapOnSearchId.put( search.getSearchId(), search );
					searchIdsSet.add( search.getSearchId() );
					searchIdsArray[ searchIdsArrayIndex ] = search.getSearchId();
					searchIdsArrayIndex++;
				}
			}
			

			//  Get Merged Proteins, crosslink and looplink
			ProteinsMergedCommonPageDownloadResult proteinsMergedCommonPageDownloadResult =
					ProteinsMergedCommonPageDownload.getInstance()
					.getCrosslinksAndLooplinkWrapped(
							null, // form,
							proteinQueryJSONRoot,
							forCrosslinksOrLooplinkOrBoth,
							projectSearchIdsListDeduppedSorted,
							searches,
							searchesMapOnSearchId  );
			
			List<MergedSearchProteinCrosslink> crosslinks = proteinsMergedCommonPageDownloadResult.getCrosslinks();
			List<MergedSearchProteinLooplink> looplinks = proteinsMergedCommonPageDownloadResult.getLooplinks();
			
			/////////////////////
			// all possible proteins for Crosslinks and Looplinks across all searches (for "Exclude Protein" list on web page)
			Map<Integer,Set<Integer>> allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds = 
					proteinsMergedCommonPageDownloadResult.getAllProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds();
			
			//////////////////////////////
			//////    Process list of all proteins for Crosslinks and Looplinks (before filtering)
			/////                 List used for "Exclude Protein" list on web page
			List<WebserviceResponse_ProteinExcludeEntry> proteinExcludeEntryList = new ArrayList<>( allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.size() );
			
			for ( Map.Entry<Integer, Set<Integer>> entry : allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.entrySet() ) {
				Integer proteinId = entry.getKey();
				Set<Integer> searchIdsForProtein = entry.getValue();
				List<SearchDTO> searchesForProtein = new ArrayList<>( searchIdsForProtein.size() );
				for ( Integer searchIdForProtein : searchIdsForProtein ) {
					SearchDTO searchForProtein = searchesMapOnSearchId.get( searchIdForProtein );
					if ( searchForProtein == null ) {
						String msg = "Processing searchIdsForProtein, no search found in searchesMapOnId for searchIdForProtein : " + searchIdForProtein;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					searchesForProtein.add(searchForProtein);
				}
				ProteinSequenceVersionObject ProteinSequenceObject = new ProteinSequenceVersionObject();
				ProteinSequenceObject.setProteinSequenceVersionId( proteinId );
				MergedSearchProtein mergedSearchProtein = new MergedSearchProtein( searchesForProtein, ProteinSequenceObject );
				//  Exclude protein if excluded for all searches
				boolean excludeTaxonomyIdAllSearches = true;
				for ( SearchDTO searchDTO : searchesForProtein ) {
					boolean excludeOnProtein =
							ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
							.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
									proteinsMergedCommonPageDownloadResult.getExcludeTaxonomy_Ids_Set_UserInput(), 
									mergedSearchProtein.getProteinSequenceVersionObject(), 
									searchDTO.getSearchId() );
					if ( ! excludeOnProtein ) {
						excludeTaxonomyIdAllSearches = false;
						break;
					}
				}
				if ( excludeTaxonomyIdAllSearches ) {
					//////////  Taxonomy Id in list of excluded taxonomy ids so drop the record
					continue;  //   EARLY Continue
				}
//				int mergedSearchProteinTaxonomyId = mergedSearchProtein.getProteinSequenceVersionObject().getTaxonomyId(); 
//
//				if ( proteinsMergedCommonPageDownloadResult.getExcludeTaxonomy_Ids_Set_UserInput().contains( mergedSearchProteinTaxonomyId ) ) {
//					
//					//////////  Taxonomy Id in list of excluded taxonomy ids so drop the record
//					
//					continue;  //   EARLY Continue
//				}
				
				WebserviceResponse_ProteinExcludeEntry webserviceResponse_ProteinExcludeEntry = new WebserviceResponse_ProteinExcludeEntry();
				webserviceResponse_ProteinExcludeEntry.proteinSequenceVersionId = mergedSearchProtein.getProteinSequenceVersionObject().getProteinSequenceVersionId();
				webserviceResponse_ProteinExcludeEntry.proteinName = mergedSearchProtein.getName();
				proteinExcludeEntryList.add( webserviceResponse_ProteinExcludeEntry );
			}
			
			Collections.sort( proteinExcludeEntryList, new Comparator<WebserviceResponse_ProteinExcludeEntry>() {
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
			
			///////////////////////////
			//   These next parts ... as specified by whether crosslinks or looplinks is copied into linksCrosslinksOrLoopLinks
			List<? extends IMergedSearchLink> linksCrosslinksOrLoopLinks = null;
			
		
			if ( forCrosslinksOrLooplinkOrBoth == ForCrosslinksOrLooplinkOrBoth.CROSSLINKS ) {
				//  Crosslink
				linksCrosslinksOrLoopLinks = crosslinks;
			} else if ( forCrosslinksOrLooplinkOrBoth == ForCrosslinksOrLooplinkOrBoth.LOOPLINKS ) {
				//  Looplink
				linksCrosslinksOrLoopLinks = looplinks;
			} else {
				String msg = "forCrosslinksOrLooplinkOrBoth is not recognized. Value is: " + forCrosslinksOrLooplinkOrBoth;
				log.error( msg );
				throw new Exception(msg);
			}
			
			VennDiagramDataToJSON vennDiagramDataToJSON =
					GenerateVennDiagramDataToJSON.createVennDiagramDataToJSON( linksCrosslinksOrLoopLinks, searches );

			//////////////////////////////////////////////
			// get the counts for the number of links for each search, save to map, save to request
			//  Temp Map searchCounts to use in next step
			Map<Integer, MutableInt> searchCounts = new TreeMap<Integer, MutableInt>();
			//  Populate Temp Map  searchCounts
			for( IMergedSearchLink link : linksCrosslinksOrLoopLinks ) {
				for( SearchDTO search : link.getSearches() ) {
					Integer searchId = search.getSearchId();
					MutableInt searchCount = searchCounts.get( searchId );
					if ( searchCount == null ) {
						searchCount = new MutableInt( 1 );
						searchCounts.put( searchId, searchCount );
					} else {
						searchCount.increment();
					}
				}
			}
			
			//   Take values in Temp Map searchCounts and put them in a list in Search Id order with Search Data
			List<WebserviceResponse_SearchEntry> searchesList = new ArrayList<>( searches.size() );
			List<SearchCount> searchCountList = new ArrayList<>();

			for ( SearchDTO search : searches  ) {
				
				WebserviceResponse_SearchEntry searchEntry = new WebserviceResponse_SearchEntry();
				searchesList.add( searchEntry );
				searchEntry.projectSearchId = search.getProjectSearchId();
				searchEntry.searchId = search.getSearchId();
				searchEntry.searchName = search.getName();
				
				Integer searchId = search.getSearchId();
				MutableInt searchCountMapValue = searchCounts.get( searchId );
				SearchCount searchCount = new SearchCount();
				searchCountList.add(searchCount);
				searchCount.setSearchId( searchId );
				searchCount.setProjectSearchId( search.getProjectSearchId() );
				if ( searchCountMapValue != null ) {
					searchCount.setCount( searchCountMapValue.intValue() );
				} else {
					searchCount.setCount( 0 );
				}
			}

			///////////////
			// build list of taxonomies to show in exclusion list
			//    puts Map<Integer, String> into request attribute where key is taxonomy id, value is taxonomy name
			Map<Integer, String> taxonomies = 
					TaxonomiesForSearchOrSearches.getInstance().getTaxonomiesForSearchIds( searchIdsSet );

			/////////////////////

			WebserviceResult webserviceResult = new WebserviceResult();

			if ( forCrosslinksOrLooplinkOrBoth == ForCrosslinksOrLooplinkOrBoth.CROSSLINKS ) {

				List<WebserviceResponse_CrosslinkProteinEntry> crosslinksForResponse = new ArrayList<>( crosslinks.size() );
				
				for ( MergedSearchProteinCrosslink crosslinkEntry : crosslinks ) {
					
					WebserviceResponse_CrosslinkProteinEntry responseEntry = new WebserviceResponse_CrosslinkProteinEntry();

					List<Boolean> searchContainsProtein_SubList = new ArrayList<>( searches.size() );
					
					List<Integer> projectSearchIds_ThisEntry = new ArrayList<>( searches.size() );
					int numSearches = 0;
					for( SearchDTO search : searches ) {
						if( crosslinkEntry.getSearches().contains( search ) ) {
							numSearches++;
							searchContainsProtein_SubList.add( true );
						} else {
							searchContainsProtein_SubList.add( false );
						}
						projectSearchIds_ThisEntry.add( search.getProjectSearchId() );
					}
					
					responseEntry.projectSearchIds_ThisEntry = projectSearchIds_ThisEntry;
					
					responseEntry.numSearches = numSearches;
					
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
					
					responseEntry.searchContainsProtein_SubList = searchContainsProtein_SubList;
					
					responseEntry.peptidePsmAnnotationValueListsForEachSearch = crosslinkEntry.getPeptidePsmAnnotationValueListsForEachSearch();

					crosslinksForResponse.add( responseEntry );
				}
					
				webserviceResult.crosslinks = crosslinksForResponse;
			
			} else if ( forCrosslinksOrLooplinkOrBoth == ForCrosslinksOrLooplinkOrBoth.LOOPLINKS ) {

				List<WebserviceResponse_LooplinkProteinEntry> looplinksForResponse = new ArrayList<>( looplinks.size() );

				for ( MergedSearchProteinLooplink looplinkEntry : looplinks ) {

					WebserviceResponse_LooplinkProteinEntry responseEntry = new WebserviceResponse_LooplinkProteinEntry();

					List<Boolean> searchContainsProtein_SubList = new ArrayList<>( searches.size() );

					List<Integer> projectSearchIds_ThisEntry = new ArrayList<>( searches.size() );
					int numSearches = 0;
					for( SearchDTO search : searches ) {
						if( looplinkEntry.getSearches().contains( search ) ) {
							numSearches++;
							searchContainsProtein_SubList.add( true );
						} else {
							searchContainsProtein_SubList.add( false );
						}
						projectSearchIds_ThisEntry.add( search.getProjectSearchId() );
					}

					responseEntry.projectSearchIds_ThisEntry = projectSearchIds_ThisEntry;

					responseEntry.numSearches = numSearches;

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

					responseEntry.searchContainsProtein_SubList = searchContainsProtein_SubList;

					responseEntry.peptidePsmAnnotationValueListsForEachSearch = looplinkEntry.getPeptidePsmAnnotationValueListsForEachSearch();

					looplinksForResponse.add( responseEntry );
				}

				webserviceResult.looplinks = looplinksForResponse;

			} else {
				String msg = "forCrosslinksOrLooplinkOrBoth is not recognized. Value is: " + forCrosslinksOrLooplinkOrBoth;
				log.error( msg );
				throw new Exception(msg);
			}
			
			
			webserviceResult.proteinExcludeEntries = proteinExcludeEntryList;
			
			webserviceResult.numCrosslinks = crosslinks.size();
			webserviceResult.numLooplinks = looplinks.size();
			webserviceResult.numLinks = looplinks.size() + crosslinks.size();
			webserviceResult.numDistinctLinks = XLinkWebAppUtils.getNumUDRs( crosslinks, looplinks );

			webserviceResult.taxonomies = taxonomies;
			
			webserviceResult.peptidePsmAnnotationNameDescListsForEachSearch = proteinsMergedCommonPageDownloadResult.getPeptidePsmAnnotationNameDescListsForEachSearch();
			webserviceResult.searchesList = searchesList;
			
			webserviceResult.searchCountList = searchCountList;
			webserviceResult.vennDiagramDataToJSON = vennDiagramDataToJSON;

			byte[] webserviceResultByteArray = MarshalObjectToJSON.getInstance().getJSONByteArray( webserviceResult );

			{  //  Cache response to RAM
				if ( forCrosslinksOrLooplinkOrBoth == ForCrosslinksOrLooplinkOrBoth.CROSSLINKS ) {
					DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
					.putData( DataType.PROTEINS_CROSSLINKS_MULTIPLE_SEARCHES, requestJSONBytes, webserviceResultByteArray );
				}
				if ( forCrosslinksOrLooplinkOrBoth == ForCrosslinksOrLooplinkOrBoth.LOOPLINKS ) {
					DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
					.putData( DataType.PROTEINS_LOOPLINKS_MULTIPLE_SEARCHES, requestJSONBytes, webserviceResultByteArray );
				}
			}
			{
				//  Cache response to disk
				Protein_crosslink_looplink_Common_MultipleSearches_PageData_Webservice_CachedResultManager.getSingletonInstance()
				.saveDataToCache( forCrosslinksOrLooplinkOrBoth, projectSearchIdsList, webserviceResultByteArray, requestJSONBytes );
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
			
			private List<WebserviceResponse_ProteinExcludeEntry> proteinExcludeEntries;
			
			//  For Table Header
			private List<AnnDisplayNameDescPeptPsmListsPair> peptidePsmAnnotationNameDescListsForEachSearch;
			private List<WebserviceResponse_SearchEntry> searchesList;
			
			//  Other
			private List<SearchCount> searchCountList;
			private VennDiagramDataToJSON vennDiagramDataToJSON;

			
			
			public int getProjectSearchId() {
				return projectSearchId;
			}
			public List<WebserviceResponse_CrosslinkProteinEntry> getCrosslinks() {
				return crosslinks;
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
			public List<WebserviceResponse_ProteinExcludeEntry> getProteinExcludeEntries() {
				return proteinExcludeEntries;
			}
			public List<AnnDisplayNameDescPeptPsmListsPair> getPeptidePsmAnnotationNameDescListsForEachSearch() {
				return peptidePsmAnnotationNameDescListsForEachSearch;
			}
			public List<WebserviceResponse_SearchEntry> getSearchesList() {
				return searchesList;
			}
			public List<SearchCount> getSearchCountList() {
				return searchCountList;
			}
			public VennDiagramDataToJSON getVennDiagramDataToJSON() {
				return vennDiagramDataToJSON;
			}
			public List<WebserviceResponse_LooplinkProteinEntry> getLooplinks() {
				return looplinks;
			}
		}

		/**
		 * For the Table Header
		 *
		 */
		public static class WebserviceResponse_SearchEntry {
			
			int projectSearchId;
			int searchId;
			String searchName;
			
			public int getProjectSearchId() {
				return projectSearchId;
			}
			public int getSearchId() {
				return searchId;
			}
			public String getSearchName() {
				return searchName;
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

			List<Boolean> searchContainsProtein_SubList;
			
			List<Integer> projectSearchIds_ThisEntry;
			
			int numSearches;
			
			private List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch;

			
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
			public List<Boolean> getSearchContainsProtein_SubList() {
				return searchContainsProtein_SubList;
			}
			public List<AnnValuePeptPsmListsPair> getPeptidePsmAnnotationValueListsForEachSearch() {
				return peptidePsmAnnotationValueListsForEachSearch;
			}
			public int getNumSearches() {
				return numSearches;
			}
			public List<Integer> getProjectSearchIds_ThisEntry() {
				return projectSearchIds_ThisEntry;
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

			List<Boolean> searchContainsProtein_SubList;
			
			List<Integer> projectSearchIds_ThisEntry;
			
			int numSearches;
			
			private List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch;
			
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
			public List<Boolean> getSearchContainsProtein_SubList() {
				return searchContainsProtein_SubList;
			}
			public List<Integer> getProjectSearchIds_ThisEntry() {
				return projectSearchIds_ThisEntry;
			}
			public int getNumSearches() {
				return numSearches;
			}
			public List<AnnValuePeptPsmListsPair> getPeptidePsmAnnotationValueListsForEachSearch() {
				return peptidePsmAnnotationValueListsForEachSearch;
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
