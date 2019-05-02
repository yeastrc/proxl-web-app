<%@page import="org.yeastrc.xlink.www.constants.PageLinkTextAndTooltipConstants"%>
<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">View Structural Analysis - <c:out value="${ headerProject.projectTblData.title }"></c:out></c:set>

 <c:set var="pageBodyClass" >project-page view-merged-structure-page</c:set>

 <c:set var="helpURLExtensionForSpecificPage" >en/latest/using/structure.html</c:set>

 <c:set var="headerAdditions">
 
		<script type="text/javascript" src="js/libs/jquery.tablesorter_Modified.js"></script>
		<script type="text/javascript" src="js/libs/spin.min.js"></script> 
		<script type="text/javascript" src="js/libs/base64.js"></script> 
		<script type="text/javascript" src="js/libs/jquery.qtip.min.js"></script>

		<%--  Color Picker - jQuery Plugin --%>
		<script type="text/javascript" src="js/libs/colorpicker/colorpicker.js"></script>


		<%--  Compression --%>
		
		<script type="text/javascript" src="js/libs/lz-string/lz-string.min.js"></script>
		
		<%--  Non-Minified version --%>
		<%-- 
		<script type="text/javascript" src="js/libs/lz-string/lz-string.js"></script>
		--%>
		
		<%--  Used by lz-string.min.js --%>
		<script type="text/javascript" src="js/libs/lz-string/base64-string.js"></script>

		

		<!-- Handlebars templating library   -->
		
		<%--  
		<script type="text/javascript" src="js/libs/handlebars-v2.0.0.js"></script>
		--%>
		
		<!-- use minimized version  -->
		<script type="text/javascript" src="js/libs/handlebars-v2.0.0.min.js"></script>


		
		
<%--  Start of Lorikeet Core Parts --%>		

		<script src="js/libs/jquery-ui-1.12.1.min.js"></script>
		
		<%--  Only load the excanvas.min.js if it is IE 8 or lower.  IE 8 does not support HTML5 so this is a way to have HTML5 canvas support --%>
		<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="js/lorikeet_google_code/excanvas.min.js"></script><![endif]-->
		
		<script src="js/lorikeet/jquery.flot.js"></script>
		<script src="js/lorikeet/jquery.flot.selection.js"></script>
		
		<script src="js/lorikeet/specview.js?x=${cacheBustValue}"></script>
		<script src="js/lorikeet/peptide.js?x=${cacheBustValue}"></script>
		<script src="js/lorikeet/aminoacid.js?x=${cacheBustValue}"></script>
		<script src="js/lorikeet/ion.js?x=${cacheBustValue}"></script>		
		
<%--  End of Lorikeet Core Parts --%>		

		
		<script type="text/javascript" src="js/libs/snap.svg-min.js"></script> <%--  Used by lorikeetPageProcessing.js --%>
				
				<%-- 
					The Struts Action for this page must call GetProteinNamesTooltipConfigData
					This include is required on this page:
					/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp
				  --%>
<%--  Replaced with the JS bundle listed next
			<script type="text/javascript" src="js/createTooltipForProteinNames.js?x=${cacheBustValue}"></script>
  --%>
  		
		<link type="text/css" rel="stylesheet" href="css/jquery.qtip.min.css" />
		
		<link rel="stylesheet" href="css/tablesorter.css" type="text/css" media="print, projection, screen" />
		
		<%--  Color Picker --%>
		<%--
		<link rel="stylesheet" media="screen" type="text/css" href="css/libs/colorpicker.css" />
		--%>
		
		<link rel="stylesheet" media="screen" type="text/css" href="css/libs/colorpicker_custom_colors.css" />
		
		

		<link REL="stylesheet" TYPE="text/css" HREF="css/lorikeet.css">
		
	<%-- includ javascript library for glmol structure viewer --%>
	<script type="text/javascript" src="js/libs/bio-pv.min.js"></script>

