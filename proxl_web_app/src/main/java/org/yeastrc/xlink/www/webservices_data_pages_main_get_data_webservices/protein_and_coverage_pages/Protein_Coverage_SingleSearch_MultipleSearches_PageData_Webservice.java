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
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.actions.ProteinsMergedProteinsCommon;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.linked_positions.CrosslinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.LinkedPositions_FilterExcludeLinksWith_Param;
import org.yeastrc.xlink.www.linked_positions.LooplinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.UnlinkedDimerPeptideProteinMapping;
import org.yeastrc.xlink.www.linked_positions.UnlinkedDimerPeptideProteinMapping.UnlinkedDimerPeptideProteinMappingResult;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.ProteinCoverageData;
import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinDimer;
import org.yeastrc.xlink.www.objects.SearchProteinDimerWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinked;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinkedWrapper;
import org.yeastrc.xlink.www.protein_coverage.ProteinCoverageCompute;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.web_utils.ExcludeOnTaxonomyForProteinSequenceVersionIdSearchId;
import org.yeastrc.xlink.www.web_utils.MarshalObjectToJSON;
import org.yeastrc.xlink.www.web_utils.TaxonomiesForSearchOrSearches;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.protein_and_coverage_pages.Protein_Coverage_SingleSearch_MultipleSearches_PageData_Webservice_CachedResultManager.Protein_Coverage_SingleSearch_MultipleSearches_PageData_Webservice_CachedResultManager_Result;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;

/**
 * Protein Coverage for Single Search AND Multiple Searches Page Main List of Protein Coverage and Exclude entries above the List
 *
 */
