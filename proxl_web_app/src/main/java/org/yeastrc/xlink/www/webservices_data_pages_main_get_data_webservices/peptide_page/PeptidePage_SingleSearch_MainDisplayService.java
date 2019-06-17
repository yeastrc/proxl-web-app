package org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.peptide_page;

import java.util.ArrayList;
import java.util.Collection;
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
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSONRoot;
import org.yeastrc.xlink.www.annotation_display.AnnTypeIdDisplayJSON_PerSearch;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.a_return_data_from_searchers.PeptideWebPageSearcherCacheOptimized;
import org.yeastrc.xlink.www.web_utils.GetLinkTypesForSearchers;
import org.yeastrc.xlink.www.web_utils.MarshalObjectToJSON;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode;
import org.yeastrc.xlink.www.web_utils.SearchPeptideWebserviceCommonCode.SearchPeptideWebserviceCommonCodeGetDataResult;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.cache_results_in_memory.DataPagesMain_GetDataWebservices_CacheResults_InMemory;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.cache_results_in_memory.DataPagesMain_GetDataWebservices_CacheResults_InMemory.DataType;
import org.yeastrc.xlink.www.webservices_data_pages_main_get_data_webservices.peptide_page.PeptidePage_SingleSearch_MainDisplayService_CachedResultManager.PeptidePage_SingleSearch_MainDisplayService_CachedResultManager_Result;
import org.yeastrc.xlink.www.webservices_utils.Unmarshal_RestRequest_JSON_ToObject;
import org.yeastrc.xlink.www.constants.MinimumPSMsConstants;
import org.yeastrc.xlink.www.constants.PeptideViewLinkTypesConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappNoDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import org.yeastrc.xlink.www.form_query_json_objects.PeptideQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_PerSearchResult;
import org.yeastrc.xlink.www.no_data_validation.ThrowExceptionOnNoDataConfig;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinPosition;
import org.yeastrc.xlink.www.objects.WebProteinPosition;
import org.yeastrc.xlink.www.objects.WebReportedPeptide;
import org.yeastrc.xlink.www.objects.WebReportedPeptideWrapper;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;


/**
 * Peptide Page Single Search Main List of Reported Peptides and statistics above the List
 *
 */
@Path("/peptidePage-SingleSearch-MainDisplay") 
public class PeptidePage_SingleSearch_MainDisplayService {

	private static final Logger log = LoggerFactory.getLogger( PeptidePage_SingleSearch_MainDisplayService.class);

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
		
