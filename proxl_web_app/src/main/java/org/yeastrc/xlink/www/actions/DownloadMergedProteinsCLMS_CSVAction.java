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
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.yeastrc.xlink.www.actions.ProteinsMergedCommonPageDownload.ProteinsMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.download_data_utils.FilterProteinsOnSelectedLinks;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.forms.DownloadProteinCLMSForm;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
/**
 * 
 *
 */
public class DownloadMergedProteinsCLMS_CSVAction extends Action {

	private static final Logger log = LoggerFactory.getLogger( DownloadMergedSearchProteinsAction.class);

	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm actionForm,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			// our form
			DownloadProteinCLMSForm form = (DownloadProteinCLMSForm)actionForm;
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
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request, response );
			//  Test access to the project id
			WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
			if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
				//  No Access Allowed for this project id
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					//  No User session 
					return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
				}
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

			ProteinsMergedCommonPageDownloadResult proteinsMergedCommonPageDownloadResult =
					ProteinsMergedCommonPageDownload.getInstance()
							.getCrosslinksAndLooplinkWrapped(
									form,
									null, // ProteinQueryJSONRoot proteinQueryJSONRoot_Param
									ProteinsMergedCommonPageDownload.ForCrosslinksOrLooplinkOrBoth.BOTH_CROSSLINKS_AND_LOOPLINKS,
									projectId,
									projectSearchIdsListDeduppedSorted,
									searches, searchesMapOnSearchId  );

			if ( StringUtils.isNoneEmpty( form.getSelectedCrosslinksLooplinksMonolinksJSON() ) ) {
				FilterProteinsOnSelectedLinks.getInstance()
						.filterProteinsOnSelectedLinks(
								proteinsMergedCommonPageDownloadResult, form.getSelectedCrosslinksLooplinksMonolinksJSON() );
			}

			List<MergedSearchProteinCrosslink> crosslinks = proteinsMergedCommonPageDownloadResult.getCrosslinks();
			List<MergedSearchProteinLooplink> looplinks = proteinsMergedCommonPageDownloadResult.getLooplinks();

			if(form.getFormat().equals( "xinet" )) {
				this.writeOutputForXiNet(searchIdsArray, crosslinks, looplinks, form, response);
			} else if(form.getFormat().equals( "xiview" )) {
				this.writeOutputForXiView(searchIdsArray, crosslinks, looplinks, form, response);
			}


			return null;
		} catch ( Exception e ) {
			String msg = "Exception:  RemoteAddr: " + request.getRemoteAddr()
					+ ", Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}

	private void writeOutputForXiView(
			int[] searchIdsArray,
			List<MergedSearchProteinCrosslink> crosslinks,
			List<MergedSearchProteinLooplink> looplinks,
			DownloadProteinCLMSForm form,
			HttpServletResponse response

	) throws Exception {

		OutputStreamWriter writer = null;

		try {

			// generate file name
			String filename = "proxl-xiview-clms-";
			filename += StringUtils.join( searchIdsArray, '-' );
			DateTime dt = new DateTime();
			DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");
			filename += "-" + fmt.print( dt );
			filename += ".csv";
			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			ServletOutputStream out = response.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(out);
			writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );

			Collection<String> outputLines = new HashSet<>();

			writer.write( "AbsPos1,AbsPos2,Protein1,Protein2,Decoy1,Decoy2,Score\n");

			for( MergedSearchProteinCrosslink link : crosslinks ) {

				String name1 = link.getProtein1().getName();
				name1 = name1.replaceAll( "\\,", "-" );

				String name2 = link.getProtein2().getName();
				name2 = name2.replaceAll( "\\,", "-" );

				String line = link.getProtein1Position() + ",";
				line += link.getProtein2Position() + ",";

				line += name1 + ",";
				line += name2 + ",";
				line += "FALSE,FALSE,0\n";

				if( !outputLines.contains( line ) ) {
					writer.write( line );
					outputLines.add( line );
				}

			}

			if( !form.isCrosslinksOnly() ) {
				for( MergedSearchProteinLooplink link : looplinks ) {

					String name = link.getProtein().getName();
					name = name.replaceAll( "\\,", "-" );

					String line = link.getProteinPosition1() + ",";
					line += link.getProteinPosition2() + ",";

					line += name + ",";
					line += name + ",";
					line += "FALSE,FALSE,0\n";

					if( !outputLines.contains( line ) ) {
						writer.write( line );
						outputLines.add( line );
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
	}

	private void writeOutputForXiNet(
			int[] searchIdsArray,
			List<MergedSearchProteinCrosslink> crosslinks,
			List<MergedSearchProteinLooplink> looplinks,
			DownloadProteinCLMSForm form,
			HttpServletResponse response

	) throws Exception {

		OutputStreamWriter writer = null;

		try {


			// generate file name
			String filename = "proxl-xinet-clms-";
			filename += StringUtils.join( searchIdsArray, '-' );
			DateTime dt = new DateTime();
			DateTimeFormatter fmt = DateTimeFormat.forPattern( "yyyy-MM-dd");
			filename += "-" + fmt.print( dt );
			filename += ".csv";
			response.setContentType("application/x-download");
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			ServletOutputStream out = response.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(out);
			writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );


			Collection<String> outputLines = new HashSet<>();

			writer.write( "Protein1,LinkPos1,Protein2,LinkPos2\n");


			for( MergedSearchProteinCrosslink link : crosslinks ) {

				String name = link.getProtein1().getName();
				name = name.replaceAll( "\\,", "-" );

				String line = name + ",";					// Protein1
				//line += ",";								// PepPos1
				//line += ",";								// PepSeq1
				line += link.getProtein1Position() + ",";	// LinkPos1

				name = link.getProtein2().getName();
				name = name.replaceAll( "\\,", "-" );

				line += name + ",";							// Protein2
				//line += ",";								// PepPos2
				//line += ",";								// PepSeq2
				line += link.getProtein2Position() + ",";	// LinkPos2

				//line += ",";								// Score
				//line += ",";								// Id

				line += "\n";

				if( !outputLines.contains( line ) ) {
					writer.write( line );
					outputLines.add( line );
				}

			}

			if( !form.isCrosslinksOnly() ) {
				for( MergedSearchProteinLooplink link : looplinks ) {

					String name = link.getProtein().getName();
					name = name.replaceAll( "\\,", "-" );

					String line = name + ",";					// Protein1
					//line += ",";								// PepPos1
					//line += ",";								// PepSeq1
					line += link.getProteinPosition1() + ",";	// LinkPos1

					line += ",";								// Protein2 (excluded for loop-links)
					//line += ",";								// PepPos2
					//line += ",";								// PepSeq2
					line += link.getProteinPosition2() + ",";	// LinkPos2

					//line += ",";								// Score
					//line += ",";								// Id

					line += "\n";

					if( !outputLines.contains( line ) ) {
						writer.write( line );
						outputLines.add( line );
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
	}

}
