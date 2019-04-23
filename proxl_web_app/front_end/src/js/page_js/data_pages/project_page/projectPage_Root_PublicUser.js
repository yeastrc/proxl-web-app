/**
 * projectPage_Root_PublicUser.js
 * 
 * Javascript for projectView.jsp page  
 * 
 * Root JS file for Public Users 
 * 
 * 
 */


/**
 * Always do in Root Javascript for page:
 */



//  Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js';

import { ProjectPage_SavedViews_Section_AllUsersInteraction } from './projectPage_SavedViews_Section_AllUsersInteraction.js';


//  Local Imports


//  Import so get included.  Have their own initializer and $(document).ready( 

import { initViewProjectPage } from './viewProjectPage.js';


///////////////
$(document).ready(function() {
	try {
        // <input type="hidden" id="project_id" value="1" />

		var $project_id = $("#project_id");
		if ($project_id.length === 0) {
			throw Error("projectPage_Root_ProjectLocked_ResearcherUser.js: No DOM element with id 'project_id'");
        }
        const projectIdString = $project_id.val();
		if ( projectIdString === undefined || projectIdString === "" ) {
			throw Error("projectPage_Root_ProjectLocked_ResearcherUser.js: Value in DOM element with id 'project_id' is undefined or empty string");
        }

		const projectPage_SavedViews_Section_AllUsersInteraction =
			new ProjectPage_SavedViews_Section_AllUsersInteraction({ 
				projectIdString });
        
        projectPage_SavedViews_Section_AllUsersInteraction.initialize();

        projectPage_SavedViews_Section_AllUsersInteraction.getSavedViewsData();

	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});
