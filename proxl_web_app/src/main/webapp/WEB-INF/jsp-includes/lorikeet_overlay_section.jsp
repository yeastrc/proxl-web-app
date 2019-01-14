
<%--  lorikeet_overlay_section.jsp --%>



			<%--  Lorikeet Overlay Background --%>
			
			<%-- Changed background class from overlay-background to  lorikeet-modal-dialog-overlay-background --%>
			
			<div id="lorikeet-modal-dialog-overlay-background" class="lorikeet-modal-dialog-overlay-background" style="display: none;"  >
			
			</div>
			
			<%--  Lorikeet Overlay Div --%>
			
			<div id="view-spectra-overlay-div" class=" view-spectra-overlay-div overlay-outer-div" style="display: none; "  >
			
			
				<div id="view-spectra-overlay-header" class="view-spectra-overlay-header" style="width:100%; " >

					<h1 id="view-spectra-overlay-X-for-exit-overlay" class="view-spectra-overlay-X-for-exit-overlay" >X</h1>

					<div style="float: right; padding-right: 5px; padding-top: 10px;">
				  		<a href="http://proxl-web-app.readthedocs.io/en/latest/using/spectrum-viewer.html"  target="_help_window" id="help_header_link" 
				  			><img src="images/icon-help.png" 
				  		></a>
					</div>
										
					<h1 id="view-spectra-overlay-header-text" class="view-spectra-overlay-header-text" >Spectrum Viewer</h1>
				</div>
				<div id="view-spectra-overlay-body" class="view-spectra-overlay-body" >
			
			
						 		       				
					<div  id="lorikeet-loop-link-data" style=" display: none;">
						<div style="text-align: left;" >
						 <table>
						  <tr>
						  	<td style="vertical-align: top; font-weight: bold;" >Looplinked peptide:</td>
							
							<td  style="vertical-align: top; font-family: monospace; padding-left: 10px;">

								<%-- 			
								Looplinked peptide:   MPKDFPKPFPQK
												        |   |
										       			+---+
								 --%>

								<div id="lorikeet_looplink_character_based_visual" style=" display: none;">
								
									<div><span id="lorikeet_looplink_sequence"></span></div>
									
									<div ><span id="lorikeet_looplink_vertical_lines_space_before"></span
											>|<span id="lorikeet_looplink_vertical_lines_space_inside"></span
											><span id="lorikeet_looplink_second_vert_bar">|</span></div>
									<div ><span id="lorikeet_looplink_dashes_space_before"></span
											>+<span id="lorikeet_looplink_dashes_inside"></span
											><span id="lorikeet_looplink_second_plus">+</span></div>
								</div>
								
								<div id="lorikeet_looplink_svg_based_visual" style=" display: none;" >
								
								
								</div> 
								
							</td>
						  </tr>
						 </table>
					 	</div>

					</div>

<%-- 			
					<div  id="lorikeet-loop-link-data" style="display: none;">
						<div style="text-align: center;">
							Loop Link Data
						</div>
						<div >
							Position&nbsp;1: <span id="lorikeet-loop-link-data-position-1"></span>
						</div>
						<div >
							Position&nbsp;2: <span id="lorikeet-loop-link-data-position-2"></span>
						</div>
						<div >
							Linker Mass: <span id="lorikeet-loop-link-data-linker-mass"></span>
						</div>
					</div>
--%>			
			
					<div  id="lorikeet-cross-link-data" style=" display: none;">
					
						<div style="text-align: left;" >
						 <table>
						  <tr>
						  	<td style="vertical-align: top; font-weight: bold; " >Crosslinked peptides:</td>
							
							<td  style="vertical-align: top; font-family: monospace; padding-left: 10px;">
								<div><span id="lorikeet_crosslink_sequence_1_space_before"
										></span><span id="lorikeet_crosslink_sequence_1"></span></div>
								<div ><span id="lorikeet_crosslink_linker_space_before"></span>|</div>
								<div ><span id="lorikeet_crosslink_sequence_2_space_before"
										></span><span id="lorikeet_crosslink_sequence_2"></span></div>
							</td>
						  </tr>
						 </table>
					 	</div>
					 	
					 </div>
<%--
					<div  id="lorikeet-crosslink-list" style="display: none;">

						<div style="text-align: center; ">
							<h3>Cross Link Data: (Same spectrum shown in both lorikeet viewers)</h3>
						</div>
						<div>
							Cross linked residue letter in <span style="color: #00FF00;" >green</span>
						</div>
						<br>
						<div >
							Cross Link 1:
						</div>
						<div style="margin-left: 10px;" >

							<div >
								Sequence: <span id="lorikeet-crosslink-sequence-1"></span>
							</div>
							<div >
								Position: <span id="lorikeet-crosslink-data-position-1"></span>
							</div>
						</div>
						<br>
						<div >
							Cross Link 2:
						</div>
						<div style="margin-left: 10px;" >
							<div >
								Sequence: <span id="lorikeet-crosslink-sequence-2"></span>
							</div>
							<div >
								Position: <span id="lorikeet-crosslink-data-position-2"></span>
							</div>
						</div>
						<br>
						<div >
							Linker Mass: <span id="lorikeet-crosslink-data-linker-mass"></span>
						</div>

					
					</div> <!--  end id="lorikeet-crosslink-list" -->
--%> 					
			
					<div  id="lorikeet-dimer-data" style="  display: none;">
					
						<div style="text-align: left;" >
						 <table>
						  <tr>
						  	<td style="vertical-align: top; font-weight: bold;" >Dimer:</td>
							
							<td  style="vertical-align: top; font-family: monospace; padding-left: 10px;">
								<div><span id="lorikeet_dimer_sequence_1"></span></div>
								<div><span id="lorikeet_dimer_sequence_2"></span></div>
							</td>
						  </tr>
						 </table>
					 	</div>
					 	
					 </div>
			
					<%-- The div <div id="lorikeet"></div> will be inserted in this div and removed from this div --%>
				    <div id="lorikeet-holder-1-div" ></div>

					<div id="lorikeet-holder-divider-div" style="height: 20px; display: none;">&nbsp;</div>

				    <div id="lorikeet-holder-2-div"  style="display: none;"></div>
			
				</div>
			
			</div>	
