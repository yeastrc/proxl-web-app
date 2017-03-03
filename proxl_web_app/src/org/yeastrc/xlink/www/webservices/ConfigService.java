package org.yeastrc.xlink.www.webservices;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.auth.dto.AuthUserDTO;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsKeysSharedConstants;
import org.yeastrc.xlink.base.config_system_table_common_access.ConfigSystemsValuesSharedConstants;
import org.yeastrc.xlink.www.objects.AuthAccessLevel;
import org.yeastrc.xlink.www.searcher_via_cached_data.cached_data_holders.Cached_ReportedPeptideBasicObjectsSearcher_Results;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.UserSignupConstants;
import org.yeastrc.xlink.www.constants.WebServiceErrorMessageConstants;
import org.yeastrc.xlink.www.dao.ConfigSystemDAO;
import org.yeastrc.xlink.www.dao.TermsOfServiceTextVersionsDAO;
import org.yeastrc.xlink.www.dto.ConfigSystemDTO;
import org.yeastrc.xlink.www.dto.TermsOfServiceTextVersionsDTO;
import org.yeastrc.xlink.www.dto.XLinkUserDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.exceptions.ProxlWebappInternalErrorException;
import org.yeastrc.xlink.www.user_account.UserSessionObject;
import org.yeastrc.xlink.www.user_web_utils.AccessAndSetupWebSessionResult;
import org.yeastrc.xlink.www.user_web_utils.GetAccessAndSetupWebSession;
import org.yeastrc.xlink.www.web_utils.IsTermsOfServiceEnabled;
import com.google.common.io.BaseEncoding;
import com.google.common.primitives.Longs;

@Path("/config")
public class ConfigService {
	
