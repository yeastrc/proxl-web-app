




		<%-- !!!   Handlebars template Protein Bar Tool Tip  !!!!!!!!!   --%>
		
		<%-- 
		<div id="protein_bar_tool_tip_template" style="display: none;"  > 
		--%>
		
		
			<div style="text-align: center; color: black;">
			
				<%-- style="border-color: red; border-width: 1px; border-style: solid;" --%>
				<div style=" {{proteinNameDisplay}}" >
					<div style="padding-bottom: 6px; padding-top: 3px;">	
						{{proteinName}}
					</div>			
				</div>			
	
				<div >  <%-- style="  border-color: red; border-width: 1px; border-style: solid;  " --%>
	
					<svg width="104" height="50" style=" margin: 0px; padding: 0px;   padding-top: 4px;  ">
	
						<%-- 
						<line x1="1" y1="1" x2="20" y2="5" stroke="red" stroke-width="4" />
						--%>
						
						<%--  textClassAtPosition... set to "proxl-primary-color-bold-svg-text" (in global.css)
								to highlight a letter --%>
						
						<%-- sequence letters before, starting with one closest to position --%>
						<text x="8"  y="15" text-anchor="middle"
						  ><tspan style="font-size: 9px;" class="{{textClassAtPositionLeft3}}"
							>{{sequenceAtPositionLeft3}}</tspan></text>
						<text x="20" y="16" text-anchor="middle"
						  ><tspan style="font-size: 11px;" class="{{textClassAtPositionLeft2}}"
						  	>{{sequenceAtPositionLeft2}}</tspan></text>
						<text x="34" y="17" text-anchor="middle"
						  ><tspan style="font-size: 15px;" class="{{textClassAtPositionLeft1}}"
						  	>{{sequenceAtPositionLeft1}}</tspan></text>
	
						<%-- sequence letter at position first --%>
						<text x="52" y="20" text-anchor="middle"
						  ><tspan style="font-size: 24px;" class="{{textClassAtPosition}}"
						  	>{{sequenceAtPosition}}</tspan></text>
						
						<%-- sequence letters after, starting with one closest to position --%>
						<text x="70" y="17" text-anchor="middle"
						  ><tspan style="font-size: 15px;" class="{{textClassAtPositionRight1}}"
						  	>{{sequenceAtPositionRight1}}</tspan></text>
						<text x="84" y="16" text-anchor="middle"
						  ><tspan style="font-size: 11px;" class="{{textClassAtPositionRight2}}"
						  	>{{sequenceAtPositionRight2}}</tspan></text>
						<text x="96" y="15" text-anchor="middle"
						  ><tspan style="font-size: 9px;" class="{{textClassAtPositionRight3}}"
						  	>{{sequenceAtPositionRight3}}</tspan></text>
						
						
						<%-- position below sequence letter at position first --%>
						<text x="52" y="45" text-anchor="middle"><tspan style="font-size: 15px;">Pos: {{sequencePosition}}</tspan></text>
						
						<%-- trypsin cut points --%>
						{{#if  cutPointBetweenCenterAndFirstLeft}}
							<line x1="42" y1="1" x2="42" y2="22" stroke="red" stroke-width="2" />
						{{/if}}						
						{{#if  cutPointBetweenFirstLeftAndSecondLeft}}
							<line x1="26" y1="5" x2="26" y2="19" stroke="red" stroke-width="2" />
						{{/if}}								
						{{#if  cutPointBetweenSecondLeftAndThirdLeft}}
							<line x1="14" y1="6" x2="14" y2="18" stroke="red" stroke-width="1" />
						{{/if}}								

						{{#if  cutPointBetweenCenterAndFirstRight}}
							<line x1="62" y1="1" x2="62" y2="22" stroke="red" stroke-width="2" />
						{{/if}}						
						{{#if  cutPointBetweenFirstRightAndSecondRight}}
							<line x1="78" y1="5" x2="78" y2="19" stroke="red" stroke-width="2" />
						{{/if}}								
						{{#if  cutPointBetweenSecondRightAndThirdRight}}
							<line x1="91" y1="6" x2="91" y2="18" stroke="red" stroke-width="1" />
						{{/if}}								
					</svg>
				</div>
			</div>
			
			
		<%-- 
		</div>
		--%>

			
