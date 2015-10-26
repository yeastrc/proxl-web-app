<%@ include file="/WEB-INF/jsp-includes/pageEncodingDirective.jsp" %>

<%@ include file="/WEB-INF/jsp-includes/strutsTaglibImport.jsp" %>
<%@ include file="/WEB-INF/jsp-includes/jstlTaglibImport.jsp" %>

 <c:set var="pageTitle">View Search</c:set>

 <c:set var="pageBodyClass" >project-page</c:set>

 <c:set var="headerAdditions">

		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.tablesorter.min.js"></script> 
		<script type="text/javascript" src="${ contextPath }/js/libs/jquery.qtip.min.js"></script>
				
		<script type="text/javascript" src="${ contextPath }/js/nagWhenFormChangedButNotUpdated.js"></script>				

		<script type="text/javascript" src="${ contextPath }/js/createTooltipForProteinNames.js"></script>

		<script type="text/javascript" src="${ contextPath }/js/defaultPageView.js"></script>
		
		<script type="text/javascript" src="${ contextPath }/js/toggleVisibility.js"></script>
		
		<link rel="stylesheet" href="${ contextPath }/css/tablesorter.css" type="text/css" media="print, projection, screen" />
		<link type="text/css" rel="stylesheet" href="${ contextPath }/css/jquery.qtip.min.css" />

	<script>
		$(document).ready(function() 
		    { 

				   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
					  
			       	$("#crosslink-table").tablesorter(); // gets exception if there are no data rows
				   },10);

				   
	 			$(".table_header_jq").each(function() { // Grab search file links
	 				
	 				var tooltip_text_jq = $(this).children(".tooltip_text_jq");
	 			
	 				if ( tooltip_text_jq.length > 0 ) {
		 				
		 				var tipText = $(this).children(".tooltip_text_jq").text();
		 				
		 			    $(this).qtip({
							position: {
								target: 'mouse' // Position at the mouse...
									,
									adjust: { x: 5, y: 5 } // Offset it slightly from under the mouse
							},
		 			        content: {
		 			            text: tipText
		 			        }
		 			    });
	 				}
	 			});				   
		    } 
		); 
    

		//  function called after all HTML above main table is generated
		
		function createPartsAboveMainTable() {

	 		createImageViewerLink();
	 		
	 		createStructureViewerLink();
	 		
			
			   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
					  
					initNagUser();
			   },10);
			   
			   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
					
					initDefaultPageView() ;
			   },10);
		}
				
    
		function imageViewerJSON() {
			var json = { };
			
			json.psmQValueCutoff = <bean:write name="psmQValueCutoff" />;
			json.peptideQValueCutoff = <bean:write name="peptideQValueCutoff" />;
			
			return "#" + encodeURI( JSON.stringify( json ) );
		}
		
		function createImageViewerLink() {
			var html = "";
		     

			html += "[<a href='${ contextPath }/";
	          
			var defaultURL = $("#viewMergedImageDefaultPageUrl").val();
			
			if ( defaultURL === "" ) {
			          
				html += "viewMergedImage.do?project_id=<c:out value="${ projectId }"></c:out>" 
						+ "&searchIds=<bean:write name="search" property="id" />"
						+ imageViewerJSON() ;
						
			} else {
				
				html += defaultURL;
				
			}
			html += "'>Image View</a>]";
			
			$( "span#image-viewer-link-span" ).empty();
			$( "span#image-viewer-link-span" ).html (html );
		}
		
		function createStructureViewerLink() {
			var html = "";
			

			html += "[<a href='${ contextPath }/";
			   			   
			  
			var defaultURL = $("#viewMergedStructureDefaultPageUrl").val();
			
			if ( defaultURL === "" ) {
			
				html += "viewMergedStructure.do?project_id=<c:out value="${ projectId }"></c:out>" 
							+ "&searchIds=<bean:write name="search" property="id" />"
							+ imageViewerJSON() ;
							
			} else {
				
				html += defaultURL;
			}
						
			html += "'>Structure View</a>]";
								
			$( "span#structure-viewer-link-span" ).empty();
			$( "span#structure-viewer-link-span" ).html (html );
		}

		function getValuesFromForm() {
			
			var psmQValueCutoff = $("#psmQValueCutoff").val();
			var peptideQValueCutoff = $("#peptideQValueCutoff").val();
			
			var filterNonUniquePeptides = $("#filterNonUniquePeptides").is( ':checked' );
			var filterOnlyOnePSM = $("#filterOnlyOnePSM").is( ':checked' );
			var filterOnlyOnePeptide = $("#filterOnlyOnePeptide").is( ':checked' );
			
			var excludeTaxonomy = [];

			var $excludeTaxonomy_jq = $(".excludeTaxonomy_jq");
			
			$excludeTaxonomy_jq.each(function () {
				
				var $this = $(this);
				
			    if ( $this.is( ':checked' ) ) {
			    	
			    	var value = $this.val();
			    	
			    	excludeTaxonomy.push( value );
			    }
			    
			});
			
			
			var excludeProtein = $("#excludeProtein").val();

			
			var formValues = {
					psmQValueCutoff : psmQValueCutoff,
					peptideQValueCutoff : peptideQValueCutoff,
					filterNonUniquePeptides : filterNonUniquePeptides,
					filterOnlyOnePSM : filterOnlyOnePSM,
					filterOnlyOnePeptide : filterOnlyOnePeptide,
					excludeTaxonomy : excludeTaxonomy,
					excludeProtein : excludeProtein
			};
			return formValues;
		}		
		
	</script>



