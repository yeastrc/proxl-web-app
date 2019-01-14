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
import { header_mainVariable } from 'page_js/common_all_pages/header_section_main_pages/header_main.js';



//  Local Imports

//  Import so get included.  Have their own initializer and $(document).ready( 

import { initViewProjectPage } from './viewProjectPage.js';

import { viewProjectPage_loggedInUser } from './viewProjectPage_loggedInUser.js';

import { proxlXMLFileImport } from './proxlXMLFileImport.js';
import { proxlXMLFileImportStatusDisplay } from './proxlXMLFileImportStatusDisplay.js';
import { proxlXMLFileImportUserUpdates } from './proxlXMLFileImportUserUpdates.js';

import { viewProject_ProjectAdminSection } from './viewProject_ProjectAdminSection.js';
import { organizeSearches } from './viewProject_OrganizeSearchesAndFoldersAdmin.js';
import { viewProject_ProjectLockAdmin } from './viewProject_ProjectLockAdmin.js';
import { viewProject_SearchMaint } from './viewProject_SearchMaint.js';
