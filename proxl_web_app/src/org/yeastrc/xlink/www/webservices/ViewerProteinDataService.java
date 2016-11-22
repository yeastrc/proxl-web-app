package org.yeastrc.xlink.www.webservices;

import java.io.IOException;
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
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.factories.ProteinSequenceObjectFactory;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.linkable_positions.GetLinkablePositionsForLinkers;
import org.yeastrc.xlink.www.linked_positions.CrosslinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.LooplinkLinkedPositions;
import org.yeastrc.xlink.www.linked_positions.UnlinkedDimerPeptideProteinMapping;
import org.yeastrc.xlink.www.linked_positions.UnlinkedDimerPeptideProteinMapping.UnlinkedDimerPeptideProteinMappingResult;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinDimer;
import org.yeastrc.xlink.www.objects.SearchProteinDimerWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinked;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinkedWrapper;
import org.yeastrc.xlink.www.searcher.LinkersForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.TaxonomyIdsForProtSeqIdSearchIdSearcher;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.web_utils.TaxonomyUtils;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.objects.ImageViewerData;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Path("/imageViewer")
public class ViewerProteinDataService {

	private static final Logger log = Logger.getLogger(ViewerProteinDataService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getProteinData") 
	public ImageViewerData getViewerData( 
			@QueryParam( "searchIds" ) List<Integer> searchIdsParam,

			@QueryParam( "psmPeptideCutoffsForSearchIds" ) String psmPeptideCutoffsForSearchIds_JSONString,

			@QueryParam( "filterNonUniquePeptides" ) String filterNonUniquePeptidesString,
			@QueryParam( "filterOnlyOnePSM" ) String filterOnlyOnePSMString,
			@QueryParam( "filterOnlyOnePeptide" ) String filterOnlyOnePeptideString,
			@QueryParam( "excludeTaxonomy" ) List<Integer> excludeTaxonomy,
			@QueryParam( "excludeType" ) List<Integer> excludeType,
			@Context HttpServletRequest request )
	throws Exception {

//		if (true)
//		throw new Exception("Forced Error");
		
		if ( searchIdsParam == null || searchIdsParam.isEmpty() ) {

			String msg = "Provided searchIds is null or empty, searchIds = " + searchIdsParam;

			log.error( msg );

		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );
		}
		
		if ( StringUtils.isEmpty( psmPeptideCutoffsForSearchIds_JSONString ) ) {

			String msg = "Provided psmPeptideCutoffsForSearchIds is null or psmPeptideCutoffsForSearchIds is missing";

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



			ImageViewerData ivd = new ImageViewerData();
			
			//  Dedup SearchIds

			Set<Integer> searchIdsSet = new HashSet<Integer>( );
			
			for ( int searchId : searchIdsParam ) {

				searchIdsSet.add( searchId );
			}
			

			
			
			if( excludeTaxonomy == null ) 
				excludeTaxonomy = new ArrayList<Integer>();

			boolean filterNonUniquePeptides = false;
			if( filterNonUniquePeptidesString != null && filterNonUniquePeptidesString.equals( "on" ) )
				filterNonUniquePeptides = true;

			boolean filterOnlyOnePSM = false;
			if( "on".equals( filterOnlyOnePSMString ) )
				filterOnlyOnePSM = true;

			boolean filterOnlyOnePeptide = false;
			if( "on".equals( filterOnlyOnePeptideString ) )
				filterOnlyOnePeptide = true;

			
			
			//  Copy Exclude Taxonomy and Exclude Protein Sets for lookup

			Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();

			if ( excludeTaxonomy != null ) {

				for ( Integer taxonomyId : excludeTaxonomy ) {
				
					excludeTaxonomy_Ids_Set_UserInput.add( taxonomyId );
				}
			}

			
			

			if ( searchIdsParam.isEmpty() ) {
				
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT ) // This string will be passed to the client
						.build()
						);
			}

			
			//   Get the project id for this search
						
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsSet );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int searchId : searchIdsSet ) {

					msg += searchId + ", ";
				}				
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
			
			
			List<Integer> searchIdsListDedupedSorted = new ArrayList<>( searchIdsSet );

			
			Collections.sort( searchIdsListDedupedSorted );
			
			

			//   Get PSM and Peptide Cutoff data from JSON


			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization


			CutoffValuesRootLevel cutoffValuesRootLevel = null;

			try {
				cutoffValuesRootLevel = jacksonJSON_Mapper.readValue( psmPeptideCutoffsForSearchIds_JSONString, CutoffValuesRootLevel.class );

			} catch ( JsonParseException e ) {

				String msg = "Failed to parse 'psmPeptideCutoffsForSearchIds_JSONString', JsonParseException.  psmPeptideCutoffsForSearchIds_JSONString: " + psmPeptideCutoffsForSearchIds_JSONString;
				log.error( msg, e );
				throw e;

			} catch ( JsonMappingException e ) {

				String msg = "Failed to parse 'psmPeptideCutoffsForSearchIds_JSONString', JsonMappingException.  psmPeptideCutoffsForSearchIds_JSONString: " + psmPeptideCutoffsForSearchIds_JSONString;
				log.error( msg, e );
				throw e;

			} catch ( IOException e ) {

				String msg = "Failed to parse 'psmPeptideCutoffsForSearchIds_JSONString', IOException.  psmPeptideCutoffsForSearchIds_JSONString: " + psmPeptideCutoffsForSearchIds_JSONString;
				log.error( msg, e );
				throw e;
			}
			

			/////////////


			Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( 
							searchIdsSet, cutoffValuesRootLevel ); 
			
			
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
			
			
			
			
			

			// Create collection with all possible proteins included in this set of  searches for this type

			//  Keyed on proteinSequenceId
			Map<Integer, List<SearchDTO>> searchDTOsKeyedOnProteinSequenceIdsMap = new HashMap<>();
			
			
			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			
			for( int searchId : searchIdsListDedupedSorted ) {
				
				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {
					
					String msg = "Search not found in DB for searchId: " + searchId;
					
					log.error( msg );

					throw new WebApplicationException(
							Response.status( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_STATUS_CODE )  //  Send HTTP code
							.entity( WebServiceErrorMessageConstants.INVALID_SEARCH_LIST_NOT_IN_DB_TEXT ) // This string will be passed to the client
							.build()
							);
				}
				
				searches.add( search );

				
				SearchDTO searchDTO = SearchDAO.getInstance().getSearch( searchId );

				SearcherCutoffValuesSearchLevel	searcherCutoffValuesSearchLevel =
						searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );
				
				if ( searcherCutoffValuesSearchLevel == null ) {
					
					searcherCutoffValuesSearchLevel = new SearcherCutoffValuesSearchLevel();
				}
				
				//////////////////////////

				///   Get Crosslink Proteins from DB

				List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
						CrosslinkLinkedPositions.getInstance()
						.getSearchProteinCrosslinkWrapperList( search, searcherCutoffValuesSearchLevel );
				
				
				for ( SearchProteinCrosslinkWrapper wrappedItem : wrappedCrosslinks ) {

					SearchProteinCrosslink item = wrappedItem.getSearchProteinCrosslink();
					
					Integer proteinId_1 = item.getProtein1().getProteinSequenceObject().getProteinSequenceId();
					Integer proteinId_2 = item.getProtein2().getProteinSequenceObject().getProteinSequenceId();

					{
						List<SearchDTO> searchDTOListForProteinId = searchDTOsKeyedOnProteinSequenceIdsMap.get( proteinId_1 );

						if ( searchDTOListForProteinId == null ) {

							searchDTOListForProteinId = new ArrayList<>();
							searchDTOsKeyedOnProteinSequenceIdsMap.put( proteinId_1, searchDTOListForProteinId );
						}

						searchDTOListForProteinId.add(searchDTO);
					}
					{
						List<SearchDTO> searchDTOListForProteinId = searchDTOsKeyedOnProteinSequenceIdsMap.get( proteinId_2 );

						if ( searchDTOListForProteinId == null ) {

							searchDTOListForProteinId = new ArrayList<>();
							searchDTOsKeyedOnProteinSequenceIdsMap.put( proteinId_2, searchDTOListForProteinId );
						}

						searchDTOListForProteinId.add(searchDTO);
					}
				}

				//////////////////////////

				///   Get Looplink Proteins from DB

				List<SearchProteinLooplinkWrapper> wrappedLooplinks = 
						LooplinkLinkedPositions.getInstance()
						.getSearchProteinLooplinkWrapperList( search, searcherCutoffValuesSearchLevel );
				
				
				for ( SearchProteinLooplinkWrapper wrappedItem : wrappedLooplinks ) {

					SearchProteinLooplink item = wrappedItem.getSearchProteinLooplink();
					
					Integer proteinId = item.getProtein().getProteinSequenceObject().getProteinSequenceId();

					List<SearchDTO> searchDTOListForProteinId = searchDTOsKeyedOnProteinSequenceIdsMap.get( proteinId );

					if ( searchDTOListForProteinId == null ) {

						searchDTOListForProteinId = new ArrayList<>();
						searchDTOsKeyedOnProteinSequenceIdsMap.put( proteinId, searchDTOListForProteinId );
					}

					searchDTOListForProteinId.add(searchDTO);
				}

				boolean includeUnlinkedAndDimer = true;

				if ( excludeType != null ) {
					for( int type : excludeType ) {
						if( type == XLinkUtils.TYPE_UNLINKED ) {
							includeUnlinkedAndDimer = false;
							break;
						}
					}
				}


				if ( includeUnlinkedAndDimer ) {

					//////////////////////////

					///   Get Dimer and Unlinked Proteins from DB
					
					
					UnlinkedDimerPeptideProteinMappingResult unlinkedDimerPeptideProteinMappingResult =
							UnlinkedDimerPeptideProteinMapping.getInstance()
							.getSearchProteinUnlinkedAndDimerWrapperLists( searchDTO, searcherCutoffValuesSearchLevel );


//					List<SearchProteinDimerWrapper> wrappedDimers = 
//							SearchProteinDimerSearcher.getInstance().searchOnSearchIdandCutoffs( searchDTO, searcherCutoffValuesSearchLevel );
					
					List<SearchProteinDimerWrapper> wrappedDimers = 
							unlinkedDimerPeptideProteinMappingResult.getSearchProteinDimerWrapperList();

					for ( SearchProteinDimerWrapper wrappedItem : wrappedDimers ) {

						SearchProteinDimer item = wrappedItem.getSearchProteinDimer();
						
						Integer proteinId_1 = item.getProtein1().getProteinSequenceObject().getProteinSequenceId();
						Integer proteinId_2 = item.getProtein2().getProteinSequenceObject().getProteinSequenceId();

						{
							List<SearchDTO> searchDTOListForProteinId = searchDTOsKeyedOnProteinSequenceIdsMap.get( proteinId_1 );

							if ( searchDTOListForProteinId == null ) {

								searchDTOListForProteinId = new ArrayList<>();
								searchDTOsKeyedOnProteinSequenceIdsMap.put( proteinId_1, searchDTOListForProteinId );
							}

							searchDTOListForProteinId.add(searchDTO);
						}
						{
							List<SearchDTO> searchDTOListForProteinId = searchDTOsKeyedOnProteinSequenceIdsMap.get( proteinId_2 );

							if ( searchDTOListForProteinId == null ) {

								searchDTOListForProteinId = new ArrayList<>();
								searchDTOsKeyedOnProteinSequenceIdsMap.put( proteinId_2, searchDTOListForProteinId );
							}

							searchDTOListForProteinId.add(searchDTO);
						}
						
						
					}
					

					//////////////////////////

					///   Get Unlinked Proteins from DB


//					List<SearchProteinUnlinkedWrapper> wrappedUnlinkeds = 
//							SearchProteinUnlinkedSearcher.getInstance().searchOnSearchIdandCutoffs( searchDTO, searcherCutoffValuesSearchLevel );
					
					List<SearchProteinUnlinkedWrapper> wrappedUnlinkeds = 
							unlinkedDimerPeptideProteinMappingResult.getSearchProteinUnlinkedWrapperList();

					for ( SearchProteinUnlinkedWrapper wrappedItem : wrappedUnlinkeds ) {

						SearchProteinUnlinked item = wrappedItem.getSearchProteinUnlinked();
						
						Integer proteinId = item.getProtein().getProteinSequenceObject().getProteinSequenceId();

						List<SearchDTO> searchDTOListForProteinId = searchDTOsKeyedOnProteinSequenceIdsMap.get( proteinId );

						if ( searchDTOListForProteinId == null ) {

							searchDTOListForProteinId = new ArrayList<>();
							searchDTOsKeyedOnProteinSequenceIdsMap.put( proteinId, searchDTOListForProteinId );
						}

						searchDTOListForProteinId.add(searchDTO);
						
						
					}
				}

			}

			
			
			

			// create the collection of proteins we're going to include

			Collection<MergedSearchProtein> proteins = new ArrayList<MergedSearchProtein>();
			
			for ( Map.Entry<Integer, List<SearchDTO>> item : searchDTOsKeyedOnProteinSequenceIdsMap.entrySet() ) {

				MergedSearchProtein mergedSearchProtein =
						new MergedSearchProtein( item.getValue(), ProteinSequenceObjectFactory.getProteinSequenceObject( item.getKey() ) );

				proteins.add( mergedSearchProtein );
			}

			// build list of taxonomies to show in exclusion list
			Map<Integer, String> taxonomies = new HashMap<Integer,String>();
			for( MergedSearchProtein mp : proteins ) {
				
				for ( SearchDTO searchDTO : mp.getSearchs() ) {

					//  Get all taxonomy ids for protein sequence id and search id

					Set<Integer> taxonomyIds = 
							TaxonomyIdsForProtSeqIdSearchIdSearcher.getInstance().getTaxonomyIdsSingleSearch( mp.getProteinSequenceObject(), searchDTO.getId() );

					for ( Integer taxonomyId : taxonomyIds ) {

						if( taxonomies.containsKey( taxonomyId ) ) { 
							continue;
						}
						taxonomies.put( taxonomyId, TaxonomyUtils.getTaxonomyName( taxonomyId ) );
					}
				}
			}
			ivd.setTaxonomies( taxonomies );


			//   Protein pages are using SearchTaxonomySearcher.getInstance().getTaxonomies( search );
			//   	which is per search
				
			
			// remove all proteins that are in the excluded taxonomy

			if ( ! excludeTaxonomy.isEmpty() ) {

				Collection<MergedSearchProtein> proteinsWithTaxonomyRemoved = new ArrayList<>( proteins.size() );

				for ( MergedSearchProtein item : proteins ) {

					boolean excludeForAllSearches = true;

					for ( SearchDTO searchDTO : item.getSearchs() ) {

						//  Get all taxonomy ids for protein sequence id and search id

						Set<Integer> taxonomyIds = 
								TaxonomyIdsForProtSeqIdSearchIdSearcher.getInstance().getTaxonomyIdsSingleSearch( item.getProteinSequenceObject(), searchDTO.getId() );

						for ( Integer taxonomyId : taxonomyIds ) {

							if( ! excludeTaxonomy_Ids_Set_UserInput.contains( taxonomyId ) ) { 
								excludeForAllSearches = false;
								break;
							}
						}
						
						if ( ! excludeForAllSearches ) {
							
							break;
						}
					}
					
					if ( excludeForAllSearches ) {
					
						continue; //  EARLY CONTINUE - Drop "item" from output list
					}

					proteinsWithTaxonomyRemoved.add( item );  //  Not excluded so add to this list
				}

				proteins = proteinsWithTaxonomyRemoved;  // Copy to original list for further processing
			}

			//  Map of linkablePositions where the key is the protein id and the value is the collection of linkable positions
			Map<Integer, Collection<Integer>> proteinIdslinkablePositionsMap = new HashMap<Integer, Collection<Integer>>();
			

			List<LinkerDTO>  linkerList = LinkersForSearchIdsSearcher.getInstance().getLinkersForSearchIds( searchIdsListDedupedSorted );
			
			
			if ( linkerList == null || linkerList.isEmpty() ) {
				
				String errorMsgSearchIdList = null;
				
				for ( Integer searchId : searchIdsListDedupedSorted ) {
					
					if ( errorMsgSearchIdList == null ) {
						
						errorMsgSearchIdList = searchId.toString();
					} else {
						
						errorMsgSearchIdList += "," + searchId.toString();
					}
				}
				String msg = "No linkers found for Search Ids: " + errorMsgSearchIdList;
				log.error( msg );
//				throw new Exception(msg);
			}
			
			
			Set<String> linkerAbbrSet = new HashSet<>();
			
			for ( LinkerDTO linker : linkerList ) {
				
				String linkerAbbr = linker.getAbbr();
				
				linkerAbbrSet.add( linkerAbbr );
			}
						
			
			// add locations of all linkablePositions in the found proteins

			for( MergedSearchProtein mp : proteins ) {

				String proteinSequence = mp.getProteinSequenceObject().getSequence();

				Collection<Integer> linkablePositionsForProtein = GetLinkablePositionsForLinkers.getLinkablePositionsForProteinSequenceAndLinkerAbbrSet( proteinSequence, linkerAbbrSet );
				
				proteinIdslinkablePositionsMap.put( mp.getProteinSequenceObject().getProteinSequenceId(), linkablePositionsForProtein );
			}
			ivd.setLinkablePositions( proteinIdslinkablePositionsMap ); 

			// guild maps of protein lengths and protein names for the found proteins
			Map<Integer, String> proteinNames = new HashMap<Integer, String>();

			for( MergedSearchProtein mp : proteins ) {
				proteinNames.put( mp.getProteinSequenceObject().getProteinSequenceId(), mp.getName() );
			}

			//   Create a list of protein sequence ids ordered by protein name, protein sequence id
			
			List<ProteinSequenceIdProteinName> proteinSequenceIdProteinNameList = new ArrayList<>( proteinNames.size() );
			for ( Map.Entry<Integer, String> proteinNamesEntry : proteinNames.entrySet() ) {
				ProteinSequenceIdProteinName proteinSequenceIdProteinName = new ProteinSequenceIdProteinName();
				proteinSequenceIdProteinName.proteinName = proteinNamesEntry.getValue();
				proteinSequenceIdProteinName.proteinSequenceId = proteinNamesEntry.getKey();
				proteinSequenceIdProteinNameList.add( proteinSequenceIdProteinName );
			}

			Collections.sort( proteinSequenceIdProteinNameList, new  Comparator<ProteinSequenceIdProteinName>() {
		        public int compare(ProteinSequenceIdProteinName o1, ProteinSequenceIdProteinName o2) {
		        	if ( o1.proteinName.equals( o2.proteinName ) ) {
		        		return o1.proteinSequenceId - o2.proteinSequenceId;
		        	}
		        	return o1.proteinName.compareTo( o2.proteinName );
		        }
			} );
			
			//  Output list of protein sequence ids ordered by protein name, protein sequence id 
			List<Integer> proteinSequenceIdsSortedOnProteinNameList = new ArrayList<>( proteinSequenceIdProteinNameList.size() );
			for ( ProteinSequenceIdProteinName proteinSequenceIdProteinName : proteinSequenceIdProteinNameList ) {
				proteinSequenceIdsSortedOnProteinNameList.add( proteinSequenceIdProteinName.proteinSequenceId );
			}
			
			ivd.setProteinNames( proteinNames );
			ivd.setProteins( proteinSequenceIdsSortedOnProteinNameList );

			ivd.setCutoffs( cutoffValuesRootLevel );

			ivd.setExcludeTaxonomy( excludeTaxonomy );
			ivd.setExcludeType( excludeType );
			ivd.setSearches( searches );
			ivd.setFilterNonUniquePeptides( filterNonUniquePeptides );
			ivd.setFilterOnlyOnePSM( filterOnlyOnePSM );
			ivd.setFilterOnlyOnePeptide( filterOnlyOnePeptide );

			return ivd;

		} catch ( WebApplicationException e ) {
			throw e;
		
		} catch ( ProxlWebappDataException e ) {

			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT )
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
	
	private static class ProteinSequenceIdProteinName {
		
		private Integer proteinSequenceId;
		private String proteinName;
	}
	
}
