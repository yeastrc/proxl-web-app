
<%--    psmPeptideCutoffBlock_inCutoffOverlay.jsp --%>

<%--    This file will be included twice, once for "PSM" and once for "Peptide" cutoffs  --%>

<%--  The section at the top of the page with the cutoffs --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


 	<%--   PSM or Peptide Block --%>
 
<c:if test="${ not empty annotationCutoffDataList }" >
	<tr style=" vertical-align: top;"
		class=" cutoff_per_search_block_tr_jq "
		data-associated_overlay_container_id="${ filterTypeHTML_Id }_cutoffs_overlay_container__search_id_<bean:write name="search" property="projectSearchId" />"
	>
		<td style="  line-height: 1.3em; white-space: nowrap; <c:if test="${ not singleSearch }" > <%-- padding-top: 3px; padding-bottom: 3px; --%> width: 125px;</c:if> ">
		  <span style=" cursor: pointer;" class="tool_tip_attached_jq" data-tooltip="Edit filter values" 
		  	onclick="cutoffProcessingCommonCode.openCutoffOverlay( { clickedThis : this } )" >
			${ filterTypeDisplay } Filters:
			<img src="${contextPath}/images/icon-edit-small.png" onclick="cutoffProcessingCommonCode.openCutoffOverlay( { clickedThis : this } )" >
		  </span>
		</td>
		<td style=" line-height: 1.3em; <c:if test="${ not singleSearch }" > <%-- padding-top: 3px; padding-bottom: 3px; --%> </c:if>">
			<%--  div updated with values from overlay --%>
			<div id="${ filterTypeHTML_Id }_cutoffs_search_id_<bean:write name="search" property="projectSearchId" />" class=" ${ filterTypeHTML_Id }_cutoffs_jq "></div>
		</td>
	</tr>
	
	
	<tr class="table-no-border-no-cell-spacing-no-cell-padding">
		<td class="table-no-border-no-cell-spacing-no-cell-padding">  <%--  Cutoffs Overlay Holder --%>

			<%--  div that contains the overlay --%>
			<div id="${ filterTypeHTML_Id }_cutoffs_overlay_container__search_id_<bean:write name="search" property="projectSearchId" />" 
				class=" cutoffs_overlay_container_jq  ${ filterTypeHTML_Id }_cutoffs_overlay_container_jq "
				style="position: relative; ">

			
				<%--  Cutoffs Overlay Background --%>
				
				<div class="filter-cutoffs-modal-dialog-overlay-background  filter_cutoffs_modal_dialog_overlay_background_jq " style="display: none;"  >
				</div>
				
				<%--  Cutoffs Overlay Div --%>
				
				<div class=" filter-cutoffs-modal-dialog-overlay-div overlay-outer-div  filter_cutoffs_modal_dialog_overlay_div_jq " style="display: none; "  >
				
				
					<div class="filter-cutoffs-modal-dialog-overlay-header" style="width:100%; " >
						
						<h1 class="filter-cutoffs-modal-dialog-overlay-X-for-exit-overlay " 
							onclick="cutoffProcessingCommonCode.cancel_RestoreUserValues( { clickedThis : this } )"
							>X</h1>
						
						<h1 class="filter-cutoffs-modal-dialog-overlay-header-text" ><c:out value="${ filterTypeDisplay }"></c:out> Filters</h1>
					</div>
					<div class="filter-cutoffs-modal-dialog-overlay-body" >
				
						
						<%--  This div encloses the fields and the buttons and marks what is part of this overlay --%>
						<div 
							class=" cutoff_overlay_enclosing_block_jq "  <%--  filterTypeHTML_Id == 'psm' or 'peptide' --%>
							 data-associated_cutoffs_display_block_id="${ filterTypeHTML_Id }_cutoffs_search_id_<bean:write name="search" property="projectSearchId" />">
									 
						<table >
			<%-- 
								<tr>
									<td style="">
										${ filterTypeDisplay } Filters:
									</td>
								</tr>
			--%>				
							<c:forEach var="annotationCutoffDataEntry" items="${ annotationCutoffDataList  }">
							
								<c:set var="class_string_tool_tip_attached_jq"></c:set>
							
								<c:if test="${ not empty annotationCutoffDataEntry.annotationDescription }">
									<c:set var="class_string_tool_tip_attached_jq"> tool_tip_attached_jq </c:set>
								</c:if>
								
								<%--  TODO: consider adding searchProgramDisplayName to tooltip --%>
														
								<tr class= " annotation_entry_root_tr_jq " style="">
									<td>
										<span style="white-space: nowrap;   "
											class="   ${ class_string_tool_tip_attached_jq }  "
											<c:if test="${ not empty annotationCutoffDataEntry.annotationDescription }">
												data-tooltip="<c:out value="${ annotationCutoffDataEntry.annotationDescription }"></c:out>"
											</c:if>
										><c:out value="${ annotationCutoffDataEntry.annotationName }"
										></c:out> (<c:out value="${ annotationCutoffDataEntry.searchProgramDisplayName }"
										></c:out>)</span></td>
										
									<td style="padding-right: 5px;" >
									
									  <div class=" cutoff_input_field_block_jq ">
									  
										<input type="text" 
											id="annotation_cutoff_input_field_ann_id_${ annotationCutoffDataEntry.annotationTypeId }"
											class=" ${ filterTypeHTML_Id }_annotation_cutoff_input_field_jq  annotation_cutoff_input_field_jq <%--  ${ class_string_tool_tip_attached_jq } --%> " 
											data-type_id="${ annotationCutoffDataEntry.annotationTypeId }"
											data-project_search_id="${ cutoffPageDisplayRootPerSearchDataListEntry.projectSearchId }"
