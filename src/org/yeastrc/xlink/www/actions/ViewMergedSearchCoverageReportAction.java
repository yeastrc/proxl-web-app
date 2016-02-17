package org.yeastrc.xlink.www.actions;


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
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;

















import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.NRProteinDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinDimer;
import org.yeastrc.xlink.www.objects.SearchProteinDimerWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinLooplink;
import org.yeastrc.xlink.www.objects.SearchProteinLooplinkWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinked;
import org.yeastrc.xlink.www.objects.SearchProteinUnlinkedWrapper;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinCrosslinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinDimerSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinLooplinkSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinUnlinkedSearcher;
import org.yeastrc.xlink.www.searcher.SearchTaxonomySearcher;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.Struts_Config_Parameter_Values_Constants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.cutoff_processing_web.GetCutoffPageDisplayRoot;
import org.yeastrc.xlink.www.cutoff_processing_web.GetDefaultPsmPeptideCutoffs;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.ProteinCoverageData;
import org.yeastrc.xlink.www.searcher.ProteinCoverageSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.AnyPDBFilesForProjectId;
import org.yeastrc.xlink.www.web_utils.GetPageHeaderData;
import org.yeastrc.xlink.www.web_utils.GetSearchDetailsData;
import org.yeastrc.xlink.www.web_utils.URLEncodeDecodeAURL;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;





public class ViewMergedSearchCoverageReportAction extends Action {
	
