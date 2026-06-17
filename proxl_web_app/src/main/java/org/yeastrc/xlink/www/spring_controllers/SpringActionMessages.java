package org.yeastrc.xlink.www.spring_controllers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Struts-free replacement for the {@code Action.saveErrors(request, ActionMessages)} mechanism used
 * by the de-Strutsed page actions. Resolves a message KEY via the {@code web_app_application_properties}
 * ResourceBundle (the same basename Struts used via {@code <message-resources>}) and appends the
 * display text to a {@code List<String>} stored under request attribute
 * {@value #SPRING_MVC_CONTROLLER_ERROR_MESSAGES_REQUEST_ATTRIBUTE}, which the message-display JSPs iterate with
 * {@code <c:forEach var="message" items="${ springMVC_Controller_ErrorMessages }">}.
 *
 * <p>Resolves message keys to display text and stores them under the request attribute the
 * message-display JSPs read. Use this from controller-side action logic that needs to surface
 * error messages to a JSP.
 */
public final class SpringActionMessages {

	public static final String SPRING_MVC_CONTROLLER_ERROR_MESSAGES_REQUEST_ATTRIBUTE = "springMVC_Controller_ErrorMessages";

	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle( "web_app_application_properties" );

	private SpringActionMessages() {}

	/**
	 * Struts-{@code saveErrors}-equivalent: REPLACE the request-scoped error list with a single
	 * message resolved from {@code messageKey} via the bundle (matching Struts' resource rendering,
	 * including the {@code ???key???} fallback for a missing key).
	 *
	 * <p>Replace (not append) matches Struts {@code saveErrors(request, messages)}, which does
	 * {@code request.setAttribute(Globals.ERROR_KEY, messages)} — so on a forward chain where a
	 * second action also sets an error, only the last one shows (one message, not accumulated).
	 */
	public static void setErrorMessageKey( HttpServletRequest request, String messageKey, Object... values ) {
		List<String> list = new ArrayList<>();
		request.setAttribute( SPRING_MVC_CONTROLLER_ERROR_MESSAGES_REQUEST_ATTRIBUTE, list );
		String pattern;
		try {
			pattern = BUNDLE.getString( messageKey );
		} catch ( MissingResourceException e ) {
			list.add( "???" + messageKey + "???" );   //  match Struts' missing-key rendering
			return;
		}
		if ( values != null && values.length > 0 ) {
			list.add( MessageFormat.format( pattern, values ) );
		} else {
			list.add( pattern );
		}
	}
}
