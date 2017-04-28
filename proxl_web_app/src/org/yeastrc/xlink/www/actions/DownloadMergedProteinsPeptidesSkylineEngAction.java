package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.yeastrc.proteomics.mass.MassUtils;
import org.yeastrc.proteomics.peptide.peptide.Peptide;
import org.yeastrc.xlink.dao.StaticModDAO;
import org.yeastrc.xlink.dto.StaticModDTO;
import org.yeastrc.xlink.www.actions.ProteinsMergedCommonPageDownload.ProteinsMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dao.SrchRepPeptPeptideDAO;
import org.yeastrc.xlink.www.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink;
import org.yeastrc.xlink.www.objects.SearchProteinCrosslink;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptDynamicModSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
/**
 * 
 *
 */
public class DownloadMergedProteinsPeptidesSkylineEngAction extends Action {

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

				// generate file name
				String filename = "proxl-skyline-PRM-import-";
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

				/*

				From Jimmy Eng:

				Here's what I would expect the exported file to look like (ideally with more accurate mass numbers):

				0.0
				DFNKVPNFSIR IVQKSSGLNMENLANHEHLLSPVR 2823.472@4 1473.764@4 6

				The value of 2823.472 corresponds to the mass of peptide IVQKSSGLNMENLANHEHLLSPVR (2685.402068) plus mass of crosslinker 138.07.  And the mass 1473.764 is the mass of peptide DFNKVPNFSIR (1335.693535) plus mass of crosslinker 138.07.  It would be great to have at least 4 digits of precision in the encoded mass modification strings.

				If there are any other modifications on the peptide, encode them in the same way.  For example, presume the methionine on the peptide IVQKSSGLNMENLANHEHLLSPVR  is modified with oxidation (15.994915).  The second modification string would change from
				   1473.764@4
				to
				   1473.764@4,15.9959@10

				 */


				writer.write( "0.0\n" );


				Collection<String> lines = new HashSet<>();


