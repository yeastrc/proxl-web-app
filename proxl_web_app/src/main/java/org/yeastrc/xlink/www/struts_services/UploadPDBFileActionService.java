package org.yeastrc.xlink.www.struts_services;

import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.yeastrc.xlink.www.constants.PDBFileConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.PDBFileUploadDAO;
import org.yeastrc.xlink.www.forms.UploadPDBFileForm;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.user_session_management.UserSession;
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
	@Override
	public ActionForward execute( ActionMapping mapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response )
					throws Exception {
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
			GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result =
					GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.getSinglesonInstance()
					.getAccessAndSetupWebSessionWithProjectId( projectId, request );
			WebSessionAuthAccessLevel authAccessLevel = getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result.getWebSessionAuthAccessLevel();
			if ( getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result.isNoSession() ) {
				response.setStatus( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE.getStatusCode() );
				response.setContentType( "text" );
				response.getWriter().print( WebServiceErrorMessageConstants.NO_SESSION_TEXT );
				return null; //  Early return
			}
			if ( ! authAccessLevel.isWriteAllowed() ) {
				response.setStatus( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE.getStatusCode() );
				response.setContentType( "text" );
				response.getWriter().print( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT );
				return null; //  Early return
			}
			UserSession userSession = getWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result.getUserSession();
			authUserId = userSession.getAuthUserId();
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