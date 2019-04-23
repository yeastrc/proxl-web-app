/**
 * projectPage_SavedViews_Section_AllUsersInteraction.js
 * 
 * Javascript for projectView.jsp page  
 * 
 * Saved Views Section - Provide interaction for All Users (including public users when project is public) 
 * 
 * 
 */

//////////////////////////////////
// JavaScript directive:   all variables have to be declared with "var", maybe other things
"use strict";

///////////////////////////////////////////

//  module import 

//  Import Handlebars templates

const _project_page__saved_views_section_template = require("../../../../../handlebars_templates_precompiled/project_page__saved_views_section/project_page__saved_views_section_template-bundle.js");

import { reportWebErrorToServer } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/reportWebErrorToServer.js';

import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

//  Local imports

/**
 * 
 */
export class ProjectPage_SavedViews_Section_AllUsersInteraction {

	/**
	 * 
	 */
	constructor({ 
		projectIdString,
		projectPage_SavedViews_Section_LoggedInUsersInteraction
	}) {

		this._initializeCalled = false;

		this._projectIdString = projectIdString;
		this._projectPage_SavedViews_Section_LoggedInUsersInteraction = projectPage_SavedViews_Section_LoggedInUsersInteraction;

		if (!_project_page__saved_views_section_template.projpg_saved_view_list_container) {
			throw Error("Nothing in _project_page__saved_views_section_template.projpg_saved_view_list_container");
		}
		this._projpg_saved_view_list_container_template = _project_page__saved_views_section_template.projpg_saved_view_list_container;
		
		if (!_project_page__saved_views_section_template.projpg_saved_view_list_item) {
			throw Error("Nothing in _project_page__saved_views_section_template.projpg_saved_view_list_item");
		}
		this._projpg_saved_view_list_item_template = _project_page__saved_views_section_template.projpg_saved_view_list_item;
		
	}

	/**
	 * 
	 */
	initialize() {
		let objectThis = this;

		this._initializeCalled = true;
	};

	/**
	 * 
	 */
	getSavedViewsData() {

		if (!this._initializeCalled) {
			throw Error("initialize method not called");
		}

		let objectThis = this;

		let projectId = Number.parseInt( this._projectIdString );
		if ( Number.isNaN( projectId ) ) {
			throw Error("projectIdString is not a number: " + projectIdString );
		}

		let requestObj = {
			projectId
		};

		const url = "services/savedView/listSavedViews";

		const webserviceCallStandardPostResponse = webserviceCallStandardPost({ dataToSend : requestObj, url }) ;

		const promise_webserviceCallStandardPost = webserviceCallStandardPostResponse.promise;

		promise_webserviceCallStandardPost.catch( () => { }  );

		promise_webserviceCallStandardPost.then( ({ responseData }) => {
			try {
				objectThis._getSavedViewsDataResponse(responseData);
			} catch (e) {
				reportWebErrorToServer.reportErrorObjectToServer({
					errorException : e
				});
				throw e;
			}
		});
	};

	/**
	 * 
	 */
	_getSavedViewsDataResponse(responseData) {

		if (!this._initializeCalled) {
			throw Error("initialize method not called");
		}

		let savedViewList = responseData.savedViewList;

		let $saved_views_list = $("#saved_views_list");
		if ( $saved_views_list.length === 0 ) {
			throw Error("No DOM element with id 'saved_views_list'");
		}
		$saved_views_list.empty();

		if ( (!savedViewList) || savedViewList.length === 0 ) {
			//  No savedViewList for identifier

			const $saved_views_no_entries = $("#saved_views_no_entries");
			if ( $saved_views_no_entries.length === 0 ) {
				throw Error("No DOM element with id 'saved_views_no_entries'");
			}
			$saved_views_no_entries.show();

			return; // EARY EXIT
		}

		const $saved_views_no_entries = $("#saved_views_no_entries");
		if ( $saved_views_no_entries.length === 0 ) {
			throw Error("No DOM element with id 'saved_views_no_entries'");
		}
		$saved_views_no_entries.hide();

		//  Add inner Saved Views list container

		let canSelectSearches = false;

		//  For now, only select searches when Searches Admin Object provided
		if ( this._projectPage_SearchesAdmin ) {
			canSelectSearches = true;
		}

		const searchesContainerContext = { canSelectSearches };

		const _projpg_saved_view_list_containerHTML = this._projpg_saved_view_list_container_template(searchesContainerContext);
		const $_projpg_saved_view_list_container = $( _projpg_saved_view_list_containerHTML );
		$_projpg_saved_view_list_container.appendTo( $saved_views_list );

		// //  Sort on search id in reverse order
		// savedViewList.sort(function(a, b) {
		// 	if (a.searchId < b.searchId) {
		// 		return 1;
		// 	}
		// 	if (a.searchId > b.searchId) {
		// 		return -1;
		// 	}
		// 	return 0;
		// })

		for ( const savedViewItem of savedViewList ) {

			let canEdit = savedViewItem.canEdit;
			let canDelete = savedViewItem.canDelete;

			if ( ! this._projectPage_SavedViews_Section_LoggedInUsersInteraction ) {
				canEdit = false;
				canDelete = false;
			}

			let context = {
				savedViewItem : savedViewItem,
				canEdit,
				canDelete
			};

			const savedViewEntry_HTML = this._projpg_saved_view_list_item_template(context);

			const $saved_view_entry = $(savedViewEntry_HTML);

			$saved_view_entry.appendTo( $_projpg_saved_view_list_container );

			this._addSavedViewItem_ClickHandlers({ $saved_view_entry, savedViewItem });

		}

		//			addToolTips();

		// if ( this._projectPage_SearchesSection_LoggedInUsersInteraction ) {
		// 	//  have _projectPage_SearchesSection_LoggedInUsersInteraction object so call method on it
		// 	this._projectPage_SearchesSection_LoggedInUsersInteraction.savedViewListPopulated();
		// }

		// if ( this._projectPage_SearchesAdmin ) {
		// 	// only when Searches Admin Object provided
		// 	this._projectPage_SearchesAdmin.savedViewListPopulated();
		// }

	};

	/**
	 * for HTML in single_search_expansion_icon_template.handlebars
	 */
	_addSearch_ShowHideBlock_ClickHandlers({ $expansion_entry, savedViewItem }) {

		this._projectPage_SearchDetails_AllUsers.addSearch_ShowHideBlock_ClickHandlers({ $expansion_entry, savedViewItem });
	}

	/**
	 * 
	 */
	_addSavedViewItem_ClickHandlers({ $saved_view_entry, savedViewItem }) {

		const objectThis = this;

		if ( ! this._projectPage_SavedViews_Section_LoggedInUsersInteraction ) {

			// No LoggedInUser so exit

			return;  // EARLY EXIT
		}

		//  Only for Logged In Users

		this._projectPage_SavedViews_Section_LoggedInUsersInteraction.add_ClickHandlers({ $saved_view_entry, savedViewItem });

	}

}
