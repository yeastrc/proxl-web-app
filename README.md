ProXL XML Importer Documentation
==============================================

Use the ProXL XML importer to import data from ProXL XML files into your proxl database.
Please see the instructions below for getting started using the importer.

Setting up the database connection
----------------------------------
The importer reads from and inserts data into the proxl database.
Access to the database must be set up before the importer can be run.
This only needs to be done once. These instructions assume you have
followed the ProXL installation instructions found at
http://proxl-web-app.readthedocs.org/en/latest/.

To set up a MySQL account for the importer:

Log in to MySQL as root:
``shell> mysql -u root mysql``

Create the MySQL user:
``mysql> CREATE USER 'proxl_importer'@'localhost' IDENTIFIED BY 'password';``	

Replace ``proxl_importer`` with the username you would prefer, ``localhost`` with the
relative hostname of the machine connecting to the MySQL database (usually localhost),
and ``password`` with your preferred password.

Grant the necessary privileges in MySQL:

``GRANT ALL ON proxl.* TO 'proxl_importer'@'localhost'``	

Replace ``proxl_importer`` and ``localhost`` with the username and hostname you used
	when creating the user.


Database config file
----------------------------------
The database config file is a simple text file containing the information necessary for the
importer to connect to the database (i.e., where the database is and login credentials). The
location of this file is passed into the importer when it is run. The format of this file is:
	
	# db connection parameters
	username=proxl_importer
	password=PASSWORD
	
	dbHost=localhost
	dbPort=3306
	
	
	#  override database names if changed from defaults
	proxl.db.name=

Save the above as a file (e.g., db_config_file.properties), and edit and change values as necessary.
The properties are:

    * username : The username you created in the first step above.
    * password : The password you created in the first step above.
    * dbHost : The hostname of your MySQL server. Usually localhost (if on the same machine).
    * dbPort : The port of your MySQL server. Probably 3306, which is the default port for MySQL.
    * (optional) proxl.db.name : If for some reason you changed the name of the proxl database, it goes here.


Running the importer
------------------------------
Download the latest release of the importer from https://github.com/yeastrc/proxl-import-xml-to-db/releases. Use
Java to run the importer from the command line as: ``java -jar importProxlXML.jar``. Running with no arguments will
show help for the importer, which is given below:

	Usage: java -jar importProxlXML.jar -p project_id  \
	                                    -i import_file.proxl.xml \
	                                    -c db_connection.properties \
	                                    [ -n ] [ -s scan_file.mzML ]
									  	
	
	Examples:
	
	    To import a Proxl XML file (with no scans):
	    java -jar importProxlXML.jar -p 1 -i proxlInput.xml -n -c /path/to/db.properties
	
	    To import a Proxl XML file and related scan file:
	    java -jar importProxlXML.jar -p 1 -i proxlInput.xml -s /path/to/file.mzML -c /path/to/db.properties
		
		
	Parameters:
	
	    -p  : [Required] : The id for the ProXL project into which the data are being loaded.
	
	    -i (--import-file=) : [Required] : The Proxl XML file to import, with it's relative or absolute path.
		
	    -c (--config=) : [Required] : The Database configuration file, with it's relative or absolute path.
	    
	                                  File must contain the following key/value pairs:
	                                  
	                                  username=your_db_username
	                                  password=your_db_password
	                                  dbHost=your_host (e.g., localhost)
	                                  dbPort=3306 (default port for MySQL)
	                                  
	                                  For more information, see the importer documentation at
	                                  https://github.com/yeastrc/proxl-import-xml-to-db
	
		
	    -s (--scan-file=) : [Optional] : The scan file ( mzML or mzXML ), with it's relative or absolute path.
									  
	                                     -s can be repeated multiple times if multiple scan files will be imported.
									  
	    -n (--no-scan-files) : [Optional] : Must be set if no Scan files are present, the Proxl XML file
	                                        will be imported without any scan files. May not be set if
	                                        -s is used.
										
Getting More Help
------------------
If you have any difficulties running the importer, please email us at proxl-help@yeastrc.org.


