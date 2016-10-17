

<%--  viewMergedImageManageProteinBarSingleBarRegionTemplate.jsp   --%>

		<%-- !!!   Handlebars template Protein Bar Tool Tip  !!!!!!!!!   --%>

<%--  Enclosed in:  <script id="view_protein_bar_highlighting_overlay_bar_region_template"  type="text/x-handlebars-template">  --%>


<div > <%-- outer div so that template has a single outermost element.  Supports finding .protein_divider_jq div at bottom --%>

  	<div data-uid="{{uid}}" data-protein_id="{{proteinId}}" data-protein_length="{{proteinLength}}"
  		class= " single_protein_bar_block_jq " style="padding-top: 3px; padding-bottom: 3px;">
	  	
	  	<div style="margin-bottom: 5px;">
	  		<table  class="table-no-border-no-cell-spacing-no-cell-padding">
	  		 <tr class="table-no-border-no-cell-spacing-no-cell-padding">
	  		 
	  		  <td style="padding-left: 0px; padding-right: 6px;">Protein:</td>
	  		  <td>{{proteinName}}</td>
	  		 </tr>
	  		 <tr>
	  		  <td style="padding-left: 0px; padding-right: 6px; padding-top: 3px;">Length:</td>
	  		  <td style="padding-top: 3px;">{{proteinLength}}</td>
	  		 </tr>
	  		</table>
	  	</div>
	  	<div >
		  	<div >
		  		<label>
		  			<input class=" whole_protein_bar_selected_checkbox_jq "
		  				type="checkbox" > 
		  			Select whole protein bar
		  		</label> 
		  	</div>
		  	<div style="position: relative;">
		  	
			  	<div class= " regions_block_jq " style="">
			  	
				  	<div style="padding-top: 5px; " >
				  		Protein bar regions:
				  	</div>
				  	<div style="padding-left: 20px; padding-bottom: 5px;">
				  		<div  class= "regions_items_block_jq ">
	
						</div>
					  	<div style="padding-top: 1px;">
					  		<a href="javascript:" class=" add_region_jq " >+Add Region</a>
					  	</div>
				  	</div>
				</div>
			</div>
		</div>  
	</div>
	
	<%-- Protein divider.  Hidden after last entry --%>
	
	<div class="search-entry-bottom-border protein_divider_jq " style="width: 220px;"></div>
</div>