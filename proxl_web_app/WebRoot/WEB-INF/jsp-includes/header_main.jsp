

<%--  header_main.jsp    /WEB-INF/jsp-includes/header_main.jsp 

	  This is included in every page other than login, forgot password processing, ...  

	  The data for this header is populated in the 
	  
	  class GetPageHeaderData 
	    using getPageHeaderDataWithProjectId(...) or getPageHeaderDataWithoutProjectId(...)
	    
	  One of those 2 methods is required to be called for every page this header is included on



	Page variable "helpURLExtensionForSpecificPage" specifies the extension to the Help URL for the specific page
	
	Page variable "helpURLExtensionForSpecificPage" is used in 2 places in this file.
--%>


<%@page import="org.yeastrc.xlink.www.constants.WebConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<%--  Default title --%>

<%-- 
	<c:if test="${ empty pageTitle }" >

		<c:set var="pageTitle" value="Protein Crosslinking Database" ></c:set>
	
	</c:if>
--%>

<%
response.setHeader("Pragma", "No-cache");
response.setHeader("Cache-Control","no-cache");
response.setDateHeader("Expires", 0);
response.addHeader("Cache-control", "no-store"); // tell proxy not to cache
response.addHeader("Cache-control", "max-age=0"); // stale right away
%>

<%--
	HTML5 DOCTYPE
	
	The DOCTYPE is partially put in to make IE not go into quirks mode (the default when there is no DOCTYPE).

--%>

<!DOCTYPE html>

<html class="no-js"> <!--  Modernizr will change "no-js" to "js" if Javascript is enabled -->


<head>

 <%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page.jsp" %>

	<title>ProXL DB - <c:out value="${ pageTitle }" ></c:out></title>

	<%--  Loaded in head_section_include_every_page.jsp   --%>
	<%-- <script type="text/javascript" src="${ contextPath }/js/jquery-1.11.0.min.js"></script>  --%>
	 
	
	
	<script type="text/javascript" src="${ contextPath }/js/crosslinksCollapsible.js?x=${cacheBustValue}"></script>
	
	<script type="text/javascript" src="${ contextPath }/js/header_main.js?x=${cacheBustValue}"></script>

	
	<style >
	
		/* This depends on the JQueryUI ui-lightness theme being included in the web app  */
		/*
		body.crosslinks-page-main .modal-dialog-overlay-background { background: #666 url(${ contextPath }/css/jquery-ui-1.10.2-Themes/ui-lightness/images/ui-bg_diagonals-thick_20_666666_40x40.png) 50% 50% repeat; }
		*/ 	
	</style>

	<%--  Output the contents of "headerAdditions" here --%>
	<c:out value="${ headerAdditions }" escapeXml="false" ></c:out>


	<link rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" type="text/css" media="print, projection, screen" />
	<link rel="stylesheet" href="${ contextPath }/css/global.css?x=${cacheBustValue}" type="text/css" media="print, projection, screen" />


</head>
 

