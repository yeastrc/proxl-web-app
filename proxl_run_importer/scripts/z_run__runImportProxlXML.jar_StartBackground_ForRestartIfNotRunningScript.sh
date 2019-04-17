#!/bin/bash

###  !!!   If running z_restart_runImportProxlXML.jar_if_not_running.sh in cron:

###		!!!   Must remove file in variable '$pid_file' manually if kill the run importer, 
###			otherwise the restart script will restart the process


#  Must have same value in:
#	this script
#	script: z_run__runImportProxlXML.jar_StartBackground_ForRestartIfNotRunningScript.sh
#	Run Importer Program config file (property name 'importer.pid.file.with.path') 
#		(the program removes the pid file when shuts down using run control file)
pid_file=x_run_ProxlImporter_pid

java -jar runImportProxlXML.jar \
--config=run_importer_config_file.properties  \
--max_tracking_record_priority_to_retrieve=50	\
 > x_runProxlImporter_sysout.txt 2> x_runProxlImporter_syserr.txt &

###  Must start run importer in the bacground with "&" in order to get PID this way
###    with next statement

run_importer_pid=$!

echo $run_importer_pid > ${pid_file}

#  Then use "ps -p $pid" and check exit code for zero to confirm is running.  Exit code non-zero of ps means process not found.


