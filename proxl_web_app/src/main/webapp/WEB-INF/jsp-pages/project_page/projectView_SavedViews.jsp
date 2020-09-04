<%--   projectView_SavedViews.jsp

	Page Section:   Saved Views
--%>


<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>
	
	
<c:if test="${ showSavedViewsBlock }">

	<%--  If <script > is NOT present, then there is no saved views block and JS will skip processing --%>
	<script type="text/text" id="saved_views_block_shown">Y</script>

	
	<div class="top-level-container collapsable_container_jq" >
	
		<div  class="collapsable-link-container top-level-collapsable-link-container collapsable_link_container_jq" style="">
			<a href="javascript:" class="top-level-collapsable-link collapsable_collapse_link_jq"
				><img  src="images/icon-collapse.png"></a>
			<a href="javascript:" class="top-level-collapsable-link collapsable_expand_link_jq" style="display: none;"
				><img  src="images/icon-expand.png"></a>
		
		</div>
	
		<div class="top-level-label">
		  Saved Views
		</div>
		
		<div class="top-level-label-bottom-border" ></div>
	
			<%-- Left align search text with 'Title' in Project Info so 40(pos of Title) - 16(width of cell for icon) --%>
			<div class=" collapsable_jq" style="margin-left: 26px;">
			
			<%-- 
			<div id="saved_views_list_above_block" style="margin-bottom: 10px;">
			</div>
			--%>
		
		  <div style="margin-bottom: 10px;">
		  
			<div id="saved_views_list" >
				Loading
			</div>
			
			<div id="saved_views_no_entries" style="display: none;">
				No Saved Views
			</div>	  
		  </div>
	
	
	  	</div>
	  	
	</div>

</c:if>
