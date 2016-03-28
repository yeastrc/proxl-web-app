===========================================
ProXL Installation Guide
===========================================

This is a guide for downloading and installing your own instance of ProXL
on your own system. ProXL is relatively simple to install and set up, but does require
some working proficiency with the command line and some knowledge of databases
(preferably MySQL).

ProXL comprises a database component and a web application component. Downloading,
installing, and configuring these components are described below.

1. Install nrseq-fasta-importer.
==========================================================
ProXL makes use of the ``nrseq-fasta-importer`` web application and its associated databases
to process FASTA files and associate peptides, reported protein names, and link positions with
full protein sequences. This system provides independence from any particular naming database and
allows results to be compared between experiments regardless of which protein naming
database was used in each experiment.

Instructions for installing and using the ``nrseq-fasta-importer`` web application and databases are
available at at `<http://nrseq-fasta-importer.readthedocs.org/en/latest/>`_.


2. Install MySQL, Java, and Apache Tomcat (if necessary)
==========================================================

This documentation assumes that `Java <http://www.java.com/>`_ (JDK version, 1.7 or later) and the
`Apache Tomcat <http://tomcat.apache.org/>`_ (7 or later) servlet container are installed on the same
computer on which you are installing ProXL. (Note: Apache Tomcat requires the JDK version of Java be
installed.)

This documentation also assumes that `MySQL <http://www.mysql.com/>`_ (5.6 or later) has been
installed and is accessible by the installation of Apache Tomcat. This does not need to be on the
same machine as Apache Tomcat.

ProXL should work equally well on any operating system for which
MySQL and Java are available (MS Windows, Apple OS X, or Linux). Other servlet containers and database
server software may work as well, though this documentation assumes that the above are being used.
Please refer to the respective websites for more information about MySQL, Java, or Apache Tomcat
installation.

You may need to download and install the MySQL JDBC driver. This is available from the 
`MySQL Connector/J <http://dev.mysql.com/downloads/connector/j/>`_ website. To install, copy
the downloaded jar file into ``$CATALINA_HOME/lib`` directory on the server on which Apache Tomcat
is installed (e.g. /usr/local/apache-tomcat-7.0.65/lib) and restart Tomcat.

3. Set up the proxl database
==========================================================

Create the ``proxl`` database.
-----------------------------------
To set up this database, first download :download:`create_empty_database.sql <../../database_scripts/install/create_empty_database.sql>`.

To run this SQL script, do one of the following:
    * Log into your MySQL server and paste in the file contents.
    * Source the file by logging into MySQL and typing the following at the MySQL prompt: ``source /location/to/create_empty_database.sql``. (Be sure MYSQL has read access to the file).
    * At the command line: ``cat /location/to/create_empty_database.sql | mysql -u your_username -p``

Populate the ``proxl`` database with needed data.
-------------------------------------------------------
Download :download:`insert_initial_data.sql <../../database_scripts/install/insert_initial_data.sql>`.

To run this SQL script, do one of the following:
    * Log into your MySQL server and paste in the file contents.
    * Source the file by logging into MySQL and typing the following at the MySQL prompt: ``source /location/to/insert_initial_data.sql``. (Be sure MYSQL has read access to the file).
    * At the command line: ``cat /location/to/insert_initial_data.sql | mysql -u your_username -p``

Add in initial ProXL configuration.
-------------------------------------------------------
Configuration in ProXL is done via the ``config_system`` table in the ``proxl`` database. To populate
this table with initial configuration information, first download :download:`insert_initial_data.sql <../../database_scripts/install/insert_initial_proxl_config.sql>`.
Edit this file in a text editor, and change the value for your SMTP server (if necessary). This is a server through which email is sent, and such a service is likely
already running on your server (localhost), or is provided to you by your institution (e.g., smtp.uw.edu). Also be sure to configure the "from" address, from which
ProXL emails will be sent (e.g., proxl@your.institution.edu). No other values need be changed.