	private static final Logger log = Logger.getLogger(ViewMergedSearchCoverageReportAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {

		//  Detect which Struts action mapping was called by examining the value of the "parameter" attribute
		//     accessed by calling mapping.getParameter()

		String strutsActionMappingParameter = mapping.getParameter();


		
		try {
			

			// our form
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm)actionForm;
			request.setAttribute( "mergedSearchViewCrosslinkProteinForm", form );

			request.setAttribute( "strutsActionForm", form );

			request.setAttribute( "queryString",  request.getQueryString() );
			request.setAttribute( "mergedQueryString", request.getQueryString() );
			
			if ( Struts_Config_Parameter_Values_Constants.STRUTS__PARAMETER__MERGED_PROTEIN_COVERAGE_PAGE.equals( strutsActionMappingParameter ) ) {

				request.setAttribute( "mergedPage", true );
			}

			// Get the session first.  
//			HttpSession session = request.getSession();
			
			

			int[] searchIds = form.getSearchIds();
			
			
			if ( searchIds.length == 0 ) {
				
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}

			
			//   Get the project id for this search
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			
			for ( int searchId : searchIds ) {

				searchIdsCollection.add( searchId );
			}
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsCollection );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				
				String msg = "No project ids for search ids: ";
				for ( int searchId : searchIds ) {

					msg += searchId + ", ";
				}
				
				log.error( msg );

				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			
			if ( projectIdsFromSearchIds.size() > 1 ) {
				
				//  Invalid request, searches across projects

				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			

			int projectId = projectIdsFromSearchIds.get( 0 );
			
			request.setAttribute( "projectId", projectId ); 
			

			

			///////////////////////
			
			
			
			

			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );

			if ( accessAndSetupWebSessionResult.isNoSession() ) {

				//  No User session 

				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			
			//  Test access to the project id
			
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();

			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {

				//  No Access Allowed for this project id

				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			


			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );


			///    Done Processing Auth Check and Auth Level

			
			//////////////////////////////


			//  Jackson JSON Mapper object for JSON deserialization and serialization
			
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object



			

			
			request.setAttribute( "searchIds", searchIds );
			

			


			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			
			Map<Integer, SearchDTO> searchesMapOnId = new HashMap<>();


			for( int searchId : form.getSearchIds() ) {

				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {
					
					String msg = "Percolator search id '" + searchId + "' not found in the database. User taken to home page.";
					
					log.warn( msg );
					
					//  Search not found, the data on the page they are requesting does not exist.
					//  The data on the user's previous page no longer reflects what is in the database.
					//  Take the user to the home page
					
					return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
				}
				
				searches.add( search );
				
				searchesMapOnId.put( searchId, search );
			}

			
			



			//  Populate request objects for Standard Header Display
			
			GetPageHeaderData.getInstance().getPageHeaderDataWithProjectId( projectId, request );

			

			
			boolean showStructureLink = true;
			
			if ( authAccessLevel.isAssistantProjectOwnerAllowed()
					|| authAccessLevel.isAssistantProjectOwnerIfProjectNotLockedAllowed() ) {
				
				
			} else {
				
				//  Public access user:
				
				showStructureLink = AnyPDBFilesForProjectId.getInstance().anyPDBFilesForProjectId( projectId );
			}
			
			request.setAttribute( WebConstants.REQUEST_SHOW_STRUCTURE_LINK, showStructureLink );
			



			// sort our searches by ID
			Collections.sort( searches, new Comparator<SearchDTO>() {
				public int compare( SearchDTO r1, SearchDTO r2 ) {
					return r1.getId() - r2.getId();
				}
			});
			


			// Set values for general page functionality
			request.setAttribute( "queryString", request.getQueryString() );
			request.setAttribute( "searches", searches );
			
			
			GetSearchDetailsData.getInstance().getSearchDetailsData( searches, request );
	
			
			

			
			//   Get Query JSON from the form and if not empty, deserialize it
			

			String queryJSONFromForm = form.getQueryJSON();
			
			ProteinQueryJSONRoot proteinQueryJSONRoot = null;
			
			if ( StringUtils.isNotEmpty( queryJSONFromForm ) ) {

				try {
					proteinQueryJSONRoot = jacksonJSON_Mapper.readValue( queryJSONFromForm, ProteinQueryJSONRoot.class );
					
				} catch ( JsonParseException e ) {
					
					String msg = "Failed to parse 'queryJSONFromForm', JsonParseException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw e;
				
				} catch ( JsonMappingException e ) {
					
					String msg = "Failed to parse 'queryJSONFromForm', JsonMappingException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw e;
					
				} catch ( IOException e ) {
					
					String msg = "Failed to parse 'queryJSONFromForm', IOException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw e;
				}
				
			} else {
				
				
				//  Query JSON in the form is empty so create an empty object that will be populated.
				
				
				proteinQueryJSONRoot = new ProteinQueryJSONRoot();

				//  TODO  only do this if not generic
				
				CutoffValuesRootLevel cutoffValuesRootLevel =
						GetDefaultPsmPeptideCutoffs.getInstance()
						.getDefaultPsmPeptideCutoffs( searchIdsCollection );
				
				proteinQueryJSONRoot.setCutoffs( cutoffValuesRootLevel );
				
				
			}



			/////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////
			
			////////   Generic Param processing
			
			


//			CutoffPageDisplayRoot cutoffPageDisplayRoot =
			
			GetCutoffPageDisplayRoot.getInstance().getCutoffPageDisplayRoot( searchIdsCollection, request );


			////////////
			
			//  Copy Exclude Taxonomy and Exclude Protein Sets for lookup

			Set<Integer> excludeTaxonomy_Ids_Set_UserInput = new HashSet<>();


			Set<Integer> excludeProtein_Ids_Set_UserInput = new HashSet<>();
			

			if ( proteinQueryJSONRoot.getExcludeTaxonomy() != null ) {

				for ( Integer taxonomyId : proteinQueryJSONRoot.getExcludeTaxonomy() ) {
				
					excludeTaxonomy_Ids_Set_UserInput.add( taxonomyId );
				}
			}

			if ( proteinQueryJSONRoot.getExcludeProtein() != null ) {

				for ( Integer proteinId : proteinQueryJSONRoot.getExcludeProtein() ) {

					excludeProtein_Ids_Set_UserInput.add( proteinId );
				}
			}


			CutoffValuesRootLevel cutoffValuesRootLevel = proteinQueryJSONRoot.getCutoffs();

			Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
					Z_CutoffValuesObjectsToOtherObjectsFactory.createSearcherCutoffValuesRootLevel( searchIdsCollection, cutoffValuesRootLevel );
			
			
			SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel = cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();
			


			ProteinCoverageSearcher pcs = new ProteinCoverageSearcher();

			pcs.setExcludedProteinIds( proteinQueryJSONRoot.getExcludeProtein() );
			pcs.setExcludedTaxonomyIds( proteinQueryJSONRoot.getExcludeTaxonomy() );
			
			pcs.setFilterNonUniquePeptides( proteinQueryJSONRoot.isFilterNonUniquePeptides() );
			pcs.setFilterOnlyOnePSM( proteinQueryJSONRoot.isFilterOnlyOnePSM() );
			pcs.setFilterOnlyOnePeptide( proteinQueryJSONRoot.isFilterOnlyOnePeptide() );
			
			pcs.setSearcherCutoffValuesRootLevel( searcherCutoffValuesRootLevel );

			pcs.setSearches( searches );

			List<ProteinCoverageData> pcd = pcs.getProteinCoverageData();
			request.setAttribute( "proteinCoverageData", pcd );




			//  TODO  Build list of proteins for the protein Exclusion list
			
			{

				// all possible proteins across all searches (for "Exclude Protein" list on web page)

				Map<Integer,Set<Integer>> allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds = new HashMap<>();


				for ( SearchDTO search : searches ) {

					Integer searchId = search.getId();
					
					SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = 
							searcherCutoffValuesRootLevel.getPerSearchCutoffs( searchId );
					
					{
						List<SearchProteinCrosslinkWrapper> wrappedCrosslinks = 
								SearchProteinCrosslinkSearcher.getInstance().searchOnSearchIdandCutoffs( search, searcherCutoffValuesSearchLevel );


						for ( SearchProteinCrosslinkWrapper wrappedCrosslink : wrappedCrosslinks ) {

							SearchProteinCrosslink crosslink = wrappedCrosslink.getSearchProteinCrosslink();

							SearchProtein searchProtein_1 = crosslink.getProtein1();
							SearchProtein searchProtein_2 = crosslink.getProtein2();
							
							Integer searchProtein_id_1 = searchProtein_1.getNrProtein().getNrseqId();
							Integer searchProtein_id_2 = searchProtein_2.getNrProtein().getNrseqId();
												
							{
								Set<Integer> searchIdsForProtein_1 =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_1 );

								if ( searchIdsForProtein_1 == null ) {

									searchIdsForProtein_1 = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_1, searchIdsForProtein_1 );
								}
								
								searchIdsForProtein_1.add( search.getId() );
							}
							
							
							{
								Set<Integer> searchIdsForProtein_2 =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_2 );

								if ( searchIdsForProtein_2 == null ) {

									searchIdsForProtein_2 = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_2, searchIdsForProtein_2 );
								}
								
								searchIdsForProtein_2.add( search.getId() );
							}
							
						}
					}

