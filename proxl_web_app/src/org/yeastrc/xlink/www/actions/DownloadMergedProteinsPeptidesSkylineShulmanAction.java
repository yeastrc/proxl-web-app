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
import org.yeastrc.xlink.dto.LinkerDTO;
import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.www.actions.ProteinsMergedCommonPageDownload.ProteinsMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.SearchPeptideCrosslinkAnnDataWrapper;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.SearchProteinPosition;
import org.yeastrc.xlink.www.searcher.LinkerForPSMMatcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher.SearchPeptideCrosslink_LinkedPosition_Searcher;
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
		try {
			// our form
			MergedSearchViewProteinsForm form = (MergedSearchViewProteinsForm)actionForm;
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
			
			OutputStreamWriter writer = null;
			try {
				ProteinsMergedCommonPageDownloadResult proteinsMergedCommonPageDownloadResult =
						ProteinsMergedCommonPageDownload.getInstance()
						.getCrosslinksAndLooplinkWrapped(
								form,
								ProteinsMergedCommonPageDownload.ForCrosslinksOrLooplinkOrBoth.BOTH_CROSSLINKS_AND_LOOPLINKS,
								projectSearchIdsListDeduppedSorted,
								searches,
								searchesMapOnSearchId  );
				List<MergedSearchProteinCrosslink> crosslinks = proteinsMergedCommonPageDownloadResult.getCrosslinks();
				
				Collection<Integer> excludedProteins = proteinsMergedCommonPageDownloadResult.getExcludeProtein_Ids_Set_UserInput();
				
				// generate file name
				String filename = "proxl-peptides-skyline-shulman-";
				filename += StringUtils.join( searchIdsArray, '-' );
				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");
				filename += "-" + fmt.print( dt );
				filename += ".txt";
				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				ServletOutputStream out = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(out);
				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );
								
				Collection<String> lines = new HashSet<>();

				
				// iterate over the crosslinks
				for( MergedSearchProteinCrosslink link : crosslinks ) {

					Map<SearchDTO, SearchProteinCrosslink> searchPeptides = link.getSearchProteinCrosslinks();					
					// iterate over searches
					for( SearchDTO search : searchPeptides.keySet() ) {
						
						SearchProteinCrosslink searchProteinCrosslink = searchPeptides.get( search );
						
						List<SearchPeptideCrosslinkAnnDataWrapper> crosslinkPeptides = SearchPeptideCrosslink_LinkedPosition_Searcher.getInstance().searchOnSearchProteinCrosslink(
									search,
									searchProteinCrosslink.getSearcherCutoffValuesSearchLevel(),
									link.getProtein1().getProteinSequenceObject().getProteinSequenceId(),
									link.getProtein2().getProteinSequenceObject().getProteinSequenceId(),
									link.getProtein1Position(),
									link.getProtein2Position()								
								);
								
						
						for( SearchPeptideCrosslinkAnnDataWrapper crosslinkPeptide : crosslinkPeptides ) {
							
							List<PsmWebDisplayWebServiceResult> PSMs = PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( search.getSearchId(), crosslinkPeptide.getReportedPeptideId(), searchPeptides.get( search ).getSearcherCutoffValuesSearchLevel() );
							
							for( SearchProteinPosition pp1 : crosslinkPeptide.getSearchPeptideCrosslink().getPeptide1ProteinPositions() ) {
								
								if( excludedProteins.contains( pp1.getProtein().getProteinSequenceObject().getProteinSequenceId() ) )
									continue;
								
								for( SearchProteinPosition pp2 : crosslinkPeptide.getSearchPeptideCrosslink().getPeptide2ProteinPositions() ) {
									
									if( excludedProteins.contains( pp2.getProtein().getProteinSequenceObject().getProteinSequenceId() ) )
										continue;
									
									String line = crosslinkPeptide.getSearchPeptideCrosslink().getPeptide1().getSequence() + "\t";
									line += crosslinkPeptide.getSearchPeptideCrosslink().getPeptide1Position() + "\t";
									line += crosslinkPeptide.getSearchPeptideCrosslink().getPeptide2().getSequence() + "\t";
									line += crosslinkPeptide.getSearchPeptideCrosslink().getPeptide2Position() + "\t";
									
									line += pp1.getProtein().getName() + "(" + pp1.getPosition() + ")";
									line += "--";
									line += pp2.getProtein().getName() + "(" + pp2.getPosition() + ")";
									
									line += ":";
									
									line += crosslinkPeptide.getSearchPeptideCrosslink().getPeptide1().getSequence() + "--" + crosslinkPeptide.getSearchPeptideCrosslink().getPeptide2().getSequence() + "\t";
									
									for( PsmWebDisplayWebServiceResult psm : PSMs ) {
										
										LinkerDTO linkerdto = null;
										
										try {
											linkerdto = LinkerForPSMMatcher.getInstance().getLinkerForPSM( psm.getPsmDTO() );
										} catch (Exception e ) {
											e.printStackTrace();
										}
										
										
										ILinker linker = GetLinkerFactory.getLinkerForAbbr( linkerdto.getAbbr() );
										
										String formula = linker.getCrosslinkFormula( psm.getPsmDTO().getLinkerMass().doubleValue() );

										lines.add( line + formula );
										
										
										
									}//end iterating over psms
								}// end iterating over pp2s
							}// end iterating over pp1s
						}// end iteration over crosslink peptides
						
							
					}// end iteration over searches
										
				}// end iteration over crosslinks
				
				
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
