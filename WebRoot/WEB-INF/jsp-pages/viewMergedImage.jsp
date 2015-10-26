<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">View Search</c:set>

 <c:set var="pageBodyClass" >project-page view-merged-image-page</c:set>

 <c:set var="headerAdditions">
 
		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.tablesorter.min.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/libs/spin.min.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/libs/base64.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.qtip.min.js"></script>
		

		
		<!-- Handlebars templating library   -->
		
		<%--  
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.js"></script>
		--%>
		
		<!-- use minimized version  -->
		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.min.js"></script>


		
		
<%--  Start of Lorikeet Core Parts --%>		

		<script src="${contextPath}/js/libs/jquery-ui-1.10.4.min.js"></script>
		
		<%--  Only load the excanvas.min.js if it is IE 8 or lower.  IE 8 does not support HTML5 so this is a way to have HTML5 canvas support --%>
		<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="${contextPath}/js/lorikeet_google_code/excanvas.min.js"></script><![endif]-->
		
		<script src="${contextPath}/js/lorikeet/jquery.flot.js"></script>
		<script src="${contextPath}/js/lorikeet/jquery.flot.selection.js"></script>
		
		<script src="${contextPath}/js/lorikeet/specview.js"></script>
		<script src="${contextPath}/js/lorikeet/peptide.js"></script>
		<script src="${contextPath}/js/lorikeet/aminoacid.js"></script>
		<script src="${contextPath}/js/lorikeet/ion.js"></script>		
		
<%--  End of Lorikeet Core Parts --%>		

		
			<%--  On this page also used by crosslink-image-viewer.js and crosslink-image-viewer-click-element-handlers.js --%>				
		<script type="text/javascript" src="${ contextPath }/js/libs/snap.svg-min.js"></script> <%--  Used by lorikeetPageProcessing.js --%>
				
		<script type="text/javascript" src="${ contextPath }/js/lorikeetPageProcessing.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/nagWhenFormChangedButNotUpdated.js"></script>				

		
		
		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js"></script> 
		 
		<script type="text/javascript" src="${ contextPath }/js/spinner.js"></script> 
		
		<script type="text/javascript" src="${ contextPath }/js/trypsinCutPointsForSequence.js"></script>
		 
		<script type="text/javascript" src="${ contextPath }/js/crosslink-image-viewer-protein-annotation.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/crosslink-image-viewer.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/crosslink-image-viewer-click-element-handlers.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/defaultPageView.js"></script>
		
		
		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/viewPsmsLoadedFromWebServiceTemplate.js"></script>
		
		<link rel="stylesheet" href="${ contextPath }/css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />
		
		<%--  some classes in this stylesheet collide with some in the lorikeet file since they are set to specific values for lorikeet drag and drop --%>
		<%-- 
		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/jquery-ui-1.10.2-Themes/ui-lightness/jquery-ui.min.css">
		--%>
		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/lorikeet.css">
		




