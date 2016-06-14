<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">View Search</c:set>

 <c:set var="pageBodyClass" >project-page view-merged-image-page</c:set>
 
 <c:set var="helpURLExtensionForSpecificPage" >en/latest/using/image.html</c:set>

 <c:set var="headerAdditions">
 
		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.tablesorter.min.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/libs/spin.min.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/libs/base64.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.qtip.min.js"></script>

		<%--  Compression --%>
		
		<script type="text/javascript" src="${ contextPath }/js/libs/lz-string/lz-string.min.js"></script>
		
		<%--  Non-Minified version --%>
		<%-- 
		<script type="text/javascript" src="${ contextPath }/js/libs/lz-string/lz-string.js"></script>
		--%>
		
		<%--  Used by lz-string.min.js --%>
		<script type="text/javascript" src="${ contextPath }/js/libs/lz-string/base64-string.js"></script>

		
		
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
		
		<script src="${contextPath}/js/lorikeet/jquery.flot.js?x=${cacheBustValue}"></script>
		<script src="${contextPath}/js/lorikeet/jquery.flot.selection.js?x=${cacheBustValue}"></script>
		
		<script src="${contextPath}/js/lorikeet/specview.js?x=${cacheBustValue}"></script>
		<script src="${contextPath}/js/lorikeet/peptide.js?x=${cacheBustValue}"></script>
		<script src="${contextPath}/js/lorikeet/aminoacid.js?x=${cacheBustValue}"></script>
		<script src="${contextPath}/js/lorikeet/ion.js?x=${cacheBustValue}"></script>		
		
<%--  End of Lorikeet Core Parts --%>		

		
			<%--  On this page Snap also used by crosslink-image-viewer.js and crosslink-image-viewer-click-element-handlers.js --%>				
		<script type="text/javascript" src="${ contextPath }/js/libs/snap.svg-min.js"></script> <%--  Used by lorikeetPageProcessing.js --%>
				
		<script type="text/javascript" src="${ contextPath }/js/lorikeetPageProcessing.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js?x=${cacheBustValue}"></script> 
		 
		<script type="text/javascript" src="${ contextPath }/js/spinner.js?x=${cacheBustValue}"></script> 
		
		<script type="text/javascript" src="${ contextPath }/js/trypsinCutPointsForSequence.js?x=${cacheBustValue}"></script>
		 
		<script type="text/javascript" src="${ contextPath }/js/circle-plot/circle-plot-viewer-protein-annotation.js?x=${cacheBustValue}"></script>

		<script type="text/javascript" src="${ contextPath }/js/psmPeptideCutoffsCommon.js?x=${cacheBustValue}"></script>

		<script type="text/javascript" src="${ contextPath }/js/image_structure_click_element_common.js?x=${cacheBustValue}"></script>

		<script type="text/javascript" src="${ contextPath }/js/circle-plot/circle-plot-viewer-click-element-handlers.js?x=${cacheBustValue}"></script>

		<script type="text/javascript" src="${ contextPath }/js/circle-plot/circle-plot-viewer-per-protein-bar-data.js?x=${cacheBustValue}"></script> 

		<script type="text/javascript" src="${ contextPath }/js/circle-plot/circle-plot-viewer-region-selections.js?x=${cacheBustValue}"></script> 

		<script type="text/javascript" src="${ contextPath }/js/circle-plot/circle-plot-viewer.js?x=${cacheBustValue}"></script> 
		<script type="text/javascript" src="${ contextPath }/js/circle-plot/circle-plot-viewer-old.js?x=${cacheBustValue}"></script> 
		
				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This input is required on this page:
					<input type="hidden" id="protein_listing_webservice_base_url" value="<c:out value="${ protein_listing_webservice_base_url }"></c:out>">
				  --%>
		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/defaultPageView.js?x=${cacheBustValue}"></script>
		
		
		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js?x=${cacheBustValue}"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/viewPsmsLoadedFromWebServiceTemplate.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplate.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplate.js?x=${cacheBustValue}"></script>
		<script type="text/javascript" src="${ contextPath }/js/viewMonolinkReportedPeptidesLoadedFromWebServiceTemplate.js?x=${cacheBustValue}"></script>
		
		<link rel="stylesheet" href="${ contextPath }/css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />
		
		<%--  some classes in this stylesheet collide with some in the lorikeet file since they are set to specific values for lorikeet drag and drop --%>
		<%-- 
		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/jquery-ui-1.10.2-Themes/ui-lightness/jquery-ui.min.css">
		--%>
		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/lorikeet.css">
		




