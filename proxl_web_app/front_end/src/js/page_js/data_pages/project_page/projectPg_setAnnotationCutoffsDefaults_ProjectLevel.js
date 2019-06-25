/**
 * projectPg_setAnnotationCutoffsDefaults_ProjectLevel.js
 * 
 * Javascript for viewProject.jsp page - Manage Annotation Cutoff Defaults at Project level
 * 
 */



///////////////////////////////////////////

//  module import 
//  Import Handlebars templates

const _project_page__ann_cutoff_defaults_project_level_template = 
require("../../../../../handlebars_templates_precompiled/project_page__ann_cutoff_defaults_project_level/project_page__ann_cutoff_defaults_project_level_template-bundle.js");

import { reportWebErrorToServer } from 'page_js/header_section_js_all_pages_main_pages/header_section_every_page/reportWebErrorToServer.js';

import { webserviceCallStandardPost } from 'page_js/webservice_call_common/webserviceCallStandardPost.js';

const _PEPTIDE_PSM_LABEL__PEPTIDE = "peptide";
const _PEPTIDE_PSM_LABEL__PSM = "psm";

const _CSS_SELECTOR_CLASS__selector_ann_type_cutoff_input_field = "selector_ann_type_cutoff_input_field";
const _CSS_SELECTOR_CLASS_PREFIX__selector_ann_type_cutoff_input_field_ = "selector_ann_type_cutoff_input_field_";


/**
 * 
 */
export class ProjectPg_setAnnotationCutoffsDefaults_ProjectLevel {

    /**
     * 
     */
    constructor({ projectId }) {

        this._projectId = projectId;

        this._projectPg_setAnnotationCutoffsDefaults_ProjectLevel_Overlay = undefined;
    }

    /**
     * 
     */
    initialize() {

        const $set_project_level_default_cutoffs_button = $("#set_project_level_default_cutoffs_button");
        if ( $set_project_level_default_cutoffs_button.length === 0 ) {
            throw Error("No DOM element with id 'set_project_level_default_cutoffs_button'");
        }
        $set_project_level_default_cutoffs_button.click( ( eventObject ) => {
            try {
                eventObject.preventDefault();
                const eventTarget = eventObject.target;

                this._projectPg_setAnnotationCutoffsDefaults_ProjectLevel_Overlay = 
                    new ProjectPg_setAnnotationCutoffsDefaults_ProjectLevel_Overlay({ 
                        projectId : this._projectId,
                        projectPg_setAnnotationCutoffsDefaults_ProjectLevel : this
                    });

                this._projectPg_setAnnotationCutoffsDefaults_ProjectLevel_Overlay.initialize();

                this._projectPg_setAnnotationCutoffsDefaults_ProjectLevel_Overlay.openOverlay({ eventTarget });

                return false;
            } catch( e ) {
                reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                throw e;
            }
        })
    }

    /**
     * 
     */
    closeOverlayCallback() {

        this._projectPg_setAnnotationCutoffsDefaults_ProjectLevel_Overlay = undefined;
    }
}

//////////////////////////////////////////////////////////////////

/**
 * Internal Class Instantiated when the overlay is opened
 */
class ProjectPg_setAnnotationCutoffsDefaults_ProjectLevel_Overlay {

    /**
     * 
     */
    constructor({ projectId, projectPg_setAnnotationCutoffsDefaults_ProjectLevel }) {

        this._projectId = projectId;
        //  Parent object
        this._projectPg_setAnnotationCutoffsDefaults_ProjectLevel = projectPg_setAnnotationCutoffsDefaults_ProjectLevel;

		if ( ! _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_background ) {
			throw Error("Nothing in _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_background");
        }
        if ( ! _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_root ) {
			throw Error("Nothing in _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_root");
        }

        if ( ! _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_search_program_entry ) {
			throw Error("Nothing in _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_search_program_entry");
        }
        if ( ! _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_peptide_list_header_entry ) {
			throw Error("Nothing in _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_peptide_list_header_entry");
        }
        if ( ! _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_psm_list_header_entry ) {
			throw Error("Nothing in _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_psm_list_header_entry");
        }
        if ( ! _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_ann_type_entry ) {
			throw Error("Nothing in _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_ann_type_entry");
        }
        
		this._project_page__ann_cutoff_defaults_project_level_overlay_background_Template = 
			_project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_background;
        
		this._project_page__ann_cutoff_defaults_project_level_overlay_root_Template = 
			_project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_root;

		this._project_page__ann_cutoff_defaults_project_level_overlay_search_program_entry_Template = 
            _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_search_program_entry;
    
		this._project_page__ann_cutoff_defaults_project_level_overlay_peptide_list_header_entry_Template = 
            _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_peptide_list_header_entry;
    
		this._project_page__ann_cutoff_defaults_project_level_overlay_psm_list_header_entry_Template = 
            _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_psm_list_header_entry;
    
		this._project_page__ann_cutoff_defaults_project_level_overlay_ann_type_entry_Template = 
            _project_page__ann_cutoff_defaults_project_level_template.project_page__ann_cutoff_defaults_project_level_overlay_ann_type_entry;


        //  Overall_Entries_AllTypes_INDEX values in Data Entry Error
        this._annotationTypes_Entries_UserDataError = new Set();

        //  Create Arrays of entries per Ann Type for mapping the index back to the full data

        this._annotationTypes_Overall_Entries_PSM = [];
        this._annotationTypes_Overall_Entries_ReportedPeptide = [];

        this._saveButton_IsEnabled = true; // Must match starting HTML
    }

