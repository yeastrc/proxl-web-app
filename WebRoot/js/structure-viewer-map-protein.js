"use strict";

//initialize the pdb upload overlay
$(document).ready(function()  { 
	attachPDBMapProteinOverlayClickHandlers();
});


// opens the overlay
var openPDBMapProteinOverlay = function (  ) {

	$("#pdb-map-protein-modal-dialog-overlay-background").show();
	$(".pdb-map-protein-overlay-div").show();
	
	//  scroll the window to the top left
	var $window = $(window);
	$window.scrollTop( 0 );
	$window.scrollLeft( 0 );
};

// close the overlay
var closePDBMapProteinOverlay = function (  ) {
	
	$("#pdb-map-protein-modal-dialog-overlay-background").hide();
	$(".pdb-map-protein-overlay-div").hide();

	// blow away the structure being displayed
	$("#pdb-map-protein-overlay-structure").empty();
	
};


//opens the overlay
var openPDBShowAlignmentOverlay = function (  ) {

	$("#pdb-show-alignment-modal-dialog-overlay-background").show();
	$(".pdb-show-alignment-overlay-div").show();
	
	//  scroll the window to the top left
	var $window = $(window);
	$window.scrollTop( 0 );
	$window.scrollLeft( 0 );
};

// close the overlay
var closePDBShowAlignmentOverlay = function (  ) {
	
	$("#pdb-show-alignment-modal-dialog-overlay-background").hide();
	$(".pdb-show-alignment-overlay-div").hide();
	
};


//attach handlers for the upload overlay, namely ensure clicking "X" closes the overlay.
var attachPDBMapProteinOverlayClickHandlers = function (  ) {
	var $pdb_map_protein_overlay_X_for_exit_overlay = $(".pdb-map-protein-overlay-X-for-exit-overlay");
	
	$pdb_map_protein_overlay_X_for_exit_overlay.click( function( eventObject ) {
		closePDBMapProteinOverlay();
	} );
	
	
	var $pdb_show_alignment_overlay_X_for_exit_overlay = $(".pdb-show-alignment-overlay-X-for-exit-overlay");
	
	$pdb_show_alignment_overlay_X_for_exit_overlay.click( function( eventObject ) {
		closePDBShowAlignmentOverlay();
	} );
	
};



var mapProtein = function ( chainId ) {
	openPDBMapProteinOverlay();
	
	showStructureInOverlay( chainId );
	showProteinSelectInOverlay( chainId );
};


var showProteinSelectInOverlay = function( chainId ) {
	
	var $contentDiv = $("#pdb-map-protein-overlay-content");
	$contentDiv.empty();
	
	var content = $("#pdb-map-protein-overlay-protein-step-one" ).html();
	
	// get all nrseq ids already mapped to this chain
	var alignments = getAllAlignmentsForChain( chainId );
	var alignedProteins = new Array();
	if( alignments && alignments.length > 0 ) {
		for( var i = 0; i < alignments.length; i++ ) {
		
			alignedProteins.push( alignments[ i ][ 'nrseqId' ] );			
		}
	}	
	
	content = content.replace( "#CHAINID#", chainId );
	$contentDiv.html( content );
	
	var $selectDiv = $("#pdb-map-protein-overlay-protein-select-div" );
	$selectDiv.empty();
	
	// build select box for proteins
	content = "<select id=\"pdb-map-protein-overlay-protein-select\">";
	content += "<option value=\"0\">Select protein:</option>\n";
	
	for( var i = 0; i < _proteins.length; i++ ) {
		
		var found = false;
		for( var k = 0; k < alignedProteins.length; k++ ) {
			if( _proteins[ i ] == alignedProteins[ k ] ) { found = true; break; }
		}

		if( !found ) {
			content += "<option value=\"" + _proteins[ i ] + "\">" + _proteinNames[ _proteins[ i ] ] + "</option>\n";
		}
	}
	
	content += "</select>\n";
	
	content += "<div>\n";
	content += "     <input type=\"button\" id=\"pdb-map-protein-submit\" value=\"Map Protein to Structure\" disabled>\n";
	content += "     <input type=\"button\" id=\"pdb-map-protein-cancel\" value=\"Cancel\">\n";	
	content += "</div>\n";
	
	$selectDiv.html( content );

	// add handlers	
	$("#pdb-map-protein-cancel").click( function() {
		closePDBMapProteinOverlay();
	});
	
	$("#pdb-map-protein-overlay-protein-select").change( function() {
		$("#pdb-map-protein-submit").removeAttr( 'disabled' );
	});

	$("#pdb-map-protein-submit").click( function() {
		submitProteinForAlignment( chainId );
	});
	
};


