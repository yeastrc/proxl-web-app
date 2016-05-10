

<%--
	Display Details for a list of searches.
	
	Used on the Search Peptide, Search Protein, Merged Peptide, Merged Protein, 
	Merged Image, and Merged Structure pages

--%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<c:set var="singleSearch" value="${ true }" />

<c:if test="${ fn:length( searches_details_list ) > 1 }">
	<c:set var="singleSearch" value="${ false }" />
</c:if>

	<tr>
	 <td style="vertical-align: top;" >Search<c:if test="${ not singleSearch }" >es</c:if>:</td>
	 <td colspan="10">
	 
<%-- 	 
	 
<c:choose>
<c:when test="${ singleSearch }" >
	
	<tr>
	 <td style="vertical-align: top;" >Search:</td>
	 <td colspan="10">
		
 </c:when>
 <c:otherwise> 
	<tr>
	 <td style="vertical-align: top;" >Search<c:if test="${ not singleSearch }" >es</c:if>:</td>
	 <td colspan="10">
 </c:otherwise>	
</c:choose>		

--%>
		
  <c:forEach  var="search_details"  items="${ searches_details_list }" varStatus="searchVarStatus">
			
	<c:set var="search" value="${ search_details.searchDTO }"></c:set>
	
				
	 <table class="table-no-border-no-cell-spacing-no-cell-padding" style="border-width:0px;" >
	  <tr>
		<td style="vertical-align: top; padding-right: 3px;" >
		
			<c:if test="${ showSearchColorBlock }">
			
				<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
				<%@ include file="/WEB-INF/jsp-includes/mergedSearch_SearchIndexToSearchColorCSSClassName.jsp" %>
				
			</c:if> 
					<%--  Display Color block before the search if 'showSearchColorBlock' is true --%>
			<c:if test="${ showSearchColorBlock }" 
			  	><span style="margin-right:10px;padding-left:10px;padding-right:10px;" class="${ backgroundColorClassName }"></span
			  	></c:if
			  		><a href="javascript:"
					onclick="toggleVisibility(this)"
					 class="tool_tip_attached_jq" data-tooltip="Show or hide search details"
					toggle_visibility_associated_element_id="search_details_<bean:write name="search" property="id" />"
			  		><span class="toggle_visibility_expansion_span_jq" 
							><img src="${contextPath}/images/icon-expand-small.png" 
								class=" <%--  icon-expand-contract-in-data-table --%> "
								></span><span class="toggle_visibility_contraction_span_jq" 
									style=" display: none; " 
									><img src="${contextPath}/images/icon-collapse-small.png"
										class=" <%--  icon-expand-contract-in-data-table --%> "
										></span></a>
		</td>
		<td>
			
		 <table class="table-no-border-no-cell-spacing-no-cell-padding" style="border-width:0px;" >
		  <tr>
		   <td style="<c:if test="${ not singleSearch }" > padding-bottom: 2px;</c:if>">
			<div><bean:write name="search" property="name" />&nbsp;(<bean:write name="search" property="id" />)</div>

			<div id="search_details_<bean:write name="search" property="id" />"
					 
			  	>						
			</div>
			<div style="margin-left:20px;display:none;">
				<table style="border-width:0px;">
				  <c:if test="${ authAccessLevel.writeAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }" >
					<tr>
						<td>Path:</td>
						<td><bean:write name="search" property="path" /></td>
					</tr>
				  </c:if>
					<tr>
						<td>Linker:</td>
						<td><c:out value="${ search_details.linkersDisplayString }"></c:out></td>
					</tr>							
		
		
					<tr>
						<td style="vertical-align: top;"  >
							Search Program<c:if test="${ fn:length( search_details.searchPrograms ) > 1 }" >s</c:if>:
						</td>
						
						 <c:choose>
						  <c:when test="${ empty search_details.searchPrograms }">
							<td  style="padding-top: 2px;">
							  	Not Found
							</td>
						  </c:when>
						  <c:otherwise>
						    <td style="border-width:0px; padding: 0px;">
		
							<table  style="border-width:0px; border-spacing: 0px; ">
			
							   <c:forEach var="searchProgram" items="${ search_details.searchPrograms }">
							     <tr>
							      <td style="padding-right: 5px;">
							     	<c:out value="${ searchProgram.displayName }"></c:out>
							      </td>
							      <td >
							     	<c:out value="${ searchProgram.version }"></c:out>
							      </td>
							     </tr>
							   </c:forEach>
		
						    </table>
						  
							</td>
						  </c:otherwise> 
						 </c:choose>
					</tr>								  
												  
		
					<tr>
						<td>Upload&nbsp;date:</td>
						<td><bean:write name="search" property="formattedLoadTime" /></td>
					</tr>
					<tr>
						<td>FASTA&nbsp;file:</td>
						<td><bean:write name="search" property="fastaFilename" /></td>
					</tr>
					
					<c:if test="${ not empty search_details.cutoffsAppliedOnImportList }">
					  <tr>
							<td style="vertical-align: top; padding-top: 3px; padding-right: 5px; " >Cutoffs Applied On Import:</td>
							
						    <td style="border-width:0px; padding: 0px; padding-top: 3px;">
		
							<table  style="border-width:0px; border-spacing: 0px; ">
			
							   <c:forEach var="cutoffsAppliedOnImport" items="${ search_details.cutoffsAppliedOnImportList }">
							     <tr>
							      <td style="padding-right: 5px; ">
							       <c:choose>
							        <c:when test="${ cutoffsAppliedOnImport.peptideCutoff }">
							        	<span style="white-space: nowrap;" >Peptide Cutoff:</span>
							        </c:when>
							        <c:otherwise>
							        	<span style="white-space: nowrap;" >PSM Cutoff:</span>
							        </c:otherwise>
							       </c:choose>
							      </td>
							      <td style="padding-right: 5px;">
							     	<c:out value="${ cutoffsAppliedOnImport.annotationName }"></c:out>
							      </td>
							      <td >
							     	<c:out value="${ cutoffsAppliedOnImport.cutoffValue }"></c:out>
							      </td>
							     </tr>
							   </c:forEach>
		
						    </table>
						  
							</td>						
					  </tr>
					</c:if>
					
				</table>
			</div>  <%--  End of Search data that is shown on demand --%>
		   </td>
		  </tr>
		 </table>
		 

			 	<%--  Display Cutoff data --%>
			
	<c:if test="${ singleSearch }" >
	
		<%--  Single seach so close table to move search filter to outer table --%>
	
		</td>
		</tr>
		</table>
		</td>
		</tr>
	
	</c:if>
			
			<c:set var="cutoffPageDisplayRootPerSearchDataListEntry" value="${ search_details.cutoffPageDisplaySearchLevel  }" />
					
		
			
			<c:if test="${ not empty cutoffPageDisplayRootPerSearchDataListEntry }">
				 
