package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.objects.AnnotationTypeDTOListForSearchId;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.WebMergedProteinPosition;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptide;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmAnnotationDataSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher.ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher;
import org.yeastrc.xlink.www.actions.PeptidesMergedCommonPageDownload.PeptidesMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataDefaultDisplayInDisplayOrder;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.MergedPeptideQueryJSONRoot;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

/**
 * 
 *
 */
public class DownloadPSMsForMergedPeptidesAction extends Action {

	private static final Logger log = LoggerFactory.getLogger( DownloadPSMsForMergedPeptidesAction.class);
	
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		try {
			// our form
			MergedSearchViewPeptidesForm form = (MergedSearchViewPeptidesForm)actionForm;
			request.setAttribute( "mergedSearchViewCrosslinkPeptideForm", form );
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
				String msg = "No project ids for projectSearchIds: ";
				for ( int projectSearchId : projectSearchIds ) {
					msg += projectSearchId + ", ";
				}
				log.warn( msg );
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}
			if ( projectIdsFromSearchIds.size() > 1 ) {
				//  Invalid request, searches across projects
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_SEARCHES_ACROSS_PROJECTS );
			}
			int projectId = projectIdsFromSearchIds.get( 0 );
			request.setAttribute( "projectId", projectId ); 

			///////////////////////
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
			}
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
			}
			request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
			
			///    Done Processing Auth Check and Auth Level
			//////////////////////////////
			
			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			Map<Integer, SearchDTO> searchesMapOnSearchId = new HashMap<>();
			Set<Integer> searchIdsSet = new HashSet<>();
			List<Integer> searchIdsForFilename = new ArrayList<>( projectSearchIdsListDeduppedSorted.size() );
			
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
				searchIdsSet.add( search.getSearchId() );
				searchIdsForFilename.add( search.getSearchId() );
			}
			// Sort searches list
			Collections.sort( searches, new Comparator<SearchDTO>() {
				@Override
				public int compare(SearchDTO o1, SearchDTO o2) {
					return o1.getProjectSearchId() - o2.getProjectSearchId();
				}
			});
			Collections.sort( searchIdsForFilename );

			OutputStreamWriter writer = null;
			try {
				Map<Integer, AnnotationTypeDTOListForSearchId> psmAnnotationTypeDataDefaultDisplayInDisplayOrder =
						GetAnnotationTypeDataDefaultDisplayInDisplayOrder.getInstance()
						.getPsmAnnotationTypeDataDefaultDisplayInDisplayOrder( searchIdsSet );
				
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
				
				// generate file name
				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");
				
				String filename = "xlinks-psms-search-" 
						+ StringUtils.join( searchIdsForFilename, '-' )
						+ "-" + fmt.print( dt )
						+ ".txt";

				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);
				ServletOutputStream out = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(out);
				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );
