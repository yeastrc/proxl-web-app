package org.yeastrc.xlink.www.struts_services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.io.PDBFileReader;
import org.yeastrc.xlink.www.constants.PDBFileConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.PDBFileUploadDAO;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId;
import org.yeastrc.xlink.www.access_control.access_control_main.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId.GetWebSessionAuthAccessLevelForProjectIds_And_NO_ProjectId_Result;
import org.yeastrc.xlink.www.access_control.result_objects.WebSessionAuthAccessLevel;
import org.yeastrc.xlink.www.user_session_management.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 *   Accepts uploaded PDB file and fields from Javascript on the page
 *   
 *   Contents of PDB file sent as POST body
 *   
 *   Fields sent in POST Header
 *
 */
public class UploadPDBFileActionService extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( UploadPDBFileActionService.class);

	//  Keep values in sync with the Javascript file: structure-viewer-pdb-upload.js
	private static final int MAX_PDB_FILESIZE_IN_MB = 200; 
	private static final int MAX_PDB_FILESIZE = MAX_PDB_FILESIZE_IN_MB * 1000 * 1000; // Must be smaller than max int ~ 2GB

	//  HTTP Header Key Strings:
	//  Keep all these Strings in sync with the Javascript file: structure-viewer-pdb-upload.js
	
	public static final String UPLOAD_FILE_HEADER_NAME_PROJECT_ID = "X-Proxl-project_id";
	public static final String UPLOAD_FILE_HEADER_NAME_FILENAME = "X-Proxl-filename";
	public static final String UPLOAD_FILE_HEADER_NAME_USER_DESCRIPTION = "X-Proxl-description";
	
	//  Contents of PDB file sent as POST body
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public ActionForward execute( ActionMapping mapping,
			ActionForm actionForm,
			HttpServletRequest request,
			HttpServletResponse response )
					throws Exception {

		String projectIdString = request.getHeader( UPLOAD_FILE_HEADER_NAME_PROJECT_ID );
		String pdbFilename = request.getHeader( UPLOAD_FILE_HEADER_NAME_FILENAME );
		String userDescription = request.getHeader( UPLOAD_FILE_HEADER_NAME_USER_DESCRIPTION );
		

		long postContentLengthAsLong = request.getContentLengthLong();
		
		if ( postContentLengthAsLong > Integer.MAX_VALUE || postContentLengthAsLong > MAX_PDB_FILESIZE ) {
			log.warn( "POST body content length is  > Integer.MAX_VALUE || is > " 
					+ MAX_PDB_FILESIZE 
					+ ".  "
					+ "Remote IP address: " + request.getRemoteAddr() );
			response.setStatus( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode() );
			response.setContentType( "text" );
			response.getWriter().print( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
			return null; //  Early return
		}
		
		int postContentLengthAsInt = (int) postContentLengthAsLong;

		if ( StringUtils.isEmpty( pdbFilename ) ) {
			response.setStatus( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode() );
			response.setContentType( "text" );
			response.getWriter().print( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
			return null; //  Early return
		}
		
		if ( StringUtils.isEmpty( projectIdString ) ) {
			response.setStatus( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode() );
			response.setContentType( "text" );
			response.getWriter().print( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
			return null; //  Early return
		}
		
		
		int projectId = 0;
		
		try { 
			projectId = Integer.parseInt( projectIdString );
		} catch ( Exception e ) {
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
		
		//  Copy PDB file from input stream (POST body) to Byte Array

		byte[] pdbFileContents = new byte[ postContentLengthAsInt ];
		{
			try ( InputStream inputStreamFromPOSTLocal = request.getInputStream() ) {

				int lengthRead = -1;
				int offset = 0; // start at beginning
				int lengthCanRead = postContentLengthAsInt; // Start at all of buffer
				int totalRead = 0;
				while ( ( lengthCanRead != 0 )  
						&& ( lengthRead = inputStreamFromPOSTLocal.read( pdbFileContents, offset, lengthCanRead ) ) != -1 ){
					offset += lengthRead;
					lengthCanRead -= lengthRead;
					totalRead += lengthRead;
				}
				if ( totalRead != postContentLengthAsInt ) {
					//  Number bytes read != postContentLengthAsInt  
					log.warn( "Number of bytes read != size of content (per header).. Returning error. Number of bytes read: " 
							+ totalRead + ", postContentLengthAsInt: " + postContentLengthAsInt );
					response.setStatus( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode() );
					response.setContentType( "text" );
					response.getWriter().print( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
					return null; //  Early return
				}
				byte[] extraBytes = new byte[ 10 ];
				int extraBytesCount = inputStreamFromPOSTLocal.read( extraBytes );
				if ( extraBytesCount != -1 ) {
					//  Not at end of POST body
					log.warn( "Number of bytes read != size of content (per header).. Returning error. postContentLength: "
							+ postContentLengthAsInt
							+ ", read at least " + extraBytesCount + " bytes after postContentLength" );
					response.setStatus( WebServiceErrorMessageConstants.INVALID_PARAMETER_STATUS_CODE.getStatusCode() );
					response.setContentType( "text" );
					response.getWriter().print( WebServiceErrorMessageConstants.INVALID_PARAMETER_TEXT );
					return null; //  Early return
				}
			} catch ( Exception e ) {
				String msg = "Fail reading PDB file from Stream: " + e.toString();
				log.error( msg, e );
			}
		}
		
		ByteArrayInputStream pdbFileContents_BIOS = new ByteArrayInputStream( pdbFileContents );
		
		
		Structure structure = null;
		try {
			PDBFileReader pdbReader = new PDBFileReader();
			structure = pdbReader.getStructure( pdbFileContents_BIOS );
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
			PDBFileUploadDAO.getInstance().savePDBFile( pdbFileContents, 
					pdbFilename,
					userDescription,
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