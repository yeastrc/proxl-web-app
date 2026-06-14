package org.yeastrc.xlink.www.spring_controllers__logic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.dto.TermsOfServiceTextVersionsDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappConfigException;
import org.yeastrc.xlink.www.terms_of_service.GetTermsOfServiceTextForDisplay;
import org.yeastrc.xlink.www.web_utils.IsTermsOfServiceEnabled;

/**
 * Terms of Service page action.
 *
 * <p>De-Strutsed: this is a plain class (no longer extends org.apache.struts.action.Action). Its
 * execute(...) returns the forward NAME as a String; the Spring controller translates that name to
 * a Spring view/forward target. The names are still the historical Struts forward names (the
 * SpringMvcGlobalForwardNames constants are plain Strings in our own constants package).
 */
public class TermsOfServicePageAction {

	private static final Logger log = LoggerFactory.getLogger( TermsOfServicePageAction.class);

	public String execute( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		try {
			boolean termsOfServiceEnabled = IsTermsOfServiceEnabled.getInstance().isTermsOfServiceEnabled();
			if ( termsOfServiceEnabled ) {
				TermsOfServiceTextVersionsDTO termsOfServiceTextVersionsDTO =
						GetTermsOfServiceTextForDisplay.getInstance().getLatestTermsOfServiceTextForDisplay();
				if ( termsOfServiceTextVersionsDTO == null ) {
					String msg = "Terms of service is enabled but there is no 'Latest' terms of service record.";
					log.error( msg );
					throw new ProxlWebappConfigException(msg);
				} else {
					request.setAttribute( "termsOfServiceText", termsOfServiceTextVersionsDTO.getTermsOfServiceText() );
				}
			}
			return "Success";
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			return SpringMvcGlobalForwardNames.GENERAL_ERROR;
		}
	}
}
