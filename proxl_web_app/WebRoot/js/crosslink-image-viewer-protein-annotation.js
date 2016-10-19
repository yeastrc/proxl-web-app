
//  crosslink-image-viewer-protein-annotation.js

//   Protein Annotation Store



//////////////////////////////////

// JavaScript directive:   all variables have to be declared with "var", maybe other things

"use strict";


//    All references to proteinId are actually referencing the protein sequence id


var DEBUG_CONFIRMS = false;  
// var DEBUG_CONFIRMS = true;  //  if true, confirm popups at key points will happen to force code behaviour for testing 


// var CONSOLE_LOGGING = false;  //  
var CONSOLE_LOGGING = true;  //  Set to true to get logging, console.log(....)    


// /////////////////////////////////////////

var ProteinAnnotationStore = function() {
	
	this.annotationData = {};
	
	this.annotationSequenceIds = {}; // keyed on ProteinSequenceId
	
	this.currentLoadIdentifierForSubmit = undefined;  // uses Date.now();

	this.currentLoadIdentifierForAllLoaded = undefined;  // uses Date.now();
};


/////////  

//   Make an Instance of this class

var proteinAnnotationStore = new ProteinAnnotationStore();

		
////////////////////////

///  Annotation Data Web Services (paws)  status values

ProteinAnnotationStore.prototype.CONSTANTS =

	{ 
		SLEEP_TIME_BEFORE_POLL_FOR_RESULT : DEBUG_CONFIRMS ? 10000 : 10000,  //  in milliseconds


		AJAX_TIMEOUT : 10000,  // in milliseconds

		SERVER_STATUS_VALUES :


			//for pawsStatus
		{
			STATUS_SUBMITTED : "submitted",
			STATUS_COMPLETE : "complete",
			STATUS_FAIL : "fail",
			STATUS_NO_RECORD : "no_record"
		},

		
		//  These strings are used as the keys into the this.annotationData structure
		
		ANNOTATION_DATA_KEY : {
			DISOPRED_2 : "disopred_2_Data",
			DISOPRED_3 : "disopred_3_Data",
			PSIPRED_3 : "psipred_3_Data",
		},

		//	These are all setup to use jsonp

		SERVER_SERVICES_PATHS : {

//			GET_SEQUENCE_ID : "getSequenceId/jsonp?jsonpCallback=?",

			DISOPRED_2 : 
			{
				SUBMIT : "submitDisopred_2/jsonp?jsonpCallback=?",
				GET : "getDisopred_2/jsonp?jsonpCallback=?"
			},
			DISOPRED_3 : 
			{
				SUBMIT : "submitDisopred_3/jsonp?jsonpCallback=?",
				GET : "getDisopred_3/jsonp?jsonpCallback=?"
			},

			PSIPRED_3 : 
			{
				SUBMIT : "submitPsipred_3/jsonp?jsonpCallback=?",
				GET : "getPsipred_3/jsonp?jsonpCallback=?"
			}

		}
	};


ProteinAnnotationStore.prototype.init  = function(  ) {
	
	this.annotation_data_webservice_base_url = $("#annotation_data_webservice_base_url").val();
	
	this.reset();
};


ProteinAnnotationStore.prototype.reset  = function(  ) {
	
	this.annotationData = {};
};


////////

ProteinAnnotationStore.prototype.cancelCheckForComplete  = function(  ) {
	
	this.currentLoadIdentifierForAllLoaded = undefined;
};


//////////////////////


//////////////////////

// 

ProteinAnnotationStore.prototype.getAnnotationDataForKey = function( dataKey ) {
	

	if ( this.annotationData[ dataKey ] === undefined ) {

		this.annotationData[ dataKey ] = {};
	}
	
	var dataForKey = this.annotationData[ dataKey ];
	
	return dataForKey;
};



//////////////////////

ProteinAnnotationStore.prototype.get_disopred_2_DataForProteinId = function( proteinId ) {

	var _get_DataForProteinId_Common_Params = {
			
			proteinId : proteinId,
			annotationDataTypeKey : this.CONSTANTS.ANNOTATION_DATA_KEY.DISOPRED_2
	};
	
	var data = this._get_DataForProteinId_Common( _get_DataForProteinId_Common_Params); 

	return data;
	
};

//////////////////////

ProteinAnnotationStore.prototype.get_disopred_3_DataForProteinId = function( proteinId ) {

	var _get_DataForProteinId_Common_Params = {

			proteinId : proteinId,
			annotationDataTypeKey : this.CONSTANTS.ANNOTATION_DATA_KEY.DISOPRED_3
	};

	var data = this._get_DataForProteinId_Common( _get_DataForProteinId_Common_Params); 

	return data;

};



//////////////////////

ProteinAnnotationStore.prototype.get_psipred_3_DataForProteinId = function( proteinId ) {

	var _get_DataForProteinId_Common_Params = {
			
			proteinId : proteinId,
			annotationDataTypeKey : this.CONSTANTS.ANNOTATION_DATA_KEY.PSIPRED_3
	};
	
	var data = this._get_DataForProteinId_Common( _get_DataForProteinId_Common_Params); 

	return data;

};



//////////////////////

//   Return null if no data or status other than complete.  Status likely is submitted or failed

