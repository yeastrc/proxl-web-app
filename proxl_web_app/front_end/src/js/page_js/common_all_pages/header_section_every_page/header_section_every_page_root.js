/**
 * header_section_every_page_root.js
 * 
 * Javascript for JSP include /WEB-INF/jsp-includes/head_section_include_every_page.jsp  
 * 
 * Root JS file for Header 
 * 
 * 
 */

import { reportWebErrorToServer } from "./reportWebErrorToServer.js";

import { addToolTips, addSingleGenericProxlToolTip } from "./genericToolTip.js";

import { CROSSLINKS_CONSTANTS_EVERY_PAGE } from "./crosslinks_constants_every_page.js";

import { showErrorMsg, hideAllErrorMessages, clearErrorMsg, initShowHideErrorMessage, initShowHideErrorMessageSpecificElements } from "./showHideErrorMessage.js";

import { handleAJAXFailure } from "./handleServicesAJAXErrors.js";

import { createSpinner } from "./spinner.js";

import { initToggleVisibility } from "./toggleVisibility.js";
