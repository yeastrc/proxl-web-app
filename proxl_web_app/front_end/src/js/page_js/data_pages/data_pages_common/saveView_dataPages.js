/**
 * saveView_dataPages.js
 * 
 * Javascript for Data Pages: Save the current view/URL.  Optionally for single search, set as default
 * 
 */


let Handlebars = require('handlebars/runtime');

let _save_view_template_bundle = require("../../../../../handlebars_templates_precompiled/save_view/save_view_template-bundle.js");

import { reportWebErrorToServer } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/reportWebErrorToServer.js';

import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';



/**
 * 
 */
export class SaveView_dataPages {

	/**
	 * 
	 */
	constructor() {

        //  NOT USED:
        // if (!_save_view_template_bundle.save_view__on_main_page_root) {
		// 	throw Error("Nothing in _save_view_template_bundle.save_view__on_main_page_root");
		// }
        // this._save_view__on_main_page_root_Template = _save_view_template_bundle.save_view__on_main_page_root;
        
        if (!_save_view_template_bundle.save_view_overlay_main) {
			throw Error("Nothing in _save_view_template_bundle.save_view_overlay_main");
		}
        this._save_view_overlay_main_Template = _save_view_template_bundle.save_view_overlay_main;
        
        if (!_save_view_template_bundle.save_view_overlay_background) {
			throw Error("Nothing in _save_view_template_bundle.save_view_overlay_background");
		}
        this._save_view_overlay_background_Template = _save_view_template_bundle.save_view_overlay_background;
        
        
    }

	/**
	 * 
	 */
	initialize( /* { projectSearchIds, container_DOM_Element, enableSetDefault } */ ) {

        const objectThis = this;

        // this._projectSearchIds = projectSearchIds;


		const projectSearchIdsLocal = [];

        const $project_search_id_jqAll = $(".project_search_id_jq");
        if ( $project_search_id_jqAll.length === 0 ) {
            throw Error( "No DOM elements found with class 'project_search_id_jq' " );
        }
		$project_search_id_jqAll.each( ( index, element ) => {

			const $element = $( element );
			const projectSearchIdString = $element.val();
			const projectSearchId = Number.parseInt( projectSearchIdString );
			if ( Number.isNaN( projectSearchId ) ) {
				throw Error("value in DOM element with class 'project_search_id_jq' is not a number");
			}
			projectSearchIdsLocal.push( projectSearchId );
        } );
        
        this._projectSearchIds = projectSearchIdsLocal;

        let enableSetDefault = undefined;

        if ( this._projectSearchIds.length === 1 ) {
            enableSetDefault = true;
        } else {
            enableSetDefault = false;
        }

        if ( enableSetDefault !== false ) {
            //  this._enableSetDefault - Present to user the "Set Default" Checkbox and pass to server the value
            this._enableSetDefault = true;  // Default to true if method param is undefined (not set)
        }

        //  Use Button on page instead of Handlebars template so skip following code:

        // //  Populate Button for Save View

        // let $saveViewButtonContainer = undefined;

        // if ( container_DOM_Element ) {
        //     // Use Reference to DOM element if provided

        //     $saveViewButtonContainer = $( container_DOM_Element );
        // } else {
        //     $saveViewButtonContainer = $(".selector_save_view_root_container");
        //     if ( $saveViewButtonContainer.length === 0 ) {
        //         console.log("Unable to initialize class SaveView_dataPages.  No DOM element with class 'selector_save_view_root_container'");
        //         return;
        //     }
        // }

        if ( ! this._enableSetDefault ) {
            this._canSetDefault = false; // Override to false if param enableSetDefault is set to false
        } else {
            
            //  Get is user Project Owner
            const $page_auth_access_level_project_owner_allowed = $("#page_auth_access_level_project_owner_allowed");
            if ( $page_auth_access_level_project_owner_allowed.length !== 0 ) {
                this._userIsProjectOwner = true;
            }

            this._canSetDefault = this._userIsProjectOwner && this._projectSearchIds.length === 1;
        }

        // const saveViewMainPageHTML = this._save_view__on_main_page_root_Template();
        // $saveViewButtonContainer.append( saveViewMainPageHTML );

        // const $selector_save_view_button = $saveViewButtonContainer.find(".selector_save_view_button");

        // $selector_save_view_button.click( function(eventObject) {
		// 	try {
		// 		eventObject.preventDefault();
        //         objectThis._saveView_MainPage_ButtonClicked();
        //         return false;
		// 	} catch( e ) {
		// 		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		// 		throw e;
		// 	}
		// });
    }


