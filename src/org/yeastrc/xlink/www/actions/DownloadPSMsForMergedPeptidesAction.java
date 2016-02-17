package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.PsmAnnotationDTO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesSearchLevel;
import org.yeastrc.xlink.www.objects.AnnDisplayNameDescPeptPsmListsPair;
import org.yeastrc.xlink.www.objects.AnnotationDisplayNameDescription;
import org.yeastrc.xlink.www.objects.AnnotationTypeDTOListForSearchId;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.PsmWebDisplayWebServiceResult;
import org.yeastrc.xlink.www.objects.ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult;
import org.yeastrc.xlink.www.objects.ReportedPeptidesForMergedPeptidePageWrapper;
import org.yeastrc.xlink.www.objects.WebMergedReportedPeptide;
import org.yeastrc.xlink.www.searcher.PeptideMergedWebPageSearcher;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.PsmAnnotationDataSearcher;
import org.yeastrc.xlink.www.searcher.PsmWebDisplaySearcher;
import org.yeastrc.xlink.www.searcher.ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher;
import org.yeastrc.xlink.www.searcher.ReportedPeptidesForUnifiedPeptIdSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchModMassDistinctSearcher;
import org.yeastrc.xlink.www.actions.PeptidesMergedCommonPageDownload.PeptidesMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeDataDefaultDisplayInDisplayOrder;
import org.yeastrc.xlink.www.constants.DynamicModificationsSelectionConstants;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory;
import org.yeastrc.xlink.www.form_query_json_objects.Z_CutoffValuesObjectsToOtherObjectsFactory.Z_CutoffValuesObjectsToOtherObjects_RootResult;
import org.yeastrc.xlink.www.forms.MergedSearchViewPeptidesForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;



public class DownloadPSMsForMergedPeptidesAction extends Action {
	
	private static final Logger log = Logger.getLogger(DownloadPSMsForMergedPeptidesAction.class);

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


			int[] searchIds = form.getSearchIds();
			
			
			if ( searchIds.length == 0 ) {
				
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}


			//   Get the project id for these searches

			Set<Integer> searchIdsSet = new HashSet<Integer>( );

			for ( int searchId : searchIds ) {

				searchIdsSet.add( searchId );
			}


			List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searchIdsSet );

			Collections.sort( searchIdsListDeduppedSorted );

			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIdsSet );
			
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
			

			
//			String project_id_from_query_string = request.getParameter( WebConstants.PARAMETER_PROJECT_ID );
//			
//
//			if ( StringUtils.isEmpty( project_id_from_query_string ) ) {
//
//				//  copy the project from the searches to the URL and redirect to that new URL.
//				
//				String getRequestURI = request.getRequestURI();
//				
//				String getQueryString = request.getQueryString();
//				
//				String newURL = getRequestURI + "?" + WebConstants.PARAMETER_PROJECT_ID + "=" + projectId + "&" + getQueryString;
//
//				if ( log.isInfoEnabled() ) {
//					
//					log.info( "Redirecting to new URL to add '" + WebConstants.PARAMETER_PROJECT_ID + "=" + projectId + "' to query string.  new URL: " + newURL );
//				}
//				
//				response.sendRedirect( newURL );
//				
//				return null;
//			}
			
			
			

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
								searchIdsListDeduppedSorted,
								searches,
								searchesMapOnId,
								searchIdsSet );


				////////////

				/////   Searcher cutoffs for all searches

				CutoffValuesRootLevel cutoffValuesRootLevel = peptidesMergedCommonPageDownloadResult.getMergedPeptideQueryJSONRoot().getCutoffs();

				Z_CutoffValuesObjectsToOtherObjects_RootResult cutoffValuesObjectsToOtherObjects_RootResult =
						Z_CutoffValuesObjectsToOtherObjectsFactory
						.createSearcherCutoffValuesRootLevel( searchIdsListDeduppedSorted, cutoffValuesRootLevel );
				
				SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel =
						cutoffValuesObjectsToOtherObjects_RootResult.getSearcherCutoffValuesRootLevel();

				
				

				// generate file name
				String filename = "xlinks-psms-search-";
				filename += StringUtils.join( form.getSearchIds(), '-' );

				DateTime dt = new DateTime();
				DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");
				filename += "-" + fmt.print( dt );

				filename += ".txt";

				response.setContentType("application/x-download");
				response.setHeader("Content-Disposition", "attachment; filename=" + filename);

				
				

				ServletOutputStream out = response.getOutputStream();

				BufferedOutputStream bos = new BufferedOutputStream(out);

				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );




