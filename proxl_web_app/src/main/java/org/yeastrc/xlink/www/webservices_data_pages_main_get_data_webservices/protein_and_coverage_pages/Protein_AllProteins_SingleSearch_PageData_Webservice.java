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
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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
import org.yeastrc.xlink.www.actions.ProteinsAllCommonAll;
import org.yeastrc.xlink.www.actions.ProteinsMergedProteinsCommon;
import org.yeastrc.xlink.www.actions.ProteinsAllCommonAll.ProteinSingleEntry;
import org.yeastrc.xlink.www.actions.ProteinsAllCommonAll.ProteinsAllCommonAllResult;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId;
import org.yeastrc.xlink.www.annotation.sort_display_records_on_annotation_values.SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId;
import org.yeastrc.xlink.www.web_utils.MarshalObjectToJSON;
import org.yeastrc.xlink.www.web_utils.TaxonomiesForSearchOrSearches;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.cache_results_in_memory.DataPagesMain_GetDataWebservices_CacheResults_InMemory;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.cache_results_in_memory.DataPagesMain_GetDataWebservices_CacheResults_InMemory.DataType;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.protein_and_coverage_pages.Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager.Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager_Result;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;

/**
 * Protein All Protein Single Search Page Main List of Proteins and statistics above the List
 *
 */