<c:choose>
 <c:when test="${ singleSearch }" >
	

			  
			
	
 </c:when>
 <c:otherwise>

	<table class="table-no-border-no-cell-spacing-no-cell-padding" style="" >

 </c:otherwise>
</c:choose>		
				 
			  	<%--   PSM Block --%>
			  				 
					<c:set var="filterTypeDisplay" >PSM</c:set>
					<c:set var="filterTypeHTML_Id" >psm</c:set>
					<c:set var="annotationCutoffDataList" value="${ cutoffPageDisplayRootPerSearchDataListEntry.psmAnnotationCutoffData }"/> 
					
					<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inCutoffOverlay.jsp" %>
			
			  	<%--   Peptide Block --%>
			  	
					<c:set var="filterTypeDisplay" >Peptide</c:set>
					<c:set var="filterTypeHTML_Id" >peptide</c:set>
					<c:set var="annotationCutoffDataList" value="${ cutoffPageDisplayRootPerSearchDataListEntry.peptideAnnotationCutoffData }"/> 
		
					<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inCutoffOverlay.jsp" %>
						
				
 
<c:choose>
 <c:when test="${ singleSearch }" >
	

			  
			
	
 </c:when>
 <c:otherwise>

		</table>

 </c:otherwise>
</c:choose>				

	<%--  Handlebars template for displaying a Single Filter value --%>		

<script id="filter_single_value_display_template"  type="text/x-handlebars-template">
			
	<span class="filter-single-value-display-block {{#if data.description }} tool_tip_attached_jq {{/if}} " 
		{{#if data.description }} data-tooltip="{{ data.description }}" {{/if}}
				>{{ data.display_name }}: {{ data.value }}</span>

</script >		
	
			
			</c:if>		
			

	<c:if test="${ not singleSearch }" >
	
		<%--  Single seach so close table to move search filter to outer table --%>
					
		</td>
	  </tr>
	 </table>
	 
	 </c:if>

  </c:forEach>
		


							<div style="position: relative; ">
						  		<div style="width: 400px;" class="error-message-container error_message_container_jq " id="error_message_cutoff_value_invalid" >
						  			<div class="error-message-inner-container" >
						  				<div class="error-message-close-x error_message_close_x_jq">X</div>
							  			<div class="error-message-text" >Cutoff value is not valid</div>
						  			</div>
							  	</div>
							</div>
						
	 </td>
	</tr>
		