package org.yeastrc.xlink.www.spring_controllers;

import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.yeastrc.xlink.www.constants.SpringMvcGlobalForwardNames;
import org.yeastrc.xlink.www.spring_controllers__logic.CacheDataAllLogCurrentCacheSizesAction;
import org.yeastrc.xlink.www.spring_controllers__logic.CacheDataClearAllAction;
import org.yeastrc.xlink.www.spring_controllers__logic.CacheDataClearConfigDataAction;

/**
 * Spring MVC controller for the cache-data admin actions.
 */
@Controller
public class CacheDataAdminController {

	//  struts-config: Success -> clearAllConfigCacheSuccess.jsp
	private static final Map<String,String> FORWARDS = new HashMap<>();
	static {
		FORWARDS.put( "Success", "clearAllConfigCacheSuccess" );
		FORWARDS.put( SpringMvcGlobalForwardNames.NO_USER_SESSION, SpringMvcForwards.NO_USER_SESSION );
		FORWARDS.put( SpringMvcGlobalForwardNames.INSUFFICIENT_ACCESS_PRIVILEGE, SpringMvcForwards.INSUFFICIENT_ACCESS_PRIVILEGE );
		FORWARDS.put( SpringMvcGlobalForwardNames.GENERAL_ERROR, "generalError" );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.CacheDataAdminController_cacheDataAllLogCurrentCacheSizes )
	public String cacheDataAllLogCurrentCacheSizes( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new CacheDataAllLogCurrentCacheSizesAction().execute( request, response ), FORWARDS );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.CacheDataAdminController_cacheDataClearAll )
	public String cacheDataClearAll( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new CacheDataClearAllAction().execute( request, response ), FORWARDS );
	}

	@RequestMapping( A__SpringMVC_Controller_Paths.CacheDataAdminController_cacheDataClearConfigData )
	public String cacheDataClearConfigData( HttpServletRequest request, HttpServletResponse response ) throws Exception {
		return SpringForwardResolver.resolve( new CacheDataClearConfigDataAction().execute( request, response ), FORWARDS );
	}
}
