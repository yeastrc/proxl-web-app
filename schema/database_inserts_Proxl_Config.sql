

--  This config

use proxl;


--  Used for accessing protein annotations services.  

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('protein_annotation_webservice_url', '');


--  Used for accessing protein listing services.  

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('protein_listing_webservice_url', '');



-- !!!!!!!!!!!!!!!!!!

--   Email configuration

--   Required that 'email_smtp_server_url' has a value

--  Required that 'email_from_address' has a value

--   The SMTP server to send to

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('email_smtp_server_url', 'localhost');
 

--   The from email address for sent email

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('email_from_address', '');
 


