package org.yeastrc.xlink.www.default_page_view;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.constants.DefaultPageViewConstants;
import org.yeastrc.xlink.www.dao.DefaultPageViewGenericDAO;
import org.yeastrc.xlink.www.dto.DefaultPageViewGenericDTO;

/**
 * 
 *
 */
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
	
	/**
	 * @param defaultPageViewGenericDTO
	 * @throws Exception
	 */
	public void defaultPageViewSaveOrUpdate( DefaultPageViewGenericDTO defaultPageViewGenericDTO ) throws Exception {
		String pageName = defaultPageViewGenericDTO.getPageName();
		if ( ! DefaultPageViewConstants.ALLOWED_PAGE_NAMES_FOR_DEFAULT_PAGE_VIEWS.contains( pageName ) ) {
			String msg = "pageName is not valid: " + pageName;
			log.error( msg );
			throw new Exception(msg);
		}
		DefaultPageViewGenericDAO.getInstance().saveOrUpdate( defaultPageViewGenericDTO );
	}
}
