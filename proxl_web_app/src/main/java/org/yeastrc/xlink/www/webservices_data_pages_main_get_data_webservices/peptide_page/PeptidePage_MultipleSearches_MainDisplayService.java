package org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.peptide_page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.actions.PeptidesMergedCommonPageDownload;
import org.yeastrc.xlink.www.actions.PeptidesMergedCommonPageDownload.PeptidesMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.objects.AnnDisplayNameDescPeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnValuePeptPsmListsPair;
import org.yeastrc.xlink.www.objects.IMergedSearchLink;
import org.yeastrc.xlink.www.objects.SearchCount;
import org.yeastrc.xlink.www.objects.VennDiagramDataToJSON;
import org.yeastrc.xlink.www.objects.WebMergedProteinPosition;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptide;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.web_utils.GenerateVennDiagramDataToJSON;
import org.yeastrc.xlink.www.web_utils.MarshalObjectToJSON;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.peptide_page.PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager.PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager_Result;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;

/**
 * Peptide Page Multiple Searches Main List of Reported Peptides and statistics above the List
 *
 */
@Path("/peptidePage-MultipleSearches-MainDisplay") 
public class PeptidePage_MultipleSearches_MainDisplayService {

	private static final Logger log = LoggerFactory.getLogger( PeptidePage_MultipleSearches_MainDisplayService.class);

	/**
	 *  !!!!!!!!!!!   VERY IMPORTANT  !!!!!!!!!!!!!!!!!!!!
	 * 
	 *  Increment this value whenever change the resulting image since Caching the resulting JSON
	 */
	public static final int VERSION_FOR_CACHING = 1;


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
		MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot = webserviceRequest.mergedPeptideQueryJSONRoot;