</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>
	
		<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		
		
		
	<div class="overall-enclosing-block">
	
	<input type="hidden" id="annotation_data_webservice_base_url" value="<c:out value="${ annotation_data_webservice_base_url }"></c:out>"> 

	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
	<c:forEach var="searchId" items="${ searchIds }">
	
		<%--  Put Search Ids on the page for the JS code --%>
		<input type="hidden" class=" search_id_jq " value="<c:out value="${ searchId }"></c:out>">
	</c:forEach>
	
	<c:if test="${ not empty onlySingleSearchId }">
	
		<input class="tool_tip_attached_jq" data-tooltip="View peptides" type="hidden" id="viewSearchPeptideDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="viewSearchPeptide.do" searchId="${ onlySingleSearchId }"></proxl:defaultPageUrl>">
		<input class="tool_tip_attached_jq" data-tooltip="View proteins" type="hidden" id="viewSearchCrosslinkProteinDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="viewSearchCrosslinkProtein.do" searchId="${ onlySingleSearchId }"></proxl:defaultPageUrl>">
		<input class="tool_tip_attached_jq" data-tooltip="View protein coverage report" type="hidden" id="viewProteinCoverageReportDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="viewProteinCoverageReport.do" searchId="${ onlySingleSearchId }"></proxl:defaultPageUrl>">
		<input class="tool_tip_attached_jq" data-tooltip="View data on 3D structures" type="hidden" id="viewMergedStructureDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="viewMergedStructure.do" searchId="${ onlySingleSearchId }"></proxl:defaultPageUrl>">
	</c:if>
					
	
		<div>
	
			<h2 style="margin-bottom:5px;">View <span id="merged_label_text_header" style="display: none;" >merged </span>search results:</h2>
	
			<div id="navigation-links">
			
				<span id="navigation_links_except_structure"></span>
				

				<c:choose>
				 <c:when test="${ showStructureLink }">
					
					<span id="structure_viewer_link_span"></span>
	
				 </c:when>
				 <c:otherwise>
				 	
										 	
					<%@ include file="/WEB-INF/jsp-includes/structure_link_non_link.jsp" %>
										 	
				 </c:otherwise>
				</c:choose>
								
			</div>
	
			<form action="#">
	
			<table style="border-width:0px;">
				<tr>
					<td valign="top">Searches:</td>
					<td>
						<%--  Set to false to NOT show color block before search for key --%>
						<c:set var="showSearchColorBlock" value="${ false }" />
						
						<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
						<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>
					</td>
				</tr>


				<tr>
					<td style="white-space: nowrap">PSM Q-value cutoff:</td>
					<td>
					
					  <div style="position: relative;">
				  		<div class="error-message-container error_message_container_jq" id="error_message_invalid_psm_q_value_cutoff">
				  			<div class="error-message-inner-container" style="width: 400px;" >
				  				<div class="error-message-close-x error_message_close_x_jq">X</div>
					  			<div class="error-message-text" >Invalid value for PSM q-value cutoff.</div>
				  			</div>
					  	</div>
					  </div>
									
						<input type="text" style="width:40px;" id="psmQValueCutoff">
					</td>
				</tr>
				
				<tr>
					<td style="white-space: nowrap">Peptide Q-value cutoff:</td>
					<td>
					  <div style="position: relative;">
				  		<div class="error-message-container error_message_container_jq" id="error_message_invalid_peptide_q_value_cutoff">
				  			<div class="error-message-inner-container" style="width: 400px;" >
				  				<div class="error-message-close-x error_message_close_x_jq">X</div>
					  			<div class="error-message-text" >Invalid value for Peptide q-value cutoff.</div>
				  			</div>
					  	</div>
					  </div>
												
						<input type="text" style="width:40px;" id="peptideQValueCutoff">
					</td>
				</tr>
				
				<tr>
					<td>Exclude xlinks with:</td>
					<td>
						 <label><span style="white-space:nowrap;" >
						 	<input type="checkbox" id="filterNonUniquePeptides">
						 	 no unique peptides
						 </span></label>
						 <label><span style="white-space:nowrap;" >
						 	<input type="checkbox" id="filterOnlyOnePSM">
						 	 only one PSM
						 </span></label>
						 <label><span style="white-space:nowrap;" >
						 	<input type="checkbox" id="filterOnlyOnePeptide">
						 	 only one peptide
						 </span></label>
					</td>
				</tr>
				
				<tr>
					<td>Exclude proteins with:</td>
					<td id="exclude_protein_types_block">
						<label><span style="white-space:nowrap;" ><input type="checkbox" id="exclude-type-4">Crosslinks</span></label>
						<label><span style="white-space:nowrap;" ><input type="checkbox" id="exclude-type-2">Looplinks</span></label>
						<label><span style="white-space:nowrap;" ><input type="checkbox" id="exclude-type-1">Monolinks</span></label>
						<%-- <label><span style="white-space:nowrap;" ><input type="checkbox" id="exclude-type-3">Dimers</span></label> --%>
						<label><span style="white-space:nowrap;" ><input type="checkbox" id="exclude-type-0">No links</span></label>		
					</td>
				</tr>

				<tr>
					<td>Exclude organisms:</td>
					<td><div id="taxonomy-checkboxes"></div></td>
				</tr>
				
				
				<tr>
					<td>&nbsp;</td>
					<td>
						<input type="button" value="Update From Database" onClick="javascript:refreshData()">
				
						<c:if test="${ authAccessLevel.projectOwnerAllowed }" >
							<input type="button" value="Save As Default" style="display: none;" id="mergedImageSaveOrUpdateDefaultPageView"
								onclick="mergedImageSaveOrUpdateDefaultPageView__( this )">
						</c:if>
					</td>
				</tr>
			
			</table>
			</form>
			
		</div>
	
			
		<hr>

		<div>
			<div>
				
				<div >

					<span class="tool_tip_attached_jq" data-tooltip="Add a selector for adding another protein bar to the image" style="margin-left:10px;" id="svg-protein-selector_location"><a href="javascript:addProteinSelect()">+Protein</a></span>
				</div>





				<div style="margin-top: 5px;">
					
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing inter-protein crosslinks" style="white-space:nowrap;" ><input type="checkbox" id="show-crosslinks" checked>Show crosslinks</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing intra-protein crosslinks" style="white-space:nowrap;" ><input type="checkbox" id="show-self-crosslinks" checked>Show self-crosslinks</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing looplinks" style="white-space:nowrap;" ><input type="checkbox" id="show-looplinks" checked>Show looplinks</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing monolinks" style="white-space:nowrap;" ><input type="checkbox" id="show-monolinks">Show monolinks</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle marking possible positions in protein where crosslinker(s) may react" style="white-space:nowrap;" ><input type="checkbox" id="show-linkable-positions">Show linkable positions</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle marking trypic positions in proteins" style="white-space:nowrap;" ><input type="checkbox" id="show-tryptic-cleavage-positions">Show tryptic positions</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing protein termini labels" style="white-space:nowrap;" ><input type="checkbox" id="show-protein-termini">Show protein termini</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle shading of links based on spectrum counts" style="white-space:nowrap;" ><input type="checkbox" id="shade-by-counts">Shade by counts</span></label>
					<label id="color_by_search_outer_container">
						<span class="tool_tip_attached_jq" data-tooltip="Toggle coloring of links based on search in which it was found" style="white-space:nowrap;"   >
							<input type="checkbox" id="color-by-search" >Color by search
						</span>
					</label>
					<%-- disabled version for when the number of searches exceeds the number supported by 'Color by search' --%>
					<label id="color_by_search_disabled_outer_container"  style="white-space:nowrap; display: none;" >
						<span style="white-space:nowrap;" 
								class="disabled-checkbox tool_tip_attached_jq"
								data-tooltip="'Color by search' unavailable for more than 3 searches" >
							<input type="checkbox" disabled="disabled">Color by search
						</span>
					</label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle display of scale bar" style="white-space:nowrap;" ><input type="checkbox" id="show-scalebar" checked>Show scalebar</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle automatic sizing of protein bars. Uncheck to allow manual horizontal sizing and vertical spacing." style="white-space:nowrap;" ><input type="checkbox" id="automatic-sizing" checked>Automatic sizing</span></label>
					<label><span style="white-space:nowrap;"
							class="tool_tip_attached_jq" data-tooltip="Toggle showing protein names to left of protein bars" 
							><input type="checkbox" id="protein_names_position_left" 
								>Protein Names On Left</span></label>

					&nbsp;&nbsp;&nbsp;&nbsp;
					<span style="white-space: nowrap;">
						<span class="tool_tip_attached_jq" data-tooltip="Choose one to view graphical feature annotations for protein sequences">Show Feature Annotations:</span> 
						<select id="annotation_type">
							<option></option>
							<option value="sequence_coverage">Sequence Coverage</option>
							
							<c:if test="${ not empty annotation_data_webservice_base_url }">
							
								<%-- These require annotation_data_webservice_base_url to be populated --%>
								<option value="disopred3">Disordered / disopred3</option>
								<option value="psired3">Secondary Structure / psipred3</option>
							</c:if>
						</select>
					</span>

					<%--
					<a style="font-size:10pt;white-space:nowrap;" href="#" class="download-svg">[Download SVG]</a>
					--%>
					
					<%--  Keep these next two items together on the same line --%>
					
					<span style="white-space:nowrap;" >

						<a class="tool_tip_attached_jq" data-tooltip="Reset left edge of all protein bars to default position" style="font-size:10pt;white-space:nowrap;" href="javascript:resetProteinOffsets()"
							>[Reset Proteins]</a>
						<a class="tool_tip_attached_jq" data-tooltip="Orients all proteins with N-terminus on left-hand side" style="font-size:10pt;white-space:nowrap;" href="javascript:resetProteinsReversed()"
							>[Reset Protein Flipping]</a>							
						<%-- 
						<input type="button" value="Reset Proteins" onClick="resetProteinOffsets()"
							title="Align all the proteins to the left edge">
						--%>
							
							
							
						<a data-tooltip="Download current image as SVG document, suitable for import into software that supports vector graphics, such as Adobe Illustrator." style="font-size:10pt;white-space:nowrap;" href="#" class="tool_tip_attached_jq download-svg">[Download SVG]</a>
					</span>

				</div>	
									
					
				<div id="user-image-sizing-div" style="display:none; margin-top: 5px;">
					
					
					<span class="tool_tip_attached_jq" data-tooltip="Use slider to change spacing between protein bars">Vertical spacing:</span>&nbsp;&nbsp;&nbsp;
					<div id="vertical_spacing_slider_div" style="display: inline-block; width: 100px;"></div>  
					<span id="vertical_spacing_value" ></span> pixels
					
					&nbsp;&nbsp;&nbsp;
					
					<span class="tool_tip_attached_jq" data-tooltip="User slider to change width of protein bars">Horizontal scaling:</span>&nbsp;&nbsp;&nbsp;
					<div id="horizontal_scaling_slider_div" style="display: inline-block; width: 100px;"></div>  
					<span id="horizontal_scaling_value" >100</span>%

				</div>
					
			</div>
		

			<%-- !!!!!!!!!!!! The image  !!!!!!!!!!! --%>
			
			<div id="svg_image_outer_container_div" 
				style="width:100%;  margin-top: 10px; overflow-x: auto;">  
						<%-- overflow-x: auto; padding: 4px; border-style: solid; border-color: #CCCCCC; border-width: 1px;  --%>
						<%--   Took out the overflow-x since the svg has too much white space on the right side for this to work and look ok --%>

				<div  id="svg_image_inner_container_div"  style=" margin: 0px; padding: 0px; " >
				
					<%--  <svg> element will be cloned and placed here when the image is drawn  --%>
				</div>
			</div>
				
			<div  id="svg_image_template_div"  style="display: none;" >
			
				<%--  <svg> element that will be cloned and put in <div  id="svg_image_inner_container_div" > when the image is drawn --%>
				<svg class=" merged_image_svg_jq " width="1" height="1"  style=" margin: 0px; padding: 0px; "></svg>
					<%--  put box around image to see image boundaries  style="border-style: solid; border-color: #CCCCCC; border-width: 1px;" --%>
			</div>
			
			
		</div>

		<%-- !!!   Handlebars templates for SVG image addtions and Tool Tips  !!!!!!!!!   --%>
		


		<%-- !!!   Handlebars template Protein Bar Tool Tip  !!!!!!!!!   --%>
		
		
		<script id="protein_bar_tool_tip_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/viewMergedImage.jsp_page_templates/viewMergedImageProteinBarTipTemplate.jsp" %>

		</script>



		<%-- !!! END:  Handlebars templates for SVG image addtions and Tool Tips  !!!!!!!!!   --%>
		
