

--  This config

use proxl;


--  Used for accessing protein annotations services.  

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('protein_annotation_webservice_url', '');



-- !!!!!!!!!!!!!!!!!!

--   Email configuration

--   Required that 'email_webservice_url' or 'email_smtp_server_url' has a value

--  Required that 'email_from_address' has a value

--   The Email webservice at yeastrc.org

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('email_webservice_url', '');
 

--   The SMTP server to send to, used if 'email_webservice_url' is null or empty string

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('email_smtp_server_url', '');
 

--   The from email address for sent email

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('email_from_address', '');
 


