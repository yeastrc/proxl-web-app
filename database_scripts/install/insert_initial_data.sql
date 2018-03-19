
USE proxl ;


--  INSERT INITIAL VALUES USED BY WEBAPP TO HANDLE PERMISSIONS

INSERT INTO xl_user_access_level_label_description (xl_user_access_level_numeric_value, label, description) 
	VALUES ( 0, 'admin', 'application wide, admin' );
	
INSERT INTO xl_user_access_level_label_description (xl_user_access_level_numeric_value, label, description) 
	VALUES ( 25, 'create new project', 'application wide, can create new project' );

INSERT INTO xl_user_access_level_label_description (xl_user_access_level_numeric_value, label, description) 
	VALUES ( 30, 'project owner', 'control all aspects of project' );

INSERT INTO xl_user_access_level_label_description (xl_user_access_level_numeric_value, label, description) 
	VALUES ( 38, 'assistant project owner', 'change most aspects of project except add/remove other assistant project owners' );

INSERT INTO xl_user_access_level_label_description (xl_user_access_level_numeric_value, label, description) 
	VALUES ( 40, 'update project and delete runs', 'update project and delete runs' );

INSERT INTO xl_user_access_level_label_description (xl_user_access_level_numeric_value, label, description) 
	VALUES ( 50, 'update project but not delete runs', 'update project but not delete runs' );

INSERT INTO xl_user_access_level_label_description (xl_user_access_level_numeric_value, label, description) 
	VALUES ( 99, 'read project', 'not able to make changes to project' );

INSERT INTO xl_user_access_level_label_description (xl_user_access_level_numeric_value, label, description) 
	VALUES ( 9999, 'no access', 'at project level, no access to that project, at application wide level, no access to any project' );

	
	


--  Search table status values (in field status_id)	

	--  These values must be kept in sync with the values in the Java class SearchRecordStatus
	
INSERT INTO search_record_status_lookup (id, display_text) VALUES ( 1, 'importing' );
INSERT INTO search_record_status_lookup (id, display_text) VALUES ( 2, 'import complete/view' );
INSERT INTO search_record_status_lookup (id, display_text) VALUES ( 3, 'import fail' );
INSERT INTO search_record_status_lookup (id, display_text) VALUES ( 4, 'import canceled/incomplete' );
INSERT INTO search_record_status_lookup (id, display_text) VALUES ( 5, 'marked for deletion' );
INSERT INTO search_record_status_lookup (id, display_text) VALUES ( 6, 'deletion in progress' );

--  Import via web app lookup values	

	--  These values must be kept in sync with the values in the Java class ProxlXMLFileImportFileType
	
INSERT INTO proxl_xml_file_import_tracking_single_file_type_lookup (id, display_text) VALUES ( 1, 'Proxl XML File' );
INSERT INTO proxl_xml_file_import_tracking_single_file_type_lookup (id, display_text) VALUES ( 2, 'Scan File' );

	--  These values must be kept in sync with the values in the Java class ProxlXMLFileImportFileUploadStatus
	
INSERT INTO proxl_xml_file_import_tracking_single_file_upload_status_lookup (id, display_text) VALUES ( 1, 'Record Inserted' );
INSERT INTO proxl_xml_file_import_tracking_single_file_upload_status_lookup (id, display_text) VALUES ( 2, 'File Upload Started' );
INSERT INTO proxl_xml_file_import_tracking_single_file_upload_status_lookup (id, display_text) VALUES ( 3, 'File Upload Complete' );




	--  These values must be kept in sync with the values in the Java class ProxlXMLFileImportStatus
    
INSERT INTO proxl_xml_file_import_tracking_status_values_lookup (id, display_text) VALUES ( 1, 'init_insert_pre_queued' );
INSERT INTO proxl_xml_file_import_tracking_status_values_lookup (id, display_text) VALUES ( 2, 'queued' );
INSERT INTO proxl_xml_file_import_tracking_status_values_lookup (id, display_text) VALUES ( 3, 're-queued' );
INSERT INTO proxl_xml_file_import_tracking_status_values_lookup (id, display_text) VALUES ( 4, 'started' );
INSERT INTO proxl_xml_file_import_tracking_status_values_lookup (id, display_text) VALUES ( 5, 'complete' );
INSERT INTO proxl_xml_file_import_tracking_status_values_lookup (id, display_text) VALUES ( 6, 'failed' );


	--  These values must be kept in sync with the values in the Java class ProxlXMLFileImportRunSubStatus
    
INSERT INTO proxl_xml_file_import_tracking_run_sub_status_values_lookup (id, display_text) VALUES ( 1, 'system error' );
INSERT INTO proxl_xml_file_import_tracking_run_sub_status_values_lookup (id, display_text) VALUES ( 2, 'data error' );
INSERT INTO proxl_xml_file_import_tracking_run_sub_status_values_lookup (id, display_text) VALUES ( 3, 'project not allow import' );

	
--  INSERT SUPPORTED CROSS-LINKERS

INSERT INTO linker(abbr,name)VALUES( 'bmoe','bismaleimidoethane' );
INSERT INTO linker(abbr,name)VALUES( 'dss','disuccinimidyl suberate' );
INSERT INTO linker(abbr,name)VALUES( 'dsg','disuccinimidyl glutarate' );
INSERT INTO linker(abbr,name)VALUES( 'bs3','bis[sulfosuccinimidyl] suberate' );
INSERT INTO linker(abbr,name)VALUES( 'bs2','bis(sulfosuccinimidyl) glutarate' );
INSERT INTO linker(abbr,name)VALUES( 'edc','1-ethyl-3-(3-dimethylaminopropyl)carbodiimide hydrochloride' );
INSERT INTO linker(abbr,name)VALUES( 'dfdnb','1,5-difluoro-2,4-dinitrobenzene' );
INSERT INTO linker(abbr,name)VALUES( 
	'dss.sty','disuccinimidyl suberate that includes sty links on one half of the link' );
INSERT INTO linker(abbr,name)VALUES( 
	'bs3.sty','bis[sulfosuccinimidyl] suberate that includes sty links on one half of the link' );
INSERT INTO linker(abbr,name)VALUES( 'sulfo-smcc','sulfosuccinimidyl 4-[N-maleimidomethyl]cyclohexane-1-carboxylate');
INSERT INTO linker(abbr,name)VALUES( 'dsso','disuccinimidyl sulfoxide' );

--  Insert entries into isotope_label

--     Do NOT add new entries without adding support in the Proxl code (and probably libraries that Proxl code uses)

--  id for 'none' is hard coded in Java in class IsotopeLabelsConstants
INSERT INTO isotope_label (id, name) VALUES ( 1, "none" );

--  The rest of records use auto increment for id
INSERT INTO isotope_label (name) VALUES ( "13C" );
INSERT INTO isotope_label (name) VALUES ( "15N" );
INSERT INTO isotope_label (name) VALUES ( "18O" ); -- (this is the letter O not zero)
INSERT INTO isotope_label (name) VALUES ( "2H" );

--  INSERT to config_system to connect to YRC services for protein listing and protein annotation (paws)

INSERT INTO config_system (config_key, config_value) VALUES ('protein_annotation_webservice_url', 'http://yeastrc.org/paws/services/');
INSERT INTO config_system (config_key, config_value) VALUES ('protein_listing_from_sequence_taxonomy_webservice_url', 'http://yeastrc.org/pdr/services/');