	//  Called from "onchange" and from CutoffProcessingCommonCode code when cutoffs change
	searchFormChanged_ForDefaultPageView() {
		$("#savePageViewButton").prop("disabled",true);
		$("#savePageViewButtonDisabledOverlay").show();
	};
	
	//  Called from Image and Structure when the Update button has been pressed
	searchFormUpdateButtonPressed_ForDefaultPageView() {
		console.log( "searchFormUpdateButtonPressed_ForDefaultPageView()" );
		$("#savePageViewButton").prop("disabled",false);
		$("#savePageViewButtonDisabledOverlay").hide();
	};
	
	/**
     * Called from 'Save View' button
     * 
	 * Show Modal Dialog
	 */
	savePageView( params ) {
		try {
			var objectThis = this;
			if ( ! params ) {
				console.log( "params cannot be empty" );
				throw Error( "saveOrUpdateDefaultPageView(params): params cannot be empty" ); 
			}
			
			var clickedThis = params.clickedThis;
			var projectSearchId = params.searchId;
			
			if ( ! clickedThis ) {
				console.log( "clickedThis cannot be empty" );
				throw Error( "saveOrUpdateDefaultPageView(params): clickedThis cannot be empty" ); 
			}
			var $clickedThis = $( clickedThis );
			var page_JS_Object = params.page_JS_Object;
			if ( ! page_JS_Object ) {
				console.log( "page_JS_Object cannot be empty" );
				throw Error( "saveOrUpdateDefaultPageView(params): page_JS_Object cannot be empty" ); 
			}
			if ( ! page_JS_Object.getQueryJSONString ) {
				console.log( "page_JS_Object.getQueryJSONString must exist and must be a function" );
				throw Error( "saveOrUpdateDefaultPageView(params): page_JS_Object.getQueryJSONString must exist and must be a function" ); 
            }
            
			var pageQueryJSONString = null;
			try {
				pageQueryJSONString = page_JS_Object.getQueryJSONString();
			} catch( e ) {
				console.log( "calling page_JS_Object.getQueryJSON() throw an exception" );
				throw Error( "saveOrUpdateDefaultPageView(params): calling page_JS_Object.getQueryJSON() throw an exception" ); 
            }


            const $body = $( "body" ); // Attach to <body>

            const backgroundHTML = this._save_view_overlay_background_Template();
            const $backgroundDOM = $( backgroundHTML ).appendTo( $body );
            $backgroundDOM.click( (eventObject) => {
                try {
                    eventObject.preventDefault();
                    objectThis._hide_remove_ModalOverlay();
                    return false;
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });
    
            const modalDialogHTML = this._save_view_overlay_main_Template( { canSetDefault : this._canSetDefault } );
            const $modalDialogDOM = $( modalDialogHTML ).appendTo( $body );
            
            const $selector_save_view_button = $modalDialogDOM.find(".selector_save_view_button");
            if ( $selector_save_view_button.length === 0 ) {
                throw Error("No element with class 'selector_save_view_button'");
            }
            $selector_save_view_button.click( (eventObject) => {
                try {
                    eventObject.preventDefault();
                    objectThis._save_view_button_Overlay_Click({ eventObject, page_JS_Object, pageQueryJSONString });
                    return false;
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });

            //  Click handler on 'X' in upper right to close
            const $selector_save_view_modal_dialog_x_exit = $modalDialogDOM.find(".selector_save_view_modal_dialog_x_exit");
            if ( $selector_save_view_modal_dialog_x_exit.length === 0 ) {
                throw Error("No element with class 'selector_save_view_modal_dialog_x_exit'");
            }
            $selector_save_view_modal_dialog_x_exit.click( (eventObject) => {
                try {
                    eventObject.preventDefault();
                    objectThis._hide_remove_ModalOverlay();
                    return false;
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });

            //  Click handler on Cancel button to close
            const $selector_save_view_cancel_button = $modalDialogDOM.find(".selector_save_view_cancel_button");
            if ( $selector_save_view_cancel_button.length === 0 ) {
                throw Error("No element with class 'selector_save_view_cancel_button'");
            }
            $selector_save_view_cancel_button.click( (eventObject) => {
                try {
                    eventObject.preventDefault();
                    objectThis._hide_remove_ModalOverlay();
                    return false;
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });

        } catch( e ) {
			reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
			throw e;
		}
	};
	

	/**
	 * 
	 */
	_hide_remove_ModalOverlay() {
    
        const $selector_save_view_modal_dialog = $(".selector_save_view_modal_dialog");
        $selector_save_view_modal_dialog.remove();

        const $selector_save_view_modal_dialog_background = $(".selector_save_view_modal_dialog_background");
        $selector_save_view_modal_dialog_background.remove();
	}
	
	/**
	 * 
	 */
	_cancel_button_Click() {
		
		this._hide_remove_ModalOverlay();
	}
	
	/**
	 * 
	 */
    _save_view_button_Overlay_Click({ eventObject, page_JS_Object, pageQueryJSONString }) {

        //  view label

        const eventObjectTarget = eventObject.target;

        const $eventObjectTarget = $( eventObjectTarget );
        const $selector_save_view_modal_dialog_body = $eventObjectTarget.closest(".selector_save_view_modal_dialog_body");
        if ( $selector_save_view_modal_dialog_body.length === 0 ) {
            throw Error("No DOM element with class 'selector_save_view_modal_dialog_body'");
        }

        //  Set Default is no longer an option

        // let setDefault = false;
        // if ( this._canSetDefault ) {
        //     const $selector_save_view_as_default_checkbox = $selector_save_view_modal_dialog_body.find(".selector_save_view_as_default_checkbox");
        //     if ( $selector_save_view_as_default_checkbox.length === 0 ) {
        //         throw Error("No element with class 'selector_save_view_as_default_checkbox'");
        //     }
        //     setDefault = $selector_save_view_as_default_checkbox.prop( "checked" );
        // }

        const $selector_save_view_label = $selector_save_view_modal_dialog_body.find(".selector_save_view_label");
        if ( $selector_save_view_label.length === 0 ) {
            throw Error("No DOM element with class 'selector_save_view_label'");
        }
        const viewLabel = $selector_save_view_label.val();

        if ( viewLabel === "" ) {
            //  No Label provided for view
            return;  // EARLY EXIT
        }

        var pageCurrentURL = window.location.href;


        const promise__saveViewToServer = this._saveViewToServer({ viewLabel, pageCurrentURL, pageQueryJSONString, projectSearchIds : this._projectSearchIds })

        promise__saveViewToServer.catch( () => {  });

        promise__saveViewToServer.then( (  ) => {
            try {
                this._hide_remove_ModalOverlay();
            } catch( e ) {
                reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                throw e;
            }
        });
    }

	/**
     * Save the view to the server
	 */
	_saveViewToServer( { viewLabel, pageCurrentURL, pageQueryJSONString, projectSearchIds } ) {

		let promise = new Promise( function( resolve, reject ) {
            try {
                let requestObject = {
                        projectSearchIds,
                        viewLabel : viewLabel,
                        pageCurrentURL,
                        pageQueryJSON : pageQueryJSONString
                };

                const url = "services/savedView/saveView";

                const webserviceCallStandardPostResponse = webserviceCallStandardPost({ dataToSend : requestObject, url }) ;

                const promise_webserviceCallStandardPost = webserviceCallStandardPostResponse.promise;
                
                promise_webserviceCallStandardPost.catch( () => { reject() }  );

                promise_webserviceCallStandardPost.then( ({ responseData }) => {
                    try {
                        resolve();

                    } catch( e ) {
                        reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                        throw e;
                    }
                });
            } catch( e ) {
                reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                throw e;
            }
		});
		
		return promise;
	};

}

