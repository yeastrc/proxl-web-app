/**
 * projectPage_Root_ProjectLocked_ProjectOwnerUser.js
 * 
 * Javascript for projectView.jsp page  
 * 
 * Root JS file for Project Owner Users when the Project is Locked
 * 
 * 
 */


/**
 * Always do in Root Javascript for page:
 */


//  Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/header_section_js_all_pages_main_pages/header_section_main_pages/header_main.js';


 

//  Local Imports

//  Import so get included.  Have their own initializer and $(document).ready( 

import { initViewProjectPage } from './viewProjectPage.js';

import { viewProjectPage_loggedInUser } from './viewProjectPage_loggedInUser.js';

import { viewProject_ProjectAdminSection } from './viewProject_ProjectAdminSection.js';

import { viewProject_ProjectLockAdmin } from './viewProject_ProjectLockAdmin.js';
