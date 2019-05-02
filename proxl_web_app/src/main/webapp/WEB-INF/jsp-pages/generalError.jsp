<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  generalError.jsp --%>

 <c:set var="pageTitle">Error</c:set>

 <c:set var="pageBodyClass" >error-page</c:set>

 <c:set var="headerAdditions">
 
	<script type="text/javascript" src="static/js_generated_bundles/header_section_every_page/header_section_every_page-bundle.js?x=${ cacheBustValue }"></script>

</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

<br>
<br>
<br>

<div class="overall-enclosing-block">
  	
  	<div  style="position: relative;" class="page-label">
  	
  		<div style="font-weight: bold;">There was a problem processing your request.</div>


	</div>

</div>


<br>
<br>
<br>


<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