					{
						List<SearchProteinLooplinkWrapper> wrappedLooplinkLinks = 
								SearchProteinLooplinkSearcher.getInstance()
								.searchOnSearchIdandCutoffs( search, searcherCutoffValuesSearchLevel );

						for ( SearchProteinLooplinkWrapper wrappedLooplink : wrappedLooplinkLinks ) {

							SearchProteinLooplink looplink = wrappedLooplink.getSearchProteinLooplink();
							
							SearchProtein searchProtein = looplink.getProtein();
							Integer searchProtein_id = searchProtein.getNrProtein().getNrseqId();

							{
								Set<Integer> searchIdsForProtein =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id );

								if ( searchIdsForProtein == null ) {

									searchIdsForProtein = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id, searchIdsForProtein );
								}
								
								searchIdsForProtein.add( search.getId() );
							}
							
						}
					}
					
					{
						List<SearchProteinDimerWrapper> wrappedDimerLinks = 
								SearchProteinDimerSearcher.getInstance()
								.searchOnSearchIdandCutoffs( search, searcherCutoffValuesSearchLevel );

						for ( SearchProteinDimerWrapper wrappedDimer : wrappedDimerLinks ) {

							SearchProteinDimer dimer = wrappedDimer.getSearchProteinDimer();

							SearchProtein searchProtein_1 = dimer.getProtein1();
							SearchProtein searchProtein_2 = dimer.getProtein2();
							
							Integer searchProtein_id_1 = searchProtein_1.getNrProtein().getNrseqId();
							Integer searchProtein_id_2 = searchProtein_2.getNrProtein().getNrseqId();
												
							{
								Set<Integer> searchIdsForProtein_1 =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_1 );

								if ( searchIdsForProtein_1 == null ) {

									searchIdsForProtein_1 = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_1, searchIdsForProtein_1 );
								}
								
								searchIdsForProtein_1.add( search.getId() );
							}
							
							
							{
								Set<Integer> searchIdsForProtein_2 =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id_2 );

								if ( searchIdsForProtein_2 == null ) {

									searchIdsForProtein_2 = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id_2, searchIdsForProtein_2 );
								}
								
								searchIdsForProtein_2.add( search.getId() );
							}
							
						}
					}

					{
						List<SearchProteinUnlinkedWrapper> wrappedUnlinkedLinks = 
								SearchProteinUnlinkedSearcher.getInstance()
								.searchOnSearchIdandCutoffs( search, searcherCutoffValuesSearchLevel );

						for ( SearchProteinUnlinkedWrapper wrappedUnlinked : wrappedUnlinkedLinks ) {

							SearchProteinUnlinked unlinked = wrappedUnlinked.getSearchProteinUnlinked();
							
							SearchProtein searchProtein = unlinked.getProtein();
							Integer searchProtein_id = searchProtein.getNrProtein().getNrseqId();

							{
								Set<Integer> searchIdsForProtein =
										allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.get( searchProtein_id );

								if ( searchIdsForProtein == null ) {

									searchIdsForProtein = new HashSet<>();
									allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.put( searchProtein_id, searchIdsForProtein );
								}
								
								searchIdsForProtein.add( search.getId() );
							}
							
						}
					}
					
				}

				
				List<MergedSearchProtein> allProteinsUnfilteredList = new ArrayList<>( allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.size() );

				for ( Map.Entry<Integer, Set<Integer>> entry : allProteinsExcludeProteinSelectOnWebPageKeyProteinIdValueSearchIds.entrySet() ) {

					Integer proteinId = entry.getKey();
					Set<Integer> searchIdsForProtein = entry.getValue();
					
					List<SearchDTO> searchesForProtein = new ArrayList<>( searchIdsForProtein.size() );
					
					for ( Integer searchIdForProtein : searchIdsForProtein ) {
						
						SearchDTO searchForProtein = searchesMapOnId.get( searchIdForProtein );
						
						if ( searchForProtein == null ) {
							
							String msg = "Processing searchIdsForProtein, no search found in searchesMapOnId for searchIdForProtein : " + searchIdForProtein;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
						}
						
						searchesForProtein.add(searchForProtein);
					}
				
					NRProteinDTO nrProteinDTO = new NRProteinDTO();
					nrProteinDTO.setNrseqId( proteinId );

					MergedSearchProtein mergedSearchProtein = new MergedSearchProtein( searchesForProtein, nrProteinDTO );
					
					int mergedSearchProteinTaxonomyId = mergedSearchProtein.getNrProtein().getTaxonomyId(); 

					if ( excludeTaxonomy_Ids_Set_UserInput.contains( mergedSearchProteinTaxonomyId ) ) {
						
						//////////  Taxonomy Id in list of excluded taxonomy ids so drop the record
						
						continue;  //   EARLY Continue
					}
					
					allProteinsUnfilteredList.add( mergedSearchProtein );
				}
			
				
				Collections.sort( allProteinsUnfilteredList, new SortMergedSearchProtein() );

				request.setAttribute( "proteins", allProteinsUnfilteredList );

			}