ProteinAnnotationStore.prototype._get_DataForProteinId_Common = function( params ) {

	var proteinId = params.proteinId;
	var annotationDataTypeKey = params.annotationDataTypeKey;
	
	var annotationDataForTypeKey = this.getAnnotationDataForKey( annotationDataTypeKey );

	var annotationData_For_proteinId = annotationDataForTypeKey[ proteinId ];

	if ( annotationData_For_proteinId === undefined ) {

		throw Error( "_get_DataForProteinId_Common:   annotationData_For_proteinId === undefined, annotationDataTypeKey: " + annotationDataTypeKey 
				+ ", proteinId: " + proteinId );
	}

	if ( annotationData_For_proteinId.status !== this.CONSTANTS.SERVER_STATUS_VALUES.STATUS_COMPLETE ) {

		return null;
	}

	if ( annotationData_For_proteinId.data === undefined || annotationData_For_proteinId.data === null ) {

		throw Error( "_get_DataForProteinId_Common:   annotationData_For_proteinId.data === undefined || annotationData_For_proteinId.data === null"
				+ "  AND  annotationData_For_proteinId.status === this.CONSTANTS.SERVER_STATUS_VALUES.STATUS_COMPLETE,"
				+ "annotationDataTypeKey: " + annotationDataTypeKey 
				+ ", proteinId: " + proteinId );
	}
	
	return annotationData_For_proteinId.data;

};



////////////////////////////////////////////////////


///  !!!!!!!!!!!  A main call to load protein data


//   If allDataLoaded returned is false, this function has invoked AJAX calls 

//Disopred 2

ProteinAnnotationStore.prototype.loadProteinAnnotationData__Disopred_2_Data = function( params ) {

	
	var loadProteinAnnotationData__Common__Params = {
			
			initialLoadAnnotationsParams : params, //  Pass around the params initially passed in

			annotationType : params.annotationType, 

			selectedProteins : params.selectedProteins, 
			globalMainData : params.globalMainData,

			loadIdentifier : params.loadIdentifier,
			
			
			annotationDataKey : this.CONSTANTS.ANNOTATION_DATA_KEY.DISOPRED_2,

			serviceURLsBase : this.CONSTANTS.SERVER_SERVICES_PATHS.DISOPRED_2

	};
	
	var response = this._loadProteinAnnotationData__Common( loadProteinAnnotationData__Common__Params );
	
	return response;
};






///  !!!!!!!!!!!  A main call to load protein data


//If allDataLoaded returned is false, this function has invoked AJAX calls 

//   Disopred 3

ProteinAnnotationStore.prototype.loadProteinAnnotationData__Disopred_3_Data = function( params ) {


	var loadProteinAnnotationData__Common__Params = {

			initialLoadAnnotationsParams : params, //  Pass around the params initially passed in

			annotationType : params.annotationType, 

			selectedProteins : params.selectedProteins, 
			globalMainData : params.globalMainData,

			loadIdentifier : params.loadIdentifier,


			annotationDataKey : this.CONSTANTS.ANNOTATION_DATA_KEY.DISOPRED_3,

			serviceURLsBase : this.CONSTANTS.SERVER_SERVICES_PATHS.DISOPRED_3

	};

	var response = this._loadProteinAnnotationData__Common( loadProteinAnnotationData__Common__Params );

	return response;
};






////////////////////////////////////////////////////


///  !!!!!!!!!!!  A main call to load protein data


//  If allDataLoaded returned is false, this function has invoked AJAX calls 


// Psipred 3

ProteinAnnotationStore.prototype.loadProteinAnnotationData__Psipred_3_Data = function( params ) {


	var loadProteinAnnotationData__Common__Params = {

			initialLoadAnnotationsParams : params, //  Pass around the params initially passed in
			
			annotationType : params.annotationType, 

			selectedProteins : params.selectedProteins, 
			globalMainData : params.globalMainData,

			loadIdentifier : params.loadIdentifier,


			annotationDataKey : this.CONSTANTS.ANNOTATION_DATA_KEY.PSIPRED_3,

			serviceURLsBase : this.CONSTANTS.SERVER_SERVICES_PATHS.PSIPRED_3

	};

	return this._loadProteinAnnotationData__Common( loadProteinAnnotationData__Common__Params );
};

/////////////////////////////////////////

//   Common Load Code

