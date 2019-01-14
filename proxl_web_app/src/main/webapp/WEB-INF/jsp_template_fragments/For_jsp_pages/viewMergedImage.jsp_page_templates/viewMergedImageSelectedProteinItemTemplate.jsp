

<%-- viewMergedImageSelectedProteinItemTemplate.jsp --%>


		<%-- !!!   Handlebars template Selected Protein Item  !!!!!!!!!   --%>
		
		<%-- 
		<script id="selected_protein_entry_template"
		--%>
		
		  <div class="outer-float  protein_select_outer_block_jq" 
		  	data-protein_id="{{proteinId}}"
		  	data-uid="{{uid}}"
		  	>
		  
		   <div class=" sort-handle-and-text protein_select_protein_item_and_sort_handle_block_jq " 
		   		data-uid="{{uid}}" data-protein_id="{{proteinId}}"
		   		 >
			<div class="sort-handle-float">
				<span class=" sort_handle_jq  tool_tip_attached_jq" 
					data-tooltip='<div style="margin-bottom: 3px;">Click to change protein.</div><div>Drag to re-order proteins</div>'
					style="cursor: pointer;"
				  ><img src="images/icon-draggable-small.png" 
				  ></span>
			</div >
		   <div class="text-float protein_select_text_container_jq " data-protein_id="{{proteinId}}" >
			{{proteinName}}
		   </div>
		   
		   <div class="delete-icon-float">
			  <input type="image" class="protein_delete_icon_jq tool_tip_attached_jq" data-tooltip="Remove Protein" 
					 data-protein_id="{{proteinId}}" data-uid="{{uid}}"  
					src="images/icon-delete-small.png" >
		   </div> 
		  </div>

		 </div>

			
