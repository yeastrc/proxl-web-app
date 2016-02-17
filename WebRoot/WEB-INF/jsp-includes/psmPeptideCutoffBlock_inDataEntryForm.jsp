

<%--    psmPeptideCutoffBlock_inDataEntryForm.jsp --%>

<%--    This file will be included inside the data entry form the user sees  --%>

<%--  The section at the top of the page with the cutoffs --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


<%--  TODO:  TEMP --%>

	<%--  use <c:set> since cannot have in page <style> inside a table --%>

	<c:set var="cutoff_input_psm_peptide_label" >padding-left: 10px; font-weight: bold;</c:set>
	<c:set var="cutoff_input_annotation_name">padding-left: 20px;</c:set>


<c:forEach var="cutoffPageDisplayRootPerSearchDataListEntry" items="${ cutoffPageDisplayRootRequestEntry.perSearchDataList  }">
		
	<%-- Spacer --%>  
	<tr>
		<td style="height: 6px;"></td>
	</tr>

	
	<tr>
		<td style="white-space: nowrap; padding-right: 5px;" valign="top">
			For Search ID: <c:out value="${ cutoffPageDisplayRootPerSearchDataListEntry.searchId }"></c:out> 
		</td>
	</tr>
	
	
	<tr>
	  <td class="  ">
	
	
		<div >
			PSM Filters:  display of entered filters here
		</div>
		
		<div >
			Peptide Filters:  display of entered filters here
		</div>
		
		<br>
		
		<div style="padding: 3px; border-style: solid; border-width: 2px; border-color: grey; ">
	
			<div style="padding: 3px; text-align: center;" >
				Displayed in an overlay
			</div>
			
			 <br>
			 
			 
			<table >
				<c:if test="${ not empty cutoffPageDisplayRootPerSearchDataListEntry.psmAnnotationCutoffData }" >
					<tr>
						<td style=" ${ cutoff_input_psm_peptide_label } ">
							PSM Filters:
						</td>
					</tr>
				</c:if>
				
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
							
						<td style="padding-right: 5px;">
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
								
								
						</td>
					</tr>
				
				</c:forEach>
		
				<%-- Spacer --%>  
				<tr>
					<td style="height: 6px;"></td>
				</tr>
						
				<c:if test="${ not empty cutoffPageDisplayRootPerSearchDataListEntry.peptideAnnotationCutoffData }" >
					<tr>
						<td style=" ${ cutoff_input_psm_peptide_label } ">
							Peptide Filters: 
						</td>
					</tr>
				</c:if>

				<c:forEach var="peptideAnnotationCutoffDataEntry" items="${ cutoffPageDisplayRootPerSearchDataListEntry.peptideAnnotationCutoffData  }">
			
					<c:set var="class_string_tool_tip_attached_jq"></c:set>
			
					<c:if test="${ not empty peptideAnnotationCutoffDataEntry.annotationDescription }">
						<c:set var="class_string_tool_tip_attached_jq"> tool_tip_attached_jq </c:set>
					</c:if>
						
				
					<tr class= " annotation_entry_root_tr_jq " style=""> <%-- REMOVED hidden initially, displayed via Javascript --%>
						<td>
							<span style="white-space: nowrap;  ${ cutoff_input_annotation_name } "
								class="   ${ class_string_tool_tip_attached_jq } "
								<c:if test="${ not empty peptideAnnotationCutoffDataEntry.annotationDescription }">
									data-tooltip="Annotation Description: <c:out value="${ peptideAnnotationCutoffDataEntry.annotationDescription }"></c:out>"
								</c:if>
								><c:out value="${ peptideAnnotationCutoffDataEntry.annotationName }"
								></c:out> (<c:out value="${ peptideAnnotationCutoffDataEntry.searchProgramDisplayName }"
							></c:out>)</span>
						</td>
						<td>
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
								
						</td>
					</tr>
					
				</c:forEach>

			</table>
			<br>
			Add "Save", "Cancel", and "Set to Defaults" buttons here
					
			
				
		</div>
	
		</td>
	</tr>
				
</c:forEach>				

<tr>
  <td>
	<div style="position: relative; ">
  		<div style="width: 400px;" class="error-message-container error_message_container_jq" id="error_message_cutoff_value_invalid">
  			<div class="error-message-inner-container" >
  				<div class="error-message-close-x error_message_close_x_jq">X</div>
	  			<div class="error-message-text" >Cutoff value is not valid</div>
  			</div>
	  	</div>
	</div>
		
  </td>
</tr>
