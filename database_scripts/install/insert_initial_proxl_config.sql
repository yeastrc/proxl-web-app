

--  ProXL configuration is done via the database. 

use proxl;

--  (!Important!) Email configuration

--  (Required) The address of the SMTP server, through which to send email.
--  E.g. smtp.your.institution.com  Default: localhost
--  A valid SMTP host is required to send emails.

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('email_smtp_server_url', 'localhost');
 

--  (Required) The from email address from which emails from ProXL
--  (such as sending out project invites) will be sent.
--  E.g., proxl@your.institution.edu

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('email_from_address', '');
 

--  (Optional) Used for accessing protein annotations services.  
--  Default, the Yeast Resource Center will provide real-time protein annotation
--  web services requests.
 INSERT INTO config_system (config_key, config_value) 
 VALUES ('protein_annotation_webservice_url', 'http://yeastrc.org/paws/services/');


--  (Optional) Used for accessing protein listing services.  

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('protein_listing_webservice_url', '');






