
//  viewSearchPeptide.js   

//  Javascript for the viewSearchPeptide.jsp page

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


$(document).ready(function() 
    { 

	
	   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
		  
       	$("#crosslink-table").tablesorter(); // gets exception if there are no data rows
	   },10);
    } 
); // end $(document).ready(function() 


//  Constructor

var ViewSearchPeptidePageCode = function() {

		var _query_json_field_Contents = null;

		
		//  function called after all HTML above main table is generated

		this.createPartsAboveMainTable = function() {
			
			var objectThis = this;

			   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
					  
				   objectThis.get_query_json_field_ContentsFromHiddenField();

//			 		createImageViewerLink();
//			 		
//			 		createStructureViewerLink();
			 		
					
			   },10);
			  
			
			
			   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
					  
//					initNagUser();
			   },10);
			   
			   setTimeout( function() { // put in setTimeout so if it fails it doesn't kill anything else
					
//					initDefaultPageView() ;
			   },10);
			   
			   
		};
		
		
		
		
		this.passCutoffsToPSMWebserviceJS = function( psmPeptideCutoffsRootObject ) {
			
			viewPsmsLoadedFromWebServiceTemplate.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
			
			viewPeptidesRelatedToPSMsByScanId.setPsmPeptideCriteria( psmPeptideCutoffsRootObject );
		};
		
		//   Currently expect _psmPeptideCriteria = { peptideQValueCutoff: 0.01, psmQValueCutoff: 0.01   }; or other numbers
		
		
		 this.get_query_json_field_ContentsFromHiddenField = function() {
			
			 var query_json_field_outside_form_id = "query_json_field_outside_form";
			 
			 var $query_json_field =  $( "#" + query_json_field_outside_form_id );
			
			if ( $query_json_field.length === 0 ) {
				
				throw "No HTML field with id '" + query_json_field_outside_form_id + "'";
			}
			
			var query_json_field_String = $query_json_field.val();
			
			try {
				_query_json_field_Contents = JSON.parse( query_json_field_String );
				
			} catch( e ) {
				
				throw "Failed to parse JSON from HTML field with id 'query_json_field'.  JSON String: " + query_json_field_String;
				
			}

			
			cutoffProcessingCommonCode.putCutoffsOnThePage(  { cutoffs : _query_json_field_Contents.cutoffs } );

			
			this.passCutoffsToPSMWebserviceJS( _query_json_field_Contents.cutoffs );
			
			
			//  Mark check boxes for chosen link types
			
			var linkTypes = _query_json_field_Contents.linkTypes;
			
			if ( linkTypes !== undefined && linkTypes !== null ) {
			
				//  linkTypes not null so process it, empty array means nothing chosen
				
				if ( linkTypes.length > 0 ) {
	
					var $link_type_jq = $(".link_type_jq");
	
					$link_type_jq.each( function( index, element ) {
	
						var $item = $( this );
	
						var linkTypeFieldValue = $item.val();
	
						//  if linkTypeFieldValue found in linkTypes array, set it to checked
	
						for ( var linkTypesIndex = 0; linkTypesIndex < linkTypes.length; linkTypesIndex++ ) {
	
							var linkTypesEntry = linkTypes[ linkTypesIndex ];
	
							if ( linkTypesEntry === linkTypeFieldValue ) {
	
								$item.prop('checked', true);
							}
						}
					});
				}
				
			} else {
				
				//  linkTypes null means all are chosen, since don't know which one was wanted
				

				var $link_type_jq = $(".link_type_jq");

				$link_type_jq.each( function( index, element ) {

					var $item = $( this );

					$item.prop('checked', true);
				});
				
			}
			
			

			//  Mark check boxes for chosen dynamic mod masses
			
			var dynamicModMasses = _query_json_field_Contents.mods;
			
			if ( dynamicModMasses !== undefined && dynamicModMasses !== null && dynamicModMasses.length > 0  ) {
			
				//  dynamicModMasses not null so process it, empty array means nothing chosen
				
				if ( dynamicModMasses.length > 0 ) {
	
					var $mod_mass_filter_jq = $(".mod_mass_filter_jq");
	
					$mod_mass_filter_jq.each( function( index, element ) {
	
						var $item = $( this );
	
						var linkTypeFieldValue = $item.val();
	
						//  if linkTypeFieldValue found in dynamicModMasses array, set it to checked
	
						for ( var dynamicModMassesIndex = 0; dynamicModMassesIndex < dynamicModMasses.length; dynamicModMassesIndex++ ) {
	
							var dynamicModMassesEntry = dynamicModMasses[ dynamicModMassesIndex ];
	
							if ( dynamicModMassesEntry === linkTypeFieldValue ) {
	
								$item.prop('checked', true);
							}
						}
					});
				}
				
			} else {
				
				//  dynamicModMasses null means all are chosen, since don't know which one was wanted
				

				var $mod_mass_filter_jq = $(".mod_mass_filter_jq");

				$mod_mass_filter_jq.each( function( index, element ) {

					var $item = $( this );

					$item.prop('checked', true);
				});
				
			}
		};
		
		
		
		/////////////
		
		

		 this.put_query_json_field_ContentsToHiddenField = function() {
			
			var $query_json_field = $("#query_json_field");
			
			if ( $query_json_field.length === 0 ) {
				
				throw "No HTML field with id 'query_json_field'";
			}
			
//			var inputCutoffs = _query_json_field_Contents.cutoffs;
			
			var getCutoffsFromThePageResult = cutoffProcessingCommonCode.getCutoffsFromThePage(  {  } );
			
			var getCutoffsFromThePageResult_FieldDataFailedValidation = getCutoffsFromThePageResult.getCutoffsFromThePageResult_FieldDataFailedValidation;
			
			if ( getCutoffsFromThePageResult_FieldDataFailedValidation ) {
				
				//  Cutoffs failed validation and error message was displayed
				
				//  EARLY EXIT from function
				
				return { output_FieldDataFailedValidation : getCutoffsFromThePageResult_FieldDataFailedValidation };
			}
			
			var outputCutoffs = getCutoffsFromThePageResult.cutoffsBySearchId;
			

			//  Create array from check boxes for chosen link types
			
			var outputLinkTypes = [];
			
//			var allLinkTypesChosen = true;
			
			var $link_type_jq = $(".link_type_jq");

			$link_type_jq.each( function( index, element ) {

				var $item = $( this );
				
				if ( $item.prop('checked') === true ) {

					var linkTypeFieldValue = $item.val();
					
					outputLinkTypes.push( linkTypeFieldValue );
					
//				} else {
//					
//					allLinkTypesChosen = false;
				}
			});
		
//			if ( allLinkTypesChosen ) {
//				
//				outputLinkTypes = null;  //  set to null when all chosen
//			}
			
			
			
			
			

			//  Create array from check boxes for chosen dynamic mod masses

			var outputDynamicModMasses = [];
			
			var allDynamicModMassesChosen = true;
			
			var $mod_mass_filter_jq = $(".mod_mass_filter_jq");

			$mod_mass_filter_jq.each( function( index, element ) {

				var $item = $( this );
				
				if ( $item.prop('checked') === true ) {

					var fieldValue = $item.val();
					
					outputDynamicModMasses.push( fieldValue );
					
				} else {
					
					allDynamicModMassesChosen = false;
				}
			});
		
			if ( allDynamicModMassesChosen ) {
				
				outputDynamicModMasses = null;  //  set to null when all chosen
			}
			
			
			var output_query_json_field_Contents = { 
					
					cutoffs : outputCutoffs, 
					linkTypes : outputLinkTypes, 
					mods : outputDynamicModMasses 
			};
			
			try {
				var output_query_json_field_String = JSON.stringify( output_query_json_field_Contents );
				
				$query_json_field.val( output_query_json_field_String );
				
			} catch( e ) {
				
				throw "Failed to stringify JSON to HTML field with id 'query_json_field'.";
				
			}
			
		};
		
		///////////////////////
		
		this.updatePageForFormParams = function() {
		
			
			var put_query_json_field_ContentsToHiddenFieldResult =
				this.put_query_json_field_ContentsToHiddenField();
			
			if ( put_query_json_field_ContentsToHiddenFieldResult 
					&& put_query_json_field_ContentsToHiddenFieldResult.output_FieldDataFailedValidation ) {
				
				//  Only submit if there were no errors in the input data

				return;
			}
			
			$('#form_get_for_updated_parameters').submit();
			
		};
		
};

