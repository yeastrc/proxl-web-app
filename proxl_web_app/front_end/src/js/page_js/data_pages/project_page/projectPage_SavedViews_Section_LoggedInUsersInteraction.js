/**
 * projectPage_SavedViews_Section_LoggedInUsersInteraction.js
 * 
 * Javascript for projectView.jsp page  
 * 
 * Saved Views Section - Provide interaction for Logged In Users
 */

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

//  module import 

//  Import Handlebars templates

const _project_page__saved_views_section_loggedin_users_template = require("../../../../../handlebars_templates_precompiled/project_page__saved_views_section_loggedin_users/project_page__saved_views_section_loggedin_users_template-bundle.js");

import { reportWebErrorToServer } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/reportWebErrorToServer.js';

import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

//  Local imports

/**
 * 
 */
export class ProjectPage_SavedViews_Section_LoggedInUsersInteraction {

	/**
	 * 
	 */
	constructor({ 
		projectIdString
	}) {

		this._initializeCalled = false;

		this._projectIdString = projectIdString;

		if (!_project_page__saved_views_section_loggedin_users_template.projpg_saved_view_list_item_edit_label) {
			throw Error("Nothing in _project_page__saved_views_section_loggedin_users_template.projpg_saved_view_list_item_edit_label");
		}
		this._projpg_saved_view_list_item_edit_label_template = _project_page__saved_views_section_loggedin_users_template.projpg_saved_view_list_item_edit_label;
		
	}

	/**
	 * 
	 */
	initialize({ projectPage_SavedViews_Section_AllUsersInteraction }) {
        
        this._projectPage_SavedViews_Section_AllUsersInteraction = projectPage_SavedViews_Section_AllUsersInteraction;

		this._initializeCalled = true;
	};

	/**
	 * 
	 */
    add_ClickHandlers({ $saved_view_entry, savedViewItem }) {
        
        let objectThis = this;
        
        const id = savedViewItem.id;
		const canEdit = savedViewItem.canEdit;
        const canDelete = savedViewItem.canDelete;
        
        if ( canEdit ) {

            const $selector_edit_saved_view_label = $saved_view_entry.find(".selector_edit_saved_view_label");
            if ( $selector_edit_saved_view_label.length === 0 ) {
                console.log("WARN: No DOM element found with class 'selector_edit_saved_view_label'");
            }
            $selector_edit_saved_view_label.click(function(eventObject) {
                try {
                    event.preventDefault(); // to stop the 
                    let clickThis = this;
                    objectThis._editSavedViewLabel_Clicked({
                        clickThis ,
                        id
                    });
                } catch (e) {
                    reportWebErrorToServer.reportErrorObjectToServer({
                        errorException : e
                    });
                    throw e;
                }
            });
        }

        if ( canDelete ) {

            const $selector_delete_saved_view = $saved_view_entry.find(".selector_delete_saved_view");
            if ( $selector_delete_saved_view.length === 0 ) {
                console.log("WARN: No DOM element found with class 'selector_delete_saved_view'");
            }
            $selector_delete_saved_view.click(function(eventObject) {
                try {
                    event.preventDefault(); // to stop the 
                    let clickThis = this;
                    objectThis._deleteSavedView_Clicked({
                        clickThis ,
                        id
                    });
                } catch (e) {
                    reportWebErrorToServer.reportErrorObjectToServer({
                        errorException : e
                    });
                    throw e;
                }
            });
        }
    }