Once changed, run this SQL script by doing one of the following:
    * Log into your MySQL server and paste in the file contents.
    * Source the file by logging into MySQL and typing the following at the MySQL prompt: ``source /location/to/insert_initial_proxl_config.sql``. (Be sure MYSQL has read access to the file).
    * At the command line: ``cat /location/to/insert_initial_proxl_config.sql | mysql -u your_username -p``

(Optional) Populate the ``taxonomy`` table.
-------------------------------------------------------
In order to provide functionality based on taxonomy (such as filtering searches based on taxonomy of hit proteins), the
``taxonomy`` table must be populated. To populate this table, download :download:`insert_initial_data.sql <../../database_scripts/install/create_taxonomy_table.sql>` and
execute the SQL script doing one of the following (note, file is too big to paste in the contents from a text editor):

    * Source the file by logging into MySQL and typing the following at the MySQL prompt: ``source /location/to/create_taxonomy_table.sql``. (Be sure MYSQL has read access to the file).
    * At the command line: ``cat /location/to/create_taxonomy_table.sql | mysql -u your_username -p``


4. Install and configure the web application
==========================================================

Add MySQL user for Tomcat access
------------------------------------------
Follow these instructions to set up access for Tomcat to access the MySQL databases.

|	Log in to MySQL as root:
|	``shell> mysql -u root mysql``
|	
|	Create the MySQL user:
|	``mysql> CREATE USER 'proxl_user'@'localhost' IDENTIFIED BY 'password';``	
|
|	Replace ``proxl_user`` with the username you would prefer, ``localhost`` with the
|	relative hostname of the machine connecting to the MySQL database (usually localhost),
|	and ``password`` with your preferred password.
|
|	Grant the necessary privileges in MySQL:
|	``GRANT ALL ON proxl.* TO 'proxl_user'@'localhost'``	
|	``GRANT SELECT ON YRC_NRSEQ.* TO 'proxl_user'@'localhost'``
|
|	Replace ``proxl_user`` and ``localhost`` with the username and hostname you used
|	when creating the user. Note that ``YRC_NRSEQ`` was installed as part of the nrseq-fasta-importer setup.
|

Configure Tomcat to access proxl database
---------------------------------------------------------

Add the following to ``$CATALINA_HOME/conf/context.xml``, inside the ``<Context></Context>`` root
element. Be sure to change ``proxl_user`` and ``password`` to the username and password you set
up above. If necessary, change ``localhost`` and ``3306`` to the hostname and port of your
MySQL server.
	
.. code-block:: xml
	
          <Resource     name="jdbc/proxl"
                        auth="Container"
                        type="javax.sql.DataSource"
                        factory="org.apache.commons.dbcp.BasicDataSourceFactory"
                        maxActive="100"
                        maxIdle="30"
                        maxWait="10000"
                        username="proxl_user"
                        password="password"
                        driverClassName="com.mysql.jdbc.Driver"

                        minEvictableIdleTimeMillis="14400000"
                        timeBetweenEvictionRunsMillis="3600000"
                        numTestsPerEvictionRun="100"

                        url="jdbc:mysql://localhost:3306/proxl?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=UTF-8&amp;characterSetResults=UTF-8"/>



Install proxl.war
------------------------------
To install the ProXL web application, first download latest release of the ``proxl.war`` file
from `<https://github.com/yeastrc/proxl-web-app/releases>`_. Then copy this file into the
``webapps`` directory of your Tomcat installation. It should automatically deploy (you should
see a ``proxl`` directory created in the webapps directory. If it does not automatically deploy,
restart Tomcat to force it to deploy.

5. Start using ProXL
==========================================================
Your web application should now be available at http://your.host:8080/proxl/
(Depending on how you have configured your web server, the ``:8080`` may not be different or
not required.) If you have a firewall running, will need to allow access through this port.
You should be able to log in with username: ``initial_proxl_user`` and
password: ``FJS483792nzmv,xc4#&@(!VMKSDL``  You should change this information at your soonest
convenience by logging in and clicking the "Manage Account" icon at the top-right of any page
(gear-shaped icon). You may add initial users by creating projects and inviting users to those projects.

For information about uploading data and using ProXL, please see the documentation at `<http://proxl-web-app.readthedocs.org/en/latest/>`_.
