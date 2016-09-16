
Setting up an IDE for subfolder 'proxl_importer'.

The Java code in subfolder 'proxl_importer' 
are dependent on the the Java code in '../proxl_web_app/src_shared_code'

For building the runnable jars in this directory, 
the ant scripts first run the ant script 
'./proxl_web_app/ant_build_shared_code_jar.xml'


For configuring an IDE to reference the Java code on '../proxl_web_app/src_shared_code'
one of the following is suggested:

1)  Run the ant script './proxl_web_app/ant_build_shared_code_jar.xml'
	and put the jar 'lib_built_proxl_shared_code_jar/proxl_shared_code.jar'
	in the class path.
	If this is done, any changes to '../proxl_web_app/src_shared_code' will require
	a re-run of the ant script './proxl_web_app/ant_build_shared_code_jar.xml'
	before those change will be visible in this project.
	
2)	Reference the project created for folder 'proxl_web_app'.
	The down side is that all the web app classes will appear but not be in the jar
	the runnable jar is built with.

3)	Link the Java code folder '../proxl_web_app/src_shared_code' into this project.