</c:set>

<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

	<%--  used by createTooltipForProteinNames.js --%>
	<%@ include file="/WEB-INF/jsp-includes/proteinNameTooltipDataForJSCode.jsp" %>

	<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
		
	<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
	<%@ include file="/WEB-INF/jsp-includes/viewPsmPerPeptideLoadedFromWebServiceTemplateFragment.jsp" %>
		
		<%@ include file="/WEB-INF/jsp-includes/specificProteinDataPerSearchProtIdsPositionsFragment.jsp" %>
		
		
		<%@ include file="/WEB-INF/jsp-includes/viewLooplinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewCrosslinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
		<%@ include file="/WEB-INF/jsp-includes/viewMonolinkReportedPeptidesLoadedFromWebServiceTemplateFragment.jsp" %>
		
		
		
	<div class="overall-enclosing-block">
	
	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
		<div id="invalid_url_no_data_after_hash_div" style="display: none;" >
		
			<h1 style="color: red;">Invalid URL, data missing</h1>

		</div>
	

	<c:forEach var="projectSearchId" items="${ projectSearchIds }">
	
		<%--  Put Search Ids on the page for the JS code --%>
		<input type="hidden" class=" project_search_id_jq " value="<c:out value="${ projectSearchId }"></c:out>">
	</c:forEach>
	
		<%--  projectSearchIdsUserOrdered set to PeptideProteinCommonForm.DO_NOT_SORT_PROJECT_SEARCH_IDS_YES if true, empty string if false (from 'ds' query string param value).  
			Javascript on this page will copy this to some URLs for Webservices and links to other pages --%>
	<input type="hidden" id="project_search_ids_user_ordered" value="<c:out value="${ projectSearchIdsUserOrdered }"></c:out>" />
	
	<c:if test="${ not empty onlySingleProjectSearchId }">
	
		<%--  Only one search id so get Default Page URL values for that search id for links to other pages for navigation.
			  Javascript will use these values for the navigation if these hidden input fields are not empty. 
		--%>
		<input type="hidden" id="viewSearchPeptideDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/peptide" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewSearchCrosslinkProteinDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/crosslinkProtein" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewProteinCoverageReportDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/proteinCoverageReport" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
		<input type="hidden" id="viewMergedImageDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="/image" projectSearchId="${ onlySingleProjectSearchId }"></proxl:defaultPageUrl>">
	</c:if>
					
	<script id="qc_page_link_text" type="text/text"><%= PageLinkTextAndTooltipConstants.QC_LINK_TEXT %></script>
	<script id="qc_page_link_tooltip" type="text/text"><%= PageLinkTextAndTooltipConstants.QC_LINK_TOOLTIP %></script>
					
	
		
		<div>
	
			<h2 style="margin-bottom:5px;">View <span id="merged_label_text_header" style="display: none;" >merged </span>search results:</h2>
	
			<div id="navigation-links"  class=" navigation-links-block ">
				<span id="navigation_links_except_image"></span>
					
				<span id="image_viewer_link_span"></span>
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
					<td><div id="taxonomy-checkboxes"></div></td>
				</tr>
				
				
				<tr>
					<td>&nbsp;</td>
					<td>
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerOverlayFragment.jsp" %>
					
						<c:set var="UpdateButtonText" value="Update From Database"/>
						
						<input type="button" value="${ UpdateButtonText }"  onclick="refreshData()" >
						
						<c:if test="${ not empty onlySingleProjectSearchId }">

							<c:set var="projectSearchId" value="${ onlySingleProjectSearchId }"/>	
								
							<c:set var="page_JS_Object" value="structureViewerPageObject"/>
							
							<%@ include file="/WEB-INF/jsp-includes/defaultPageViewButtonFragment.jsp" %>
						</c:if>

						<c:set var="page_JS_Object" value="structureViewerPageObject"/>
						
						<%@ include file="/WEB-INF/jsp-includes/savePageViewButtonFragment.jsp" %>
	
						<%@ include file="/WEB-INF/jsp-includes/sharePageURLShortenerButtonFragment.jsp" %>
					</td>
				</tr>
			
			</table>
			</form>
			
		</div>
	
		<hr>


		<div>


				<div >
				  <span style="margin-left:10px;" id="pdb-file-selector-location">
				  
					<c:if test="${ authAccessLevel.writeAllowed }" >
						<a href="javascript:openPDBUploadOverlay()">+Upload PDB File</a>
					</c:if>	
				  </span>
				  
				</div>

				<div>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing crosslinks on structure" style="white-space:nowrap;" ><input type="checkbox" id="show-crosslinks" checked>Show crosslinks</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing looplinks on structure" style="white-space:nowrap;" ><input type="checkbox" id="show-looplinks">Show looplinks</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle showing monolinks on structure" style="white-space:nowrap;" ><input type="checkbox" id="show-monolinks">Show monolinks</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Toggle marking possible positions where crosslinker(s) may react " style="white-space:nowrap;" ><input type="checkbox" id="show-linkable-positions" checked>Show linkable positions</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Recommended - checking this causes only the shortest instance of a UDR to be shown. Unchecking attempts to show all discrete instances of all UDRs. See the help pages (? at top right of page) for more details." style="white-space:nowrap;" ><input type="checkbox" id="show-unique-udrs" checked>Show UDRs once</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Shade links drawn on structure by spectrum count" style="white-space:nowrap;" ><input type="checkbox" id="shade-by-counts">Shade by counts</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Color structure of backbone to indicate sequence coverage" style="white-space:nowrap;" ><input type="checkbox" id="show-coverage">Show sequence coverage</span></label>

					<span class="tool_tip_attached_jq" data-tooltip="Select method used to color links on structure" style="white-space:nowrap;" >Color links by:
						<select id="select-link-color-mode">
							<option value="length">Length</option>
							<option value="type">Type</option>

							<c:choose>
							  <c:when test="${ fn:length( projectSearchIds ) <= 3 }">
							
								<%-- Only shown when the number of searches is <= the number supported by 'Color by search' --%>
								<option value="search">Search/Run</option>
							  </c:when>
							  <c:otherwise>
							  	<%--  Disable the color by search option --%>
							  	<option value="search_disabled" disabled="disabled">Search/Run</option>
							  </c:otherwise>
							</c:choose>
						</select>
					</span>

					<span class="tool_tip_attached_jq" data-tooltip="Selecting a rendering style for the structure" style="white-space:nowrap;" >Render mode:
						<select id="select-render-mode">
							<option value="cartoon">Cartoon</option>
							<option value="sline">Smooth Line</option>
							<option value="trace">Trace</option>
							<option value="lines">Lines (slow)</option>
							<option value="points">Points (slow)</option>
							<%-- <option value="spheres">Spheres (unstable?)</option> --%>
						</select>
					</span>
					
					<span class="tool_tip_attached_jq" data-tooltip="Pop the viewer out into a separate window. Useful for viewing structure on a large or separate monitor." id="popout-link-span" style="white-space:nowrap;" ><a href="javascript:popoutViewer()">[Popout Viewer]</a></span>

				</div>
		</div>


			<table style="margin-top:20px;border:0px;padding:0px;">
				<tr>
					<td style="width:500px;">
						<div id="pdb-title-div" style="margin-top:0px;margin-bottm:5px;vertical-align:top;"></div>					
					</td>
					
					<td style="min-width:500px;width:auto;">
						<div id="pdb-info-nav-div" style="margin-left:20px;">
							<div id="pdb-info-nav-chain-choice" style="">
								<span style="font-size:14pt;">PDB Chain to Protein Map</span> <span style="font-size:10pt;"><a class="tool_tip_attached_jq" data-tooltip="Show report of visible crosslinks and distances" href="javascript:showDistanceReportPanel()">[Show Distance Report]</a></span>
							</div><div id="pdb-info-nav-report-choice" style="display:none;">
								<span style="font-size:14pt;">Distance Report</span> <span style="font-size:10pt;"><a class="tool_tip_attached_jq" data-tooltip="Show which PDB chains are mapped to which proteins from the experiment" href="javascript:showChainMapPanel()">[Show PDB Chain to Protein Map]</a></span>
							</div>
						</div>
					</td>
				
				</tr>
				<tr>
					<td style="width:500px;">
						<div style="width:500px;height:500px;border-width:1px;border-style:solid;border-color:#A55353;">
							<div id="glmol-div" style="width:500px;height:500px;display:inline;"></div>
						</div>
						
						<%-- Legend --%>
						
						<div id="legend-div" style="margin-top:5px;vertical-align:top; display: none;">

						  <h2 style="display:inline;font-size:12pt;margin-top:5px;">Legend:</h2>
						  
						  <div id="legend_by_link_length" style="">
						  
							<div id="legend_by_link_length_main" style="font-size:10pt;margin-left:20px; margin-top: 5px;">
							
							 <div >
							  <span style="white-space:nowrap;">
							 	<span id="legend_by_link_length_color_block_short" 
							 		class=" tool_tip_attached_jq color_picker_link_length_jq " 
							 		data-tooltip="Click to change the color"
							 		style="display:inline-block;width:15px;height:15px; cursor: pointer;"></span> 
							 	&lt;= <span id="legend_by_link_length_cutoff_1"></span> Å 
							  </span>
							  <span style="white-space:nowrap;margin-left:15px;">
							 	<span id="legend_by_link_length_color_block_medium"
							 		class=" tool_tip_attached_jq color_picker_link_length_jq " 
							 		data-tooltip="Click to change the color"
							 		style="display:inline-block;width:15px;height:15px; cursor: pointer;"></span> 
							 	&lt;= <span id="legend_by_link_length_cutoff_2"></span> Å 
							  </span>
							  <span style="white-space:nowrap;margin-left:15px;">
							 	<span id="legend_by_link_length_color_block_long" 
							 		class=" tool_tip_attached_jq color_picker_link_length_jq " 
							 		data-tooltip="Click to change the color"
							 		style="display:inline-block;width:15px;height:15px; cursor: pointer;"></span>
							 	&gt; <span id="legend_by_link_length_cutoff_3"></span> Å 
							  </span>
							  <span style="margin-left:15px; white-space: nowrap;">
							 	[<a href="javascript:" onclick="userChangeDistanceConstraintsInterface()"
							 		>Change cutoffs</a>]</span>