//				writer.write( "SEARCH ID(S)\tTYPE\tPEPTIDE 1\tPOSITION\tMODS\tPEPTIDE 2\tPOSITION\tMODS\tPROTEIN 1\tPROTEIN 2\tBEST PSM Q-VALUE\tNUM PSMS\n" );

				//  Write header line
				
				writer.write( "SEARCH ID\tSCAN NUMBER\tPEPTIDE 1\tPOSITION 1\tMODS\tPEPTIDE 1\tPOSITION 2\tMODS\tLink Type" );
				writer.write( "\tOBSERVED M/Z\tCHARGE\tRETENTION TIME (MINUTES)\tSCAN FILENAME" );


				//  Process for each search id:

				for ( Integer eachSearchIdToProcess : searchIdsListDeduppedSorted ) {
					
					AnnotationTypeDTOListForSearchId annotationTypeDTOListForSearchId = psmAnnotationTypeDataDefaultDisplayInDisplayOrder.get( eachSearchIdToProcess );

					if ( annotationTypeDTOListForSearchId == null ) {
						
						String msg = "annotationTypeDTOListForSearchId not foudn for search id " + eachSearchIdToProcess;
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


//		SEARCH ID
//		SCAN NUMBER
//		PEPTIDE 1
//		POSITION 1
//		MODS
//		PEPTIDE 1
//		POSITION 2
//		MODS
//		Link Type
//		OBSERVED M/Z
//		CHARGE
//		RETENTION TIME (MINUTES)
//		SCAN FILENAME (THE MZML FILE)
				
				
				
				for( WebMergedReportedPeptide link : peptidesMergedCommonPageDownloadResult.getWebMergedReportedPeptideList() ) {

					//  Process links
					
					
					int unifiedReportedPeptideId = link.getUnifiedReportedPeptideId();


					//  Process for each search id:

					for ( Integer eachSearchIdToProcess : searchIdsListDeduppedSorted ) {
						
						
						
						SearcherCutoffValuesSearchLevel searcherCutoffValuesSearchLevel = 
								searcherCutoffValuesRootLevel.getPerSearchCutoffs( eachSearchIdToProcess );


						//  First get list of reported peptide ids for unifiedReportedPeptideId and search id

						List<Integer> singleSearchIdList = new ArrayList<>( 1 );

						singleSearchIdList.add( eachSearchIdToProcess );

						List<ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult>  resultList = 
								ReportedPeptideIdsForSearchIdsUnifiedPeptideIdSearcher.getInstance()
								.getReportedPeptideIdsForSearchIdsAndUnifiedReportedPeptideId( singleSearchIdList, unifiedReportedPeptideId );


						//  Process each search id, reported peptide id pair

						for ( ReportedPeptideIdsForSearchIdsUnifiedPeptideIdResult item : resultList ) {

							int reportedPeptideId = item.getReportedPeptideId();


							//  Process Each search id/reported peptide id for the link


							//  Get the PSMs for a Peptide/Search combination and output the records

							List<PsmWebDisplayWebServiceResult> psms = 
									PsmWebDisplaySearcher.getInstance().getPsmsWebDisplay( 
											eachSearchIdToProcess, 
											reportedPeptideId, 
											searcherCutoffValuesSearchLevel);
							
							

							for ( PsmWebDisplayWebServiceResult psmWebDisplay : psms ) {
					
							//  Process each PSM
							
//								SEARCH ID
//								SCAN NUMBER
//								PEPTIDE 1
//								POSITION 1
//								MODS
//								PEPTIDE 1
//								POSITION 2
//								MODS
//								Link Type
//								OBSERVED M/Z
//								CHARGE
//								RETENTION TIME (MINUTES)
//								SCAN FILENAME (THE MZML FILE)

					

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

							//  Peptide 2 data

							writer.write( "\t" );
							if ( link.getPeptide2() != null ) {
								writer.write( link.getPeptide2().getSequence() );
							}
							writer.write( "\t" );
							writer.write( link.getPeptide2Position() );
							
							writer.write( "\t" );
							writer.write( link.getModsStringPeptide2() );  // MODS
							
							
							
							writer.write( "\t" );
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

							for ( Integer searchIdOtherThanSearchIdBeingProcessed : searchIdsListDeduppedSorted ) {
								
								if ( searchIdOtherThanSearchIdBeingProcessed >= eachSearchIdToProcess ) {
									
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

							for ( Integer searchIdOtherThanSearchIdBeingProcessed : searchIdsListDeduppedSorted ) {
								
								if ( searchIdOtherThanSearchIdBeingProcessed <= eachSearchIdToProcess ) {
									
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


//							SEARCH ID
//							SCAN NUMBER
//							PEPTIDE 1
//							POSITION 1
//							MODS
//							PEPTIDE 1
//							POSITION 2
//							MODS
//							Link Type
//							OBSERVED M/Z
//							CHARGE
//							RETENTION TIME (MINUTES)
//							SCAN FILENAME (THE MZML FILE)
							
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
			
			String msg = "Exception caught: " + e.toString();
			
			log.error( msg, e );
			
			throw e;
		}
	}
	
	
	
	
	
}
