<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


		<%-- !!!   Handlebars template   !!!!!!!!!   --%>
		
	<%--  proxlXMLFileImportItemDetails.jsp --%>
	
	<%--  Scan File Upload Details Template --%>



<%-- 
containing <script> id:  proxl_xml_import_item_row_details_template
--%>

 <tr  class=" proxl_xml_import_item_details_row_jq " style="display: none;">
  <td>
  </td>
  <td>
  </td>
  <td colspan="{{ numColsMinusTwo }}" <%-- colspan so occupies all but first column --%>
		style="padding-top: 10px; padding-left: 25px;">
			
	 <div > <%-- width: applied here doesn't work.  set width on tooltip using css.   style="width: 400px;" --%>
	    <table class="upload-search-tooltip-table">
		  <tr>
	  	 	<td class="label-cell">File:</td>
			<td>{{ uploadedFilename }}</td>
		  </tr>
		  
		  <c:if test="${ configSystemValues.scanFileImportAllowedViaWebSubmit }" >
		  <tr>
	  	 	<td class="label-cell">Scan file(s):</td>
	  	 	{{#if scanfileNamesCommaDelim }}
				<td>{{ scanfileNamesCommaDelim }}</td>
			{{else}}
				<td>None</td>
			{{/if}}	 
		  </tr>
		  </c:if>
		  
		  
	 	{{#if searchName }}
		  <tr>
	  	 	<td class="label-cell">Search description:</td>
			<td>{{ searchName }}</td>
		  </tr>
	 	{{/if}}	  
	 	
	 	
		  <tr>
	  	 	<td class="label-cell">Uploaded by:</td>
			<td>{{ nameOfUploadUser }}</td>
		  </tr>	 
		  
		  <tr>  <%--  Empty line to create space --%>
	  	 	<td style="height: 10px;">&nbsp;</td>
	  	  </tr>
	  	  	  
		  <tr>
	  	 	<td class="label-cell">Submitted:</td>
			<td>{{ importSubmitDateTime }}</td>
		  </tr>
		  
		  {{#if importStartDateTime }}
		   <tr>
	  	 	<td class="label-cell">Started Processing:</td>
			<td>{{ importStartDateTime }}</td>
		   </tr>
		  {{/if}}	
	
		  {{#if importEndDateTime }}
		 	{{#if statusComplete }}
			   <tr>
		  	 	<td class="label-cell">Imported:</td>
				<td>{{ importEndDateTime }}</td>
			   </tr>
			{{/if}}	
		 	{{#if statusFailed }}
			   <tr>
		  	 	<td class="label-cell">Failed:</td>
				<td>{{ importEndDateTime }}</td>
			   </tr>
			{{/if}}	
	  	  {{/if}}
	
	
		{{#if statusFailedMsg }}
	 		   <tr>  <%--  Empty line to create space --%>
		  	 	<td style="height: 10px;">&nbsp;</td>
		  	   </tr>
	 		   <tr>
		  	 	<td class="label-cell ">Fail reason:</td>
				<td >{{ statusFailedMsg }}</td>
			   </tr>
		{{/if}}		   
			  
	   </table>
	</div>

 </td>
</tr>	