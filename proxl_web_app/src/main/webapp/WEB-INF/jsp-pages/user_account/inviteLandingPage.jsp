<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<%--  inviteLandingPage.jsp

 /WEB-INF/jsp-pages/user_account/inviteLandingPage.jsp 
 
    For when an invite link is clicked and not logged in
 
 --%>


<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>

 	<title>ProXL DB</title>
 
 <link REL="stylesheet" TYPE="text/css" HREF="static/css_generated/global.css?x=${cacheBustValue}">

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="js/jquery-1.11.0.min.js"></script>  --%>
	
		
</head>

<body class="user-invite-landing-page inset-page"> <%-- "inset-page" is for pages with an 'inset' look --%>

 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>


<div class="inset-page-main-outermost-div"> <%--  Closed in footer_main.jsp --%>


<div class="page-content-outer-container" >	
 <div class="page-content-container" >	
  <div class="page-content" >	

	<div class="logo-large-container" >
		<img src="images/logo-large.png" />
	</div>

	<c:choose>
	 <c:when test="${ not empty titleDisplay }">
	 
	  	<div  class="page-label">
	  		Project Invitation
	  	</div>
	  	
	  	<div class="page-text" style="margin-bottom: 20px;">
	  		You have been invited to a ProXL project:
	  	</div>
	  	
	  	<div class="page-text" style="margin-bottom: 20px;">
	  		<c:out value="${ titleDisplay }"></c:out>
	  	</div>

	 </c:when>
	
	</c:choose>
	
	<div class="page-text sign-in-above-text">
		If you already have an account, sign in:
	</div>
  	
	<form action="user_loginPage.do" >
		<input type="hidden" name="requestedURL" value="${ intialIncomingURL }"> 
		<input type="hidden" name="inviteTrackingCode" value="<c:out value="${ inviteCode }" ></c:out>">
	 	<INPUT TYPE="submit" class="submit-button" VALUE="Sign In" >
	</form>
	
	<div class="page-text create-account-above-text">
		If you do not have an account, please create one:
	</div>
  	
	<form action="user_inviteCreateNewUserPage.do" >
		<input type="hidden" name="code" value="<c:out value="${ inviteCode }" ></c:out>"> 
	 	<INPUT TYPE="submit" class="submit-button" VALUE="Create Account" >
	</form>


	
  </div>
  
 </div>
</div>
  
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