		List<Integer> projectSearchIdList = webserviceRequest.projectSearchIds;
		PeptideQueryJSONRoot peptideQueryJSONRoot = webserviceRequest.peptideQueryJSONRoot;
		
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
		if ( peptideQueryJSONRoot == null ) {
			String msg = "peptideQueryJSONRoot is null or peptideQueryJSONRoot is missing";
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
						.getData( DataType.PEPTIDE_SINGLE_SEARCH, requestJSONBytes );
				if ( cachedWebserviceResponseJSONAsBytes != null ) {
					//  Have Cached response so just return it
					return cachedWebserviceResponseJSONAsBytes;  // EARLY RETURN !!!!!!!!!!!!!!
				}
			}
			{
				//  Next check Cached response on disk
				PeptidePage_SingleSearch_MainDisplayService_CachedResultManager_Result result = 
						PeptidePage_SingleSearch_MainDisplayService_CachedResultManager.getSingletonInstance()
						.retrieveDataFromCache( projectSearchIdList, requestJSONBytes );
				
				byte[] cachedWebserviceResponseJSONAsBytes = result.getWebserviceResponseJSONAsBytes();
				if ( cachedWebserviceResponseJSONAsBytes != null ) {
					//  Have Cached response so just return it

					{  //  Cache response to RAM
						DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
						.putData( DataType.PEPTIDE_SINGLE_SEARCH, requestJSONBytes, cachedWebserviceResponseJSONAsBytes );
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


			//   Update Link Type to default to Crosslink if no value was set
			String[] linkTypesInForm = peptideQueryJSONRoot.getLinkTypes();
			if ( linkTypesInForm == null || linkTypesInForm.length == 0 ) {
				String[] linkTypesCrosslink = { PeptideViewLinkTypesConstants.CROSSLINK_PSM };
				linkTypesInForm = linkTypesCrosslink;
				peptideQueryJSONRoot.setLinkTypes( linkTypesInForm );
			}
			/////////////////////////////////
			//  Get LinkTypes for DB query - Sets to null when all selected as an optimization
			String[] linkTypesForDBQuery = GetLinkTypesForSearchers.getInstance().getLinkTypesForSearchers( linkTypesInForm );
			//   Mods for DB Query
			String[] modsForDBQuery = peptideQueryJSONRoot.getMods();
			
			///////////////////////////////////////////////////////
			String projectSearchIdAsString = Integer.toString( projectSearchId );
			//  Get Cutoff values at search level
			SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = null;
			CutoffValuesSearchLevel cutoffValuesSearchLevel = null;
			CutoffValuesRootLevel cutoffValuesRootLevel = peptideQueryJSONRoot.getCutoffs();
			if ( cutoffValuesRootLevel != null ) {
				if ( cutoffValuesRootLevel.getSearches() != null ) {
					cutoffValuesSearchLevel = peptideQueryJSONRoot.getCutoffs().getSearches().get( projectSearchIdAsString );
				}
			}
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
			
			////////////////////
			//  Get User Selected Peptide Annotation type ids to display
			List<Integer> peptideAnnotationTypeIdsToDisplay = null;
			AnnTypeIdDisplayJSONRoot annTypeIdDisplayRoot =	peptideQueryJSONRoot.getAnnTypeIdDisplay();
			if ( annTypeIdDisplayRoot != null && annTypeIdDisplayRoot.getSearches() != null ) {
				AnnTypeIdDisplayJSON_PerSearch annTypeIdDisplayJSON_PerSearch =
						annTypeIdDisplayRoot.getSearches().get( projectSearchIdAsString );
				if ( annTypeIdDisplayJSON_PerSearch != null ) {
					peptideAnnotationTypeIdsToDisplay = annTypeIdDisplayJSON_PerSearch.getPeptide();
				}
			}
			
			//////////////////////////////////////////////////////////////
			//  Get Peptides from DATABASE
			List<WebReportedPeptideWrapper> wrappedlinks =
					PeptideWebPageSearcherCacheOptimized.getInstance().searchOnSearchIdPsmCutoffPeptideCutoff(
							search, searcherCutoffValuesSearchLevel, linkTypesForDBQuery, modsForDBQuery, 
							PeptideWebPageSearcherCacheOptimized.ReturnOnlyReportedPeptidesWithMonolinks.NO );
			
			//////////////////////////////////////////////////////////////////
			
			// Filter out links if requested, and Update PSM counts if "remove non-unique PSMs" selected 
			
			if( peptideQueryJSONRoot.getMinPSMs() != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT 
					|| peptideQueryJSONRoot.isRemoveNonUniquePSMs()
					|| peptideQueryJSONRoot.isRemoveIntraProteinLinks() ) {
				
				///////  Output Lists, Results After Filtering
				List<WebReportedPeptideWrapper> wrappedlinksAfterFilter = new ArrayList<>( wrappedlinks.size() );

				///  Filter links
				for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedlinks ) {
					WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
					// did the user request to removal of links with only Non-Unique PSMs?
					if( peptideQueryJSONRoot != null && peptideQueryJSONRoot.isRemoveNonUniquePSMs()  ) {
						//  Update webReportedPeptide object to remove non-unique PSMs
						webReportedPeptide.updateNumPsmsToNotInclude_NonUniquePSMs();
						if ( webReportedPeptide.getNumPsms() <= 0 ) {
							// The number of PSMs after update is now zero
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did the user request to removal of links with less than a specified number of PSMs?
					if( peptideQueryJSONRoot.getMinPSMs() != MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT ) {
						int psmCountForSearchId = webReportedPeptide.getNumPsms();
						if ( psmCountForSearchId < peptideQueryJSONRoot.getMinPSMs() ) {
							//  Skip to next entry in list, dropping this entry from output list
							continue;  // EARLY CONTINUE
						}
					}
					// did the user request to removal of links Intra Protein Link
					if( peptideQueryJSONRoot.isRemoveIntraProteinLinks() ) {
						
						SearchPeptideCrosslink searchPeptideCrosslink = webReportedPeptide.getSearchPeptideCrosslink();
						if ( searchPeptideCrosslink != null ) {
							
							boolean foundPeptide_1_protein_In_Peptide_2_proteins = false;
							
							List<SearchProteinPosition> peptide_1_Proteins = searchPeptideCrosslink.getPeptide1ProteinPositions();
							List<SearchProteinPosition> peptide_2_Proteins = searchPeptideCrosslink.getPeptide2ProteinPositions();
							for ( SearchProteinPosition peptide_1_Protein : peptide_1_Proteins ) {
							
								for ( SearchProteinPosition peptide_2_Protein : peptide_2_Proteins ) {
									
									if (       peptide_1_Protein.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() 
											== peptide_2_Protein.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId() ) {
										
										//  Found Same ProteinSequenceVersionId in Proteins for Peptide 1 and Peptide 2
										foundPeptide_1_protein_In_Peptide_2_proteins = true;
										break;
									}
								}
								if ( foundPeptide_1_protein_In_Peptide_2_proteins ) {
									break;
								}
							}
							if ( foundPeptide_1_protein_In_Peptide_2_proteins ) {
								//  Skip to next entry in list, dropping this entry from output list
								continue;  // EARLY CONTINUE
							}
						}
					}
					
					wrappedlinksAfterFilter.add( webReportedPeptideWrapper );
				}

				wrappedlinks = wrappedlinksAfterFilter;
			}

			//  If configured, throw exception if no peptides found
			if ( ThrowExceptionOnNoDataConfig.getInstance().isThrowExceptionNoData() ) {
				if ( wrappedlinks.isEmpty() ) {
					String msg = "No Peptides found and config set for ThrowExceptionNoData";
					log.error( msg );
					throw new ProxlWebappNoDataException( msg );
				}
			}
			
			//  Get Annotation Data for links and Sort Links
			SearchPeptideWebserviceCommonCodeGetDataResult searchPeptideWebserviceCommonCodeGetDataResult =
					SearchPeptideWebserviceCommonCode.getInstance()
					.getPeptideAndPSMDataForLinksAndSortLinks( 
							searchId, 
							wrappedlinks, 
							searcherCutoffValuesSearchLevel, 
							peptideAnnotationTypeIdsToDisplay );
			//  Copy the links out of the wrappers for output - and Copy searched for peptide and psm annotations to link
			List<WebReportedPeptide> links = new ArrayList<>( wrappedlinks.size() );
			for ( WebReportedPeptideWrapper webReportedPeptideWrapper : wrappedlinks ) {
				WebReportedPeptide webReportedPeptide = webReportedPeptideWrapper.getWebReportedPeptide();
				//  Put Annotation data on the link
				SearchPeptideWebserviceCommonCode.getInstance()
				.putPeptideAndPSMDataOnWebserviceResultLinkOject( 
						searchPeptideWebserviceCommonCodeGetDataResult, 
						webReportedPeptideWrapper, // input
						webReportedPeptide );  // updated output
				links.add( webReportedPeptide );
			}
			
			List<WebserviceResponse_ReportedPeptideEntry> resultReportedPeptideEntryList = new ArrayList<>( wrappedlinks.size() );
			for ( WebReportedPeptide webReportedPeptide : links ) {
				
				WebserviceResponse_ReportedPeptideEntry entry = new WebserviceResponse_ReportedPeptideEntry();
                
				if ( webReportedPeptide.getSearchPeptideCrosslink() != null ) {
                	entry.linkType = XLinkUtils.CROSS_TYPE_STRING_CAPITAL_CASE;
                } else if ( webReportedPeptide.getSearchPeptideLooplink() != null ) {
                	entry.linkType = XLinkUtils.LOOP_TYPE_STRING_CAPITAL_CASE;
                } else if ( webReportedPeptide.getSearchPeptideUnlinked() != null ) {
                	entry.linkType = XLinkUtils.UNLINKED_TYPE_STRING_CAPITAL_CASE;
                } else if ( webReportedPeptide.getSearchPeptideDimer() != null ) {
                	entry.linkType = XLinkUtils.DIMER_TYPE_STRING_CAPITAL_CASE;
                } else {
                	entry.linkType = XLinkUtils.UNKNOWN_TYPE_STRING_CAPITAL_CASE;
                }
                
        		List<WebserviceResponse_ProteinSequenceVersionIdAndName> peptide1ProteinPositions = null;
        		if ( webReportedPeptide.getPeptide1ProteinPositions() != null ) {
        			peptide1ProteinPositions = new ArrayList<>( webReportedPeptide.getPeptide1ProteinPositions().size() );
        			for ( WebProteinPosition WebProteinPosition : webReportedPeptide.getPeptide1ProteinPositions() ) {
        				WebserviceResponse_ProteinSequenceVersionIdAndName psvn_entry = new WebserviceResponse_ProteinSequenceVersionIdAndName();
        				psvn_entry.proteinSequenceVersionId = WebProteinPosition.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
        				psvn_entry.proteinName = WebProteinPosition.getProtein().getName();
        				psvn_entry.position1 = WebProteinPosition.getPosition1();
        				psvn_entry.position2 = WebProteinPosition.getPosition2();
        				peptide1ProteinPositions.add( psvn_entry );
        			}
        		}

        		List<WebserviceResponse_ProteinSequenceVersionIdAndName> peptide2ProteinPositions = null;
        		if ( webReportedPeptide.getPeptide2ProteinPositions() != null ) {
        			peptide2ProteinPositions = new ArrayList<>( webReportedPeptide.getPeptide2ProteinPositions().size() );
        			for ( WebProteinPosition WebProteinPosition : webReportedPeptide.getPeptide2ProteinPositions() ) {
        				WebserviceResponse_ProteinSequenceVersionIdAndName psvn_entry = new WebserviceResponse_ProteinSequenceVersionIdAndName();
        				psvn_entry.proteinSequenceVersionId = WebProteinPosition.getProtein().getProteinSequenceVersionObject().getProteinSequenceVersionId();
        				psvn_entry.proteinName = WebProteinPosition.getProtein().getName();
        				psvn_entry.position1 = WebProteinPosition.getPosition1();
        				psvn_entry.position2 = WebProteinPosition.getPosition2();
        				peptide2ProteinPositions.add( psvn_entry );
        			}
        		}
                
				entry.reportedPeptide = webReportedPeptide.getReportedPeptide();
				entry.peptide1 = webReportedPeptide.getPeptide1();
				entry.peptide2 = webReportedPeptide.getPeptide2();
				entry.peptide1Position = webReportedPeptide.getPeptide1Position();
				entry.peptide2Position = webReportedPeptide.getPeptide2Position();
				entry.peptide1ProteinPositions = peptide1ProteinPositions;
				entry.peptide2ProteinPositions = peptide2ProteinPositions;
				entry.psmAnnotationValueList = webReportedPeptide.getPsmAnnotationValueList();
				entry.peptideAnnotationValueList = webReportedPeptide.getPeptideAnnotationValueList();
				entry.numPsms = webReportedPeptide.getNumPsms();
				entry.numNonUniquePsms = webReportedPeptide.getNumNonUniquePsms();
				if ( entry.numNonUniquePsms != 0 ) {
					entry.numNonUniquePsmsNotZero = true;
				}
				resultReportedPeptideEntryList.add( entry );
			}
			
			
			WebserviceResult webserviceResult = new WebserviceResult();
			
			webserviceResult.projectSearchId = projectSearchId;
			webserviceResult.peptideListSize = resultReportedPeptideEntryList.size();
			webserviceResult.peptideList = resultReportedPeptideEntryList;
			webserviceResult.peptideAnnotationDisplayNameDescriptionList =
					searchPeptideWebserviceCommonCodeGetDataResult.getPeptideAnnotationDisplayNameDescriptionList();
			webserviceResult.psmAnnotationDisplayNameDescriptionList =
					searchPeptideWebserviceCommonCodeGetDataResult.getPsmAnnotationDisplayNameDescriptionList();

			byte[] webserviceResultByteArray = MarshalObjectToJSON.getInstance().getJSONByteArray( webserviceResult );

			{  //  Cache response to RAM
				DataPagesMain_GetDataWebservices_CacheResults_InMemory.getInstance()
				.putData( DataType.PEPTIDE_SINGLE_SEARCH, requestJSONBytes, webserviceResultByteArray );
			}
			{
				//  Cache response to disk
				PeptidePage_SingleSearch_MainDisplayService_CachedResultManager.getSingletonInstance()
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
		private PeptideQueryJSONRoot peptideQueryJSONRoot;
		
		public void setProjectSearchIds(List<Integer> projectSearchIds) {
			this.projectSearchIds = projectSearchIds;
		}
		public void setPeptideQueryJSONRoot(PeptideQueryJSONRoot peptideQueryJSONRoot) {
			this.peptideQueryJSONRoot = peptideQueryJSONRoot;
		}
	}
	
	/**
	 * 
	 *
	 */
	public static class WebserviceResult {

		private int projectSearchId;
		private List<WebserviceResponse_ReportedPeptideEntry> peptideList;
		private int peptideListSize;
		private List<AnnotationDisplayNameDescription> peptideAnnotationDisplayNameDescriptionList;
		private List<AnnotationDisplayNameDescription> psmAnnotationDisplayNameDescriptionList;
		
		public List<WebserviceResponse_ReportedPeptideEntry> getPeptideList() {
			return peptideList;
		}
		public int getPeptideListSize() {
			return peptideListSize;
		}
		public List<AnnotationDisplayNameDescription> getPeptideAnnotationDisplayNameDescriptionList() {
			return peptideAnnotationDisplayNameDescriptionList;
		}
		public List<AnnotationDisplayNameDescription> getPsmAnnotationDisplayNameDescriptionList() {
			return psmAnnotationDisplayNameDescriptionList;
		}
		public int getProjectSearchId() {
			return projectSearchId;
		}
	}
	
	/**
	 * 
	 *
	 */
	public static class WebserviceResponse_ReportedPeptideEntry {

		String linkType;
		
		ReportedPeptideDTO reportedPeptide;
		PeptideDTO peptide1;
		PeptideDTO peptide2;
		String peptide1Position;
		String peptide2Position;

		List<WebserviceResponse_ProteinSequenceVersionIdAndName> peptide1ProteinPositions;
		List<WebserviceResponse_ProteinSequenceVersionIdAndName> peptide2ProteinPositions;

		List<String> psmAnnotationValueList;
		List<String> peptideAnnotationValueList;

		int numPsms;
		int numNonUniquePsms;
		
		boolean numNonUniquePsmsNotZero;
		
		
		public String getLinkType() {
			return linkType;
		}
		public ReportedPeptideDTO getReportedPeptide() {
			return reportedPeptide;
		}
		public PeptideDTO getPeptide1() {
			return peptide1;
		}
		public PeptideDTO getPeptide2() {
			return peptide2;
		}
		public String getPeptide1Position() {
			return peptide1Position;
		}
		public String getPeptide2Position() {
			return peptide2Position;
		}
		public List<WebserviceResponse_ProteinSequenceVersionIdAndName> getPeptide1ProteinPositions() {
			return peptide1ProteinPositions;
		}
		public List<WebserviceResponse_ProteinSequenceVersionIdAndName> getPeptide2ProteinPositions() {
			return peptide2ProteinPositions;
		}
		public List<String> getPsmAnnotationValueList() {
			return psmAnnotationValueList;
		}
		public List<String> getPeptideAnnotationValueList() {
			return peptideAnnotationValueList;
		}
		public int getNumPsms() {
			return numPsms;
		}
		public int getNumNonUniquePsms() {
			return numNonUniquePsms;
		}
		public boolean isNumNonUniquePsmsNotZero() {
			return numNonUniquePsmsNotZero;
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
