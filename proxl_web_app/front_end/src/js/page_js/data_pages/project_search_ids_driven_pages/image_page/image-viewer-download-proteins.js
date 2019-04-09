/**
 * image-viewer-download-proteins.js
 * 
 * Javascript for the viewMergedImage.jsp page
 * 
 *  
 * !!! The following variables passed in from "crosslink-image-viewer.js" are used in this file:
 * 
 *    imagePagePrimaryRootCodeObject (copied to local variable imagePagePrimaryRootCodeObject_LocalCopy)
 * 
 */

 
//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

///////////////////////////////////////////

var imagePagePrimaryRootCodeObject_LocalCopy = undefined; // passed in from "crosslink-image-viewer.js"

let _NOT_HIGHLIGHTED_LINE_COLOR_LOCAL_COPY = undefined;

/**
 * 
 */
export class DownloadProteins {

	/**
	 * 
	 */
	constructor() {
		
		this.initialized = false;
	}
    
	/**
	 * 
	 */
	init( params ) {

        if ( this.initialized ) {
            //  Only initialize once
            return;  // EARLY EXIT
        }
		
        console.log("DownloadProteins.init() called");

        if ( ! params ) {
            throw Error("No Parameters to init(...)")
        }
        if ( ! params._NOT_HIGHLIGHTED_LINE_COLOR ) {
            throw Error("No value in params._NOT_HIGHLIGHTED_LINE_COLOR init(...)")
        }

        _NOT_HIGHLIGHTED_LINE_COLOR_LOCAL_COPY = params._NOT_HIGHLIGHTED_LINE_COLOR;

        const objectThis = this;

        {
            const $link = $("#download-protein-data");
            $link.click( function( event ) { 
                try {
                    objectThis.submitDownloadForParams({ clickedThis : this, downloadStrutsAction : "downloadMergedProteins.do" } ); 
                    event.preventDefault();
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });
        }

        {
            const $link = $("#download-protein-udrs");
            $link.click( function( event ) { 
                try {
                    objectThis.submitDownloadForParams({ clickedThis : this, downloadStrutsAction : "downloadMergedProteinUDRs.do" } ); 
                    event.preventDefault();
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });
        }
        {
            const $link = $("#download-protein-shulman");
            $link.click( function( event ) { 
                try {
                    objectThis.submitDownloadForParams({ clickedThis : this, downloadStrutsAction : "downloadMergedProteinsPeptidesSkylineShulman.do" } ); 
                    event.preventDefault();
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });
        }
        {
            const $link = $("#download-protein-skyline-prm");
            $link.click( function( event ) { 
                try {
                    objectThis.submitDownloadForParams({ clickedThis : this, downloadStrutsAction : "downloadMergedProteinsPeptidesSkylineEng.do" } ); 
                    event.preventDefault();
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });
        }
        {
            const $link = $("#download-fasta-file");
            $link.click( function( event ) { 
                try {
                    objectThis.submitDownloadForParams({ clickedThis : this, downloadStrutsAction : "downloadMergedProteinsFASTA.do" } ); 
                    event.preventDefault();
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });
        }
        {
            const $link = $("#download-protein-xinet");
            $link.click( function( event ) { 
                try {
                    objectThis.submitDownloadForParams({ clickedThis : this, downloadStrutsAction : "downloadMergedProteinsCLMS_CSV.do" } ); 
                    event.preventDefault();
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });
        }
        {
            const $link = $("#download-protein-lengths");
            $link.click( function( event ) { 
                try {
                    objectThis.submitDownloadForParams({ clickedThis : this, downloadStrutsAction : "downloadMergedProteinsLengths.do" } ); 
                    event.preventDefault();
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });
        }
        {
            const $link = $("#download-links-for-xvis");
            $link.click( function( event ) { 
                try {
                    objectThis.submitDownloadForParams({ clickedThis : this, downloadStrutsAction : "downloadMergedProteinsXvis.do" } ); 
                    event.preventDefault();
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });
        }


        // setTimeout(() => {
        //     this.showDownloadParams();
        // }, 1000 );

        this.initialized = true;
	}
    
	// /**
	//  * 
	//  */
	// showDownloadParams() {
        
    //     console.log("DownloadProteins.showDownloadParams() called");
        
    //     const projectSearchIds = imagePagePrimaryRootCodeObject_LocalCopy.getVariable__v_projectSearchIds();

	// 	for ( var i = 0; i < projectSearchIds.length; i++ ) {
	// 		console.log( "projectSearchId=" + projectSearchIds[ i ] );
    //     }
        
	// 	var baseJSONObject = imagePagePrimaryRootCodeObject_LocalCopy.getNavigationJSON_Not_for_Image_Or_Structure();

    //     var psmPeptideCutoffsForProjectSearchIds_JSONString = JSON.stringify( baseJSONObject );

    //     console.log( "psmPeptideCutoffsForProjectSearchIds_JSONString: " + psmPeptideCutoffsForProjectSearchIds_JSONString );
    // }
    

	/////////////////////////////////////////////

	//   download calls
	
	/*
	 * Takes a filename, mimetype, and string content and initiates a file download
	 * of the content from the current page, without leaving the page. 
	 * 
	 * It is assumed jquery is loaded.
	 * 
	 */
	get_selectedCrosslinksLooplinksMonolinks() {

        const crosslinksResult = [];
        const looplinksResult = [];

        const indexManager = imagePagePrimaryRootCodeObject_LocalCopy.getVariable__v_indexManager();
        const selectedProteins = indexManager.getProteinList();

        const _proteinLinkPositions     = imagePagePrimaryRootCodeObject_LocalCopy.getVariable__v_proteinLinkPositions();
        const _proteinLooplinkPositions = imagePagePrimaryRootCodeObject_LocalCopy.getVariable__v_proteinLooplinkPositions();
        // const _proteinMonolinkPositions = imagePagePrimaryRootCodeObject_LocalCopy.getVariable__v_proteinMonolinkPositions();

        // draw interprotein crosslink lines, if requested
		if ( $( "input#show-crosslinks" ).is( ':checked' ) ) {
            // Copied from drawInterProteinCrosslinkLines(...)
            for ( var fromSelectedProteinsIndex = 0; fromSelectedProteinsIndex < selectedProteins.length; fromSelectedProteinsIndex++ ) {
                var fromProteinId = selectedProteins[ fromSelectedProteinsIndex ];
                for ( var toSelectedProteinsIndex = 0; toSelectedProteinsIndex < selectedProteins.length; toSelectedProteinsIndex++ ) {
                    var toProteinId = selectedProteins[ toSelectedProteinsIndex ];
                    if ( toSelectedProteinsIndex <= fromSelectedProteinsIndex ) { continue; }
                    if ( _proteinLinkPositions[ selectedProteins[ fromSelectedProteinsIndex ] ] == undefined ) { continue; }
                    if ( _proteinLinkPositions[ selectedProteins[ fromSelectedProteinsIndex ] ][ toProteinId ] == undefined ) { continue; }
                    var fromProteinPositionObject = _proteinLinkPositions[ fromProteinId ][ toProteinId ];
                    var fromProteinPositionKeys = Object.keys( fromProteinPositionObject );
                    for ( var ii = 0; ii < fromProteinPositionKeys.length; ii++ ) {
                        var fromProteinPosition = fromProteinPositionKeys[ ii ];
                        var fromProteinPositionInt =  parseInt( fromProteinPosition );
                        var toProteinPositionObject = _proteinLinkPositions[ fromProteinId ][ toProteinId ][ fromProteinPosition ];
                        var toProteinPositionKeys = Object.keys( toProteinPositionObject );
                        for ( var kk = 0; kk < toProteinPositionKeys.length; kk++ ) {
                            var toProteinPosition = toProteinPositionKeys[ kk ];
                            var toProteinPositionInt =  parseInt( toProteinPosition );
                            // for display to user
                            var getCrosslinkLineColorParams = { 
                                    fromProteinIndex : fromSelectedProteinsIndex,
                                    fromProteinPosition : fromProteinPositionInt, 
                                    toProteinIndex : toSelectedProteinsIndex,
                                    toProteinPosition : toProteinPositionInt 
                            };
                            var lineColor = imagePagePrimaryRootCodeObject_LocalCopy.call__getCrosslinkLineColor( getCrosslinkLineColorParams );

                            if ( lineColor !== _NOT_HIGHLIGHTED_LINE_COLOR_LOCAL_COPY ) {
                                const linkInfoToSave = { fromProtId : fromProteinId, toProtId : toProteinId, fromPos : fromProteinPositionInt, toPos : toProteinPositionInt };
                                crosslinksResult.push( linkInfoToSave );
                            } else {
                                var y = 0;
                            }
                            var z = 0;
                        }
                    }
                }
            }
        }

		// draw self-crosslinks, if requested
		if ( $( "input#show-self-crosslinks" ).is( ':checked' ) ) {
            //  Copied from: drawSelfProteinCrosslinkLines( selectedProteins, svgRootSnapSVGObject );
            for ( var i = 0; i < selectedProteins.length; i++ ) {
                var proteinBarProteinId = selectedProteins[ i ];
                if ( _proteinLinkPositions[ proteinBarProteinId ] == undefined ) { continue; }
                if ( _proteinLinkPositions[ proteinBarProteinId ][ proteinBarProteinId ] == undefined ) { continue; }
                var fromProteinPositionKeys = Object.keys( _proteinLinkPositions[ proteinBarProteinId ][ proteinBarProteinId ] );
                for ( var ii = 0; ii < fromProteinPositionKeys.length; ii++ ) {
                    var fromProteinPosition = fromProteinPositionKeys[ ii ];
                    var fromProteinPositionInt =  parseInt( fromProteinPosition );
                    var toProteinPositionKeys = Object.keys( _proteinLinkPositions[ proteinBarProteinId ][ proteinBarProteinId ][ fromProteinPosition ] );
                    for ( var kk = 0; kk < toProteinPositionKeys.length; kk++ ) {
                        var toProteinPosition = toProteinPositionKeys[ kk ];
                        var toProteinPositionInt =  parseInt( toProteinPosition );
                        if ( fromProteinPositionInt > toProteinPositionInt ) {
                            //  Drawing line from right to left.  
                            //  Likely line already drawn for same positions from left to right.
                            //  Check for line already drawn between From and To positions
                            var toPositionsUsingToPositionAsFromPosition = _proteinLinkPositions[ proteinBarProteinId ][ proteinBarProteinId ][ toProteinPosition ];
                            if ( toPositionsUsingToPositionAsFromPosition !== undefined ) {
                                //  toPosition found as a from position 
                                //  so now check if the fromPosition is a to position in that sub-array
                                if ( toPositionsUsingToPositionAsFromPosition[ fromProteinPosition ] !== undefined ) {
                                    //  fromPosition is a to position in that sub-array
                                    //  so a line has already been drawn from left to right
                                    //  for these positions
                                    //  Skip drawing this line
                                    continue;  //  EARLY CONTINUE
                                }
                            }
                        }
                        var getLineColorSingleProteinBarParams = {
                            proteinIndex: i,
                            fromProteinPosition: fromProteinPositionInt,
                            toProteinPosition: toProteinPositionInt
                        };
                        var lineColor = imagePagePrimaryRootCodeObject_LocalCopy.call__getLineColorSingleProteinBar( getLineColorSingleProteinBarParams ) ;
                      
                        if ( lineColor !== _NOT_HIGHLIGHTED_LINE_COLOR_LOCAL_COPY ) {
                            const linkInfoToSave = { fromProtId : proteinBarProteinId, toProtId : proteinBarProteinId, fromPos : fromProteinPositionInt, toPos : toProteinPositionInt };
                            crosslinksResult.push( linkInfoToSave );
                        } else {
                            var y = 0;
                        }
                        var z = 0;
                    }
                }
            }
        }

        // draw looplinks, if requested
		if ( $( "input#show-looplinks" ).is( ':checked' ) ) {
            //  Copied from:  drawProteinLooplinkLines( selectedProteins, svgRootSnapSVGObject );
            for ( var i = 0; i < selectedProteins.length; i++ ) {
                var proteinBarProteinId = selectedProteins[ i ];
                if ( _proteinLooplinkPositions[ proteinBarProteinId ] == undefined ) { continue; }
                if ( _proteinLooplinkPositions[ proteinBarProteinId ][ proteinBarProteinId ] == undefined ) { continue; }
                var fromProteinPositionKeys = Object.keys( _proteinLooplinkPositions[ proteinBarProteinId ][ proteinBarProteinId ] );
                for ( var ii = 0; ii < fromProteinPositionKeys.length; ii++ ) {
                    var fromProteinPosition = fromProteinPositionKeys[ ii ];
                    var fromProteinPositionInt =  parseInt( fromProteinPosition );
                    var toProteinPositionKeys = Object.keys( _proteinLooplinkPositions[ proteinBarProteinId ][ proteinBarProteinId ][ fromProteinPosition ] );
                    for ( var kk = 0; kk < toProteinPositionKeys.length; kk++ ) {
                        var toProteinPosition = toProteinPositionKeys[ kk ];
                        var toProteinPositionInt =  parseInt( toProteinPosition );
                        var getLineColorSingleProteinBarParams = {
                                proteinIndex : i,
                                fromProteinPosition : fromProteinPositionInt,  
                                toProteinPosition : toProteinPositionInt
                        };
                        var lineColor = imagePagePrimaryRootCodeObject_LocalCopy.call__getLineColorSingleProteinBar( getLineColorSingleProteinBarParams ) ;
                      
                        if ( lineColor !== _NOT_HIGHLIGHTED_LINE_COLOR_LOCAL_COPY ) {
                            const linkInfoToSave = { protId : proteinBarProteinId, pos1 : fromProteinPositionInt, pos2 : toProteinPositionInt };
                            looplinksResult.push( linkInfoToSave );
                        } else {
                            var y = 0;
                        }
                        var z = 0;
                    }
                }
            }
        }
        		// draw monolinks, if requested
		// if ( $( "input#show-monolinks" ).is( ':checked' ) ) {
        //     //  Copied from:  drawProteinMonolinkLines( selectedProteins, svgRootSnapSVGObject );
        //     for ( var i = 0; i < selectedProteins.length; i++ ) {
        //         var proteinBarProteinId = selectedProteins[ i ];
        //         if ( _proteinMonolinkPositions[ selectedProteins[ i ] ] == undefined ) { 
        //             continue; //  skip processing this selected protein 
        //         }
        //         var positions = Object.keys( _proteinMonolinkPositions[ selectedProteins[ i ] ] );
        //         for ( var k = 0; k < positions.length; k++ ) {
        //             var proteinPosition = positions[ k ];
        //             var proteinPositionInt =  parseInt( proteinPosition );
        //             var getLineColorSingleProteinBarParams = {
        //                     proteinIndex : i,
        //                     fromProteinPosition : proteinPositionInt  
        //             };
        //             var lineColor = imagePagePrimaryRootCodeObject_LocalCopy.call__getLineColorSingleProteinBar( getLineColorSingleProteinBarParams ) ;

        //             if ( lineColor !== _NOT_HIGHLIGHTED_LINE_COLOR_LOCAL_COPY ) {
        //                 var x = 0;
        //             } else {
        //                 var y = 0;
        //             }
        //             var z = 0;
        //         }
        //     }
        // }
        
        return { selectedCrosslinks : crosslinksResult, selectedLooplinks : looplinksResult };
    }

	/////////////////////////////////////////////

	//   download calls
	
	/*
	 * Takes a filename, mimetype, and string content and initiates a file download
	 * of the content from the current page, without leaving the page. 
	 * 
	 * It is assumed jquery is loaded.
	 * 
	 */
	submitDownloadForParams({ downloadStrutsAction }) {

        const projectSearchIds = imagePagePrimaryRootCodeObject_LocalCopy.getVariable__v_projectSearchIds();

		var baseJSONObject = imagePagePrimaryRootCodeObject_LocalCopy.getNavigationJSON_Not_for_Image_Or_Structure();

        var psmPeptideCutoffsForProjectSearchIds_JSONString = JSON.stringify( baseJSONObject );

        const selectedCrosslinksLooplinksMonolinks = this.get_selectedCrosslinksLooplinksMonolinks();

        // throw Error("FORCE")

		const form = document.createElement( "form" );

		try {

			$( form ).hide();

			form.setAttribute( "method", "post" );
			form.setAttribute( "action", downloadStrutsAction );
            form.setAttribute( "target", "_blank" );
            

            for ( var i = 0; i < projectSearchIds.length; i++ ) {
                const projectSearchId = projectSearchIds[ i ];
                const field = document.createElement( "textarea" );
                field.setAttribute("name", "projectSearchId");

                $( field ).text( projectSearchId );

                form.appendChild( field );
            }
            
            {
                const queryJSONField = document.createElement( "textarea" );
                queryJSONField.setAttribute("name", "queryJSON");

                $( queryJSONField ).text( psmPeptideCutoffsForProjectSearchIds_JSONString );

                form.appendChild( queryJSONField );
            }

			if ( selectedCrosslinksLooplinksMonolinks ) {
				const selectedCrosslinksLooplinksMonolinks_JSONString = JSON.stringify( selectedCrosslinksLooplinksMonolinks );

				const selectedCrosslinksLooplinksMonolinks_StringField = document.createElement( "textarea" );
				selectedCrosslinksLooplinksMonolinks_StringField.setAttribute("name", "selectedCrosslinksLooplinksMonolinksJSON");

				$( selectedCrosslinksLooplinksMonolinks_StringField ).text( selectedCrosslinksLooplinksMonolinks_JSONString );

				form.appendChild( selectedCrosslinksLooplinksMonolinks_StringField );
			}

			document.body.appendChild(form);    // Not entirely sure if this is necessary			

			form.submit();

		} finally {

			document.body.removeChild( form );
		}
	};
	

}



/**
 * Called from "crosslink-image-viewer.js" to populate local copy of imagePagePrimaryRootCodeObject_LocalCopy
 */
var downloadProteins_pass_imagePagePrimaryRootCodeObject = function( imagePagePrimaryRootCodeObject_LocalCopy_Param ) {
	imagePagePrimaryRootCodeObject_LocalCopy = imagePagePrimaryRootCodeObject_LocalCopy_Param;
}


export { downloadProteins_pass_imagePagePrimaryRootCodeObject }
