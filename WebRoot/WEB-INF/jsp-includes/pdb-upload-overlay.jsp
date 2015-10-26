<%@page import="org.yeastrc.xlink.www.constants.PDBFileConstants"%>



<%-- pdb-upload-overlay.jsp --%>


			<%--
			
				Show this overlay when the user clicks the "Upload PDB File" button on the structure view page.
			
			--%>
			
			<%-- Changed background class from overlay-background to  lorikeet-modal-dialog-overlay-background --%>
			
			<div id="pdb-upload-modal-dialog-overlay-background" class="pdb-upload-modal-dialog-overlay-background" style="display: none;"  >
			
			</div>
			
			<%--  The div showing all the PDB upload dialog --%>
			
			<div id="pdb-upload-overlay-div" class=" pdb-upload-overlay-div overlay-outer-div" style="display: none; "  >
			
			
				<div id="pdb-upload-overlay-header" class="pdb-upload-overlay-header" style="width:100%; " >
					<h1 id="pdb-upload-overlay-X-for-exit-overlay" class="pdb-upload-overlay-X-for-exit-overlay" >X</h1>
					<h1 id="pdb-upload-overlay-header-text" class="pdb-upload-overlay-header-text" >Upload new PDB File</h1>
				</div>
				<div id="pdb-upload-overlay-body" class="pdb-upload-overlay-body" style="text-align:left;" >
			
					<form enctype="multipart/form-data">
							
							<table style="border-width:0px;">
							
								<tr>
									<td style="text-align:left;">Select PDB File:</td>
									<td style="text-align:left;"><input id="pdb-file-field" type="file" /></td>
								</tr>
								
<%-- 							
								<tr>
									<td style="text-align:left;">Visibility:</td>
									<td style="text-align:left;">
										<select id="pdb-file-visibility">
											<option value="<%= PDBFileConstants.VISIBILITY_PUBLIC %>">All users of Proxl DB</option>
											<option value="<%= PDBFileConstants.VISIBILITY_PROJECT %>">Only members of this project</option>
										</select>
									</td>
								</tr>							
--%>	

								<tr>
									<td style="text-align:left;">Brief description:</td>
									<td style="text-align:left;"><input id="pdb-file-description" type="text" maxlength="30"></td>
								</tr>

							</table>
							
							<input id="pdb-upload-button" type="button" value="Upload PDB File" style="margin-left: 5px;">							
							<input type="button" value="Cancel" onClick="closePDBUploadOverlay()" style="margin-left: 5px;">
					
					</form>
				
				</div>
						 		       				
			</div>
			
			