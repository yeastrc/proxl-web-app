package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
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
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.linkable_positions.Get_BuiltIn_Linker_From_Abbreviation_Factory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker_Builtin_Linker;
import org.yeastrc.xlink.www.actions.ProteinsMergedCommonPageDownload.ProteinsMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dao.SrchRepPeptPeptideDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.form_query_json_objects.ProteinQueryJSONRoot;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
/**
 * 
 *
 */
public class DownloadMergedProteinsPeptidesSkylineShulmanAction extends Action {

	private static final Logger log = Logger.getLogger(DownloadMergedSearchProteinsAction.class);

	public ActionForward execute( ActionMapping mapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response )
					throws Exception {
		

		throw new ProxlWebappInternalErrorException( "Not Currently Implemented" );
		
//		try {
//			// our form
//			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm)actionForm;
//			// Get the session first.  
//			//			HttpSession session = request.getSession();
//			//   Get the project id for these searches
//			int[] projectSearchIds = form.getProjectSearchId();
//			if ( projectSearchIds.length == 0 ) {
//				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
//			}
//			//   Get the project id for these searches
//			Set<Integer> projectSearchIdsSet = new HashSet<Integer>( );
//			for ( int projectSearchId : projectSearchIds ) {
//				projectSearchIdsSet.add( projectSearchId );
//			}
//			List<Integer> projectSearchIdsListDeduppedSorted = new ArrayList<>( projectSearchIdsSet );
//			Collections.sort( projectSearchIdsListDeduppedSorted );
//			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
//			if ( projectIdsFromSearchIds.isEmpty() ) {
//				// should never happen
//				String msg = "No project ids for search ids: ";
//				for ( int projectSearchId : projectSearchIds ) {
//					msg += projectSearchId + ", ";
//				}
//				log.error( msg );
//				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
//			}
//			if ( projectIdsFromSearchIds.size() > 1 ) {
//				//  Invalid request, searches across projects
//				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
//			}
//			int projectId = projectIdsFromSearchIds.get( 0 );
//			request.setAttribute( "projectId", projectId ); 
//			///////////////////////
//			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
//					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );
//			if ( accessAndSetupWebSessionResult.isNoSession() ) {
//				//  No User session 
//				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
//			}
//			//  Test access to the project id
//			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
//			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
//				//  No Access Allowed for this project id
//				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
//			}
//			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
//
//			///    Done Processing Auth Check and Auth Level
//			//////////////////////////////
//
//			List<SearchDTO> searches = new ArrayList<SearchDTO>( projectSearchIdsListDeduppedSorted.size() );
//			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();
//			int[] searchIdsArray = new int[ projectSearchIdsListDeduppedSorted.size() ];
//			int searchIdsArrayIndex = 0;
//			List<Integer> searchIds = new ArrayList<>( projectSearchIdsListDeduppedSorted.size() );
//
//			for( int projectSearchId : projectSearchIdsListDeduppedSorted ) {
//				SearchDTO search = SearchDAO.getInstance().getSearchFromProjectSearchId( projectSearchId );
//				if ( search == null ) {
//					String msg = "search id '" + projectSearchId + "' not found in the database. User taken to home page.";
//					log.warn( msg );
//					//  Search not found, the data on the page they are requesting does not exist.
//					//  The data on the user's previous page no longer reflects what is in the database.
//					//  Take the user to the home page
//					return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
//				}
//				searches.add( search );
//				searchesMapOnSearchId.put( search.getSearchId(), search );
//				searchIdsArray[ searchIdsArrayIndex ] = search.getSearchId();
//				searchIdsArrayIndex++;
//				searchIds.add( search.getSearchId() );
//			}
//			// Sort searches list
//			Collections.sort( searches, new Comparator<SearchDTO>() {
//				@Override
//				public int compare(SearchDTO o1, SearchDTO o2) {
//					return o1.getSearchId() - o2.getSearchId();
//				}
//			});
//			Collections.sort( searchIds );
//
//			OutputStreamWriter writer = null;
//			try {
//				ProteinsMergedCommonPageDownloadResult proteinsMergedCommonPageDownloadResult =
//						ProteinsMergedCommonPageDownload.getInstance()
//						.getCrosslinksAndLooplinkWrapped(
//								form,
//								ProteinsMergedCommonPageDownload.ForCrosslinksOrLooplinkOrBoth.BOTH_CROSSLINKS_AND_LOOPLINKS,
//								projectSearchIdsListDeduppedSorted,
//								searches,
//								searchesMapOnSearchId  );
//
//				ProteinQueryJSONRoot proteinQueryJSONRoot = proteinsMergedCommonPageDownloadResult.getProteinQueryJSONRoot();
//				
//				List<MergedSearchProteinCrosslink> crosslinks = proteinsMergedCommonPageDownloadResult.getCrosslinks();
//
//				// generate file name
//				String filename = "proxl-peptides-skyline-shulman-";
//				filename += StringUtils.join( searchIdsArray, '-' );
//				DateTime dt = new DateTime();
//				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");
//				filename += "-" + fmt.print( dt );
//				filename += ".txt";
//				response.setContentType("application/x-download");
//				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
//				ServletOutputStream out = response.getOutputStream();
//				BufferedOutputStream bos = new BufferedOutputStream(out);
//				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );
//
//				Collection<String> lines = new HashSet<>();
//				
//				Collection<LinkerDTO> linkerDTOs = LinkersForSearchIdsSearcher.getInstance().getLinkersForSearchIds( searchIds );
//
//
//				// iterate over the crosslinks
//				for( MergedSearchProteinCrosslink link : crosslinks ) {
//
//					Map<SearchDTO, SearchProteinCrosslink> searchProteinCrosslinks = link.getSearchProteinCrosslinks();					
//					
//					// iterate over searches that contain this crosslink
//					for( SearchDTO search : searchProteinCrosslinks.keySet() ) {
//
//						SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinks.get( search );
//
//						
//						List<ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink> crosslinkReportedPeptidePeptides = searchProteinCrosslink.getReportedPeptide_SearchReportedPeptidepeptideId_CrosslinkList();
//
//						for( ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink crosslinkReportedPeptidePeptide : crosslinkReportedPeptidePeptides ) {
//
//							List<PsmWebDisplayWebServiceResult> PSMs = PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( search.getSearchId(), crosslinkReportedPeptidePeptide.getReportedPeptideId(), searchProteinCrosslinks.get( search ).getSearcherCutoffValuesSearchLevel() );
//
//							PSMs = DownloadPSMs_Common.getInstance().filterPSMs( proteinQueryJSONRoot, search, PSMs );
//
//							if ( PSMs.isEmpty() ) {
//								//  No PSMs after filter so skip this reported peptide
//								continue;  //  EARLY CONINUE
//							}
//
//							SrchRepPeptPeptideDTO searchReportedPeptidePeptide1 = SrchRepPeptPeptideDAO.getInstance().getForId( crosslinkReportedPeptidePeptide.getSearchReportedPeptidepeptideId_1() );
//							SrchRepPeptPeptideDTO searchReportedPeptidePeptide2 = SrchRepPeptPeptideDAO.getInstance().getForId( crosslinkReportedPeptidePeptide.getSearchReportedPeptidepeptideId_2() );
//
//							PeptideDTO peptide1 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( searchReportedPeptidePeptide1.getPeptideId() );
//							PeptideDTO peptide2 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( searchReportedPeptidePeptide2.getPeptideId() );
//							
//
//							String line = peptide1.getSequence() + "\t";
//							line += searchReportedPeptidePeptide1.getPeptidePosition_1() + "\t";
//							line += peptide2.getSequence() + "\t";
//							line += searchReportedPeptidePeptide2.getPeptidePosition_1() + "\t";
//
//							line += link.getProtein1().getName() + "(" + link.getProtein1Position() + ")";
//							line += "--";
//							line += link.getProtein2().getName() + "(" + link.getProtein2Position() + ")";
//
//							line += ":";
//
//							line += peptide1.getSequence() + "--" + peptide2.getSequence() + "\t";
//
//							
//							// if there is only one possible formula, just use it no matter what
//							{
//
//								if( linkerDTOs.size() == 1 ) {
//									ILinker linker = GetLinkerFactory.getLinkerForAbbr( searches.get( 0 ).getLinkers().get( 0 ).getAbbr() );
//									if( linker.getCrosslinkFormulas().size() == 1 ) {
//										
//										lines.add( line + linker.getCrosslinkFormula( 0.0 ) );
//										continue;// go to next reported peptide
//									}
//								}
//							}
//							
//							
//							for( PsmWebDisplayWebServiceResult psm : PSMs ) {
//
//								LinkerDTO linkerdto = null;
//
//								try {
//									linkerdto = LinkerForPSMMatcher.getInstance().getLinkerForPSM( psm.getPsmDTO() );
//								} catch (Exception e ) {
//									try {
//										int psmId = psm.getPsmDTO().getId();
//										log.error( "Error getting linkerDTO for psmId: " + psmId, e );
//									} catch (Exception e2 ) {
//										log.error( "Error getting searchId: " + psm.getSearchId(), e2 );
//									}
//								}
//
//								ILinker linker = GetLinkerFactory.getLinkerForAbbr( linkerdto.getAbbr() );
//
//								String formula = linker.getCrosslinkFormula( psm.getPsmDTO().getLinkerMass().doubleValue() );
//
//								lines.add( line + formula );
//
//
//
//							}//end iterating over psms
//
//						}// end iteration over crosslinkReportedPeptidePeptides
//
//
//					}// end iteration over searches
//
//				}// end iteration over crosslinks
//
//
//				for( String line : lines ) {
//					writer.write( line + "\n" );
//				}
//
//			} finally {
//				try {
//					if ( writer != null ) {
//						writer.close();
//					}
//				} catch ( Exception ex ) {
//					log.error( "writer.close():Exception " + ex.toString(), ex );
//				}
//				try {
//					response.flushBuffer();
//				} catch ( Exception ex ) {
//					log.error( "response.flushBuffer():Exception " + ex.toString(), ex );
//				}
//			}
//			return null;
//		} catch ( Exception e ) {
//			String msg = "Exception:  RemoteAddr: " + request.getRemoteAddr()  
//			+ ", Exception caught: " + e.toString();
//			log.error( msg, e );
//			throw e;
//		}
	}

}
