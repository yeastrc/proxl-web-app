

<%--  projectReOrderSearches.jsp  --%>

<%--  Re-order searches block, on project page --%>


  <div id="re_order_searches_data_block" style="display: none;">

	<div id="re_order_searches_loading_message" style="color: green;">
		Loading Data
	</div>

	<div id="re_order_searches_re_loading_page_message" style="color: green; display: none;">
		Re-loading page
	</div>	
	
	<div id="re_order_searches_main_data_block">	

	 <div >
	   <input onClick="searchReorder.doneSearchReorder()" type="button" 
	   		class="submit-button  " value="Done Re-ordering" >
	 </div>
	 <div id="re_order_searches_search_entries_block">	

	 </div>

	</div>
	
	<%--  Single Search Entry HTML Template --%>
			
	<script id="re_order_searches_single_search_template"  type="text/x-handlebars-template">

	 <%--  include the template text  --%>
	 <%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_includes/srchDsplyOrderAdmin/srchDsplyOrderAdminTmpl.jsp" %>

	</script>
	
  </div> <%-- Re-order searches Block   <div id="explore_data_re_order_searches_data_block">  --%>
  