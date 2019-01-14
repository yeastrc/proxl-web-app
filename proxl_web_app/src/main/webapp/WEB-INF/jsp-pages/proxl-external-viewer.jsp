<!DOCTYPE html>
<html>

	<head>
	
		
	 	<%--  Include file that is really included into <head> of every page --%>
	  	
	  	<%@ include file="/WEB-INF/jsp-includes/head_section_include_every_page_light.jsp" %>
	  	
		
		<title>Proxl External Viewer</title>
		
		<script type="text/javascript" src="js/libs/jquery-1.11.0.min.js"></script>
		

		
		
	</head>

	<body style="overflow:hidden;">
		<div id="new-window-viewer-div" style="width:100%;height:100%;border-width:1px;border-style:solid;border-color:#A55353;"></div>

		<script>
		
			var rtime = new Date();
			var timeout = false;
			var delta = 300;
			
			$(window).bind('unload', function(){
				window.opener.popinViewer();
			});
			
			$(document).ready( function() {
			    rtime = new Date();
			    if (timeout === false) {
			        timeout = true;
			        setTimeout(resizeend, delta);
			    }
			});
			
			$(window).resize(function() {
			    rtime = new Date();
			    if (timeout === false) {
			        timeout = true;
			        setTimeout(resizeend, delta);
			    }
			});

			function resizeend() {
			    if (new Date() - rtime < delta) {
			        setTimeout(resizeend, delta);
			    } else {
			        timeout = false;
					window.opener.drawStructureAfterResize();
			    }               
			}
			
		
		</script>

	</body>

</html>
