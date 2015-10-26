
<%-- nagWhenFormChangedButNotUpdated_Overlay.jsp --%>


			<%--  Nag user when form changed but "Update" button not clicked  Overlay Background --%>
			
			<%-- Changed background class from overlay-background to  lorikeet-modal-dialog-overlay-background --%>
			
			<div id="nag-user-modal-dialog-overlay-background" class="nag-user-modal-dialog-overlay-background" style="display: none;"  >
			
			</div>
			
			<%--  Nag user when form changed but "Update" button not clicked  Overlay Div --%>
			
			<div id="nag-user-overlay-div" class=" nag-user-overlay-div overlay-outer-div" style="display: none; "  >
			
			
				<div id="nag-user-overlay-header" class="nag-user-overlay-header" style="width:100%; " >
					<h1 id="nag-user-overlay-X-for-exit-overlay" class="nag-user-overlay-X-for-exit-overlay" >X</h1>
					<h1 id="nag-user-overlay-header-text" class="nag-user-overlay-header-text" >Please Update for changed form values</h1>
				</div>
				<div id="nag-user-overlay-body" class="nag-user-overlay-body" style="text-align: center" >
			
					<div style="">
						The form entries are not reflected in the data shown. 
					</div>
					<div style="margin-top: 5px;">
						Please click the "Update" button to refresh the data.
					</div>
					<div style="margin-top: 10px;">
						<%-- Button for Merged Image Page --%>
						<input id="nag_update_button_merged_image_page" type="button" 
							value="Update From Database" onClick="closeNagUserOverlay();refreshData()" style="display:none;">

						<%-- Button for other Pages --%>
						<input  id="nag_update_button_other_pages" type="button" 
							value="Update" onClick='closeNagUserOverlay();$("#form_get_for_updated_parameters").submit()'>
						
						<input type="button" value="Close" onClick="closeNagUserOverlay()" style="margin-left: 5px;">
					
					</div>
						 		       				
				</div>
			
			</div>	