		if ( projectSearchIdsList == null || projectSearchIdsList.isEmpty() ) {
			String msg = "Provided projectSearchIds is null or projectSearchIds is missing";
			log.error( msg );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);
		}

		if ( mergedPeptideQueryJSONRoot == null ) {
			String msg = "mergedPeptideQueryJSONRoot is null or mergedPeptideQueryJSONRoot is missing";
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
				PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager_Result result = 
						PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager.getSingletonInstance()
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
			Collection<Integer> searchIds = new HashSet<>();
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
					searchIds.add( search.getSearchId() );
					searchIdsArray[ searchIdsArrayIndex ] = search.getSearchId();
					searchIdsArrayIndex++;
				}
			}
			
			////////     Get Merged Peptides
			PeptidesMergedCommonPageDownloadResult peptidesMergedCommonPageDownloadResult =
					PeptidesMergedCommonPageDownload.getInstance()
					.getWebMergedPeptideRecords(
							null, // form
							mergedPeptideQueryJSONRoot,
							projectSearchIdsListDeduppedSorted,
							searches,
							searchesMapOnSearchId,
							PeptidesMergedCommonPageDownload.FlagCombinedReportedPeptideEntries.YES );
			
			boolean anyResultsHaveIsotopeLabels = peptidesMergedCommonPageDownloadResult.isAnyResultsHaveIsotopeLabels();
			
			List<WebMergedReportedPeptide> webMergedReportedPeptideList = peptidesMergedCommonPageDownloadResult.getWebMergedReportedPeptideList();

			List<WebserviceResponse_PeptideEntry> webserviceResponse_PeptideEntry_ResultList = new ArrayList<>( webMergedReportedPeptideList.size() );
			
			for ( WebMergedReportedPeptide webMergedReportedPeptide : webMergedReportedPeptideList ) {
				//  First create List to indicate which searches the peptide is in
				int numSearches = 0;
				List<Boolean> searchContainsPeptide_SubList = new ArrayList<>( searches.size() );
				for( SearchDTO search : searches ) {
					if( webMergedReportedPeptide.getSearches().contains( search ) ) {
						numSearches++;
						searchContainsPeptide_SubList.add( true );
					} else {
						searchContainsPeptide_SubList.add( false );
					}					
				}
				
				WebserviceResponse_PeptideEntry webserviceResponse_PeptideEntry = new WebserviceResponse_PeptideEntry();
				
				webserviceResponse_PeptideEntry.unifiedReportedPeptideId = webMergedReportedPeptide.getUnifiedReportedPeptideId();
				
				webserviceResponse_PeptideEntry.numSearches = numSearches;
				
				webserviceResponse_PeptideEntry.searchContainsPeptide_SubList = searchContainsPeptide_SubList;

				if ( webMergedReportedPeptide.getMergedSearchPeptideCrosslink() != null ) {
					webserviceResponse_PeptideEntry.linkType = XLinkUtils.CROSS_TYPE_STRING_CAPITAL_CASE;
                } else if ( webMergedReportedPeptide.getMergedSearchPeptideLooplink() != null ) {
                	webserviceResponse_PeptideEntry.linkType = XLinkUtils.LOOP_TYPE_STRING_CAPITAL_CASE;
                } else if ( webMergedReportedPeptide.getMergedSearchPeptideUnlinked() != null ) {
                	webserviceResponse_PeptideEntry.linkType = XLinkUtils.UNLINKED_TYPE_STRING_CAPITAL_CASE;
                } else if ( webMergedReportedPeptide.getMergedSearchPeptideDimer() != null ) {
                	webserviceResponse_PeptideEntry.linkType = XLinkUtils.DIMER_TYPE_STRING_CAPITAL_CASE;
                } else {
                	webserviceResponse_PeptideEntry.linkType = XLinkUtils.UNKNOWN_TYPE_STRING_CAPITAL_CASE;
                }
				
				webserviceResponse_PeptideEntry.peptide_1_Sequence = webMergedReportedPeptide.getPeptide1().getSequence();
				if ( webMergedReportedPeptide.getPeptide2() != null ) {
					webserviceResponse_PeptideEntry.peptide_2_Sequence = webMergedReportedPeptide.getPeptide2().getSequence();
				}
				webserviceResponse_PeptideEntry.peptide_1_Position = webMergedReportedPeptide.getPeptide1Position();
				webserviceResponse_PeptideEntry.peptide_2_Position = webMergedReportedPeptide.getPeptide2Position();
				
				webserviceResponse_PeptideEntry.modsStringPeptide_1 = webMergedReportedPeptide.getModsStringPeptide1();
				webserviceResponse_PeptideEntry.modsStringPeptide_2 = webMergedReportedPeptide.getModsStringPeptide2();
				
				webserviceResponse_PeptideEntry.isotopeLabelsStringPeptide_1 = webMergedReportedPeptide.getIsotopeLabelsStringPeptide1();
				webserviceResponse_PeptideEntry.isotopeLabelsStringPeptide_2 = webMergedReportedPeptide.getIsotopeLabelsStringPeptide2(); 
				
				{ //  Protein Positions for Peptide 1
					List<WebserviceResponse_ProteinSequenceVersionIdAndName> peptide_1_ProteinPositions = null;
					if ( webMergedReportedPeptide.getPeptide1ProteinPositions() != null ) {
						peptide_1_ProteinPositions = new ArrayList<>( webMergedReportedPeptide.getPeptide1ProteinPositions().size() );
						for ( WebMergedProteinPosition webMergedProteinPosition : webMergedReportedPeptide.getPeptide1ProteinPositions() ) {
							WebserviceResponse_ProteinSequenceVersionIdAndName psvn_entry = new WebserviceResponse_ProteinSequenceVersionIdAndName();
							psvn_entry.proteinSequenceVersionId = webMergedProteinPosition.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
							psvn_entry.proteinName = webMergedProteinPosition.getProtein().getName();
							psvn_entry.position1 = webMergedProteinPosition.getPosition1();
							psvn_entry.position2 = webMergedProteinPosition.getPosition2();
							peptide_1_ProteinPositions.add( psvn_entry );
						}
					}
					webserviceResponse_PeptideEntry.peptide_1_ProteinPositions = peptide_1_ProteinPositions;
				}

				{ //  Protein Positions for Peptide 2
					List<WebserviceResponse_ProteinSequenceVersionIdAndName> peptide_2_ProteinPositions = null;
					if ( webMergedReportedPeptide.getPeptide2ProteinPositions() != null ) {
						peptide_2_ProteinPositions = new ArrayList<>( webMergedReportedPeptide.getPeptide2ProteinPositions().size() );
						for ( WebMergedProteinPosition webMergedProteinPosition : webMergedReportedPeptide.getPeptide2ProteinPositions() ) {
							WebserviceResponse_ProteinSequenceVersionIdAndName psvn_entry = new WebserviceResponse_ProteinSequenceVersionIdAndName();
							psvn_entry.proteinSequenceVersionId = webMergedProteinPosition.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
							psvn_entry.proteinName = webMergedProteinPosition.getProtein().getName();
							psvn_entry.position1 = webMergedProteinPosition.getPosition1();
							psvn_entry.position2 = webMergedProteinPosition.getPosition2();
							peptide_2_ProteinPositions.add( psvn_entry );
						}
					}
					webserviceResponse_PeptideEntry.peptide_2_ProteinPositions = peptide_2_ProteinPositions;
				}
				
				webserviceResponse_PeptideEntry.peptidePsmAnnotationValueListsForEachSearch = webMergedReportedPeptide.getPeptidePsmAnnotationValueListsForEachSearch();

				webserviceResponse_PeptideEntry.numPsms = webMergedReportedPeptide.getNumPsms();
				
				webserviceResponse_PeptideEntry_ResultList.add( webserviceResponse_PeptideEntry );
			}

			// build the JSON data structure for searches
			VennDiagramDataToJSON vennDiagramDataToJSON =
					GenerateVennDiagramDataToJSON.createVennDiagramDataToJSON( webMergedReportedPeptideList, searches );

			// get the counts for the number of links for each search, save to map, save to request
			Map<Integer, MutableInt> searchCounts = new TreeMap<Integer, MutableInt>();
			for( IMergedSearchLink link : webMergedReportedPeptideList ) {
				for( SearchDTO search : link.getSearches() ) {
					Integer projectSearchId = search.getSearchId();
					MutableInt searchCount = searchCounts.get( projectSearchId );
					if ( searchCount == null ) {
						searchCount = new MutableInt( 1 );
						searchCounts.put( projectSearchId, searchCount );
					} else {
						searchCount.increment();
					}
				}
			}
			

			List<WebserviceResponse_SearchEntry> searchesList = new ArrayList<>( searches.size() );
			
			List<SearchCount> searchCountList = new ArrayList<>( searches.size() );
			
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


			WebserviceResult webserviceResult = new WebserviceResult();

			webserviceResult.searchesList = searchesList;
			
			webserviceResult.peptideListSize = webserviceResponse_PeptideEntry_ResultList.size();
			webserviceResult.peptideList = webserviceResponse_PeptideEntry_ResultList;

			webserviceResult.peptidePsmAnnotationNameDescListsForEachSearch = peptidesMergedCommonPageDownloadResult.getPeptidePsmAnnotationNameDescListsForEachSearch();
			webserviceResult.anyReportedPeptideEntriesWereCombined = peptidesMergedCommonPageDownloadResult.isAnyReportedPeptideEntriesWereCombined();
			webserviceResult.anyResultsHaveIsotopeLabels = anyResultsHaveIsotopeLabels;
			
			webserviceResult.searchCountList = searchCountList;
			webserviceResult.vennDiagramDataToJSON = vennDiagramDataToJSON;

			byte[] webserviceResultByteArray = MarshalObjectToJSON.getInstance().getJSONByteArray( webserviceResult );

			{
				//  Cache response to disk
				PeptidePage_MultipleSearches_MainDisplayService_CachedResultManager.getSingletonInstance()
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
		private MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot;

		public void setProjectSearchIds(List<Integer> projectSearchIds) {
			this.projectSearchIds = projectSearchIds;
		}

		public void setMergedPeptideQueryJSONRoot(MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot) {
			this.mergedPeptideQueryJSONRoot = mergedPeptideQueryJSONRoot;
		}
	}

	/**
	 * 
	 *
	 */
	public static class WebserviceResult {

		private int projectSearchId;
		private List<WebserviceResponse_PeptideEntry> peptideList;
		private int peptideListSize;
		
		//  For Table Header
		
		private List<AnnDisplayNameDescPeptPsmListsPair> peptidePsmAnnotationNameDescListsForEachSearch;
		private List<WebserviceResponse_SearchEntry> searchesList;
		
		//  Other
		private boolean anyReportedPeptideEntriesWereCombined;
		private boolean anyResultsHaveIsotopeLabels;
		
		private List<SearchCount> searchCountList;
		private VennDiagramDataToJSON vennDiagramDataToJSON;


		public List<WebserviceResponse_PeptideEntry> getPeptideList() {
			return peptideList;
		}
		public int getPeptideListSize() {
			return peptideListSize;
		}
		public int getProjectSearchId() {
			return projectSearchId;
		}
		public List<AnnDisplayNameDescPeptPsmListsPair> getPeptidePsmAnnotationNameDescListsForEachSearch() {
			return peptidePsmAnnotationNameDescListsForEachSearch;
		}
		public boolean isAnyResultsHaveIsotopeLabels() {
			return anyResultsHaveIsotopeLabels;
		}
		public boolean isAnyReportedPeptideEntriesWereCombined() {
			return anyReportedPeptideEntriesWereCombined;
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
	 * 
	 *
	 */
	public static class WebserviceResponse_PeptideEntry {
		
		int unifiedReportedPeptideId;
		
		String linkType;
		
		int numSearches;

		String peptide_1_Sequence;
		String peptide_2_Sequence;
		String peptide_1_Position;
		String peptide_2_Position;
		
		String modsStringPeptide_1;
		String modsStringPeptide_2;
		
		String isotopeLabelsStringPeptide_1;
		String isotopeLabelsStringPeptide_2;

		List<WebserviceResponse_ProteinSequenceVersionIdAndName> peptide_1_ProteinPositions;
		List<WebserviceResponse_ProteinSequenceVersionIdAndName> peptide_2_ProteinPositions;

		List<Boolean> searchContainsPeptide_SubList;

		private List<AnnValuePeptPsmListsPair> peptidePsmAnnotationValueListsForEachSearch;

		int numPsms;


		public String getLinkType() {
			return linkType;
		}

		public String getPeptide_1_Sequence() {
			return peptide_1_Sequence;
		}

		public String getPeptide_2_Sequence() {
			return peptide_2_Sequence;
		}

		public String getPeptide_1_Position() {
			return peptide_1_Position;
		}

		public String getPeptide_2_Position() {
			return peptide_2_Position;
		}

		public List<WebserviceResponse_ProteinSequenceVersionIdAndName> getPeptide_1_ProteinPositions() {
			return peptide_1_ProteinPositions;
		}

		public List<WebserviceResponse_ProteinSequenceVersionIdAndName> getPeptide_2_ProteinPositions() {
			return peptide_2_ProteinPositions;
		}

		public List<Boolean> getSearchContainsPeptide_SubList() {
			return searchContainsPeptide_SubList;
		}

		public int getNumPsms() {
			return numPsms;
		}

		public List<AnnValuePeptPsmListsPair> getPeptidePsmAnnotationValueListsForEachSearch() {
			return peptidePsmAnnotationValueListsForEachSearch;
		}

		public String getModsStringPeptide_1() {
			return modsStringPeptide_1;
		}

		public String getModsStringPeptide_2() {
			return modsStringPeptide_2;
		}

		public String getIsotopeLabelsStringPeptide_1() {
			return isotopeLabelsStringPeptide_1;
		}

		public String getIsotopeLabelsStringPeptide_2() {
			return isotopeLabelsStringPeptide_2;
		}

		public int getNumSearches() {
			return numSearches;
		}

		public int getUnifiedReportedPeptideId() {
			return unifiedReportedPeptideId;
		}


	}

	public static class WebserviceResponse_ProteinSequenceVersionIdAndName {
		private int proteinSequenceVersionId;
		private String proteinName;
		private String position1;
		private String position2;
		public int getProteinSequenceVersionId() {
			return proteinSequenceVersionId;
		}
		public String getProteinName() {
			return proteinName;
		}
		public String getPosition1() {
			return position1;
		}
		public String getPosition2() {
			return position2;
		}
	}


}
