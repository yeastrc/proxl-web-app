

<%--
	Display Details for a list of searches.
	
	Used on the Search Peptide, Search Protein, Merged Peptide, Merged Protein, 
	Merged Image, and Merged Structure pages

--%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

<c:forEach items="${ searches_details_list }" var="search_details"  varStatus="searchVarStatus">
	
	<c:set var="search" value="${ search_details.searchDTO }"></c:set>
	
	<c:if test="${ showSearchColorBlock }">
	
		<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
		<%@ include file="/WEB-INF/jsp-includes/mergedSearch_SearchIndexToSearchColorCSSClassName.jsp" %>
		
	</c:if>
	
	<div id="search_details_<bean:write name="search" property="id" />"
	  	><c:if test="${ showSearchColorBlock }"
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
								></span></a>&nbsp;<bean:write name="search" property="name" />&nbsp;(<bean:write name="search" property="id" />)</div>
	
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
				<td valign="top"  >
					Search 
					Program<c:if test="${ fn:length( search_details.searchPrograms ) > 1 }" >s</c:if>:
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
		</table>
	</div>
</c:forEach>