				// iterate over the crosslinks
				for( MergedSearchProteinCrosslink crosslink : crosslinks ) {
					
					Map<SearchDTO, SearchProteinCrosslink> searchProteinCrosslinks = crosslink.getSearchProteinCrosslinks();					
					// iterate over searches
					for( SearchDTO search : searchProteinCrosslinks.keySet() ) {

						// static mods for this search
						List<StaticModDTO> staticMods = StaticModDAO.getInstance().getStaticModDTOForSearchId( search.getSearchId() );
						
						SearchProteinCrosslink searchProteinCrosslink = searchProteinCrosslinks.get( search );
						
						List<ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink> crosslinkReportedPeptidePeptides = searchProteinCrosslink.getReportedPeptide_SearchReportedPeptidepeptideId_CrosslinkList();


						for( ReportedPeptide_SearchReportedPeptidepeptideId_Crosslink crosslinkReportedPeptidePeptide : crosslinkReportedPeptidePeptides ) {

							
							List<PsmWebDisplayWebServiceResult> PSMs = PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( search.getSearchId(), crosslinkReportedPeptidePeptide.getReportedPeptideId(), searchProteinCrosslinks.get( search ).getSearcherCutoffValuesSearchLevel() );

							SrchRepPeptPeptideDTO searchReportedPeptidePeptide1 = SrchRepPeptPeptideDAO.getInstance().getForId( crosslinkReportedPeptidePeptide.getSearchReportedPeptidepeptideId_1() );
							SrchRepPeptPeptideDTO searchReportedPeptidePeptide2 = SrchRepPeptPeptideDAO.getInstance().getForId( crosslinkReportedPeptidePeptide.getSearchReportedPeptidepeptideId_2() );

							// ensure these are ordered the same every time
							if( searchReportedPeptidePeptide1.getId() > searchReportedPeptidePeptide2.getId() ) {
								SrchRepPeptPeptideDTO tmp = searchReportedPeptidePeptide1;
								searchReportedPeptidePeptide1 = searchReportedPeptidePeptide2;
								searchReportedPeptidePeptide2 = tmp;
							}
							
							
							PeptideDTO peptide1 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( searchReportedPeptidePeptide1.getPeptideId() );
							PeptideDTO peptide2 = PeptideDAO.getInstance().getPeptideDTOFromDatabase( searchReportedPeptidePeptide2.getPeptideId() );
							
							List<SrchRepPeptPeptDynamicModDTO> dynamidMods1 = SrchRepPeptPeptDynamicModSearcher.getInstance().getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( searchReportedPeptidePeptide1.getId() );
							List<SrchRepPeptPeptDynamicModDTO> dynamidMods2 = SrchRepPeptPeptDynamicModSearcher.getInstance().getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( searchReportedPeptidePeptide2.getId() );
							
							
							for( PsmWebDisplayWebServiceResult psm : PSMs ) {
								
								
								// TODO: a lot of this doesn't need to be done at the PSM level--only the charge and linker mass need to be at the PSM level
								
								
								String line = "";
								
								line += peptide1.getSequence() + " ";
								line += peptide2.getSequence() + " ";
								
								List<String> peptide1Mods = new ArrayList<>();
								List<String> peptide2Mods = new ArrayList<>();
								
								


								// create a mod to add to peptide1, for the cross-linked residue
								{
									Peptide otherPeptide = new Peptide( peptide2.getSequence() );


									// get mods of other peptide									

									double modsSum = 0;

									// handle the dynamic mods
									if( dynamidMods2 != null && dynamidMods2.size() > 0 ) {

										for( SrchRepPeptPeptDynamicModDTO mod : dynamidMods2 ) {
											modsSum += mod.getMass();											
										}
									}

									// handle the static mods
									if( staticMods != null && staticMods.size() > 0 ) {
										for( StaticModDTO staticMod : staticMods ) {
											BigDecimal mass = staticMod.getMass();
											int count = getNumberOfTimesResidueOccurs( staticMod.getResidue(), otherPeptide.getSequence() );

											modsSum += mass.doubleValue() * (double)count;
										}
									}

									BigDecimal modMass = psm.getPsmDTO().getLinkerMass();

									modMass = modMass.add( new BigDecimal( otherPeptide.getMass( MassUtils.MASS_TYPE_MONOISOTOPIC ) ) );
									modMass = modMass.add( new BigDecimal( modsSum ) );

									modMass = modMass.setScale( 5, RoundingMode.CEILING );		// set to 5 decimal places


									peptide1Mods.add( modMass.toString() + "@" + searchReportedPeptidePeptide1.getPeptidePosition_1() );
								}



								// create a mod to add to peptide2, for the cross-linked residue
								{
									Peptide otherPeptide = new Peptide( peptide1.getSequence() );


									// get mods of other peptide									

									double modsSum = 0;

									// handle the dynamic mods
									if( dynamidMods1 != null && dynamidMods1.size() > 0 ) {

										for( SrchRepPeptPeptDynamicModDTO mod : dynamidMods1 ) {
											modsSum += mod.getMass();											
										}
									}

									// handle the static mods
									if( staticMods != null && staticMods.size() > 0 ) {
										for( StaticModDTO staticMod : staticMods ) {
											BigDecimal mass = staticMod.getMass();
											int count = getNumberOfTimesResidueOccurs( staticMod.getResidue(), otherPeptide.getSequence() );

											modsSum += mass.doubleValue() * (double)count;
										}
									}

									BigDecimal modMass = psm.getPsmDTO().getLinkerMass();

									modMass = modMass.add( new BigDecimal( otherPeptide.getMass( MassUtils.MASS_TYPE_MONOISOTOPIC ) ) );
									modMass = modMass.add( new BigDecimal( modsSum ) );

									modMass = modMass.setScale( 5, RoundingMode.CEILING );		// set to 5 decimal places


									peptide2Mods.add( modMass.toString() + "@" + searchReportedPeptidePeptide2.getPeptidePosition_1() );
								}



								// now add in other mods on the peptide

								// peptide 1
								{

									if( dynamidMods1 != null && dynamidMods1.size() > 0 ) {

										for( SrchRepPeptPeptDynamicModDTO mod : dynamidMods1 ) {
											peptide1Mods.add( mod.getMass() + "@" + mod.getPosition() );
										}
									}
								}


								// peptide 2
								{

									if( dynamidMods2 != null && dynamidMods2.size() > 0 ) {

										for( SrchRepPeptPeptDynamicModDTO mod : dynamidMods2 ) {
											peptide2Mods.add( mod.getMass() + "@" + mod.getPosition() );
										}
									}
								}

								line += StringUtils.join( peptide1Mods, "," ) + " ";
								line += StringUtils.join( peptide2Mods, "," ) + " ";

								line += psm.getPsmDTO().getCharge() + "\n";

								lines.add( line );



							}//end iterating over psms
						}// end iteration over crosslink peptides


					}// end iteration over searches

				}// end iteration over crosslinks


				for( String line : lines ) {
					writer.write( line );
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
	
	private int getNumberOfTimesResidueOccurs( String residue, String sequence ) {
		
		return StringUtils.countMatches( sequence,  residue );
	}

}
