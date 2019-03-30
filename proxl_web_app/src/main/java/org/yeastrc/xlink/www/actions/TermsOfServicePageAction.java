package org.yeastrc.xlink.www.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.constants.StrutsGlobalForwardNames;
import org.yeastrc.xlink.www.dto.TermsOfServiceTextVersionsDTO;
import org.yeastrc.xlink.www.exceptions.ProxlWebappConfigException;
import org.yeastrc.xlink.www.terms_of_service.GetTermsOfServiceTextForDisplay;
import org.yeastrc.xlink.www.web_utils.IsTermsOfServiceEnabled;

/**
 * Terms of Service page action
 *
 */
public class TermsOfServicePageAction extends Action {
	
	private static final Logger log = LoggerFactory.getLogger( TermsOfServicePageAction.class);
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
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
			return mapping.findForward( "Success" );
		} catch ( Exception e ) {
			String msg = "Exception caught: " + e.toString();
			log.error( msg, e );
			return mapping.findForward( StrutsGlobalForwardNames.GENERAL_ERROR );
		}
	}
}