//				writer.write( "SEARCH ID(S)\tTYPE\tPEPTIDE 1\tPOSITION\tMODS\tPEPTIDE 2\tPOSITION\tMODS\tPROTEIN 1\tPROTEIN 2\tBEST PSM Q-VALUE\tNUM PSMS\n" );
				//  Write header line
				writer.write( "SEARCH ID\tSCAN NUMBER\tPEPTIDE 1\tPOSITION 1\tMODS\tISOTOPE LABELS\tPROTEINS\tPEPTIDE 2\tPOSITION 2\tMODS\tISOTOPE LABELS\tPROTEINS\tLink Type" );
				writer.write( "\tOBSERVED M/Z\tCHARGE\tRETENTION TIME (MINUTES)\tSCAN FILENAME" );
				//  Process for each search id:
				for ( SearchDTO search : searches ) {
//					int projectSearchId = search.getProjectSearchId();
					Integer eachSearchIdToProcess = search.getSearchId();
					AnnotationTypeDTOListForSearchId annotationTypeDTOListForSearchId = psmAnnotationTypeDataDefaultDisplayInDisplayOrder.get( eachSearchIdToProcess );
					if ( annotationTypeDTOListForSearchId == null ) {
						String msg = "annotationTypeDTOListForSearchId not found for search id " + eachSearchIdToProcess;
						log.error( msg );
						throw new ProxlWebappDataException( msg );
					}
					for ( AnnotationTypeDTO psmAnnotationTypeDTO : annotationTypeDTOListForSearchId.getAnnotationTypeDTOList() ) {
						writer.write( "\t" );
						writer.write( psmAnnotationTypeDTO.getName() );
						writer.write( "(SEARCH ID: " );
						writer.write( Integer.toString( eachSearchIdToProcess ) );
						writer.write( ")" );
					}
				}
				writer.write( "\n" );

				for( WebMergedReportedPeptide link : peptidesMergedCommonPageDownloadResult.getWebMergedReportedPeptideList() ) {
					//  Process links
					int unifiedReportedPeptideId = link.getUnifiedReportedPeptideId();
					//  Process for each search id:
					for ( SearchDTO search : searches ) {
						int eachProjectSearchIdToProcess = search.getProjectSearchId();
						Integer eachSearchIdToProcess = search.getSearchId();
						
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

							psms = DownloadPSMs_Common.getInstance().filterPSMs( mergedPeptideQueryJSONRoot, search, psms );

							if ( psms.isEmpty() ) {
								//  No PSMs after filter so skip this reported peptide
								continue;  //  EARLY CONINUE
							}

							for ( PsmWebDisplayWebServiceResult psmWebDisplay : psms ) {
								writer.write( Integer.toString( eachSearchIdToProcess ) );
								writer.write( "\t" );
								if ( psmWebDisplay.getScanNumber() != null ) {
									writer.write( Integer.toString( psmWebDisplay.getScanNumber() ) );
								}
								//  Peptide 1 data
								writer.write( "\t" );
								writer.write( link.getPeptide1().getSequence() );
								writer.write( "\t" );
								writer.write( link.getPeptide1Position() );
								writer.write( "\t" );
								writer.write( link.getModsStringPeptide1() );  // MODS
								writer.write( "\t" );
								writer.write( link.getIsotopeLabelsStringPeptide1() ); // Isotope Labels
								writer.write( "\t" );
								{
									List<WebMergedProteinPosition> peptideProteinPositions = link.getPeptide1ProteinPositions();
									List<String> reportedProteinStrings = new ArrayList<>();
									for( WebMergedProteinPosition peptideProteinPosition : peptideProteinPositions ) {
										String output = peptideProteinPosition.getProtein().getName();
										if(  XLinkUtils.LOOP_TYPE_STRING_UPPERCASE.equals( link.getLinkType() ) ) {
											output += "(" + peptideProteinPosition.getPosition1() + "," + peptideProteinPosition.getPosition2() + ")";
										} else if( XLinkUtils.CROSS_TYPE_STRING_UPPERCASE.equals( link.getLinkType() ) ) {
											output += "(" + peptideProteinPosition.getPosition1() + ")";
										}
										reportedProteinStrings.add( output );
									}
									writer.write( StringUtils.join( reportedProteinStrings, "," ) );
									writer.write( "\t" );
								}
								//  Peptide 2 data
								if ( link.getPeptide2() != null ) {
									writer.write( link.getPeptide2().getSequence() );
								}
								writer.write( "\t" );
								writer.write( link.getPeptide2Position() );
								writer.write( "\t" );
								writer.write( link.getModsStringPeptide2() );  // MODS
								writer.write( "\t" );
								writer.write( link.getIsotopeLabelsStringPeptide2() ); // Isotope Labels
								writer.write( "\t" );
								{
									List<WebMergedProteinPosition> peptideProteinPositions = link.getPeptide2ProteinPositions();
									if( peptideProteinPositions != null && peptideProteinPositions.size() > 0 ) {
										List<String> reportedProteinStrings = new ArrayList<>();
										for( WebMergedProteinPosition peptideProteinPosition : peptideProteinPositions ) {
											if( XLinkUtils.CROSS_TYPE_STRING_UPPERCASE.equals( link.getLinkType() ) ) {
												String output = peptideProteinPosition.getProtein().getName();
												output += "(" + peptideProteinPosition.getPosition1() + ")";
												reportedProteinStrings.add( output );
											}
										}
										writer.write( StringUtils.join( reportedProteinStrings, "," ) );
									}
									writer.write( "\t" );
								}
								writer.write( link.getLinkType() );
								//  Scan data
								writer.write( "\t" );
								if ( StringUtils.isNotEmpty( psmWebDisplay.getPreMZRounded() ) ) {
									writer.write( psmWebDisplay.getPreMZRounded() ); // OBSERVED M/Z
								}
								writer.write( "\t" );
								if ( psmWebDisplay.getCharge() != null ) {
									writer.write( Integer.toString( psmWebDisplay.getCharge() ) ); // CHARGE
								}
								writer.write( "\t" );
								if ( StringUtils.isNotEmpty( psmWebDisplay.getRetentionTimeMinutesRoundedString() ) ) {
									writer.write( psmWebDisplay.getRetentionTimeMinutesRoundedString() ); // RETENTION TIME (MINUTES)
								}
								writer.write( "\t" );
								if ( psmWebDisplay.getScanFilename() != null ) {
									writer.write( psmWebDisplay.getScanFilename() );   /// SCAN FILENAME
								}
								///  Fill in empty cells for other search ids before search id being processed
								for ( SearchDTO searchOtherThanSearchIdBeingProcessed : searches ) {
									int projectSearchIdOtherThanSearchIdBeingProcessed = searchOtherThanSearchIdBeingProcessed.getProjectSearchId();
									Integer searchIdOtherThanSearchIdBeingProcessed = searchOtherThanSearchIdBeingProcessed.getSearchId();
									
									if ( projectSearchIdOtherThanSearchIdBeingProcessed >= eachProjectSearchIdToProcess ) {
										//  Exit loop at eachSearchIdToProcess,  >= comparison since search ids in sorted order
										break;  // EARLY EXIT of loop
									}
									//  Fill in cells for search id searchIdOtherThanSearchIdBeingProcessed
									AnnotationTypeDTOListForSearchId annotationTypeDTOListFor_OTHER_SearchId = 
											psmAnnotationTypeDataDefaultDisplayInDisplayOrder.get( searchIdOtherThanSearchIdBeingProcessed );
									if ( annotationTypeDTOListFor_OTHER_SearchId == null ) {
										String msg = "annotationTypeDTOListFor_OTHER_SearchId not found for search id " + searchIdOtherThanSearchIdBeingProcessed;
										log.error( msg );
										throw new ProxlWebappDataException( msg );
									}
									for ( int counter = 0; counter < annotationTypeDTOListFor_OTHER_SearchId.getAnnotationTypeDTOList().size(); counter++ ) {
										writer.write( "\t" );
									}
								}
								AnnotationTypeDTOListForSearchId annotationTypeDTOListForSearchId = 
										psmAnnotationTypeDataDefaultDisplayInDisplayOrder.get( eachSearchIdToProcess );
								if ( annotationTypeDTOListForSearchId == null ) {
									String msg = "annotationTypeDTOListForSearchId not found for search id " + eachSearchIdToProcess;
									log.error( msg );
									throw new ProxlWebappDataException( msg );
								}
								//  Get set of annotation type ids for getting annotation data
								Set<Integer> annotationTypeIdsForGettingAnnotationData = new HashSet<>();
								for ( AnnotationTypeDTO annotationTypeDTO : annotationTypeDTOListForSearchId.getAnnotationTypeDTOList() ) {
									annotationTypeIdsForGettingAnnotationData.add( annotationTypeDTO.getId() );
								}
								Map<Integer, PsmAnnotationDTO> psmAnnotationDTOMapOnTypeId = new HashMap<>();
								List<PsmAnnotationDTO> psmAnnotationDataList = 
										PsmAnnotationDataSearcher.getInstance()
										.getPsmAnnotationDTOList( psmWebDisplay.getPsmDTO().getId(), annotationTypeIdsForGettingAnnotationData );
								for ( PsmAnnotationDTO psmAnnotationDataItem : psmAnnotationDataList ) {
									psmAnnotationDTOMapOnTypeId.put( psmAnnotationDataItem.getAnnotationTypeId(), psmAnnotationDataItem );
								}
								for ( AnnotationTypeDTO annotationTypeDTO : annotationTypeDTOListForSearchId.getAnnotationTypeDTOList() ) {
									PsmAnnotationDTO psmAnnotationDTO = psmAnnotationDTOMapOnTypeId.get( annotationTypeDTO.getId() );
									if ( psmAnnotationDTO == null ) {
										String msg = "psmAnnotationDTO not foudn for annotation type id " +  annotationTypeDTO.getId();
										log.error( msg );
										throw new ProxlWebappDataException( msg );
									}
									writer.write( "\t" );
									writer.write( psmAnnotationDTO.getValueString() );
								}
								///  Fill in empty cells for other search ids After search id being processed
								for ( SearchDTO searchOtherThanSearchIdBeingProcessed : searches ) {
									int projectSearchIdOtherThanSearchIdBeingProcessed = searchOtherThanSearchIdBeingProcessed.getProjectSearchId();
									Integer searchIdOtherThanSearchIdBeingProcessed = searchOtherThanSearchIdBeingProcessed.getSearchId();
									if ( projectSearchIdOtherThanSearchIdBeingProcessed <= eachProjectSearchIdToProcess ) {
										//  skip to next entry at eachSearchIdToProcess,  <= comparison since search ids in sorted order
										continue;  // EARLY CONTINUE of loop, Skip to next entry
									}
									//  Fill in cells for search id searchIdOtherThanSearchIdBeingProcessed
									AnnotationTypeDTOListForSearchId annotationTypeDTOListFor_OTHER_SearchId = 
											psmAnnotationTypeDataDefaultDisplayInDisplayOrder.get( searchIdOtherThanSearchIdBeingProcessed );
									if ( annotationTypeDTOListFor_OTHER_SearchId == null ) {
										String msg = "annotationTypeDTOListFor_OTHER_SearchId not found for search id " + searchIdOtherThanSearchIdBeingProcessed;
										log.error( msg );
										throw new ProxlWebappDataException( msg );
									}
									for ( int counter = 0; counter < annotationTypeDTOListFor_OTHER_SearchId.getAnnotationTypeDTOList().size(); counter++ ) {
										writer.write( "\t" );
									}
								}
								writer.write( "\n" );
							}
						}
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
}
