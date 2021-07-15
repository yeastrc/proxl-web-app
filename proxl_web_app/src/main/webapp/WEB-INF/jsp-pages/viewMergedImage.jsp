<%@page import="org.yeastrc.xlink.www.constants.PageLinkTextAndTooltipConstants"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">View Search - <c:out value="${ headerProject.projectTblData.title }"></c:out></c:set>

 <c:set var="pageBodyClass" >project-page view-merged-image-page</c:set>
 
 <c:set var="helpURLExtensionForSpecificPage" >en/latest/using/image-bar.html</c:set>

 <c:set var="headerAdditions">
 
		<script type="text/javascript" src="js/libs/jquery.tablesorter_Modified.js"></script>
		<script type="text/javascript" src="js/libs/base64.js"></script> 
		<script type="text/javascript" src="js/libs/jquery.qtip.min.js"></script>

		<%--  Compression --%>
		
		<script type="text/javascript" src="js/libs/lz-string/lz-string.min.js"></script>
		
		<%--  Non-Minified version --%>
		<%-- 
		<script type="text/javascript" src="js/libs/lz-string/lz-string.js"></script>
		--%>
		
		<%--  Used by lz-string.min.js --%>
		<script type="text/javascript" src="js/libs/lz-string/base64-string.js"></script>

		<script src="js/libs/jquery-ui-1.12.1.min.js"></script>
		
				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This include is required on this page:
					/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp
				  --%>

				<%--  Color Picker - jQuery Plugin --%>
		<script type="text/javascript" src="js/libs/colorpicker/colorpicker.js"></script>
		
		<link rel="stylesheet" href="css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="css/jquery.qtip.min.css" />
		
		<%--  some classes in this stylesheet collide with some in the lorikeet file since they are set to specific values for lorikeet drag and drop --%>
		<%-- <link REL="stylesheet" TYPE="text/css" HREF="css/jquery-ui-1.10.2-Themes/ui-lightness/jquery-ui.min.css"> --%>

		<link REL="stylesheet" TYPE="text/css" HREF="css/lorikeet.css">
		<link rel="stylesheet" media="screen" type="text/css" href="css/libs/colorpicker_custom_colors.css" />
		

