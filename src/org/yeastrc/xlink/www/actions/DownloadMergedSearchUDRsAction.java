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
import org.yeastrc.xlink.www.dao.SearchDAO;
import org.yeastrc.xlink.www.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProteinCrosslink;
import org.yeastrc.xlink.www.objects.MergedSearchProteinLooplink;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.actions.ProteinsMergedCommonPageDownload.ProteinsMergedCommonPageDownloadResult;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.forms.MergedSearchViewProteinsForm;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.XLinkWebAppUtils;


/**
 * 
 *
 */
public class DownloadMergedSearchUDRsAction extends Action {

	private static final Logger log = Logger.getLogger(DownloadMergedSearchUDRsAction.class);
	
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

			int[] searchIds = form.getSearchIds();
			
			
			if ( searchIds.length == 0 ) {
				
				return mapping.findForward( StrutsGlobalForwardNames.INVALID_REQUEST_DATA );
			}

			
			//   Get the project id for these searches
			
			Collection<Integer> searchIdsCollection = new HashSet<Integer>( );
			
			for ( int searchId : searchIds ) {

				searchIdsCollection.add( searchId );
			}

			List<Integer> searchIdsListDeduppedSorted = new ArrayList<>( searchIdsCollection );

			Collections.sort( searchIdsListDeduppedSorted );

			
			
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



			

			List<SearchDTO> searches = new ArrayList<SearchDTO>();
			
			Map<Integer, SearchDTO> searchesMapOnId = new HashMap<>();

			for( int searchId : form.getSearchIds() ) {

				SearchDTO search = SearchDAO.getInstance().getSearch( searchId );
				
				if ( search == null ) {
					
					String msg = "search id '" + searchId + "' not found in the database. User taken to home page.";
					
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

				ProteinsMergedCommonPageDownloadResult proteinsMergedCommonPageDownloadResult =
						ProteinsMergedCommonPageDownload.getInstance()
						.getCrosslinksAndLooplinkWrapped(
								form,
								ProteinsMergedCommonPageDownload.ForCrosslinksOrLooplinkOrBoth.BOTH_CROSSLINKS_AND_LOOPLINKS,
								searchIdsListDeduppedSorted,
								searches,
								searchesMapOnId );


				List<MergedSearchProteinCrosslink> crosslinks = proteinsMergedCommonPageDownloadResult.getCrosslinks();
				List<MergedSearchProteinLooplink> looplinks = proteinsMergedCommonPageDownloadResult.getLooplinks();


				// generate file name
				String filename = "udr-list-search-";
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


				writer.write( "PROTEIN 1\tPOSITION\tPROTEIN 2\tPOSITION\tSEARCHES\n" );

				
				Map<Integer, String> proteinNames = new HashMap<Integer, String>();


				// map for naming purposes
				for( MergedSearchProteinCrosslink link : crosslinks ) {
					proteinNames.put( link.getProtein1().getNrProtein().getNrseqId(), link.getProtein1().getName() );
					proteinNames.put( link.getProtein2().getNrProtein().getNrseqId(), link.getProtein2().getName() );
				}
				for( MergedSearchProteinLooplink link : looplinks ) {
					proteinNames.put( link.getProtein().getNrProtein().getNrseqId(), link.getProtein().getName() );
				}

				// get map of all UDRs
				Map<Integer, Map<Integer, Map<Integer, Set<Integer>>>> udrMap = XLinkWebAppUtils.getUDRs( crosslinks, looplinks );
				
				for( int nrseqId1 : udrMap.keySet() ) {
					for( int pos1 : udrMap.get( nrseqId1 ).keySet() ) {
						for( int nrseqId2 : udrMap.get( nrseqId1 ).get( pos1 ).keySet() ) {
							for( int pos2 : udrMap.get( nrseqId1 ).get( pos1 ).get( nrseqId2 ) ) {
								StringBuffer line = new StringBuffer();

								line.append( proteinNames.get( nrseqId1 ) + "\t" );
								line.append( pos1 + "\t" );
								line.append( proteinNames.get( nrseqId2 ) + "\t" );
								line.append( pos2 + "\t" );
								line.append( StringUtils.join( getSearchesForLinks( crosslinks, looplinks, nrseqId1, nrseqId2, pos1, pos2 ), "," ) + "\n" );

								writer.write( line.toString() );
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
	
	/**
	 * 
	 * @param crosslinks
	 * @param looplinks
	 * @param protein1
	 * @param protein2
	 * @param position1
	 * @param position2
	 * @return
	 */
	private Set<Integer> getSearchesForLinks( List<MergedSearchProteinCrosslink> crosslinks, List<MergedSearchProteinLooplink> looplinks,
			int protein1, int protein2, int position1, int position2 ) {
		
		Set<Integer> searchIds = new HashSet<Integer>();
		
		for( MergedSearchProteinCrosslink link : crosslinks ) {
			
			if( ( link.getProtein1().getNrProtein().getNrseqId() == protein1 && link.getProtein2().getNrProtein().getNrseqId() == protein2 && link.getProtein1Position() == position1 && link.getProtein2Position() == position2 ) ||
				( link.getProtein1().getNrProtein().getNrseqId() == protein2 && link.getProtein2().getNrProtein().getNrseqId() == protein1 && link.getProtein1Position() == position2 && link.getProtein2Position() == position1 ) ) {
				
				for( SearchDTO search : link.getSearches() ) {
					searchIds.add( search.getId() );
				}
			}
		}
		
		
		for( MergedSearchProteinLooplink link : looplinks ) {
			
			if( link.getProtein().getNrProtein().getNrseqId() == protein1 && link.getProtein().getNrProtein().getNrseqId() == protein2 && 
				( ( link.getProteinPosition1() == position1 && link.getProteinPosition2() == position2 ) || ( link.getProteinPosition1() == position2 && link.getProteinPosition2() == position1 ) ) ) {
				
				for( SearchDTO search : link.getSearches() ) {
					searchIds.add( search.getId() );
				}
			}
		}
		
		return searchIds;
	}
	
}
