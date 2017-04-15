===========================================
Proxl Installation Guide
===========================================

This is a guide for downloading and installing your own copy of proxl, running on
you own server. proxl is relatively simple to install and set up, but does require
some working proficiency with the command line and some knowledge of databases
(preferably MySQL).

Proxl comprises a database component and a web application component. Downloading,
installing, and configuring these components are described below.

1. Install MySQL, Java, and Apache Tomcat (if necessary)
==========================================================

This documentation assumes that `Java <http://www.oracle.com/technetwork/java/javase/downloads/index.html>`_ (JDK version, 1.8 or later) and the
`Apache Tomcat <http://tomcat.apache.org/>`_ (8 or later) servlet container are installed on the same
computer on which you are installing proxl.

This documentation also assumes that `MySQL <http://www.mysql.com/>`_ (5.6 or later) has been
installed and is accessible by the installation of Apache Tomcat. This does not need to be on the
same machine as Apache Tomcat.

Proxl should work equally well on any operating system for which
MySQL and Java are available (MS Windows, Apple OS X, or Linux). Other servlet containers and database
server software may work as well, though these have not been tested.

After Apache Tomcat is installed, you will need to download and install the MySQL JDBC driver. This is available free of charge from the 
`MySQL Connector/J <http://dev.mysql.com/downloads/connector/j/>`_ website. To install, copy
the downloaded jar file into lib directory of Apache Tomcat  (e.g. /usr/local/apache-tomcat-7.0.65/lib)
and restart Tomcat.

2. Download the latest release of proxl
==========================================================
Download the latest release at `<https://github.com/yeastrc/proxl-web-app/releases>`_. Unzip
the contents of the zip file to the directory of your choice.

3. Set up the proxl database
==========================================================
All database creation and population scripts are in the ``database_scripts/install`` directory of the zip file.

Execute the following sql scripts in this order.

    * ``create_empty_database.sql``
    * ``create_empty_database_add_tables.sql``
    * ``insert_initial_data.sql``
    * ``insert_initial_user.sql``
    

To run these SQL scripts, do one of the following:
    * Log into your MySQL server and paste in the file contents.
    * Source the file by logging into MySQL and typing the following at the MySQL prompt: ``source /location/to/script_name.sql``. (Be sure MYSQL has read access to the file).
    * At the command line (on macos or linux): ``cat /location/to/script_name.sql | mysql -u your_username -p``

(Optional) Populate the ``taxonomy`` table.
-------------------------------------------------------
In order to provide functionality based on taxonomy (such as filtering searches based on taxonomy of hit proteins), the
``taxonomy`` table must be populated. To populate this table, execute the ``create_taxonomy_table.sql`` SQL script.

4. Install and configure the web application
==========================================================

Add MySQL user for Tomcat access
------------------------------------------
Follow these instructions to set up access for Tomcat to access the MySQL databases.

|	Log in to MySQL as root:
|	``shell> mysql -u root -p``
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
|
|	Replace ``proxl_user`` and ``localhost`` with the username and hostname you used
|	when creating the user.
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
Copy ``proxl.war`` in the top directory of the zip file into the
``webapps`` directory of your Tomcat installation. It should automatically deploy (you should
see a ``proxl`` directory created in the webapps directory. If it does not automatically deploy,
restart Tomcat to force it to deploy.

5. Start using proxl
==========================================================
Your web application should now be available at http://your.host:8080/proxl/.
If you have a firewall running, may need to allow access through this port.
You should be able to log in with username: ``initial_proxl_user`` and
password: ``FJS483792nzmv,xc4#&@(!VMKSDL``  You should change this information at your soonest
convenience by logging in and clicking the "Manage Account" icon at the top-right of any page
(person-shaped icon). You may add initial users by creating projects and inviting users to those projects.

For information about uploading data and using proxl, please see the documentation at `<http://proxl-web-app.readthedocs.org/en/latest/>`_.
