/**
 * projectPage_Root_ProjectOwnerUser.js
 * 
 * Javascript for projectView.jsp page  
 * 
 * Root JS file for Project Owner Users 
 * 
 * 
 */

 

//  Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js';



//  Local Imports

//  Import so get included.  Have their own initializer and $(document).ready( 

import { initViewProjectPage } from './viewProjectPage.js';

import { viewProjectPage_loggedInUser } from './viewProjectPage_loggedInUser.js';

import { proxlXMLFileImport } from './proxlXMLFileImport.js';
import { proxlXMLFileImportStatusDisplay } from './proxlXMLFileImportStatusDisplay.js';
import { proxlXMLFileImportUserUpdates } from './proxlXMLFileImportUserUpdates.js';

import { viewProject_ProjectAdminSection, set_projectPage_UserCustomProjectLabel_ProjectOwnerInteraction } from './viewProject_ProjectAdminSection.js';
import { organizeSearches } from './viewProject_OrganizeSearchesAndFoldersAdmin.js';
import { viewProject_ProjectLockAdmin } from './viewProject_ProjectLockAdmin.js';
import { viewProject_SearchMaint } from './viewProject_SearchMaint.js';


import { ProjectPage_UserCustomProjectLabel_ProjectOwnerInteraction } from './projectPage_UserCustomProjectLabel.js';
import { ProjectPage_SavedViews_Section_AllUsersInteraction } from './projectPage_SavedViews_Section_AllUsersInteraction.js';
import { ProjectPage_SavedViews_Section_LoggedInUsersInteraction } from './projectPage_SavedViews_Section_LoggedInUsersInteraction.js';

///////////////
$(document).ready(function() {
	try {
        // <input type="hidden" id="project_id" value="1" />

		var $project_id = $("#project_id");
		if ($project_id.length === 0) {
			throw Error("projectPage_Root_ProjectOwnerUser.js: No DOM element with id 'project_id'");
        }
        const projectIdString = $project_id.val();
		if ( projectIdString === undefined || projectIdString === "" ) {
			throw Error("projectPage_Root_ProjectOwnerUser.js: Value in DOM element with id 'project_id' is undefined or empty string");
        }

        const userIsProjectOwner = true;
        const projectLocked = false;

        const projectPage_UserCustomProjectLabel_ProjectOwnerInteraction =
            new ProjectPage_UserCustomProjectLabel_ProjectOwnerInteraction( { 
                projectIdString, userIsProjectOwner, projectLocked } );

        set_projectPage_UserCustomProjectLabel_ProjectOwnerInteraction( projectPage_UserCustomProjectLabel_ProjectOwnerInteraction );


        const projectPage_SavedViews_Section_LoggedInUsersInteraction = new ProjectPage_SavedViews_Section_LoggedInUsersInteraction({ projectIdString });

		const projectPage_SavedViews_Section_AllUsersInteraction =
			new ProjectPage_SavedViews_Section_AllUsersInteraction({ 
				projectIdString,
				projectPage_SavedViews_Section_LoggedInUsersInteraction });
        
        projectPage_SavedViews_Section_LoggedInUsersInteraction.initialize({ projectPage_SavedViews_Section_AllUsersInteraction });

        projectPage_SavedViews_Section_AllUsersInteraction.initialize();

        projectPage_SavedViews_Section_AllUsersInteraction.getSavedViewsData();

	} catch( e ) {
		reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
		throw e;
	}
});
