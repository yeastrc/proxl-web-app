<%--
		minimumPSM_Count_Filter.jsp
		
		Filter section of Protein, Merged Protein, Coverage, Image, and Structure Pages


--%>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>
<%@page import="org.yeastrc.xlink.www.constants.MinimumPSMsConstants"%>

<tr>
		<td>
			<div style="position: relative;">
				<%--  Minimum PSMs Overlay Background --%>
				
				<div id="minimum_psm_count_modal_dialog_overlay_background" class="minimum-psm-count-modal-dialog-overlay-background " style="display: none;"  >
				</div>
				
				<%--  Minimum PSMs Overlay Div --%>
				
				<div id="minimum_psm_count_modal_dialog_overlay_div" class=" minimum-psm-count-modal-dialog-overlay-div overlay-outer-div " style="display: none; "  > <%-- display: none --%>
				
				
					<div class="minimum-psm-count-modal-dialog-overlay-header" style="width:100%; " >
						
						<h1 class="minimum-psm-count-modal-dialog-overlay-X-for-exit-overlay " 
							onclick="minimumPSM_Count_Filter.cancel( { clickedThis : this } )"
							>X</h1>
						
						<h1 class="minimum-psm-count-modal-dialog-overlay-header-text" >Minimum PSM Filter</h1>
					</div>
					<div class="minimum-psm-count-modal-dialog-overlay-body" >
						
						<%--  This div encloses the fields and the buttons and marks what is part of this overlay --%>
						<div >
						  	<div style="margin-bottom: 5px;">
								<span class="tool_tip_attached_jq  "
			 	 					data-tooltip="Cross-links or loop-links must have at least this number of PSMs to be shown"
			 	 						> Minimum PSMs:</span> 
			 	 				<input id="minimum_psm_count_user_input" style="width: 40px;">
							</div>

							<input type="button" value="Save" 
								onclick="minimumPSM_Count_Filter.saveUserValues( { clickedThis : this } )"
								>
							<input type="button" value="Cancel" onclick="minimumPSM_Count_Filter.cancel( { clickedThis : this } )" >
							<input type="button" value="Reset to Default" onclick="minimumPSM_Count_Filter.reset( { clickedThis : this } )" >
										
						</div>
					</div>
				</div>		
			</div>
		
		<span class="tool_tip_attached_jq  "
			data-tooltip="Cross-links or loop-links must have at least this number of PSMs to be shown"
			onclick="minimumPSM_Count_Filter.openOverlay();"
			>Minimum PSMs: <img src="images/icon-edit-small.png" onclick="minimumPSM_Count_Filter.openOverlay()"></span>
			
		 <script type="text/text" id="minimum_psm_count_default_value"><%= MinimumPSMsConstants.MINIMUM_PSMS_DEFAULT %></script>
			
		</td>
		<td>
		  
		  <c:choose>
		    <c:when test="${ peptidePage or mergedPeptidePage }">
		       <c:set var="minimum_psm_count_value_tooltip">Peptides must have at least this number of PSMs to be shown</c:set>
		    </c:when>
		    <c:otherwise>
		       <c:set var="minimum_psm_count_value_tooltip">Cross-links or loop-links must have at least this number of PSMs to be shown</c:set>
		    </c:otherwise>
		  
		  </c:choose>
		  
			 <span id="minimum_psm_count_current_value_display_when_value_default" 
			 	class="filter-single-value-display-block  tool_tip_attached_jq  "
			 	 data-tooltip="<c:out value="${ minimum_psm_count_value_tooltip }"/>" 
			 	 style=" cursor: pointer; display: none;" 
			 	 onclick="minimumPSM_Count_Filter.openOverlay()" 
			 	 >Showing All</span>
					
			 <span id="minimum_psm_count_current_value_display" 
			 	class="filter-single-value-display-block  tool_tip_attached_jq  "
			 	 data-tooltip="<c:out value="${ minimum_psm_count_value_tooltip }"/>" 
			 	 style=" cursor: pointer; display: none;" 
			 	 onclick="minimumPSM_Count_Filter.openOverlay()" 
			 	 ></span>
					 	 
		</td>
	</tr>
