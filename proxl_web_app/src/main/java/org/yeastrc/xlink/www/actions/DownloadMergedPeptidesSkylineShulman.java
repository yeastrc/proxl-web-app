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
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.yeastrc.xlink.dto.PsmDTO;
import org.yeastrc.xlink.linkable_positions.Get_BuiltIn_Linker_From_Abbreviation_Factory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.actions.PeptidesMergedCommonPageDownload.PeptidesMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.WebMergedProteinPosition;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptide;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher.ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_SearchLinker_ForSearchId;
import org.yeastrc.xlink.www.searcher_via_cached_data.return_objects_from_searchers_for_cached_data.SearchLinker_ForSearchId_Response;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util;

/**
 * 
 *
 */
public class DownloadMergedPeptidesSkylineShulman extends Action {
	
	private static final Logger log = Logger.getLogger(DownloadMergedPeptidesSkylineShulman.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		
		try {
			// our form
			MergedSearchViewPeptidesForm form = (MergedSearchViewPeptidesForm)actionForm;
			request.setAttribute( "mergedSearchViewCrosslinkPeptideForm", form );
			// Get the session first.  
//			HttpSession session = request.getSession();
			int[] projectSearchIds = form.getProjectSearchId();
			if ( projectSearchIds.length == 0 ) {
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			//   Get the project id for these searches
			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
			for ( int searchId : projectSearchIds ) {
				projectSearchIdsSet.add( searchId );
			}
			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
			Collections.sort( projectSearchIdsListDeduppedSorted );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search ids: ";
				for ( int searchId : projectSearchIds ) {
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
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
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
			List<Integer> searchIds = new ArrayList<>( projectSearchIdsListDeduppedSorted.size() );
			
			for( int projectSearchId : projectSearchIdsListDeduppedSorted ) {
				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
				if ( search == null ) {
					String msg = "projectSearchId '" + projectSearchId + "' not found in the database. User taken to home page.";
					log.warn( msg );
					//  Search not found, the data on the page they are requesting does not exist.
					//  The data on the user's previous page no longer reflects what is in the database.
					//  Take the user to the home page
					return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
				}
				searches.add( search );
				searchesMapOnSearchId.put( search.getSearchId(), search );
				searchIds.add( search.getSearchId() );
			}
			Collections.sort( searches, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getSearchId() - o2.getSearchId();
				}
			});
			Collections.sort( searchIds );
			
			OutputStreamWriter writer = null;
			try {
				////////     Get Merged Peptides
				PeptidesMergedCommonPageDownloadResult peptidesMergedCommonPageDownloadResult =
						PeptidesMergedCommonPageDownload.getInstance()
						.getWebMergedPeptideRecords(
								form,
								projectSearchIdsListDeduppedSorted,
								searches,
								searchesMapOnSearchId,
								PeptidesMergedCommonPageDownload.FlagCombinedReportedPeptideEntries.NO );

				MergedPeptideQueryJSONRoot mergedPeptideQueryJSONRoot = peptidesMergedCommonPageDownloadResult.getMergedPeptideQueryJSONRoot();
				
				////////////
				/////   Searcher cutoffs for all searches
				SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
						peptidesMergedCommonPageDownloadResult.searcherCutoffValuesRootLevel;

				
				String crosslinkerFormulaString = null;
				
				// if there is only one possible formula, just use it no matter what
				
				{
					Set<String> linkerAbbreviationsAllSearches = new HashSet<>(); // Accumulate distinct values for all searches
					
					Cached_SearchLinker_ForSearchId cached_SearchLinker_ForSearchId = Cached_SearchLinker_ForSearchId.getInstance();
					
					for ( Integer searchId : searchIds ) {
					
						SearchLinker_ForSearchId_Response searchLinker_ForSearchId_Response =
								cached_SearchLinker_ForSearchId.getSearchLinkers_ForSearchId_Response( searchId );
						
						List<String> linkerAbbreviationsForSearchIdList = searchLinker_ForSearchId_Response.getLinkerAbbreviationsForSearchIdList();
						if ( linkerAbbreviationsForSearchIdList == null || linkerAbbreviationsForSearchIdList.isEmpty() ) {
							String msg = "No Linker abbreviations for searchId: " + searchId;
							log.error( msg );
							throw new ProxlWebappDataException(msg);
						}
						linkerAbbreviationsAllSearches.addAll( linkerAbbreviationsForSearchIdList );  //  Accumulate distinct values for all searches
					}
					
					if( linkerAbbreviationsAllSearches.size() == 1 ) {
						
						String linkerAbbr = linkerAbbreviationsAllSearches.iterator().next();
						
						ILinker linker = Get_BuiltIn_Linker_From_Abbreviation_Factory.getLinkerForAbbr( linkerAbbr );
						
						if ( linker == null ) {
							//  No ILinker for Linker Abbreviation, is not a supported Linker for extra compute
							//  Not valid to get here.  The download link should have been hidden
							String msg = "No ILinker for linker abbreviation: '"
									+ linkerAbbr
									+ "'.  Should not get here.  The download link should have been hidden";
							log.error( msg );
							throw new ProxlWebappInternalErrorException( msg );
						}
						
						if( linker.getCrosslinkFormulas().size() == 1 ) {
							
							crosslinkerFormulaString = linker.getCrosslinkFormula( 0.0 );
						}
					}
				}
				



				Collection<String> lines = new HashSet<>();
				
				//  Cached Data
				
				Map<String, ILinker> linkersCachedKeyLinkerAbbr = new HashMap<>();
				
				Map<Integer, SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util> searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_KEY_SearchId = new HashMap<>();
				Map<Integer, SearcherCutoffValuesSearchLevel> searcherCutoffValuesSearchLevel_KEY_SearchId = new HashMap<>();
				
				//  Process Main Reported Peptides for Merged:
				
				for( WebMergedReportedPeptide link : peptidesMergedCommonPageDownloadResult.getWebMergedReportedPeptideList() ) {
					
					// should only return cross-linked peptides
					if( !link.getLinkType().equals( XLinkUtils.CROSS_TYPE_STRING_UPPERCASE ) ) {
						continue;
					}
										
					String line = link.getPeptide1().getSequence() + "\t";
					line += link.getPeptide1Position() + "\t";
					line += link.getPeptide2().getSequence() + "\t";
					line += link.getPeptide2Position() + "\t";

					WebMergedProteinPosition pp1 = link.getPeptide1ProteinPositions().get( 0 );
					WebMergedProteinPosition pp2 = link.getPeptide2ProteinPositions().get( 0 );
					
					line += pp1.getProtein().getName() + "(" + pp1.getPosition1() + ")";
					line += "--";
					line += pp2.getProtein().getName() + "(" + pp2.getPosition1() + ")";
					
					line += ":";
					
					line += link.getPeptide1().getSequence() + "--" + link.getPeptide2().getSequence() + "\t";
					
					// if there is only one possible formula, just use it no matter what
					
					if ( crosslinkerFormulaString != null ) {

						lines.add( line + crosslinkerFormulaString );
						
						// go to next reported peptide
						
						continue;  //  EARLY CONTINUE
					}
					
					
					// iterate over PSMs for this reported peptide, get linker masses to find linker formula
					int unifiedReportedPeptideId = link.getUnifiedReportedPeptideId();

					for ( SearchDTO search : searches ) {
						int eachProjectSearchIdToProcess = search.getProjectSearchId();
						Integer eachSearchIdToProcess = search.getSearchId();
						
						//  Linkers for Search
						
						//  Get from Cache, if not found, create
						SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util =
								searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_KEY_SearchId.get(  eachSearchIdToProcess );
						if ( searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util == null ) { 
							searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util = SearchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util.getInstanceForSearchId( eachSearchIdToProcess );
							searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util_KEY_SearchId.put(  eachSearchIdToProcess, searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util );
						}
						
						//  Searcher cutoffs for getting Reported Peptides and PSMs
						
						SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = searcherCutoffValuesSearchLevel_KEY_SearchId.get(  eachSearchIdToProcess );
						if ( searcherCutoffValuesSearchLevel == null ) {
							searcherCutoffValuesSearchLevel = searcherCutoffValuesRootLevel.getPerSearchCutoffs( eachProjectSearchIdToProcess );
							if ( searcherCutoffValuesSearchLevel == null ) {
								String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + eachProjectSearchIdToProcess;
								log.error( msg );
								throw new ProxlWebappDataException( msg );
							}
							searcherCutoffValuesSearchLevel_KEY_SearchId.put(  eachSearchIdToProcess, searcherCutoffValuesSearchLevel );
						}
						
						//  First get list of reported peptide ids for unifiedReportedPeptideId and search id
						List<Integer> reportedPeptideIdList = 
								ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher.getInstance()
								.getReportedPeptideIdsForSearchIdsAndUnifiedReportedPeptideId( eachSearchIdToProcess, unifiedReportedPeptideId );
						
						//  Process each search id, reported peptide id pair
						for ( int reportedPeptideId : reportedPeptideIdList ) {
							
							//  Process Each search id/reported peptide id for the link
							
							//  Get the PSMs for a Peptide/Search combination and output the records
							List<PsmWebDisplayWebServiceResult> psms = 
									PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( 
											eachSearchIdToProcess, 
											reportedPeptideId, 
											searcherCutoffValuesSearchLevel);
														
							psms = DownloadPSMs_Common.getInstance().filterPSMs( mergedPeptideQueryJSONRoot, search, psms );

							if ( psms.isEmpty() ) {
								//  No PSMs after filter so skip this reported peptide
								continue;  //  EARLY CONINUE
							}

							for ( PsmWebDisplayWebServiceResult psm : psms ) {
																
								PsmDTO psmDTO = psm.getPsmDTO();
								BigDecimal psmLinkerMass = psmDTO.getLinkerMass();
								
								String linkerAbbr = searchLinkerAndLinkerAbbreviationForLinkerMass_SingleSearch_Util.getLinkerAbbreviationForLinkerMass( psmLinkerMass );
								
								if ( linkerAbbr == null ) {
									String msg = "No Linker abbreviation for psm id: " 
											+ psmDTO.getId() 
											+ ", search id: " + eachSearchIdToProcess
											+ ", project search id: " + eachProjectSearchIdToProcess;
									log.error( msg );
									throw new ProxlWebappDataException( msg );
								}
								
								ILinker linker = linkersCachedKeyLinkerAbbr.get( linkerAbbr );
										
								if ( linker == null ) {
									linker = Get_BuiltIn_Linker_From_Abbreviation_Factory.getLinkerForAbbr( linkerAbbr );
									if ( linker == null ) {
										//  No ILinker for Linker Abbreviation, is not a supported Linker for extra compute
										//  Not valid to get here.  The download link should have been hidden
										String msg = "No ILinker for linker abbreviation: '"
												+ linkerAbbr
												+ "'.  Should not get here.  The download link should have been hidden";
										log.error( msg );
										throw new ProxlWebappInternalErrorException( msg );
									}
								}
								
								String formula = linker.getCrosslinkFormula( psmLinkerMass.doubleValue() );

								lines.add( line + formula );
								
							}
						}
					}
				}  //  end iterating over reported peptides
				
				
				// generate file name
				String filename = "proxl-peptides-skyline-shulman-";
				filename += StringUtils.join( searchIds, '-' );
				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");
				filename += "-" + fmt.print( dt );
				filename += ".txt";
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				ServletOutputStream out = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(out);
				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );
				
				for( String line : lines ) {
					writer.write( line + "\n" );
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
