
"use strict";

/**
 * The custom region manager manages loading/saving/changing and
 * querying user-defined custom regions to show on protein sequence
 * bars.
 */
var customRegionManager = function() {	
	this.initialize();
};

/**
 * Perform any initialization
 */
customRegionManager.prototype.initialize  = function(  ) {

    /*
     * our main cached data object. all data retrieved from
     * web services will be stored in this object so we
     * do not need to re-retrieve. this naturally assumes
     * only one person is editing these data at a time!
     */
    this._customRegionAnnotationData = { };

    /*
     * compiled handlebars templates
     */
    this._compiledHandlebarsTemplates = { };

};


/**
 * Populate the right panel, which shows the custom protein region annotation
 * data for this protein.
 * 
 * @param {*} proteinId 
 */
customRegionManager.prototype.populateCustomProteinAnnotationPanelForProtein = function( proteinId ) {

    var objectThis = this;

    // if we have no data for this protein, go get it
    if( !(this._customRegionAnnotationData[ proteinId ]) ) {

        this.showInfo( "Loading protein data..." );

        var callbackFunction = function( _proteinId ) {
            objectThis.populateCustomProteinAnnotationPanelForProtein( _proteinId );
        };

        this.getCustomRegionDataForProteinViaAjax( proteinId, callbackFunction );
        return;
    }

    this.hideAllDialogs();

    // make sure we're showing the correct content
    $("#custom_region_manager_right_pane_empty").hide();
    $("#custom_region_manager_right_pane_protein_selected").show();

    var $domainListDiv = $("#custom_region_manager_add_domains_list");

    // add the buttons
    $domainListDiv.append( this.getButtonsHTML( { } ) );
    this.addClickHandlersToButtons( $domainListDiv, { proteinId:proteinId } );

    // add empty form to top for adding a new region
    if( this._customRegionAnnotationData[ proteinId ].length < 1 ) {
        $domainListDiv.append( this.getCustomRegionFormHTML( { } ) );   
    }

    // show a form for all previously-defined regions
    var regions = this._customRegionAnnotationData[ proteinId ];
    for( var i = 0; i < regions.length; i++ ) {
        var region = regions[ i ];
        $domainListDiv.append( this.getCustomRegionFormHTML( region ) ); 
    }


    // add the color picker to all the listed domains
    this.addColorPickerToFormSection( $domainListDiv );

};

/**
 * Code to be run upon a successful save of region information to the database.
 * 
 * @param {*} params 
 * @param {*} regionData 
 */
customRegionManager.prototype.handleSuccessfulSave = function( params, regionData ) {

    this._customRegionAnnotationData[ params.proteinId ] = regionData;

    this.hideAllDialogs();
    this.showSuccess( "Data saved to database." );

    drawSvg();

}

/**
 * Validates the given array of regions for suitability for submission to the server.
 * Ensures all regions have defined values, that they're the right type, that
 * start positions are < ending positions, that the ending position is not past the
 * end of the protein, and that no regions overlap.
 * 
 * Returns false if invalid, true if valid. Populates error dialog if invalid
 * data are found.
 * 
 * @param {*} regionArray 
 * @param {*} proteinId 
 */
customRegionManager.prototype.validateRegionData = function ( regionArray, proteinId ) {

    /*
     * Actually not going to require this--this is a way they can delete all
     * custom annotations for a protein.
     */
    /*
    if( !regionArray || regionArray.length < 1 ) {
        this.showError( "Must have at least one region defined." );
        return false;
    }
    */

    for( var i = 0; i < regionArray.length; i++ ) {

        var region = regionArray[ i ];

        var startPosition = region.startPosition;
        var endPosition = region.endPosition;
        var annotationText = region.annotationText;
        var annotationColor = region.annotationColor;

        if( !this.isAnInteger( startPosition ) ) {
            this.showError( "Start position must be a number. (Region #" + ( i + 1 ) + ")" );
            return false;
        }

        startPosition = parseInt( startPosition );
        if( startPosition < 1 ) {
            this.showError( "Start position must be greater than or equal to 1. (Region #" + ( i + 1 ) + ")" );
            return false;
        }

        if( !this.isAnInteger( endPosition ) ) {
            this.showError( "End position must be a number. (Region #" + ( i + 1 ) + ")" );
            return false;
        }

        endPosition = parseInt( endPosition );

        if( endPosition <= startPosition ) {
            this.showError( "End position must be greater than start position. (Region #" + ( i + 1 ) + ")" );
            return false;
        }
        if( endPosition > _proteinLengths.getProteinLength( proteinId ) ) {
            this.showError( "End position is passed the end of the protein. Protein length is: " +  _proteinLengths.getProteinLength( proteinId ) + " (Region #" + ( i + 1 ) + ")" );
            return false;
        }

        if( !annotationText || annotationText.length < 1 ) {
            this.showError( "The annotation text may not be blank. (Region #" + ( i + 1 ) + ")" );
            return false;
        }

        if( !annotationColor || annotationColor.length < 1 ) {
            this.showError( "The color may not be blank. Try choosing again. (Region #" + ( i + 1 ) + ")" );
            return false;
        }

    }

    if( this.testIfRegionsOverlap( regionArray ) ) {
        this.showError( "Regions may not overlap." );
        return false;
    }

    return true;
}

