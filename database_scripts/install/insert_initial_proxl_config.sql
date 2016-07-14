

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


--  (Optional) Used for adding the Google Analytics Javascript containing the configured tracking code to the footer of every page.  
--  Default, not set
 INSERT INTO config_system (config_key, config_value) 
 VALUES ('google_analytics_tracking_code', '' );



--  (Optional) Used for accessing protein annotations services.  
--  Default, the Yeast Resource Center will provide real-time protein annotation
--  web services requests.
 INSERT INTO config_system (config_key, config_value) 
 VALUES ('protein_annotation_webservice_url', 'http://yeastrc.org/paws/services/');


--  (Optional) Used for accessing protein listing services.  

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('protein_listing_from_sequence_taxonomy_webservice_url', '');



--  (!Important!) Items on Web Pages configuration

--  (Optional) HTML displayed at bottom center of page  
--  HTML that will be displayed at the bottom center of the page.

--  Example: Managed by Michael Riffle (<a href="mailto:mriffle@uw.edu" target="_top">mriffle@uw.edu</a>) 

--  Cached in memory so if change database, need to wait or change in web app

 INSERT INTO config_system (config_key, config_value) 
 VALUES ('footer_center_of_page_html', '');


