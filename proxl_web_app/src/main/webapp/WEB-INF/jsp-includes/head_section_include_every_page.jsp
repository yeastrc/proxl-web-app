

<%--  head_section_include_every_page.jsp

			This is included at the top of the <head> section of every page
			
			This is included in header_main.jsp which covers most of the pages.


	This is not included in the <head> section of the following pages:
	
			(They include head_section_include_every_page_light.jsp directly)
	
			 /jsp-pages/special_redirect_pages/redirect_pre_generic_image_structure_ToGenericURL.jsp
			 /jsp-pages/special_redirect_pages/redirect_searchIdsFromHashToQueryString.jsp
			 
			 /jsp-pages/proxl-external-viewer.jsp

 --%>
 
 	<%--  Include file that is really included into <head> of every page --%>
  	
  	<%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page_light.jsp" %>
  	
	
    <link rel="icon" href="images/favicon.ico" />
 
	<script type="text/javascript" src="js/libs/jquery-1.11.0.min.js"></script>
<%-- 
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.js"></script>
--%>	

	<script type="text/javascript" src="js/libs/jquery.qtip.min.js"></script>


	<script type="text/javascript" src="static/js_generated_bundles/header_section_every_page/header_section_every_page-bundle.js?x=${ cacheBustValue }"></script>

<%--  Moved to Front End Build Bundles		
	
	<script type="text/javascript" src="js/reportWebErrorToServer.js?x=${cacheBustValue}"></script>
	
	<script type="text/javascript" src="js/genericToolTip.js?x=${cacheBustValue}"></script>
--%>	
	
	<script type="text/javascript" >
	  
	  var _PROXL_DEFAULT_FONT_COLOR = "#545454";
	  
	  var _PROXL_COLOR_SITE_RED = "#A55353";
	  var _PROXL_COLOR_SITE_PINK = "#FFF0F0"; // Light pink background of page header and footer
	  var _PROXL_COLOR_SITE_GREEN = "#53A553";
	  var _PROXL_COLOR_SITE_BLUE = "#5353A5";
	  
	  var _PROXL_COLOR_LINK_TYPE_CROSSLINK = _PROXL_COLOR_SITE_RED;
	  var _PROXL_COLOR_LINK_TYPE_LOOPLINK = _PROXL_COLOR_SITE_GREEN;
	  var _PROXL_COLOR_LINK_TYPE_UNLINKED = _PROXL_COLOR_SITE_BLUE;
	  var _PROXL_COLOR_LINK_TYPE_ALL_COMBINED = "#A5A5A5";
	  
	</script>	
	
	
<%--  Moved to Front End Build Bundles		
	
	<script type="text/javascript" src="js/crosslinks_constants_every_page.js?x=${cacheBustValue}"></script>
	
	<script type="text/javascript" src="js/showHideErrorMessage.js?x=${cacheBustValue}"></script>
--%>	
	
	