<body class="crosslinks-page-main <c:out value="${ pageBodyClass }" ></c:out>">


 <%@ include file="/WEB-INF/jsp-includes/body_section_start_include_every_page.jsp" %>


 <%--  Whole Page Div container, needed for forcing the footer to the bottom of the viewport --%>
		
 <div class="crosslinks-page-main-outermost-div">  <%--  This div is closed in footer_main.jsp --%>
 
  
  <div class="header-outer-container">  <%--  Outer Container for the Header --%>
  
    <%--  The Right side contents for the header are first  --%>
  
  	<div class="header-right-edge-container">  <%--  Container for Right Side contents --%>
  	
  	 <c:choose>
  	  <c:when test="${ empty headerUser }">
  	  
  	   <div class="header-right-icons" style="position: relative;">
	  	    
	  		<a href="http://proxl-web-app.readthedocs.io/${ helpURLExtensionForSpecificPage }"  target="_help_window" id="help_header_link" 
	  			><img src="${ contextPath }/images/icon-help.png" 
	  		></a>
	  		
	  		<a href="${ contextPath }/user_loginPage.do?useDefaultURL=yes"  id="signin_header_link" 
	  			><img src="${ contextPath }/images/icon-login.png" 
	  		></a>
	   </div>  	  
	  	<%--  Simulated tool tips, absolutely positioned divs --%>
  		<div class="header-icon-tool-tips" id="help_header_tooltip" >
  			View ProXL Documentation
  		</div>
  		<div class="header-icon-tool-tips" id="signin_header_tooltip" >
  			Signin
  		</div>
  		
	   
	   <%--  images/icon-login.png and images/icon-login-small.png --%>
	   
  	  </c:when>
  	  <c:otherwise>
  	  
  	 	<%--  icons will be to the right of username --%>
  	   <div class="header-right-icons" style="position: relative;">

	  		<a href="http://proxl-web-app.readthedocs.io/${ helpURLExtensionForSpecificPage }"  target="_help_window" id="help_header_link" 
	  			><img src="${ contextPath }/images/icon-help.png" 
	  		></a>
	  	    
	  		<a href="${ contextPath }/accountPage.do"  id="account_settings_header_link" 
	  			><img src="${ contextPath }/images/icon-user-settings.png" 
	  		></a>
	  		
		  <c:if test="${headerUserIsAdmin}" >
	  		<a href="${ contextPath }/manageUsersPage.do" id="manage_users_header_link" 
		  		><img src="${ contextPath }/images/icon-users.png" 
	  		></a>
	  		<a href="${ contextPath }/manageConfiguration.do"  id="manage_proxl_settings_header_link" 
	  			><img src="${ contextPath }/images/icon-proxl-config.png" 
	  		></a>
	  	  </c:if>
	  		
	  		<a href="${ contextPath }/user_logout.do" id="sign_out_header_link" 
	  			><img src="${ contextPath }/images/icon-logout.png" class="header-logout-image" 
	  		></a>
	  		
		  	<%--  Simulated tool tips, absolutely positioned divs --%>
	  		<div class="header-icon-tool-tips" id="account_settings_header_tooltip" >
	  			Account Settings
	  		</div>
		  <c:if test="${headerUserIsAdmin}" >
	  		<div class="header-icon-tool-tips" id="manage_users_tooltip_header" >
	  			Manage Users
	  		</div>
	  		<div class="header-icon-tool-tips" id="manage_config_tooltip_header" >
	  			Manage Proxl Configuration
	  		</div>
	  	  </c:if>
	  		<div class="header-icon-tool-tips" id="sign_out_header_tooltip" >
	  			Sign Out
	  		</div>
	  		<div class="header-icon-tool-tips" id="help_header_tooltip" >
	  			View ProXL Documentation
	  		</div>
	  		
  	   </div>
  	     	  
  	   <div  class="header-user-name-container"> 
		<a href="${ contextPath }/accountPage.do" class="overide-text-color-to-base-color"
	  		><span class="header-user-name">
	  			<span id="header-user-first-name"><c:out value="${ headerUser.firstName }"></c:out></span> 
	  			<span id="header-user-last-name"><c:out value="${ headerUser.lastName }"></c:out></span>
	  			(<span id="header-user-user-name"><c:out value="${ headerUser.authUser.username }"></c:out></span>) 
  			</span></a>
  	   </div>
  		
  	  </c:otherwise>
  	 </c:choose>

  	
  	</div>  <%--  END:  Container for Right Side contents  --%>
	
	
	<%-- Left Side contents --%>
	
	<div class="header-logo">
	 <c:choose>
	  <c:when test="${ not empty headerUser }">
	   <a href="listProjects.do" >
	  </c:when>
	  <c:when test="${ not empty headerProject }">
	   <a href="${ contextPath }/viewProject.do?<%= WebConstants.PARAMETER_PROJECT_ID %>=<c:out value="${ headerProject.projectDTO.id }" ></c:out>">
	  </c:when>
	  <c:otherwise>
	  	
	  </c:otherwise>
	 </c:choose>
		<img src="${ contextPath }/images/proxl-logo-23px.png" >
	 <c:choose>
	  <c:when test="${ not empty headerUser }">
	   </a>
	  </c:when>
	  <c:when test="${ not empty headerProject }">
	   </a>
	  </c:when>
	 </c:choose>
	</div>

	<div class="header-left-main-container">
	
 	 <c:if test="${ not empty headerUser }">
 	
	  <div class="header-pointer-right">
		<span style="padding-left: 5px; padding-right: 5px;"> 
			<img src="${ contextPath }/images/pointer-right.png">
		</span>
	  </div>

	  <div class="header-projects-label-div" style="position: relative;">
	  
	  
		<a href="listProjects.do" class="header-projects-label" id="header_projects_link">
			<span class="header-projects-label">Projects</span>
			<img src="${ contextPath }/images/pointer-down.png">
		</a>
		
		<div class="header-projects-list" id="header_projects_list">
		
			<c:forEach var="projectItem" items="${ headerProjectList }" varStatus="headerProjectListVarStatus" >
			
				<c:if test="${ headerProjectListVarStatus.count > 1 }"> <%-- All but first item, "count" starts at 1 --%>
					<div class="top-level-label-bottom-border" ></div>
				</c:if>
				
				<c:set var="header_current_project_in_drop_down_list"></c:set>  <%-- default to empty --%>
				
				<c:if test="${ not empty headerProject  }">
				  <c:if test="${ projectItem.projectDTO.id == headerProject.projectDTO.id }">
				  
				  	<%--  Set to the value for the "id" in the span expected by the Javascript for the header --%>
					<c:set var="header_current_project_in_drop_down_list">header_current_project_in_drop_down_list</c:set>
				  </c:if>
				</c:if>
			
				<div  class="project-text-div" >
					<a href="${ contextPath }/viewProject.do?<%= WebConstants.PARAMETER_PROJECT_ID %>=<c:out value="${ projectItem.projectDTO.id }" ></c:out>"
						class="project-text-link" 
						><span id="${ header_current_project_in_drop_down_list }"  <%-- << use EL ${  } to drop in the value set just above --%>
							><c:out value="${ projectItem.titleHeaderDisplay }" ></c:out
						></span>
					</a>
				</div>
						
			</c:forEach>
		
		
		</div>
		
	  </div>
		
	 </c:if>

	 <c:if test="${ not empty headerProject }">

	  <div class="header-projects-pointer-right--right-of-projects">
		<img src="${ contextPath }/images/pointer-right.png" >
	  </div>

	  <div class="header-current-project-label-div">
	  
	  
		<a href="${ contextPath }/viewProject.do?<%= WebConstants.PARAMETER_PROJECT_ID %>=<c:out value="${ headerProject.projectDTO.id }" ></c:out>"  
				class="header-project-title"  id="header_project_title_link" 
				<c:if test="${ headerProject.projectDTO.title ne  headerProject.titleHeaderDisplay }">
				
					title="<c:out value="${ headerProject.projectDTO.title }"></c:out>"
				</c:if>
				
				>
				
				<c:set var="header_project_locked_display_none">display:none;</c:set> <%-- initialize to hide the lock symbol for non-locked projects --%>
				<c:if test="${ not headerProject.projectDTO.projectLocked }">
					<c:set var="header_project_locked_display_none"></c:set> <%-- for locked projects, clear the "display:none;" so the lock is shown --%>
				</c:if>
				
				<%--  Show only for locked projects and not for public access --%>
				
				<c:if test="${ not empty headerUser }">
					<img src="${ contextPath }/images/icon-locked-small.png" 
							style="<c:if test="${ not headerProject.projectDTO.projectLocked }">display:none;</c:if>" 
							id="header_project_locked_icon">
				</c:if>
				
				
				<c:choose>
				  <c:when test="${ not empty headerUser }">
					<span class="header-project-title" id="header_project_title"
						><c:out value="${ headerProject.titleHeaderDisplay }"></c:out></span>
				  </c:when>
				  <c:otherwise>
					<span class="header-project-title" id="header_project_title"
						><c:out value="${ headerProject.titleHeaderDisplayNonUser }"></c:out></span>
				  </c:otherwise>
				</c:choose>

		</a>
	  </div>

	 </c:if>
	 
	</div>

  </div>

	

		