/**
 * Test if the given array of region definitions contains any overlapping regions.
 * Returns true if yes, false if no.
 * 
 * @param {*} regionArray 
 */
customRegionManager.prototype.testIfRegionsOverlap = function( regionArray ) {

    for( var i = 0; i < regionArray.length; i++ ) {

        var region1 = regionArray[ i ];
        var startPosition1 = parseInt( region1.startPosition );
        var endPosition1 = parseInt( region1.endPosition );

        for( var k = 0; k < regionArray.length; k++ ) {

            if( i === k ) { continue; }

            var region2 = regionArray[ k ];
            var startPosition2 = parseInt( region2.startPosition );
            var endPosition2 = parseInt( region2.endPosition );

            if( startPosition1 >= startPosition2 && startPosition1 <= endPosition2 ) { return true; }
            if( endPosition1 >= startPosition2 && endPosition1 <= endPosition2 ) { return true; }

        }
    }

    return false;
}

/**
 * Get an array of regions currently defined by the collection of region definition
 * forms being displayed for the currently selected protein. This array is suitable
 * for direct submission to web services for saving purposes.
 * 
 * @param {*} params 
 */
customRegionManager.prototype.getRegionsArrayFromForms = function( params ) {

    var regionArray = [ ];

    $( ".region-input-form" ).each(function( index ) {
        var $thisElement = $( this );
        var region = { };

        region.startPosition = $thisElement.find( "#startPosition").val();
        region.endPosition = $thisElement.find( "#endPosition").val();
        region.annotationColor = $thisElement.find( "#custom_region_manager_right_pane_add_domain_color").css('backgroundColor');
        region.annotationText = $thisElement.find( "#annotation_text").val();
        region.projectId = params.projectId;
        region.proteinSequenceVersionId = params.proteinId;

        if(  (!region.startPosition || region.startPosition.length < 1 ) &&
             ( !region.endPosition || region.endPosition.length < 1 ) && 
             ( !region.annotationText || region.annotationText.length < 1 ) ) {
                       // skip totally empty region definitions
             }
        else {
            regionArray.push( region );
        }
    });

    return regionArray;
}

/**
 * Populate the protein list in the left pane
 */
customRegionManager.prototype.populateProteinList  = function(  ) {

    var objectThis = this;

    var proteinListContainer = $("#custom_region_manager_protein_list");
    
    for( var i = 0; i < _proteins.length; i++ ) {

        var proteinId = _proteins[ i ];
        var proteinName = _proteinNames[ proteinId ];

        var html = "<div data-protein-id=\"" + proteinId + "\" ";
        html += "class=\"custom-region-manager-protein-item";
        
        if( i === _proteins.length - 1 ) {
            html += " last-item";
        }
        
        html += "\">";
        html += proteinName;
        html += "</div>";

        proteinListContainer.append( html );

    }

    // attach a click handler to the listed proteins
    $(".custom-region-manager-protein-item").click( function( eventObject ) {
		try {

            objectThis.clearSelectedItems();

            $(this).addClass( "custom-region-manager-selected-item" );
            objectThis.populateCustomProteinAnnotationPanelForProtein( $(this).data('proteinId') );
            
		} catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	} );

    this.proteinListPopulated = true;

};

/**
 * Clear all currently-selected items.
 */
customRegionManager.prototype.clearSelectedItems  = function(  ) {

    $("#custom_region_manager_add_domains_list").empty();
    this.hideAllDialogs();

    $( ".custom-region-manager-selected-item" ).each(function( i ) {
        $( this ).removeClass( "custom-region-manager-selected-item" );
      });
}


