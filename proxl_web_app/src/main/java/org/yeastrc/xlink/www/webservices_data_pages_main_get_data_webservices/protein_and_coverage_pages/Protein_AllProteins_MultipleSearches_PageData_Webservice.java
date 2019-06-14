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
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.actions.ProteinsAllMergedCommonPageDownload;
import org.yeastrc.xlink.www.actions.ProteinsAllMergedCommonPageDownload.AllProteinsMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.actions.ProteinsAllMergedCommonPageDownload.MergedProteinSingleEntry;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.objects.AnnDisplayNameDescPeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnValuePeptPsmListsPair;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.objects.SearchCount;
import org.yeastrc.xlink.www.objects.VennDiagramDataToJSON;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId;
import org.yeastrc.xlink.www.web_utils.GenerateVennDiagramDataToJSON;
import org.yeastrc.xlink.www.web_utils.MarshalObjectToJSON;
import org.yeastrc.xlink.www.web_utils.TaxonomiesForSearchOrSearches;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.protein_and_coverage_pages.Protein_AllProteins_MultipleSearches_PageData_Webservice_CachedResultManager.Protein_AllProteins_MultipleSearches_PageData_Webservice_CachedResultManager_Result;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;

/**
 * Protein All Protein Multiple Searches Page Main List of Proteins and statistics above the List
 *
 */
@Path("/proteinPage-AllProteins-MultipleSearches-MainDisplay") 
public class Protein_AllProteins_MultipleSearches_PageData_Webservice {

	private static final Logger log = LoggerFactory.getLogger( Protein_AllProteins_SingleSearch_PageData_Webservice.class );

	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 */
	static final int VERSION_FOR_CACHING = 1;
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public byte[]
		webserviceMethod( 
				byte[] requestJSONBytes,
				@Context HttpServletRequest request )
	throws Exception {
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

			{
				//  First check Cached response on disk
				Protein_AllProteins_MultipleSearches_PageData_Webservice_CachedResultManager_Result result = 
						Protein_AllProteins_MultipleSearches_PageData_Webservice_CachedResultManager.getSingletonInstance()
						.retrieveDataFromCache( projectSearchIdsList, requestJSONBytes );

				byte[] cachedWebserviceResponseJSONAsBytes = result.getWebserviceResponseJSONAsBytes();
				if ( cachedWebserviceResponseJSONAsBytes != null ) {
					//  Have Cached response so just return it
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



			//  Get Merged Proteins, All Types dependent on user selection
			AllProteinsMergedCommonPageDownloadResult allProteinsMergedCommonPageDownloadResult =
					ProteinsAllMergedCommonPageDownload.getInstance()
					.getAllProteinsWrapped(
							null, // form,
							proteinQueryJSONRoot,
							projectSearchIdsListDeduppedSorted,
							searches,
							searchesMapOnSearchId  );

			List<MergedProteinSingleEntry> proteins = allProteinsMergedCommonPageDownloadResult.getProteins();

			/////////////////////
			// all possible proteins for Crosslinks and Looplinks across all searches (for "Exclude Protein" list on web page)
			Map<Integer,Set<Integer>> allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds = 
					allProteinsMergedCommonPageDownloadResult.getAllProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds();

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
									allProteinsMergedCommonPageDownloadResult.getExcludeTaxonomy_Ids_Set_UserInput(), 
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

			VennDiagramDataToJSON vennDiagramDataToJSON =
					GenerateVennDiagramDataToJSON.createVennDiagramDataToJSON( proteins, searches );

			//////////////////////////////////////////////
			// get the counts for the number of links for each search, save to map, save to request
			//  Temp Map searchCounts to use in next step
			Map<Integer, MutableInt> searchCounts = new TreeMap<Integer, MutableInt>();
			//  Populate Temp Map  searchCounts
			for( MergedProteinSingleEntry link : proteins ) {
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

			List<WebserviceResponse_ProteinEntry> proteinsForResponse = new ArrayList<>( proteins.size() );

			for ( MergedProteinSingleEntry proteinEntry : proteins ) {

				WebserviceResponse_ProteinEntry responseEntry = new WebserviceResponse_ProteinEntry();

				List<Boolean> searchContainsProtein_SubList = new ArrayList<>( searches.size() );

				List<Integer> projectSearchIds_ThisEntry = new ArrayList<>( searches.size() );
				int numSearches = 0;
				for( SearchDTO search : searches ) {
					if( proteinEntry.getSearches().contains( search ) ) {
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
				responseEntry.proteinSequenceVersionId = proteinEntry.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
				try {
					responseEntry.proteinName = proteinEntry.getProtein().getName();
				} catch ( Exception e ) {
					//  Skip if fail
					continue; // EARLY CONTINUE
				}

				//  Other:
				responseEntry.numPsms = proteinEntry.getNumPsms();
				responseEntry.numPeptides = proteinEntry.getNumPeptides();
				responseEntry.numUniquePeptides = proteinEntry.getNumUniquePeptides();

				responseEntry.searchContainsProtein_SubList = searchContainsProtein_SubList;

				responseEntry.peptidePsmAnnotationValueListsForEachSearch = proteinEntry.getPeptidePsmAnnotationValueListsForEachSearch();

				proteinsForResponse.add( responseEntry );
			}

			webserviceResult.proteins = proteinsForResponse;


			webserviceResult.proteinExcludeEntries = proteinExcludeEntryList;

			webserviceResult.numProteins = proteins.size();

			webserviceResult.taxonomies = taxonomies;

			webserviceResult.peptidePsmAnnotationNameDescListsForEachSearch = allProteinsMergedCommonPageDownloadResult.getPeptidePsmAnnotationNameDescListsForEachSearch();
			webserviceResult.searchesList = searchesList;

			webserviceResult.searchCountList = searchCountList;
			webserviceResult.vennDiagramDataToJSON = vennDiagramDataToJSON;

			byte[] webserviceResultByteArray = MarshalObjectToJSON.getInstance().getJSONByteArray( webserviceResult );

			{
				//  Cache response to disk
				Protein_AllProteins_MultipleSearches_PageData_Webservice_CachedResultManager.getSingletonInstance()
				.saveDataToCache( projectSearchIdsList, webserviceResultByteArray, requestJSONBytes );
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

		private List<WebserviceResponse_ProteinEntry> proteins;

		private int numProteins;
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
		public List<WebserviceResponse_ProteinEntry> getProteins() {
			return proteins;
		}

		public int getNumProteins() {
			return numProteins;
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
	 * Entry for Protein
	 *
	 */
	public static class WebserviceResponse_ProteinEntry {

		private int proteinSequenceVersionId;
		private String proteinName;

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