@Path("/proteinPage-AllProteins-SingleSearch-MainDisplay") 
public class Protein_AllProteins_SingleSearch_PageData_Webservice {

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
				byte[] cachedWebserviceResponseJSONAsBytes = 
						DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
						.getData( DataType.PROTEINS_ALL_SINGLE_SEARCH, requestJSONBytes );
				if ( cachedWebserviceResponseJSONAsBytes != null ) {
					//  Have Cached response so just return it
					return cachedWebserviceResponseJSONAsBytes;  // EARLY RETURN !!!!!!!!!!!!!!
				}
			}
			{
				//  Next check Cached response on disk
				Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager_Result result = 
						Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager.getSingletonInstance()
						.retrieveDataFromCache( projectSearchIdList, requestJSONBytes );
				
				if ( result != null ) { //  result is null when caching to disk not enabled

					byte[] cachedWebserviceResponseJSONAsBytes = result.getWebserviceResponseJSONAsBytes();
					if ( cachedWebserviceResponseJSONAsBytes != null ) {
						//  Have Cached response so just return it

						{  //  Cache response to RAM
							DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
							.putData( DataType.PROTEINS_ALL_SINGLE_SEARCH, requestJSONBytes, cachedWebserviceResponseJSONAsBytes );
						}

						return cachedWebserviceResponseJSONAsBytes;  // EARLY RETURN !!!!!!!!!!!!!!
					}
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
			
			

			ProteinsAllCommonAllResult proteinsAllCommonAllResult =
					ProteinsAllCommonAll.getInstance().getProteinSingleEntryList(
							null /* onlyReturnThisproteinSequenceVersionId */, 
							search, 
							searchId,
							proteinQueryJSONRoot, 
							excludeTaxonomy_Ids_Set_UserInput, 
							excludeproteinSequenceVersionIds_Set_UserInput,
							searcherCutoffValuesSearchLevel );

			List<ProteinSingleEntry> proteinSingleEntryList = proteinsAllCommonAllResult.getProteinSingleEntryList();
			Set<SearchProtein> searchProteinUnfilteredForSearch = proteinsAllCommonAllResult.getSearchProteinUnfilteredForSearch();

			////   Prepare for Web display
			
			//  presort to have consistent final sort order where annotation data is matching.
			Collections.sort( proteinSingleEntryList, new Comparator<ProteinSingleEntry>() {
				@Override
				public int compare(ProteinSingleEntry o1,
						ProteinSingleEntry o2) {
					return o1.getProteinSequenceVersionId() - o2.getProteinSequenceVersionId();
				}
			} );
			//      Get Annotation data and Sort by Annotation data
			SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt srtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt =
					SrtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchId.getInstance()
					.sortOnBestPeptideBestPSMAnnValuesPopulateAnnValueListsReturnTableHeadersSingleSearchId(
							searchId, 
							proteinSingleEntryList, 
							peptideCutoffsAnnotationTypeDTOList, 
							psmCutoffsAnnotationTypeDTOList );
			
			///////////////////////////////////////////////////////
			Collection<SearchProtein> proteinsForExclusionListCopy = new HashSet<SearchProtein>();
			proteinsForExclusionListCopy.addAll( searchProteinUnfilteredForSearch );
			// build a collection of protein IDs to include
			for( SearchProtein prp : proteinsForExclusionListCopy ) {
				// did they request removal of certain taxonomy IDs?
				if( proteinQueryJSONRoot.getExcludeTaxonomy() != null && proteinQueryJSONRoot.getExcludeTaxonomy().length > 0 ) {
					boolean excludeOnProtein =
							ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId.getInstance()
							.excludeOnTaxonomyForProteinSequenceVersionIdSearchId( 
									excludeTaxonomy_Ids_Set_UserInput, 
									prp.getProteinSequenceVersionObject(), 
									searchId );
					if ( excludeOnProtein ) {
						searchProteinUnfilteredForSearch.remove( prp );
					}
				}
			}
			
			List<WebserviceResponse_ProteinExcludeEntry> proteinExcludeEntries = new ArrayList<>( searchProteinUnfilteredForSearch.size() );
			{
				//  Create Protein Exclude entries
				for ( SearchProtein searchProtein : searchProteinUnfilteredForSearch ) {
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

			//  Build output list of proteins
			List<WebserviceResponse_ProteinEntry> proteinsList = new ArrayList<>( proteinSingleEntryList.size() );
			for ( ProteinSingleEntry proteinSingleEntry : proteinSingleEntryList ) {
				WebserviceResponse_ProteinEntry responseEntry = new WebserviceResponse_ProteinEntry();
				//  Protein
				responseEntry.proteinSequenceVersionId = proteinSingleEntry.getProteinSequenceVersionId();
				try {
					responseEntry.proteinName = proteinSingleEntry.getSearchProtein().getName();
				} catch ( Exception e ) {
					//  Skip if fail
					continue; // EARLY CONTINUE
				}

				//  Other:
				responseEntry.numPsms = proteinSingleEntry.getNumPsms();
				responseEntry.numPeptides = proteinSingleEntry.getNumPeptides();
				responseEntry.numUniquePeptides = proteinSingleEntry.getNumUniquePeptides();
				
				responseEntry.psmAnnotationValueList = proteinSingleEntry.getPsmAnnotationValueList();
				responseEntry.peptideAnnotationValueList = proteinSingleEntry.getPeptideAnnotationValueList();
				
				proteinsList.add( responseEntry );
			}
			
			WebserviceResult webserviceResult = new WebserviceResult();

			webserviceResult.projectSearchId = projectSearchId;
						
			webserviceResult.proteinsList = proteinsList;

			webserviceResult.numProteins = proteinSingleEntryList.size();
			
			webserviceResult.proteinExcludeEntries = proteinExcludeEntries;
			
			webserviceResult.taxonomies = taxonomies;
			
			webserviceResult.peptideAnnotationDisplayNameDescriptionList =
					srtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt.getPeptideAnnotationDisplayNameDescriptionList();
			webserviceResult.psmAnnotationDisplayNameDescriptionList =
					srtOnBestAnnValsPopAnnValListsRetnTblHeadrsSinglSrchIdReslt.getPsmAnnotationDisplayNameDescriptionList();

			byte[] webserviceResultByteArray = MarshalObjectToJSON.getInstance().getJSONByteArray( webserviceResult );

			{  //  Cache response to RAM
				DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
				.putData( DataType.PROTEINS_ALL_SINGLE_SEARCH, requestJSONBytes, webserviceResultByteArray );
			}
			{
				//  Cache response to disk
				Protein_AllProteins_SingleSearch_PageData_Webservice_CachedResultManager.getSingletonInstance()
				.saveDataToCache( projectSearchIdList, webserviceResultByteArray, requestJSONBytes );
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
		
		private List<WebserviceResponse_ProteinEntry> proteinsList;
		
		private int numProteins;
		
		private List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList;
		private List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList;
		
		private List<WebserviceResponse_ProteinExcludeEntry> proteinExcludeEntries;

		// build list of taxonomies to show in exclusion list
		Map<Integer, String> taxonomies;
		
		public int getProjectSearchId() {
			return projectSearchId;
		}
		public List<WebserviceResponse_ProteinEntry> getProteinsList() {
			return proteinsList;
		}
		public int getNumProteins() {
			return numProteins;
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
	 * Entry for Protein
	 *
	 */
	public static class WebserviceResponse_ProteinEntry {

		private int proteinSequenceVersionId;
		private String proteinName;
		
		private int numPsms;
		private int numPeptides;
		private int numUniquePeptides;
		
		private List<String> psmAnnotationValueList;
		private List<String> peptideAnnotationValueList;
		
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
		public int getNumPsms() {
			return numPsms;
		}
		public void setNumPsms(int numPsms) {
			this.numPsms = numPsms;
		}
		public int getNumPeptides() {
			return numPeptides;
		}
		public void setNumPeptides(int numPeptides) {
			this.numPeptides = numPeptides;
		}
		public int getNumUniquePeptides() {
			return numUniquePeptides;
		}
		public void setNumUniquePeptides(int numUniquePeptides) {
			this.numUniquePeptides = numUniquePeptides;
		}
		public List<String> getPsmAnnotationValueList() {
			return psmAnnotationValueList;
		}
		public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
			this.psmAnnotationValueList = psmAnnotationValueList;
		}
		public List<String> getPeptideAnnotationValueList() {
			return peptideAnnotationValueList;
		}
		public void setPeptideAnnotationValueList(List<String> peptideAnnotationValueList) {
			this.peptideAnnotationValueList = peptideAnnotationValueList;
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
