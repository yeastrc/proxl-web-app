package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.dao.SearchFileDAO;
import org.yeastrc.xlink.dao.SearchFileProjectSearchDAO;
import org.yeastrc.xlink.dto.SearchFileDTO;
import org.yeastrc.xlink.dto.SearchFileProjectSearchDTO;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.searcher.ProjectIdsForProjectSearchIdsSearcher;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;

/**
 * 
 *
 */
public class DownloadSearchFileAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( DownloadSearchFileAction.class);
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response )
					  throws Exception {
		try {
			String fileIdString = request.getParameter( "fileId" );
			if ( StringUtils.isEmpty(fileIdString) ) {
				String msg = "'fileId' is empty: fileId: " + fileIdString;
				log.error( msg );
				throw new Exception(msg);
			}
			int fileId = 0;
			try {
				fileId = Integer.parseInt( fileIdString );
			} catch( Exception e ) {
				String msg = "'fileId' not parsable to int: fileId: " + fileIdString;
				log.error( msg );
				throw e;
			}
			// Get the session first.  
//			HttpSession session = request.getSession();
			
			SearchFileProjectSearchDTO searchFileProjectSearchDTO =
					SearchFileProjectSearchDAO.getInstance().getSearchFileProjectSearchDTOForId( fileId );
			if ( searchFileProjectSearchDTO == null ) {
				String msg = " no searchFileProjectSearchDTO record found for 'fileId': fileId: " + fileId;
				log.error( msg );
				throw new ProxlWebappDataException( msg );
			}
			int projectSearchId = searchFileProjectSearchDTO.getProjectSearchId();
			
			//  Auth Check
			
			//   Get the project id for this search
			Collection<Integer> projectSearchIdsSet = new HashSet<>();
			projectSearchIdsSet.add( projectSearchId );
			List<Integer> projectIdsFromSearchIds = ProjectIdsForProjectSearchIdsSearcher.getInstance().getProjectIdsForProjectSearchIds( projectSearchIdsSet );
			if ( projectIdsFromSearchIds.isEmpty() ) {
				// should never happen
				String msg = "No project ids for search id: " + projectSearchId;
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
			
			SearchFileDTO  searchFileDTO = 
					SearchFileDAO.getInstance().getSearchFileDTOForId( searchFileProjectSearchDTO.getSearchFileId()  );

			if ( searchFileDTO == null ) {
				String msg = " no searchFileDTO record found for SearchFileId: " + searchFileProjectSearchDTO.getSearchFileId() ;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			
			byte[] fileContents = SearchFileDAO.getInstance().getDataFileData( searchFileProjectSearchDTO.getSearchFileId() );
			int fileSize = 0;
			if ( fileContents != null ) {
				fileSize = fileContents.length;
			}
			// generate file name
			String filename = searchFileProjectSearchDTO.getDisplayFilename();
			response.setContentType( searchFileDTO.getMimeType() );
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.setContentLength( fileSize );
			BufferedOutputStream bos = null;
			try {
				ServletOutputStream out = response.getOutputStream();
				bos = new BufferedOutputStream(out);
				out.write( fileContents );
			} finally {
				try {
					if ( bos != null ) {
						bos.close();
					}
				} catch ( Exception ex ) {
					log.error( "bos.close():Exception " + ex.toString(), ex );
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
