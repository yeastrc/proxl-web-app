
<%--  sharePageURLShortenerOverlayFragment.jsp    /WEB-INF/jsp-includes/sharePageURLShortenerOverlayFragment.jsp

		This contains the Overlay for "Share Page" to display the shortened URL

	  This is used by the Javascript code in sharePageURLShortener.js
--%>


<%@page import="org.yeastrc.xlink.www.constants.WebConstants"%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>



	<!--  Modal dialog for showing user the shortened URL -->

		<!--  Div behind modal dialog div --> 

	<div class="modal-dialog-overlay-background   shortened_url_display_overlay_show_hide_parts_jq shortened_url_display_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
		id="shortened_url_display_overlay_background" 
		onclick='$(".shortened_url_display_overlay_show_hide_parts_jq").hide();' 
		></div>
	
			<!--  Inline div for positioning modal dialog on page -->
	<div class="shortened-url-display-overlay-containing-outermost-div " id="shortened_url_display_overlay_containing_outermost_div_inline_div"  >

	  <div class="shortened-url-display-overlay-containing-outer-div " >
	

			<!--  Div overlay for confirming removing a note -->
		<div class="modal-dialog-overlay-container shortened-url-display-overlay-container   shortened_url_display_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
			 id="shortened_url_display_overlay_container" >

			<div class="top-level-label" style="margin-left: 0px;">URL Shortcut</div>

			<div class="top-level-label-bottom-border" ></div>
			
			<div >
			
				<div >
					<span id="shortened_url_display_overlay_url_display_div"></span>
					<%--
					<input type="button" value="Copy" class=""
						onclick='sharePageURLShortener.copyToClipboard()' >
					--%>
				</div>
				<div style="margin-top: 15px;">
					Use this URL to share this page and all current options with authorized users.  This URL does not provide access rights to anyone with the URL.
				</div>
				
				<div style="margin-top: 10px">
					<input type="button" value="Close" class="shortened_url_display_overlay_cancel_parts_jq"
						onclick='$(".shortened_url_display_overlay_show_hide_parts_jq").hide();' >
				</div>
					
			</div>
			
		</div>
	
	  </div>
	</div>
	
	
	<!-- END:   Modal dialog for confirming deleting a note -->
	