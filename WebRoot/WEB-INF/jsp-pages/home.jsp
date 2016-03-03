<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<html>
<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="${ contextPath }/js/jquery-1.11.0.min.js"></script>  --%>
	 
	
	
</head>
<body>
HOME<br>
<br>

<a href="user_loginPage.do?useDefaultURL=true" >sign in</a><br><br>
<a href="user_logout.do" >sign out</a><br><br>

<c:if test="${  allowedReadAccessProjectIdsNotEmpty or accountLoggedIn }">
	<a href="listProjects.do" >list projects</a><br><br>
</c:if>

<c:if test="${ authAccessLevel.createNewProjectAllowed }" >

	<a href="createProjectPage.do" >create project</a><br><br>


</c:if>

<c:if test="${ accountLoggedIn }" >

				
	<br><br>
	
	<a href="accountPage.do" >account maintenance</a><br><br>
</c:if>
	
<c:if test="${ authAccessLevel.adminAllowed }" >

<%-- 
--%>
	<br><br>
	
	<a href="manageUsersPage.do" >user accounts maintenance</a><br><br>
					
	<br><br>
</c:if>

<br><br>
			

<c:if test="${ accountLoggedIn }" >

		
	Name: <c:out value="${ user.userDBObject.firstName }"></c:out>, <c:out value="${ user.userDBObject.lastName }"></c:out><br>
	Username: <c:out value="${ user.userDBObject.authUser.username }"></c:out>
</c:if>

</body>

</html>