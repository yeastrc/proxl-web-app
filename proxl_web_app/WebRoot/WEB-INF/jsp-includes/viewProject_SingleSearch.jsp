
<%--  viewProject_SingleSearch.jsp
/WEB-INF/jsp-includes/viewProject_SingleSearch.jsp
   A single search on the viewProject Page
--%>
<%--   Incoming page variable 'search_wrapper' --%>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

   <c:set var="search" value="${ search_wrapper.searchDTO }" />
							
  <div id="single_search_entry__project_search_id_<bean:write name="search" property="projectSearchId" />"
  	data-project_search_id="<bean:write name="search" property="projectSearchId" />" class=" search_row_jq " >
	<table style="padding:0px;margin-top:0px;margin-bottom:0px;width:100%;">
		<tr data-project_search_id="<bean:write name="search" property="projectSearchId" />" class=" search_root_jq ">
		  <script type="text/text" class=" qc_plots_links_filtered_on_import_message_jq "
				><c:out value="${ search_wrapper.cutoffsAppliedOnImportAllAsString }"></c:out></script>
			<td style="width:10px;" valign="top" class="search-checkbox-cell">
				<input id="search-checkbox-<bean:write name="search" property="projectSearchId" />" 
					onChange="javascript:checkSearchCheckboxes(<bean:write name="search" property="projectSearchId" />)" 
					class="search-checkbox" type="checkbox" 
					name="projectSearchId" 
					value="<bean:write name="search" property="projectSearchId" />"/>
			</td>
			<td>
			  <div style="float: right;" >
				[<a data-tooltip="View peptides found in search" class="tool_tip_attached_jq" 
					href="${ contextPath }/<proxl:defaultPageUrl pageName="/peptide" projectSearchId="${ search.projectSearchId }">peptide.do?projectSearchId=<bean:write name="search" property="projectSearchId" /></proxl:defaultPageUrl>"
						>Peptides</a>]
				[<a data-tooltip="View proteins found in search" class="tool_tip_attached_jq" 
					href="${ contextPath }/<proxl:defaultPageUrl pageName="/crosslinkProtein" projectSearchId="${ search.projectSearchId }">crosslinkProtein.do?projectSearchId=<bean:write name="search" property="projectSearchId" /></proxl:defaultPageUrl>"
						>Proteins</a>]
				[<a data-tooltip="Graphical view of links between proteins" class="tool_tip_attached_jq" 
					href="${ contextPath }/<proxl:defaultPageUrl pageName="/image" projectSearchId="${ search.projectSearchId }">image.do?projectSearchId=<bean:write name="search" property="projectSearchId" /></proxl:defaultPageUrl>"
						>Image</a>]
				<c:choose>
				 <c:when test="${ showStructureLink }">
					[<a data-tooltip="View data on 3D structures" class="tool_tip_attached_jq" 
						href="${ contextPath }/<proxl:defaultPageUrl pageName="/structure" projectSearchId="${ search.projectSearchId }">structure.do?projectSearchId=<bean:write name="search" property="projectSearchId" /></proxl:defaultPageUrl>"
							>Structure</a>]
				 </c:when>
				 <c:otherwise>
					<%@ include file="/WEB-INF/jsp-includes/structure_link_non_link.jsp" %>
				 </c:otherwise>
				</c:choose>
				<c:if test="${authAccessLevel.searchDeleteAllowed}" >
					<a href="javascript:" data-tooltip="Delete search" class="tool_tip_attached_jq delete_search_link_jq"
					 		<%-- WAS  href="javascript:confirmDelete(<bean:write name="search" property="projectSearchId" />)"  --%>
						><img src="${ contextPath }/images/icon-delete-small.png" ></a>
				</c:if>
			  </div>
			  <div>
				<a class="tool_tip_attached_jq expand-link" data-tooltip="Show or hide more details" 
					id="search-details-link-<bean:write name="search" property="projectSearchId" />" 
					style="font-size:80%;color:#4900d4;text-decoration:none;" 
					href="javascript:showSearchDetails(<bean:write name="search" property="projectSearchId" />)"
					><img src="${ contextPath }/images/icon-expand-small.png" <%-- This image src is changed in the Javascript --%>
					></a>
				<span id="search-name-normal-<bean:write name="search" property="projectSearchId" />"
					><span class="search-name-display  search_name_display_jq" 
						id="search-name-display-<bean:write name="search" property="projectSearchId" />"
						><bean:write name="search" property="name" /></span
					 > <span class="search-name-display search_number_in_parens_display_jq "
					 			>(<bean:write name="search" property="searchId" />)</span
					 			><c:if test="${authAccessLevel.writeAllowed}" 
					 				><a class="tool_tip_attached_jq" data-tooltip="Edit name of search" 
					 					href="javascript:showSearchNameForm(<bean:write name="search" property="projectSearchId" />)"
										><img class="edit-icon" src="${ contextPath }/images/icon-edit-small.png" 
											></a></c:if></span>
				<span style="display:none;" id="search-name-edit-<bean:write name="search" property="projectSearchId" />"
					><input id="search-name-value-<bean:write name="search" property="projectSearchId" />" 
						type="text" style="width:200px;" value="<bean:write name="search" property="name" />"
						><input class="submit-button" type="button" value="Save" 
							onClick="saveName(<bean:write name="search" property="projectSearchId" />)"
							><input class="submit-button" type="button" value="Cancel" 
								onClick="cancelNameEdit(<bean:write name="search" property="projectSearchId" />)"></span>
			  </div>
			  <div style="clear: right;"  class="search-details-container-div">
				<table class="search-details" id="search-details-<bean:write name="search" property="projectSearchId" />" style="display:none;margin-left:15px;">
				  <c:if test="${ authAccessLevel.writeAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }" >
				   <c:if test="${ not empty search.path }" >
					<tr>
						<td>Path:</td>
						<td><bean:write name="search" property="path" /></td>
					</tr>
				   </c:if>
				  </c:if>
					<tr>
						<td>Linker:</td>
						<td><c:out value="${ search_wrapper.linkersDisplayString }"></c:out></td>
					</tr>								  
					<tr>
						<td valign="top"  >
							Search Program<c:if test="${ fn:length( search_wrapper.searchPrograms ) > 1 }" >s</c:if>:
						</td>
						 <c:choose>
						  <c:when test="${ empty search_wrapper.searchPrograms }">
							<td  style="padding-top: 2px;">
							  	Not Found
							</td>
						  </c:when>
						  <c:otherwise>
						    <td style="border-width:0px; padding: 0px;">
							<table  style="border-width:0px; border-spacing: 0px; ">
						   <c:forEach var="searchProgram" items="${ search_wrapper.searchPrograms }">
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
						<td>Upload:</td>
						<td><bean:write name="search" property="formattedLoadTime" /></td>
					</tr>
					<tr>
						<td style="white-space: nowrap;">FASTA file:</td>
						<td><bean:write name="search" property="fastaFilename" /></td>
					</tr>
					<%--  Copy  'search_wrapper' to 'search_details' to use here --%>
					<c:set var="search_details" value="${ search_wrapper }"/>
					<c:if test="${ not empty search_details.cutoffsAppliedOnImportList }">
					  <tr>
							<td style="vertical-align: top; padding-top: 3px; padding-right: 5px; " >Cutoffs Applied On Import:</td>
						    <td style="border-width:0px; padding: 0px; padding-top: 3px;">
							<table  style="border-width:0px; border-spacing: 0px; ">
							   <c:forEach var="cutoffsAppliedOnImport" items="${ search_details.cutoffsAppliedOnImportList }">
							     <tr>
							      <td style="padding-right: 5px; ">
							       <c:choose>
							        <c:when test="${ cutoffsAppliedOnImport.peptideCutoff }">
							        	<span style="white-space: nowrap;" >Peptide Cutoff:</span>
							        </c:when>
							        <c:otherwise>
							        	<span style="white-space: nowrap;" >PSM Cutoff:</span>
							        </c:otherwise>
							       </c:choose>
							      </td>
							      <td style="padding-right: 5px;">
							     	<c:out value="${ cutoffsAppliedOnImport.annotationName }"></c:out>
							      </td>
							      <td >
							     	<c:out value="${ cutoffsAppliedOnImport.cutoffValue }"></c:out>
							      </td>
							     </tr>
							   </c:forEach>
						    </table>
							</td>						
					  </tr>
					</c:if>
					<tr >
						<td>QC Plots:</td>
						<td>
							<c:if test="${ search.hasScanData }" >
								[<a href="javascript:" data-tooltip="View scan counts as function of retention time" 
									class="tool_tip_attached_jq qc_plot_scan_retention_time_link_jq" 
									>Retention Time</a>]
							</c:if>
							[<a href="javascript:" data-tooltip="View PSM counts as function of score" 
								class="tool_tip_attached_jq qc_plot_psm_count_vs_score_link_jq" 
								>PSM Count vs/ Score</a>]
							[<a href="javascript:" data-tooltip="View 2 PSM scores plotted together" 
								class="tool_tip_attached_jq qc_plot_psm_score_vs_score_link_jq" 
								>Score vs/ Score</a>]