<%-- 		
		<input type="button" value="start spinner - createSpinner()" onclick="createSpinner()"> 
--%>

			<!--  The Spinner -->

<%-- 			
			<div style="position: relative;">
			<div style="width:100%;height:100%;background-color:grey;opacity:0.5; z-index: 20000;   position: fixed;top: 0;left: 0;" 
						id="coverage-map-loading-spinner-block" >
			
			
			</div>
			</div>
--%>
			
				<div style="opacity:1.0;position:absolute;left:20%;top:20%; z-index: 20001" id="coverage-map-loading-spinner" style="" ></div>

<!--  	
			</div>
-->	


	<%--  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --%>
	
	<%--  !!!!!!!!!!    Overlays               !!!!!!!!!! --%>

	<%--  !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! --%>
	
	

			<%--  Running Program to Create Annotation Data  Overlay Div,  
			
					Choose an annotation type for a protein that is not already computed and this div pops up 
			--%>

			<%--  Running Program to Create Annotation Data Overlay Background --%>
			
			
			<div id="run_pgm_annotation_data_modal_dialog_overlay_background" class="run-pgm-annotation-data-modal-dialog-overlay-background" style="display: none;"  >
			
			</div>
			
			
			<%--  Running Program to Create Annotation Data Overlay Div --%>
			
			<div id="run_pgm_annotation_data_overlay_div" class=" run-pgm-annotation-data-overlay-div " style="display: none; "  >
		
				<div id="run_pgm_annotation_data_overlay_body" class="run-pgm-annotation-data-overlay-body" >
				
					<div style="height: 200px;" >&nbsp;</div>

				
					Processing of one or more proteins for annotation type 
					"<span id="run_pgm_annotation_data_overlay_annotation_type_text"></span>" 
					is required so there will be a delay in the availablity of viewing the data.
					
					<br>
					<br>
					If Cancel is clicked, the processing will continue and the data will be available at a later time.

					  <br>
					  <br>
					 <input type="button" value="Cancel" id="run_pgm_annotation_data_overlay_cancel_button" >
				</div>
					
			</div>
	
	
	
	
	
	

			<%--  Program FAILED  to Create Annotation Data  Overlay Div,  
			
					Choose an annotation type for a protein that is not already computed and this div pops up 
			--%>

			<%--  Program FAILED to Create Annotation Data Overlay Background --%>
			
			
			<div id="pgm_failed_annotation_data_modal_dialog_overlay_background" class="pgm-failed-annotation-data-modal-dialog-overlay-background" style="display: none;"  >
			
			</div>
			
			
			<%--  Program FAILED to Create Annotation Data Overlay Div --%>
			
			<div id="pgm_failed_annotation_data_overlay_div" class=" pgm-failed-annotation-data-overlay-div " style="display: none; "  >
			
				<div id="pgm_failed_annotation_data-overlay-header" class="pgm-failed-annotation-data-overlay-header" style="width:100%; " >

					<h1 id="pgm_failed_annotation_data-overlay-header-text" class="pgm-failed-annotation-data-overlay-header-text" 
						>Errors encountered while processing Annotation Type "<span id="pgm_failed_annotation_data_overlay_annotation_type_header_text"></span>"</h1>
				</div>
				<div id="pgm_failed_annotation_data_overlay_body" class="pgm-failed-annotation-data-overlay-body" >
				
					Processing for annotation type 
					"<span id="pgm_failed_annotation_data_overlay_annotation_type_text"></span>" 
					failed for the following proteins: 
					<br>
					<span id="pgm_failed_annotation_data_overlay_failed_protein_names_text"
						style="font-weight: bold;"
						></span>
					<br>
					<br>
					Annotation data for those protein(s) will not be displayed.
					<br>
					  <br>
					  <br>
					 <input type="button" value="Display Data" id="pgm_failed_annotation_data_overlay_annotation_type_button" >
				</div>
					
			</div>
	
	
	
	
	
	
	
	
	
	
	

			<%--  View Line Info Overlay Div ,  Click on a line for a link and this div pops up --%>


			
			
			<%--  View Link Info Overlay Background --%>
			
			
			<div id="view_link_info_modal_dialog_overlay_background" class="view-link-info-modal-dialog-overlay-background" style="display: none;"  >
			
			</div>
			
			
			<%--  View Link Info Overlay Div --%>
			
			<div id="view_link_info_overlay_div" class=" view-link-info-overlay-div " style="display: none; "  >
			
			
				<div id="view-link-info-overlay-header" class="view-link-info-overlay-header" style="width:100%; " >
					<h1 id="view-link-info-overlay-X-for-exit-overlay" class="view-link-info-overlay-X-for-exit-overlay" >X</h1>
					<h1 id="view-link-info-overlay-header-text" class="view-link-info-overlay-header-text" >Data for Link</h1>
				</div>
				<div id="view-link-info-overlay-body" class="view-link-info-overlay-body" >
				
				
				
				  <div style="margin-bottom: 5px; font-weight: bold;" >
				  
					<%-- Loop Link general data  --%> 	
					
					<table id="loop_link_general_data"  >
					  <tr>
					  	<td>Looplink:</td>
					  	<td id="loop_link_protein_info"><span id="loop_link_from_protein_name"></span
					  		> (<span id="loop_link_from_protein_position" ></span
					  		>,<span id="loop_link_to_protein_position" ></span
					  		>)</td>
					  </tr>
					</table>
				
					
					<%-- Cross Link general data  --%> 					  
										
					<table id="cross_link_general_data"  >
					  <tr>
					  	<td style="padding-right: 10px; vertical-align: top;">Crosslink:</td>
					  	<td style=" vertical-align: top;">
					  	  <div >
					  		<span id="cross_link_protein_name_1"></span
					  		> (<span id="cross_link_protein_position_1" ></span
					  		>)
					  		&nbsp;

					  		<span id="cross_link_protein_name_2"></span
					  		> (<span id="cross_link_protein_position_2" ></span
					  		>)
					  	  </div>
					  	</td>
					  </tr>
					</table>


					<%-- Mono Link general data  --%> 	
					
					<table id="mono_link_general_data"  >
					  <tr>
					  	<td>Monolink:</td>
					  	<td id="mono_link_protein_info"><span id="mono_link_from_protein_name"></span
					  		> (<span id="mono_link_protein_position" ></span
					  		>)</td>
					  </tr>
					</table>
										
					
					<div style="display: none;">
						From Protein id: <span id="from_protein_id"></span>
					</div>
				
					<div style="display: none;">
						To Protein id: <span id="to_protein_id"></span>
					</div>		
					
				  </div>			

		
				<table id="link_info_table" class="link-info-table  " >
					<thead>
					<tr>
						<th style="text-align:left;width:60%;font-weight:bold;">Name</th>
						<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Peptides</th>
						<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Unique peptides</th>
						<th class="integer-number-column-header" style="width:10%;font-weight:bold;">Psms</th>
						<th style="text-align:left;width:10%;font-weight:bold; padding-left: 5px;">Best&nbsp;Peptide <span style="white-space: nowrap">Q-value</span></th>
						<th style="text-align:left;width:10%;font-weight:bold; padding-left: 5px;">Best&nbsp;PSM <span style="white-space: nowrap">Q-value</span></th>
					</tr>
					</thead>
					<tbody id="link_info_table__tbody"></tbody>
				</table>



			<%-- !!!!  Handlebars templates   !!!! --%>
			
			
				<%-- !!!!   WARNING:   IE will drop any illegal text in the "style" attribute so cannot put conditionals in the "style" attribute  !!! 
				
											IE will drop the text inside the conditionals as well
				--%>



	<%--  Search Template - row to hold data --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<table id="search_entry_data_row_template" style="display: none;" >

		<tr id="perc_{{search.id}}" 
			style="cursor: pointer; "
			onclick="toggleVisibility(this)"
			toggle_visibility_associated_element_id="perc_{{search.id}}"
		>
			<td>{{search.name}}</td>
			
			<td class="integer-number-column" style="" >
				<a class="show-child-data-link   "
					href="javascript:"
					>{{numPeptides}}<%-- << actual data in the cell --%><span class=" toggle_visibility_expansion_span_jq" 
								style="" 
							><img src="${contextPath}/images/icon-expand-small.png" 
								class=" icon-expand-contract-in-data-table "
								></span><span class="toggle_visibility_contraction_span_jq" 
									style=" display: none;" 
									><img src="${contextPath}/images/icon-collapse-small.png"
										class=" icon-expand-contract-in-data-table "
										></span>
				</a>
			</td>												
			
			<td class="integer-number-column" style="" >{{numUniquePeptides}}</td>
			
			<td class="integer-number-column" style="" >{{numPsms}}</td>
			<td style="text-align: left; padding-left: 5px; white-space: nowrap" >{{bestPeptideQValue}}</td>
			<td style="text-align: left; padding-left: 5px; white-space: nowrap" >{{bestPSMQValue}}</td>
		</tr>
	</table>	
		
		

	<%--  Search Template - row to hold children --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<table id="search_entry_child_row_template" style="display: none;" >
		
		<tr  class="expand-child  expand_child_jq " style=" display:none; " > 
		
			<td class="peptide_data_container" colspan="6"  style="text-align: center;" >
				

			</td>
		</tr>
	</table>	

				

	<%--  Looplink Peptide Template --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<div id="looplink_peptide_block_template" style="display: none;" >

		<%--  top level <div> in the template so can reference the inserted element with jQuery after insert it .
				
				var $looplink_peptide_block_template =  $(handlebarsSource_looplink_peptide_block_template).appendTo($peptide_data_container);
				
				$looplink_peptide_block_template can then be used.  If no top level <div> in the template, cannot use $looplink_peptide_block_template 
				
		--%>
				
		<div style="text-align: left;"> <%--  top level div in the template --%>
			
				<table class=" peptide_table_jq" style="margin-bottom: 10px; margin-top: 5px; width: 95%; margin-left: auto; margin-right: auto; text-align: left;" >
				
					<thead>
					<tr>
						<th style="text-align:left;font-weight:bold;">Reported peptide</th>
						<th style="text-align:left;font-weight:bold;">Peptide</th>
						<th class="integer-number-column-header" style="font-weight:bold;">Pos&nbsp;1</th>
						<th class="integer-number-column-header" style="font-weight:bold;">Pos&nbsp;2</th>
						<th style="text-align:left;font-weight:bold;"><span style="white-space: nowrap">Q-value</span></th>
						<th class="integer-number-column-right-most-column-no-ts-header" style="font-weight:bold;">#&nbsp;PSMs</th>
					</tr>
					</thead>
					<tbody></tbody>
				</table>	
				
		</div>
								
	</div>
			

	<%--  Looplink Peptide Data Entry Template --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<table id="looplink_peptide_data_row_entry_template" style="display: none;" >
		
		<tr id="peptide_{{reportedPeptide.id}}"
			style="cursor: pointer; "
			onclick="viewPsmsLoadedFromWebServiceTemplate.showHidePsms( { clickedElement : this } )"
			reported_peptide_id="{{ reportedPeptide.id }}"
			search_id="{{ searchId }}"
			project_id="${ project_id }"
			psm_q_value_cutoff="{{ psmQValueCutoff }}"
		>
				
			<td>{{reportedPeptide.sequence}}</td>
			<td>{{peptide.sequence}}</td>
			<td class="integer-number-column" style="" >{{peptidePosition1}}</td>
			<td class="integer-number-column" style="" >{{peptidePosition2}}</td>
			
			<td style="text-align: left; " >{{qvalue}}</td>
			
			
			<td class="integer-number-column-right-most-column-no-ts" style="" >
				<a class="show-child-data-link   "
					href="javascript:"
					>{{numPsms}}<%-- << actual data in the cell --%><span class="toggle_visibility_expansion_span_jq" 								style="{{#if onlyOneEntry}} display: none; {{else}}{{/if}}" 
								style="" 
							><img src="${contextPath}/images/icon-expand-small.png" 
								class=" icon-expand-contract-in-data-table "
								></span><span class="toggle_visibility_contraction_span_jq" 
									style="display: none;" 
									><img src="${contextPath}/images/icon-collapse-small.png"
										class=" icon-expand-contract-in-data-table "
										></span>
				</a>
			</td>												
		</tr>
	</table>	
			

	<%--  Looplink Peptide Data Entry Template --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<table id="looplink_peptide_child_row_entry_template" style="display: none;" >
			
		<tr   class="expand-child  expand_child_jq "  style=" display: none; ">
		
			<td class="psm_data_container child_data_container_jq " colspan="6"  style="text-align: center;" >
				

			</td>
		</tr>
	</table>	
	
				
			

	<%--  Crosslink Peptide Template --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<div id="crosslink_peptide_block_template" style="display: none;" >

		<%--  top level <div> in the template so can reference the inserted element with jQuery after insert it .
				
				var $crosslink_peptide_block_template = $(handlebarsSource_crosslink_peptide_block_template).appendTo($peptide_data_container);
				
				$crosslink_peptide_block_template can then be used.  If no top level <div> in the template, cannot use $crosslink_peptide_block_template 
				
		--%>
				
		<div style="" > <%--  top level div in the template --%>

				<table class=" peptide_table_jq" style="margin-bottom: 10px; margin-top: 5px; width: 95%; margin-left: auto; margin-right: auto; text-align: left;  " > <%-- margin-left: auto; margin-right: auto; --%>
				
					<thead>
					<tr>
						<th style="text-align:left; font-weight:bold;">Reported peptide</th>
						<th style="text-align:left; font-weight:bold;">Peptide 1</th>
						<th class="integer-number-column-header" style=" font-weight:bold;">Pos</th>
						<th style="text-align:left; font-weight:bold;">Peptide 2</th>
						<th class="integer-number-column-header" style=" font-weight:bold;">Pos</th>
						<th style="text-align:left; font-weight:bold;"><span style="white-space: nowrap">Q-value</span></th>
						<th class="integer-number-column-right-most-column-no-ts-header" style="font-weight:bold;">#&nbsp;PSMs</th>
					</tr>
					</thead>
					<tbody></tbody>
				</table>			
		</div>
		
	</div> <%--  end of  id="crosslink_peptide_block_template"  --%>


	<%--  Crosslink Peptide Entry Template --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<table id="crosslink_peptide_data_row_entry_template" style="display: none;" >
			   
		<tr id="peptide_{{reportedPeptide.id}}"
			style="cursor: pointer; "
			onclick="viewPsmsLoadedFromWebServiceTemplate.showHidePsms( { clickedElement : this } )"
			reported_peptide_id="{{ reportedPeptide.id }}"
			search_id="{{ searchId }}"
			project_id="${ project_id }"
			psm_q_value_cutoff="{{ psmQValueCutoff }}"			
		>
			<td>{{reportedPeptide.sequence}}</td>
			<td style="text-align:left;" >{{peptide1.sequence}}</td>
			<td class="integer-number-column" style="" >{{peptide1Position}}</td>
			<td style="text-align:left;" >{{peptide2.sequence}}</td>
			<td class="integer-number-column" style="" >{{peptide2Position}}</td>
			<td style="text-align:left; white-space: nowrap" >{{qvalue}}</td>
			
			<td class="integer-number-column-right-most-column-no-ts" style="" >
				<a class="show-child-data-link   "
					href="javascript:"
					>{{numPsms}}<%-- << actual data in the cell --%><span class="toggle_visibility_expansion_span_jq" 
								style="" 
							><img src="${contextPath}/images/icon-expand-small.png" 
								class=" icon-expand-contract-in-data-table "
								></span><span class="toggle_visibility_contraction_span_jq" 
									style="display: none; " 
									><img src="${contextPath}/images/icon-collapse-small.png"
										class=" icon-expand-contract-in-data-table "
										></span>
				</a>
			</td>												
			
		</tr>
	</table>	
		

	<%--  Crosslink Peptide Child row Entry Template --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<table id="crosslink_peptide_child_row_entry_template" style="display: none;" >

		
		<tr   class="expand-child  expand_child_jq "  style="display: none; ">
		
			<td class="psm_data_container child_data_container_jq" colspan="7" style="text-align: center;" >
				

			</td>
		</tr>
	</table>	
	
	


	<%--  Monolink Peptide Template --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<div id="monolink_peptide_block_template" style="display: none;" >

		<%--  top level <div> in the template so can reference the inserted element with jQuery after insert it .
				
				var $monolink_peptide_block_template =  $(handlebarsSource_monolink_peptide_block_template).appendTo($peptide_data_container);
				
				$monolink_peptide_block_template can then be used.  If no top level <div> in the template, cannot use $monolink_peptide_block_template 
				
		--%>
				
		<div style=""> <%--  top level div in the template --%>
		
				<table class=" peptide_table_jq" style="margin-bottom: 10px; margin-top: 5px; width: 95%; margin-left: auto; margin-right: auto; text-align: left;" >
				
					<thead>
					<tr>
						<th style="text-align:left;font-weight:bold;">Reported peptide</th>
						<th style="text-align:left;font-weight:bold;">Peptide</th>
						<th class="integer-number-column-header" style="font-weight:bold;">Pos</th>
						<th style="text-align:left;font-weight:bold;"><span style="white-space: nowrap">Q-value</span></th>
						<th class="integer-number-column-right-most-column-no-ts-header" style="font-weight:bold;">#&nbsp;PSMs</th>
					</tr>
					</thead>
					<tbody></tbody>
				</table>	
				
		</div>
								
	</div>
			

	<%--  Monolink Peptide Data Row Entry Template --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<table id="monolink_peptide_data_row_entry_template" style="display: none;" >

		<tr id="peptide_{{reportedPeptide.id}}"
			style="cursor: pointer; "
			onclick="viewPsmsLoadedFromWebServiceTemplate.showHidePsms( { clickedElement : this } )"
			reported_peptide_id="{{ reportedPeptide.id }}"
			search_id="{{ searchId }}"
			project_id="${ project_id }"
			psm_q_value_cutoff="{{ psmQValueCutoff }}"
		>
			<td>{{reportedPeptide.sequence}}</td>
			<td>{{peptide.sequence}}</td>
			<td class="integer-number-column" style="" >{{peptidePosition}}</td>
			
			<td style="text-align: left; white-space: nowrap" >{{qvalue}}</td>
			
			
			<td class="integer-number-column-right-most-column-no-ts" style="" >
				<a class="show-child-data-link   "
					href="javascript:"
					>{{numPsms}}<%-- << actual data in the cell --%><span class="toggle_visibility_expansion_span_jq" 								style="{{#if onlyOneEntry}} display: none; {{else}}{{/if}}" 
								style="" 
							><img src="${contextPath}/images/icon-expand-small.png" 
								class=" icon-expand-contract-in-data-table "
								></span><span class="toggle_visibility_contraction_span_jq" 
									style=" display: none; " 
									><img src="${contextPath}/images/icon-collapse-small.png"
										class=" icon-expand-contract-in-data-table "
										></span>
				</a>
			</td>												
			
		</tr>
	</table>	
		

	<%--  Monolink Peptide Child Row Entry Template --%>

	<%-- This table is just a container and will not be placed into the final output --%>
	<table id="monolink_peptide_child_row_entry_template" style="display: none;" >
		
		<tr   class="expand-child  expand_child_jq "  style=" display: none; ">
		
			<td class="psm_data_container child_data_container_jq" colspan="5" style="text-align: center;" >
				

			</td>
		</tr>
	</table>	
	
					
	
			</div>  <%--  END  <div id="view-link-info-overlay-body" class="view-link-info-overlay-body" > --%>


		</div>  <%--  END  <div id="view_link_info_overlay_div" class=" view-link-info-overlay-div " style="display: none; "  > --%>
	
			

			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	
			
			<%@ include file="/WEB-INF/jsp-includes/nagWhenFormChangedButNotUpdated_Overlay.jsp" %>
			
			
	</div>  <%--  END  <div class="overall-enclosing-block"> --%>
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>

