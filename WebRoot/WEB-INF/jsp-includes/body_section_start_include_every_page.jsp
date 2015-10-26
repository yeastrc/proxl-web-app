

<%--  body_section_start_include_every_page.jsp

			This is included at the top of the <head> section of every page
			
			This is included in header_main.jsp which covers most of the pages.


 --%>
 
 	
	<%--  Store the context path of the web app in a hidden field with id "context_path_every_page_field" 
	
		This "id" must match js/crosslinks_constants_every_page.js  CROSSLINKS_CONSTANTS_EVERY_PAGE.CONTEXT_PATH_EVERY_PAGE_FIELD_ID_NAME
	--%>

	<input type="hidden" id="context_path_every_page_field"  value="${contextPath}">
	

