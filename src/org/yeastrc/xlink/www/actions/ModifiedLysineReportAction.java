package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.dao.SearchDAO;
import org.yeastrc.xlink.dto.SearchDTO;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.objects.SearchProtein;
import org.yeastrc.xlink.www.searcher.ProjectIdsForSearchIdsSearcher;
import org.yeastrc.xlink.www.searcher.SearchProteinSearcher;
import org.yeastrc.xlink.utils.ProteinSequenceUtils;
import org.yeastrc.xlink.utils.XLinkUtils;
import org.yeastrc.xlink.www.constants.ServletOutputStreamCharacterSetConstant;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

/**
 * IMPORTANT
 * 
 * This report is only for Lysines.
 * 
 * Only some linkers link to lysines.
 *
 */
public class ModifiedLysineReportAction extends Action {
	
	private static final Logger log = Logger.getLogger(ModifiedLysineReportAction.class);

	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		

		try {
			

			int searchId = Integer.parseInt( request.getParameter( "searchId" ) );


			// Get the session first.  
//			HttpSession session = request.getSession();

			
			//   Get the project id for this search
			
			Collection<Integer> searchIds = new HashSet<>();
			
			searchIds.add( searchId );
			
			List<Integer> projectIdsFromSearchIds = ProjectIdsForSearchIdsSearcher.getInstance().getProjectIdsForSearchIds( searchIds );
			
			if ( projectIdsFromSearchIds.isEmpty() ) {
				
				// should never happen
				
				String msg = "No project ids for search id: " + searchId;
				
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





			double psmQValueCutoff = Double.parseDouble( request.getParameter( "psmQValueCutoff" ) );
			double peptideQValueCutoff = Double.parseDouble( request.getParameter( "peptideQValueCutoff" ) );
			StringBuilder sb = new StringBuilder();

			// first build a collection of types to include
			Collection<Integer> includedTypes = new HashSet<Integer>();
			includedTypes.add( XLinkUtils.TYPE_CROSSLINK );
			includedTypes.add( XLinkUtils.TYPE_DIMER );
			includedTypes.add( XLinkUtils.TYPE_LOOPLINK );
			includedTypes.add( XLinkUtils.TYPE_MONOLINK );
			includedTypes.add( XLinkUtils.TYPE_UNLINKED );

			SearchDTO search = SearchDAO.getInstance().getSearch( searchId );

			
			if ( search == null ) {
				
				String msg = "Percolator search id '" + searchId + "' not found in the database. User taken to home page.";
				
				log.warn( msg );
				
				//  Search not found, the data on the page they are requesting does not exist.
				//  The data on the user's previous page no longer reflects what is in the database.
				//  Take the user to the home page
				
				return mapping.findForward( StrutsGlobalForwardNames.HOME );  //  EARLY EXIT from Method
			}
			
			// build a collection of the potentially included proteins
			Collection<SearchProtein> proteins = SearchProteinSearcher.getInstance().getProteinsWithLinkType(search, includedTypes, psmQValueCutoff, peptideQValueCutoff);

			// add locations of all lysines in the found proteins
			Map<Integer, Collection<Integer>> lysineLocations = new HashMap<Integer, Collection<Integer>>();
			for( SearchProtein mp : proteins ) {
				lysineLocations.put( mp.getNrProtein().getNrseqId(), ProteinSequenceUtils.getPositionsOfResidueForProteinId( mp.getNrProtein().getNrseqId(), "K" ) );
			}

			for( SearchProtein p : proteins ) {
				for( int ll : lysineLocations.get( p.getNrProtein().getNrseqId() ) ) {
					sb.append( p.getName() );
					sb.append( "\t" );

					sb.append( ll );
					sb.append( "\t" );

					// test crosslink
					if( SearchProteinSearcher.getInstance().isLinked( p.getNrProtein().getNrseqId(), ll, search, XLinkUtils.TYPE_CROSSLINK, psmQValueCutoff, peptideQValueCutoff) ) {
						sb.append( "C\t" );
					} else {
						sb.append( "\t" );
					}

					// test looplink
					if( SearchProteinSearcher.getInstance().isLinked( p.getNrProtein().getNrseqId(), ll, search, XLinkUtils.TYPE_LOOPLINK, psmQValueCutoff, peptideQValueCutoff) ) {
						sb.append( "L\t" );
					} else {
						sb.append( "\t" );
					}

					// test monolink
					if( SearchProteinSearcher.getInstance().isLinked( p.getNrProtein().getNrseqId(), ll, search, XLinkUtils.TYPE_MONOLINK, psmQValueCutoff, peptideQValueCutoff) ) {
						sb.append( "M\n" );
					} else {
						sb.append( "\n" );
					}
				}
			}


			OutputStreamWriter writer = null;
			
			try {


				ServletOutputStream out = response.getOutputStream();

				BufferedOutputStream bos = new BufferedOutputStream(out);

				writer = new OutputStreamWriter( bos , ServletOutputStreamCharacterSetConstant.outputStreamCharacterSet );


				writer.write( sb.toString() );

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
