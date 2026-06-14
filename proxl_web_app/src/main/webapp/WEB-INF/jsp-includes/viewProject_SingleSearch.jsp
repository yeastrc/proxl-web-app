
<%--  viewProject_SingleSearch.jsp
/WEB-INF/jsp-includes/viewProject_SingleSearch.jsp
   A single search on the viewProject Page
--%>
<%--   Incoming page variable 'search_wrapper' --%>

<%@page import="org.yeastrc.xlink.www.constants.PageLinkTextAndTooltipConstants"%>
<%@ include file="/WEB-INF/jsp-includes/proxlTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

   <c:set var="search" value="${ search_wrapper.searchDTO }" />
							
  <div id="single_search_entry__project_search_id_<c:out value="${search.projectSearchId}"/>"
  	data-project_search_id="<c:out value="${search.projectSearchId}"/>" class=" search_row_jq " >
	<table style="padding:0px;margin-top:0px;margin-bottom:0px;width:100%;">
		<tr data-project_search_id="<c:out value="${search.projectSearchId}"/>" class=" search_root_jq ">
			<td style="width:10px;" valign="top" class="search-checkbox-cell">
				<input id="search-checkbox-<c:out value="${search.projectSearchId}"/>" 
					onChange="javascript:checkSearchCheckboxes(<c:out value="${search.projectSearchId}"/>)" 
					class="search-checkbox" type="checkbox" 
					name="projectSearchId" 
					value="<c:out value="${search.projectSearchId}"/>"/>
			</td>
			<td>
			  <div style="float: right;" >
				[<a data-tooltip="<%= PageLinkTextAndTooltipConstants.QC_LINK_TOOLTIP %>" class="tool_tip_attached_jq" 
					href="qc.do?projectSearchId=<c:out value="${search.projectSearchId}"/>"
						><%= PageLinkTextAndTooltipConstants.QC_LINK_TEXT %></a>]
				[<a data-tooltip="View peptides found in search" class="tool_tip_attached_jq" 
					href="<proxl:defaultPageUrl pageName="/peptide" projectSearchId="${ search.projectSearchId }">peptide.do?projectSearchId=<c:out value="${search.projectSearchId}"/></proxl:defaultPageUrl>"
						>Peptides</a>]
				[<a data-tooltip="View proteins found in search" class="tool_tip_attached_jq" 
					href="<proxl:defaultPageUrl pageName="/crosslinkProtein" projectSearchId="${ search.projectSearchId }">crosslinkProtein.do?projectSearchId=<c:out value="${search.projectSearchId}"/></proxl:defaultPageUrl>"
						>Proteins</a>]
				[<a data-tooltip="Graphical view of links between proteins" class="tool_tip_attached_jq" 
					href="<proxl:defaultPageUrl pageName="/image" projectSearchId="${ search.projectSearchId }">image.do?projectSearchId=<c:out value="${search.projectSearchId}"/></proxl:defaultPageUrl>"
						>Image</a>]
				<c:choose>
				 <c:when test="${ showStructureLink }">
					[<a data-tooltip="View data on 3D structures" class="tool_tip_attached_jq" 
						href="<proxl:defaultPageUrl pageName="/structure" projectSearchId="${ search.projectSearchId }">structure.do?projectSearchId=<c:out value="${search.projectSearchId}"/></proxl:defaultPageUrl>"
							>Structure</a>]
				 </c:when>
				 <c:otherwise>
					<%@ include file="/WEB-INF/jsp-includes/structure_link_non_link.jsp" %>
				 </c:otherwise>
				</c:choose>
				<c:if test="${authAccessLevel.searchDeleteAllowed}" >
					<a href="javascript:" data-tooltip="Delete search" class="tool_tip_attached_jq delete_search_link_jq"
					 		<%-- WAS  href="javascript:confirmDelete(<c:out value="${search.projectSearchId}"/>)"  --%>
						><img src="images/icon-delete-small.png" ></a>
				</c:if>
			  </div>
			  <div>
				<a class="tool_tip_attached_jq expand-link" data-tooltip="Show or hide more details" 
					id="search-details-link-<c:out value="${search.projectSearchId}"/>" 
					style="font-size:80%;color:#4900d4;text-decoration:none;" 
					href="javascript:showSearchDetails(<c:out value="${search.projectSearchId}"/>)"
					><img src="images/icon-expand-small.png" <%-- This image src is changed in the Javascript --%>
					></a>
				<span id="search-name-normal-<c:out value="${search.projectSearchId}"/>"
					><span class="search-name-display  search_name_display_jq" 
						id="search-name-display-<c:out value="${search.projectSearchId}"/>"
						><c:out value="${search.name}"/></span
					 > <span class="search-name-display search_number_in_parens_display_jq "
					 			>(<c:out value="${search.searchId}"/>)</span
					 			><c:if test="${authAccessLevel.writeAllowed}" 
					 				><a class="tool_tip_attached_jq" data-tooltip="Edit name of search" 
					 					href="javascript:showSearchNameForm(<c:out value="${search.projectSearchId}"/>)"
										><img class="edit-icon" src="images/icon-edit-small.png" 
											></a></c:if></span>
				<span style="display:none;" id="search-name-edit-<c:out value="${search.projectSearchId}"/>"
					><input id="search-name-value-<c:out value="${search.projectSearchId}"/>" 
						type="text" style="width:200px;" value="<c:out value="${search.name}"/>"
						><input class="submit-button" type="button" value="Save" 
							onClick="saveName(<c:out value="${search.projectSearchId}"/>)"
							><input class="submit-button" type="button" value="Cancel" 
								onClick="cancelNameEdit(<c:out value="${search.projectSearchId}"/>)"></span>
			  </div>
			  <div style="clear: right;"  class="search-details-container-div">
				<table class="search-details" id="search-details-<c:out value="${search.projectSearchId}"/>" style="display:none;margin-left:15px;">
				  <c:if test="${ authAccessLevel.writeAllowed or authAccessLevel.assistantProjectOwnerIfProjectNotLockedAllowed }" >
				   <c:if test="${ not empty search.path }" >
					<tr>
						<td>Path:</td>
						<td><c:out value="${search.path}"/></td>
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
						<td><c:out value="${search.formattedLoadTime}"/></td>
					</tr>
					<tr>
						<td style="white-space: nowrap;">FASTA file:</td>
						<td><c:out value="${search.fastaFilename}"/></td>
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

					<c:if test="${ not ( not authAccessLevel.writeAllowed and empty search.webLinks ) }" >
							<%--  Hide this block if no Web Links and user unable to add Web Links --%>
					<tr>
						<td valign="top">Raw MS data files:</td>
						<td id="search-web-links-<c:out value="${search.projectSearchId}"/>">
							<div style="position: relative;">			 
						  		<div class="error-message-container error_message_container_jq" 
						  				id="error_message_web_link_url_invalid_<c:out value="${search.projectSearchId}"/>"
						  				style="width: 600px;">
						  			<div class="error-message-inner-container" >
						  				<div class="error-message-close-x error_message_close_x_jq">X</div>
							  			<div class="error-message-text" >Web Link URL is invalid</div>
						  			</div>
							  	</div>	
							  </div>
							<c:forEach var="webLink" items="${search.webLinks}">
								<%--  Keep this block in sync with the Template just below --%>
								<div id="web-links-<c:out value="${webLink.id}"/>"
									class="search_web_link_root_jq"
									searchwebLinkId="<c:out value="${webLink.id}"/>" 
									style="margin-bottom:5px; margin-top:0px;">
									<c:if test="${authAccessLevel.writeAllowed}" >
										<a id="web-links-delete-<c:out value="${webLink.id}"/>" 
											style="color:#d40000;font-size:80%;" 
											class="tool_tip_attached_jq delete_search_webLink_link_jq"
											data-tooltip="Delete link to RAW file"
											href="javascript:"
												><img src="images/icon-delete-small.png"></a>
									</c:if>
									<a  target="_blank" href="<c:out value="${webLink.linkUrl}"/>" 
										><c:out value="${webLink.linkLabel}"/></a>
								</div>
							</c:forEach>
							<c:if test="${authAccessLevel.writeAllowed}" >
							 <div >
								<div id="add-web-links-link-span-<c:out value="${search.projectSearchId}"/>"
									>[<a id="add-web-link-link-<c:out value="${search.projectSearchId}"/>" 
										style="font-size:80%;text-decoration:none;" 
										href="javascript:showAddWebLink(<c:out value="${search.projectSearchId}"/>)"
										class="tool_tip_attached_jq" data-tooltip="Add URL for a RAW file">+Link to Raw file</a>]</div>
								<div style="display:none;" id="add-web-links-form-span-<c:out value="${search.projectSearchId}"/>" >
								 <div>
								  URL:
								  <input id="web-links-url-input-<c:out value="${search.projectSearchId}"/>" 
										type="text" style="font-size:80%;width:200px;">
								  Label:
								  <input id="web-links-label-input-<c:out value="${search.projectSearchId}"/>" 
										type="text" style="font-size:80%;width:200px;">
								  <input style="font-size:80%;" class="submit-button" type="button" value="Add Web Link" 
											onClick="addWebLink(<c:out value="${search.projectSearchId}"/>)">
								  <input style="font-size:80%;" class="submit-button" type="button" value="Cancel" 
										 	onClick="cancelWebLink(<c:out value="${search.projectSearchId}"/>)" >
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
												><img src="images/icon-delete-small.png"></a>
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
						  <c:forEach var="searchFile" items="${search.files}">
							<div class=" display_search_filename_outer_container_jq " search_file_id="${ searchFile.id }" search_id="${ search.projectSearchId }"> 
							 <div class="display_search_filename_container_jq">
							  <%--  Normal display of link with filename --%>
							  <a href="downloadSearchFile.do?fileId=<c:out value="${searchFile.id}"/>" 
							  	class="tool_tip_attached_jq search_file_link_for_tooltip_jq" data-tooltip="Download file">
							  	<span class="search_filename_jq">
									<c:out value="${searchFile.displayFilename}"/>
								</span>
							  </a>
							  <c:if test="${authAccessLevel.projectOwnerAllowed}"> 
									<a class="tool_tip_attached_jq" data-tooltip="Edit name" href="javascript:" onclick="showSearchFilenameForm( this )"
										><img class="edit-icon" src="images/icon-edit-small.png" 
											></a>
							  </c:if>
							 </div>
						  	<c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" >
							 <div class=" edit_search_filename_container_jq " style="display: none;">
							  <%--  Edit filename --%>
								<input type="text" style="width:200px;" value="<c:out value="${searchFile.displayFilename}"/>"
									class=" edit_search_filename_input_field_jq "
									><input class="submit-button" type="button" value="Save" onClick="saveSearchFilename( this )"
									><input class="submit-button" type="button" value="Cancel" onClick="cancelSearchFilenameEdit( this )">													 
							 </div>
							</c:if>  <%--  END <c:if test="${authAccessLevel.assistantProjectOwnerAllowed}" > --%>
							</div>
						  </c:forEach>
						</td>
					  </tr>
					</c:if>
					<c:if test="${ not ( not authAccessLevel.writeAllowed and empty search.comments ) }" >
							<%--  Hide this block if no comments and user unable to add comments --%>
					  <tr>
						<td valign="top">Comments:</td>
						<td id="search-comments-<c:out value="${search.projectSearchId}"/>">
							<c:forEach var="comment" items="${search.comments}">
								<%--  Keep this block in sync with the Template just below --%>
								<div id="comment-<c:out value="${comment.id}"/>"
									class="search_comment_root_jq"
									searchCommentId="<c:out value="${comment.id}"/>" 
									style="margin-bottom:5px; margin-top:0px;">
								  <div class=" search_comment_display_jq ">
									<c:if test="${authAccessLevel.writeAllowed}" >
										<a class="tool_tip_attached_jq" data-tooltip="Delete comment" style="color:#d40000;font-size:80%;" 
											onclick="deleteSearchCommentClickHandler(this);return false;" 
											href="javascript:"
												><img src="images/icon-delete-small.png"></a>
									</c:if>
									<span class=" search_comment_string_jq "
										><c:out value="${comment.comment}"/></span>
									<c:if test="${authAccessLevel.writeAllowed}" >
									  <a class="tool_tip_attached_jq" data-tooltip="Edit comment"  onclick="showSearchCommentEditForm( this ); return false;" href="javascript:" >
										<img class="edit-icon" src="images/icon-edit-small.png">
									  </a> 
									</c:if>
									(<span class=" search_comment_date_jq "
										><c:out value="${comment.dateTimeString}"/></span>)
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
							</c:forEach>
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
													><img src="images/icon-delete-small.png"></a>
										</c:if>
										<span class=" search_comment_string_jq "
											>{{comment}}</span>
										<c:if test="${authAccessLevel.writeAllowed}" >
										  <a class="tool_tip_attached_jq" data-tooltip="Edit comment" onclick="showSearchCommentEditForm( this ); return false;" href="javascript:" >
											<img class="edit-icon" src="images/icon-edit-small.png">
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
								<span id="add-comment-link-span-<c:out value="${search.projectSearchId}"/>"
									>[<a class="tool_tip_attached_jq" data-tooltip="Add a comment" id="add-comment-link-<c:out value="${search.projectSearchId}"/>" style="font-size:80%;text-decoration:none;" href="javascript:showAddComment(<c:out value="${search.projectSearchId}"/>)"
										>+Comment</a>]</span>
								<span style="display:none;" id="add-comment-form-span-<c:out value="${search.projectSearchId}"/>"
									><input id="comment-input-<c:out value="${search.projectSearchId}"/>" type="text" style="font-size:80%;width:200px;"
									><input style="font-size:80%;" class="submit-button" type="button" value="Add Comment" onClick="addComment(<c:out value="${search.projectSearchId}"/>)"
									><input style="font-size:80%;" class="submit-button" type="button" value="Cancel" onClick="cancelComment(<c:out value="${search.projectSearchId}"/>)"
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
  