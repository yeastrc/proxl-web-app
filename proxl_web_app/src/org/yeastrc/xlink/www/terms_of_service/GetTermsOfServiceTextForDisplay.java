package org.yeastrc.xlink.www.terms_of_service;

//import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.dao.TermsOfServiceTextVersionsDAO;
import org.yeastrc.xlink.www.dto.TermsOfServiceTextVersionsDTO;

/**
 * For use everywhere except the configuration page
 * 
 * Substitute <br> for \n
 *
 */
public class GetTermsOfServiceTextForDisplay {

//	private static final Logger log = Logger.getLogger(GetTermsOfServiceTextForDisplay.class);
	private GetTermsOfServiceTextForDisplay() { }
	public static GetTermsOfServiceTextForDisplay getInstance() {
		return new GetTermsOfServiceTextForDisplay(); 
	}
	
	/**
	 * For use everywhere except the configuration page
	 * Substitute <br> for \n in text
	 * @return null if none found
	 * @throws Exception 
	 */
	public TermsOfServiceTextVersionsDTO getLatestTermsOfServiceTextForDisplay() throws Exception {
		
		TermsOfServiceTextVersionsDTO termsOfServiceTextVersionsDTO =
				TermsOfServiceTextVersionsDAO.getInstance().getLatest();
		if ( termsOfServiceTextVersionsDTO == null ) {
			return null;  //  EARLY RETURN
		}
		String termsOfServiceText = termsOfServiceTextVersionsDTO.getTermsOfServiceText();
		termsOfServiceText = termsOfServiceText.replaceAll( "\\n", "<br>" );
		termsOfServiceTextVersionsDTO.setTermsOfServiceText( termsOfServiceText );
		return termsOfServiceTextVersionsDTO;
	}

}
