<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %><%-- Always put this directive at the very top of the page --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<%--  projectsSearchList.jsp --%>

<%--  List all the Projects and their searches for a user  --%>

 <c:set var="pageTitle">Projects Search List</c:set>

 <c:set var="pageBodyClass" >projects-list-page</c:set>

 <c:set var="headerAdditions">
 

	<script type="text/javascript" src="static/js_generated_bundles/header_section_every_page/header_section_every_page-bundle.js?x=${ cacheBustValue }"></script>

 </c:set>


<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>


<div class="overall-enclosing-block">
	
	<div class="top-level-label your-projects-title" >Your Projects, their Searches and Researchers</div>	
	
	

		<%--  The list of projects will be put in this div by the Javascript --%>
	<table border="0" width="100%"  style="margin-left: 20px;">

    <c:forEach var="project" items="${ projectList }">
	 <tr class="project_root_container_jq">	
		<td colspan="3" >			
				<a style="font-weight: bold; font-size: 18px;"
					class="project-text-link  project_title_jq tool_tip_attached_jq" data-tooltip="View project" 
					href="viewProject.do?<%= WebConstants.PARAMETER_PROJECT_ID %>=${ project.projectMain.id }" 
					><c:out value="${ project.projectMain.title }"></c:out></a>
		</td>
	 </tr>
	 <tr>
	 	<td></td>
	 	<td colspan="2">
	 		<span style="font-weight: bold">Researchers:</span> <c:out value="${ project.users }"></c:out>
	 	</td>
	 <tr>
	 	<td style="width: 20px;"></td>
	 	<td colspan="2" style="font-weight: bold">
	 		Searches for Project:
	 	</td>
	 </tr>
	  <c:forEach var="search" items="${ project.searches }">
		 <tr>
		 	<td></td>
		 	<td style="width: 20px;"></td>
		 	<td>
		 		<c:out value="${ search.name }"></c:out> (<c:out value="${ search.searchId }"></c:out>)
		 	</td>
		 </tr>
	  </c:forEach>
	 <tr  class="project_separator_row_jq">
	   <td colspan="3">	
		<div class="project-container-bottom-border" ></div>
	  </td>
	 </tr>
    </c:forEach>	
	  
	</table>
		
	
</div>

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
