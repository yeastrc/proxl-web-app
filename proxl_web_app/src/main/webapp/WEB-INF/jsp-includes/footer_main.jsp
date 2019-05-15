

<%--  footer_main.jsp    /WEB-INF/jsp-includes/footer_main.jsp

	  This is included in every page 
--%>


<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>
	
	<div class="footer-outer-container">
	
		<div class="footer-left-container">
			<span><img src="images/proxl-logo-footer-21px.png" ></span>
		</div>
		<div class="footer-right-container">
			© 2019 University of Washington
			<c:if test="${ configSystemValues.termsOfServiceEnabled }">
				(<a href="termsOfService.do" target="terms_of_service" >Terms of Service</a>)
			</c:if>			
		</div>
		
		
		<div class="footer-center-outer-container">
		  <div class="footer-center-container" >
			<%--  'id' used by manage configuration to update this div with admin entered data --%>
			<div id="footer_center_container" >
				<c:out value="${ configSystemValues.footerCenterOfPageHTML }" escapeXml="false"></c:out>
			</div>
		  </div>
		</div>
	
	</div>
	
	<%--  Add Google Analytics if a tracking code is in the config  --%>
  <c:if test="${ not empty configSystemValues.googleAnalyticsTrackingCode }">
	<script>
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');
	
	  ga('create', '<c:out value="${ configSystemValues.googleAnalyticsTrackingCode }" escapeXml="false"></c:out>', 'auto');
	  ga('send', 'pageview');
	
	</script>	
  </c:if>

  </div>  <%--  End of <div class="crosslinks-page-main-outermost-div">  in the header  --%>
 
 </body>
</html>