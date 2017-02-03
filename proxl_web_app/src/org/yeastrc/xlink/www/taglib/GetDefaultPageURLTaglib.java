package org.yeastrc.xlink.www.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.DefaultPageViewConstants;
import org.yeastrc.xlink.www.dao.DefaultPageViewGenericDAO;
import org.yeastrc.xlink.www.dto.DefaultPageViewGenericDTO;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
/**
 * JSP Taglib to get Default Page URL
 *
 */
public class GetDefaultPageURLTaglib extends TagSupport {
	
	private static final Logger log = Logger.getLogger(GetDefaultPageURLTaglib.class);
	
	private String pageName;
	
	/**
	 * actually projectSearchId
	 */
	private String searchId;
	
	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public String getSearchId() {
		return searchId;
	}
	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	public int doStartTag() throws JspException { 
		int returnValue = EVAL_BODY_INCLUDE; // default to print the contents of the body of the tag
    	if ( StringUtils.isEmpty(pageName)) {
        	String msg = "Exception:  pageName is empty";
        	log.error( msg );
            throw new JspException( msg );
    	}
    	if ( StringUtils.isEmpty(searchId)) {
        	String msg = "Exception:  searchId is empty";
        	log.error( msg );
            throw new JspException( msg );
    	}
    	if ( ! DefaultPageViewConstants.ALLOWED_PAGE_NAMES_FOR_DEFAULT_PAGE_VIEWS.contains(pageName)) {
        	String msg = "Exception:  pageName is not a supported pageName: " + pageName;
        	log.error( msg );
            throw new JspException( msg );
    	}
    	int projectSearchIdInt = 0;
    	try {
    		projectSearchIdInt = Integer.parseInt(searchId); //  searchId is actually projectSearchId
    	} catch (Exception e) { 
    		String msg = "Exception:  Search Id is not a number: " + searchId + ", pageName: " + pageName;
    		log.error( msg, e );
    		throw new JspException( e );
    	} 
    	try {
    		Integer searchIdActual = MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchIdInt );
    		if ( searchIdActual == null ) {
        		String msg = "Failed to find searchIdActual (not in DB) for  projectSearchIdInt:" + projectSearchIdInt + ", pageName: " + pageName;
        		log.error( msg );
        		throw new JspException( msg );
    		}
    	} catch (JspException e) {
    		throw e;
    	} catch (Exception e) { 
    		String msg = "Exception:  getting searchIdActual for  projectSearchIdInt:" + projectSearchIdInt + ", pageName: " + pageName;
    		log.error( msg, e );
    		throw new JspException( e );
    	} 
        try { 
        	DefaultPageViewGenericDTO defaultPageViewDTO = DefaultPageViewGenericDAO.getInstance().getForSearchIdPageName( projectSearchIdInt, pageName );
        	if ( defaultPageViewDTO != null ) {
        		returnValue = SKIP_BODY; // Do NOT print the contents of the body of the tag since print data here
        		// Get our writer for the JSP page using this tag
        		JspWriter writer = pageContext.getOut();
        		writer.println( defaultPageViewDTO.getUrl() ); 
        	}
        } catch (Exception e) { 
        	String msg = "Exception:  pageName: " + pageName;
        	log.error( msg, e );
            throw new JspException( e );
        } 
        return returnValue;
    } 
}