/**
 * Show the overlay for managing protein sequences
 */
customRegionManager.prototype.showManagerOverlay  = function(  ) {

    var managerOverlay = $("#custom-region-manager-overlay-container");
    var managerOverlayBackground = $("#custom_region_manager_modal_dialog_overlay_background");
    
    managerOverlay.show();
    managerOverlayBackground.show();

    if( !this.proteinListPopulated ) {
        this.populateProteinList();
    }

};

/**
 * Close/hide the overlay for managing protein sequences
 */
customRegionManager.prototype.closeManagerOverlay  = function(  ) {

    var managerOverlay = $("#custom-region-manager-overlay-container");
    var managerOverlayBackground = $("#custom_region_manager_modal_dialog_overlay_background");
    
    this.clearSelectedItems();

    $("#custom_region_manager_right_pane_empty").show();
    $("#custom_region_manager_right_pane_protein_selected").hide();

    managerOverlay.hide();
    managerOverlayBackground.hide();

};

/**
 * Get the HTML for a new custom region definition section
 * uses handlebars
 * 
 * @param {*} params 
 */
customRegionManager.prototype.getCustomRegionFormHTML = function( params ) {

    if( !this._compiledHandlebarsTemplates[ 'customRegionForm' ] ) {
        var $template = $("#custom_region_manager_region_form_template");
        var handlebarsSource = $template.text();
        
        if ( handlebarsSource === undefined ) {
            throw Error( "handlebarsSource === undefined" );
        }
        if ( handlebarsSource === null ) {
            throw Error( "handlebarsSource === null" );
        }
        
        var compiledHandlebarsTemplate = Handlebars.compile( handlebarsSource );
        this._compiledHandlebarsTemplates[ 'customRegionForm' ] = compiledHandlebarsTemplate;
    }

    return this._compiledHandlebarsTemplates.customRegionForm( params );

}

/**
 * Get the HTML for the buttons section of the form (uses handlebars)
 * @param {*} params 
 */
customRegionManager.prototype.getButtonsHTML = function( params ) {

    if( !this._compiledHandlebarsTemplates[ 'buttonsHTML' ] ) {
        var $template = $("#custom_region_manager_region_form_buttons");
        var handlebarsSource = $template.text();
        
        if ( handlebarsSource === undefined ) {
            throw Error( "handlebarsSource === undefined" );
        }
        if ( handlebarsSource === null ) {
            throw Error( "handlebarsSource === null" );
        }
        
        var compiledHandlebarsTemplate = Handlebars.compile( handlebarsSource );
        this._compiledHandlebarsTemplates[ 'buttonsHTML' ] = compiledHandlebarsTemplate;
    }

    return this._compiledHandlebarsTemplates.buttonsHTML( params );

}

/**
 * Add the click handlers to the buttons in the form
 * @param {*}  
 * @param {*} params 
 */
customRegionManager.prototype.addClickHandlersToButtons = function( $domainListDiv, params ) {

    var objectThis = this;

    // add handler to the create new region button
    $domainListDiv.find( "#custom_region_manager_create_new_region_button" ).click( function( eventObject ) {
        var $newElement = $( objectThis.getCustomRegionFormHTML( { } ) );
        $newElement.insertAfter( "#custom_region_manager_right_pane_form_buttons" );
        objectThis.addColorPickerToFormSection( $newElement );
    });


    // add handler to the cancel button
    $domainListDiv.find( "#custom_region_manager_cancel_button" ).click( function( eventObject ) {
        $domainListDiv.empty();
        objectThis.populateCustomProteinAnnotationPanelForProtein( params.proteinId );
     });

         // add handler to the cancel button
    $domainListDiv.find( "#custom_region_manager_save_button" ).click( function( eventObject ) {
        objectThis.validateAndSaveRegionsToDatabase( params );
     });

}

/**
 * Add a color picker to the given chunk of HTML containing a color picker box class
 * @param {*}  
 */
customRegionManager.prototype.addColorPickerToFormSection = function ( $formSection ) {

    var objectThis = this;

    $formSection.find( ".color_picker_box" ).ColorPicker({
        onSubmit: function(hsbColor, hexColor, rgbColor, htmlElement ) {
            
            // update block on page with selected color 
            
            var $htmlElement = $( htmlElement );
            
            $htmlElement.css('backgroundColor', '#' + hexColor); // set HTML block color on page
            $htmlElement.data( 'backgroundHex', '#' + hexColor );
            $htmlElement.ColorPickerHide();

        },
        onHide: function() {
            //  called when hidden
            // alert("On Hide");
        }
    });
}

