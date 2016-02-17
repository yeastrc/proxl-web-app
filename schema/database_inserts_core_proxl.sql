

USE proxl;

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

	

INSERT INTO filter_direction_values (id, value_string) VALUES (1, 'above');
INSERT INTO filter_direction_values (id, value_string) VALUES (2, 'below');
	
	