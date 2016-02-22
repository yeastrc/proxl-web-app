

<%--
	Display Details for a list of searches.
	
	Used on the Search Peptide, Search Protein, Merged Peptide, Merged Protein, 
	Merged Image, and Merged Structure pages

--%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>



<%--  TODO:  TEMP --%>

	<%--  use <c:set> since cannot have in page <style> inside a table --%>

	<c:set var="cutoff_input_psm_peptide_label" >padding-left: 10px; font-weight: bold;</c:set>
	<c:set var="cutoff_input_annotation_name">padding-left: 20px;</c:set>



<c:forEach items="${ searches_details_list }" var="search_details"  varStatus="searchVarStatus">
	
	<c:set var="search" value="${ search_details.searchDTO }"></c:set>
	
	<c:if test="${ showSearchColorBlock }">
	
		<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
		<%@ include file="/WEB-INF/jsp-includes/mergedSearch_SearchIndexToSearchColorCSSClassName.jsp" %>
		
	</c:if> 
	
	<div id="search_details_<bean:write name="search" property="id" />"
			 <%--  Display Color block before the search if 'showSearchColorBlock' is true --%>
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

	 	<%--  Display Cutoff data --%>
	

	<c:set var="cutoffPageDisplayRootPerSearchDataListEntry" value="${ search_details.cutoffPageDisplaySearchLevel  }" />
			

	
	<c:if test="${ not empty cutoffPageDisplayRootPerSearchDataListEntry }">
		 
	  <table >
	  	
	  	<%--   PSM Block --%>
	  
		<c:if test="${ not empty cutoffPageDisplayRootPerSearchDataListEntry.psmAnnotationCutoffData }" >
			<tr>
				<td style=" ${ cutoff_input_psm_peptide_label } ">
					PSM Filters:
					<a href="javascript:" >
						<img src="/proxl_generic/images/icon-edit-small.png" 
							onclick="alert('Under Construction')"
							class="tool_tip_attached_jq" data-tooltip="Edit filter values" 
							>
					</a>
				</td>
				<td >
					<%--  div updated with values from overlay --%>
					<div id="psm_cutoffs_search_id_<bean:write name="search" property="id" />" class=" psm_cutoffs_jq "></div>
				</td>
			</tr>
			<tr>
				<td>  <%--  PSM Cutoffs Overlay Holder --%>


		<div style="padding: 3px; border-style: solid; border-width: 2px; border-color: grey; ">
	
			<div style="padding: 3px; text-align: center;" >
				Displayed in an overlay
			</div>
			
			<%--  This div encloses the fields and the buttons and marks what is part of this overlay --%>
			<div
				class=" cutoff_overlay_enclosing_block_jq "
				 data-associated_cutoffs_display_block_id="psm_cutoffs_search_id_<bean:write name="search" property="id" />">
						 
			<table >
					<tr>
						<td style=" ${ cutoff_input_psm_peptide_label } ">
							PSM Filters:
						</td>
					</tr>
				
				<c:forEach var="psmAnnotationCutoffDataEntry" items="${ cutoffPageDisplayRootPerSearchDataListEntry.psmAnnotationCutoffData  }">
				
					<c:set var="class_string_tool_tip_attached_jq"></c:set>
				
					<c:if test="${ not empty psmAnnotationCutoffDataEntry.annotationDescription }">
						<c:set var="class_string_tool_tip_attached_jq"> tool_tip_attached_jq </c:set>
					</c:if>
					
					<%--  TODO: consider adding searchProgramDisplayName to tooltip --%>
											
					<tr class= " annotation_entry_root_tr_jq " style="">
						<td>
							<span style="white-space: nowrap;  ${ cutoff_input_annotation_name } "
								class="   ${ class_string_tool_tip_attached_jq }  "
								<c:if test="${ not empty psmAnnotationCutoffDataEntry.annotationDescription }">
									data-tooltip="Annotation Description: <c:out value="${ psmAnnotationCutoffDataEntry.annotationDescription }"></c:out>"
								</c:if>
							><c:out value="${ psmAnnotationCutoffDataEntry.annotationName }"
							></c:out> (<c:out value="${ psmAnnotationCutoffDataEntry.searchProgramDisplayName }"
							></c:out>)</span></td>
							
						<td style="padding-right: 5px;" >
						
						  <div class=" cutoff_input_field_block_jq ">
						  
							<input type="text" 
								id="annotation_cutoff_input_field_ann_id_${ psmAnnotationCutoffDataEntry.annotationTypeId }"
								class=" psm_annotation_cutoff_input_field_jq  annotation_cutoff_input_field_jq  ${ class_string_tool_tip_attached_jq }" 
								data-type_id="${ psmAnnotationCutoffDataEntry.annotationTypeId }"
								data-search_id="${ cutoffPageDisplayRootPerSearchDataListEntry.searchId }"
								<c:if test="${ not empty psmAnnotationCutoffDataEntry.annotationDescription }">
									data-tooltip="Annotation Description: <c:out value="${ psmAnnotationCutoffDataEntry.annotationDescription }"></c:out>"
								</c:if> 
								value="<%--- <c:out value="${ psmAnnotationCutoffDataEntry.annotationValue }"></c:out> --%>" />						
						
						<%-- 
						<html:text property="psmQValueCutoff" styleId="psmQValueCutoff" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:text></td>
						--%> 
							
							<input type="hidden" class=" annotation_cutoff_default_value_field_jq " 
								value="<c:out value="${ psmAnnotationCutoffDataEntry.annotationDefaultValue }"></c:out>" >

									<input type="hidden" class=" annotation_display_name_field_jq "
										value="<c:out value="${ psmAnnotationCutoffDataEntry.annotationName }"
												></c:out> (<c:out value="${ psmAnnotationCutoffDataEntry.searchProgramDisplayName }"
											></c:out>)" > 
									
									<input type="hidden" class=" annotation_description_field_jq "
										value="<c:out value="${ psmAnnotationCutoffDataEntry.annotationDescription }"></c:out>" > 								
						  </div>
						  	
						</td>
					</tr>
				
				</c:forEach>
				
				</table>
	
				<input type="button" value="Save" onclick="cutoffProcessingCommonCode.saveUserValues( { clickedThis : this } )" >
				<input type="button" value="Cancel" onclick="cutoffProcessingCommonCode.cancel_RestoreUserValues( { clickedThis : this } )" >
				<input type="button" value="Reset to Defaults" onclick="cutoffProcessingCommonCode.setToDefaultValues( { clickedThis : this } )" >

				
				</div>
				
			<div style="color:red; font-size: 18pt">
				Should "Reset to Defaults" immediately close the dialog?  
				If not, should clicking "Cancel" remove the effect of clicking "Reset to Defaults"?
				It is coded to not close the dialog and not change the values if "Cancel" is clicked.  
			</div>
			
				
			</div>
			<br>


				</td>
			</tr>
				
		</c:if>
	

	  	<%--   Peptide Block --%>
	  
		<c:if test="${ not empty cutoffPageDisplayRootPerSearchDataListEntry.peptideAnnotationCutoffData }" >

			<tr>
				<td style=" ${ cutoff_input_psm_peptide_label } ">
					Peptide Filters: 
					<a href="javascript:" >
						<img src="/proxl_generic/images/icon-edit-small.png" 
							onclick="alert('Under Construction')"
							class="tool_tip_attached_jq" data-tooltip="Edit filter values" 
							>
					</a>
				</td>
				<td >
					<%--  div updated with values from overlay --%>
					<div id="peptide_cutoffs_search_id_<bean:write name="search" property="id" />" class=" psm_cutoffs_jq "></div>
				</td>
			</tr>
			<tr>
				<td>  <%--  Peptide Cutoffs Overlay Holder --%>


		<div style="padding: 3px; border-style: solid; border-width: 2px; border-color: grey; ">
	

			<div style="padding: 3px; text-align: center;" >
				Displayed in an overlay
			</div>

			<%--  This div encloses the fields and the buttons and marks what is part of this overlay --%>
			<div
				class=" cutoff_overlay_enclosing_block_jq "
				 data-associated_cutoffs_display_block_id="peptide_cutoffs_search_id_<bean:write name="search" property="id" />">
						 
			<table >			
				<tr>
					<td style=" ${ cutoff_input_psm_peptide_label } ">
						Peptide Filters: 
					</td>
				</tr>

				<c:forEach var="peptideAnnotationCutoffDataEntry" items="${ cutoffPageDisplayRootPerSearchDataListEntry.peptideAnnotationCutoffData  }">
			
					<c:set var="class_string_tool_tip_attached_jq"></c:set>
			
					<c:if test="${ not empty peptideAnnotationCutoffDataEntry.annotationDescription }">
						<c:set var="class_string_tool_tip_attached_jq"> tool_tip_attached_jq </c:set>
					</c:if>
						
				
					<tr class= " annotation_entry_root_tr_jq " style=""> <%-- REMOVED hidden initially, displayed via Javascript --%>
						<td>
							<span style="white-space: nowrap;  ${ cutoff_input_annotation_name } "
								class=" annotation_type_name_string_jq  ${ class_string_tool_tip_attached_jq } "
								<c:if test="${ not empty peptideAnnotationCutoffDataEntry.annotationDescription }">
									data-tooltip="Annotation Description: <c:out value="${ peptideAnnotationCutoffDataEntry.annotationDescription }"></c:out>"
								</c:if>
								><c:out value="${ peptideAnnotationCutoffDataEntry.annotationName }"
								></c:out> (<c:out value="${ peptideAnnotationCutoffDataEntry.searchProgramDisplayName }"
							></c:out>)</span>
						</td>
						<td>
						  <div class=" cutoff_input_field_block_jq ">
							<input type="text" 
								id="annotation_cutoff_input_field_ann_id_${ peptideAnnotationCutoffDataEntry.annotationTypeId }"
								class=" peptide_annotation_cutoff_input_field_jq  annotation_cutoff_input_field_jq  ${ class_string_tool_tip_attached_jq } " 
								data-type_id="${ peptideAnnotationCutoffDataEntry.annotationTypeId }"  
								data-search_id="${ cutoffPageDisplayRootPerSearchDataListEntry.searchId }"  
								<c:if test="${ not empty peptideAnnotationCutoffDataEntry.annotationDescription }">
									data-tooltip="Annotation Description: <c:out value="${ peptideAnnotationCutoffDataEntry.annotationDescription }"></c:out>"
								</c:if>
								value="<%--- <c:out value="${ peptideAnnotationCutoffDataEntry.annotationValue }"></c:out> --%>" />
								
									<input type="hidden" class=" annotation_cutoff_default_value_field_jq " 
										value="<c:out value="${ peptideAnnotationCutoffDataEntry.annotationDefaultValue }"></c:out>" >
										
									<input type="hidden" class=" annotation_display_name_field_jq "
										value="<c:out value="${ peptideAnnotationCutoffDataEntry.annotationName }"
												></c:out> (<c:out value="${ peptideAnnotationCutoffDataEntry.searchProgramDisplayName }"
											></c:out>)" > 
									
									<input type="hidden" class=" annotation_description_field_jq "
										value="<c:out value="${ peptideAnnotationCutoffDataEntry.annotationDescription }"></c:out>" > 
						  </div>
						</td>
					</tr>
					
				</c:forEach>

			</table>
			<br>
				<input type="button" value="Save" onclick="cutoffProcessingCommonCode.saveUserValues( { clickedThis : this } )" >
				<input type="button" value="Cancel" onclick="cutoffProcessingCommonCode.cancel_RestoreUserValues( { clickedThis : this } )" >
				<input type="button" value="Reset to Defaults" onclick="cutoffProcessingCommonCode.setToDefaultValues( { clickedThis : this } )" >

			</div>
			
			<div style="color:red; font-size: 18pt">
				Should "Reset to Defaults" immediately close the dialog?  
				If not, should clicking "Cancel" remove the effect of clicking "Reset to Defaults"?
				It is coded to not close the dialog and not change the values if "Cancel" is clicked.  
			</div>
			
			
			</div>

				</td>
			</tr>
				
		</c:if>					
			
				
		</table>


	<%--  Handlebars template for displaying a Single Filter value --%>		

	<script id="filter_single_value_display_template"  type="text/x-handlebars-template">
		
		<span class="filter-single-value-display-block">{{ data.display_name }}: {{ data.value }} </span>

	</script >		
	
	
	</c:if>
	
</c:forEach>