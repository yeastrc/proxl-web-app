package org.yeastrc.xlink.www.spring_controllers;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.config_system_table.ConfigSystemCaching;
import org.yeastrc.xlink.www.constants.ConfigSystemsKeysConstants;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.constants.UserSignupConstants;
import org.yeastrc.xlink.www.user_session_management.UserSessionManager;
import org.yeastrc.xlink.www.user_account.UserInsufficientAccessPrivilegePageInitAction;
import org.yeastrc.xlink.www.user_account.UserDisabledPageInitAction;
import org.yeastrc.xlink.www.user_account.UserResetPasswordProcessCodeAction;
import org.yeastrc.xlink.www.user_account.UserSignupPageInitAction;
import org.yeastrc.xlink.www.user_account.UserInviteProcessCodeAction;
import org.yeastrc.xlink.www.user_account.UserInviteLandingPageInitAction;
import org.yeastrc.xlink.www.user_account.UserInviteCreateNewUserInitPageAction;
import org.yeastrc.xlink.www.user_account.AccountPageInitAction;
import org.yeastrc.xlink.www.user_account.ManageUsersPageInitAction;

/**
 * Spring MVC controller for user-account page actions.
 */
@Controller
public class UserAccountController {

	private static final Logger log = LoggerFactory.getLogger( UserAccountController.class );

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_userLoginPage )
	public String userLoginPage( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		try {
			String userSignupAllowWithoutInviteConfigValue =
					ConfigSystemCaching.getInstance()
					.getConfigValueForConfigKey( ConfigSystemsKeysConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY );

			if ( UserSignupConstants.USER_SIGNUP_ALLOW_WITHOUT_INVITE_KEY__TRUE.equals( userSignupAllowWithoutInviteConfigValue ) ) {

				request.setAttribute( "userSignupAllowWithoutInvite", true );
			}

			return "user_account/login";

		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_userLogout )
	public String userLogout( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		try {
			UserSessionManager.getSinglesonInstance().invalidateUserSession( request );

			//  Struts forward Success -> /listProjects.do  redirect="true"
			return "redirect:/listProjects.do";

		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}

	//  ====== Forward-only actions (no action class; just render a JSP) ======

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_userNoSession )
	public String userNoSession() {
		return "user_account/no_user_session";
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_userResetPasswordPage )
	public String userResetPasswordPage() {
		return "user_account/resetPassword";
	}

	//  ====== Page-init actions delegated to the existing Struts action classes ======
	//  Per-action maps translate each forward name the action can return into a Spring target.

	private static final Map<String,String> FWD_INSUFFICIENT_ACCESS = forwards(
			"Success", "user_account/insufficient_access_privilege",
			"UserDisabled", "redirect:/account_disabled.do" );

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_userInsufficientAccessPrivilege )
	public String userInsufficientAccessPrivilege( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new UserInsufficientAccessPrivilegePageInitAction().execute( request, response ), FWD_INSUFFICIENT_ACCESS );
	}

	private static final Map<String,String> FWD_ACCOUNT_DISABLED = forwards(
			"Success", "user_account/account_disabled" );

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_accountDisabled )
	public String accountDisabled( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new UserDisabledPageInitAction().execute( request, response ), FWD_ACCOUNT_DISABLED );
	}

	private static final Map<String,String> FWD_RESET_PW_PROCESS_CODE = forwards(
			"Success", "user_account/resetPasswordChangePassword",
			"Failure", "user_account/resetPasswordChangePasswordProcessCodeFail" );

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_userResetPasswordProcessCode )
	public String userResetPasswordProcessCode( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new UserResetPasswordProcessCodeAction().execute( request, response ), FWD_RESET_PW_PROCESS_CODE );
	}

	private static final Map<String,String> FWD_SIGNUP = forwards(
			"Success", "user_account/userSignup",
			"NotAllowed", "user_account/userSignupNotAllowed",
			SpringMvcGlobalForwardNames.GENERAL_ERROR, "generalError" );

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_userSignupPage )
	public String userSignupPage( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new UserSignupPageInitAction().execute( request, response ), FWD_SIGNUP );
	}

	private static final Map<String,String> FWD_INVITE_PROCESS_CODE = forwards(
			"ProjectInviteLandingPage", "forward:/user_inviteLandingPage.do",
			"AddNewUser", "forward:/user_inviteCreateNewUserPage.do",
			"GoToProjectList", "redirect:/listProjects.do",
			"Failure", "user_account/inviteUserProcessCodeFail" );

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_userInviteProcessCode )
	public String userInviteProcessCode( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new UserInviteProcessCodeAction().execute( request, response ), FWD_INVITE_PROCESS_CODE );
	}

	private static final Map<String,String> FWD_INVITE_LANDING = forwards(
			"Success", "user_account/inviteLandingPage",
			"Failure", "user_account/inviteUserProcessCodeFail" );

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_userInviteLandingPage )
	public String userInviteLandingPage( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new UserInviteLandingPageInitAction().execute( request, response ), FWD_INVITE_LANDING );
	}

	private static final Map<String,String> FWD_INVITE_CREATE_NEW_USER = forwards(
			"Success", "user_account/inviteUserGetNewUserInfo",
			"Failure", "forward:/user_inviteProcessCode.do" );

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_userInviteCreateNewUserPage )
	public String userInviteCreateNewUserPage( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new UserInviteCreateNewUserInitPageAction().execute( request, response ), FWD_INVITE_CREATE_NEW_USER );
	}

	private static final Map<String,String> FWD_ACCOUNT_PAGE = forwards(
			"Success", "user_account/account",
			SpringMvcGlobalForwardNames.NO_USER_SESSION, SpringMvcForwards.NO_USER_SESSION );

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_accountPage )
	public String accountPage( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new AccountPageInitAction().execute( request, response ), FWD_ACCOUNT_PAGE );
	}

	private static final Map<String,String> FWD_MANAGE_USERS = forwards(
			"Success", "user_account/manageUsersPage",
			SpringMvcGlobalForwardNames.NO_USER_SESSION, SpringMvcForwards.NO_USER_SESSION,
			SpringMvcGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE, SpringMvcForwards.INSUFFICIENT_ACCESS_PRIVILEGE );

	@RequestMapping( A__SpringMVC_Controller_Paths.UserAccountController_manageUsersPage )
	public String manageUsersPage( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new ManageUsersPageInitAction().execute( request, response ), FWD_MANAGE_USERS );
	}

	/** Build an unmodifiable-style forward map from alternating name,target pairs. */
	private static Map<String,String> forwards( String... nameTargetPairs ) {
		Map<String,String> map = new HashMap<>();
		for ( int i = 0; i < nameTargetPairs.length; i += 2 ) {
			map.put( nameTargetPairs[ i ], nameTargetPairs[ i + 1 ] );
		}
		return map;
	}
}
