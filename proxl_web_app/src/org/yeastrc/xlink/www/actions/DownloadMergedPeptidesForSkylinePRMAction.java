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
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.AnnDisplayNameDescPeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnValuePeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.WebMergedProteinPosition;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptide;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher.ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher;
import org.yeastrc.proteomics.mass.MassUtils;
import org.yeastrc.proteomics.peptide.peptide.Peptide;
import org.yeastrc.xlink.dao.StaticModDAO;
import org.yeastrc.xlink.dto.StaticModDTO;
import org.yeastrc.xlink.dto.UnifiedRepPepDynamicModLookupDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.actions.PeptidesMergedCommonPageDownload.PeptidesMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.XLinkWebAppUtils;
/**
 * 
 *
 */
public class DownloadMergedPeptidesForSkylinePRMAction extends Action {
	
	private static final Logger log = Logger.getLogger(DownloadMergedPeptidesForSkylinePRMAction.class);
	
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
								searchesMapOnSearchId );

				
				
				////////////
				/////   Searcher cutoffs for all searches
				SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
						peptidesMergedCommonPageDownloadResult.searcherCutoffValuesRootLevel;
				
				
				
				// generate file name
				String filename = "proxl-skyline-PRM-import-";
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
				
				//writer.write( "# Skyline PRM tool import file\n" );
				//writer.write( "# Search(es): " + StringUtils.join( searchIds, "," ) + "\n" );
				
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
				
				for( WebMergedReportedPeptide link : peptidesMergedCommonPageDownloadResult.getWebMergedReportedPeptideList() ) {
					
					// should only return cross-linked peptides
					if( !link.getLinkType().equals( XLinkUtils.CROSS_TYPE_STRING_UPPERCASE ) ) {
						continue;
					}
					
					/*
					 * Construct a set of all distinct lines to print for this reported peptide.

					 * E.g., DFNKVPNFSIR IVQKSSGLNMENLANHEHLLSPVR 2823.472@4 1473.764@4 6
					 * 
					 * Note that these lines use the linker mass from each distinct search, and use the
					 * charge states from all PSMs that mapped to this link.
					 * 
					 * Strategy will be to construct each line as a string and add to set, allowing
					 * for the uniqueness of the strings in the set to remove redundant lines.
					 * 
					 */
					
					Collection<String> lineStrings = new HashSet<>();
					
					
					//  Process links
					int unifiedReportedPeptideId = link.getUnifiedReportedPeptideId();
					//  Process for each search id:
					for ( SearchDTO search : searches ) {
						int eachProjectSearchIdToProcess = search.getProjectSearchId();
						Integer eachSearchIdToProcess = search.getSearchId();
						
						// static mods for this search
						List<StaticModDTO> staticMods = StaticModDAO.getInstance().getStaticModDTOForSearchId( search.getSearchId() );
						
						SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = 
								searcherCutoffValuesRootLevel.getPerSearchCutoffs( eachProjectSearchIdToProcess );
						if ( searcherCutoffValuesSearchLevel == null ) {
							String msg = "searcherCutoffValuesRootLevel.getPerSearchCutoffs(projectSearchId) returned null for:  " + eachProjectSearchIdToProcess;
							log.error( msg );
							throw new ProxlWebappDataException( msg );
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
							for ( PsmWebDisplayWebServiceResult psm : psms ) {

								// now iterating over the PSMs for this reported peptide in a given search
								
								
								String line = "";
								
								line += link.getPeptide1().getSequence() + " ";
								line += link.getPeptide2().getSequence() + " ";
								
								List<String> peptide1Mods = new ArrayList<>();
								List<String> peptide2Mods = new ArrayList<>();
																
								// TODO: check for static mods on cysteines?
								
								
								// create a mod to add to peptide1, for the cross-linked residue
								{
									
									
									Peptide otherPeptide = new Peptide( link.getPeptide2().getSequence() );
									
									
									// get mods of other peptide
									List<UnifiedRepPepDynamicModLookupDTO> mods = link.getMergedSearchPeptideCrosslink().getUnifiedRpDynamicModListPeptide2();
																		
									double modsSum = 0;
									
									// handle the dynamic mods
									if( mods != null && mods.size() > 0 ) {
										
										for( UnifiedRepPepDynamicModLookupDTO mod : mods ) {
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
									
									
									peptide1Mods.add( modMass.toString() + "@" + link.getPeptide1Position() );
									
								}
								
								
								
								// create a mod to add to peptide2, for the cross-linked residue
								{
									
									
									Peptide otherPeptide = new Peptide( link.getPeptide1().getSequence() );
									
									// get mods of other peptide
									List<UnifiedRepPepDynamicModLookupDTO> mods = link.getMergedSearchPeptideCrosslink().getUnifiedRpDynamicModListPeptide1();

									double modsSum = 0;
									
									// handle the dynamic mods
									if( mods != null && mods.size() > 0 ) {
										
										for( UnifiedRepPepDynamicModLookupDTO mod : mods ) {
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
									
									
									peptide2Mods.add( modMass.toString() + "@" + link.getPeptide2Position() );
									
								}
								
								
								
								// now add in other mods on the peptide
								
								// peptide 1
								{
									
									List<UnifiedRepPepDynamicModLookupDTO> mods = link.getMergedSearchPeptideCrosslink().getUnifiedRpDynamicModListPeptide1();
									if( mods != null && mods.size() > 0 ) {
										
										for( UnifiedRepPepDynamicModLookupDTO mod : mods ) {
											peptide1Mods.add( mod.getMassRoundedString() + "@" + mod.getPosition() );
										}
									}
								}
								
								
								// peptide 2
								{
									
									List<UnifiedRepPepDynamicModLookupDTO> mods = link.getMergedSearchPeptideCrosslink().getUnifiedRpDynamicModListPeptide2();
									if( mods != null && mods.size() > 0 ) {
										
										for( UnifiedRepPepDynamicModLookupDTO mod : mods ) {
											peptide2Mods.add( mod.getMassRoundedString() + "@" + mod.getPosition() );
										}
									}
								}
							
								line += StringUtils.join( peptide1Mods, "," ) + " ";
								line += StringUtils.join( peptide2Mods, "," ) + " ";
								
								line += psm.getPsmDTO().getCharge() + "\n";
								
								lineStrings.add( line );
							}
						}
					}
					
					
					// write out all the distinct lineStrings for this reported peptide
					for( String line : lineStrings ) {
						writer.write( line );
					}					
					
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