ProteinAnnotationStore.prototype._loadProteinAnnotationData__Common = function( params ) {

	var objectThis = this;
	
	var selectedProteins = params.selectedProteins; 
	
//	params.globalMainData : {
//	
//	proteinSequences : _proteinSequences,
//	proteinTaxonomyIds : _proteinTaxonomyIds,
//	proteinNames : _proteinNames,
//	proteinLengths : _proteinLengths
//},
	
	

	this.currentLoadIdentifierForSubmit =  Date.now();

	this.currentLoadIdentifierForAllLoaded =  Date.now();

	
	var loadIdentifier = Date.now();
	
	params.loadIdentifier = loadIdentifier;
	
	
//	var loadIdentifier = params.loadIdentifier;
	
	var annotationDataKey = params.annotationDataKey;
	
//	var serviceURLsBase = params.serviceURLsBase;
	
	
	var annotationTypeData = this.getAnnotationDataForKey( annotationDataKey );
	
	
	var allDataLoaded = true;
	
	var submitStatusPerProtein = {};

	
	
	var proteinIdsToSubmit = [];
	
	for ( var selectedProteinsIndex = 0; selectedProteinsIndex < selectedProteins.length; selectedProteinsIndex++ ) {

		var proteinId = selectedProteins[ selectedProteinsIndex ];
		
		var annotationTypeData_proteinId = annotationTypeData[ proteinId ];

		if ( annotationTypeData_proteinId === undefined 
				|| ( annotationTypeData_proteinId.status !== objectThis.CONSTANTS.SERVER_STATUS_VALUES.STATUS_COMPLETE
						&& annotationTypeData_proteinId.status !== objectThis.CONSTANTS.SERVER_STATUS_VALUES.STATUS_FAIL ) ) {
			
			submitStatusPerProtein[ proteinId ] = { responseReceived: false };

			proteinIdsToSubmit.push( proteinId );
		}
	}
	
	for ( var proteinIdsToSubmitIndex = 0; proteinIdsToSubmitIndex < proteinIdsToSubmit.length; proteinIdsToSubmitIndex++ ) {
		
		var proteinIdToSubmit = proteinIdsToSubmit[ proteinIdsToSubmitIndex ];
		
		var _submit_Common_ForProteinId_Params = {
				

				initialLoadAnnotationsParams : params.initialLoadAnnotationsParams, //  Pass around the params initially passed in

				proteinId : proteinIdToSubmit,
				loadIdentifier : params.loadIdentifier,
				
				annotationDataKey : params.annotationDataKey,
				
				serviceURLsBase : params.serviceURLsBase,
				
				submitStatusPerProtein : submitStatusPerProtein,
				selectedProteins : params.selectedProteins,
				
				globalMainData : params.globalMainData
		};
		
		this._submit_Common_ForProteinId( _submit_Common_ForProteinId_Params );
		
		allDataLoaded = false;

	}
	
	var waitingAllSubmitResponse = true;
	
	if ( allDataLoaded ) {
		
		waitingAllSubmitResponse = false;
	}
	

	var proteinAnnotationStore_loadData_response = { allDataLoaded : allDataLoaded, waitingAllSubmitResponse : waitingAllSubmitResponse };
	
	
//	if ( proteinAnnotationStore_loadData_response.allDataLoaded ) {
//	
//	}
	

	return proteinAnnotationStore_loadData_response;

};


////////////////////////////