</c:set>



<%@ include file="/WEB-INF/jsp-includes/header_main.jsp" %>

	
		<%@ include file="/WEB-INF/jsp-includes/defaultPageViewFragment.jsp" %>
			
		<div class="overall-enclosing-block">
	
			<h2 style="margin-bottom:5px;">List search protein coverage:</h2>
	
			<div style="margin-bottom:20px;">
				[<a class="tool_tip_attached_jq" data-tooltip="View peptides" href="${ contextPath }/viewSearchPeptide.do?<bean:write name="queryString" />">Peptide View</a>]
		
				[<a class="tool_tip_attached_jq" data-tooltip="View proteins" href="${ contextPath }/viewSearchCrosslinkProtein.do?<bean:write name="queryString" />">Protein View</a>]
				<span class="tool_tip_attached_jq" data-tooltip="Graphical view of links between proteins" id="image-viewer-link-span"></span>
				
				<input type="hidden" id="viewMergedImageDefaultPageUrl" 
					value="<proxl:defaultPageUrl pageName="viewMergedImage.do" searchId="${ search.id }"></proxl:defaultPageUrl>">
				
				
				<span class="tool_tip_attached_jq" data-tooltip="View data on 3D structures" id="structure-viewer-link-span"></span>
				
				<input type="hidden" id="viewMergedStructureDefaultPageUrl" 
					value="<proxl:defaultPageUrl pageName="viewMergedStructure.do" searchId="${ search.id }"></proxl:defaultPageUrl>">
		
			</div>
	
			<html:form action="viewProteinCoverageReport" method="get" styleId="form_get_for_updated_parameters">
			
				<html:hidden property="project_id"/>
						
				<html:hidden property="searchId"/>
			
			<table style="border-width:0px;">
				<tr>
					<td valign="top">Search:</td>
					<td>
					
						<%--  Set to false to not show color block before search for key --%>
						<c:set var="showSearchColorBlock" value="${ false }" />
						
						<%--  Include file is dependent on containing loop having varStatus="searchVarStatus"  --%>
						<%@ include file="/WEB-INF/jsp-includes/searchDetailsBlock.jsp" %>

					</td>
				</tr>

				<tr>
					<td>PSM <span style="white-space: nowrap">Q-value</span> cutoff:</td>
					<td><html:text property="psmQValueCutoff" styleId="psmQValueCutoff" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:text></td>
				</tr>
				
				<tr>
					<td>Peptide <span style="white-space: nowrap">Q-value</span> cutoff:</td>
					<td><html:text property="peptideQValueCutoff" styleId="peptideQValueCutoff" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:text></td>
				</tr>
				

				<tr>
					<td>Exclude xlinks with:</td>
					<td>
						 <label><span style="white-space:nowrap;" >
							<html:checkbox property="filterNonUniquePeptides" styleId="filterNonUniquePeptides" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:checkbox>					
						 	 no unique peptides
						 </span></label>
						 <label><span style="white-space:nowrap;" >
							<html:checkbox property="filterOnlyOnePSM" styleId="filterOnlyOnePSM" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:checkbox>					
						 	 only one PSM
						 </span></label>
						 <label><span style="white-space:nowrap;" >
							<html:checkbox property="filterOnlyOnePeptide" styleId="filterOnlyOnePeptide" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:checkbox>					
						 	 only one peptide
						 </span></label>
					</td>
				</tr>
								