	/**
	 * 
	 */
    _editSavedViewLabel_Clicked({ clickThis, id }) {

        const objectThis = this;

        const $clickThis = $( clickThis );

        const $selector_saved_view_item_display_container = $clickThis.closest(".selector_saved_view_item_display_container");
        if ( $selector_saved_view_item_display_container.length === 0 ) {
            console.log("WARN: No DOM element found with class 'selector_saved_view_item_display_container'");
        }
        $selector_saved_view_item_display_container.hide();

        //  Get current label text
        const $selector_saved_view_item_label = $selector_saved_view_item_display_container.find(".selector_saved_view_item_label");
        if ( $selector_saved_view_item_label.length === 0 ) {
            console.log("WARN: No DOM element found with class 'selector_saved_view_item_label'");
        }
        const labelText = $selector_saved_view_item_label.text();

        //  Add edit HTML and update label text

        const $selector_saved_view_item_root_container = $clickThis.closest(".selector_saved_view_item_root_container");
        if ( $selector_saved_view_item_root_container.length === 0 ) {
            console.log("WARN: No DOM element found with class 'selector_saved_view_item_root_container'");
        }
        
        const editHTML = this._projpg_saved_view_list_item_edit_label_template();
        const $editElement = $( editHTML );
        $selector_saved_view_item_root_container.append( $editElement );

        const $selector_saved_view_item_edit_label_input = $editElement.find(".selector_saved_view_item_edit_label_input");
        if ( $selector_saved_view_item_edit_label_input.length === 0 ) {
            console.log("WARN: No DOM element found with class 'selector_saved_view_item_edit_label_input'");
        }
        $selector_saved_view_item_edit_label_input.val( labelText );

        //  Add click handlers
        const $selector_saved_view_item_edit_label_cancel_button = $editElement.find(".selector_saved_view_item_edit_label_cancel_button");
        if ( $selector_saved_view_item_edit_label_cancel_button.length === 0 ) {
            console.log("WARN: No DOM element found with class 'selector_saved_view_item_edit_label_cancel_button'");
        }
        $selector_saved_view_item_edit_label_cancel_button.click(function(eventObject) {
            try {
                event.preventDefault(); // to stop the 
                let clickThis = this;
                objectThis._cancelLabelChange_Clicked({ clickThis });
            } catch (e) {
                reportWebErrorToServer.reportErrorObjectToServer({
                    errorException : e
                });
                throw e;
            }
        });

        const $selector_saved_view_item_edit_label_save_button = $editElement.find(".selector_saved_view_item_edit_label_save_button");
        if ( $selector_saved_view_item_edit_label_save_button.length === 0 ) {
            console.log("WARN: No DOM element found with class 'selector_saved_view_item_edit_label_save_button'");
        }
        $selector_saved_view_item_edit_label_save_button.click(function(eventObject) {
            try {
                event.preventDefault(); // to stop the 
                let clickThis = this;
                objectThis._saveLabelChange_Clicked({ clickThis, id });
            } catch (e) {
                reportWebErrorToServer.reportErrorObjectToServer({
                    errorException : e
                });
                throw e;
            }
        });
    }

	/**
	 * 
	 */
    _saveLabelChange_Clicked({ clickThis, id }) {

        const objectThis = this;

        const $selector_saved_view_item_edit_label_container = $( clickThis ).closest(".selector_saved_view_item_edit_label_container");
        if ( $selector_saved_view_item_edit_label_container.length === 0 ) {
            throw Error("No DOM element with class 'selector_saved_view_item_edit_label_container'");
        }
        const $selector_saved_view_item_edit_label_input = $selector_saved_view_item_edit_label_container.find(".selector_saved_view_item_edit_label_input");
        if ( $selector_saved_view_item_edit_label_input.length === 0 ) {
            console.log("WARN: No DOM element found with class 'selector_saved_view_item_edit_label_input'");
        }
        
        const labelText = $selector_saved_view_item_edit_label_input.val();

        if ( labelText === "" ) {

            //  No value so exit
            return; // EARLY EXIT
        }

        const promise_changeLabel_SavedView_OnServer = this._changeLabel_SavedView_OnServer( { labelText, id } );

        promise_changeLabel_SavedView_OnServer.catch((reason) => {});

        promise_changeLabel_SavedView_OnServer.then((result) => {
            try {
                objectThis._saveLabelChange_UpdatePageAfterUpdateServer({ clickThis, labelText, id });
            } catch( e ) {
                reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                throw e;
            }
        })
    }

	/**
	 * 
	 */
    _saveLabelChange_UpdatePageAfterUpdateServer({ clickThis, labelText, id }) {

        const $selector_saved_view_item_root_container = $( clickThis ).closest(".selector_saved_view_item_root_container");
        if ( $selector_saved_view_item_root_container.length === 0 ) {
            throw Error("No DOM element with class 'selector_saved_view_item_root_container'");
        }

        //  Update label text on page
        const $selector_saved_view_item_label = $selector_saved_view_item_root_container.find(".selector_saved_view_item_label");
        if ( $selector_saved_view_item_label.length === 0 ) {
            console.log("WARN: No DOM element found with class 'selector_saved_view_item_label'");
        }
        $selector_saved_view_item_label.text( labelText );

        this._removeLabelChangeElement_ShowMainContainer({ clickThis });
    }