<%-- 							 		
							  <span style="margin-left:2px; white-space: nowrap;">
							 	[<a href="javascript:" onclick="removeUserDistanceConstraints()"
							 		>Reset cutoffs</a>]</span>
--%>							 		
							 </div>
							 <div style="font-size:10pt; margin-top: 5px;">
							  	Click color to change
								
								<span style="margin-left:2px; white-space: nowrap;">
								 	[<a href="javascript:" onclick="clearUserColorByLength()"
								 		>Reset legend colors</a>]</span>	
							 </div>
							 		
							 								 		
							</div>

							<div id="legend_by_link_length_change_cutoffs" style="font-size:10pt;margin-left:20px; display: none;">
							
								<span>Fill in new distance cutoffs for color coding:</span>
								<span style="white-space:nowrap;margin-left:15px;">
									
									<span id="userConstraintFormShortDistance_color_block"
										style="display:inline-block;width:22px;height:11px;"></span>
										
									<input id="userConstraintFormShortDistance" type="text" style="width:30px;" value="">Å
									 
									<span id="userConstraintFormMediumDistance_color_block" 
										style="display:inline-block;width:22px;height:11px;"></span> 
										
									<input id="userConstraintFormLongDistance" type="text" style="width:30px;" value="">Å
									 
									<span id="userConstraintFormLongDistance_color_block"  
										style="display:inline-block;width:22px;height:11px;"></span>
									
									<span style="margin-left:15px">
										[<a href="javascript:" onclick="updateUserDistanceConstraints()">Update</a>]
									</span>
									<span style="margin-left:5px">
										[<a href="javascript:" onclick="drawLegend()">Cancel</a>]
									</span>
								</span>
								<span style="margin-left:15px;" id="userConstraintsErrorSpan"></span>
						
							</div>
													  
						  </div> <%-- End legend by link length --%>
						
						  <div id="legend_by_link_type" style="font-size:10pt;margin-left:20px; margin-top: 5px;">

							 <div>							

							    <span style="white-space:nowrap;">
									<span id="legend_by_link_type_crosslink_color_block" 
								 		class=" tool_tip_attached_jq color_picker_link_type_jq " 
								 		data-tooltip="Click to change the color"
										style="display:inline-block;width:15px;height:15px; cursor: pointer;" ></span>
									Crosslinks
								</span>
							    <span style="white-space:nowrap;margin-left:15px;">
									<span id="legend_by_link_type_looplink_color_block" 
								 		class=" tool_tip_attached_jq color_picker_link_type_jq " 
								 		data-tooltip="Click to change the color"
										style="display:inline-block;width:15px;height:15px; cursor: pointer;" ></span>
									Looplinks
								</span>
							    <span style="white-space:nowrap;margin-left:15px;">
									<span id="legend_by_link_type_monolink_color_block" 
								 		class=" tool_tip_attached_jq color_picker_link_type_jq " 
								 		data-tooltip="Click to change the color"
										style="display:inline-block;width:15px;height:15px; cursor: pointer;" ></span>
									Monolinks
								</span>
							 </div>							
							 <div style="font-size:10pt; margin-top: 5px;">
							  	Click color to change
								
								<span style="margin-left:2px; white-space: nowrap;">
								 	[<a href="javascript:" onclick="clearUserColorByLinkType()"
								 		>Reset legend colors</a>]</span>	
							 </div>							
						  </div> <%-- End legend by link type --%>

						  <div id="legend_by_search" style="font-size:10pt;margin-top: 5px;">
						  
						   <div>
							<span id="legend_by_search_container_for_search_1" class=" legend_by_search_container_for_search_jq "
								style="white-space:nowrap;margin-left:15px;">
								<span id="legend_by_search_color_block_for_search_1"
								 		class=" tool_tip_attached_jq color_picker_by_search_jq " 
								 		data-tooltip="Click to change the color"
										style="display:inline-block;width:15px;height:15px; cursor: pointer;" ></span>
								Search: <span class=" legend_by_search_search_id_for_search_1_jq " ></span>
							</span>
							<span id="legend_by_search_container_for_search_2" class=" legend_by_search_container_for_search_jq "
								style="white-space:nowrap;margin-left:15px;">
								<span id="legend_by_search_color_block_for_search_2"
								 		class=" tool_tip_attached_jq color_picker_by_search_jq " 
								 		data-tooltip="Click to change the color"
										style="display:inline-block;width:15px;height:15px; cursor: pointer;" ></span>
								Search: <span class=" legend_by_search_search_id_for_search_2_jq " ></span>
							</span>
							<span id="legend_by_search_container_for_search_3" class=" legend_by_search_container_for_search_jq "
								style="white-space:nowrap;margin-left:15px;">
								<span id="legend_by_search_color_block_for_search_3"
								 		class=" tool_tip_attached_jq color_picker_by_search_jq " 
								 		data-tooltip="Click to change the color"
										style="display:inline-block;width:15px;height:15px; cursor: pointer;" ></span>
								Search: <span class=" legend_by_search_search_id_for_search_3_jq " ></span>
							</span>
							<span id="legend_by_search_container_for_search_1_2" class=" legend_by_search_container_for_search_jq "
								style="white-space:nowrap;margin-left:15px;">
								<span id="legend_by_search_color_block_for_search_1_2"
								 		class=" tool_tip_attached_jq color_picker_by_search_jq " 
								 		data-tooltip="Click to change the color"
										style="display:inline-block;width:15px;height:15px; cursor: pointer;" ></span>
								Search: <span class=" legend_by_search_search_id_for_search_1_jq " ></span>,
									<span class=" legend_by_search_search_id_for_search_2_jq " ></span>
							</span>
							<span id="legend_by_search_container_for_search_1_3" class=" legend_by_search_container_for_search_jq "
								style="white-space:nowrap;margin-left:15px;">
								<span id="legend_by_search_color_block_for_search_1_3"
								 		class=" tool_tip_attached_jq color_picker_by_search_jq " 
								 		data-tooltip="Click to change the color"
										style="display:inline-block;width:15px;height:15px; cursor: pointer;" ></span>
								Search: <span class=" legend_by_search_search_id_for_search_1_jq " ></span>,
									<span class=" legend_by_search_search_id_for_search_3_jq " ></span>
							</span>
							<span id="legend_by_search_container_for_search_2_3" class=" legend_by_search_container_for_search_jq "
								style="white-space:nowrap;margin-left:15px;">
								<span id="legend_by_search_color_block_for_search_2_3"
								 		class=" tool_tip_attached_jq color_picker_by_search_jq " 
								 		data-tooltip="Click to change the color"
										style="display:inline-block;width:15px;height:15px; cursor: pointer;" ></span>
								Search: <span class=" legend_by_search_search_id_for_search_2_jq " ></span>,
									<span class=" legend_by_search_search_id_for_search_3_jq " ></span>
							</span>
							<span id="legend_by_search_container_for_search_1_2_3" class=" legend_by_search_container_for_search_jq "
								style="white-space:nowrap;margin-left:15px;">
								<span id="legend_by_search_color_block_for_search_1_2_3"
								 		class=" tool_tip_attached_jq color_picker_by_search_jq " 
								 		data-tooltip="Click to change the color"
										style="display:inline-block;width:15px;height:15px; cursor: pointer;" ></span>
								Search: 
									<span class=" legend_by_search_search_id_for_search_1_jq " ></span>,
									<span class=" legend_by_search_search_id_for_search_2_jq " ></span>,
									<span class=" legend_by_search_search_id_for_search_3_jq " ></span>
							</span>
						   </div>
						   <div style="font-size:10pt; margin-top: 5px;margin-left:15px;">
							  	Click color to change
								
								<span style="margin-left:2px; white-space: nowrap;">
								 	[<a href="javascript:" onclick="clearUserColorBySearch()"
								 		>Reset legend colors</a>]</span>	
							</div>							  
						  </div> <%-- End legend by search --%>

						</div>
					</td>
					<td style="vertical-align:top;min-width:500px;width:auto;">
					
						<div id="pdb-info-container-div" style="padding:0px;min-width:500px;width:auto;margin-left:20px;margin-bottom:0px;margin-top:0px;">
							<div id="pdb-info-div" style="margin:0px;padding:0px;min-width:500px;width:auto;height:500px;overflow-y:scroll;border-width:1px;border-style:solid;border-color:#A55353;">
								<div id="chain-list-div" style="margin-left:10px;font-size:14px;"></div>
								<div id="distance-report-div" style="display:none;margin-left:10px;font-size:14px;"></div>
							</div>
						</div>
	
			
						<!--  Modal dialog for confirming deleting a PDB file -->
				
							<!--  Div behind modal dialog div -->
				
						<div class="modal-dialog-overlay-background   delete_pdb_file_overlay_show_hide_parts_jq delete_pdb_file_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
							id="delete_pdb_file_overlay_background" ></div>
						
								<!--  Inline div for positioning modal dialog on page -->
						<div class="delete-pdb-file-overlay-containing-outermost-div " id="delete_pdb_file_overlay_containing_outermost_div_inline_div"  >
			
						  <div class="delete-pdb-file-overlay-containing-outer-div " >
						
				
								<!--  Div overlay for confirming removing a pdb_file -->
							<div class="modal-dialog-overlay-container delete-pdb-file-overlay-container   delete_pdb_file_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
								 id="delete_pdb_file_overlay_container" >
			
								<div class="top-level-label" style="margin-left: 0px;">Delete PDB File</div>
				
								<div class="top-level-label-bottom-border" ></div>
								
								<div >
								
									<div >
										Are you sure you want to delete this PDB file from the database? 
										All protein mapping will be lost and it will no longer be available to other users.
									</div>
									
									<div style="margin-top: 10px">
										<input type="button" value="Yes" id="delete_pdb_file_confirm_button" >
										<input type="button" value="Cancel" class="delete_pdb_file_overlay_cancel_parts_jq" >
									</div>
										
								</div>
								
							</div>
						
						  </div>
						</div>
						
						
						<!-- END:   Modal dialog for confirming deleting an Alignment -->
						
														

			
						<!--  Modal dialog for confirming deleting an Alignment -->
				
							<!--  Div behind modal dialog div -->
				
						<div class="modal-dialog-overlay-background   delete_alignment_overlay_show_hide_parts_jq delete_alignment_overlay_cancel_parts_jq  overlay_show_hide_parts_jq" 
							id="delete_alignment_overlay_background" ></div>
						
								<!--  Inline div for positioning modal dialog on page -->
						<div class="delete-alignment-overlay-containing-outermost-div " id="delete_alignment_overlay_containing_outermost_div_inline_div"  >
			
						  <div class="delete-alignment-overlay-containing-outer-div " >
						
				
								<!--  Div overlay for confirming removing a pdb_file -->
							<div class="modal-dialog-overlay-container delete-alignment-overlay-container   delete_alignment_overlay_show_hide_parts_jq  overlay_show_hide_parts_jq" 
								 id="delete_alignment_overlay_container" >
			
								<div class="top-level-label" style="margin-left: 0px;">Remove Protein PDB chain Association</div>
				
								<div class="top-level-label-bottom-border" ></div>
								
								<div >
								
									<div >
										Are you sure you want to unassociate this protein from this PDB chain?
									</div>
									
									<div style="margin-top: 10px">
										<input type="button" value="Yes" id="delete_alignment_confirm_button" >
										<input type="button" value="Cancel" class="delete_alignment_overlay_cancel_parts_jq" >
									</div>
										
								</div>
								
							</div>
						
						  </div>
						</div>
						
						
						<!-- END:   Modal dialog for confirming deleting an Alignment -->
						
															
						
						
					</td>
				</tr>
			</table>


			<!--  The Spinner -->
			<div style="width:100%;height:100%;background-color:grey;opacity:0.5;" id="coverage-map-loading-spinner-block" >
				<div style="opacity:1.0;position:absolute;left:20%;top:20%" id="coverage-map-loading-spinner" style="" ></div>
			</div>












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
					  <tr>
					  	<td>Length:</td>
					  	<td><span id="looplink_length"></span> Å</td>
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
					  <tr>
					  	<td>Length:</td>
					  	<td><span id="crosslink_length"></span> Å</td>
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
	
			


			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	
			
			<%@ include file="/WEB-INF/jsp-includes/pdb-upload-overlay.jsp" %>
			<%@ include file="/WEB-INF/jsp-includes/pdb-map-protein-overlay.jsp" %>
						
	</div>  <%--  END  <div class="overall-enclosing-block"> --%>
	  
  <%--  Front End Build  --%>
  
  		<script type="text/javascript" src="static/js_generated_bundles/data_pages/structureView-bundle.js?x=${cacheBustValue}"></script> 
  		

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>