<%-- 						 
				<tr>
					<td>Filter out xlinks with no unique peptides:</td>
					<td>
						<html:checkbox property="filterNonUniquePeptides" styleId="filterNonUniquePeptides" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" ></html:checkbox>					
					</td>
				</tr>
--%>					

				<tr>
					<td>Exclude organisms:</td>
					<td>
						<logic:iterate id="taxonomy" name="taxonomies">
						 <label style="white-space: nowrap" >
						  <html:multibox property="excludeTaxonomy" styleClass="excludeTaxonomy_jq" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" >
						   <bean:write name="taxonomy" property="key"/> 
						  </html:multibox> 
						   <span style="font-style:italic;"><bean:write name="taxonomy" property="value"/></span>
						 </label> 
						</logic:iterate>				
					</td>
				</tr>

				<tr>
					<td>Exclude protein(s):</td>
					<td>
						<%--  shortened property from "excludeProtein" to "excP" to shorten the URL  --%>
						<html:select property="excP" multiple="true" styleId="excludeProtein" onchange="searchFormChanged_ForNag(); searchFormChanged_ForDefaultPageView();" >
							<html:options collection="proteins" property="nrProtein.nrseqId" labelProperty="name" />
						</html:select>			
					</td>
				</tr>
				
				<tr>
					<td>&nbsp;</td>
					<td>
						<input type="submit" value="Update">

						<c:if test="${ authAccessLevel.projectOwnerAllowed }" >
							<input type="button" value="Save As Default" id="mergedImageSaveOrUpdateDefaultPageView" 
								onclick="saveOrUpdateDefaultPageView( { clickedThis : this, searchId: <bean:write name="search" property="id" /> } )">
						</c:if>
					</td>
				</tr>
			
			</table>
			</html:form>
	
			<h3 style="display:inline;">Protein Coverage Report:</h3>

			<div style="display:inline;">
				[<a class="tool_tip_attached_jq" data-tooltip="Download as tab-delimited text" href="${ contextPath }/downloadProteinCoverageReport.do?<bean:write name="mergedQueryString" />">Download Coverage Report</a>]
			</div>


			<%--  Create via javascript the parts that will be above the main table --%>
			<script type="text/javascript">
			
				createPartsAboveMainTable();
				
			</script>
			
			
				<table style="" id="crosslink-table" class="tablesorter">
				
					<thead>
					<tr>
						<th style="text-align:left;width:15%;font-weight:bold;" class="table_header_jq">
							Protein
						</th>
						<th style="width:5%;text-align:left;font-weight:bold;" class="table_header_jq">
							Residues
							<span class="tooltip_text_jq" style="display: none;" >
								Residues
							</span>
						</th>
						<th style="width:5%;font-weight:bold;" class="table_header_jq">
							Sequence Coverage
							<span class="tooltip_text_jq" style="display: none;" >
								Sequence Coverage
							</span>
						</th>
						<th style="width:5%;text-align:left;font-weight:bold;" class="table_header_jq">
							Linkable Residues
							<span class="tooltip_text_jq" style="display: none;" >
								Count of Linkable Residues
							</span>
						</th>
						<th style="width:5%;text-align:left;font-weight:bold;" class="table_header_jq">
							Linkable Residues Covered
							<span class="tooltip_text_jq" style="display: none;" >
								Count of Linkable Residues covered by Sequence Coverage
							</span>
						</th>
						<th style="width:5%;text-align:left;font-weight:bold;" class="table_header_jq">
							Linkable Residues Coverage
							<span class="tooltip_text_jq" style="display: none;" >
								Fraction of Linkable Residues covered by Sequence Coverage
							</span>
						</th>

						<th style="width:6%;font-weight:bold;" class="table_header_jq">
							M+L+C Residues
							<span class="tooltip_text_jq" style="display: none;" >
								Count of Monolinks, Looplinks and Crosslinks Residues
							</span>
						</th>
						<th style="width:6%;font-weight:bold;" class="table_header_jq">
							M+L+C Coverage
							<span class="tooltip_text_jq" style="display: none;" >
								Fraction of Linkable Residues covered by Monolinks, Looplinks and Crosslinks   
							</span>
						</th>

						<th style="width:6%;font-weight:bold;" class="table_header_jq">
							L+C Residues
							<span class="tooltip_text_jq" style="display: none;" >
								Count of Looplinks and Crosslinks Residues
							</span>
						</th>
						<th style="width:6%;font-weight:bold;" class="table_header_jq">
							L+C Coverage						
							<span class="tooltip_text_jq" style="display: none;" >
								Fraction of Linkable Residues covered by Looplinks and Crosslinks
							</span>
						</th>

						<th style="width:6%;font-weight:bold;" class="table_header_jq">
							M Residues
							<span class="tooltip_text_jq" style="display: none;" >
								Count of Monolinks Residues
							</span>
						</th>
						<th style="width:6%;font-weight:bold;" class="table_header_jq">
							M Coverage
							<span class="tooltip_text_jq" style="display: none;" >
								Fraction of Linkable Residues covered by Monolinks
							</span>
						</th>

						<th style="width:6%;font-weight:bold;" class="table_header_jq">
							L Residues
							<span class="tooltip_text_jq" style="display: none;" >
								Count of Looplinks Residues
							</span>
						</th>
						<th style="width:6%;font-weight:bold;" class="table_header_jq">
							L Coverage
							<span class="tooltip_text_jq" style="display: none;" >
								Fraction of Linkable Residues covered by Looplinks
							</span>
						</th>

						<th style="width:6%;font-weight:bold;" class="table_header_jq">
							C Residues
							<span class="tooltip_text_jq" style="display: none;" >
								Count of Crosslinks Residues
							</span>
						</th>
						<th style="width:6%;font-weight:bold;" class="table_header_jq">
							C Coverage
							<span class="tooltip_text_jq" style="display: none;" >
								Fraction of Linkable Residues covered by Crosslinks
							</span>
						</th>
						
					</tr>
					</thead>
						
					<logic:iterate id="coverage" name="proteinCoverageData">

						<tr>
							<td><bean:write name="coverage" property="name" /></td>
							<td><bean:write name="coverage" property="numResidues" /></td>
							<td>
								<bean:write name="coverage" property="sequenceCoverage" />
							</td>
							<td><bean:write name="coverage" property="numLinkableResidues" /></td>
							<td><bean:write name="coverage" property="numLinkableResiduesCovered" /></td>
							<td><bean:write name="coverage" property="linkableResiduesCoverageFmt" /></td>

							<td><bean:write name="coverage" property="numMLCResidues" /></td>
							<td><bean:write name="coverage" property="MLCSequenceCoverage" /></td>
							
							<td><bean:write name="coverage" property="numLCResidues" /></td>
							<td><bean:write name="coverage" property="LCSequenceCoverage" /></td>
							
							<td><bean:write name="coverage" property="monolinkedResidues" /></td>
							<td><bean:write name="coverage" property="MSequenceCoverage" /></td>
							
							<td><bean:write name="coverage" property="looplinkedResidues" /></td>
							<td><bean:write name="coverage" property="LSequenceCoverage" /></td>
							
							<td><bean:write name="coverage" property="crosslinkedResidues" /></td>
							<td><bean:write name="coverage" property="CSequenceCoverage" /></td>
							
						</tr>



					</logic:iterate>
				</table>
				
		<style>
		
		   .header-def-label { padding-right: 5px; }
		   .header-def-description { padding-left: 5px; }
		</style>
				
				
				<table >
				
					<tr>
						<td class="header-def-label" style="padding-bottom: 5px;">
							Column Header
						</td>
						<td class="header-def-description" style="padding-bottom: 5px;">
							Column Description
						</td>
					</tr>	
				
					<tr>
						<td class="header-def-label">
							Protein
						</td>
					</tr>				
					<tr>
						<td class="header-def-label">
							Residues
						</td>
						<td class="header-def-description">
								Count of Residues
						</td>
					</tr>				
					<tr>
						<td class="header-def-label">
							Sequence Coverage
						</td>
						<td class="header-def-description">
								Sequence Coverage
						</td>
					</tr>				
					<tr>
						<td class="header-def-label">
							Linkable Residues
						</td>
						<td class="header-def-description">
								Count of Linkable Residues
						</td>
					</tr>			
			
					<tr>
						<td class="header-def-label">
							Linkable Residues Covered
						</td>
						<td class="header-def-description">
								Count of Linkable Residues covered by Sequence Coverage
						</td>
					</tr>	
					<tr>
						<td class="header-def-label">
							Linkable Residues Coverage
						</td>
						<td class="header-def-description">
								Fraction of Linkable Residues covered by Sequence Coverage
						</td>
					</tr>				
					<tr>

						<td class="header-def-label">
							M+L+C Residues
						</td>
						<td class="header-def-description">
								Count of Monolinks, Looplinks and Crosslinks Residues
						</td>
					</tr>				
					<tr>
						<td class="header-def-label">
							M+L+C Coverage
						</td>
						<td class="header-def-description">
								Fraction of Linkable Residues covered by Monolinks, Looplinks and Crosslinks   
						</td>
					</tr>				
					<tr>

						<td class="header-def-label">
							L+C Residues
						</td>
						<td class="header-def-description">
								Count of Looplinks and Crosslinks Residues
						</td>
					</tr>				
					<tr>
						<td class="header-def-label">
							L+C Coverage	
						</td>					
						<td class="header-def-description">
								Fraction of Linkable Residues covered by Looplinks and Crosslinks
						</td>
					</tr>				
					<tr>

						<td class="header-def-label">
							M Residues
						</td>
						<td class="header-def-description">
								Count of Monolinks Residues
						</td>
					</tr>				
					<tr>
						<td class="header-def-label">
							M Coverage
						</td>
						<td class="header-def-description">
								Fraction of Linkable Residues covered by Monolinks 
						</td>
					</tr>				
					<tr>

						<td class="header-def-label">
							L Residues
						</td>
						<td class="header-def-description">
								Count of Looplinks Residues
						</td>
					</tr>				
					<tr>
						<td class="header-def-label">
							L Coverage
						</td>
						<td class="header-def-description">
								Fraction of Linkable Residues covered by Looplinks
						</td>
					</tr>				
					<tr>

						<td class="header-def-label">
							C Residues
						</td>
						<td class="header-def-description">
								Count of Crosslinks Residues
						</td>
					</tr>				
					<tr>
						<td class="header-def-label">
							C Coverage
						</td>
						<td class="header-def-description">
								Fraction of Linkable Residues covered by Crosslinks
						</td>
						
					</tr>				
				</table>
								

			<%@ include file="/WEB-INF/jsp-includes/nagWhenFormChangedButNotUpdated_Overlay.jsp" %>
	
		</div>
	

<%@ include file="/WEB-INF/jsp-includes/footer_main.jsp" %>
