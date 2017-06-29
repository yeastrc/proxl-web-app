

<%--  head_section_include_every_page.jsp

			This is included at the top of the <head> section of every page
			
			This is included in header_main.jsp which covers most of the pages.


 --%>
 
    <link rel="icon" href="${ contextPath }/images/favicon.ico" />
 
	<script type="text/javascript" src="${contextPath}/js/libs/modernizr.v2.7.1__custom.39924_min.js"></script>
	
	<script type="text/javascript" src="${ contextPath }/js/reportWebErrorToServer.js?x=${cacheBustValue}"></script>
	
	
	<%--
	<script type="text/javascript" src="${contextPath}/js/libs/modernizr.v2.7.1__custom.39924.js"></script>
	--%>

	<script type="text/javascript" src="${ contextPath }/js/libs/jquery-1.11.0.min.js"></script>
<%-- 
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.js"></script>
--%>	

	<script type="text/javascript" src="${ contextPath }/js/libs/jquery.qtip.min.js"></script>

	<script type="text/javascript" src="${ contextPath }/js/genericToolTip.js?x=${cacheBustValue}"></script>


	
	
	<%--  Store the context path of the web app in a javascript variable named contextPathJSVar --%>

	<script type="text/javascript" >
	  var contextPathJSVar = "${contextPath}";
	  
	  var _PROXL_DEFAULT_FONT_COLOR = "#545454";
	  
	  var _PROXL_COLOR_SITE_RED = "#A55353";
	  var _PROXL_COLOR_SITE_GREEN = "#53A553";
	  var _PROXL_COLOR_SITE_BLUE = "#5353A5";
	  
	  var _PROXL_COLOR_LINK_TYPE_CROSSLINK = _PROXL_COLOR_SITE_RED;
	  var _PROXL_COLOR_LINK_TYPE_LOOPLINK = _PROXL_COLOR_SITE_GREEN;
	  var _PROXL_COLOR_LINK_TYPE_UNLINKED = _PROXL_COLOR_SITE_BLUE;
	  var _PROXL_COLOR_LINK_TYPE_ALL_COMBINED = "#A5A5A5";
	  
	  <%--
	  https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/String/Trim
	  
	  // Polyfill for String.trim() 
	  //  Running the following code before any other code will create trim() if it's not natively available.
		--%>	  
	  if (!String.prototype.trim) {
		  String.prototype.trim = function () {
		    return this.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, '');
		  };
		}
	  
	  <%--
	  https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/isArray
	  
	  // Polyfill for Array.isArray() 
	  //  Running the following code before any other code will create Array.isArray() if it's not natively available.
		--%>
	  
	  if (!Array.isArray) {
	    Array.isArray = function(arg) {
	      return Object.prototype.toString.call(arg) === '[object Array]';
	    };
	  }
	  

		<%--
			https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Date/now
			
		  // Polyfill for Date.now() 
		  //  Running the following code before any other code will create Date.now() if it's not natively available.
		  //  Date.now()  returns  the milliseconds elapsed since 1 January 1970 00:00:00 UTC up until now as a Number.
		--%>
	  
	  if (!Date.now) {
		  Date.now = function now() {
		    return new Date().getTime();
		  };
		}
	  
	</script>	
	
	<script type="text/javascript" src="${contextPath}/js/crosslinks_constants_every_page.js?x=${cacheBustValue}"></script>
	
	<script type="text/javascript" src="${contextPath}/js/showHideErrorMessage.js?x=${cacheBustValue}"></script>
	
	
	