//				
//				
//				
//				Collection<MergedSearchProtein> proteins = new ArrayList<>();
//				
//				
//
//				Set<Integer> proteinIds = new HashSet<>();
//				
//				{
//
//					List<MergedSearchProteinCrosslink> crosslinksProteins = MergedSearchProteinCrosslinkSearcher.getInstance().search( searches, searcherCutoffValuesRootLevel );
//
//					List<MergedSearchProteinLooplink> looplinksProteins = MergedSearchProteinLooplinkSearcher.getInstance().search( searches, searcherCutoffValuesRootLevel );
//
//					for( MergedSearchProteinCrosslink item : crosslinksProteins ) {
//
//						proteinIds.add( item.getProtein1().getNrProtein().getNrseqId() );
//
//						proteinIds.add( item.getProtein2().getNrProtein().getNrseqId() );
//					}
//
//					for( MergedSearchProteinLooplink item : looplinksProteins ) {
//
//						proteinIds.add( item.getProtein().getNrProtein().getNrseqId() );
//					}
//
//					for ( SearchDTO search : searches ) {
//
//						{
//							List<SearchProteinDimerWrapper> wrappedDimerLinks = 
//									SearchProteinDimerSearcher.getInstance()
//									.searchOnSearchIdandCutoffs( search, searcherCutoffValuesRootLevel.getPerSearchCutoffs( search.getId() ) );
//
//							for ( SearchProteinDimerWrapper wrappedDimer : wrappedDimerLinks ) {
//
//								SearchProteinDimer dimer = wrappedDimer.getSearchProteinDimer();
//
//								proteinIds.add( dimer.getProtein1().getNrProtein().getNrseqId() );
//
//								proteinIds.add( dimer.getProtein2().getNrProtein().getNrseqId() );
//							}
//						}
//
//						{
//							List<SearchProteinUnlinkedWrapper> wrappedUnlinkedLinks = 
//									SearchProteinUnlinkedSearcher.getInstance()
//									.searchOnSearchIdandCutoffs( search, searcherCutoffValuesRootLevel.getPerSearchCutoffs( search.getId() ) );
//
//							for ( SearchProteinUnlinkedWrapper wrappedUnlinked : wrappedUnlinkedLinks ) {
//
//								SearchProteinUnlinked unlinked = wrappedUnlinked.getSearchProteinUnlinked();
//
//								proteinIds.add( unlinked.getProtein().getNrProtein().getNrseqId() );
//							}
//						}
//						
//					}
//
//					for ( int proteinId : proteinIds ) {
//
//						proteins.add( new MergedSearchProtein( searches, NRProteinDAO.getInstance().getNrProtein( proteinId ) ) );
//					}
//				}
//				
//				List<MergedSearchProtein> sortedProteins = new ArrayList<>();
//				sortedProteins.addAll( proteins );
//				Collections.sort( sortedProteins, new SortMergedSearchProtein() );
//
//
//				request.setAttribute( "proteins", sortedProteins );
//				
//			}
//			



