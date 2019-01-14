/**
 * header_section_every_page_root.js
 * 
 * Javascript for JSP include /WEB-INF/jsp-includes/head_section_include_every_page.jsp  
 * 
 * Root JS file for Header 
 * 
 * Included in page_js/common_all_pages/header_section_main_pages/header_main.js
 */

var z = 0;

import { reportWebErrorToServer } from "./reportWebErrorToServer.js";

import { addToolTips, addSingleGenericProxlToolTip } from "./genericToolTip.js";

// import { CROSSLINKS_CONSTANTS_EVERY_PAGE } from "./crosslinks_constants_every_page.js";

import { showErrorMsg, hideAllErrorMessages, clearErrorMsg, initShowHideErrorMessage, initShowHideErrorMessageSpecificElements } from "./showHideErrorMessage.js";

import { handleAJAXFailure } from "./handleServicesAJAXErrors.js";

import { createSpinner } from "./spinner.js";

import { initToggleVisibility } from "./toggleVisibility.js";

const header_section_every_page_rootVariable = false;

var z = 1;

//  Dummy export
export { header_section_every_page_rootVariable }
