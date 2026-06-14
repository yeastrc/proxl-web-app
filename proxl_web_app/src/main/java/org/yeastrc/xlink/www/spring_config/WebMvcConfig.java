package org.yeastrc.xlink.www.spring_config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.util.UrlPathHelper;

/**
 * Spring Web MVC configuration for the DispatcherServlet that incrementally
 * replaces the Struts 1 ActionServlet.
 *
 * <p>The DispatcherServlet (see web.xml, servlet 'springDispatcher') is mapped to
 * EXACT paths (e.g. /peptide.do). Per the Servlet spec, an exact url-pattern takes
 * precedence over the extension mapping *.do used by Struts, so only the converted
 * paths route to Spring; everything else still falls through to Struts.
 *
 * <p>Because the servlet is mapped to an exact path, {@link UrlPathHelper#setAlwaysUseFullPath(boolean)}
 * is enabled so the handler-mapping lookup path is the full path within the web app
 * (e.g. "/peptide.do"), allowing {@code @RequestMapping("/peptide.do")} to match.
 *
 * <p>Views resolve to JSPs under /WEB-INF/jsp-pages/ , the same location Struts forwarded to.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "org.yeastrc.xlink.www.spring_controllers")
public class WebMvcConfig implements WebMvcConfigurer {

	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix( "/WEB-INF/jsp-pages/" );
		resolver.setSuffix( ".jsp" );
		return resolver;
	}

	@Override
	public void configurePathMatch( PathMatchConfigurer configurer ) {
		UrlPathHelper urlPathHelper = new UrlPathHelper();
		urlPathHelper.setAlwaysUseFullPath( true );
		configurer.setUrlPathHelper( urlPathHelper );
	}
}