var showAlignment = function( alignment, showSave ) {
		
	var pdbFile = getSelectedPDBFile();
	
	
	closePDBMapProteinOverlay();
	openPDBShowAlignmentOverlay();
	
	var $showAlignmentDiv = $( "#pdb-show-alignment-overlay-body" );
	$showAlignmentDiv.empty();
	
	var html = "<div style=\"margin-top:20px;margin-bottom:20px;font-size:14pt;\">Showing alignment for " +  _proteinNames[ alignment.nrseqId ] + " and " + pdbFile.name + " (Chain " + alignment.chainId + "):</div>\n";
	
	
	html += "<div style=\"overflow:scroll;\">\n";
	html += "<table>";
	html += "<tr>";
	html += "<td style=\"white-space:nowrap;font-weight:bold;\">" + _proteinNames[ alignment.nrseqId ] + "</td>";
	html += "<td style=\"white-space:nowrap;font-size:11pt;font-family:monospace;\">" + alignment.alignedNrseqSequence + "</td>";
	html += "</tr>";
	
	html += "<tr>";
	html += "<td style=\"white-space:nowrap;font-weight:bold;\">" + pdbFile.name + "(" + alignment.chainId + ")</td>";
	html += "<td style=\"white-space:nowrap;font-size:11pt;font-family:monospace;\">" + alignment.alignedPDBSequence + "</td>";
	html += "</tr>";
	html += "</table>";
	html += "</div>\n";
	
	html += "<div style=\"margin-top:20px;\">\n";
	
	if( showSave ) {
		html += "<input type=\"button\" value=\"Save\" id=\"saveAlignmentButton\" />";
	}
	
	if( _PDB_FILES[ pdbFile.id ][ 'canEdit' ] ) {
		html += "<input type=\"button\" value=\"Edit\" id=\"editAlignmentButton\" />";
	}
	
	html += "<input type=\"button\" value=\"Cancel\" onClick=\"closePDBShowAlignmentOverlay()\" />";	
	html += "</div>\n";
	
	$showAlignmentDiv.html( html );
	
	$("#saveAlignmentButton").click( function() {
		saveAlignment( alignment );
	});
	
	$("#editAlignmentButton").click( function() {
		showEditAlignmentOverlay( alignment );
	});
	
};


var showEditAlignmentOverlay = function( alignment ) {
	
	var pdbFile = getSelectedPDBFile();
	
	var $overlayDiv = $("#pdb-show-alignment-overlay-body");
	$overlayDiv.empty();
	
	var html = "<div style=\"margin-top:20px;margin-bottom:20px;font-size:14pt;\">Editing alignment for " +  _proteinNames[ alignment.nrseqId ] + " and " + pdbFile.name + " (Chain " + alignment.chainId + "):</div>\n";
	
	
	html += "<div>\n";
	html += "<textarea id=\"edit-alignment-textarea\" style=\"white-space:nowrap;overflow:scroll;width:95%;height:4em;\">";
	
	html += alignment.alignedNrseqSequence + "\n" + alignment.alignedPDBSequence;
	
	html += "</textarea>";
	html += "</div>\n";
	
	html += "<div style=\"margin-top:20px;\">\n";
	
	html += "<input type=\"button\" value=\"Save\" id=\"saveEditedAlignmentButton\" />";
	html += "<input type=\"button\" value=\"Cancel\" onClick=\"closePDBShowAlignmentOverlay()\" />";	
	html += "</div>\n";
	
	html += "<div style=\"margin-top:20px;\">Instructions: Sequence for " + _proteinNames[ alignment.nrseqId ] + " is on the top and " + pdbFile.name + " (Chain " + alignment.chainId + ") " +
			" is on the bottom. Replace the text above with a new alignment and click &quot;Save.&quot;</div>";
	
	
	$overlayDiv.html( html );
	
	$("#saveEditedAlignmentButton").click( function() {
		saveEditedAlignment( alignment );
	});
	
	
};


