
<%--    annotationDisplayBlock_PsmPeptide_SubPart.jsp --%>

<%--    This file will be included twice, once for "PSM" and once for "Peptide" Annotation Display  --%>

<%--  The section at the top of the page with the cutoffs --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>


 	<%--   PSM or Peptide Block --%>
 	

<%--  block container div --%>

<div id="${ filterTypeHTML_Id }_annotation_displays_container__search_id_${ search.projectSearchId }" 
	class=" annotation_displays_container_jq  ${ filterTypeHTML_Id }_annotation_displays_container_jq "
	data-project_search_id="${ search.projectSearchId }"
	data-psm_peptide_type="${ filterTypeHTML_Id }"
	style="">

	<div style="font-size: 18px; font-weight: bold; text-align: center; margin-top: 10px; margin-bottom: 10px;">
		${ filterTypeDisplay } Data
	</div>

  <table class="table-no-border-no-cell-spacing-no-cell-padding" style="border-width:0px; margin-bottom: 10px; width: 100%;" >
   <tr>
    <td style="width: 50%;   vertical-align: top;">  <%--  Left Cell where user chooses which Annotation Types to display --%>
	
	 <div style="margin-bottom: 10px; ">
		Click on items to display
	 </div>
	</td>
	<td style="width: 50%;   vertical-align: top;" >

		<div style="margin-bottom: 10px; ">
		  <div >
			Drag the items to the order to display them in.
		  </div>
		  <div>
		    Click the <img src="${ contextPath }/images/icon-delete-small.png" > to remove.
		  </div>
		</div>
	</td>
   <tr>
    <td style="padding-right: 10px; vertical-align: top;">

	 <%--  This div encloses the selection items and the buttons and marks what is part of this overlay --%>
	 <div 
		class=" annotation_display_overlay_enclosing_block_jq "  <%--  filterTypeHTML_Id == 'psm' or 'peptide' --%>
		data-associated_ann_data_display_block_id="${ filterTypeHTML_Id }_annotation_displays_search_id_${ search.projectSearchId }"
		>
				 
		<%-- Data Display Selection Items.  Each is Click to Select --%>
	  <div class=" annotation_display_select_items_container_jq data-list-container">
	  
		<c:forEach var="annotationTypeDisplayDataEntry" items="${ annotationDisplayDataList  }">
		
			<c:set var="class_string_tool_tip_attached_jq"></c:set>
		
			<c:if test="${ not empty annotationTypeDisplayDataEntry.annotationTypeDTO.description }">
				<c:set var="class_string_tool_tip_attached_jq"> tool_tip_attached_jq </c:set>
			</c:if>
			
			<%--  TODO: consider adding searchProgramDisplayName to tooltip --%>
									
			<div class=" annotation_data_single_entry_jq clickable annotation-data-display-single-data-select-item "
				style="white-space: nowrap;   " 
				onclick="annotationDataDisplayProcessingCommonCode.dataEntryClicked( { clickedThis : this } )"
				data-annotation_type_id="${ annotationTypeDisplayDataEntry.annotationTypeDTO.id }"
				data-annotation_name="<c:out value="${ annotationTypeDisplayDataEntry.annotationTypeDTO.name }"
					></c:out> (<c:out value="${ annotationTypeDisplayDataEntry.searchProgramPerSearchDTO.displayName }"
					></c:out>)"
				data-annotation_description="<c:out value="${ annotationTypeDisplayDataEntry.annotationTypeDTO.description }"></c:out>"
				>
					<div style="white-space: nowrap; "
						class="  ${ class_string_tool_tip_attached_jq } ann_data_name_text_jq  "
						<c:if test="${ not empty annotationTypeDisplayDataEntry.annotationTypeDTO.description }">
							data-tooltip="<c:out value="${ annotationTypeDisplayDataEntry.annotationTypeDTO.description }"></c:out>"
						</c:if>
					><c:out value="${ annotationTypeDisplayDataEntry.annotationTypeDTO.name }"
					></c:out> (<c:out value="${ annotationTypeDisplayDataEntry.searchProgramPerSearchDTO.displayName }"
					></c:out>)</div
				>
					
			</div>
		
		</c:forEach>
		
	  </div>

	 </div>

    </td>
    <td style="width: 50%; padding-left: 10px;  vertical-align: top;">  <%--  Right Cell where user chooses the order of the Annotation Types to display --%>
	    
		
		<%--  This div encloses the selection items and the buttons and marks what is part of this overlay --%>
		<div 
			class=" annotation_display_overlay_enclosing_block_jq "  <%--  filterTypeHTML_Id == 'psm' or 'peptide' --%>
			data-associated_ann_data_display_block_id="${ filterTypeHTML_Id }_annotation_displays_search_id_${ search.projectSearchId }"
			>
					 
			<%-- Data Display Sort Items.  Each is Drag to re-order --%>
		  <div class=" annotation_display_order_items_container_jq data-list-container">
		  
			
		  </div>
	
		</div>
		 					    
    </td>
   </tr>
  </table>

</div>						  
