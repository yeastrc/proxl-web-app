/**
 * dataPages_LoggedInUser_CommonObjectsFactory.js
 * 
 * Javascript for Data Pages
 * 
 * Creates objects from classes that are for Logged In Users.
 * 
 */


 //  Imports

 import { SaveView_dataPages } from 'page_js/data_pages/data_pages_common/saveView_dataPages.js';


/**
 * 
 */
export class DataPages_LoggedInUser_CommonObjectsFactory {
	
	/**
	 * 
	 */
	constructor() {

    }

	/**
	 * 
	 */
	initialize() {


    }


    /**
     * Create object of class SaveView_dataPages AND put it on the 'window' using 'window.saveView_dataPages'
     * 
     */
    instantiate_SaveView_dataPages() {

        const saveView_dataPages = new SaveView_dataPages();

        window.saveView_dataPages = saveView_dataPages; // Stick on window since other things expecting it there

        return saveView_dataPages;
    }

}
