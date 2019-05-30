

//  lorikeetPageProcessing.js

//   /js/lorikeetPageProcessing.js

//  Processing to get lorikeet data and launch the lorikeet viewer in an overlay

//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";

// /////////////////////////////////////////

var LORIKEET_PAGE_PROCESSING_CONSTANTS = {
		
		serviceURL_lorikeetSpectrum_getData : "services/lorikeetSpectrum/getData",

		LORIKEET_VIEWER_HEIGHT_PARAM : 500,
		
		LORIKEET_VIEWER_WIDTH_PARAM : 500
	
};


var _LOOP_LINK_LINE_COLOR__LORIKEET_PAGE_PROCESSING = "#000000";

///  Function Parameter constants

var _MAKE_ARC_PATH_DIRECTION_UP__LORIKEET_PAGE_PROCESSING = "up";
var _MAKE_ARC_PATH_DIRECTION_DOWN__LORIKEET_PAGE_PROCESSING = "down";




$(document).ready(function()  { 

	initLorikeetViewer();

});

/////////////

function initLorikeetViewer() {

	try {

		attachLorikeetOverlayClickHandlers();

		var $openLorkeetLinks = $(".view_spectrum_open_spectrum_link_jq");

		if ( $openLorkeetLinks.length !== 0 ) {
			addOpenLorikeetViewerClickHandlers( $openLorkeetLinks );
		}

	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
}



/////////////

function addOpenLorikeetViewerClickHandlers( $openLorkeetLinks ) {


	$openLorkeetLinks.click( openViewLorikeetOverlay );
	
}



	/////////////////////////////////////////////////////////////////////////////////////////

	//////////   Lorikeet View Spectrum Overylay Handling

	/////////////////////////////////////////////////////////////////////////////




	////////////////////////////////////////////////////

	var openViewLorikeetOverlay = function( event, ui ) {

		var $this = $( this );

		//  Code to mark the last clicked psm with a different color.  The CSS class does not exist yet
		$(".psm-open-spectrum-link-clicked").removeClass("psm-open-spectrum-link-clicked");
		$this.addClass("psm-open-spectrum-link-clicked");

		
		
		
		var psmId = $this.attr("psmId");

//		var ajaxRequestData = { scanId: scanId, peptideId: peptideId, psmId: psmId };
		
		var ajaxRequestData = { psmId: psmId };
		
		$.ajax(
				{ url : LORIKEET_PAGE_PROCESSING_CONSTANTS.serviceURL_lorikeetSpectrum_getData,

					success :  function( data ) {

						try {
//							testStatusOnReturnedJSON( data );

							openViewLorikeetOverlayProcessData( data );
							
						} catch( e ) {
							reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
							throw e;
						}
					},
			        failure: function(errMsg) {
			        	handleAJAXFailure( errMsg );
			        },

					error: function(jqXHR, textStatus, errorThrown) {

//						alert("Error response from server getting Lorikeet data");
						
						handleAJAXError( jqXHR, textStatus, errorThrown );

					},
					data: ajaxRequestData,  //  The data sent as params on the URL
					dataType : "json"
				});

		event.preventDefault(); // prevent default for element
		
		event.stopPropagation(); // prevent click bubbling
	};


	///////////////////////////////

	var openViewLorikeetOverlayProcessData = function( data ) {

		
		var lorikeetOptions = data.data;
		
		//  Set Lorikeet options
		
		lorikeetOptions.peakDetect = false;
		
		
		//  Need to at least set lorikeetOptions.ms1scanLabel to something to get the "MS1 Scan:" to show up on the MS1 scan
//		lorikeetOptions.ms1scanLabel = "ms1scanLabel";  //  TODO  set this to something else
		lorikeetOptions.ms1scanLabel = " ";  //  TODO  set this to something else

		
		
		//  Add these items to the lorikeetOptions variable

		lorikeetOptions.height = LORIKEET_PAGE_PROCESSING_CONSTANTS.LORIKEET_VIEWER_HEIGHT_PARAM;
		lorikeetOptions.width = LORIKEET_PAGE_PROCESSING_CONSTANTS.LORIKEET_VIEWER_WIDTH_PARAM;

		if ( lorikeetOptions === undefined || lorikeetOptions === null ) {

			var msg = "Error retrieving data.  lorikeetOptions === undefined || lorikeetOptions === null";

//			handleGeneralServerError( { msg: msg  });
			
			alert( msg );
		}


		var $view_spectra_overlay_div = $("#view-spectra-overlay-div");


		//  Adjust the overlay positon to be within the viewport

		var scrollTopWindow = $(window).scrollTop();

		if ( scrollTopWindow > 0 ) {

			//  User has scrolled down

//			var overlayBackgroundDivTop = $("#lorikeet-modal-dialog-overlay-background").offset().top;
//
//			var overlayTop = scrollTopWindow - overlayBackgroundDivTop + 10;
			
			//  Changed since overlay-background is position fixed

			var overlayTop = scrollTopWindow + 10;

			$view_spectra_overlay_div.css( { top: overlayTop + "px" } );

		} else {

			$view_spectra_overlay_div.css( { top: "10px" } );
		}
		
		
		
		/////////////////////
		
		///   Open the lorikeet overlay
		
		openLorikeetOverlayBackground();

		$view_spectra_overlay_div.show();
		
		
		
		

		lorikeetOptions.sizeChangeCallbackFunction = function() {

			resizeLorikeetOverlayBackgroundToLorikeetSize();
		};


		var overlayZIndex = $view_spectra_overlay_div.css("z-index");

		//  overlayZIndex is a string so this is the equivalent to ( overlayZIndex * 10 ) + 2
		//   which achieves the desired affect of lorikeetOptions.tooltipZIndex > overlayZIndex

		lorikeetOptions.tooltipZIndex = overlayZIndex + 2; // set higher than overlay z index so the tooltip will display

		
//		if ( lorikeetPerPeptideDataList.length > 1 ) {
//
//			//  If more than one sequence, show these divs
//
//			$("#lorikeet-holder-sequence-list").show();
//			$("#lorikeet-holder-divider-div").show();
//		} else {
//			
//			//  If NOT more than one sequence, hide these divs
//
//			$("#lorikeet-holder-sequence-list").hide();
//			$("#lorikeet-holder-divider-div").hide();
//		}
		

		////////   Initialization  
		
		
		//  Initially hide all
		$("#lorikeet-cross-link-data").hide();
		$("#lorikeet-loop-link-data").hide();
		$("#lorikeet-dimer-data").hide();
		
		//  Remove both old lorikeet viewers
		
		var $lorikeet_holder_1_div = $("#lorikeet-holder-1-div");
		$lorikeet_holder_1_div.empty();

		var $lorikeet_holder_2_div = $("#lorikeet-holder-2-div");
		$lorikeet_holder_2_div.empty();

		//  Hide the divider between the Lorikeet viewers
		
		$("lorikeet-holder-divider-div").hide();


		
		
		
		
		if ( lorikeetOptions.loopLinkDataInputFormat ) {
			
			lorikeetOptions.sequence = lorikeetOptions.loopLinkDataInputFormat.peptideData.sequence;
			lorikeetOptions.label = lorikeetOptions.loopLinkDataInputFormat.peptideData.label;
			lorikeetOptions.variableMods = lorikeetOptions.loopLinkDataInputFormat.peptideData.variableMods;
			lorikeetOptions.ntermMod = lorikeetOptions.loopLinkDataInputFormat.peptideData.ntermMod;
			lorikeetOptions.ctermMod = lorikeetOptions.loopLinkDataInputFormat.peptideData.ctermMod;
			
//			loopLinkDataInputFormat: Object
//			linkerMass: 138.0681
//			loopLinkPos1: 6
//			loopLinkPos2: 9
//			peptideData: Object
//			sequence: "DAAIMKMAKEAGVEVVTENSHTLYDLDR"
//			variableMods: Array[0]
		
		
			
			$("#lorikeet-loop-link-data").show();
			
//			$("#lorikeet-loop-link-data-position-1").text( lorikeetOptions.loopLinkDataInputFormat.loopLinkPos1 );
//			$("#lorikeet-loop-link-data-position-2").text( lorikeetOptions.loopLinkDataInputFormat.loopLinkPos2 );
//			$("#lorikeet-loop-link-data-linker-mass").text( lorikeetOptions.loopLinkDataInputFormat.linkerMass );

			
//			<%-- 			
//			Looplinked peptide:   MPKDFPKPFPQK
//							        |   |
//					       			+---+
//			 --%>
//
//			<div><span id="lorikeet_looplink_sequence"></span></div>
//			<div ><span id="lorikeet_looplink_vertical_lines_space_before"></span>|
//					<span id="lorikeet_looplink_vertical_lines_space_inside"></span>|</div>
//			<div ><span id="lorikeet_looplink_dashes_space_before"></span>+
//					<span id="lorikeet_looplink_dashes_inside"></span>+</div>

			var linkPosition1 = lorikeetOptions.loopLinkDataInputFormat.loopLinkPos1;
			var linkPosition2 = lorikeetOptions.loopLinkDataInputFormat.loopLinkPos2;
			
			if (linkPosition1 > linkPosition2 ) {
				
				var tempPosition = linkPosition1;
				linkPosition1 = linkPosition2;
				linkPosition2 = tempPosition;
			}
			
			var $lorikeet_looplink_character_based_visual = $("#lorikeet_looplink_character_based_visual");
			var $lorikeet_looplink_svg_based_visual = $("#lorikeet_looplink_svg_based_visual");

			
			var svgSupportedSnapLoaded = false;
			
			if ( typeof Modernizr !== 'undefined' && Modernizr.svg && typeof Snap !== 'undefined' ) {
				
				svgSupportedSnapLoaded = true;
			}
			
			if ( svgSupportedSnapLoaded ) {
				
				//  SVG supported and Snap SVG library is loaded, create a SVG based visual
				
//					!!!!!!!!  This does work:
//			
//							Issue 1: the SVG image must be visible in order to get the BBox info.
//			
//								Move these 2 lines to above creating this SVG
//
//									openLorikeetOverlayBackground();
//									$("#view-spectra-overlay-div").show();
//			
//							Issue 2: The Bounding box (BBox) for a <tspan> is the Bounding box for the enclosing <text> element 
			
				$lorikeet_looplink_svg_based_visual.show();
				$lorikeet_looplink_character_based_visual.hide();

				
				//  split sequence into <before><pos1><between><pos2><after>

				var sequence = lorikeetOptions.loopLinkDataInputFormat.peptideData.sequence;
				
				var pos_1_ZeroBased = linkPosition1 - 1;
				var pos_2_ZeroBased = linkPosition2 - 1;
				
				

				if ( pos_1_ZeroBased === pos_2_ZeroBased ) {
					
					//  Special case of looplink to itself

					var seqBefore = sequence.substring( 0, pos_1_ZeroBased );
					var seqPos1 = sequence.substring( pos_1_ZeroBased, pos_1_ZeroBased + 1 );
					var seqAfter = sequence.substring( pos_2_ZeroBased + 1 );




					$lorikeet_looplink_svg_based_visual.empty();

					var $svg_element = $('<svg width="5" height="5">').appendTo( $lorikeet_looplink_svg_based_visual );

					var svgElement = $svg_element[0];

					var svgRootSnapSVGObject = Snap( svgElement );  // pass in HTML element

					var outerGroup = svgRootSnapSVGObject.g();

					var textX = 1;
					var textY = 8;

					var sequenceTextSnapSVGObject = svgRootSnapSVGObject.text( textX, textY, [ seqBefore, seqPos1, seqAfter ] );

					sequenceTextSnapSVGObject.attr( { "dy": "0.35em"} );

//					var sequenceTextSnapSVGObject_bbox_Initial = sequenceTextSnapSVGObject.getBBox();


					var sequenceTextSnapSVGObject_bbox = sequenceTextSnapSVGObject.getBBox();

					var sequenceText_X = sequenceTextSnapSVGObject_bbox.x;
					var sequenceText_Y = sequenceTextSnapSVGObject_bbox.y;
					var sequenceText_Height = sequenceTextSnapSVGObject_bbox.height;


					var lorikeet_loop_link_seqBefore_SnapSVGObject = sequenceTextSnapSVGObject.select("tspan:nth-child(1)"); //  !!! This selector must match the position in the array above
//					var lorikeet_loop_link_seqAfter_SnapSVGObject = sequenceTextSnapSVGObject.select("tspan:nth-child(3)"); //  !!! This selector must match the position in the array above

					lorikeet_loop_link_seqBefore_SnapSVGObject.attr( { "dy": "0.35em"} );
//					lorikeet_loop_link_seqAfter_SnapSVGObject.attr( { "dy": "0.35em"} );

					var lorikeet_loop_link_seqBefore_PlainSVGObject = lorikeet_loop_link_seqBefore_SnapSVGObject.node;
//					var lorikeet_loop_link_seqAfter_PlainSVGObject = lorikeet_loop_link_seqAfter_SnapSVGObject.node;

//					var lorikeet_loop_link_seqBefore_textLength = lorikeet_loop_link_seqBefore_PlainSVGObject.textLength.baseVal.value;
//					var lorikeet_loop_link_seqBetween_textLength = lorikeet_loop_link_seqBetween_PlainSVGObject.textLength.baseVal.value;
//					var lorikeet_loop_link_seqAfter_textLength = lorikeet_loop_link_seqAfter_PlainSVGObject.textLength.baseVal.value;

					var lorikeet_loop_link_seqBefore_textLength = lorikeet_loop_link_seqBefore_PlainSVGObject.getComputedTextLength();
//					var lorikeet_loop_link_seqAfter_textLength = lorikeet_loop_link_seqAfter_PlainSVGObject.getComputedTextLength();

//					http://www.w3.org/TR/SVG/text.html#__svg__SVGTextContentElement__getComputedTextLength
//					Note: getComputedTextLength(); get's the width of the actual characters, not the bounding box of the tspans. 
//					The font characters can be slightly larger than the tspan bounding box.



					var lorikeet_loop_link_pos_1_SnapSVGObject = sequenceTextSnapSVGObject.select("tspan:nth-child(2)"); //  !!! This selector must match the position in the array above
					lorikeet_loop_link_pos_1_SnapSVGObject.addClass("lorikeet_loop_link_pos_1_jq");

//					lorikeet_loop_link_pos_1_SnapSVGObject.attr( { "dy": "0.35em"} );


					outerGroup.add( sequenceTextSnapSVGObject );


//					var lorikeet_loop_link_pos_1_bbox = lorikeet_loop_link_pos_1_SnapSVGObject.getBBox(); //  is bbox of entire text element


					var lorikeet_loop_link_pos_1_PlainSVGObject = lorikeet_loop_link_pos_1_SnapSVGObject.node;

//					var lorikeet_loop_link_pos_1_textLength = lorikeet_loop_link_pos_1_PlainSVGObject.textLength.baseVal.value;

					var lorikeet_loop_link_pos_1_textLength = lorikeet_loop_link_pos_1_PlainSVGObject.getComputedTextLength();

					var pos1X = sequenceText_X + lorikeet_loop_link_seqBefore_textLength;
					var pos1Width = lorikeet_loop_link_pos_1_textLength;
					var pos1CenterX = pos1X + ( pos1Width / 2 );

					var x1 = pos1CenterX;
					var y1 = sequenceText_Y + sequenceText_Height;
					
					var height = 20;
					

					var lineSnapSVGObject = svgRootSnapSVGObject.line( x1, y1, x1, y1 + height );
					lineSnapSVGObject.attr({
						stroke: _LOOP_LINK_LINE_COLOR__LORIKEET_PAGE_PROCESSING,
						strokeWidth: 1,
						fill: "none"
					});

//					var arc = svgRootSnapSVGObject.path( makeArcPathLorikeetPageProcessing( _MAKE_ARC_PATH_DIRECTION_DOWN__LORIKEET_PAGE_PROCESSING, x1, y1, x1, y1 ) );
//					arc.attr({
//						stroke: _LOOP_LINK_LINE_COLOR__LORIKEET_PAGE_PROCESSING,
//						strokeWidth:1,
//						//"stroke-dasharray":"1,1",
//						fill: "none"
//					});
//
//
//					var arc_bbox = arc.getBBox();
//
//					var arc_Y1 = arc_bbox.y1;

					var sequenceTextSnapSVGObject_bbox_X2 = sequenceTextSnapSVGObject_bbox.x2;

					var imageWidth = Math.ceil( sequenceTextSnapSVGObject_bbox_X2 + 10 );
					var imageHeight = Math.ceil( height + 10 );

					svgRootSnapSVGObject.attr( { width: imageWidth } );
					svgRootSnapSVGObject.attr( { height: imageHeight } );

				//  END:  "Special case of looplink to itself"
					
				} else {
					
					//  Standard case of a looplink between two positions

					var seqBefore = sequence.substring( 0, pos_1_ZeroBased );
					var seqPos1 = sequence.substring( pos_1_ZeroBased, pos_1_ZeroBased + 1 );
					var seqBetween = sequence.substring( pos_1_ZeroBased + 1, pos_2_ZeroBased );
					var seqPos2 = sequence.substring( pos_2_ZeroBased, pos_2_ZeroBased + 1 );
					var seqAfter = sequence.substring( pos_2_ZeroBased + 1 );




					$lorikeet_looplink_svg_based_visual.empty();

					var $svg_element = $('<svg width="5" height="5">').appendTo( $lorikeet_looplink_svg_based_visual );

					var svgElement = $svg_element[0];

					var svgRootSnapSVGObject = Snap( svgElement );  // pass in HTML element

					var outerGroup = svgRootSnapSVGObject.g();

					var textX = 1;
					var textY = 8;

					var sequenceTextSnapSVGObject = svgRootSnapSVGObject.text( textX, textY, [ seqBefore, seqPos1, seqBetween, seqPos2, seqAfter ] );

					sequenceTextSnapSVGObject.attr( { "dy": "0.35em"} );

//					var sequenceTextSnapSVGObject_bbox_Initial = sequenceTextSnapSVGObject.getBBox();


					var sequenceTextSnapSVGObject_bbox = sequenceTextSnapSVGObject.getBBox();

					var sequenceText_X = sequenceTextSnapSVGObject_bbox.x;
					var sequenceText_Y = sequenceTextSnapSVGObject_bbox.y;
					var sequenceText_Height = sequenceTextSnapSVGObject_bbox.height;


					var lorikeet_loop_link_seqBefore_SnapSVGObject = sequenceTextSnapSVGObject.select("tspan:nth-child(1)"); //  !!! This selector must match the position in the array above
					var lorikeet_loop_link_seqBetween_SnapSVGObject = sequenceTextSnapSVGObject.select("tspan:nth-child(3)"); //  !!! This selector must match the position in the array above
//					var lorikeet_loop_link_seqAfter_SnapSVGObject = sequenceTextSnapSVGObject.select("tspan:nth-child(5)"); //  !!! This selector must match the position in the array above

					lorikeet_loop_link_seqBefore_SnapSVGObject.attr( { "dy": "0.35em"} );
//					lorikeet_loop_link_seqBetween_SnapSVGObject.attr( { "dy": "0.35em"} );
//					lorikeet_loop_link_seqAfter_SnapSVGObject.attr( { "dy": "0.35em"} );

					var lorikeet_loop_link_seqBefore_PlainSVGObject = lorikeet_loop_link_seqBefore_SnapSVGObject.node;
					var lorikeet_loop_link_seqBetween_PlainSVGObject = lorikeet_loop_link_seqBetween_SnapSVGObject.node;
//					var lorikeet_loop_link_seqAfter_PlainSVGObject = lorikeet_loop_link_seqAfter_SnapSVGObject.node;

//					var lorikeet_loop_link_seqBefore_textLength = lorikeet_loop_link_seqBefore_PlainSVGObject.textLength.baseVal.value;
//					var lorikeet_loop_link_seqBetween_textLength = lorikeet_loop_link_seqBetween_PlainSVGObject.textLength.baseVal.value;
//					var lorikeet_loop_link_seqAfter_textLength = lorikeet_loop_link_seqAfter_PlainSVGObject.textLength.baseVal.value;

					var lorikeet_loop_link_seqBefore_textLength = lorikeet_loop_link_seqBefore_PlainSVGObject.getComputedTextLength();
					var lorikeet_loop_link_seqBetween_textLength = lorikeet_loop_link_seqBetween_PlainSVGObject.getComputedTextLength();
//					var lorikeet_loop_link_seqAfter_textLength = lorikeet_loop_link_seqAfter_PlainSVGObject.getComputedTextLength();

//					http://www.w3.org/TR/SVG/text.html#__svg__SVGTextContentElement__getComputedTextLength
//					Note: getComputedTextLength(); get's the width of the actual characters, not the bounding box of the tspans. 
//					The font characters can be slightly larger than the tspan bounding box.



					var lorikeet_loop_link_pos_1_SnapSVGObject = sequenceTextSnapSVGObject.select("tspan:nth-child(2)"); //  !!! This selector must match the position in the array above
					lorikeet_loop_link_pos_1_SnapSVGObject.addClass("lorikeet_loop_link_pos_1_jq");

//					lorikeet_loop_link_pos_1_SnapSVGObject.attr( { "dy": "0.35em"} );

					var lorikeet_loop_link_pos_2_SnapSVGObject = sequenceTextSnapSVGObject.select("tspan:nth-child(4)"); //  !!! This selector must match the position in the array above
					lorikeet_loop_link_pos_2_SnapSVGObject.addClass("lorikeet_loop_link_pos_2_jq");

//					lorikeet_loop_link_pos_2_SnapSVGObject.attr( { "dy": "0.35em"} );

					outerGroup.add( sequenceTextSnapSVGObject );


//					var lorikeet_loop_link_pos_1_bbox = lorikeet_loop_link_pos_1_SnapSVGObject.getBBox(); //  is bbox of entire text element
//					var lorikeet_loop_link_pos_2_bbox = lorikeet_loop_link_pos_2_SnapSVGObject.getBBox(); //  is bbox of entire text element


					var lorikeet_loop_link_pos_1_PlainSVGObject = lorikeet_loop_link_pos_1_SnapSVGObject.node;
					var lorikeet_loop_link_pos_2_PlainSVGObject = lorikeet_loop_link_pos_2_SnapSVGObject.node;

//					var lorikeet_loop_link_pos_1_textLength = lorikeet_loop_link_pos_1_PlainSVGObject.textLength.baseVal.value;
//					var lorikeet_loop_link_pos_2_textLength = lorikeet_loop_link_pos_2_PlainSVGObject.textLength.baseVal.value;

					var lorikeet_loop_link_pos_1_textLength = lorikeet_loop_link_pos_1_PlainSVGObject.getComputedTextLength();
					var lorikeet_loop_link_pos_2_textLength = lorikeet_loop_link_pos_2_PlainSVGObject.getComputedTextLength();

					var pos1X = sequenceText_X + lorikeet_loop_link_seqBefore_textLength;
					var pos1Width = lorikeet_loop_link_pos_1_textLength;
					var pos1CenterX = pos1X + ( pos1Width / 2 );


					var pos2X = sequenceText_X + lorikeet_loop_link_seqBefore_textLength + lorikeet_loop_link_pos_1_textLength + lorikeet_loop_link_seqBetween_textLength;
					var pos2Width = lorikeet_loop_link_pos_2_textLength;
					var pos2CenterX = pos2X + ( pos2Width / 2 );


					var x1 = pos1CenterX;
					var y1 = sequenceText_Y + sequenceText_Height;
					var x2 = pos2CenterX;
					var y2 = y1;

					var arc = svgRootSnapSVGObject.path( makeArcPathLorikeetPageProcessing( _MAKE_ARC_PATH_DIRECTION_DOWN__LORIKEET_PAGE_PROCESSING, x1, y1, x2, y2 ) );
					arc.attr({
						stroke: _LOOP_LINK_LINE_COLOR__LORIKEET_PAGE_PROCESSING,
						strokeWidth:1,
						//"stroke-dasharray":"1,1",
						fill: "none"
					});


					var arc_bbox = arc.getBBox();

					var arc_Y2 = arc_bbox.y2;

					var sequenceTextSnapSVGObject_bbox_X2 = sequenceTextSnapSVGObject_bbox.x2;

					var imageWidth = Math.ceil( sequenceTextSnapSVGObject_bbox_X2 + 10 );
					var imageHeight = Math.ceil( arc_Y2 + 10 );

					svgRootSnapSVGObject.attr( { width: imageWidth } );
					svgRootSnapSVGObject.attr( { height: imageHeight } );
					
					//  END:  ELSE of "Special case of looplink to itself"

				}

				//  END:  SVG supported and Snap SVG library is loaded, create a SVG based visual
				
			} else {
				
				//  Create Text representation of Looplink

				$lorikeet_looplink_character_based_visual.show();
				$lorikeet_looplink_svg_based_visual.hide();


				if ( linkPosition1 === linkPosition2 ) {
					
					//  Special case of looplink to itself

					var spaceBeforeFirstLink_string = "";

					for ( var counter = 1; counter < linkPosition1; counter++ ) {

						spaceBeforeFirstLink_string += "&nbsp;";
					}


					$("#lorikeet_looplink_sequence").text( lorikeetOptions.loopLinkDataInputFormat.peptideData.sequence );

					$("#lorikeet_looplink_vertical_lines_space_before").html( spaceBeforeFirstLink_string );
					$("#lorikeet_looplink_dashes_space_before").html( spaceBeforeFirstLink_string );

					$("#lorikeet_looplink_second_vert_bar").hide();
					$("#lorikeet_looplink_second_plus").hide();
					
					//  END:  "Special case of looplink to itself"
					
				} else {
		
					//  Standard case of a looplink between two positions

					var spaceBeforeFirstLink_string = "";
					var spaceInsideLink_string = "";
					var dashesInsideLink_string = "";

					for ( var counter = 1; counter < linkPosition1; counter++ ) {

						spaceBeforeFirstLink_string += "&nbsp;";
					}

					for ( var counter = linkPosition1 + 1; counter < linkPosition2; counter++ ) {

						spaceInsideLink_string += "&nbsp;";
						dashesInsideLink_string += "-";
					}

					$("#lorikeet_looplink_sequence").text( lorikeetOptions.loopLinkDataInputFormat.peptideData.sequence );

					$("#lorikeet_looplink_vertical_lines_space_before").html( spaceBeforeFirstLink_string );
					$("#lorikeet_looplink_dashes_space_before").html( spaceBeforeFirstLink_string );

					$("#lorikeet_looplink_vertical_lines_space_inside").html( spaceInsideLink_string );
					$("#lorikeet_looplink_dashes_inside").html( dashesInsideLink_string );

					//  END: Standard case of a looplink between two positions
					
				}
				
				//  END:  Create Text representation of Looplink
				
			} //  else of if ( Modernizr && Modernizr.svg && Snap ) {
				
		}
		
		if ( lorikeetOptions.crossLinkDataInputFormat ){ 
			
			//  Process cross link data
			
			$("#lorikeet-holder-divider-div").show();
			$("#lorikeet-holder-2-div").show();

			$("#lorikeet-cross-link-data").show();
						
//			Crosslinked peptides:   VKNTTTLQQHLIDGR
//									   |
//									  MPKDFPPFPQK
			
			$("#lorikeet_crosslink_sequence_1").text( lorikeetOptions.crossLinkDataInputFormat.peptideData1.sequence );
			$("#lorikeet_crosslink_sequence_2").text( lorikeetOptions.crossLinkDataInputFormat.peptideData2.sequence );
			
			var get_crossLinkData_CleavedMass_singleRadioButtonHTML = function( mass, peptideNumber, checked ) {
				
				var checkedHTML = "";
				if ( checked ) {
					checkedHTML = " checked ";
				}
				
				var radioElementName = "lorikeet_crosslink_cleaved_mass_select_" + peptideNumber;
			
				var singleRadioButtonHTML = '<label >' + 

					'<input type="radio" name="' + radioElementName + '" ' +
					' class=" lorikeet_crosslink_cleaved_mass_select_jq " ' +
					' value="' + mass + '" ' + checkedHTML + '>' +
					lorikeetOptions.crossLinkDataInputFormat.linkerAbbr +
					'-' +
					mass + 
					'</label>';
					
				return singleRadioButtonHTML;
			}
			
			var cleavedLinkerMassList = lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMassList;
			if ( cleavedLinkerMassList && cleavedLinkerMassList.length !== 0 ) { 
				
				var $lorikeet_crosslink_cleaved_mass_selection_block_1 = $("#lorikeet_crosslink_cleaved_mass_selection_block_1");
				
				var $lorikeet_crosslink_cleaved_mass_selection_block_2 = $("#lorikeet_crosslink_cleaved_mass_selection_block_2");
				
				$lorikeet_crosslink_cleaved_mass_selection_block_1.empty();
				$lorikeet_crosslink_cleaved_mass_selection_block_2.empty();
				
				cleavedLinkerMassList.forEach(function( cleavedLinkerMass, index, array) {
					
					//  For Peptide 1
					
					var checked = false;
					if ( index === 0 ) {
						checked = true; // Set first entry as checked initially
					}
					var singleRadioButtonForHTML = get_crossLinkData_CleavedMass_singleRadioButtonHTML( cleavedLinkerMass, 1, checked );
					
					var $singleRadioButtonHTML = $( singleRadioButtonForHTML );
					
					$lorikeet_crosslink_cleaved_mass_selection_block_1.append( $singleRadioButtonHTML );
					
					$singleRadioButtonHTML.change(function(eventObject) {
						var selectedValue = this.value;
						// Update Lorikeet for value
						var cleavedLinkerMassLocal = cleavedLinkerMass;
						lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMass = cleavedLinkerMassLocal;
						
						//  Re-create Lorikeet
						createCrossLinkLorikeet_1( lorikeetOptions );
					})
				}, this );

				cleavedLinkerMassList.forEach(function( cleavedLinkerMass, index, array) {
					
					//  For Peptide 2
					
					var checked = false;
					if ( cleavedLinkerMassList.length === 1 || index === 1 ) {
						checked = true; // If 2 entries, set second entry as checked initially, otherwise set first entry as checked initially
					}
					var singleRadioButtonForHTML = get_crossLinkData_CleavedMass_singleRadioButtonHTML( cleavedLinkerMass, 2, checked );
					
					var $singleRadioButtonHTML = $( singleRadioButtonForHTML );
					
					$lorikeet_crosslink_cleaved_mass_selection_block_2.append( $singleRadioButtonHTML );

					$singleRadioButtonHTML.change(function(eventObject) {
						var selectedValue = this.value;
						// Update Lorikeet for value
						var cleavedLinkerMassLocal = cleavedLinkerMass;
						lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMass = cleavedLinkerMassLocal;
						
						//  Re-create Lorikeet
						createCrossLinkLorikeet_2( lorikeetOptions );
					})
					
				}, this );
				
			}


			var spaceBeforeSequence1 = 0;
			var spaceBeforeSequence2 = 0;
			var spaceBeforeLinker = 0;
			
			var crossLinkPos1 = lorikeetOptions.crossLinkDataInputFormat.crossLinkPos1;
			var crossLinkPos2 = lorikeetOptions.crossLinkDataInputFormat.crossLinkPos2;
			
			if ( crossLinkPos1 > crossLinkPos2 ) {
				
				spaceBeforeSequence2 = crossLinkPos1 - crossLinkPos2;
				spaceBeforeLinker = crossLinkPos1 - 1;
			} else {
				
				spaceBeforeSequence1 = crossLinkPos2 - crossLinkPos1;
				spaceBeforeLinker = crossLinkPos2 - 1;
			}
			
			var spaceBeforeSequence1_string = "";
			var spaceBeforeSequence2_string = "";
			var spaceBeforeLinker_string = "";
			
			var addSpace = function( string, spaceCount ) {
				
				for ( var counter = 0; counter < spaceCount; counter++ ) {
					
					string += "&nbsp;";
				}
				return string;
			};
			
			spaceBeforeSequence1_string = addSpace( spaceBeforeSequence1_string, spaceBeforeSequence1 );
			spaceBeforeSequence2_string = addSpace( spaceBeforeSequence2_string, spaceBeforeSequence2 );
			spaceBeforeLinker_string = addSpace( spaceBeforeLinker_string, spaceBeforeLinker );

			$("#lorikeet_crosslink_sequence_1_space_before").html( spaceBeforeSequence1_string );
			$("#lorikeet_crosslink_sequence_2_space_before").html( spaceBeforeSequence2_string );
			$("#lorikeet_crosslink_linker_space_before").html( spaceBeforeLinker_string );

			//  Create Lorikeet for each Peptide in Crosslink
			
			if ( lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMassList && lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMassList.length !== 0 ) {
				
				lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMass = lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMassList[ 0 ];
			}
			
			createCrossLinkLorikeet_1( lorikeetOptions );

			if ( lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMassList && lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMassList.length !== 0 ) {
				
				if ( lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMassList.length === 1 ) {
					lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMass = lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMassList[ 0 ];
				} else {
					lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMass = lorikeetOptions.crossLinkDataInputFormat.cleavedLinkerMassList[ 1 ];
				}
			}
			
			createCrossLinkLorikeet_2( lorikeetOptions );
			
		} else if ( lorikeetOptions.dimerDataInputFormat ) {
			
			$("#lorikeet_dimer_sequence_1").text( lorikeetOptions.dimerDataInputFormat.peptideData1.sequence );
			$("#lorikeet_dimer_sequence_2").text( lorikeetOptions.dimerDataInputFormat.peptideData2.sequence );

			$("#lorikeet-dimer-data").show();
			
			$("#lorikeet-holder-divider-div").show();
			$("#lorikeet-holder-2-div").show();
			

			//  TODO  MUST REMOVE:   Hard coded to 1.    Need to fix Lorikeet so don't need these statements
			
			lorikeetOptions.sequence = lorikeetOptions.dimerDataInputFormat.peptideData1.sequence;
			lorikeetOptions.variableMods = lorikeetOptions.dimerDataInputFormat.peptideData1.variableMods;
			lorikeetOptions.ntermMod = lorikeetOptions.dimerDataInputFormat.peptideData1.ntermMod;
			lorikeetOptions.ctermMod = lorikeetOptions.dimerDataInputFormat.peptideData1.ctermMod;
			
			
			createSingleDimerLorikeet( lorikeetOptions, 1 /* indexStartAtOne */ );
			
			//  TODO  MUST REMOVE:   Hard coded to 2.    Need to fix Lorikeet so don't need these statements
			
			lorikeetOptions.sequence = lorikeetOptions.dimerDataInputFormat.peptideData2.sequence;
			lorikeetOptions.variableMods = lorikeetOptions.dimerDataInputFormat.peptideData2.variableMods;
			lorikeetOptions.ntermMod = lorikeetOptions.dimerDataInputFormat.peptideData2.ntermMod;
			lorikeetOptions.ctermMod = lorikeetOptions.dimerDataInputFormat.peptideData2.ctermMod;
			
			createSingleDimerLorikeet( lorikeetOptions, 2 /* indexStartAtOne */ );

		} else {
			

			//  process loop link and unlinked data

			//  Get div to put lorikeet vieweer in

			var indexStartAtOne = 1;

			$("#lorikeet-holder-sequence-" + indexStartAtOne).text( lorikeetOptions.sequence );

			var $lorikeet_holder_div = $("#lorikeet-holder-" + indexStartAtOne + "-div");

			$lorikeet_holder_div.empty();

			//  add div inside it and lorikeet viewer will go in that

			var $lorikeetDiv = $("<div id='lorikeet-in-overlay-" + indexStartAtOne + "'></div>").appendTo( $lorikeet_holder_div );

			//  Create the lorikeet viewer

			$lorikeetDiv.specview(lorikeetOptions);
		}

		
//		openLorikeetOverlayBackground();
//
//		$("#view-spectra-overlay-div").show();
		
		resizeLorikeetOverlayBackgroundToLorikeetSize();

	};
	


	////////////////////////////////

	var createCrossLinkLorikeet_1 = function( lorikeetOptions ) {
		
		//  TODO  MUST REMOVE:   Hard coded to 1.    Need to fix Lorikeet so don't need these statements
		
		lorikeetOptions.sequence = lorikeetOptions.crossLinkDataInputFormat.peptideData1.sequence;
		lorikeetOptions.variableMods = lorikeetOptions.crossLinkDataInputFormat.peptideData1.variableMods;
		lorikeetOptions.ntermMod = lorikeetOptions.crossLinkDataInputFormat.peptideData1.ntermMod;
		lorikeetOptions.ctermMod = lorikeetOptions.crossLinkDataInputFormat.peptideData1.ctermMod;
		lorikeetOptions.label = lorikeetOptions.crossLinkDataInputFormat.peptideData1.label;
		
		createSingleCrossLinkLorikeet( lorikeetOptions, 1 /* indexStartAtOne */ );
	}

	////////////////////////////////

	var createCrossLinkLorikeet_2 = function( lorikeetOptions ) {
		
		//  TODO  MUST REMOVE:   Hard coded to 1.    Need to fix Lorikeet so don't need these statements
		
		lorikeetOptions.sequence = lorikeetOptions.crossLinkDataInputFormat.peptideData2.sequence;
		lorikeetOptions.variableMods = lorikeetOptions.crossLinkDataInputFormat.peptideData2.variableMods;
		lorikeetOptions.ntermMod = lorikeetOptions.crossLinkDataInputFormat.peptideData2.ntermMod;
		lorikeetOptions.ctermMod = lorikeetOptions.crossLinkDataInputFormat.peptideData2.ctermMod;
		lorikeetOptions.label = lorikeetOptions.crossLinkDataInputFormat.peptideData2.label;

		createSingleCrossLinkLorikeet( lorikeetOptions, 2 /* indexStartAtOne */ );
	}

	
	////////////////////////////////

	var createSingleCrossLinkLorikeet = function( lorikeetOptions, indexStartAtOne ) {


		//  Add these items to the lorikeetOptions variable
		
		lorikeetOptions.crossLinkDataInputFormat.currentPeptideNumber = indexStartAtOne;
		
		//  Get div to put lorikeet viewer in
		
		var $lorikeet_holder_div = $("#lorikeet-holder-" + indexStartAtOne + "-div");

		$lorikeet_holder_div.empty();

		//  add div inside it and lorikeet viewer will go in that

		var $lorikeetDiv = $("<div id='lorikeet-in-overlay-" + indexStartAtOne + "'></div>").appendTo( $lorikeet_holder_div );

		//  Create the lorikeet viewer

		$lorikeetDiv.specview(lorikeetOptions);
	};
	
	
	

	////////////////////////////////

	var createSingleDimerLorikeet = function( lorikeetOptions, indexStartAtOne ) {

		//  Get div to put lorikeet viewer in
		
		var $lorikeet_holder_div = $("#lorikeet-holder-" + indexStartAtOne + "-div");

		$lorikeet_holder_div.empty();

		//  add div inside it and lorikeet viewer will go in that

		var $lorikeetDiv = $("<div id='lorikeet-in-overlay-" + indexStartAtOne + "'></div>").appendTo( $lorikeet_holder_div );

		//  Create the lorikeet viewer

		$lorikeetDiv.specview(lorikeetOptions);
	};


	////////////////////////////////

	var resizeLorikeetOverlayBackgroundToLorikeetSize = function() {



		//  Adjust width of overlay as necessary


		var $lorikeetOuterTable = $("#lorikeet-holder-1-div table.lorikeet-outer-table");

		if ( $lorikeetOuterTable.length === 0 ) {

			//  did not find the element on the page

			return;  ///  EXIT Function
		}


		var lorikeetOuterTableOuterWidth = $lorikeetOuterTable.outerWidth();
		

		var $lorikeetOuterTable2 = $("#lorikeet-holder-2-div table.lorikeet-outer-table");

		if ( $lorikeetOuterTable.length !== 0 ) {

			//  found the element on the page

			var lorikeetOuterTableOuterWidth_2 = $lorikeetOuterTable2.outerWidth();
			
			if ( lorikeetOuterTableOuterWidth_2 > lorikeetOuterTableOuterWidth ) {
				
				lorikeetOuterTableOuterWidth = lorikeetOuterTableOuterWidth_2;
			}
		}



		//  Fix the width of the overlay to match the width of the lorikeet viewer

		var $view_spectra_overlay_div = $("#view-spectra-overlay-div");

		//  The width of the overall overlay
//		var view_spectra_overlay_div_outerWidth = $view_spectra_overlay_div.outerWidth();

		var $view_spectra_overlay_body = $("#view-spectra-overlay-body");

		var paddingLeft = parseInt( $view_spectra_overlay_body.css("padding-left"), 10);
		var paddingRight = parseInt( $view_spectra_overlay_body.css("padding-right"), 10);

		var newOverlayDivWidth = lorikeetOuterTableOuterWidth + paddingLeft + paddingRight + 10;

		$view_spectra_overlay_div.width( newOverlayDivWidth );

//		var readOverlayDivWidth = $view_spectra_overlay_div.width( );


//		var z = 0;


	};

	
	/////////////////////////////////

	function makeArcPathLorikeetPageProcessing( direction, startx, starty, endx, endy ) {
		
		var height = 20;

		var x1 = startx + ( endx - startx ) / 4 ;
		var x2 = startx + (endx - startx  ) / 2 ;
		var x3 = startx + ( ( endx - startx  ) * 3 / 4 );

		var y1, y2, y3;

		if ( direction === _MAKE_ARC_PATH_DIRECTION_UP__LORIKEET_PAGE_PROCESSING ) {
			
			y1 = starty - ( height * 3 / 4 );
			y2 = starty - height;
			y3 = y1;
			
		} else if ( direction === _MAKE_ARC_PATH_DIRECTION_DOWN__LORIKEET_PAGE_PROCESSING ) {
			
			y1 = starty + ( height * 3 / 4 );
			y2 = starty + height;
			y3 = y1;
		
		} else {
			
			throw Error( "direction passed to makeArcPath(...) is invalid.  direction: '" + direction + "'." );
		}

		var path = "M" + startx + " " + starty;

		path += "R" + x1 + " " + y1;
		path += " " + x2 + " " + y2;
		path += " " + x3 + " " + y3;
		path += " " + endx + " " + endy;				

		return path;
	}




	///////////////////////////////////////////////////////////////////////////


	///  Overlay general processing

	var openLorikeetOverlayBackground = function (  ) {

		$("#lorikeet-modal-dialog-overlay-background").show();

		//  position: fixed; height: 100% covers the whole document
//		var docHeight = $(document).height();
//
//		$("#lorikeet-modal-dialog-overlay-background").css({ height: docHeight });
	};

	///  Overlay general processing

	var closeLorikeetOverlay = function (  ) {

		$("#lorikeet-modal-dialog-overlay-background").hide();
		$(".view-spectra-overlay-div").hide();

		$("#lorikeet-view-spectra-overlay").empty();
	};

	///////////////////////////////////////////////////////////////////////////

	///  Attach Overlay Click handlers


	var attachLorikeetOverlayClickHandlers = function (  ) {

		var $view_spectra_overlay_X_for_exit_overlay = $(".view-spectra-overlay-X-for-exit-overlay");
		
		$view_spectra_overlay_X_for_exit_overlay.click( function( eventObject ) {
			try {
				closeLorikeetOverlay();
				
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );

		$("#lorikeet-modal-dialog-overlay-background").click( function( eventObject ) {
			try {
				closeLorikeetOverlay();
				
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );

//		$(".view-spectra-overlay-div").click( function( eventObject ) {
//
//			closeLorikeetOverlay();
//		} );

		$(".error-message-ok-button").click( function( eventObject ) {
			try {
				closeLorikeetOverlay();
				
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		} );

		
		
		
//		$("#view-spectra-overlay-div").click( function( eventObject ) {
//
//			return false;
//		} );

	};
	

	export { addOpenLorikeetViewerClickHandlers, closeLorikeetOverlay }