//  Instance of class

var viewSearchPeptidePageCode = new ViewSearchPeptidePageCode();



		

//		function imageViewerJSON() {
//			var json = { };
//			
//			json.psmQValueCutoff = <bean:write name="psmQValueCutoff" />;
//			json.peptideQValueCutoff = <bean:write name="peptideQValueCutoff" />;
//			
//			return "#" + encodeURI( JSON.stringify( json ) );
//		}

		
//		function createImageViewerLink() {
//			var html = "";
//			
//			//   Contains EL ${ contextPath }
//
//			html += "[<a href='${ contextPath }/";
//			          
//			var defaultURL = $("#viewMergedImageDefaultPageUrl").val();
//			
//			if ( defaultURL === "" ) {
//				      
//				//   Contains EL ${ projectId }
//				
//				html += "image.do?project_id=<c:out value="${ projectId }"></c:out>" 
//						+ "&searchIds=<bean:write name="search" property="id" />"
//						+ imageViewerJSON() ;
//						
//			} else {
//				
//				html += defaultURL;
//				
//			}
//			html += "'>Image View</a>]";
//			
//			
//			$( "span#image-viewer-link-span" ).empty();
//			$( "span#image-viewer-link-span" ).html (html );
//		}
//		
//		function createStructureViewerLink() {
//			
//			var $structure_viewer_link_span = $("#structure-viewer-link-span");
//			
//			if ( $structure_viewer_link_span.length > 0 ) {
//				
//				var html = "";
//				
//				//   Contains EL ${ contextPath }
//
//				html += "[<a href='${ contextPath }/";
//				  
//				var defaultURL = $("#viewMergedStructureDefaultPageUrl").val();
//				
//				if ( defaultURL === "" ) {
//				
//					//   Contains EL ${ projectId }
//
//					html += "structure.do?project_id=<c:out value="${ projectId }"></c:out>" 
//								+ "&searchIds=<bean:write name="search" property="id" />"
//								+ imageViewerJSON() ;
//								
//				} else {
//					
//					html += defaultURL;
//				}
//					
//				html += "'>Structure View</a>]";						
//							
//				$structure_viewer_link_span.empty();
//				$structure_viewer_link_span.html( html );
//			}
//		}

