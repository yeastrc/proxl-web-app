package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.dao.PDBFileDAO;
import org.yeastrc.xlink.www.dto.PDBFileDTO;
import org.yeastrc.xlink.www.constants.PDBFileConstants;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;

/**
 * 
 *
 */
public class DownloadPDBFileAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( DownloadPDBFileAction.class);
	
	@Override
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		try {
			String fileIdString = request.getParameter( "id" );
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
			PDBFileDTO pdb = PDBFileDAO.getInstance().getPDBFile( fileId );
			if ( pdb == null ) {
				String msg = " no pdb record found for 'fileId': fileId: " + fileId;
				log.error( msg );
				throw new Exception(msg);
			}
			if ( PDBFileConstants.VISIBILITY_PUBLIC.equals( pdb.getVisibility() ) ) {
			} else if ( PDBFileConstants.VISIBILITY_PROJECT.equals( pdb.getVisibility() ) ) {
				// pdb file restricted to this project
				int projectId = pdb.getProjectId();
				GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result accessAndSetupWebSessionResult =
						GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance().getAccessAndSetupWebSessionWithProjectId( projectId, request );
				if ( accessAndSetupWebSessionResult.isNoSession() ) {
					//  No User session 
					return mapping.findForward( StrutsGlobalForwardNames.NO_USER_SESSION );
				}
				//  Test access to the project id
				WebSessionAuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getWebSessionAuthAccessLevel();
				//  Test access to the project id, admin users are also allowed
				if ( ! authAccessLevel.isPublicAccessCodeReadAllowed() ) {
					//  No Access Allowed for this project id
					return mapping.findForward( StrutsGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE );
				}
				request.setAttribute( WebConstants.REQUEST_AUTH_ACCESS_LEVEL, authAccessLevel );
			} else {
				String msg = "Unknown value for visibility: " + pdb.getVisibility();
				log.error( msg );
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			
			byte[] fileContents = pdb.getContent().getBytes();
			// generate file name
			String filename = pdb.getName();
			response.setContentType( "chemical/x-pdb" );
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.setContentLength( fileContents.length );
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