<%-- 											
											<c:if test="${ not empty annotationCutoffDataEntry.annotationDescription }">
												data-tooltip="<c:out value="${ annotationCutoffDataEntry.annotationDescription }"></c:out>"
											</c:if>
--%>
											onchange="cutoffProcessingCommonCode.inputFieldChanged( { fieldThis: this } )"
											value="<%--- <c:out value="${ annotationCutoffDataEntry.annotationValue }"></c:out> --%>" />
											
				
									
										<input type="hidden" class=" annotation_cutoff_default_value_field_jq " 
											value="<c:out value="${ annotationCutoffDataEntry.annotationDefaultValue }"></c:out>" >
			
										<input type="hidden" class=" annotation_display_name_field_jq "
											value="<c:out value="${ annotationCutoffDataEntry.annotationName }"
													></c:out> (<c:out value="${ annotationCutoffDataEntry.searchProgramDisplayName }"
												></c:out>)" > 
										
										<input type="hidden" class=" annotation_description_field_jq "
											value="<c:out value="${ annotationCutoffDataEntry.annotationDescription }"></c:out>" >
											
										<input type="hidden" class=" annotation_filter_direction_field_jq "
											value="<c:out value="${ annotationCutoffDataEntry.annotationFilterDirection }"></c:out>" >

										<c:if test="${ not empty annotationCutoffDataEntry.annotationCutoffOnImportValue }">
										  <%-- 
										  	This annotation has Cutoff on Import Value.  
										  --%>

										  <input type="hidden" class=" annotation_cutoff_on_import_value_field_jq "
											value="<c:out value="${ annotationCutoffDataEntry.annotationCutoffOnImportValue }"></c:out>" >
												
										</c:if>			
												 								
									  </div>
									  	
									</td>
									<td>
										<c:if test="${ not empty annotationCutoffDataEntry.annotationCutoffOnImportValue }">
										  <%-- 
										  	This annotation has Cutoff on Import Value.  
										  	Add messages for when:
										  	1) user blanks out the value
										  	2) user sets the value to beyond the cutoff on import value
										  --%>

										  <div class=" annotation_cutoff_missing_or_exceeds_cutoff_on_import_message_jq  error-text "
										  		style="display: none; " >
										  	value has to be 
										  	<c:choose>
										  	 <c:when test="${ annotationCutoffDataEntry. annotationFilterDirectionAbove }">
										  	   >= 
										  	 </c:when>
										  	 <c:otherwise>
										  	   <=
										  	 </c:otherwise>
										  	</c:choose>
										  	 
										  	<c:out value="${ annotationCutoffDataEntry.annotationCutoffOnImportValue }"></c:out>
										  </div>
										
										</c:if>		
										
										
										  <div class=" annotation_cutoff_not_number_message_jq  error-text "
										  		style="display: none; " >
										  	value has to be a decimal number 
										  </div>
									</td>									
								</tr>
							
							</c:forEach>
							
							</table>
							
							<div style="position: relative; ">
						  		<div style="width: 400px;" class="error-message-container error_message_container_jq  error_message_cutoff_value_invalid_jq" >
						  			<div class="error-message-inner-container" >
						  				<div class="error-message-close-x error_message_close_x_jq">X</div>
							  			<div class="error-message-text" >Cutoff value is not valid</div>
						  			</div>
							  	</div>
							</div>
		
							
				
							<input type="button" value="Save" 
								class=" save_user_cutoff_values_button_jq  "
								onclick="cutoffProcessingCommonCode.saveUserValues( { clickedThis : this } )"
								>
							<input type="button" value="Cancel" onclick="cutoffProcessingCommonCode.cancel_RestoreUserValues( { clickedThis : this } )" >
							<input type="button" value="Reset to Defaults" onclick="cutoffProcessingCommonCode.setToDefaultValues( { clickedThis : this } )" >
			
							
							</div>
					
						</div>
					</div>
						
				</div>


	</td>
</tr>
		
</c:if>
	