<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  shortcutNotFound.jsp --%>

 <c:set var="pageTitle">Sortcut Not Found</c:set>

 <c:set var="pageBodyClass" >shortcut-not-found-page</c:set>

 <c:set var="headerAdditions">
 

		<link REL="stylesheet" TYPE="text/css" HREF="${ contextPath }/css/jquery-ui-1.10.2-Themes/base/jquery.ui.all.css">


		<script type="text/javascript" src="${ contextPath }/js/libs/jquery-ui-1.10.4.min.js"></script> 

		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js?x=${cacheBustValue}"></script> 
		<script type="text/javascript" src="${ contextPath }/js/viewProjectPage.js?x=${cacheBustValue}"></script> 
		

</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

<br>
<br>
<br>

<div class="overall-enclosing-block">
  	
  	<div  style="position: relative;" class="page-label">
  		<div style="font-weight: bold;">Shortcut could not be found..</div>
	</div>
</div>

<br>
<br>
<br>
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