var saveEditedAlignment = function( alignment ) {
	
	var rawText = $("#edit-alignment-textarea").val();	
	rawText = rawText.trim().replace(/(\r\n|\r)/gm, "\n");	// ensure we know what we're working with
		
	// verify the new alignments
	var seqs = rawText.split( "\n" );
		
	if( seqs.length != 2 ) {
		alert( "Detected something other than 2 lines. The input must contain exactly two lines, the protein sequence you're aligning on the first line and the sequence for the PDB chain on the second.");
		return;
	}
	
	var nrseqSequence = seqs[ 0 ].trim();
	var pdbSequence = seqs[ 1 ].trim();
	
	if( nrseqSequence.length != pdbSequence.length ) {
		alert( "Aligned sequences must be the same length. Use hyphens to indicate insertions in the sequences." );
		return;
	}
	
	var tmpNrseqSequence = nrseqSequence.replace( /-/g, "" );
	var tmpPdbSequence = pdbSequence.replace( /-/g, "" );
	
	var rawNrseqSequence = alignment.alignedNrseqSequence.replace( /-/g, "" );
	if( tmpNrseqSequence !== rawNrseqSequence ) {
		var proteinName = _proteinNames[ alignment.nrseqId ];
		
		alert( "Sequence given for " + proteinName + " does not match sequence on file for that protein." );
		return;
	}
	
	var rawPdbSequence = alignment.alignedPDBSequence.replace( /-/g, "" );
	if( tmpPdbSequence !== rawPdbSequence) {
		alert( "Sequence given for PDB chain does not match sequence on file." );
		return;
	}

	
	if( alignment.alignedNrseqSequence == nrseqSequence && alignment.alignedPDBSequence == pdbSequence ) {
		alert( "The alignments were not changed." );
	} else {

		// looks good, can save it
		alignment.alignedNrseqSequence = nrseqSequence;
		alignment.alignedPDBSequence = pdbSequence;
		
		saveAlignment( alignment );
	}
};


var saveAlignment = function( alignment ) {
	
	var _URL = contextPathJSVar + "/services/psa/saveAlignment";

	$.ajax({
	        type: "POST",
	        url: _URL,
	        data: { 'id' : alignment.id, 'pdbFileId' : alignment.pdbFileId, 'chainId' : alignment.chainId,
	        	'alignedPDBSequence' : alignment.alignedPDBSequence, 'nrseqId' : alignment.nrseqId,
	        	'alignedNrseqSequence' : alignment.alignedNrseqSequence },
	        dataType: "json",
	        success: function(data)	{

	        	closePDBShowAlignmentOverlay();
	        	loadPDBFileAlignments( listChains );
											
			},
	        failure: function(errMsg) {
	        	handleAJAXFailure( errMsg );
	        },
	        error: function(jqXHR, textStatus, errorThrown) {	
	        	
				handleAJAXError( jqXHR, textStatus, errorThrown );
			}
	  });
	
};

var submitProteinForAlignment = function( chainId ) {

	var proteinId = $("#pdb-map-protein-overlay-protein-select").val();
	var pdbFileId = getSelectedPDBFile().id;
	
	var url = contextPathJSVar + "/services/psa/alignSequences";
	url += "?pdbFileId=" + pdbFileId;
	url += "&chain=" + chainId;
	url += "&proteinId=" + proteinId;
	
	 $.ajax({
	        type: "GET",
	        url: url,
	        dataType: "json",
	        success: function(data)	{
	        	        	
	        	showAlignment( data, true );
	        		
	        },
	        failure: function(errMsg) {
	        	handleAJAXFailure( errMsg );
	        },
			error: function(jqXHR, textStatus, errorThrown) {
				handleAJAXError( jqXHR, textStatus, errorThrown );
			}
	  });
	
};


var showStructureInOverlay = function ( chainId ) {
	
	var options = {
			  width: 450,
			  height: 450,
			  antialias: true,
			  quality : 'high',
			  fog: false
	};
	
	$("#pdb-map-protein-overlay-structure").empty();
	
	var overLayViewer = pv.Viewer(document.getElementById('pdb-map-protein-overlay-structure'), options);
	
	var chains = _STRUCTURE.chains();
	for( var i = 0; i < chains.length; i++ ) {
		
		if( chains[i].name() === chainId ) {
			var chain = _STRUCTURE.select({cname : chains[i].name()});
			overLayViewer.cartoon( 'protein', chain, { color:color.uniform( '#A55353' ) } );
		} else {
			var chain = _STRUCTURE.select({cname : chains[i].name()});
			overLayViewer.cartoon( 'protein', chain, { color:color.uniform( '#fefefe' ) } );
		}
		
		overLayViewer.centerOn(_STRUCTURE);
		overLayViewer.autoZoom();
	}
	
};
