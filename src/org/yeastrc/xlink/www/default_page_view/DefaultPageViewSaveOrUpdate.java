package org.yeastrc.xlink.www.default_page_view;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.DefaultPageViewConstants;
import org.yeastrc.xlink.www.dao.DefaultPageViewDAO;
import org.yeastrc.xlink.www.dto.DefaultPageViewDTO;

public class DefaultPageViewSaveOrUpdate {

	private static final Logger log = Logger.getLogger(DefaultPageViewSaveOrUpdate.class);

	//  private constructor
	private DefaultPageViewSaveOrUpdate() { }
	
	/**
	 * @return newly created instance
	 */
	public static DefaultPageViewSaveOrUpdate getInstance() { 
		return new DefaultPageViewSaveOrUpdate(); 
	}
	
	public void defaultPageViewSaveOrUpdate( DefaultPageViewDTO defaultPageViewDTO ) throws Exception {
		
		String pageName = defaultPageViewDTO.getPageName();
		
		if ( ! DefaultPageViewConstants.ALLOWED_PAGE_NAMES_FOR_DEFAULT_PAGE_VIEWS.contains( pageName ) ) {
			
			String msg = "pageName is not valid: " + pageName;
			log.error( msg );
			throw new Exception(msg);
		}
		
		DefaultPageViewDAO.getInstance().saveOrUpdate(defaultPageViewDTO);
		
	}
	
}