</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>
	
		<%--  protein name data webservice base URL, used by createTooltipForProteinNames.js --%>
	<input type="hidden" id="protein_listing_webservice_base_url" value="<c:out value="${ protein_listing_webservice_base_url }"></c:out>">

	
		<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/specificProteinDataPerSearchProtIdsPositionsFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewMonolinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
		
	<div class="overall-enclosing-block">
	
	<input type="hidden" id="annotation_data_webservice_base_url" value="<c:out value="${ annotation_data_webservice_base_url }"></c:out>"> 

	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
	<c:forEach var="searchId" items="${ searchIds }">
	
		<%--  Put Search Ids on the page for the JS code --%>
		<input type="hidden" class=" search_id_jq " value="<c:out value="${ searchId }"></c:out>">
	</c:forEach>
	
	<c:if test="${ not empty onlySingleSearchId }">
	
		<input type="hidden" id="viewSearchPeptideDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/peptide" searchId="${ onlySingleSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewSearchCrosslinkProteinDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/crosslinkProtein" searchId="${ onlySingleSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewProteinCoverageReportDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/proteinCoverageReport" searchId="${ onlySingleSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewMergedStructureDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/structure" searchId="${ onlySingleSearchId }"></proxl:defaultPageUrl>">
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
			
			<%--  Hidden fields to pass data to JS --%>
			
			<input type="hidden" id="cutoffValuesRootLevelCutoffDefaults" value="<c:out value="${ cutoffValuesRootLevelCutoffDefaults }"></c:out>"> 
			
				<%--  A block outside any form for PSM Peptide cutoff JS code --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_outsideAnyForm.jsp" %>
			
				<%--  A block in the submitted form for PSM Peptide cutoff JS code --%>
				<%--   In the Merged Image and Merged Structure Pages, this will not be in any form  --%>
				<%@ include file="/WEB-INF/jsp-includes/psmPeptideCutoffBlock_inSubmitForm.jsp" %>

	
			<form action="#">
	
			<table style="border-width:0px;">

				<%--  Set to false to NOT show color block before search for key --%>
				<c:set var="showSearchColorBlock" value="${ false }" />
				
				<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
				<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>

				<tr>
					<td>Exclude links with:</td>
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

						<%--
						<label><span style="white-space:nowrap;" ><input type="checkbox" id="exclude-type-4">Crosslinks</span></label>
						<label><span style="white-space:nowrap;" ><input type="checkbox" id="exclude-type-2">Looplinks</span></label>
						<label><span style="white-space:nowrap;" ><input type="checkbox" id="exclude-type-1">Monolinks</span></label>
						--%>
						<%-- <label><span style="white-space:nowrap;" ><input type="checkbox" id="exclude-type-3">Dimers</span></label> --%>
						<label><span style="white-space:nowrap;" ><input type="checkbox" id="exclude-type-0">No links</span></label>
					</td>
				</tr>

				<tr>
					<td>Exclude organisms:</td>
					<td><div id="taxonomy-checkboxes"></div>
					</td>
					
				</tr>
				
				
				<tr>
					<td>&nbsp;</td>
					<td>

						<c:set var="UpdateButtonText" value="Update From Database"/>
						
						<input type="button" value="${ UpdateButtonText }"  onclick="refreshData()" >

						<c:if test="${ not empty onlySingleSearchId }">

							<c:set var="searchId" value="${ onlySingleSearchId }"/>	
								
							<c:set var="page_JS_Object" value="imageViewerPageObject"/>
							
							<%@ include file="/WEB-INF/jsp-includes/defaultPageViewButtonFragment.jsp" %>
						</c:if>
											
					</td>
				</tr>
			
			</table>
			</form>
			
		</div>
	
		<hr>

		<div>
			<div>

				<%-- !!!   Handlebars template Select Protein  !!!!!!!!!   --%>
		
				<%--  Protein Item to put in Overlay --%>		
				<script id="selected_protein_entry_template"  type="text/x-handlebars-template">

					<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/viewMergedImage.jsp_page_templates/viewMergedImageSelectedProteinItemTemplate.jsp" %>

				</script>
				
								



				<div style="margin-top: 5px; clear: both; ">
					
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing inter-protein crosslinks" style="white-space:nowrap;" ><input type="checkbox" id="show-crosslinks" checked>Show crosslinks</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing intra-protein crosslinks" style="white-space:nowrap;" ><input type="checkbox" id="show-self-crosslinks" checked>Show self-crosslinks</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing looplinks" style="white-space:nowrap;" ><input type="checkbox" id="show-looplinks" checked>Show looplinks</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing monolinks" style="white-space:nowrap;" ><input type="checkbox" id="show-monolinks">Show monolinks</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle marking possible positions in protein where crosslinker(s) may react" style="white-space:nowrap;" ><input type="checkbox" id="show-linkable-positions">Show linkable positions</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle marking trypic positions in proteins" style="white-space:nowrap;" ><input type="checkbox" id="show-tryptic-cleavage-positions">Show tryptic positions</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing protein termini labels" style="white-space:nowrap;" ><input type="checkbox" id="show-protein-termini">Show protein termini</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle shading of links based on spectrum counts" style="white-space:nowrap;" ><input type="checkbox" id="shade-by-counts">Shade by counts</span></label>
					
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle display of scale bar" style="white-space:nowrap;" ><input type="checkbox" id="show-scalebar" checked>Show scalebar</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle automatic sizing of protein bars. Uncheck to allow manual horizontal sizing and vertical spacing." style="white-space:nowrap;" ><input type="checkbox" id="automatic-sizing" checked>Automatic sizing</span></label>
					<label><span style="white-space:nowrap;"
							class="tool_tip_attached_jq" data-tooltip="Toggle showing protein names to left of protein bars" 
							><input type="checkbox" id="protein_names_position_left" 
								>Protein Names On Left</span></label>

					<%--  Select for "color by" with options of by search or by region  --%>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<span style="white-space: nowrap;">
						<span class="tool_tip_attached_jq" data-tooltip="Choose alternate link coloring"
								>Color by:</span> 
								
						<select id="color_by">
							<option value="">Protein</option>
							
							<%--  These option values must be kept in sync with Javascript.
								  These values must be kept backward compatible or 
								    Javascript must be written to convert
							--%>
							
							<option value="region">Region</option>
							
							<c:choose>
							  <c:when test="${ fn:length( searchIds ) <= 3 }">
							
								<%-- Only shown when the number of searches is <= the number supported by 'Color by search' --%>
								<option value="search">Search</option>
							  </c:when>
							  <c:otherwise>
							  	<%--  Disable the color by search option --%>
							  	<option value="search_disabled" disabled="disabled">Search</option>
							  </c:otherwise>
							</c:choose>
						</select>
					</span>
								
					&nbsp;
					<span style="white-space: nowrap;">
						<span class="tool_tip_attached_jq" data-tooltip="Choose one to view graphical feature annotations for protein sequences"
								>Show Feature Annotations:</span>
								 
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
				
					<%--  Keep these next two items together on the same line --%>
					
					<span style="white-space:nowrap;" >

						<a class="tool_tip_attached_jq" 
							data-tooltip="Reset Proteins highlighting, flipping, positioning, and horizontal scaling"  
							href="javascript:resetProteins()"  style="font-size:10pt;white-space:nowrap;"
							>[Reset Proteins]</a>
						<a class="tool_tip_attached_jq" data-tooltip="Orients all proteins with N-terminus on left-hand side" style="font-size:10pt;white-space:nowrap;" 
							href="javascript:resetProteinsReversed()"
							>[Reset Protein Flipping]</a>							

						<a class="tool_tip_attached_jq" data-tooltip="Protein Selection Regions Management" style="font-size:10pt;white-space:nowrap;" 
							href="javascript:" onclick="_proteinBarRegionSelectionsOverlayCode.openOverlay()"
							>[Manage Protein Selections]</a>							
							


						<a id="download_svg_link"
								data-tooltip="Download current image as SVG document, suitable for import into software that supports vector graphics, such as Adobe Illustrator." style="font-size:10pt;white-space:nowrap;" 
								href="#" class="tool_tip_attached_jq download-svg">[Download SVG]</a>

						<span id="download_svg_not_supported"
								data-tooltip="Not supported in this browser." 
								style="font-size:10pt;white-space:nowrap; display: none;" 
								class="tool_tip_attached_jq download-svg-link-non-link">[Download SVG]</span>
						
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
				
				<div style="margin-top: 10px;">
					<div id="selected_proteins_outer_container">
					  <div id="selected_proteins_container" class="selected-proteins-container">
					  </div>
					</div>
					
					<div style="float: left;">
					  <div >
						<input type="button" class="tool_tip_attached_jq" data-tooltip="Add another protein bar to the image" 
							id="svg-protein-selector_location"
							onclick="openSelectProteinSelect( { clickedThis : this, addProteinsClicked : true } )" value="Add Protein">
					  </div>
					</div>
				</div>		
				
				<div style="clear: both;"></div>		
					
			</div>
		
				<%--  Only shown when no proteins selected --%>
				
			<div id="no_proteins_add_protein_outer_container" style="display: none;">
			
				<div class="no-proteins-add-protein-block"
					onclick="openSelectProteinSelect( { clickedThis : this, addProteinsClicked : true } )" >

						Click to Add Protein
				</div>
			
			</div>
						


			<%-- !!!!!!!!!!!! The image  !!!!!!!!!!! --%>
			
			<div id="svg_image_outer_container_div" 
				style="width:100%;  margin-top: 10px; overflow-x: auto; display: none;">  
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

		<%-- !!!   Handlebars templates for SVG image additions and Tool Tips  !!!!!!!!!   --%>
		


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
	
	
	
		<%--  Select Protein Overlay --%>
		

			<%--  Select Protein Overlay Background --%>
			
			
			<div id="select_protein_modal_dialog_overlay_background" class="select-protein-modal-dialog-overlay-background" style="display: none;"  >
			
			</div>
			
			
			<%--  Select Protein Overlay Div --%>
			
			<div id="select_protein_overlay_div" class=" select-protein-overlay-div " style="display: none; "  >
			
				<div id="select_protein_overlay_header" class="select-protein-overlay-header" style="width:100%; " >
					<h1 id="select_protein_overlay_X_for_exit_overlay" class="select-protein-overlay-X-for-exit-overlay" >X</h1>
					<h1 id="select_protein_overlay_header_text" class="select-protein-overlay-header-text" 
						><span id="select_protein_add_proteins_overlay_header_text" >Add Protein(s)</span
						><span id="select_protein_change_protein_overlay_header_text" >Change Protein</span
						></h1>
				</div>
				<div id="select_protein_overlay_body" class="select-protein-overlay-body" >
				
				  <div style=" margin-bottom: 10px;" >
					<div id="select_protein_add_proteins_overlay_body_text">
						Choose protein(s) and click "Add"
					</div>
				
					<div id="select_protein_change_protein_overlay_body_text">
						Choose a protein
					</div>
				  </div>
				
					<div id="protein_list_container" class=" protein-list-container">
					</div>
					
					<div id="select_protein_add_proteins_overlay_add_button_container"
						style=" margin-top: 10px;" >
						<input type="button" value="Add"  id="select_protein_add_proteins_overlay_add_button" 
							 class="tool_tip_attached_jq" data-tooltip="Add selected proteins" />
					</div> 
					
				</div> <%--  END  <div id="select_protein_overlay_body"  --%>
				
			</div>  <%--  END  <div id="select_protein_overlay_div"  --%>

		

		<%-- !!!   Handlebars template Select Protein  !!!!!!!!!   --%>

		<%--  Protein Item to put in Overlay --%>		
		<script id="select_protein_overlay_protein_entry_template"  type="text/x-handlebars-template">
		  <div class=" protein_select_jq single-protein-select-item " data-protein_id="{{proteinId}}" >
			{{proteinName}}
		  </div>
		</script>

		<%--  Protein Item for main display --%>
		<script id="select_protein_overlay_bar_region_template"  type="text/x-handlebars-template">
		
		</script>
		
			
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
				  
				  <div id="link_data_table_place_holder">
				  
				  
				  </div>


					
	
			</div>  <%--  END  <div id="view-link-info-overlay-body" class="view-link-info-overlay-body" > --%>


		</div>  <%--  END  <div id="view_link_info_overlay_div" class=" view-link-info-overlay-div " style="display: none; "  > --%>
	
			
			
		<%--  --------------------------------------------------------------------  --%>
	

			<%--  View Protein Bar Highlighting Overlay Div,  this overlay div is for setting highlighting regions --%>

			
				<%--  View Protein Bar Highlighting Overlay Background --%>
			
			
			<div id="view_protein_bar_highlighting_modal_dialog_overlay_background" class="view-protein-bar-highlighting-modal-dialog-overlay-background" style="display: none;"  >
			
			</div>
			
			
				<%--  View Protein Bar Highlighting Overlay Div --%>
			
			<div id="view_protein_bar_highlighting_overlay_div" class=" view-protein-bar-highlighting-overlay-div " style="display: none; "  >
			
			
				<div id="view_protein_bar_highlighting_overlay_header" class="view-protein-bar-highlighting-overlay-header" style="width:100%; " >
					<h1 id="view_protein_bar_highlighting_overlay_X_for_exit_overlay" class="view-protein-bar-highlighting-overlay-X-for-exit-overlay" >X</h1>
					<h1 id="view_protein_bar_highlighting_overlay_header_text" class="view-protein-bar-highlighting-overlay-header-text" >Proten Bar Region Selections</h1>
				</div>
				<div id="view_protein_bar_highlighting_overlay_body" class="view-protein-bar-highlighting-overlay-body" >
				
				
				
				  <div style="margin-bottom: 5px; font-weight: bold;" >
				  
				  	
				  	<div id="view_protein_bar_highlighting_overlay_protein_bars_data_div">
				  	
				  	</div>
				  	
				  	<input type="button" value="Save" id="view_protein_bar_highlighting_overlay_protein_bars_save_button"
				  		 class="tool_tip_attached_jq" data-tooltip="Save above selected regions.">
				  	<input type="button" value="Cancel" id="view_protein_bar_highlighting_overlay_protein_bars_cancel_button"
				  		 class="tool_tip_attached_jq" data-tooltip="Cancel without changing selected regions.">
				  	<input type="button" value="Reset" id="view_protein_bar_highlighting_overlay_protein_bars_reset_button"
				  		 class="tool_tip_attached_jq" data-tooltip="Reset regions to those shown in diagram.">
				  	<input type="button" value="Clear All" id="view_protein_bar_highlighting_overlay_protein_bars_clear_all_button"
				  		 class="tool_tip_attached_jq" data-tooltip="Remove all selected regions, make all proteins visible.">
					
				</div>
				
			</div>  <%--  END  <div id="view-protein-bar-highlighting-overlay-body" class="view-protein-bar-highlighting-overlay-body" > --%>


		</div>  <%--  END  <div id="view_protein_bar_highlighting_overlay_div" class=" view-protein-bar-highlighting-overlay-div " style="display: none; "  > --%>
	
	


		<%-- !!!   Handlebars template Manage Protein Bar Regions  !!!!!!!!!   --%>
		
		
		<script id="view_protein_bar_highlighting_overlay_single_bar_template"  type="text/x-handlebars-template">

			<%--  include the template text  --%>
			<%@ include file="/WEB-INF/jsp_template_fragments/For_jsp_pages/viewMergedImage.jsp_page_templates/viewMergedImageManageProteinBarSingleBarRegionTemplate.jsp" %>

		</script>
	

		<script id="view_protein_bar_highlighting_overlay_bar_region_template"  type="text/x-handlebars-template">
	
			<div class=" bar_region_jq " style="padding-top: 2px; position: relative;" > <%--  position: relative; to support positioning error messages --%>

				start: <input type="text" style="width: 30px;" value="{{start}}" class=" start_jq ">  
				End: <input type="text" style="width: 30px;" value="{{end}}" class=" end_jq ">  

				<input type="image" src="${ contextPath }/images/icon-delete-small.png"  
					class=" view_protein_bar_highlighting_region_remove_button_jq tool_tip_attached_jq" 
					data-tooltip="Remove region" > 

		  		<div class="error-message-container error_message_container_jq invalid_number_error_msg_jq " >
		  			<div class="error-message-inner-container" >
		  				<div class="error-message-close-x error_message_close_x_jq">X</div>
			  			<div class="error-message-text" >Not a valid number</div>
		  			</div>
			  	</div>

		  		<div class="error-message-container error_message_container_jq end_before_start_error_msg_jq " >
		  			<div class="error-message-inner-container" >
		  				<div class="error-message-close-x error_message_close_x_jq">X</div>
			  			<div class="error-message-text" >The End cannot be before the Start</div>
		  			</div>
			  	</div>

			</div>

		</script>
		
		<%-- !!!   END   Handlebars template Manage Protein Bar Regions  !!!!!!!!!   --%>
								

			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	
			
	</div>  <%--  END  <div class="overall-enclosing-block"> --%>
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
