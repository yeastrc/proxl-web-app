

<%--  footer_main.jsp    /WEB-INF/jsp-includes/footer_main.jsp

	  This is included in every page 
--%>


<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>
	
	<div class="footer-outer-container">
	
		<div class="footer-left-container">
			<span class="ProXL-DB-text" >ProXL DB</span> - Protein Crosslinking Database
		</div>
		<div class="footer-right-container">
			© 2016 University of Washington
		</div>
		<div class="footer-center-outer-container">
			<%--  'id' used by manage configuration to update this div with admin entered data --%>
			<div id="footer_center_container" class="footer-center-container">
				<c:out value="${ configSystemValues.footerCenterOfPageHTML }" escapeXml="false"></c:out>
			</div>
		</div>
	
	</div>

  </div>  <%--  End of <div class="crosslinks-page-main-outermost-div">  in the header  --%>
 
 </body>
</html>