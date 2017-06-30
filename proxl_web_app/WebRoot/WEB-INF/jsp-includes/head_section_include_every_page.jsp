

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
	  
	  <%--
	  	https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/forEach
	  	Polyfill
	  forEach() was added to the ECMA-262 standard in the 5th edition; as such it may not be present in other implementations of the standard. You can work around this by inserting the following code at the beginning of your scripts, allowing use of forEach() in implementations that don't natively support it. This algorithm is exactly the one specified in ECMA-262, 5th edition, assuming Object and TypeError have their original values and that callback.call() evaluates to the original value of Function.prototype.call().

		// Production steps of ECMA-262, Edition 5, 15.4.4.18
		// Reference: http://es5.github.io/#x15.4.4.18
	  
	  --%>
if (!Array.prototype.forEach) {

  Array.prototype.forEach = function(callback/*, thisArg*/) {

    var T, k;

    if (this == null) {
      throw new TypeError('this is null or not defined');
    }

    // 1. Let O be the result of calling toObject() passing the
    // |this| value as the argument.
    var O = Object(this);

    // 2. Let lenValue be the result of calling the Get() internal
    // method of O with the argument "length".
    // 3. Let len be toUint32(lenValue).
    var len = O.length >>> 0;

    // 4. If isCallable(callback) is false, throw a TypeError exception. 
    // See: http://es5.github.com/#x9.11
    if (typeof callback !== 'function') {
      throw new TypeError(callback + ' is not a function');
    }

    // 5. If thisArg was supplied, let T be thisArg; else let
    // T be undefined.
    if (arguments.length > 1) {
      T = arguments[1];
    }

    // 6. Let k be 0.
    k = 0;

    // 7. Repeat while k < len.
    while (k < len) {

      var kValue;

      // a. Let Pk be ToString(k).
      //    This is implicit for LHS operands of the in operator.
      // b. Let kPresent be the result of calling the HasProperty
      //    internal method of O with argument Pk.
      //    This step can be combined with c.
      // c. If kPresent is true, then
      if (k in O) {

        // i. Let kValue be the result of calling the Get internal
        // method of O with argument Pk.
        kValue = O[k];

        // ii. Call the Call internal method of callback with T as
        // the this value and argument list containing kValue, k, and O.
        callback.call(T, kValue, k, O);
      }
      // d. Increase k by 1.
      k++;
    }
    // 8. return undefined.
  };
}	  
	  
	</script>	
	
	<script type="text/javascript" src="${contextPath}/js/crosslinks_constants_every_page.js?x=${cacheBustValue}"></script>
	
	<script type="text/javascript" src="${contextPath}/js/showHideErrorMessage.js?x=${cacheBustValue}"></script>
	
	
	