@Path("/proteinPage-Coverage-SingleSearch-MultipleSearches-MainDisplay") 
public class Protein_Coverage_SingleSearch_MultipleSearches_PageData_Webservice {

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
				Protein_Coverage_SingleSearch_MultipleSearches_PageData_Webservice_CachedResultManager_Result result = 
						Protein_Coverage_SingleSearch_MultipleSearches_PageData_Webservice_CachedResultManager.getSingletonInstance()
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


			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			////////   Generic Param processing
			////////////
			//  Copy Exclude Taxonomy and Exclude Protein Sets for lookup
			Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();
			Set<Integer> excludeProtein_Ids_Set_UserInput = new HashSet<>();
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
					excludeProtein_Ids_Set_UserInput.add( proteinId );
				}
			}
			CutoffValuesRootLevel cutoffValuesRootLevel = proteinQueryJSONRoot.getCutoffs();
			Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( searchIdsSet, cutoffValuesRootLevel );
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
			
			LinkedPositions_FilterExcludeLinksWith_Param linkedPositions_FilterExcludeLinksWith_Param = new LinkedPositions_FilterExcludeLinksWith_Param( proteinQueryJSONRoot );
			
			//   Get the Protein Coverage Data
			ProteinCoverageCompute pcs = new ProteinCoverageCompute();
			pcs.setExcludedproteinSequenceVersionIds( proteinQueryJSONRoot.getExcludeproteinSequenceVersionIds() );
			pcs.setExcludedTaxonomyIds( proteinQueryJSONRoot.getExcludeTaxonomy() );
			pcs.setMinPSMs( proteinQueryJSONRoot.getMinPSMs() );
			pcs.setFilterNonUniquePeptides( proteinQueryJSONRoot.isFilterNonUniquePeptides() );
			pcs.setFilterOnlyOnePeptide( proteinQueryJSONRoot.isFilterOnlyOnePeptide() );
			pcs.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );
			pcs.setLinkedPositions_FilterExcludeLinksWith_Param( linkedPositions_FilterExcludeLinksWith_Param );
			pcs.setSearches( searches );
			List<ProteinCoverageData> proteinCoverageDataList = pcs.getProteinCoverageData();
			
			List<WebserviceResponse_ProteinExcludeEntry> proteinExcludeEntryList = null;

			//   Build list of proteins for the protein Exclusion list
			{
				// all possible proteins across all searches (for "Exclude Protein" list on web page)
				Map<Integer,Set<Integer>> allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds = new HashMap<>();
				for ( SearchDTO search : searches ) {
					Integer projectSearchId = search.getProjectSearchId();
//					Integer searchId = search.getSearchId();
					SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = 
							searcherCutoffValuesRootLevel.getPerSearchCutoffs( projectSearchId );
					if ( searcherCutoffValuesSearchLevel == null ) {
						String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + projectSearchId;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					{
						List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
								CrosslinkLinkedPositions.getInstance()
								.getSearchProteinCrosslinkWrapperList( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
						for ( SearchProteinCrosslinkWrapper wrappedCrosslink : wrappedCrosslinks ) {
							SearchProteinCrosslink crosslink = wrappedCrosslink.getSearchProteinCrosslink();
							SearchProtein searchProtein_1 = crosslink.getProtein1();
							SearchProtein searchProtein_2 = crosslink.getProtein2();
							Integer searchProtein_id_1 = searchProtein_1.getProteinSequenceVersionObject().getProteinSequenceVersionId();
							Integer searchProtein_id_2 = searchProtein_2.getProteinSequenceVersionObject().getProteinSequenceVersionId();
							{
								Set<Integer> searchIdsForProtein_1 =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_1 );
								if ( searchIdsForProtein_1 == null ) {
									searchIdsForProtein_1 = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_1, searchIdsForProtein_1 );
								}
								searchIdsForProtein_1.add( search.getSearchId() );
							}
							{
								Set<Integer> searchIdsForProtein_2 =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_2 );
								if ( searchIdsForProtein_2 == null ) {
									searchIdsForProtein_2 = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_2, searchIdsForProtein_2 );
								}
								searchIdsForProtein_2.add( search.getSearchId() );
							}
						}
					}
					{
						List<SearchProteinLooplinkWrapper> wrappedLooplinkLinks = 
								LooplinkLinkedPositions.getInstance()
								.getSearchProteinLooplinkWrapperList( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
						for ( SearchProteinLooplinkWrapper wrappedLooplink : wrappedLooplinkLinks ) {
							SearchProteinLooplink looplink = wrappedLooplink.getSearchProteinLooplink();
							SearchProtein searchProtein = looplink.getProtein();
							Integer searchProtein_id = searchProtein.getProteinSequenceVersionObject().getProteinSequenceVersionId();
							{
								Set<Integer> searchIdsForProtein =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id );
								if ( searchIdsForProtein == null ) {
									searchIdsForProtein = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id, searchIdsForProtein );
								}
								searchIdsForProtein.add( search.getSearchId() );
							}
						}
					}
					{
						UnlinkedDimerPeptideProteinMappingResult unlinkedDimerPeptideProteinMappingResult =
								UnlinkedDimerPeptideProteinMapping.getInstance()
								.getSearchProteinUnlinkedAndDimerWrapperLists( search, searcherCutoffValuesSearchLevel, linkedPositions_FilterExcludeLinksWith_Param );
						List<SearchProteinDimerWrapper> wrappedDimerLinks = 
								unlinkedDimerPeptideProteinMappingResult.getSearchProteinDimerWrapperList();
						for ( SearchProteinDimerWrapper wrappedDimer : wrappedDimerLinks ) {
							SearchProteinDimer dimer = wrappedDimer.getSearchProteinDimer();
							SearchProtein searchProtein_1 = dimer.getProtein1();
							SearchProtein searchProtein_2 = dimer.getProtein2();
							Integer searchProtein_id_1 = searchProtein_1.getProteinSequenceVersionObject().getProteinSequenceVersionId();
							Integer searchProtein_id_2 = searchProtein_2.getProteinSequenceVersionObject().getProteinSequenceVersionId();
							{
								Set<Integer> searchIdsForProtein_1 =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_1 );
								if ( searchIdsForProtein_1 == null ) {
									searchIdsForProtein_1 = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_1, searchIdsForProtein_1 );
								}
								searchIdsForProtein_1.add( search.getSearchId() );
							}
							{
								Set<Integer> searchIdsForProtein_2 =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_2 );
								if ( searchIdsForProtein_2 == null ) {
									searchIdsForProtein_2 = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_2, searchIdsForProtein_2 );
								}
								searchIdsForProtein_2.add( search.getSearchId() );
							}
						}
						List<SearchProteinUnlinkedWrapper> wrappedUnlinkedLinks = 
								unlinkedDimerPeptideProteinMappingResult.getSearchProteinUnlinkedWrapperList();
						for ( SearchProteinUnlinkedWrapper wrappedUnlinked : wrappedUnlinkedLinks ) {
							SearchProteinUnlinked unlinked = wrappedUnlinked.getSearchProteinUnlinked();
							SearchProtein searchProtein = unlinked.getProtein();
							Integer searchProtein_id = searchProtein.getProteinSequenceVersionObject().getProteinSequenceVersionId();
							{
								Set<Integer> searchIdsForProtein =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id );
								if ( searchIdsForProtein == null ) {
									searchIdsForProtein = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id, searchIdsForProtein );
								}
								searchIdsForProtein.add( search.getSearchId() );
							}
						}
					}
				}
				
				proteinExcludeEntryList = new ArrayList<>( allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.size() );

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
										excludeTaxonomy_Ids_Set_UserInput, 
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
//					int mergedSearchProteinTaxonomyId = mergedSearchProtein.getProteinSequenceVersionObject().getTaxonomyId(); 
//
//					if ( excludeTaxonomy_Ids_Set_UserInput.contains( mergedSearchProteinTaxonomyId ) ) {
//						
//						//////////  Taxonomy Id in list of excluded taxonomy ids so drop the record
//						
//						continue;  //   EARLY Continue
//					}

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
			}

			

			///////////////
			// build list of taxonomies to show in exclusion list
			//    puts Map<Integer, String> into request attribute where key is taxonomy id, value is taxonomy name
			Map<Integer, String> taxonomies = 
					TaxonomiesForSearchOrSearches.getInstance().getTaxonomiesForSearchIds( searchIdsSet );

			/////////////////////

			WebserviceResult webserviceResult = new WebserviceResult();

			webserviceResult.proteinCoverageDataList = proteinCoverageDataList;


			webserviceResult.proteinExcludeEntries = proteinExcludeEntryList;

			webserviceResult.taxonomies = taxonomies;

			byte[] webserviceResultByteArray = MarshalObjectToJSON.getInstance().getJSONByteArray( webserviceResult );

			{
				//  Cache response to disk
				Protein_Coverage_SingleSearch_MultipleSearches_PageData_Webservice_CachedResultManager.getSingletonInstance()
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

		private List<ProteinCoverageData> proteinCoverageDataList;

		// build list of taxonomies to show in exclusion list
		Map<Integer, String> taxonomies;
		private List<WebserviceResponse_ProteinExcludeEntry> proteinExcludeEntries;
		
		public Map<Integer, String> getTaxonomies() {
			return taxonomies;
		}
		public List<WebserviceResponse_ProteinExcludeEntry> getProteinExcludeEntries() {
			return proteinExcludeEntries;
		}
		public List<ProteinCoverageData> getProteinCoverageDataList() {
			return proteinCoverageDataList;
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
