<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">View Structural Analysis</c:set>

 <c:set var="pageBodyClass" >project-page view-merged-structure-page</c:set>

 <c:set var="headerAdditions">
 
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

		<script type="text/javascript" src="${ contextPath }/js/libs/handlebars-v2.0.0.min.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/handleServicesAJAXErrors.js"></script> 
		 
		<script type="text/javascript" src="${ contextPath }/js/spinner.js"></script> 
		 
		<script type="text/javascript" src="${ contextPath }/js/structure-viewer-color-handler.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/structure-viewer-page.js"></script> 
		
		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/defaultPageView.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/viewPsmsLoadedFromWebServiceTemplate.js"></script>
				
		<script type="text/javascript" src="${ contextPath }/js/structure-viewer-pdb-upload.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/structure-viewer-map-protein.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/structure-viewer-click-element-handlers.js"></script>
		<script type="text/javascript" src="${ contextPath }/js/download-string-as-file.js"></script>
		
		
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />
		
		<link rel="stylesheet" href="${ contextPath }/css/tablesorter.css" type="text/css" media="print, projection, screen" />
		

		<link REL="stylesheet" TYPE="text/css" HREF="${contextPath}/css/lorikeet.css">
		


	<%-- includ javascript library for glmol structure viewer --%>
	<script type="text/javascript" src="${ contextPath }/js/libs/bio-pv.min.js"></script>


</c:set>















<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>


	
	<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
		
	<%@ include file="/WEB-INF/jsp-includes/viewPsmsLoadedFromWebServiceTemplateFragment.jsp" %>
		
	<div class="overall-enclosing-block">
	
	<input type="hidden" id="project_id" value="<c:out value="${ project_id }"></c:out>"> 
	
		<div id="invalid_url_no_data_after_hash_div" style="display: none;" >
		
			<h1 style="color: red;">Invalid URL, data missing</h1>

		</div>
	

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
		<input class="tool_tip_attached_jq" data-tooltip="Graphical view of links between proteins" type="hidden" id="viewviewMergedImageDefaultPageUrl" 
			value="<proxl:defaultPageUrl pageName="viewMergedImage.do" searchId="${ onlySingleSearchId }"></proxl:defaultPageUrl>">
	</c:if>
					
		
		<div>
	
			<h2 style="margin-bottom:5px;">View <span id="merged_label_text_header" style="display: none;" >merged </span>search results:</h2>
	
			<div id="navigation-links"></div>
	
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
					<td>PSM <span style="white-space: nowrap">Q-value</span> cutoff:</td>
					<td><input type="text" style="width:40px;" id="psmQValueCutoff"></td>
				</tr>
				
				<tr>
					<td>Peptide <span style="white-space: nowrap">Q-value</span> cutoff:</td>
					<td><input type="text" style="width:40px;" id="peptideQValueCutoff"></td>
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
					<label><span class="tool_tip_attached_jq" data-tooltip="Recommended - checking this causes only the shortest instance of a UDR to be shown. Unchecking attempts to show all discrete instances of all UDRs. See the help pages (? at top left of page) for more details." style="white-space:nowrap;" ><input type="checkbox" id="show-unique-udrs" checked>Show UDRs once</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Shade links drawn on structure by spectrum count" style="white-space:nowrap;" ><input type="checkbox" id="shade-by-counts">Shade by counts</span></label>
					<label><span class="tool_tip_attached_jq" data-tooltip="Color structure of backbone to indicate sequence coverage" style="white-space:nowrap;" ><input type="checkbox" id="show-coverage">Show sequence coverage</span></label>

					<span class="tool_tip_attached_jq" data-tooltip="Select method used to color links on structure" style="white-space:nowrap;" >Color links by:
						<select id="select-link-color-mode">
							<option value="length">Length</option>
							<option value="type">Type</option>
							<option value="search">Search/Run</option>
						</select>
					</span>

					<span class="tool_tip_attached_jq" data-tooltip="Selecting a rendering style for the structure" style="white-space:nowrap;" >Render mode:
						<select id="select-render-mode">
							<option value="cartoon">Cartoon</option>
							<option value="sline">Smooth Line</option>
							<option value="trace">Trace</option>
							<option value="lines">Lines (slow)</option>
							<option value="points">Points (slow)</option>
							<!-- <option value="spheres">Spheres (unstable?)</option> -->
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
						<div id="legend-div" style="margin-top:5px;vertical-align:top;"></div>
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

		
				<table id="link_info_table" class="link-info-table tablesorter top_data_table_jq " >
					<thead>
					<tr>
						<th style="text-align:left;width:60%;font-weight:bold;">Search Name</th>
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
			
				<table class="  tablesorter peptide_table_jq" style="margin-bottom: 10px; margin-top: 5px; width: 95%; margin-left: auto; margin-right: auto; text-align: left;" >
				
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
			peptide_q_value_cutoff="{{ peptideQValueCutoff }}"			
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
			
<%-- 			
			<td>{{peptide.id}}</td>
--%>			

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
			peptide_q_value_cutoff="{{ peptideQValueCutoff }}"			
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
		
			<td class="psm_data_container child_data_container_jq " colspan="7" style="text-align: center;" >
				

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
			peptide_q_value_cutoff="{{ peptideQValueCutoff }}"			
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
		
			<td class="psm_data_container child_data_container_jq " colspan="5" style="text-align: center;" >
				

			</td>
		</tr>
	</table>	
	
					
	
			</div>  <%--  END  <div id="view-link-info-overlay-body" class="view-link-info-overlay-body" > --%>


		</div>  <%--  END  <div id="view_link_info_overlay_div" class=" view-link-info-overlay-div " style="display: none; "  > --%>
	
			


			<%@ include file="/WEB-INF/jsp-includes/lorikeet_overlay_section.jsp" %>	
			
			<%@ include file="/WEB-INF/jsp-includes/nagWhenFormChangedButNotUpdated_Overlay.jsp" %>
			
			<%@ include file="/WEB-INF/jsp-includes/pdb-upload-overlay.jsp" %>
			<%@ include file="/WEB-INF/jsp-includes/pdb-map-protein-overlay.jsp" %>
						
	</div>  <%--  END  <div class="overall-enclosing-block"> --%>
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>