<%--								
--%>								
							[<a data-tooltip="View QC data" class="tool_tip_attached_jq" 
								href="${ contextPath }/qc.do?projectSearchId=<bean:write name="search" property="projectSearchId" />"
									>QC data</a>]

						</td>
					</tr>
					<c:if test="${ not ( not authAccessLevel.writeAllowed and empty search.webLinks ) }" >
							<%--  Hide this block if no Web Links and user unable to add Web Links --%>
					<tr>
						<td valign="top">Raw MS data files:</td>
						<td id="search-web-links-<bean:write name="search" property="projectSearchId" />">
							<div style="position: relative;">			 
						  		<div class="error-message-container error_message_container_jq" 
						  				id="error_message_web_link_url_invalid_<bean:write name="search" property="projectSearchId" />"
						  				style="width: 600px;">
						  			<div class="error-message-inner-container" >
						  				<div class="error-message-close-x error_message_close_x_jq">X</div>
							  			<div class="error-message-text" >Web Link URL is invalid</div>
						  			</div>
							  	</div>	
							  </div>
							<logic:iterate name="search" property="webLinks" id="webLink" >
								<%--  Keep this block in sync with the Template just below --%>
								<div id="web-links-<bean:write name="webLink" property="id" />"
									class="search_web_link_root_jq"
									searchwebLinkId="<bean:write name="webLink" property="id" />" 
									style="margin-bottom:5px; margin-top:0px;">
									<c:if test="${authAccessLevel.writeAllowed}" >
										<a id="web-links-delete-<bean:write name="webLink" property="id" />" 
											style="color:#d40000;font-size:80%;" 
											class="tool_tip_attached_jq delete_search_webLink_link_jq"
											data-tooltip="Delete link to RAW file"
											href="javascript:"
												><img src="${ contextPath }/images/icon-delete-small.png"></a>
									</c:if>
									<a  target="_blank" href="<bean:write name="webLink" property="linkUrl" />" 
										><bean:write name="webLink" property="linkLabel" /></a>
								</div>
							</logic:iterate>
							<c:if test="${authAccessLevel.writeAllowed}" >
							 <div >
								<div id="add-web-links-link-span-<bean:write name="search" property="projectSearchId" />"
									>[<a id="add-web-link-link-<bean:write name="search" property="projectSearchId" />" 
										style="font-size:80%;text-decoration:none;" 
										href="javascript:showAddWebLink(<bean:write name="search" property="projectSearchId" />)"
										class="tool_tip_attached_jq" data-tooltip="Add URL for a RAW file">+Link to Raw file</a>]</div>
								<div style="display:none;" id="add-web-links-form-span-<bean:write name="search" property="projectSearchId" />" >
								 <div>
								  URL:
								  <input id="web-links-url-input-<bean:write name="search" property="projectSearchId" />" 
										type="text" style="font-size:80%;width:200px;">
								  Label:
								  <input id="web-links-label-input-<bean:write name="search" property="projectSearchId" />" 
										type="text" style="font-size:80%;width:200px;">
								  <input style="font-size:80%;" class="submit-button" type="button" value="Add Web Link" 
											onClick="addWebLink(<bean:write name="search" property="projectSearchId" />)">
								  <input style="font-size:80%;" class="submit-button" type="button" value="Cancel" 
										 	onClick="cancelWebLink(<bean:write name="search" property="projectSearchId" />)" >
								  </div>
								  <div style="font-size: 80%;">
								  	The URL must start with "http://", "https://", "ftp://" or some other transport protocal
								  </div>
								</div>
							 </div>
							</c:if>
							<%--  Template for links added by Javascript --%>
							<div id="web_link_template" style="display: none;">
								<div id="web-links-{{id}}"
									class="search_web_link_root_jq"
									searchwebLinkId="{{id}}" 
									style="margin-bottom:5px; margin-top:0px;">
									<c:if test="${authAccessLevel.writeAllowed}" >
										<a  id="web-links-delete-{{id}}" 
											style="color:#d40000;font-size:80%;" 
											class="tool_tip_attached_jq delete_search_webLink_link_jq"
											data-tooltip="Delete link to RAW file"
											href="javascript:"
												><img src="${ contextPath }/images/icon-delete-small.png"></a>
									</c:if>
									<a  target="_blank" href="{{linkUrl}}" >{{linkLabel}}</a>
								</div>
							</div>
						</td>
					</tr>
					</c:if>
					<c:if test="${ not empty search.files }">
					  <tr>
						<td valign="top">Additional files:</td>
						<td >
						  <logic:iterate name="search" property="files" id="searchFile" >
							<div class=" display_search_filename_outer_container_jq " search_file_id="${ searchFile.id }" search_id="${ search.projectSearchId }"> 
							 <div class="display_search_filename_container_jq">
							  <%--  Normal display of link with filename --%>
							  <a href="downloadSearchFile.do?fileId=<bean:write name="searchFile" property="id" />" 
							  	class="tool_tip_attached_jq search_file_link_for_tooltip_jq" data-tooltip="Download file">
							  	<span class="search_filename_jq">
									<bean:write name="searchFile" property="displayFilename" />
								</span>
							  </a>
							  <c:if test="${authAccessLevel.projectOwnerAllowed}"> 
									<a class="tool_tip_attached_jq" data-tooltip="Edit name" href="javascript:" onclick="showSearchFilenameForm( this )"
										><img class="edit-icon" src="${ contextPath }/images/icon-edit-small.png" 
											></a>
							  </c:if>
							 </div>
						  	<c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" >
							 <div class=" edit_search_filename_container_jq " style="display: none;">
							  <%--  Edit filename --%>
								<input type="text" style="width:200px;" value="<bean:write name="searchFile" property="displayFilename" />"
									class=" edit_search_filename_input_field_jq "
									><input class="submit-button" type="button" value="Save" onClick="saveSearchFilename( this )"
									><input class="submit-button" type="button" value="Cancel" onClick="cancelSearchFilenameEdit( this )">													 
							 </div>
							</c:if>  <%--  END <c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" > --%>
							</div>
						  </logic:iterate>
						</td>
					  </tr>
					</c:if>
					<c:if test="${ not ( not authAccessLevel.writeAllowed and empty search.comments ) }" >
							<%--  Hide this block if no comments and user unable to add comments --%>
					  <tr>
						<td valign="top">Comments:</td>
						<td id="search-comments-<bean:write name="search" property="projectSearchId" />">
							<logic:iterate name="search" property="comments" id="comment" >
								<%--  Keep this block in sync with the Template just below --%>
								<div id="comment-<bean:write name="comment" property="id" />"
									class="search_comment_root_jq"
									searchCommentId="<bean:write name="comment" property="id" />" 
									style="margin-bottom:5px; margin-top:0px;">
								  <div class=" search_comment_display_jq ">
									<c:if test="${authAccessLevel.writeAllowed}" >
										<a class="tool_tip_attached_jq" data-tooltip="Delete comment" style="color:#d40000;font-size:80%;" 
											onclick="deleteSearchCommentClickHandler(this);return false;" 
											href="javascript:"
												><img src="${ contextPath }/images/icon-delete-small.png"></a>
									</c:if>
									<span class=" search_comment_string_jq "
										><bean:write name="comment" property="comment" 
									/></span>
									<c:if test="${authAccessLevel.writeAllowed}" >
									  <a class="tool_tip_attached_jq" data-tooltip="Edit comment"  onclick="showSearchCommentEditForm( this ); return false;" href="javascript:" >
										<img class="edit-icon" src="${ contextPath }/images/icon-edit-small.png">
									  </a> 
									</c:if>
									(<span class=" search_comment_date_jq "
										><bean:write name="comment" property="dateTimeString" /></span>)
								  </div>
								  <c:if test="${authAccessLevel.writeAllowed}" >
								  	<%--  For editing the comment value --%>
								  	<div class=" search_comment_edit_jq " style="display: none;">
								  		<input type="text" class="search_comment_input_field_jq" style="width:200px;" >
								  		<input class="submit-button" type="button" value="Save" 
								  			onclick="updateSearchComment( this ); return false;" >
								  		<input class="submit-button" type="button" value="Cancel" 
								  			onclick="cancelSearchCommentEditForm( this ); return false;" >
								  	</div>
								  </c:if>
								</div>
							</logic:iterate>
							<c:if test="${authAccessLevel.writeAllowed}" >
								<%--  Template for search comments added by Javascript --%>
								<div id="search_comment_template" style="display: none;">
									<div id="comment-{{id}}"
										class="search_comment_root_jq"
										searchCommentId="{{id}}" 
										style="margin-bottom:5px; margin-top:0px;">
								  	  <div class=" search_comment_display_jq ">
										<c:if test="${authAccessLevel.writeAllowed}" >
											<a class="tool_tip_attached_jq" data-tooltip="Delete comment" style="color:#d40000;font-size:80%;" 
												onclick="deleteSearchCommentClickHandler(this);return false;" 
												href="javascript:"
													><img src="${ contextPath }/images/icon-delete-small.png"></a>
										</c:if>
										<span class=" search_comment_string_jq "
											>{{comment}}</span>
										<c:if test="${authAccessLevel.writeAllowed}" >
										  <a class="tool_tip_attached_jq" data-tooltip="Edit comment" onclick="showSearchCommentEditForm( this ); return false;" href="javascript:" >
											<img class="edit-icon" src="${ contextPath }/images/icon-edit-small.png">
										  </a> 
										</c:if>
										(<span class=" search_comment_date_jq "
											>{{dateTimeString}}</span>)
								  	  </div>
									  <c:if test="${authAccessLevel.writeAllowed}" >
									  	<%--  For editing the comment value --%>
									  	<div class=" search_comment_edit_jq " style="display: none;">
									  		<input type="text" class="search_comment_input_field_jq" style="width:200px;" >
									  		<input class="submit-button" type="button" value="Save" 
									  			onclick="updateSearchComment( this ); return false;" >
									  		<input class="submit-button" type="button" value="Cancel" 
									  			onclick="cancelSearchCommentEditForm( this ); return false;" >
									  	</div>
									  </c:if>
									</div>
								</div>													
								<span id="add-comment-link-span-<bean:write name="search" property="projectSearchId" />"
									>[<a class="tool_tip_attached_jq" data-tooltip="Add a comment" id="add-comment-link-<bean:write name="search" property="projectSearchId" />" style="font-size:80%;text-decoration:none;" href="javascript:showAddComment(<bean:write name="search" property="projectSearchId" />)"
										>+Comment</a>]</span>
								<span style="display:none;" id="add-comment-form-span-<bean:write name="search" property="projectSearchId" />"
									><input id="comment-input-<bean:write name="search" property="projectSearchId" />" type="text" style="font-size:80%;width:200px;"
									><input style="font-size:80%;" class="submit-button" type="button" value="Add Comment" onClick="addComment(<bean:write name="search" property="projectSearchId" />)"
									><input style="font-size:80%;" class="submit-button" type="button" value="Cancel" onClick="cancelComment(<bean:write name="search" property="projectSearchId" />)"
									></span>
							</c:if>
						</td>
					  </tr>
					</c:if>
				</table>
			  </div>
			 </td>
		</tr>
	</table>
	<c:choose>
	  <c:when test="${ SingleSearch_SkipBottomSeperator }">
	  </c:when>
	  <c:otherwise>
		<div class="search-entry-bottom-border"></div>
	  </c:otherwise>
	</c:choose>
  </div>
  