</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>
	
	<%@ include file="/WEB-INF/jsp-includes/body_section_data_pages_after_header_main.jsp" %>
	
	<%--  used by createTooltipForProteinNames.js --%>
	<%@ include file="/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp" %>

	
		<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewPsmPerPeptideLoadedFromWebServiceTemplateFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewMonolinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
		
	<div class="overall-enclosing-block">
	
	<input type="hidden" id="annotation_data_webservice_base_url" value="<c:out value="${ annotation_data_webservice_base_url }"></c:out>"> 

	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
	<c:forEach var="projectSearchId" items="${ projectSearchIds }">
	
		<%--  Put Project_Search_Ids on the page for the JS code --%>
		<input type="hidden" class=" project_search_id_jq " value="<c:out value="${ projectSearchId }"></c:out>">
	</c:forEach>
	
	<%--  projectSearchIdsUserOrdered set to PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES if true, empty string if false (from 'ds' query string param value).  
			Javascript on this page will copy this to some URLs for Webservices and links to other pages --%>
	<input type="hidden" id="project_search_ids_user_ordered" value="<c:out value="${ projectSearchIdsUserOrdered }"></c:out>" />
	
	<c:if test="${ not empty onlySingleProjectSearchId }">
	
		<input type="hidden" id="viewSearchPeptideDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/peptide" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewSearchCrosslinkProteinDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/crosslinkProtein" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewProteinCoverageReportDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/proteinCoverageReport" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewMergedStructureDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/structure" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
	</c:if>
	
	<script id="qc_page_link_text" type="text/text"><%= PageLinkTextAndTooltipConstants.QC_LINK_TEXT %></script>
	<script id="qc_page_link_tooltip" type="text/text"><%= PageLinkTextAndTooltipConstants.QC_LINK_TOOLTIP %></script>
					
	
		<div>
	
			<h2 style="margin-bottom:5px;">View <span id="merged_label_text_header" style="display: none;" >merged </span>search results:</h2>
	
			<div id="navigation-links"  class=" navigation-links-block ">
			
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
			
			<input type="hidden" id="default_values_cutoffs_others" value='<c:out value="${ default_values_cutoffs_others }"></c:out>' >
			
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

				<%--  Minimum PSM filter --%>
				<%@ include file="/WEB-INF/jsp-includes/minimumPSM_Count_Filter.jsp" %>

				<tr>
					<td>Exclude links with:</td>
					<td>
						 <%--  Checkboxes --%>
						<%@ include file="/WEB-INF/jsp-includes/excludeLinksWith_Checkboxes_ProteinPages_Fragment.jsp" %>
					</td>
				</tr>
				
				<tr>
					<td>Exclude proteins with:</td>
					<td id="exclude_protein_types_block">
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
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerOverlayFragment.jsp" %>
					
						<c:set var="UpdateButtonText" value="Update From Database"/>
						
						<input type="button" value="${ UpdateButtonText }"  onclick="window.imagePagePrimaryRootCodeObject_OnWindow.call__refreshData()" >
						
						<c:if test="${ not empty onlySingleProjectSearchId }">
							<c:set var="projectSearchId" value="${ onlySingleProjectSearchId }"/>	
							<c:set var="page_JS_Object" value="imageViewerPageObject"/>
							
							<%@ include file="/WEB-INF/jsp-includes/defaultPageViewButtonFragment.jsp" %>
						</c:if>
						
						<c:set var="page_JS_Object" value="imageViewerPageObject"/>
							
						<%@ include file="/WEB-INF/jsp-includes/savePageViewButtonFragment.jsp" %>

						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp" %>
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
					
					
					<label id="show-linkable-positions-container" style="display: none;" ><span class="tool_tip_attached_jq" 
						data-tooltip="Toggle marking possible positions in protein where crosslinker(s) may react" 
						style="white-space:nowrap;" 
						><input type="checkbox" id="show-linkable-positions"
						>Show linkable positions</span></label>
					
					<label id="show-linkable-positions-disabled-container" style="display: none;" ><span style="white-space:nowrap;" class="disabled-checkbox tool_tip_attached_jq " 
						data-tooltip="Feature not available. At least one cross-linker has no parameters."
						style="white-space:nowrap;" 
						><input disabled="disabled" type="checkbox"
						>Show linkable positions</span></label>	
					
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle marking trypic positions in proteins" style="white-space:nowrap;" ><input type="checkbox" id="show-tryptic-cleavage-positions">Show tryptic positions</span></label>
					<label><span class="bar-only tool_tip_attached_jq" data-tooltip="Toggle showing protein termini labels" style="white-space:nowrap;" ><input type="checkbox" id="show-protein-termini">Show protein termini</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle shading of links based on spectrum counts" style="white-space:nowrap;" ><input type="checkbox" id="shade-by-counts">Shade by counts</span></label>
					
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle display of scale bar" style="white-space:nowrap;" ><input type="checkbox" id="show-scalebar" checked>Show scalebar</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle automatic sizing of protein bars. Uncheck to allow manual horizontal sizing and vertical spacing." style="white-space:nowrap;" ><input type="checkbox" id="automatic-sizing" checked>Automatic sizing</span></label>

					<label class="bar-only"><span style="white-space:nowrap;"
							class="tool_tip_attached_jq" data-tooltip="Toggle showing protein names to left of protein bars" 
							><input type="checkbox" id="protein_names_position_left" 
								>Protein Names On Left</span></label>
								
					<label><span style="white-space:nowrap;"
							class="tool_tip_attached_jq" data-tooltip="Toggle showing image as a circle plot" 
							><input type="checkbox" id="view-as-circle-plot" 
								>View as Circle Plot</span></label>

					<label><span style="white-space:nowrap;"
								 class="tool_tip_attached_jq" data-tooltip="If checked, only links found in all searches will be shown"
					><input type="checkbox" id="only-show-links-in-all-searches"
					>Only Show Links in All Searches</span></label>

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
							  <c:when test="${ fn:length( projectSearchIds ) <= 3 }">
							
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
							<option value="custom">Custom Regions</option>
							<c:if test="${ not empty annotation_data_webservice_base_url }">
							
								<%-- These require annotation_data_webservice_base_url to be populated --%>
								<option value="disopred3">Disordered / disopred3</option>
								<option value="psired3">Secondary Structure / psipred3</option>
							</c:if>
						</select>
					</span>
				
					<%--  Keep these next two items together on the same line --%>
					
					<span style="white-space:nowrap;" >

						<a class="bar-only tool_tip_attached_jq" 
							data-tooltip="Reset Proteins highlighting, flipping, positioning, and horizontal scaling"  
							href="javascript:window.imagePagePrimaryRootCodeObject_OnWindow.call__resetProteins()"  
							style="font-size:10pt;white-space:nowrap;"
							>[Reset Proteins]</a>

					</span>

					<%--  Keep these next two items together on the same line --%>
					
					<span style="white-space:nowrap;" >
		
					  <span id="manage_regios_exclusions_links_block"  style="display: none;">
					  
						<a class="tool_tip_attached_jq" data-tooltip="Protein Selection Regions Management" 
							style="font-size:10pt;white-space:nowrap;" 
							href="javascript:" onclick="window.imagePagePrimaryRootCodeObject_OnWindow.call__proteinBarRegionSelectionsOverlayCode_openOverlay()"
							>[Manage Protein Selections]</a>							
							
						<a class="tool_tip_attached_jq" data-tooltip="Choose which links are always un-highlighted (greyed out)." 
							style="font-size:10pt;white-space:nowrap;" 
							href="javascript:" onclick="window.imagePagePrimaryRootCodeObject_OnWindow.call__linkExclusionSelectionsOverlayCode_openOverlay()"
							>[Manage Link Exclusions]</a>							
					  </span>

					  	<%--  Display when no proteins shown  --%>
					  <span id="manage_regios_exclusions_links_disabled_block" >
						<span class=" non-link" style="font-size:10pt;white-space:nowrap;" 
								>[Manage Protein Selections]</span>
						<span class=" non-link" style="font-size:10pt;white-space:nowrap;" 
								>[Manage Link Exclusions]</span>
					  </span>
					</span>

					<c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" >
						<span style="white-space:nowrap;" >
	
							<a class="tool_tip_attached_jq" 
								data-tooltip="Manage user-defined protein region annotations--such as marking domains of interest."  
								href="javascript:" onclick="window.imagePagePrimaryRootCodeObject_OnWindow.call__customRegionManager_showManagerOverlay()" 
								style="font-size:10pt;white-space:nowrap;"
								>[Custom Annotation Manager]</a>
	
						</span>
					</c:if>
					
					<%--  Keep these next two items together on the same line --%>
					
					<span style="white-space:nowrap;" >
						<span id="svg-download">
							<a id="download_as_link"
									data-tooltip="Download current image as file." style="font-size:10pt;white-space:nowrap;" 
									href="javascript:" class="tool_tip_attached_jq download-svg">[Download Image]</a>
								
							<span id="svg-download-options">
								Choose file format:
								<a data-tooltip="Download as PDF file suitable for use in Adobe Illustrator or printing." id="svg-download-pdf" class="svg-download-option tool_tip_attached_jq" href="javascript:">PDF</a>
								<a data-tooltip="Download as PNG image file." id="svg-download-png" class="svg-download-option tool_tip_attached_jq" href="javascript:">PNG</a>
								<a data-tooltip="Download as scalable vector graphics file suitable for use in Inkscape or other compatible software." id="svg-download-svg" class="svg-download-option tool_tip_attached_jq" href="javascript:">SVG</a>
							</span>
						</span>
						
						<span id="download_svg_not_supported"
								data-tooltip="Not supported in this browser." 
								style="font-size:10pt;white-space:nowrap; display: none;" 
								class="tool_tip_attached_jq download-svg-link-non-link">[Download SVG]</span>
					</span>
					

						<span id="data-download">
							<a
								data-tooltip="Download data" style="font-size:10pt;white-space:nowrap;" 
								href="#" class="tool_tip_attached_jq download-link">[Download Data]</a>
								
							<span id="data-download-options">
								Choose file format:
								<%-- href="downloadMergedProteins.do?..."  --%>
								<a id="download-protein-data" href="javascript:" class="download-option tool_tip_attached_jq" style="margin-top:5px;"
									data-tooltip="Download all cross-links and mono-links as a tab-delimited file.">Download all cross-links and mono-links</a>
								<%--  downloadMergedProteinUDRs.do?... --%>
								<a id="download-protein-udrs" class="download-option tool_tip_attached_jq" href="javascript:"
									data-tooltip="Download all distinct unique distance restraints (cross-links and loop-links) as tab-delimited text."
									>Download distinct UDRs</a>
								
								<c:if test="${ showDownloadLinks_Skyline}">
									<br><span style="font-size:15px;">Skyline export</span><br>
									<c:if test="${ showDownloadLink_SkylineShulman }">
										<%-- downloadMergedProteinsPeptidesSkylineShulman.do?... --%>
										<a id="download-protein-shulman" class="download-option tool_tip_attached_jq" href="javascript:"
											data-tooltip="Export peptides for listed proteins for import into Skyline quant. tool. (Shulman et al)"
											>Export peptides for Skyline quant (Shulman et al)</a>
									</c:if>
									<%--  downloadMergedProteinsPeptidesSkylineEng.do?... --%>
									<a id="download-protein-skyline-prm" class="download-option tool_tip_attached_jq" href="javascript:"
										data-tooltip="Export peptides for listed proteins for Skyline PRM analysis. (Chavez et al)" 
										>Export peptides for Skyline PRM (Chavez et al)</a>
								</c:if>
								
								<br><span style="font-size:15px;">xiNET / xiVIEW export</span><br>
								<%-- downloadMergedProteinsFASTA.do?...  --%>
								<a id="download-fasta-file" class="download-option tool_tip_attached_jq" href="javascript:"
									data-tooltip="Download FASTA file for proteins found in cross-links or loop-links."
									>Download FASTA file</a>
								<%-- downloadMergedProteinsCLMS_CSV.do?...  --%>
								<a id="download-protein-xinet" class="download-option tool_tip_attached_jq" href="javascript:"
									data-tooltip="View CLMS-CSV formatted data for use in xiNET (http://crosslinkviewer.org/)" 
									>Export data for xiNET visualization</a>

								<a id="download-protein-xiview" class="download-option tool_tip_attached_jq" href="javascript:"
								   data-tooltip="View CLMS-CSV formatted data for use in xiVIEW (https://xiview.org/)"
								>Export data for xiVIEW visualization</a>
								
								<br><span style="font-size:15px;">xVis export</span><br>
								<%--  downloadMergedProteinsLengths.do?... --%>
								<a id="download-protein-lengths" class="download-option tool_tip_attached_jq" href="javascript:"
									data-tooltip="Export protein lengths file for cross-links and loop-links. For use in xVis (https://xvis.genzentrum.lmu.de/)" 
									>Export protein lengths for use in xVis.</a>
									<%--  downloadMergedProteinsXvis.do?...  --%>
								<a id="download-links-for-xvis" class="download-option tool_tip_attached_jq" href="javascript:"
									data-tooltip="Export cross-links and loop-links for use in xVis (https://xvis.genzentrum.lmu.de/)" 
									>Download cross-links and loop-links for use in xVis.</a>
							</span>
						</span>
					
				</div>	
									
					
				<div id="user-image-sizing-div" style="display:none; margin-top: 5px;">
					
					<div class="bar-only" style="margin:0px;">
						<span class="tool_tip_attached_jq" data-tooltip="Use slider to change spacing between protein bars">Vertical spacing:</span>&nbsp;&nbsp;&nbsp;
						<div id="vertical_spacing_slider_div" style="display: inline-block; width: 100px;"></div>  
						<span id="vertical_spacing_value" ></span> pixels
						
						&nbsp;&nbsp;&nbsp;
						
						<span class="tool_tip_attached_jq" data-tooltip="User slider to change width of protein bars">Horizontal scaling:</span>&nbsp;&nbsp;&nbsp;
						<div id="horizontal_scaling_slider_div" style="display: inline-block; width: 100px;"></div>  
						<span id="horizontal_scaling_value" >100</span>%
					</div>
					
					
					<div class="circle-only" style="margin:0px;">
						<span class="tool_tip_attached_jq" data-tooltip="User slider to change circle diameter">Circle diameter:</span>&nbsp;&nbsp;&nbsp;
						<div id="circle_diameter_slider_div" style="display: inline-block; width: 100px;"></div>  
						<span id="circle_diameter_value" ></span> pixels
					</div>

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
							onclick="window.imagePagePrimaryRootCodeObject_OnWindow.call__openSelectProteinSelect( { clickedThis : this, addProteinsClicked : true } )" 
							value="Add Protein">
					  </div>
					</div>
				</div>		
				
				<div style="clear: both;"></div>		
					
			</div>
		
				<%--  Only shown when no proteins selected --%>
				
			<div id="no_proteins_add_protein_outer_container" style="display: none;">
			
				<div class=" no-proteins-add-protein-block clickable "
					onclick="window.imagePagePrimaryRootCodeObject_OnWindow.call__openSelectProteinSelect( { clickedThis : this, addProteinsClicked : true } )" >

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
	
	
		<!--  Overlay for custom region manager -->
	
			<div id="custom_region_manager_modal_dialog_overlay_background" class="custom_region_manager_modal_dialog_overlay_background" style="display: none;"  >
			
			</div>
	
		<div id="custom-region-manager-overlay-container" class="custom-region-manager-overlay-container" style="display:none;">

				<div id="custom_region_manager_overlay_header" class="custom-region-manager-overlay-header" style="width:100%; " >
					<h1 id="custom_region_manager_overlay_X_for_exit_overlay" class="custom-region-manager-overlay-X-for-exit-overlay" >X</h1>
					<h1 id="custom_region_manager_header_text" class="custom-region-manager-overlay-header-text">
						Custom Protein Region Manager
					</h1>
				</div>
				
				
				<div id="custom_region_manager_left_pane" class="custom-region-manager-left-pane">

					<div style="width:100%;text-align:center;font-weight:bold;font-size:14pt;margin-top:5px;">Select Protein</div>


					<div id="custom_region_manager_protein_list" class="custom-region-manager-protein-list">
					
					</div>


				</div>
				
				<div style="font-size:14pt;" id="custom_region_manager_right_pane_empty" class="custom-region-manager-right-pane">
					<div style="margin:10px;">
						This tools allows you to define custom protein domain annotations that may optionally appear on the
						protein bars in the image viewer. These include binding domains, structural motifs, or any other
						annotation you like.
						
						<br><br>
						
						These may be viewed by choosing &quot;Custom Regions&quot; in the &quot;Show Feature Annotations&quot; select list.
						
						<br><br>
						
						Choose a protein to the left to define domains for that protein.
					</div>
				</div>
				
				
				<div style="display:none;" id="custom_region_manager_right_pane_protein_selected" class="custom-region-manager-right-pane">

					<div style="width:100%;text-align:center;font-weight:bold;font-size:14pt;margin-top:5px;">Add/Change Annotations</div>
					
					<div id="custom_region_manager_error_div" style="margin:5px;display:none;width:350px;color:red;font-size:12pt;border-width:1px;border-color:red;border-style:solid;padding:5px;"></div>
					<div id="custom_region_manager_info_div" style="margin:5px;display:none;width:350px;color:#7c783c;font-size:12pt;border-width:1px;border-color:#7c783c;border-style:solid;padding:5px;"></div>
					<div id="custom_region_manager_success_div" style="margin:5px;display:none;width:350px;color:#3f7c3c;font-size:12pt;border-width:1px;border-color:#3f7c3c;border-style:solid;padding:5px;"></div>
					
					
					<!--  Where we'll put the custom annotations for the selected protein -->
					<div id="custom_region_manager_right_pane_add_domain">
					
			
			 			<div style="margin-top:10px;" id="custom_region_manager_add_domains_list">
						</div>
					
					</div>
					

				</div>
				

		</div>
	
	
		<script id="custom_region_manager_region_form_template" type="text/x-handlebars-template">
			
						<div style="margin-top:10px;">							
							<form class="region-input-form" style="font-size:12pt;">
							
								Start: <input class="region_form_input" type="text" id="startPosition" size="4" value="{{startPosition}}"/>
								End: <input class="region_form_input" type="text" id="endPosition" size="4" value="{{endPosition}}"/>

  								{{#if annotationColor}}
									Color: <span id="custom_region_manager_right_pane_add_domain_color" class="color_picker_box" style="background-color:{{annotationColor}};">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> <span style="font-size:10pt;">(click color)</span>
  								{{else}}
									Color: <span id="custom_region_manager_right_pane_add_domain_color" class="color_picker_box" style="background-color:#ff0000;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> <span style="font-size:10pt;">(click color)</span>
								{{/if}}

								<div style="margin-top:5px;">Annotation: <input id="annotation_text" class="region_form_input" type="text" size="30" maxlength="2000" value="{{annotationText}}"/></div>
								
							</form>
						</div>
		</script>
	
	
		<script id="custom_region_manager_region_form_buttons" type="text/x-handlebars-template">
			
						<div style="margin-top:10px;" id="custom_region_manager_right_pane_form_buttons">
						 <form>
							<input id="custom_region_manager_create_new_region_button" type="button" value="+Create New Region" class="tool_tip_attached_jq" data-tooltip="Add custom domain annotation for protein." />
							<input id="custom_region_manager_save_button" type="button" value="Save to Database" class="tool_tip_attached_jq" data-tooltip="Save changes to database." />
							<input id="custom_region_manager_cancel_button" type="button" value="Reset" class="tool_tip_attached_jq" data-tooltip="Discard changes." />
						 </form>
						 <span style="font-size:10pt;">Regions with no start, end, and annotation will be ignored.</span>
						</div>
		</script>
	
	
	
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
				
					<div id="protein_list_outer_container" class=" protein-list-outer-container">
						<div id="protein_list_container" class=" protein-list-container">
						</div>
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
				  
				  <c:set var="imageOrStructurePageForAnnDispMgmt" value="${ true }" />
						
					<%--  Block for user choosing which annotation types to display  --%>
				  <%@ include file="/WEB-INF/jsp-includes/annotationDisplayManagementBlock.jsp" %>

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
					<div style="float: right; padding-right: 5px; padding-top: 10px;">
				  		<a href="http://proxl-web-app.readthedocs.io/en/latest/using/image-bar.html"  target="_help_window" id="view_link_exclusions_overlay_X_for_exit_overlay_help_link"
				  			><img src="images/icon-help.png" 
				  		></a>
					</div>
					<h1 id="view_protein_bar_highlighting_overlay_header_text" class="view-protein-bar-highlighting-overlay-header-text" 
						>Protein Bar Region Selections</h1>
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
	
			<div class=" bar_region_jq " style="padding-top: 2px; position: relative;" 
				data-region_unique_id="{{regionUniqueId}}"
				data-start_prev_value="{{start}}"
				data-end_prev_value="{{end}}"
				> <%--  position: relative; to support positioning error messages --%>

				start: <input type="text" style="width: 30px;" value="{{start}}" class=" start_jq ">  
				End: <input type="text" style="width: 30px;" value="{{end}}" class=" end_jq ">  

				<input type="image" src="images/icon-delete-small.png"  
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



		<%--  --------------------------------------------------------------------  --%>
	

			<%--  View Link Exclusions Overlay Div,  this overlay div is for choosing which links to exclude by choosing proteins bars and regions --%>

			
				<%--  View Link Exclusions Overlay Background --%>
			
			
			<div id="view_link_exclusions_modal_dialog_overlay_background" class="view-link-exclusions-modal-dialog-overlay-background" style="display: none;"  >
			
			</div>
			
				<%--  View Link Exclusions Overlay Div --%>
			
			<div id="view_link_exclusions_overlay_div" class=" view-link-exclusions-overlay-div " style="display: none; "  >
			
				<div id="view_link_exclusions_overlay_header" class="view-link-exclusions-overlay-header" style="width:100%; " >
					<h1 id="view_link_exclusions_overlay_X_for_exit_overlay" class="view-link-exclusions-overlay-X-for-exit-overlay" >X</h1>
					<div style="float: right; padding-right: 5px; padding-top: 10px;">
				  		<a href="http://proxl-web-app.readthedocs.io/en/latest/using/image-bar.html#highlight-proteins-regions"  target="_help_window" id="view_link_exclusions_overlay_X_for_exit_overlay_help_link"
				  			><img src="images/icon-help.png" 
				  		></a>
					</div>
					<h1 id="view_link_exclusions_overlay_header_text" class="view-link-exclusions-overlay-header-text" 
						>Link Exclusion</h1>
				</div>
				<div id="view_link_exclusions_overlay_body" class="view-link-exclusions-overlay-body" >
				  <div style="margin-bottom: 5px; font-weight: bold;" >
					Select pairs of proteins or regions between which links will always be greyed-out.
				  </div>
				  <%--  current exclusions --%>
				  <div style="margin-bottom: 5px; font-weight: bold;" >
				  		<span id="view_link_exclusions_overlay_no_exclusions_text">No </span>Current Exclusions
				  </div>
				  <div id="view_link_exclusions_overlay_excluded_items_outer_block"
				  		class=" excluded-list-outer-block " >
				  			<%-- List of current exclusions is inserted into this div --%>
				  	  <div id="view_link_exclusions_overlay_excluded_items_data_div" class= " excluded-list-block " >
				  	  </div>
				  </div>
				  		
				  <%--  Lists of proteins and regions for choosing new exclusion --%>
				  		  	
				 <div  style="clear: both; margin-top: 5px;">
					 <div style="margin-top: 15px; margin-bottom: 5px; font-weight: bold;">
					  		Add Exclusion: choose one entry from each list and click "Exclude".
					 </div>
					  
					  		<%-- Right block - float right --%>
					 <div class=" exclude-choices-outer-block  exclude-choices-outer-block-2 ">
					  		<%--  Lists of proteins and regions inserted into this div.  Same as in Left Block --%>
						<div id="view_link_exclusions_overlay_exclude_choices_2"
						  		class=" exclude-choices-block exclude_choices_block_jq ">
						</div>
					 </div>
							<%-- Left block --%>  			
					 <div class=" exclude-choices-outer-block exclude-choices-outer-block-1 ">
					  		<%--  Lists of proteins and regions inserted into this div.  Same as in Right Block --%>
					 	<div id="view_link_exclusions_overlay_exclude_choices_1"
						  		class=" exclude-choices-block exclude_choices_block_jq ">
					  	</div>
					</div>
					 
					 <div  style="clear: both; margin-top: 5px;">
					  	  <div style="display:inline-block;position:relative; "> <%-- outer div to support overlay div when button disabled --%>
						  	 <input type="button" value="Exclude" id="view_link_exclusions_overlay_add_exclusion_button"
						  		 class="tool_tip_attached_jq" data-tooltip="Add Exclusion." disabled="disabled">
								<%-- overlay div to provide tooltip for button --%>
							<div id="view_link_exclusions_overlay_add_exclusion_button_disabled_cover_div" 
								class=" tool_tip_attached_jq " 
								style="position:absolute;left:0;right:0;top:0;bottom:0;" 
								data-tooltip="Click on an entry from each list above and click here to exclude that combination." ></div>
						  </div>
	
					   <div style="padding-top: 3px; display:inline-block;position:relative; ">
						 <span id="view_link_exclusions_overlay_new_excluded_item_pre_show">
						 </span>
					   </div>
					 </div>

				  	<div style="margin-top: 5px; margin-bottom: 5px;">
					  	<input type="button" value="Close" id="view_link_exclusions_overlay_protein_bars_close_button"
					  		 class="tool_tip_attached_jq" data-tooltip="Close.">
					</div>
				</div>			  	
			</div>  <%--  END  <div id="view-link-exclusions-overlay-body" class="view-link-exclusions-overlay-body" > --%>

		</div>  <%--  END  <div id="view_link_exclusions_overlay_div" class=" view-link-exclusions-overlay-div " style="display: none; "  > --%>

		<%-- !!!   Handlebars template Manage Exclusions  !!!!!!!!!   --%>
		
		<script id="view_link_exclusions_overlay_single_existing_exclusion_template"  type="text/x-handlebars-template">
	
			<div class=" exclusion_entry_jq " style="padding-top: 2px; position: relative;" 
				data-protein_uid_1="{{ proteinUID_1 }}"
				data-region_uid_1="{{ regionUID_1 }}"
				data-protein_uid_2="{{ proteinUID_2 }}"
				data-region_uid_2="{{ regionUID_2 }}"
				>
				<input type="image" src="images/icon-delete-small.png"  
					class=" view_link_exclusions_overlay_exclusion_remove_button_jq tool_tip_attached_jq" 
					data-tooltip="Remove Exclusion" > 
				{{ proteinName_1 }}{{ regionRange_1 }}&mdash;{{ proteinName_2 }}{{ regionRange_2 }}
			</div>  
		</script>	<%-- &8212; is long dash.  &mdash; is another option for long dash --%>
		
			<%--  <div id="view_link_exclusions_overlay_new_excluded_item_pre_show"> --%>
		<script id="view_link_exclusions_overlay_exclusion_choice_pre_show_template"  type="text/x-handlebars-template">
			<div>
				{{ proteinName_1 }}{{ regionRange_1 }}&mdash;{{ proteinName_2 }}{{ regionRange_2 }}
			</div>
		</script>  <%-- &8212; is long dash.  &mdash; is another option for long dash --%>	
		
		<%--  Click handler will be added to root element of template --%>
		<script id="view_link_exclusions_overlay_single_exclusion_choice_template"  type="text/x-handlebars-template">
	
			<div class=" exclusion-choice-option exclusion_choice_entry_jq " style="padding-top: 2px; cursor: pointer;" 
				data-protein_uid="{{ proteinUID }}"
				data-region_uid="{{ regionUID }}"
				>
				{{ proteinName }}{{ regionRange }}

			</div>

		</script>	
								

			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	
			
	</div>  <%--  END  <div class="overall-enclosing-block"> --%>

		<script type="text/javascript" src="static/js_generated_bundles/data_pages/imageView-bundle.js?x=${cacheBustValue}"></script>
		
<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