//		function getValuesFromForm() {
//			
//			var psmQValueCutoff = $("#psmQValueCutoff").val();
//			var peptideQValueCutoff = $("#peptideQValueCutoff").val();
//			
//			var crosslinksTypeFilter = $("#crosslinksTypeFilter").is( ':checked' );
//			var looplinksTypeFilter = $("#looplinksTypeFilter").is( ':checked' );
//			var unlinkedTypeFilter = $("#unlinkedTypeFilter").is( ':checked' );
//			
//
//			var modMassFilter = [];
//
//			var $modMassFilter_jq = $(".modMassFilter_jq");
//			
//			$modMassFilter_jq.each(function () {
//				
//				var $this = $(this);
//				
//			    if ( $this.is( ':checked' ) ) {
//			    	
//			    	var value = $this.val();
//			    	
//			    	modMassFilter.push( value );
//			    }
//			    
//			});
//						
//
//			var formValues = {
//					psmQValueCutoff : psmQValueCutoff,
//					peptideQValueCutoff : peptideQValueCutoff,
//					
//					crosslinksTypeFilter : crosslinksTypeFilter,
//					looplinksTypeFilter : looplinksTypeFilter,
//					unlinkedTypeFilter : unlinkedTypeFilter,
//					modMassFilter: modMassFilter
//			};
//			return formValues;
//		}