//			// Code for handling which proteins and species to show for exclusion filters
			
//			Collection<Integer> types = new HashSet<Integer>();
//			types.add( XLinkUtils.TYPE_CROSSLINK );
//			types.add( XLinkUtils.TYPE_LOOPLINK );
//			types.add( XLinkUtils.TYPE_DIMER );
//			types.add( XLinkUtils.TYPE_MONOLINK );
//			types.add( XLinkUtils.TYPE_UNLINKED );
//
//			Collection<MergedSearchProtein> prProteins = MergedSearchProteinSearcher.getInstance().getProteinsWithLinkType(searches, types, psmQValueCutoff, peptideQValueCutoff);
//			Collection<MergedSearchProtein> prProteins2 = new HashSet<MergedSearchProtein>();
//			prProteins2.addAll( prProteins );
//
//			// build a collection of protein IDs to include
//			for( MergedSearchProtein prp : prProteins2 ) {
//
//				// did they request removal of certain taxonomy IDs?
//				if( form.getExcludeTaxonomy() != null && form.getExcludeTaxonomy().length > 0 ) {
//					for( int tid : form.getExcludeTaxonomy() ) {
//						if( tid == prp.getNrProtein().getTaxonomyId() ) {
//							prProteins.remove( prp );
//							break;
//						}
//					}
//				}
//			}
//
//			List<MergedSearchProtein> sortedProteins = new ArrayList<MergedSearchProtein>();
//			sortedProteins.addAll( prProteins );
//			Collections.sort( sortedProteins, new SortMergedSearchProtein() );
//
//			request.setAttribute( "proteins", sortedProteins );

			

			// build list of taxonomies to show in exclusion list
			
			request.setAttribute("taxonomies", SearchTaxonomySearcher.getInstance().getTaxonomies( searches ) );

			

			//////////////////////////////////////////////
			

			/////////////////////
			
			//  clear out form so value doesn't go back on the page in the form

			form.setQueryJSON( "" );

			/////////////////////
			
			////  Put Updated queryJSON on the page
			
			{
			
				try {

					String queryJSONToForm = jacksonJSON_Mapper.writeValueAsString( proteinQueryJSONRoot );
					
					//  Set queryJSON in request attribute to put on page outside of form
					
					request.setAttribute( "queryJSONToForm", queryJSONToForm );

					//  Create URI Encoded JSON for passing to Image and Structure pages in hash 
					
					String queryJSONToFormURIEncoded = URLEncodeDecodeAURL.urlEncodeAURL( queryJSONToForm );

					request.setAttribute( "queryJSONToFormURIEncoded", queryJSONToFormURIEncoded );
					
				
				} catch ( JsonProcessingException e ) {
					
					String msg = "Failed to write as JSON 'queryJSONToForm', JsonProcessingException.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw e;
				
				} catch ( Exception e ) {
					
					String msg = "Failed to write as JSON 'queryJSONToForm', Exception.  queryJSONFromForm: " + queryJSONFromForm;
					log.error( msg, e );
					throw e;
				}
			
			}
			
			//////////////////////////////////////
			

			return mapping.findForward( "Success" );
			

			
		} catch ( ProxlWebappDataException e ) {

			String msg = "Exception processing request data";
			
			log.error( msg, e );

			return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			
		} catch ( Exception e ) {
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}


    public class SortMergedSearchProtein implements Comparator<MergedSearchProtein> {
        public int compare(MergedSearchProtein o1, MergedSearchProtein o2) {
            try { return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()); }
            catch( Exception e ) { return 0; }
        }
    }
}
