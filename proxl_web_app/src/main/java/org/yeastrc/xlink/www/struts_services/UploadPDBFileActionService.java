package org.yeastrc.xlink.www.struts_services;

import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.yeastrc.xlink.www.constants.PDBFileConstants;
import org.yeastrc.xlink.www.constants.WebConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.PDBFileUploadDAO;
import org.yeastrc.xlink.www.forms.UploadPDBFileForm;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.GetAuthAccessLevelForWebRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 *   Accepts uploaded PDB file and fields from Javascript on the page
 *
 */
public class UploadPDBFileActionService extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( UploadPDBFileActionService.class);
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward execute( ActionMapping mapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response )
					throws Exception {
		// Get the session first.  
		HttpSession session = request.getSession();
		UploadPDBFileForm form = null;
		try {
			form = (UploadPDBFileForm)actionForm;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			response.setStatus( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE.getStatusCode() /* 500  */ );
			response.setContentType( "text" );
			response.getWriter().print( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT );
			return null; //  Early return
		}
		int projectId = form.getProjectId();
		if( projectId == 0 ) {
			response.setStatus( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode() );
			response.setContentType( "text" );
			response.getWriter().print( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
			return null; //  Early return
		}
		int authUserId = 0;
		try {
			UserSessionObject userSessionObject = (UserSessionObject) session.getAttribute( WebConstants.SESSION_CONTEXT_USER_LOGGED_IN );
			if ( userSessionObject == null ) {
				response.setStatus( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE.getStatusCode() );
				response.setContentType( "text" );
				response.getWriter().print( WebServiceErrorMessageConstants.NO_SESSION_TEXT );
				return null; //  Early return
			}
			AuthAccessLevel authAccessLevel = GetAuthAccessLevelForWebRequest.getInstance().getAuthAccessLevelForWebRequestProjectId( userSessionObject, projectId );
			if ( ! authAccessLevel.isWriteAllowed() ) {
				response.setStatus( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE.getStatusCode() );
				response.setContentType( "text" );
				response.getWriter().print( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT );
				return null; //  Early return
			}
			authUserId = userSessionObject.getUserDBObject().getAuthUser().getId();
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			response.setStatus( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE.getStatusCode() /* 500  */ );
			response.setContentType( "text" );
			response.getWriter().print( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT );
			return null; //  Early return
		}
		Structure structure = null;
		try {
			PDBFileReader pdbReader = new PDBFileReader();
			structure = pdbReader.getStructure( form.getFile().getInputStream() );
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			UploadPDBFileActionServiceResponse importFASTAServletResponse = new UploadPDBFileActionServiceResponse();
			importFASTAServletResponse.setStatusSuccess(false);
			importFASTAServletResponse.setParsePDBFailed(true);
			OutputStream responseOutputStream = response.getOutputStream();
			// send the JSON response 
			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
			mapper.writeValue( responseOutputStream, importFASTAServletResponse ); // where first param can be File, OutputStream or Writer
			responseOutputStream.flush();
			responseOutputStream.close();
			return null; //  Early return
		}
		try {
			// ensure we find at least one chain
			if( structure.getChains().size() < 1 ) {
//				response.setContentType( "text" );
//				response.getWriter().print( "Error: No Chains" );
				UploadPDBFileActionServiceResponse importFASTAServletResponse = new UploadPDBFileActionServiceResponse();
				importFASTAServletResponse.setStatusSuccess(false);
				importFASTAServletResponse.setNoChains(true);
				OutputStream responseOutputStream = response.getOutputStream();
				// send the JSON response 
				ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
				mapper.writeValue( responseOutputStream, importFASTAServletResponse ); // where first param can be File, OutputStream or Writer
				responseOutputStream.flush();
				responseOutputStream.close();
				return null; //  Early return
			}
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			response.setStatus( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE.getStatusCode() /* 500  */ );
			response.setContentType( "text" );
			response.getWriter().print( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT );
			return null; //  Early return
		}
		try {
			// save PDB file to the database
			PDBFileUploadDAO.getInstance().savePDBFile( form.getFile(), 
					form.getDescription(),
					authUserId,
					projectId,
					// WAS
//					form.getVisibility()
					PDBFileConstants.VISIBILITY_PROJECT
					);
			UploadPDBFileActionServiceResponse importFASTAServletResponse = new UploadPDBFileActionServiceResponse();
			importFASTAServletResponse.setStatusSuccess(true);
			OutputStream responseOutputStream = response.getOutputStream();
			// send the JSON response 
			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
			mapper.writeValue( responseOutputStream, importFASTAServletResponse ); // where first param can be File, OutputStream or Writer
			responseOutputStream.flush();
			responseOutputStream.close();
			return null;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			response.setStatus( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE.getStatusCode() /* 500  */ );
			response.setContentType( "text" );
			response.getWriter().print( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT );
			return null; //  Early return
		}
	}
	
	/**
	 * 
	 *
	 */
	public static class UploadPDBFileActionServiceResponse {
		private boolean statusSuccess;
		private boolean noChains;
		private boolean parsePDBFailed;
		public boolean isStatusSuccess() {
			return statusSuccess;
		}
		public void setStatusSuccess(boolean statusSuccess) {
			this.statusSuccess = statusSuccess;
		}
		public boolean isNoChains() {
			return noChains;
		}
		public void setNoChains(boolean noChains) {
			this.noChains = noChains;
		}
		public boolean isParsePDBFailed() {
			return parsePDBFailed;
		}
		public void setParsePDBFailed(boolean parsePDBFailed) {
			this.parsePDBFailed = parsePDBFailed;
		}
	}
}