    /**
     * 
     */
    initialize() {


    }

    /**
     * 
     */
    openOverlay({ eventTarget }){

        const promise_getDataForOverlay = this._getDataForOverlay({ projectId : this._projectId })

        promise_getDataForOverlay.catch( (  ) => { } );

        promise_getDataForOverlay.then( ({ responseData }) => {
            this._displayOverlay({ responseData })
        })
        
    }


    /**
     * 
     */
    _getDataForOverlay({ projectId }){

        return new Promise( (resolve, reject) => {
            try {
                const promise_getAnnotationTypesFilterableForProjectId = this._getAnnotationTypesFilterableForProjectId({ projectId });

                const promise_projectLevelDefaultCutoffs_GetExistingEntries_ForProjectId = this._projectLevelDefaultCutoffs_GetExistingEntries_ForProjectId({ projectId });

                const promiseAll = Promise.all( [ promise_getAnnotationTypesFilterableForProjectId, promise_projectLevelDefaultCutoffs_GetExistingEntries_ForProjectId ] );

                promiseAll.catch( () => { reject() } );

                promiseAll.then( ( responseDataArray ) => {

                    const responseData = {};

                    for ( const responseDataEntry of responseDataArray ) {

                        if ( responseDataEntry.annotationTypeDataResponse ) {
                            responseData.annotationTypeDataResponse = responseDataEntry.annotationTypeDataResponse
                        }
                        if ( responseDataEntry.existingEntriesResponse ) {
                            responseData.existingEntriesResponse = responseDataEntry.existingEntriesResponse
                        }
                    }

                    resolve({ responseData });
                });
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
    _getAnnotationTypesFilterableForProjectId({ projectId }){

        return new Promise( (resolve, reject) => {
            try {
                let requestObj = {
                    projectId
                };

                const url = "services/getAnnotationTypesFilterableForProjectId";

                const webserviceCallStandardPostResponse = webserviceCallStandardPost({ dataToSend : requestObj, url }) ;

                const promise_webserviceCallStandardPost = webserviceCallStandardPostResponse.promise;

                promise_webserviceCallStandardPost.catch( () => { reject() }  );

                promise_webserviceCallStandardPost.then( ({ responseData }) => {
                    try {
                        resolve({ annotationTypeDataResponse : responseData });
                    } catch (e) {
                        reportWebErrorToServer.reportErrorObjectToServer({
                            errorException : e
                        });
                        throw e;
                    }
                });
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
    _projectLevelDefaultCutoffs_GetExistingEntries_ForProjectId({ projectId }){

        return new Promise( (resolve, reject) => {
            try {
                let requestObj = {
                    projectId
                };

                const url = "services/projectLevelDefaultCutoffs_GetExistingEntries_ForProjectId";

                const webserviceCallStandardPostResponse = webserviceCallStandardPost({ dataToSend : requestObj, url }) ;

                const promise_webserviceCallStandardPost = webserviceCallStandardPostResponse.promise;

                promise_webserviceCallStandardPost.catch( () => { reject() }  );

                promise_webserviceCallStandardPost.then( ({ responseData }) => {
                    try {
                        resolve({ existingEntriesResponse : responseData });
                    } catch (e) {
                        reportWebErrorToServer.reportErrorObjectToServer({
                            errorException : e
                        });
                        throw e;
                    }
                });
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
    _displayOverlay({ responseData }){

        const displayObjects = this._createDisplayObjectsFromWebserviceResponseData({ responseData });

        const minPSMs = responseData.existingEntriesResponse.minPSMs;

        const $body = $( "body" );

        {
            const html = this._project_page__ann_cutoff_defaults_project_level_overlay_background_Template();
            const $background = $( html )
            $background.appendTo( $body );
            // $background.click( ( eventObject ) => {
            //     try {
            //         eventObject.preventDefault();
            //         const eventTarget = eventObject.target;
            //         this._closeOverlay({ eventTarget });
            //         return false;
            //     } catch( e ) {
            //         reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
            //         throw e;
            //     }
            // });
        }
        {
            let $overlay = undefined;
            {
                const $window = $(window);
                const scrollTop = $window.scrollTop();
                const topPosition = scrollTop + 10;
                let mainBodyMaxHeight = $window.height() - 110;
                if  ( mainBodyMaxHeight < 200 ) {
                    mainBodyMaxHeight = 200;
                }
                // if  ( mainBodyMaxHeight > 630 ) {
                //     mainBodyMaxHeight = 630;
                // }
                const rootContext = { topPosition, mainBodyMaxHeight };
                const html = this._project_page__ann_cutoff_defaults_project_level_overlay_root_Template( rootContext );
                $overlay = $( html )
                $overlay.appendTo( $body );
            }

            {
                const $project_page__ann_cutoff_defaults_project_level_psms_min = $("#project_page__ann_cutoff_defaults_project_level_psms_min");
                if ( $project_page__ann_cutoff_defaults_project_level_psms_min.length === 0 ) {
                    throw Error("No DOM element with id 'project_page__ann_cutoff_defaults_project_level_psms_min'");
                }
                $project_page__ann_cutoff_defaults_project_level_psms_min.val( minPSMs );
                this._add_MinPSM_InputField__OnChange_Processing({ $project_page__ann_cutoff_defaults_project_level_psms_min });
            }

            const $project_page__ann_cutoff_defaults_project_level_main_table = $overlay.find("#project_page__ann_cutoff_defaults_project_level_main_table");
            if ( $project_page__ann_cutoff_defaults_project_level_main_table.length === 0 ) {
                throw Error("No DOM element with id 'project_page__ann_cutoff_defaults_project_level_main_table'");
            }
            const $project_page__ann_cutoff_defaults_project_level_main_table_tbody = $project_page__ann_cutoff_defaults_project_level_main_table.children("tbody");
            if ( $project_page__ann_cutoff_defaults_project_level_main_table_tbody.length === 0 ) {
                throw Error("No DOM element <tbody> under elemetn with id 'project_page__ann_cutoff_defaults_project_level_main_table'");
            }

            for ( const dataPerSearchProgramName_Entry of displayObjects.dataPerSearchProgramName_Array ) {

                { //  Search Program Entry
                    const html = this._project_page__ann_cutoff_defaults_project_level_overlay_search_program_entry_Template({ dataPerSearchProgramName_Entry });
                    const $entry = $( html )
                    $entry.appendTo( $project_page__ann_cutoff_defaults_project_level_main_table_tbody );
                }

                if ( dataPerSearchProgramName_Entry.peptide_List ) { //  Peptide List
                    {
                        const html = this._project_page__ann_cutoff_defaults_project_level_overlay_peptide_list_header_entry_Template({});
                        const $entry = $( html )
                        $entry.appendTo( $project_page__ann_cutoff_defaults_project_level_main_table_tbody );
                    }
                    for ( const annType_Entry of dataPerSearchProgramName_Entry.peptide_List ) {

                        const cssSelectorClasses = _CSS_SELECTOR_CLASS__selector_ann_type_cutoff_input_field +
                            " " +
                            _CSS_SELECTOR_CLASS_PREFIX__selector_ann_type_cutoff_input_field_ +
                            _PEPTIDE_PSM_LABEL__PEPTIDE;

                        const html = this._project_page__ann_cutoff_defaults_project_level_overlay_ann_type_entry_Template({ 
                            annType_Entry, peptidePSM : _PEPTIDE_PSM_LABEL__PEPTIDE, cssSelectorClasses });

                        const $annType_Entry = $( html )
                        $annType_Entry.appendTo( $project_page__ann_cutoff_defaults_project_level_main_table_tbody );

                        this._add_Cutoff_InputField__OnChange_Processing({ $annType_Entry, annType_Entry });
                    }
                }

                if ( dataPerSearchProgramName_Entry.psm_List ) { //  PSM List
                    {
                        const html = this._project_page__ann_cutoff_defaults_project_level_overlay_psm_list_header_entry_Template({});
                        const $entry = $( html )
                        $entry.appendTo( $project_page__ann_cutoff_defaults_project_level_main_table_tbody );
                    }
                    for ( const annType_Entry of dataPerSearchProgramName_Entry.psm_List ) {

                        const cssSelectorClasses = _CSS_SELECTOR_CLASS__selector_ann_type_cutoff_input_field +
                            " " +
                            _CSS_SELECTOR_CLASS_PREFIX__selector_ann_type_cutoff_input_field_ +
                            _PEPTIDE_PSM_LABEL__PSM;

                        const html = this._project_page__ann_cutoff_defaults_project_level_overlay_ann_type_entry_Template({ 
                            annType_Entry, peptidePSM : _PEPTIDE_PSM_LABEL__PSM, cssSelectorClasses });

                        const $annType_Entry = $( html )
                        $annType_Entry.appendTo( $project_page__ann_cutoff_defaults_project_level_main_table_tbody );

                        this._add_Cutoff_InputField__OnChange_Processing({ $annType_Entry, annType_Entry });
                    }
                }
            }

            const $project_page__ann_cutoff_defaults_project_level_cancel_button = $overlay.find("#project_page__ann_cutoff_defaults_project_level_cancel_button");
            if ( $project_page__ann_cutoff_defaults_project_level_cancel_button.length === 0 ) {
                throw Error("No DOM element with id 'project_page__ann_cutoff_defaults_project_level_cancel_button'");
            }
            $project_page__ann_cutoff_defaults_project_level_cancel_button.click( ( eventObject ) => {
                try {
                    eventObject.preventDefault();
                    const eventTarget = eventObject.target;
                    this._closeOverlay({ eventTarget });
                    return false;
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });

            const $project_page__ann_cutoff_defaults_project_level_x_exit = $overlay.find("#project_page__ann_cutoff_defaults_project_level_x_exit");
            if ( $project_page__ann_cutoff_defaults_project_level_x_exit.length === 0 ) {
                throw Error("No DOM element with id 'project_page__ann_cutoff_defaults_project_level_x_exit'");
            }
            $project_page__ann_cutoff_defaults_project_level_x_exit.click( ( eventObject ) => {
                try {
                    eventObject.preventDefault();
                    const eventTarget = eventObject.target;
                    this._closeOverlay({ eventTarget });
                    return false;
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });
    
            const $project_page__ann_cutoff_defaults_project_level_save_button = $overlay.find("#project_page__ann_cutoff_defaults_project_level_save_button");
            if ( $project_page__ann_cutoff_defaults_project_level_save_button.length === 0 ) {
                throw Error("No DOM element with id 'project_page__ann_cutoff_defaults_project_level_save_button'");
            }
            $project_page__ann_cutoff_defaults_project_level_save_button.click( ( eventObject ) => {
                try {
                    eventObject.preventDefault();
                    const eventTarget = eventObject.target;
                    this._save({ eventTarget });
                    return false;
                } catch( e ) {
                    reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                    throw e;
                }
            });
    
        }
    }

    //////////  Validate Min PSM Value

    /**
     * 
     */
    _add_MinPSM_InputField__OnChange_Processing({ $project_page__ann_cutoff_defaults_project_level_psms_min }) {

        const project_page__ann_cutoff_defaults_project_level_psms_min_DOM = $project_page__ann_cutoff_defaults_project_level_psms_min[ 0 ];

        project_page__ann_cutoff_defaults_project_level_psms_min_DOM.addEventListener('input', ( eventObject ) => {
            try {
                eventObject.preventDefault();
                // console.log("'input' fired");
                const eventTarget = eventObject.target;
                // const inputBoxValue = eventTarget.value;
                // console.log("'input' fired. inputBoxValue: " + inputBoxValue );
                const $eventTarget = $( eventTarget );
                const $selector_invalid_entry = $("#project_page__ann_cutoff_defaults_project_level_psms_min_invalid_entry");
                var fieldValue = $eventTarget.val();
                if ( ! this._isFieldValueValidMinimumPSMValue({ fieldValue }) ) {
                    $selector_invalid_entry.show();

                    if ( this._minPSMs_Entry_Valid ) {
                        this._minPSMs_Entry_Valid = false;

                        //  Value changed so update dependent items
                        this._update_enable_disable_SaveButton();
                    }
                } else {
                    $selector_invalid_entry.hide();

                    if ( ! this._minPSMs_Entry_Valid ) {
                        this._minPSMs_Entry_Valid = true;

                        //  Value changed so update dependent items
                        this._update_enable_disable_SaveButton();
                    }
                }
                return false;
            } catch( e ) {
                reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                throw e;
            }
        });
    }

	_isFieldValueValidMinimumPSMValue({ fieldValue }) {
		if ( fieldValue === "" ) {
			return true;
        }
        if ( fieldValue === "0" ) {
			return false;
        }
		// only test for valid cutoff value if not empty string
		if ( !  /^[+]?(\d+)$/.test( fieldValue ) ) {
			//  cutoff value is not a valid integer number
			return false; 
        }
        const valueNumber = Number.parseInt( fieldValue );
        if ( Number.isNaN( valueNumber ) ) {
            return false;
        }
        if ( valueNumber < 1 ) {
            return false;
        }
		return true;
    };
    
    //////////  Validate Filterable Annotation Cutoff Value

    /**
     * 
     */
    _add_Cutoff_InputField__OnChange_Processing({ $annType_Entry, annType_Entry }) {

        const annType_Entry_DOM = $annType_Entry[ 0 ];

        // document.querySelector(...) Returns first element found
        const selector_ann_type_cutoff_input_field_DOM_Element = 
            annType_Entry_DOM.querySelector(".selector_ann_type_cutoff_input_field");

        if ( selector_ann_type_cutoff_input_field_DOM_Element === null || selector_ann_type_cutoff_input_field_DOM_Element.length === 0 ) {
            throw Error("No DOM element with class 'selector_ann_type_cutoff_input_field' ");
        }

        selector_ann_type_cutoff_input_field_DOM_Element.addEventListener('input', ( eventObject ) => {
            try {
                eventObject.preventDefault();
                // console.log("'input' fired");
                const eventTarget = eventObject.target;
                // const inputBoxValue = eventTarget.value;
                // console.log("'input' fired. inputBoxValue: " + inputBoxValue );
                const $eventTarget = $( eventTarget );
                const $selector_ann_type_containing_row = $eventTarget.closest(".selector_ann_type_containing_row");
                const $selector_invalid_entry = $selector_ann_type_containing_row.find(".selector_invalid_entry");
                var fieldValue = $eventTarget.val();
                if ( ! this._isFieldValueValidDecimalNumber({ fieldValue }) ) {
                    $selector_invalid_entry.show();

                    const indexOverall = annType_Entry.indexOverall;
                    if ( ! this._annotationTypes_Entries_UserDataError.has( indexOverall ) ) {
                        this._annotationTypes_Entries_UserDataError.add( indexOverall );

                        //  Value changed so update dependent items
                        this._update_enable_disable_SaveButton();
                    }
                } else {
                    $selector_invalid_entry.hide();

                    const indexOverall = annType_Entry.indexOverall;
                    if ( this._annotationTypes_Entries_UserDataError.has( indexOverall ) ) {
                        this._annotationTypes_Entries_UserDataError.delete( indexOverall );

                        //  Value changed so update dependent items
                        this._update_enable_disable_SaveButton();
                    }
                }
                return false;
            } catch( e ) {
                reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
                throw e;
            }
        });
    }

	_isFieldValueValidDecimalNumber({ fieldValue }) {
		if ( fieldValue === "" ) {
			return true;
		}
		// only test for valid cutoff value if not empty string
		if ( !  /^[+-]?((\d+(\.\d*)?)|(\.\d+))$/.test( fieldValue ) ) {
			//  cutoff value is not a valid decimal number
			return false; 
		}
		return true;
    };
    
    /////////////////////////////////////////////////////
	
    /**
     * Create final Object structure for generating HTML
     */
    _createDisplayObjectsFromWebserviceResponseData({ responseData }) {

        const annotationTypeDataResponse = responseData.annotationTypeDataResponse;
        const existingEntriesResponse = responseData.existingEntriesResponse;

        const minPSMs = existingEntriesResponse.minPSMs;

        // Use objects for index so can increment the index property in called methods

        const annotationTypes_Overall_Entries_AllTypes_Index_Container= { index :  0 };

        const annotationTypes_Overall_Entries_PSM_Index_Container = { index :  0 };
        const annotationTypes_Overall_Entries_ReportedPeptide_Index_Container = { index :  0 };
        

        //      Match up Annotation Type Data to Existing Entries
 
        //  annotationTypeDataResponse.reportedPeptideAnnotationTypeList
        //  annotationTypeDataResponse.psmAnnotationTypeList
        
        //  existingEntriesResponse.reportedPeptideEntriesList;
        //  existingEntriesResponse.psmEntriesList;
        
        //  Create 2 Maps ( peptide_DataMap, psm_DataMap )
        //    <[Search Program name],Map<[annTypeName],Set<[defaultFilterValue]>>>

        const peptide_DataMap_Key_SearchProgramName = 
            this._create_Map_Per_SearchProgramName_ProcessPeptideOrPSM({ 
                annotationTypeList : annotationTypeDataResponse.reportedPeptideAnnotationTypeList
             });

        const psm_DataMap_Key_SearchProgramName = 
        this._create_Map_Per_SearchProgramName_ProcessPeptideOrPSM({ 
            annotationTypeList : annotationTypeDataResponse.psmAnnotationTypeList
        });

        //  Combine Maps of Ann Type data and Existing Entries to produce result Display Objects

        //   Result Display Objects

        //    Array<{ searchProgramName, peptide_List, psm_List }

        //  peptide_List, psm_List contain:
        //     Array<{ annTypeName, foundValues: Array< Double [Default cutoffs for searches] >, foundValuesString: String Comma delim of foundValues }>
        //            properties 'foundValues' and 'foundValuesString' are optional and will not be present if none exist 

        const dataPerSearchProgramName_Array = [];

        for ( const peptide_DataMap_Key_SearchProgramName_Entry of peptide_DataMap_Key_SearchProgramName.entries() ) {

            const searchProgramName = peptide_DataMap_Key_SearchProgramName_Entry[ 0 ];
            const peptide_AnnTypeData_Map = peptide_DataMap_Key_SearchProgramName_Entry[ 1 ];

            const dataPerSearchProgramName_Entry = { searchProgramName };

            dataPerSearchProgramName_Entry.peptide_List = this._create_Array_ProcessPeptideOrPSM({ 
                annTypeData_Map : peptide_AnnTypeData_Map, 
                searchProgramName,
                existingCutoffEntriesList : existingEntriesResponse.reportedPeptideEntriesList,
                psmPeptideOverallList : this._annotationTypes_Overall_Entries_ReportedPeptide,
                indexPerTypeContainer : annotationTypes_Overall_Entries_ReportedPeptide_Index_Container,
                annotationTypes_Overall_Entries_AllTypes_Index_Container
            });

            { //  Process psm data for same searchProgramName
                const psm_AnnTypeData_Map = psm_DataMap_Key_SearchProgramName.get( searchProgramName );;
                if ( psm_AnnTypeData_Map ) {
                    dataPerSearchProgramName_Entry.psm_List = this._create_Array_ProcessPeptideOrPSM({ 
                        annTypeData_Map : psm_AnnTypeData_Map,
                        searchProgramName,
                        existingCutoffEntriesList : existingEntriesResponse.psmEntriesList,
                        psmPeptideOverallList : this._annotationTypes_Overall_Entries_PSM,
                        indexPerTypeContainer : annotationTypes_Overall_Entries_PSM_Index_Container,
                        annotationTypes_Overall_Entries_AllTypes_Index_Container
                    });
                }
            }

            psm_DataMap_Key_SearchProgramName.delete( searchProgramName );  // Remove since processed

            dataPerSearchProgramName_Array.push( dataPerSearchProgramName_Entry );
        }

        //  Process any remaining PSM Map entries
        for ( const psm_DataMap_Key_SearchProgramName_Entry of psm_DataMap_Key_SearchProgramName.entries() ) {

            const searchProgramName = psm_DataMap_Key_SearchProgramName_Entry[ 0 ];
            const psm_AnnTypeData_Map = psm_DataMap_Key_SearchProgramName_Entry[ 1 ];

            const dataPerSearchProgramName_Entry = { searchProgramName };

            dataPerSearchProgramName_Entry.psm_List = this._create_Array_ProcessPeptideOrPSM({ 
                annTypeData_Map : psm_AnnTypeData_Map,
                searchProgramName,
                existingCutoffEntriesList : existingEntriesResponse.psmEntriesList,
                psmPeptideOverallList : this._annotationTypes_Overall_Entries_PSM,
                indexPerTypeContainer : annotationTypes_Overall_Entries_PSM_Index_Container,
                annotationTypes_Overall_Entries_AllTypes_Index_Container
            });

            dataPerSearchProgramName_Array.push( dataPerSearchProgramName_Entry );
        }

        //  Sort dataPerSearchProgramName_Array on searchProgramName
        dataPerSearchProgramName_Array.sort( (a, b) => {
            return a.searchProgramName.localeCompare( b.searchProgramName, undefined, { sensitivity: 'base' } );
        });

        return { dataPerSearchProgramName_Array };
    }

         //     Array<{ annTypeName, foundValues: Array< Double [Default cutoffs for searches] >, foundValuesString: String Comma delim of foundValues }>
         //            properties 'foundValues' and 'foundValuesString' are optional and will not be present if none exist 
    /**
     * 
     */
    _create_Array_ProcessPeptideOrPSM({ annTypeData_Map, searchProgramName, existingCutoffEntriesList,
        psmPeptideOverallList, indexPerTypeContainer, annotationTypes_Overall_Entries_AllTypes_Index_Container }) {

        if ( ! annTypeData_Map ) {
            return undefined;
        }

        const annType_ResultArray = [];

        for ( const annTypeData_Entry of annTypeData_Map.entries() ) {
            const annTypeName = annTypeData_Entry[ 0 ];
            const defaultFilterValues_Set = annTypeData_Entry[ 1 ];

            const annType_ResultEntry = { annTypeName, indexPerType : indexPerTypeContainer.index, indexOverall : annotationTypes_Overall_Entries_AllTypes_Index_Container.index };

            //  Get existing cutoff entry for ann type name and search program name

            for ( const existingCutoffEntry of existingCutoffEntriesList ) {
                if ( existingCutoffEntry.annotationTypeName === annTypeName && existingCutoffEntry.searchProgramName === searchProgramName ) {
                    annType_ResultEntry.existingCutoffValue = existingCutoffEntry.annotationCutoffValue;
                    break;
                }
            }

            if ( defaultFilterValues_Set && defaultFilterValues_Set.size !== 0 ) {
                const defaultFilterValues_Array = Array.from( defaultFilterValues_Set );
                defaultFilterValues_Array.sort();
                annType_ResultEntry.foundValues = defaultFilterValues_Array;
                annType_ResultEntry.foundValuesString = annType_ResultEntry.foundValues.join( ", " );
            }

            annType_ResultArray.push( annType_ResultEntry );

            //  Also add to psmPeptideOverallList
            {
                const overallListEntry = { annTypeName, searchProgramName, annType_ResultEntry };
                psmPeptideOverallList[ indexPerTypeContainer.index ] = overallListEntry;
            }

            //  Increment the indexes

            indexPerTypeContainer.index++;
            annotationTypes_Overall_Entries_AllTypes_Index_Container.index++;
        }

        //  Sort annType_ResultArray on annTypeName
        annType_ResultArray.sort( (a, b) => {
            return a.annTypeName.localeCompare( b.annTypeName, undefined, { sensitivity: 'base' } );
        });

        return annType_ResultArray;
    }

        //    <[Search Program name],Map<[annTypeName],Set<[defaultFilterValue]>>>
    /**
     * 
     */
    _create_Map_Per_SearchProgramName_ProcessPeptideOrPSM({ annotationTypeList }) {

        const resultMap_Key_SearchProgramName = new Map();

        for ( const annotationTypeDataEntry of annotationTypeList ) {

            const annotationTypeDTO = annotationTypeDataEntry.annotationTypeDTO;
            const annotationTypeFilterableDTO = annotationTypeDTO.annotationTypeFilterableDTO;

            // matching searchProgramsPerSearchDTO for annotationTypeDTO
            const searchProgramsPerSearchDTO = annotationTypeDataEntry.searchProgramsPerSearchDTO; 

            const annTypeId = annotationTypeDTO.id;
            const annTypeName = annotationTypeDTO.name;
            const defaultFilter = annotationTypeFilterableDTO.defaultFilter; // boolean
            const defaultFilterValue = annotationTypeFilterableDTO.defaultFilterValue; // double
            const defaultFilterValueString = annotationTypeFilterableDTO.defaultFilterValueString; // String

            const searchProgramName = searchProgramsPerSearchDTO.name;



            let subMap_Key_AnnTypeName = resultMap_Key_SearchProgramName.get( searchProgramName );
            if ( ! subMap_Key_AnnTypeName ) {
                subMap_Key_AnnTypeName = new Map();
                resultMap_Key_SearchProgramName.set( searchProgramName, subMap_Key_AnnTypeName );
            }
            let defaultCutoffs_ForAnnTypeName = subMap_Key_AnnTypeName.get( annTypeName );
            if ( ! defaultCutoffs_ForAnnTypeName ) {
                defaultCutoffs_ForAnnTypeName = new Set();
                subMap_Key_AnnTypeName.set( annTypeName, defaultCutoffs_ForAnnTypeName );
            }
            if ( defaultFilter ) {
                defaultCutoffs_ForAnnTypeName.add( defaultFilterValue );
            }
        }

        return resultMap_Key_SearchProgramName;
    }

    /////////////////////////////////////////////////////

    /**
     * 
     */
    _update_enable_disable_SaveButton() {
        
        const project_page__ann_cutoff_defaults_project_level_save_buttonDOM = document.getElementById("project_page__ann_cutoff_defaults_project_level_save_button");
        if ( ! project_page__ann_cutoff_defaults_project_level_save_buttonDOM ) {
            throw Error("No DOM element with id 'project_page__ann_cutoff_defaults_project_level_save_button'");
        }
        
        if ( this._annotationTypes_Entries_UserDataError.size === 0 && this._minPSMs_Entry_Valid ) {
            if ( ! this._saveButton_IsEnabled ) {
                
                project_page__ann_cutoff_defaults_project_level_save_buttonDOM.disabled = false;
                this._saveButton_IsEnabled = true;
            }
        } else {
            if ( this._saveButton_IsEnabled ) {

                project_page__ann_cutoff_defaults_project_level_save_buttonDOM.disabled = true;
                this._saveButton_IsEnabled = false;
            }
        }
    }

    /**
     * 
     */
    _save() {

        const cutoffValuesFromPage = this._getCutoffValuesFromPage();

        const minPSMs = this._get_minPSMs_FromPage();
        
        const promise_save_ValuesToServer = this._save_ValuesToServer({ cutoffValuesFromPage, minPSMs });

        promise_save_ValuesToServer.catch( ( ) => { } );

        promise_save_ValuesToServer.then( (  ) =>  {

            // window.confirm("Save Complete");

            this._closeOverlay();

            var $element = $("#set_project_level_default_cutoffs_button_success_message_values_updated");
            showErrorMsg( $element );  //  Used for success messages as well
        });
    }

    /**
     * 
     */
    _get_minPSMs_FromPage() {

        const $project_page__ann_cutoff_defaults_project_level_psms_min = $("#project_page__ann_cutoff_defaults_project_level_psms_min");
        let minPSMs = $project_page__ann_cutoff_defaults_project_level_psms_min.val();
        if ( minPSMs === "" ) {
            minPSMs = undefined;
        }
        return minPSMs;
    }

    /**
     * 
     */
    _getCutoffValuesFromPage() {

        const results ={};

        //  retrieve values using CSS selectors

        const $project_page__ann_cutoff_defaults_project_level_main_table = $("#project_page__ann_cutoff_defaults_project_level_main_table");
        if ( $project_page__ann_cutoff_defaults_project_level_main_table.length === 0 ) {
            throw Error("No DOM element found with id 'project_page__ann_cutoff_defaults_project_level_main_table'");
        }

        if ( this._annotationTypes_Overall_Entries_ReportedPeptide && this._annotationTypes_Overall_Entries_ReportedPeptide.length !== 0 ) {
            //  Get Reported Peptide Cutoff values

            const selector = "." + _CSS_SELECTOR_CLASS_PREFIX__selector_ann_type_cutoff_input_field_ + _PEPTIDE_PSM_LABEL__PEPTIDE;
            const reportedPeptide_CutoffValues =
                this._getCutoffValuesFromPage_ForType({ 
                    selector, 
                    psmPeptideOverallList : this._annotationTypes_Overall_Entries_ReportedPeptide,
                    $project_page__ann_cutoff_defaults_project_level_main_table });

            results.reportedPeptideEntriesList = reportedPeptide_CutoffValues;
        }

        if ( this._annotationTypes_Overall_Entries_PSM && this._annotationTypes_Overall_Entries_PSM.length !== 0 ) {
            //  Get Reported Peptide Cutoff values

            const selector = "." + _CSS_SELECTOR_CLASS_PREFIX__selector_ann_type_cutoff_input_field_ + _PEPTIDE_PSM_LABEL__PSM;
            const psm_CutoffValues =
                this._getCutoffValuesFromPage_ForType({ 
                    selector, 
                    psmPeptideOverallList : this._annotationTypes_Overall_Entries_PSM,
                    $project_page__ann_cutoff_defaults_project_level_main_table });

            results.psmEntriesList = psm_CutoffValues;
        }

        return results;
    }

    /**
     * 
     */
    _getCutoffValuesFromPage_ForType({ selector, psmPeptideOverallList, $project_page__ann_cutoff_defaults_project_level_main_table }) {

        const results = [];

        const $inputFieldsAll = $project_page__ann_cutoff_defaults_project_level_main_table.find( selector );
        if ( $inputFieldsAll.length === 0 ) {
            throw Error("No DOM elements found with selector " + selector );
        }
        $inputFieldsAll.each( ( index, element ) => {
            const $element = $( element );
            const fieldValueString = $element.val();
            if ( fieldValueString === "" ) {
                //  No value so skip to next 
                return; // EARLY RETURN
            }
            if ( ! this._isFieldValueValidDecimalNumber({ fieldValue : fieldValueString }) ) {
                throw Error("_getCutoffValuesFromPage_ForType(...) '! this._isFieldValueValidNumber( fieldValueString )' fieldValueString: " + fieldValueString );
            }
            const fieldValue = Number.parseFloat( fieldValueString );
            if ( Number.isNaN( fieldValue ) ) {
                throw Error("_getCutoffValuesFromPage_ForType(...) 'Number.isNaN( fieldValue )' fieldValueString: " + fieldValueString );
            }
            const indexForTypeString = $element.attr( "data-index_per_type" );
            const indexForType = Number.parseInt( indexForTypeString );
            if ( Number.isNaN( indexForType ) ) {
                throw Error("Value in indexForTypeString is not a integer: " + indexForTypeString );
            }
            const psmPeptideOverallList_Entry = psmPeptideOverallList[ indexForType ];
            if ( ! psmPeptideOverallList_Entry ) {
                throw Error("No entry in psmPeptideOverallList for indexForType: " + indexForType );
            }

            const result = {
                annotationCutoffValue : fieldValue,
                annotationCutoffValueString : fieldValueString,
                annotationTypeName : psmPeptideOverallList_Entry.annTypeName,
                searchProgramName : psmPeptideOverallList_Entry.searchProgramName
            };

            results.push( result );
        })

        return results;
    }

    /**
     * 
     */
    _save_ValuesToServer({ cutoffValuesFromPage, minPSMs }) {

        return new Promise( (resolve, reject) => {
            try {
                let requestObj = {
                    projectId : this._projectId,
                    cutoffValues : cutoffValuesFromPage,
                    minPSMs
                };

                const url = "services/projectLevelDefaultCutoffs_SaveUpdateEntries_ForProjectId";

                const webserviceCallStandardPostResponse = webserviceCallStandardPost({ dataToSend : requestObj, url }) ;

                const promise_webserviceCallStandardPost = webserviceCallStandardPostResponse.promise;

                promise_webserviceCallStandardPost.catch( () => { reject() }  );

                promise_webserviceCallStandardPost.then( ({ responseData }) => {
                    try {
                        resolve({ annotationTypeDataResponse : responseData });
                    } catch (e) {
                        reportWebErrorToServer.reportErrorObjectToServer({
                            errorException : e
                        });
                        throw e;
                    }
                });
            } catch (e) {
                reportWebErrorToServer.reportErrorObjectToServer({
                    errorException : e
                });
                throw e;
            }
        })
    }

    /**
     * 
     */
    _closeOverlay() {

        const $project_page__ann_cutoff_defaults_project_level_overlay_background = $("#project_page__ann_cutoff_defaults_project_level_overlay_background");
        if ( $project_page__ann_cutoff_defaults_project_level_overlay_background.length === 0 ) {
            throw Error("No DOM element with id 'project_page__ann_cutoff_defaults_project_level_overlay_background'");
        }
        $project_page__ann_cutoff_defaults_project_level_overlay_background.remove();

        const $project_page__ann_cutoff_defaults_project_level_overlay_root = $("#project_page__ann_cutoff_defaults_project_level_overlay_root");
        if ( $project_page__ann_cutoff_defaults_project_level_overlay_root.length === 0 ) {
            throw Error("No DOM element with id 'project_page__ann_cutoff_defaults_project_level_overlay_root'");
        }
        $project_page__ann_cutoff_defaults_project_level_overlay_root.remove();

        this._projectPg_setAnnotationCutoffsDefaults_ProjectLevel.closeOverlayCallback();
    }

}