/*
 * Methods for showing and hiding dialogs/informational messages
 */

/**
 * Hide all dialog messages
 */
customRegionManager.prototype.hideAllDialogs = function() {
    this.hideError();
    this.hideInfo();
    this.hideSuccess();
}

/**
 * Show the given error message
 * @param {*} message 
 */
customRegionManager.prototype.showError = function( message ) {

    var $errorDiv = $( "#custom_region_manager_error_div" );

    $errorDiv.show();
    $errorDiv.empty();
    $errorDiv.append( message );

}

/**
 * Clear and hide the error message dialog
 */
customRegionManager.prototype.hideError = function() {

    var $errorDiv = $( "#custom_region_manager_error_div" );

    $errorDiv.empty();
    $errorDiv.hide();

}

/**
 * Show the given informational message
 * @param {*} message 
 */
customRegionManager.prototype.showInfo = function( message ) {

    var $infoDiv = $( "#custom_region_manager_info_div" );

    $infoDiv.show();
    $infoDiv.empty();
    $infoDiv.append( message );

}

/**
 * Clear and hide the informational message dialog
 */
customRegionManager.prototype.hideInfo = function() {

    var $errorDiv = $( "#custom_region_manager_info_div" );

    $errorDiv.empty();
    $errorDiv.hide();

}

/**
 * Show the given success message
 * @param {*} message 
 */
customRegionManager.prototype.showSuccess = function( message ) {

    var $infoDiv = $( "#custom_region_manager_success_div" );

    $infoDiv.show();
    $infoDiv.empty();
    $infoDiv.append( message );

}

/**
 * Clear and hide the success message dialog
 */
customRegionManager.prototype.hideSuccess = function() {

    var $errorDiv = $( "#custom_region_manager_success_div" );

    $errorDiv.empty();
    $errorDiv.hide();

}



/*
 * All AJAX methods go here
 */

 /**
 * Make ajax call to get custom region annotation data for a given proteinId
 * @param {*} proteinId 
 * @param {*} callback 
 */
customRegionManager.prototype.getCustomRegionDataForProteinViaAjax = function( proteinId, callback ) {

    var objectThis = this;

	var project_id = this.getProjectId();

	var _URL = contextPathJSVar + "/services/customRegionAnnotation/queryDataForProtein";
	var requestData = {
            proteinId : proteinId,
            projectId : project_id
	};
	var requestDataJSON = JSON.stringify( requestData );

    $.ajax({
		type : "POST",
		url : _URL,
	    data: requestDataJSON,
	    contentType: "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {

            console.log( "in ajax success" );
            console.log( data );

            objectThis._customRegionAnnotationData[ proteinId ] = data;
            callback( proteinId );
            
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
        error : function(jqXHR, textStatus, errorThrown) {
			handleAJAXError(jqXHR, textStatus, errorThrown);
		}
	});
};

/**
 * Make ajax call to get custom region annotation data for a given proteinId
 * @param {*} proteinId 
 * @param {*} callback 
 */
customRegionManager.prototype.getCustomRegionDataForProteinsViaAjaxForViewerDisplay = function( proteinIds, doDraw ) {

    incrementSpinner();				// create spinner

    var objectThis = this;

	var project_id = this.getProjectId();

	var _URL = contextPathJSVar + "/services/customRegionAnnotation/queryDataForProteins";
	var requestData = {
            proteinIds : proteinIds,
            projectId : project_id
	};
	var requestDataJSON = JSON.stringify( requestData );

    $.ajax({
		type : "POST",
		url : _URL,
	    data: requestDataJSON,
	    contentType: "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {

            var proteinIds = Object.keys( data );
            for ( var i = 0; i < proteinIds.length; i++ ) {
                var proteinId = proteinIds[ i ];
                var regionData = data[ proteinId ];

                objectThis._customRegionAnnotationData[ proteinId ] = regionData;
            }

            console.log( "got these data");
            console.log( objectThis._customRegionAnnotationData );

            decrementSpinner();
            loadDataAndDraw( doDraw );
            
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
        error : function(jqXHR, textStatus, errorThrown) {
			handleAJAXError(jqXHR, textStatus, errorThrown);
		}
	});
};

