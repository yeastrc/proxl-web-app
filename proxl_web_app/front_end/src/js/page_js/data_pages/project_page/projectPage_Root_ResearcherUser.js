/**
 * projectPage_Root_ResearcherUser.js
 * 
 * Javascript for projectView.jsp page  
 * 
 * Root JS file for Researcher Users 
 * 
 * 
 */


/**
 * Always do in Root Javascript for page:
 */

 
//  Import header_main.js and children to ensure on the page
import { header_mainVariable } from 'page_js/common_all_pages/header_section_main_pages/header_main.js';



//  Local Imports

//  Import so get included.  Have their own initializer and $(document).ready( 

import { initViewProjectPage } from './viewProjectPage.js';

import { viewProjectPage_loggedInUser } from './viewProjectPage_loggedInUser.js';

import { proxlXMLFileImportStatusDisplay } from './proxlXMLFileImportStatusDisplay.js';

import { viewProject_ProjectAdminSection } from './viewProject_ProjectAdminSection.js';
import { viewProject_SearchMaint } from './viewProject_SearchMaint.js';