ProteinAnnotationStore.prototype._submit_Common_ForProteinId = function( params ) {
	
	var objectThis = this;
	
	var proteinId = params.proteinId;
	var loadIdentifier = params.loadIdentifier;
	
	
	var annotationDataKey = params.annotationDataKey;
	
	var serviceURLsBase = params.serviceURLsBase;
	
//	var selectedProteins = params.selectedProteins;
	
	var proteinSequences = params.globalMainData.proteinSequences;
	var proteinTaxonomyIds = params.globalMainData.proteinTaxonomyIds;
	
//	var submitStatusPerProtein = params.submitStatusPerProtein;
	
	
//	params.globalMainData : {
//	
//	proteinSequences : _proteinSequences,
//	proteinTaxonomyIds : _proteinTaxonomyIds,
//	proteinNames : _proteinNames,
//	proteinLengths : _proteinLengths
//},
	
	
		
	var proteinSequence = proteinSequences[ proteinId ];
	
	if ( proteinSequence === undefined ) {
		
		throw Error( "no protein sequence for protein id: " + proteinId );
	}
	
	
	var proteinTaxonomyId = proteinTaxonomyIds[ proteinId ];
	
	if ( proteinTaxonomyId === undefined ) {
		
		throw Error( "no protein Taxonomy Id for protein id: " + proteinId );
	}
	
	
	var requestData = { 
			sequence: proteinSequence,
			ncbiTaxonomyId: proteinTaxonomyId
	};


	var url = this.annotation_data_webservice_base_url + serviceURLsBase.SUBMIT;

	
	if ( CONSOLE_LOGGING ) {
	
		console.log( "Submitting to server to get annotation data for protein id: " + proteinId
				+ ", annotation type: " + annotationDataKey
				+ ", url: " + url );
	}
	
	//  Create new timeout to trigger if don't get a response.  
	//  Will fail to get a response if get HTTP status other than Ok(200), will not get a response for status codes 404, 500, ...
	
	var ajaxTimeoutTimerId = setTimeout(function() {

		alert("PAWS server did not respond within the expected time.  There may be an error.  The Console may hold additional information.");

	}, this.CONSTANTS.AJAX_TIMEOUT);
	
	
	
	$.ajax({
		type: "GET",
		url: url,
		
		dataType: "jsonp",
//		dataType: "json",

		data: requestData,
		
		success: function( responseData )	{
			
			try {

				if ( ajaxTimeoutTimerId ) {

					//   Clear Timeout since have response
					clearTimeout( ajaxTimeoutTimerId );

					ajaxTimeoutTimerId = null;
				}

				var process_submit_Common_ForProteinId_Response_Params = {

						initialLoadAnnotationsParams : params.initialLoadAnnotationsParams, //  Pass around the params initially passed in

						responseData : responseData, 
						proteinId : proteinId, 
						loadIdentifier : loadIdentifier,

						annotationDataKey : annotationDataKey,

						serviceURLsBase : serviceURLsBase,

						selectedProteins : params.selectedProteins, 

						globalMainData : params.globalMainData,

						submitStatusPerProtein : params.submitStatusPerProtein
				};

				objectThis._process_submit_Common_ForProteinId_Response( process_submit_Common_ForProteinId_Response_Params );

			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}

		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
        error: function(jqXHR, textStatus, errorThrown) {	
			
			if ( ajaxTimeoutTimerId ) {

				//   Clear Timeout since have response
				clearTimeout( ajaxTimeoutTimerId );
				
				ajaxTimeoutTimerId = null;
			}
			

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});

	
};

//////////


ProteinAnnotationStore.prototype._process_submit_Common_ForProteinId_Response = function( params ) {

	var objectThis = this;
	

	var responseData = params.responseData; 
	var proteinId = params.proteinId; 
//	var loadIdentifier = params.loadIdentifier;

	var annotationDataKey = params.annotationDataKey;
	
//	var serviceURLsBase = params.serviceURLsBase;
	
//	var selectedProteins = params.selectedProteins;
	
	var proteinTaxonomyIds = params.globalMainData.proteinTaxonomyIds;
	
	var submitStatusPerProtein = params.submitStatusPerProtein;
	
//	params.globalMainData : {
//	
//	proteinSequences : _proteinSequences,
//	proteinTaxonomyIds : _proteinTaxonomyIds,
//	proteinNames : _proteinNames,
//	proteinLengths : _proteinLengths
//},
	
	
//	if ( data.pawsStatus === this.CONSTANTS.SERVER_STATUS_VALUES.STATUS_COMPLETE ) {
//	
//		this.annotationSequenceIds[ proteinId ] = { status: data.pawsStatus, sequenceId: data.sequenceId };
//		
//	} else {
//		
//		this.annotationSequenceIds[ proteinId ] = { status: data.pawsStatus };
//	}
	
	
	
	var annotationTypeData = this.getAnnotationDataForKey( annotationDataKey );


	if ( DEBUG_CONFIRMS ) {
	
		if ( window.confirm( "After initial Submit, Force paws Response status to 'Submitted'?" ) ) {

			responseData.pawsStatus = this.CONSTANTS.SERVER_STATUS_VALUES.STATUS_SUBMITTED;

		} else {

			
			if ( window.confirm( "After initial Submit, Force paws Response status to 'Fail'?" ) ) {

				responseData.pawsStatus = this.CONSTANTS.SERVER_STATUS_VALUES.STATUS_FAIL;
			}
		}
	
	}
	
	
	submitStatusPerProtein[ proteinId ] = { responseReceived: true, pawsStatus : responseData.pawsStatus };
	
	if ( CONSOLE_LOGGING ) {
		
		console.log( "Received response to Submitting to server to get annotation data for protein id: " + proteinId
				+ ", annotation type: " + annotationDataKey + ", pawsStatus: " + responseData.pawsStatus );
	}
	
	var proteinTaxonomyId = proteinTaxonomyIds[ proteinId ];
	
	if ( proteinTaxonomyId === undefined ) {
		
		throw Error( "no protein Taxonomy Id for protein id: " + proteinId );
	}
	
	
	
	annotationTypeData[ proteinId ] = { status: responseData.pawsStatus, sequenceId : responseData.sequenceId, 
			data: responseData.data,  // may not be in "responseData"
			ncbiTaxonomyId : proteinTaxonomyId
	};
	
	
	var checkAllProteinsAfterSubmitResponse_params = {

			initialLoadAnnotationsParams : params.initialLoadAnnotationsParams, //  Pass around the params initially passed in

			annotationDataForType : annotationTypeData,
			selectedProteins : params.selectedProteins,
			loadIdentifier : params.loadIdentifier
	};
	
	this.checkAllProteinsAfterSubmitResponse( checkAllProteinsAfterSubmitResponse_params );

	
	if ( responseData.pawsStatus === this.CONSTANTS.SERVER_STATUS_VALUES.STATUS_COMPLETE
			|| responseData.pawsStatus === this.CONSTANTS.SERVER_STATUS_VALUES.STATUS_FAIL ) {

	} else {
	
		// Start a looping call to check for the data
		
		setTimeout( function() {
			
			//  TODO   Uncomment this when have the blocking modal dialog
			
			
			var get_AnnotationData_Common_ForProteinId_Params = {

					initialLoadAnnotationsParams : params.initialLoadAnnotationsParams, //  Pass around the params initially passed in

					proteinId : proteinId, 
					loadIdentifier : params.loadIdentifier,
					
					annotationDataKey : annotationDataKey,
					
					serviceURLsBase : params.serviceURLsBase,

					selectedProteins : params.selectedProteins, 
					
					globalMainData : params.globalMainData, 

					submitStatusPerProtein : params.submitStatusPerProtein
			};

			
			objectThis._get_AnnotationData_Common_ForProteinId( get_AnnotationData_Common_ForProteinId_Params );
			
		}, this.CONSTANTS.SLEEP_TIME_BEFORE_POLL_FOR_RESULT );
		
	}


	
};

//////////

ProteinAnnotationStore.prototype._get_AnnotationData_Common_ForProteinId = function( params ) {

	var objectThis = this;

	var proteinId = params.proteinId; 
//	var loadIdentifier = params.loadIdentifier;

	var annotationDataKey = params.annotationDataKey;
	
	var serviceURLsBase = params.serviceURLsBase;
	
//	var selectedProteins = params.selectedProteins; 
	
	var proteinTaxonomyIds = params.globalMainData.proteinTaxonomyIds;
	
//	var submitStatusPerProtein = params.submitStatusPerProtein;

	
//	params.globalMainData : {
//	
//	proteinSequences : _proteinSequences,
//	proteinTaxonomyIds : _proteinTaxonomyIds,
//	proteinNames : _proteinNames,
//	proteinLengths : _proteinLengths
//},
	
	
	
	if ( ! this.isContinueCheckingForCompleteStatus( { loadIdentifier : params.loadIdentifier } ) ) {

		if ( CONSOLE_LOGGING ) {
			
			console.log( "_get_AnnotationData_Common_ForProteinId:  "
					+ "isContinueCheckingForCompleteStatus() returns false so END checking for Complete status for protein id: " + proteinId
					+ ", annotation type: " + annotationDataKey );
		}
		
		return;
	}

	var annotationTypeData = this.getAnnotationDataForKey( annotationDataKey );

	
	var annotationTypeData_For_proteinId = annotationTypeData[ proteinId ];

	if ( annotationTypeData_For_proteinId === undefined 
			|| annotationTypeData_For_proteinId.status === objectThis.CONSTANTS.SERVER_STATUS_VALUES.STATUS_NO_RECORD ) {
		
		throw Error( "_get_AnnotationData_Common_ForProteinId: Unable to get PAWS proteinSequenceId for protein id: " + proteinId );
	}
	
	var sequenceId = annotationTypeData_For_proteinId.sequenceId;
	

	if ( sequenceId === undefined ) {
		
		throw Error( "_get_AnnotationData_Common_ForProteinId: sequenceId not populated in annotationTypeData_For_proteinId.sequenceId for"
			+ " protein id: " + proteinId );
	}
	
	var proteinTaxonomyId = proteinTaxonomyIds[ proteinId ];
	
	if ( proteinTaxonomyId === undefined ) {
		
		throw Error( "no protein Taxonomy Id for protein id: " + proteinId );
	}
	
	

	var requestData = { 
			sequenceId: sequenceId,
			ncbiTaxonomyId: proteinTaxonomyId
	};


	
	var url = this.annotation_data_webservice_base_url + serviceURLsBase.GET;

	
	if ( CONSOLE_LOGGING ) {
		
		console.log( "Sending Get annotation data for protein id: " + proteinId
				+ ", annotation type: " + annotationDataKey + ", PAWS sequenceId: " + sequenceId
				+ ", url: " + url );
	}
	
	
	//  Create new timeout to trigger if don't get a response.  
	//  Will fail to get a response if get HTTP status other than Ok(200), will not get a response for status codes 404, 500, ...
	
	var ajaxTimeoutTimerId = setTimeout(function() {

		alert("PAWS server did not respond within the expected time.  There may be an error.  The Console may hold additional information.");

	}, this.CONSTANTS.AJAX_TIMEOUT);
	
	
	$.ajax({
		type: "GET",
		url: url,
		
		dataType: "jsonp",
//		dataType: "json",

		data: requestData,
		success: function( responseData )	{

			try {

				if ( ajaxTimeoutTimerId ) {

					//   Clear Timeout since have response
					clearTimeout( ajaxTimeoutTimerId );

					ajaxTimeoutTimerId = null;
				}

				var _get_AnnotationData_Common_ForProteinId_Response_Params = {

						initialLoadAnnotationsParams : params.initialLoadAnnotationsParams, //  Pass around the params initially passed in

						responseData : responseData,

						proteinId : proteinId, 
						loadIdentifier : params.loadIdentifier,

						annotationDataKey : params.annotationDataKey,

						serviceURLsBase : params.serviceURLsBase,

						selectedProteins : params.selectedProteins, 

						globalMainData : params.globalMainData,

						submitStatusPerProtein : params.submitStatusPerProtein
				};

				objectThis._get_AnnotationData_Common_ForProteinId_Response( _get_AnnotationData_Common_ForProteinId_Response_Params );
			} catch( e ) {
				reportWebErrorToServer.reportErrorObjectToServer( { errorException : e } );
				throw e;
			}
		},
        failure: function(errMsg) {
        	handleAJAXFailure( errMsg );
        },
        error: function(jqXHR, textStatus, errorThrown) {	

			if ( ajaxTimeoutTimerId ) {

				//   Clear Timeout since have response
				clearTimeout( ajaxTimeoutTimerId );
				
				ajaxTimeoutTimerId = null;
			}

			handleAJAXError( jqXHR, textStatus, errorThrown );
		}
	});

	
};


////////////


ProteinAnnotationStore.prototype._get_AnnotationData_Common_ForProteinId_Response = function( params ) {

	var objectThis = this;
	
//	if ( data.pawsStatus === this.CONSTANTS.SERVER_STATUS_VALUES.STATUS_COMPLETE ) {
//	
//		this.annotationSequenceIds[ proteinId ] = { status: data.pawsStatus, sequenceId: data.sequenceId };
//		
//	} else {
//		
//		this.annotationSequenceIds[ proteinId ] = { status: data.pawsStatus };
//	}
	
	var responseData = params.responseData;
	
	var proteinId = params.proteinId; 
//	var loadIdentifier = params.loadIdentifier;

	var annotationDataKey = params.annotationDataKey;
	
//	var serviceURLsBase = params.serviceURLsBase;
	
	var proteinTaxonomyIds = params.globalMainData.proteinTaxonomyIds;
//	var submitStatusPerProtein = params.submitStatusPerProtein;
	

//	params.globalMainData : {
//	
//	proteinSequences : _proteinSequences,
//	proteinTaxonomyIds : _proteinTaxonomyIds,
//	proteinNames : _proteinNames,
//	proteinLengths : _proteinLengths
//},

	var annotationTypeData = this.getAnnotationDataForKey( annotationDataKey );
	
	
	if ( ! this.isContinueCheckingForCompleteStatus( { loadIdentifier : params.loadIdentifier } ) ) {

		if ( CONSOLE_LOGGING ) {
			
			console.log( "_get_AnnotationData_Common_ForProteinId_Response:  "
					+ "isContinueCheckingForCompleteStatus() returns false so END checking for Complete status for protein id: " + proteinId
					+ ", annotation type: " + annotationDataKey + ", PAWS sequenceId: " + responseData.sequenceId
					+ ", pawsStatus: " + responseData.pawsStatus );
		}
		
		return;
	}

	if ( CONSOLE_LOGGING ) {
		
		console.log( "_get_AnnotationData_Common_ForProteinId_Response:  "
				+ "Processing Response to Sending Get annotation data for protein id: " + proteinId
				+ ", annotation type: " + annotationDataKey + ", PAWS sequenceId: " + responseData.sequenceId
				+ ", pawsStatus: " + responseData.pawsStatus );
	}

	if ( DEBUG_CONFIRMS ) {
	
		if ( window.confirm( "After subsequent Get, Force paws Response status to 'Submitted'?" ) ) {

			responseData.pawsStatus = this.CONSTANTS.SERVER_STATUS_VALUES.STATUS_SUBMITTED;

		} else {

			if ( window.confirm( "After subsequent Get, Force paws Response status to 'Fail'?" ) ) {

				responseData.pawsStatus = this.CONSTANTS.SERVER_STATUS_VALUES.STATUS_FAIL;
			}

		}
	}
	
	
	if ( responseData.pawsStatus === this.CONSTANTS.SERVER_STATUS_VALUES.STATUS_COMPLETE
			|| responseData.pawsStatus === this.CONSTANTS.SERVER_STATUS_VALUES.STATUS_FAIL ) {
		
		annotationTypeData[ proteinId ] = { status: responseData.pawsStatus, sequenceId : responseData.sequenceId, data: responseData.data };
		
		
		var processGetAnnotationData_Response_CompleteOrFail_params = {

				initialLoadAnnotationsParams : params.initialLoadAnnotationsParams, //  Pass around the params initially passed in

				annotationDataForType : annotationTypeData,
				selectedProteins : params.selectedProteins,
				loadIdentifier : params.loadIdentifier
		};
		
		this._processGetAnnotationData_Response_CompleteOrFail( processGetAnnotationData_Response_CompleteOrFail_params );

		
		
	} else {
		
		
		var proteinTaxonomyId = proteinTaxonomyIds[ proteinId ];
		
		if ( proteinTaxonomyId === undefined ) {
			
			throw Error( "no protein Taxonomy Id for protein id: " + proteinId );
		}
		
		
		annotationTypeData[ proteinId ] = { 
				status: responseData.pawsStatus, 
				sequenceId : responseData.sequenceId, 
				ncbiTaxonomyId : proteinTaxonomyId
				
		};
		
		setTimeout( function() {
			

			var get_AnnotationData_Common_ForProteinId_Params = {

					initialLoadAnnotationsParams : params.initialLoadAnnotationsParams, //  Pass around the params initially passed in

					proteinId : params.proteinId, 
					loadIdentifier : params.loadIdentifier,
					
					annotationDataKey : params.annotationDataKey,
					
					serviceURLsBase : params.serviceURLsBase,

					selectedProteins : params.selectedProteins, 
					globalMainData : params.globalMainData,
					submitStatusPerProtein : params.submitStatusPerProtein
			};

			
			objectThis._get_AnnotationData_Common_ForProteinId( get_AnnotationData_Common_ForProteinId_Params );
			
		}, this.CONSTANTS.SLEEP_TIME_BEFORE_POLL_FOR_RESULT );
	}
};


//////////////////////////////////////////////

ProteinAnnotationStore.prototype.isContinueCheckingForCompleteStatus = function( params ) {
	
	var loadIdentifier = params.loadIdentifier;

	if ( loadIdentifier === this.currentLoadIdentifierForAllLoaded ) {
		
		return true;
	}
	
	return false;
};



//////////////////////////////////////////////


ProteinAnnotationStore.prototype.checkAllProteinsAfterSubmitResponse = function( params ) {
	
	

	var annotationDataForType = params.annotationDataForType;
	var selectedProteins = params.selectedProteins;
	
	var loadIdentifier = params.loadIdentifier;

	if ( loadIdentifier === this.currentLoadIdentifierForSubmit ) {

		if ( this.isAllDataHaveResponseFromServerForType( annotationDataForType, selectedProteins ) ) {



			this.currentLoadIdentifierForSubmit = undefined;  // clear it
			
			var functionCallbacks = params.initialLoadAnnotationsParams.functionCallbacks;
			
			
			var isOneOrMoreHasFailedStatusForTypeResult = this.isOneOrMoreHasFailedStatusForType( annotationDataForType, selectedProteins );
						
			if ( this.isAllDataLoadedForType( annotationDataForType, selectedProteins ) ) {
				
				functionCallbacks.annotationLoadingAllCompleteOnSubmit( params.initialLoadAnnotationsParams );

			} else if ( isOneOrMoreHasFailedStatusForTypeResult.atLeastOneHasStatusFailed ) {
				
				var annotationLoadingAtLeastOneFailedOnSubmitParams = {
						
						proteinIdsThatFailed : isOneOrMoreHasFailedStatusForTypeResult.proteinIdsThatFailed
				};
				
				functionCallbacks.annotationLoadingAtLeastOneFailedOnSubmit( params.initialLoadAnnotationsParams, annotationLoadingAtLeastOneFailedOnSubmitParams );
				
			} else if ( this.isOneOrMoreHasSubmittedStatusForType( annotationDataForType, selectedProteins ) ) {  

				functionCallbacks.annotationLoading_NOT_AllCompleteOnSubmit( params.initialLoadAnnotationsParams );
			}
		}
	}
};


///////////////


ProteinAnnotationStore.prototype._processGetAnnotationData_Response_CompleteOrFail = function( params ) {



	var annotationDataForType = params.annotationDataForType;
	var selectedProteins = params.selectedProteins;

	var loadIdentifier = params.loadIdentifier;

	if ( loadIdentifier === this.currentLoadIdentifierForAllLoaded ) {

		if ( this.isAllDataHaveResponseFromServerForType( annotationDataForType, selectedProteins ) ) {



			var functionCallbacks = params.initialLoadAnnotationsParams.functionCallbacks;
			
			
			var isOneOrMoreHasFailedStatusForTypeResult = this.isOneOrMoreHasFailedStatusForType( annotationDataForType, selectedProteins );
			
			
			if ( this.isAllDataLoadedForType( annotationDataForType, selectedProteins ) ) {
	
				if ( CONSOLE_LOGGING ) {
					
					console.log("_processGetAnnotationData_Response_CompleteOrFail. isAllDataLoadedForType(...) returned true, calling functionCallbacks.annotationLoadingAllCompleteOnGet(...) ");
				}
				
				this.currentLoadIdentifierForAllLoaded = undefined;  // clear it


				functionCallbacks.annotationLoadingAllCompleteOnGet( params.initialLoadAnnotationsParams );
				
			} else if ( isOneOrMoreHasFailedStatusForTypeResult.atLeastOneHasStatusFailed ) {

				if ( CONSOLE_LOGGING ) {
					
					console.log("_processGetAnnotationData_Response_CompleteOrFail. isOneOrMoreHasFailedStatusForType(...) returned true, calling functionCallbacks.annotationLoadingAtLeastOneFailedOnGet(...) ");
				}
				
				this.currentLoadIdentifierForAllLoaded = undefined;  // clear it

				var annotationLoadingAtLeastOneFailedOnGetParams = {
						
						proteinIdsThatFailed : isOneOrMoreHasFailedStatusForTypeResult.proteinIdsThatFailed
				};
				
				functionCallbacks.annotationLoadingAtLeastOneFailedOnGet( params.initialLoadAnnotationsParams, annotationLoadingAtLeastOneFailedOnGetParams );
				
			}
		}
	}

};



///////////



ProteinAnnotationStore.prototype.isAllDataHaveResponseFromServerForType = function( annotationDataForType, selectedProteins ) {


	var allDataHaveStatusFromServer = true;

	for ( var selectedProteinsIndex = 0; selectedProteinsIndex < selectedProteins.length; selectedProteinsIndex++ ) {

		var proteinId = selectedProteins[ selectedProteinsIndex ];

		if ( annotationDataForType[ proteinId ] === undefined ) {

			allDataHaveStatusFromServer = false;
		}
	}

	return allDataHaveStatusFromServer;

};



//////////////////////////////////////////////


ProteinAnnotationStore.prototype.isAllDataLoadedForType = function( annotationDataForType, selectedProteins ) {

	var objectThis = this;


	var allDataLoaded = true;

	for ( var selectedProteinsIndex = 0; selectedProteinsIndex < selectedProteins.length; selectedProteinsIndex++ ) {

		var proteinId = selectedProteins[ selectedProteinsIndex ];

		if ( annotationDataForType[ proteinId ] === undefined 
				|| annotationDataForType[ proteinId ].status !== objectThis.CONSTANTS.SERVER_STATUS_VALUES.STATUS_COMPLETE ) {

			allDataLoaded = false;
		}
	}
	
	return allDataLoaded;

};





ProteinAnnotationStore.prototype.isOneOrMoreHasFailedStatusForType = function( annotationDataForType, selectedProteins ) {

	var objectThis = this;

	var atLeastOneHasStatusFailed = false;
	
	var proteinIdsThatFailed = [];

	for ( var selectedProteinsIndex = 0; selectedProteinsIndex < selectedProteins.length; selectedProteinsIndex++ ) {

		var proteinId = selectedProteins[ selectedProteinsIndex ];

		if ( annotationDataForType[ proteinId ] !== undefined 
				&& annotationDataForType[ proteinId ].status === objectThis.CONSTANTS.SERVER_STATUS_VALUES.STATUS_FAIL ) {

			atLeastOneHasStatusFailed = true;
			
			proteinIdsThatFailed.push( proteinId );
		}
	}

	return { atLeastOneHasStatusFailed : atLeastOneHasStatusFailed, proteinIdsThatFailed : proteinIdsThatFailed };

};

//////////////////////////////////////////////


ProteinAnnotationStore.prototype.isOneOrMoreHasSubmittedStatusForType = function( annotationDataForType, selectedProteins ) {

	var objectThis = this;
	
	var atLeastOneHasStatusSubmitted = false;

	for ( var selectedProteinsIndex = 0; selectedProteinsIndex < selectedProteins.length; selectedProteinsIndex++ ) {

		var proteinId = selectedProteins[ selectedProteinsIndex ];

		if ( annotationDataForType[ proteinId ] !== undefined 
				&& annotationDataForType[ proteinId ].status === objectThis.CONSTANTS.SERVER_STATUS_VALUES.STATUS_SUBMITTED ) {
			
			atLeastOneHasStatusSubmitted = true;
		}
	}
	
	return atLeastOneHasStatusSubmitted;
	
};


///////////////////////////////////////////////////////////


/////////   The following functions are for specific kinds of protein annotations 

/////////      and are not part of the class


/////////////////////////////////////////////


var DISORDERED = "DISORDERED";



function getDisorderedRegionsDisopred_2( proteinId ) {
	
	
	var disorderedBlocks = [];

	
	var disorderedData =
		proteinAnnotationStore.get_disopred_2_DataForProteinId( proteinId );
	
	if ( disorderedData === null ) {
		
		return null;
	}
	
	////   Break the disorderedData into regions of ordered and disordered
	
	
	var disorderedDataEntries = disorderedData.entries;


	var prevType = "";

	var startPositionInitializationValue = -1;

	var startPosition = startPositionInitializationValue;
	var endPosition = startPositionInitializationValue;
	
	

	for ( var dataIndex = 0; dataIndex < disorderedDataEntries.length; dataIndex++ ) {

		var entry = disorderedDataEntries[ dataIndex ];
		

		var type = entry.type;

		if ( type === DISORDERED ) {


			var position = entry.position;

			if ( type !== prevType || position > ( endPosition + 1 )  ) {

				if ( startPosition !== startPositionInitializationValue ) {

					//  not first group so save this group
					
					disorderedBlocks.push({ startPosition: startPosition, endPosition: endPosition, type: prevType } );

				}

				startPosition = position;
			}
			endPosition = position;

			prevType = type;

		}

	}
	
	if ( startPosition !== startPositionInitializationValue ) {

		//  add last group

		disorderedBlocks.push({ startPosition: startPosition, endPosition: endPosition, type: prevType } );
	}

	
	return disorderedBlocks;
	
}


//////////////////



function getDisorderedRegionsDisopred_3( proteinId ) {
	
	
	var disorderedBlocks = [];

	
	var disoPred3Data =
		proteinAnnotationStore.get_disopred_3_DataForProteinId( proteinId );
	
	if ( disoPred3Data === null ) {
		
		return null;
	}
	
	var disorderedData = disoPred3Data.disopredParsedDisorderedResiduesPredictions;
	
	
	////   Break the disorderedData into regions of ordered and disordered
	
	
	var disorderedDataEntries = disorderedData.entries;


	var prevType = "";

	var startPositionInitializationValue = -1;

	var startPosition = startPositionInitializationValue;
	var endPosition = startPositionInitializationValue;
	
	

	for ( var dataIndex = 0; dataIndex < disorderedDataEntries.length; dataIndex++ ) {

		var entry = disorderedDataEntries[ dataIndex ];
		

		var type = entry.type;

		if ( type === DISORDERED ) {


			var position = entry.position;

			if ( type !== prevType || position > ( endPosition + 1 )  ) {

				if ( startPosition !== startPositionInitializationValue ) {

					//  not first group so save this group
					
					disorderedBlocks.push({ startPosition: startPosition, endPosition: endPosition, type: prevType } );

				}

				startPosition = position;
			}
			endPosition = position;

			prevType = type;

		}

	}
	
	if ( startPosition !== startPositionInitializationValue ) {

		//  add last group

		disorderedBlocks.push({ startPosition: startPosition, endPosition: endPosition, type: prevType } );
	}

	
	return disorderedBlocks;
	
}


/////////////////////////////////////////
	
var BETA_SHEET = "E";
var ALPHA_HELIX = "H";



function getSecondaryStructureRegions( proteinId ) {
	
	
	var ssBlocks = [];

	
	var secondaryStructureData =
		proteinAnnotationStore.get_psipred_3_DataForProteinId( proteinId );
	
	if ( secondaryStructureData === null ) {
		
		return null;
	}
	
	////   Break the secondaryStructureData 
	
	
	var secondaryStructureDataEntries = secondaryStructureData.entries;


	var prevType = "";

	var startPositionInitializationValue = -1;

	var startPosition = startPositionInitializationValue;
	var endPosition = startPositionInitializationValue;
	
	

	for ( var dataIndex = 0; dataIndex < secondaryStructureDataEntries.length; dataIndex++ ) {

		var entry = secondaryStructureDataEntries[ dataIndex ];
		

		var type = entry.type;


		if ( type === BETA_SHEET || type === ALPHA_HELIX ) {

		
			var position = entry.position;
			
			
			if ( type !== prevType || position > ( endPosition + 1 )  ) {
				
				if ( startPosition !== startPositionInitializationValue ) {
					
					//  not first group so save this group
					
					ssBlocks.push( { startPosition: startPosition, endPosition: endPosition, type: prevType  } );
				}
									
				startPosition = position;
			}
			endPosition = position;

			prevType = type;
		}
	}
	
	if ( startPosition !== startPositionInitializationValue ) {

		//  add last group
		
		ssBlocks.push( { startPosition: startPosition, endPosition: endPosition, type: prevType  } );
	}
	
	return ssBlocks;
	
}