/**
 * Validates and saves the region data currently being displayed in the region
 * definition form elements. This makes an ajax call to save the data to the
 * database.
 * 
 * @param {*} params 
 */
customRegionManager.prototype.validateAndSaveRegionsToDatabase = function( params ) {

    var objectThis = this;

	var projectId = this.getProjectId();
    var proteinId = params.proteinId;

    // need to validate against protein length--need to make sure we know how long it is
    if( !_proteinLengths._proteinLengthsInternal[ proteinId ] ) {

        var callback = function( _params ) {
            objectThis.validateAndSaveRegionsToDatabase( _params );
        }

        this.loadProteinSequenceDataForProtein( params, callback );
        return;
    }

    var regionArray = this.getRegionsArrayFromForms( { proteinId:proteinId, projectId:projectId } );

    if( !this.validateRegionData( regionArray, proteinId ) ) { return; }

    this.hideError();
    this.showInfo( "Saving to database..." );


	var _URL = contextPathJSVar + "/services/customRegionAnnotation/saveDataForProtein";
	var requestData = {
            proteinId : proteinId,
            projectId : projectId,
            regionData : regionArray
    };
    
    console.log( requestData );

	var requestDataJSON = JSON.stringify( requestData );

    $.ajax({
		type : "POST",
		url : _URL,
	    data: requestDataJSON,
	    contentType: "application/json; charset=utf-8",
		dataType : "json",
		success : function(data) {

            objectThis.handleSuccessfulSave( params, data );
            
		},
        failure: function(errMsg) {
            handleAJAXFailure( errMsg );
            objectThis.showError( "Error saving to database: " + errMsg );
        },
        error : function(jqXHR, textStatus, errorThrown) {
            handleAJAXError(jqXHR, textStatus, errorThrown);
            objectThis.showError( "Error saving to database: " + errMsg );
		}
	});

}

/**
 * Loads the sequence and length information for params.proteinId--currently used
 * to ensure regions do not extend past the end of the protein.
 * 
 * These data are queried and cached in the main image viewer in the same way the
 * image viewer queries and caches these data.
 * 
 * @param {*} params 
 * @param {*} callback 
 */
customRegionManager.prototype.loadProteinSequenceDataForProtein = function( params, callback ) {

	var url = contextPathJSVar + "/services/proteinSequence/getDataForProtein";
    var project_id = this.getProjectId();
    
	var ajaxRequestData = {
			project_id : project_id,
			proteinIdsToGetSequence: [ params.proteinId ]
	};
	$.ajax({
	        type: "GET",
	        url: url,
			dataType: "json",
			data: ajaxRequestData,  //  The data sent as params on the URL
			traditional: true,  //  Force traditional serialization of the data sent
			//   One thing this means is that arrays are sent as the object property instead of object property followed by "[]".
			//   So proteinIdsToGetSequence array is passed as "proteinIdsToGetSequence=<value>" which is what Jersey expects
	        success: function(data)	{
	        	try {

	        		var returnedProteinIdsAndSequences = data;  //  The property names are the protein ids and the property values are the sequences
	        		// copy the returned sequences into the global object
	        		var returnedProteinIdsAndSequences_Keys = Object.keys( returnedProteinIdsAndSequences );
	        		for ( var keysIndex = 0; keysIndex < returnedProteinIdsAndSequences_Keys.length; keysIndex++ ) {
	        			var proteinId = returnedProteinIdsAndSequences_Keys[ keysIndex ];
	        			_proteinSequences[ proteinId ] = returnedProteinIdsAndSequences[ proteinId ];
	        			_proteinLengths.setProteinLength( proteinId, returnedProteinIdsAndSequences[ proteinId ].length )
	        		}

	        		callback( params );
	        	} catch( e ) {
	        		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
	        		throw e;
	        	}
	        },
	        failure: function(errMsg) {
	        	handleAJAXFailure( errMsg );
	        },
	        error: function(jqXHR, textStatus, errorThrown) {	
					handleAJAXError( jqXHR, textStatus, errorThrown );
			}
	  });	
}


/*
 * General utility methods go here
 */
customRegionManager.prototype.getProjectId = function () {
    var project_id = $("#project_id").val();
	if ( project_id === undefined || project_id === null 
			|| project_id === "" ) {
		throw Error( '$("#project_id").val() returned no value' );
    }
    return project_id;
}

customRegionManager.prototype.isAnInteger = function( x ) {
    if( x == parseInt( x, 10 ) ) {
        return true;
    }

    return false;
}