	private static final Logger log = Logger.getLogger(ConfigService.class);
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/list") 
	public ConfigListResult getViewerData( @Context HttpServletRequest request )
	throws Exception {
		try {
			//  Restricted to users with ACCESS_LEVEL_ADMIN or better
			// Get the session first.  
//			HttpSession session = request.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			//  Test access at global level
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isAdminAllowed() ) {
				//  No Access Allowed 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			////////   Auth complete
			//////////////////////////////////////////
			
			ConfigListResult configListResult = new ConfigListResult();
			List<ConfigSystemDTO> configList = ConfigSystemDAO.getInstance().getAll();
			configListResult.setConfigList( configList );
			return configListResult;
			
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	
	/**
	 * 
	 *
	 */
	private static class ConfigListResult {
		private List<ConfigSystemDTO> configList;
		@SuppressWarnings("unused")
		public List<ConfigSystemDTO> getConfigList() {
			return configList;
		}
		public void setConfigList(List<ConfigSystemDTO> configList) {
			this.configList = configList;
		}
	}
	
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/save")
	public SaveResult save( SaveRequest saveRequest,
			@Context HttpServletRequest request ) throws Exception {
		try {
			//  Restricted to users with ACCESS_LEVEL_ADMIN or better
			// Get the session first.  
//			HttpSession session = request.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			//  Test access at global level
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isAdminAllowed() ) {
				//  No Access Allowed 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			////////   Auth complete
			//////////////////////////////////////////
			
			List<ConfigSystemDTO> configList = saveRequest.getConfigList();
			///  Validate config keys and values (from checkboxes) are valid
			for ( ConfigSystemDTO item : configList ) {
				if ( ( ! ConfigSystemsKeysConstants.textConfigKeys.contains( item.getConfigKey() ) )
						&& ( ! ConfigSystemsKeysSharedConstants.textConfigKeys.contains( item.getConfigKey() ) ) ) {
					
					if ( ConfigSystemsKeysConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY.equals( item.getConfigKey() ) ) {
						//  Not one of the text config keys so validate the config keys with specific values
						if ( ( ! UserSignupConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY__TRUE.equals( item.getConfigValue() ) )
								&& ( ! UserSignupConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY__FALSE.equals( item.getConfigValue() ) ) ) {
							//  Invalid value for config key found
							String msg = "Invalid value for config key: " + item.getConfigKey();
							log.error( msg );
							throw new WebApplicationException(
									Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
									.entity( msg )
									.build()
									);		
						}
					} else if ( ConfigSystemsKeysSharedConstants.IMPORT_DELETE_UPLOADED_FILES.equals( item.getConfigKey() ) ) {
						//  Not one of the text config keys so validate the config keys with specific values
						if ( ( ! ConfigSystemsValuesSharedConstants.TRUE.equals( item.getConfigValue() ) )
								&& ( ! ConfigSystemsValuesSharedConstants.FALSE.equals( item.getConfigValue() ) ) ) {
							//  Invalid value for config key found
							String msg = "Invalid value for config key: " + item.getConfigKey();
							log.error( msg );
							throw new WebApplicationException(
									Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
									.entity( msg )
									.build()
									);		
						}
					} else {
						//  Invalid config key found
						String msg = "Invalid config key: " + item.getConfigKey();
						log.error( msg );
						throw new WebApplicationException(
								Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
								.entity( msg )
								.build()
								);		
					}
				}
			}
			
			//  Save values to DB
			ConfigSystemDAO.getInstance().updateValueOnlyOnConfigKey( configList );
			//  Clear the cached values
			ConfigSystemCaching.getInstance().clearCache();
			//  Rebuild cache since config comes from config table
			Cached_ReportedPeptideBasicObjectsSearcher_Results.getInstance().clearCache();
			SaveResult saveResult = new SaveResult();
			return saveResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
					.entity( msg )
					.build()
					);			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	/**
	 * 
	 *
	 */
	private static class SaveRequest {
		private List<ConfigSystemDTO> configList;
		public List<ConfigSystemDTO> getConfigList() {
			return configList;
		}
		@SuppressWarnings("unused")
		public void setConfigList(List<ConfigSystemDTO> configList) {
			this.configList = configList;
		}
	}
	/**
	 * 
	 *
	 */
	private static class SaveResult {
		private String status;
		@SuppressWarnings("unused")
		public String getStatus() {
			return status;
		}
		@SuppressWarnings("unused")
		public void setStatus(String status) {
			this.status = status;
		}
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/termsOfServiceData") 
	public GetTermsOfServiceDataResult getTermsOfServiceData( 
										  @Context HttpServletRequest request )
	throws Exception {
		try {
			//  Restricted to users with ACCESS_LEVEL_ADMIN or better
			// Get the session first.  
//			HttpSession session = request.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			//  Test access at global level
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isAdminAllowed() ) {
				//  No Access Allowed 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			////////   Auth complete
			//////////////////////////////////////////
			GetTermsOfServiceDataResult getTermsOfServiceDataResult = new GetTermsOfServiceDataResult();
			boolean termsOfServiceEnabled = IsTermsOfServiceEnabled.getInstance().isTermsOfServiceEnabled();
			String termsOfServiceText = null;
			TermsOfServiceTextVersionsDTO termsOfServiceTextVersionsDTO = 
					TermsOfServiceTextVersionsDAO.getInstance().getLatest();
			if ( termsOfServiceTextVersionsDTO != null ) {
				termsOfServiceText = termsOfServiceTextVersionsDTO.getTermsOfServiceText();
			}
			getTermsOfServiceDataResult.termsOfServiceEnabled = termsOfServiceEnabled;
			getTermsOfServiceDataResult.termsOfServiceText = termsOfServiceText;
			return getTermsOfServiceDataResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( ProxlWebappDataException e ) {
			String msg = "Exception processing request data, msg: " + e.toString();
			log.error( msg, e );
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
		    	        .entity( msg )
		    	        .build()
		    	        );			
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw new WebApplicationException(
					Response.status( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_STATUS_CODE )  //  Send HTTP code
					.entity( WebServiceErrorMessageConstants.INTERNAL_SERVER_ERROR_TEXT ) // This string will be passed to the client
					.build()
					);
		}
	}
	/**
	 * 
	 *
	 */
	private static class GetTermsOfServiceDataResult {
		private boolean termsOfServiceEnabled;
		private String termsOfServiceText;
		@SuppressWarnings("unused")
		public boolean isTermsOfServiceEnabled() {
			return termsOfServiceEnabled;
		}
		@SuppressWarnings("unused")
		public void setTermsOfServiceEnabled(boolean termsOfServiceEnabled) {
			this.termsOfServiceEnabled = termsOfServiceEnabled;
		}
		@SuppressWarnings("unused")
		public String getTermsOfServiceText() {
			return termsOfServiceText;
		}
		@SuppressWarnings("unused")
		public void setTermsOfServiceText(String termsOfServiceText) {
			this.termsOfServiceText = termsOfServiceText;
		}
	}
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/addTermsOfService")
	public AddTermsOfServiceResult addTermsOfService(
			@FormParam("termsOfServiceText") String termsOfServiceText, 
			@Context HttpServletRequest request ) throws Exception {
		AddTermsOfServiceResult webserviceResult = new AddTermsOfServiceResult();
		try {
			if ( StringUtils.isEmpty(termsOfServiceText) ) {
				String msg = "Provided termsOfServiceText is empty, is = " + termsOfServiceText;
				log.error( msg );
			    throw new WebApplicationException(
			    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
			    	        .entity( msg )
			    	        .build()
			    	        );
			}
			//  Restricted to users with ACCESS_LEVEL_ADMIN or better
			// Get the session first.  
//			HttpSession session = request.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			//  Test access at global level
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isAdminAllowed() ) {
				//  No Access Allowed 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			////////   Auth complete
			//////////////////////////////////////////
			//  Get auth user id
			XLinkUserDTO userDBObject = userSessionObject.getUserDBObject();
			if ( userDBObject == null ) {
				String msg = "authAccessLevel.isAdminAllowed() but no XLinkUserDTO object on session object";
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			AuthUserDTO authUserDTO = userDBObject.getAuthUser();
			if ( authUserDTO == null ) {
				String msg = "authAccessLevel.isAdminAllowed() but no authUserDTO object on session object";
				log.error( msg );
				throw new ProxlWebappInternalErrorException(msg);
			}
			//  Create a new random key for the terms of service record
			//  Generate a random string
			final int TOS_KEY_LENGTH = 50; // Must match DB field
			StringBuilder tosKeySB = new StringBuilder( 200 );
			while ( tosKeySB.length() < TOS_KEY_LENGTH ) {
				double tosKeyMultiplier = Math.random();
				if ( tosKeyMultiplier < 0.5 ) {
					tosKeyMultiplier += 0.5;
				}
				long tosKeyLong = (long) ( System.currentTimeMillis() * tosKeyMultiplier );
				// Google Guava classes BaseEncoding and Longs
				tosKeySB.append( BaseEncoding.base64().encode( Longs.toByteArray(tosKeyLong) ) );
			}
			String tosKey = tosKeySB.toString().substring(0, TOS_KEY_LENGTH);
			//   Save the terms of service as a new record
			TermsOfServiceTextVersionsDTO termsOfServiceTextVersionsDTO = new TermsOfServiceTextVersionsDTO();
			termsOfServiceTextVersionsDTO.setIdString( tosKey );
			termsOfServiceTextVersionsDTO.setTermsOfServiceText( termsOfServiceText );
			termsOfServiceTextVersionsDTO.setCreatedAuthUserId( authUserDTO.getId() );
			TermsOfServiceTextVersionsDAO.getInstance().save( termsOfServiceTextVersionsDTO );
			//   Change config record for TERMS_OF_SERVICE_ENABLED to true
			ConfigSystemDTO configSystemDTO = new ConfigSystemDTO();
			configSystemDTO.setConfigKey( ConfigSystemsKeysSharedConstants.TERMS_OF_SERVICE_ENABLED );
			configSystemDTO.setConfigValue( ConfigSystemsValuesSharedConstants.TRUE );
			List<ConfigSystemDTO> configList = new ArrayList<>();
			configList.add(configSystemDTO);
			ConfigSystemDAO.getInstance().updateValueOnlyOnConfigKey( configList );
			//  Clear the cached values
			ConfigSystemCaching.getInstance().clearCache();
			webserviceResult.status = true;
			return webserviceResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	/**
	 * 
	 *
	 */
	private static class AddTermsOfServiceResult {
		private boolean status;
		@SuppressWarnings("unused")
		public boolean getStatus() {
			return status;
		}
		@SuppressWarnings("unused")
		public void setStatus(boolean status) {
			this.status = status;
		}
	}
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/disableTermsOfService")
	public DisableTermsOfServiceResult disableTermsOfService(
			@Context HttpServletRequest request ) throws Exception {
		DisableTermsOfServiceResult webserviceResult = new DisableTermsOfServiceResult();
		try {
			//  Restricted to users with ACCESS_LEVEL_ADMIN or better
			// Get the session first.  
//			HttpSession session = request.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			//  Test access at global level
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isAdminAllowed() ) {
				//  No Access Allowed 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			////////   Auth complete
			//////////////////////////////////////////
			ConfigSystemDTO configSystemDTO = new ConfigSystemDTO();
			configSystemDTO.setConfigKey( ConfigSystemsKeysSharedConstants.TERMS_OF_SERVICE_ENABLED );
			configSystemDTO.setConfigValue( ConfigSystemsValuesSharedConstants.FALSE );
			List<ConfigSystemDTO> configList = new ArrayList<>();
			configList.add(configSystemDTO);
			ConfigSystemDAO.getInstance().updateValueOnlyOnConfigKey( configList );
			//  Clear the cached values
			ConfigSystemCaching.getInstance().clearCache();
			webserviceResult.status = true;
			return webserviceResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	/**
	 * 
	 *
	 */
	private static class DisableTermsOfServiceResult {
		private boolean status;
		@SuppressWarnings("unused")
		public boolean getStatus() {
			return status;
		}
		@SuppressWarnings("unused")
		public void setStatus(boolean status) {
			this.status = status;
		}
	}
	@POST
	@Consumes( MediaType.APPLICATION_FORM_URLENCODED )
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/enableTermsOfService")
	public EnableTermsOfServiceResult enableTermsOfService(
			@Context HttpServletRequest request ) throws Exception {
		EnableTermsOfServiceResult webserviceResult = new EnableTermsOfServiceResult();
		try {
			//  Restricted to users with ACCESS_LEVEL_ADMIN or better
			// Get the session first.  
//			HttpSession session = request.getSession();
			AccessAndSetupWebSessionResult accessAndSetupWebSessionResult =
					GetAccessAndSetupWebSession.getInstance().getAccessAndSetupWebSessionNoProjectId( request );
			if ( accessAndSetupWebSessionResult.isNoSession() ) {
				//  No User session 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NO_SESSION_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NO_SESSION_TEXT ) // This string will be passed to the client
						.build()
						);
			}
//			UserSessionObject userSessionObject = accessAndSetupWebSessionResult.getUserSessionObject();
			//  Test access at global level
			AuthAccessLevel authAccessLevel = accessAndSetupWebSessionResult.getAuthAccessLevel();
			if ( ! authAccessLevel.isAdminAllowed() ) {
				//  No Access Allowed 
				throw new WebApplicationException(
						Response.status( WebServiceErrorMessageConstants.NOT_AUTHORIZED_STATUS_CODE )  //  Send HTTP code
						.entity( WebServiceErrorMessageConstants.NOT_AUTHORIZED_TEXT ) // This string will be passed to the client
						.build()
						);
			}
			////////   Auth complete
			//////////////////////////////////////////
			ConfigSystemDTO configSystemDTO = new ConfigSystemDTO();
			configSystemDTO.setConfigKey( ConfigSystemsKeysSharedConstants.TERMS_OF_SERVICE_ENABLED );
			configSystemDTO.setConfigValue( ConfigSystemsValuesSharedConstants.TRUE );
			List<ConfigSystemDTO> configList = new ArrayList<>();
			configList.add(configSystemDTO);
			ConfigSystemDAO.getInstance().updateValueOnlyOnConfigKey( configList );
			//  Clear the cached values
			ConfigSystemCaching.getInstance().clearCache();
			webserviceResult.status = true;
			return webserviceResult;
		} catch ( WebApplicationException e ) {
			throw e;
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
	
	/**
	 * 
	 *
	 */
	private static class EnableTermsOfServiceResult {
		private boolean status;
		@SuppressWarnings("unused")
		public boolean getStatus() {
			return status;
		}
		@SuppressWarnings("unused")
		public void setStatus(boolean status) {
			this.status = status;
		}
	}
}