	/**
	 * 
	 */  
    _changeLabel_SavedView_OnServer( { labelText, id } ) {
    
        return new Promise(function(resolve, reject) {
          try {
            let requestObj = {
                labelText,
                id
            };

			const url = "services/savedView/changeLabel";

            const webserviceCallStandardPostResponse = webserviceCallStandardPost({ dataToSend : requestObj, url }) ;

            const promise_webserviceCallStandardPost = webserviceCallStandardPostResponse.promise;

			promise_webserviceCallStandardPost.catch( () => { reject() }  );

			promise_webserviceCallStandardPost.then( ({ responseData }) => {
                try {
                    resolve( responseData );
                } catch (e) {
                    reportWebErrorToServer.reportErrorObjectToServer({ errorException: e });
                    throw e;
                }
            });
          } catch( e ) {
            reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
            throw e;
          }
        });
    };
    
    /**
	 * 
	 */
    _cancelLabelChange_Clicked({ clickThis }) {

        this._removeLabelChangeElement_ShowMainContainer({ clickThis });
    }

    /**
	 * 
	 */
    _removeLabelChangeElement_ShowMainContainer({ clickThis }) {

        const $clickThis = $( clickThis );

        const $selector_saved_view_item_root_container = $clickThis.closest(".selector_saved_view_item_root_container");
        if ( $selector_saved_view_item_root_container.length === 0 ) {
            console.log("WARN: No DOM element found with class 'selector_saved_view_item_root_container'");
        }
        
        const $selector_saved_view_item_display_container = $selector_saved_view_item_root_container.find(".selector_saved_view_item_display_container");
        if ( $selector_saved_view_item_display_container.length === 0 ) {
            console.log("WARN: No DOM element found with class 'selector_saved_view_item_display_container'");
        }
        $selector_saved_view_item_display_container.show();

        const $selector_saved_view_item_edit_label_container = $clickThis.closest(".selector_saved_view_item_edit_label_container");
        if ( $selector_saved_view_item_edit_label_container.length === 0 ) {
            console.log("WARN: No DOM element found with class 'selector_saved_view_item_edit_label_container'");
        }
        $selector_saved_view_item_edit_label_container.remove();

    }

    ////////////////////////////////////////

    //   Delete

	/**
	 * 
	 */
    _deleteSavedView_Clicked({ clickThis, id }) {

        if ( ! window.confirm("Delete Saved View?") ) {
            return;  // EARLY EXIT
        }

        const promise_deleteSavedView_OnServer = this._deleteSavedView_OnServer( { id } );

        promise_deleteSavedView_OnServer.catch((reason) => {});

        promise_deleteSavedView_OnServer.then((result) => {
            try {
                const $selector_saved_view_item_root_container = $( clickThis ).closest(".selector_saved_view_item_root_container");
                if ( $selector_saved_view_item_root_container.length === 0 ) {
                    throw Error("No DOM element with class 'selector_saved_view_item_root_container'");
                }
                $selector_saved_view_item_root_container.remove();
            } catch( e ) {
                reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                throw e;
            }
        })
    }

	/**
	 * 
	 */  
    _deleteSavedView_OnServer( { id } ) {
    
        return new Promise(function(resolve, reject) {
          try {
            let requestObj = {
                id
            };

			const url = "services/savedView/delete";

            const webserviceCallStandardPostResponse = webserviceCallStandardPost({ dataToSend : requestObj, url }) ;

            const promise_webserviceCallStandardPost = webserviceCallStandardPostResponse.promise;

			promise_webserviceCallStandardPost.catch( () => { reject() }  );

			promise_webserviceCallStandardPost.then( ({ responseData }) => {
                try {
                    resolve( responseData );
                } catch (e) {
                    reportWebErrorToServer.reportErrorObjectToServer({ errorException: e });
                    throw e;
                }
            });
          } catch( e ) {
            reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
            throw e;
          }
        });
    };
    
}
