<%-- pdb-upload-overlay.jsp --%>


			<%--
			
				Show this overlay when the user clicks the "Map Protein" link next to the chain on the structure view page
			
			--%>
			
			<%-- Changed background class from overlay-background to  lorikeet-modal-dialog-overlay-background --%>
			
			<div id="pdb-map-protein-modal-dialog-overlay-background" class="pdb-map-protein-modal-dialog-overlay-background" style="display: none;"  ></div>
			<div id="pdb-show-alignment-modal-dialog-overlay-background" class="pdb-show-alignment-modal-dialog-overlay-background" style="display: none;"  ></div>
			
			<%--  The div showing all the PDB upload dialog --%>
			
			<div id="pdb-map-protein-overlay-div" class=" pdb-map-protein-overlay-div overlay-outer-div" style="display: none; "  >
			
			
				<div id="pdb-map-protein-overlay-header" class="pdb-map-protein-overlay-header" style="width:100%; " >
					<h1 id="pdb-map-protein-overlay-X-for-exit-overlay" class="pdb-map-protein-overlay-X-for-exit-overlay" >X</h1>
					<h1 id="pdb-map-protein-overlay-header-text" class="pdb-map-protein-overlay-header-text" >Map Protein to PDB Chain</h1>
				</div>
				<div id="pdb-map-protein-overlay-body" class="pdb-map-protein-overlay-body" style="text-align:left;" >
					
					<table style="margin-top:10px;border:0px;width:100%;">
						<tr>
							<td style="width:450px;height:450px;border-width:1px;border-style:solid;border-color:#A55353;">
								<div id="pdb-map-protein-overlay-structure" style="width: 450; height: 450px;display:inline;"></div>
							</td>
							<td style="vertical-align:top;">
								<div id="pdb-map-protein-overlay-content" style="margin-left:20px;margin-top:10px;vertical-align:top;"></div>
							</td>
						</tr>
					</table>

				
				</div>
						 		       				
			</div>

			<div id="pdb-show-alignment-overlay-div" class=" pdb-show-alignment-overlay-div overlay-outer-div" style="display: none; "  >
			
			
				<div id="pdb-show-alignment-overlay-header" class="pdb-show-alignment-overlay-header" style="width:100%; " >
					<h1 id="pdb-show-alignment-overlay-X-for-exit-overlay" class="pdb-show-alignment-overlay-X-for-exit-overlay" >X</h1>
					<h1 id="pdb-show-alignment-overlay-header-text" class="pdb-show-alignment-overlay-header-text" >Show PDB Alignment</h1>
				</div>
				<div id="pdb-show-alignment-overlay-body" class="pdb-show-alignment-overlay-body" style="text-align:left;" >
					

				
				</div>
						 		       				
			</div>
			
			
			<%-- Template for the protein selection box in the protein mapping overlay content div --%>
			<div id="pdb-map-protein-overlay-protein-step-one" style="display:none;">
			
				<div>Select a protein to map to chain #CHAINID# (highlighted to the left):</div>
				<div id="pdb-map-protein-overlay-protein-select-div" style="margin-top:15px;"></div>
			</div>