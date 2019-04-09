package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.linker_data_processing_base.ILinker_Main;
import org.yeastrc.xlink.linker_data_processing_base.ILinkers_Main_ForSingleSearch;
import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.Get_BuiltIn_Linker_From_Abbreviation_Factory;
import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin.ILinker_Builtin_Linker;
import org.yeastrc.xlink.www.actions.ProteinsMergedCommonPageDownload.ProteinsMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dao.SrchRepPeptPeptideDAO;
import org.yeastrc.xlink.www.download_data_utils.FilterProteinsOnSelectedLinks;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.forms.DownloadMergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.linkable_positions.ILinker_Main_Objects_ForSearchId_Cached;
import org.yeastrc.xlink.www.linkable_positions.ILinker_Main_Objects_ForSearchId_Cached.ILinker_Main_Objects_ForSearchId_Cached_Response;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util;
import org.yeastrc.xlink.www.web_utils.SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util.SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_Response;

/**
 * 
 *
 */
public class DownloadMergedProteinsPeptidesSkylineShulmanAction extends Action {

	private static final Logger log = LoggerFactory.getLogger( DownloadMergedSearchProteinsAction.class);

	@Override
	public ActionForward execute( ActionMapping mapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response )
					throws Exception {
		
		try {
			// our form
			DownloadMergedSearchViewProteinsForm form = (DownloadMergedSearchViewProteinsForm)actionForm;
			// Get the session first.  
			//			HttpSession session = request.getSession();
			//   Get the project id for these searches
			int[] projectSearchIds = form.getProjectSearchId();
			if ( projectSearchIds.length == 0 ) {
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int projectSearchId : projectSearchIds ) {
				projectSearchIdsSet.add( projectSearchId );
			}
			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdsListDeduppedSorted );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int projectSearchId : projectSearchIds ) {
					msg += projectSearchId + ", ";
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

			List<SearchDTO> searches = new ArrayList<SearchDTO>( projectSearchIdsListDeduppedSorted.size() );
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();
			int[] searchIdsArray = new int[ projectSearchIdsListDeduppedSorted.size() ];
			int searchIdsArrayIndex = 0;
			List<Integer> searchIds = new ArrayList<>( projectSearchIdsListDeduppedSorted.size() );

			for( int projectSearchId : projectSearchIdsListDeduppedSorted ) {
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = "search id '" + projectSearchId + "' not found in the database. User taken to home page.";
					log.warn( msg );
					//  Search not found, the data on the page they are requesting does not exist.
					//  The data on the user's previous page no longer reflects what is in the database.
					//  Take the user to the home page
					return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
				}
				searches.add( search );
				searchesMapOnSearchId.put( search.getSearchId(), search );
				searchIdsArray[ searchIdsArrayIndex ] = search.getSearchId();
				searchIdsArrayIndex++;
				searchIds.add( search.getSearchId() );
			}
			// Sort searches list
			Collections.sort( searches, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getSearchId() - o2.getSearchId();
				}
			});
			Collections.sort( searchIds );
			

			//  If change to open in new tab, 
			//  move following to just before write out main data and show error page where write error msg to response.
			
			//  Set Response Parameters
			try {

				// generate file name
				String filename = "proxl-peptides-skyline-shulman-";
				filename += StringUtils.join( searchIdsArray, '-' );
				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");
				filename += "-" + fmt.print( dt );
				filename += ".txt";
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);

			} catch ( Exception e ) {
				String msg = "Failed to create response parameters or set response parameters";
				log.error( msg );
				throw e;
			}
			

			//  Crosslinker Chemical Formula - Only a Single value across all searches all linkers if this is set to a string.
			String onlyOneCrosslinkerChemicalFormula_AllSearches_String = null;

			//  Get and Cache ILinkers_Main_ForSingleSearch per search id
			Map<Integer, ILinkers_Main_ForSingleSearch> iLinkers_Main_ForSingleSearch_KeySearchId = new HashMap<>();
			
			//  Get/Compute and Cache Crosslinker Chemical Formula for search ids where only one crosslinker formula for that search id
			Map<Integer, String> onlyOneCrosslinkerChemicalFormulaString_ForSingleSearch_KeySearchId = new HashMap<>();
			
			Map<Integer, SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util> searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_KeySearchId = new HashMap<>();

			{
				boolean foundCrosslinkerChemicalFormulasForAllSearchesAllLinkers = true;
	
				//  Get Linker Data, including CrosslinkerFormula
				try {
					boolean singleCrosslinkerFormulaAcrossAllSearches = true;
					String firstCrosslinkerFormula_AllSearches_String = null;
					{
						ILinker_Main_Objects_ForSearchId_Cached iLinker_Main_Objects_ForSearchId_Cached = ILinker_Main_Objects_ForSearchId_Cached.getInstance(); 
						for( Integer searchId : searchIds ) {
	
							{
								SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util = 
										SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util.getInstanceForSearchId( searchId );
								searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_KeySearchId.put( searchId, searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util );
							}
							
							ILinker_Main_Objects_ForSearchId_Cached_Response iLinker_Main_Objects_ForSearchId_Cached_Response =
									iLinker_Main_Objects_ForSearchId_Cached.getSearchLinkers_ForSearchId_Response( searchId );
							ILinkers_Main_ForSingleSearch iLinkers_Main_ForSingleSearch = iLinker_Main_Objects_ForSearchId_Cached_Response.getiLinkers_Main_ForSingleSearch();
							iLinkers_Main_ForSingleSearch_KeySearchId.put( searchId, iLinkers_Main_ForSingleSearch );
							
							{
								boolean singleCrosslinkerFormulaAcrossSearch = true;
								String firstCrosslinkerFormulaString_ForSingleSearch = null;
								List<ILinker_Main> iLinkers_Main_List = iLinkers_Main_ForSingleSearch.getLinker_MainList();
								if ( iLinkers_Main_List == null || iLinkers_Main_List.isEmpty() ) {
									String msg = "iLinkers_Main_List == null || iLinkers_Main_List.isEmpty(), searchId: " + searchId ;
									log.error( msg );
									throw new ProxlWebappInternalErrorException(msg);
								}
								for ( ILinker_Main iLinker_Main : iLinkers_Main_List ) {
									Set<String> crosslinkFormulasSet = iLinker_Main.getCrosslinkFormulas();
									
									if ( crosslinkFormulasSet == null || crosslinkFormulasSet.isEmpty() ) {
										//  No Crosslinker Chemical Formulas for this linker
										//     Set Flag and exit since cannot create this download
										foundCrosslinkerChemicalFormulasForAllSearchesAllLinkers = false;
										break;  //  EARLY LOOP EXIT
									}
									
									if ( crosslinkFormulasSet.size() == 1 ) {
										// Have only 1 chemical formula so process it for at search and across search tracking
										
										String singleCrosslinkerChemicalFormulaForLinker = crosslinkFormulasSet.iterator().next();
										
										if ( firstCrosslinkerFormulaString_ForSingleSearch == null ) {
											//  Not saved for search yet so save for search
											firstCrosslinkerFormulaString_ForSingleSearch = singleCrosslinkerChemicalFormulaForLinker;
										} else if ( ! firstCrosslinkerFormulaString_ForSingleSearch.equals( singleCrosslinkerChemicalFormulaForLinker ) ) {
											// Have more than 1 crosslinker formula for search
											singleCrosslinkerFormulaAcrossSearch = false;
										}
									} else {
										// Have > 1 chemical formula
										singleCrosslinkerFormulaAcrossSearch = false;
									}
								}
								if ( singleCrosslinkerFormulaAcrossSearch ) {
									// Have a single crosslinker formula for the search so save it
									onlyOneCrosslinkerChemicalFormulaString_ForSingleSearch_KeySearchId.put( searchId, firstCrosslinkerFormulaString_ForSingleSearch );

									if ( firstCrosslinkerFormula_AllSearches_String == null ) {
										//  crosslinker formula all searches not set yet
										firstCrosslinkerFormula_AllSearches_String = firstCrosslinkerFormulaString_ForSingleSearch;
									} else if ( ! firstCrosslinkerFormula_AllSearches_String.equals( firstCrosslinkerFormulaString_ForSingleSearch)  ) {
										// first crosslinker formula all searches not match current crosslinker formula 
										singleCrosslinkerFormulaAcrossAllSearches = false;
									}
								} else {
									//  Since more than 1 crosslinker formula for this search, there is > 1 across all searches
									singleCrosslinkerFormulaAcrossAllSearches = false;
								}
							}
							
							if ( ! foundCrosslinkerChemicalFormulasForAllSearchesAllLinkers ) {
								//     Exit since cannot create this download.  Check for flag is false next and exit
								break;  //  EARLY LOOP EXIT
							}
						}
					}
					
					if ( singleCrosslinkerFormulaAcrossAllSearches ) {
						//  Have Single Crosslinker Formula across all searches so copy to top level shared variable
						onlyOneCrosslinkerChemicalFormula_AllSearches_String = firstCrosslinkerFormula_AllSearches_String;
					}
				} catch ( Exception e ) {
					String msg = "Failed to get Linker data";
					log.error( msg );
					throw e;
				}
			
				if ( ! foundCrosslinkerChemicalFormulasForAllSearchesAllLinkers ) {
					
					// Found at least one linker with no crosslinker Chemical formula.  Should not have shown this download link.
					
					String msg = "Found at least one linker with no crosslinker chemical formula.  Should not have shown this download link.  search ids: " + searchIds;
					log.error( msg );
					
					OutputStreamWriter writer = null;
					try {
						ServletOutputStream out = response.getOutputStream();
						BufferedOutputStream bos = new BufferedOutputStream(out);
						writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );
						
						writer.write( "Unable to create download.  " );
						writer.write( "\n" );
						writer.write( "Found at least one linker with no crosslinker chemical formula.  " );
						writer.write( "\n" );
						
					} finally {
						try {
							if ( writer != null ) {
								writer.close();
							}
						} catch ( Exception ex ) {
							log.error( "writer.close():Exception " + ex.toString(), ex );
						}
						try {
							response.flushBuffer();
						} catch ( Exception ex ) {
							log.error( "response.flushBuffer():Exception " + ex.toString(), ex );
						}
					}
					
					///////  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					
					//    EXIT Download Struts Action
					
					return null;  //  EARLY EXIT
				}
			}

			Collection<String> downloadOutputLines = new HashSet<>();

			try {
				ProteinsMergedCommonPageDownloadResult proteinsMergedCommonPageDownloadResult =
						ProteinsMergedCommonPageDownload.getInstance()
						.getCrosslinksAndLooplinkWrapped(
								form,
								ProteinsMergedCommonPageDownload.ForCrosslinksOrLooplinkOrBoth.BOTH_CROSSLINKS_AND_LOOPLINKS,
								projectSearchIdsListDeduppedSorted,
								searches,
								searchesMapOnSearchId  );

				if ( StringUtils.isNoneEmpty( form.getSelectedCrosslinksLooplinksMonolinksJSON() ) ) {
					FilterProteinsOnSelectedLinks.getInstance()
					.filterProteinsOnSelectedLinks( 
							proteinsMergedCommonPageDownloadResult, form.getSelectedCrosslinksLooplinksMonolinksJSON() );
				}
				
				ProteinQueryJSONRoot proteinQueryJSONRoot = proteinsMergedCommonPageDownloadResult.getProteinQueryJSONRoot();

				//  Process Main Reported Peptides for Merged:
				
				List<MergedSearchProteinCrosslink> crosslinks = proteinsMergedCommonPageDownloadResult.getCrosslinks();


				// iterate over the crosslinks
				for( MergedSearchProteinCrosslink link : crosslinks ) {

					Map<SearchDTO, SearchProteinCrosslink> searchProteinCrosslinks = link.getSearchProteinCrosslinks();					
					
					// iterate over searches that contain this crosslink
					for( SearchDTO search : searchProteinCrosslinks.keySet() ) {
						
						int eachProjectSearchIdToProcess = search.getProjectSearchId();
						Integer eachSearchIdToProcess = search.getSearchId();

						SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinks.get( search );

						
						List<ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink> crosslinkReportedPeptidePeptides = searchProteinCrosslink.getReportedPeptide_SearchReportedPeptidepeptideId_CrosslinkList();

						for( ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink crosslinkReportedPeptidePeptide : crosslinkReportedPeptidePeptides ) {

							SrchRepPeptPeptideDTO searchReportedPeptidePeptide1 = SrchRepPeptPeptideDAO.getInstance().getForId( crosslinkReportedPeptidePeptide.getSearchReportedPeptidepeptideId_1() );
							SrchRepPeptPeptideDTO searchReportedPeptidePeptide2 = SrchRepPeptPeptideDAO.getInstance().getForId( crosslinkReportedPeptidePeptide.getSearchReportedPeptidepeptideId_2() );

							PeptideDTO peptide1 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( searchReportedPeptidePeptide1.getPeptideId() );
							PeptideDTO peptide2 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( searchReportedPeptidePeptide2.getPeptideId() );
							

							String line = peptide1.getSequence() + "\t"
									+ searchReportedPeptidePeptide1.getPeptidePosition_1() + "\t"
									+ peptide2.getSequence() + "\t"
									+ searchReportedPeptidePeptide2.getPeptidePosition_1() + "\t"

									+ link.getProtein1().getName() + "(" + link.getProtein1Position() + ")"
									+ "--"
									+ link.getProtein2().getName() + "(" + link.getProtein2Position() + ")"
		
									+ ":"
		
									+ peptide1.getSequence() + "--" + peptide2.getSequence() + "\t";

							
							// if there is only one possible formula, just use it no matter what
							
							if ( onlyOneCrosslinkerChemicalFormula_AllSearches_String != null ) {

								downloadOutputLines.add( line + onlyOneCrosslinkerChemicalFormula_AllSearches_String );
								
								// go to next reported peptide - ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink
								
								continue;  //  EARLY CONTINUE
							}

							{
								String formulaForSearch = onlyOneCrosslinkerChemicalFormulaString_ForSingleSearch_KeySearchId.get( eachSearchIdToProcess );
								if ( formulaForSearch != null ) {
									
									//  Have a single formula for search so use it

									downloadOutputLines.add( line + formulaForSearch );
								
									//  Go to reported peptide - ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink

									continue;  //  EARLY CONTINUE
								}
							}
							
							List<PsmWebDisplayWebServiceResult> PSMs = PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( search.getSearchId(), crosslinkReportedPeptidePeptide.getReportedPeptideId(), searchProteinCrosslinks.get( search ).getSearcherCutoffValuesSearchLevel() );

							PSMs = DownloadPSMs_Common.getInstance().filterPSMs( proteinQueryJSONRoot, search, PSMs );

							if ( PSMs.isEmpty() ) {
								//  No PSMs after filter so skip this reported peptide

								String msg = "Should not get no PSMs after filtering. Peptide Query is incorrect then. searchId: " 
										+ eachSearchIdToProcess
										+ ", reportedPeptideId: " + crosslinkReportedPeptidePeptide.getReportedPeptideId();
								log.warn( msg );
								
								continue;  //  EARLY CONINUE
							}

							//  Get Crosslinker Chemical Formula based on PSM Linker Mass and Search Id

							//  Only need to process a linker mass once
							Set<BigDecimal> processedLinkerMasses = new HashSet<>();
							
							
							for( PsmWebDisplayWebServiceResult psm : PSMs ) {

								PsmDTO psmDTO = psm.getPsmDTO();
								BigDecimal psmLinkerMass = psmDTO.getLinkerMass();
								
								if ( ! processedLinkerMasses.add( psmLinkerMass ) ) {
									//  Already processed this linker mass so continue to next PSM
									
									continue;  //  EARLY CONINUE
								}


								SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util = 
										searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_KeySearchId.get( eachSearchIdToProcess );
								if ( searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util == null ) {
									final String msg = "searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util == null: get from map. eachSearchIdToProcess: " + eachSearchIdToProcess;
									log.error( msg );
									throw new ProxlWebappInternalErrorException(msg);
								}

								//  Get Linker info for psmLinkerMass
								SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_Response searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_Response =
										searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util
										.get_SearchLinkerDTO_LinkerPerSearchCrosslinkMassDTO_ForLinkerMass( psmLinkerMass );
								
								if ( searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_Response == null ) {
									String msg = "No Linker Crosslink Mass record for psm id: " 
											+ psmDTO.getId() 
											+ ", search id: " + eachSearchIdToProcess
											+ ", project search id: " + eachProjectSearchIdToProcess;
									log.error( msg );
									throw new ProxlWebappDataException( msg );
								}
								String crosslinkerChemicalFormulaFromDB = searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_Response.getLinkerPerSearchCrosslinkMassDTO().getChemicalFormula();
								
								if ( crosslinkerChemicalFormulaFromDB != null ) {
									// Use Crosslinker Chemical Formula From DB
									downloadOutputLines.add( line + crosslinkerChemicalFormulaFromDB );
									
									continue;  //  EARLY CONINUE
								}
								
								//  Try to get Crosslinker Chemical Formula From Built in crosslinker 
								
								String linkerAbbr = searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_Response.getSearchLinkerDTO().getLinkerAbbr();
								
								if ( linkerAbbr == null ) {
									String msg = "No Linker abbreviation for psm id: " 
											+ psmDTO.getId() 
											+ ", search id: " + eachSearchIdToProcess
											+ ", project search id: " + eachProjectSearchIdToProcess;
									log.error( msg );
									throw new ProxlWebappDataException( msg );
								}
								
								ILinker_Builtin_Linker linker = Get_BuiltIn_Linker_From_Abbreviation_Factory.getLinkerForAbbr( linkerAbbr );
								if ( linker == null ) {
									//  No ILinker for Linker Abbreviation, is not a supported Linker for extra compute
									//  Not valid to get here.  The download link should have been hidden
									String msg = "No ILinker_Builtin_Linker for linker abbreviation: '"
											+ linkerAbbr
											+ "'.  Should not get here.  The download link should have been hidden";
									log.error( msg );
									throw new ProxlWebappInternalErrorException( msg );
								}
								
								String formula = linker.getCrosslinkFormula( psmLinkerMass.doubleValue() );

								downloadOutputLines.add( line + formula );

							}//end iterating over psms

						}// end iteration over crosslinkReportedPeptidePeptides


					}// end iteration over searches

				}// end iteration over crosslinks

			} catch ( Exception e ) {
				String msg = "Exception:  RemoteAddr: " + request.getRemoteAddr()  
						+ ", Exception caught: " + e.toString();
				log.error( msg, e );
				throw e;
			}

			OutputStreamWriter writer = null;
			try {

				ServletOutputStream out = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(out);
				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );

				for( String line : downloadOutputLines ) {
					writer.write( line );
					writer.write( "\n" ); 
				}
				
			} finally {
				try {
					if ( writer != null ) {
						writer.close();
					}
				} catch ( Exception ex ) {
					log.error( "writer.close():Exception " + ex.toString(), ex );
				}
				try {
					response.flushBuffer();
				} catch ( Exception ex ) {
					log.error( "response.flushBuffer():Exception " + ex.toString(), ex );
				}
			}
			return null;
		} catch ( Exception e ) {
			String msg = "Exception:  RemoteAddr: " + request.getRemoteAddr()  
			+ ", Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}

}
