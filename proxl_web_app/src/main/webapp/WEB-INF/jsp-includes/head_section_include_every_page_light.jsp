<%-- 
		head_section_include_every_page_light.jsp

 			This is included at the top of the <head> section of every page, 
			either directly or by being included through 
			head_section_include_every_page.jsp being included.
			
			Keep this file light weight.

--%>

  	<%--  Make all relative URLs start after the context path request.getContextPath() --%>
	<base href="<%= request.getContextPath() %>/">  <%-- The trailing '/' in the href is required --%>
	
	