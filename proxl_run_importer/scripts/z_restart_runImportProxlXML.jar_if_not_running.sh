#!/bin/bash

###  See output in x_restart_runImportProxlXML.jar_if_not_running.sh__status.txt
###  (placed there since will run in cron)

#  Must have same value in:
#	this script
#	script: ./z_run__runImportProxlXML.jar_StartBackground_ForRestartIfNotRunningScript.sh
#	Run Importer Program config file (property name 'importer.pid.file.with.path') 
#		(the program removes the pid file when shuts down using run control file)
pid_file=x_runProxlImporter_pid

output_status_file=x_restart_runImportProxlXML.jar_if_not_running.sh__status.txt


mail_program=/bin/mail

mail_to_1="ZZZ@WWW.com"
##  Duplicate each line before where mail_to_1 is used if add variables like mail_to_2, ...

#  Then use "ps -p $pid" and check exit code for zero to confirm is running.  Exit code non-zero of ps means process not found.

if [ ! -f $pid_file ]; then

	#  No PID file so just exit.  
	##  No PID file means that either:
	##	1) the run importer was shut down via the run control file
	##	2) the OS user removed it to prevent auto restart
	##	3) the run importer was not started with the correct script

	echo "pid file not found so exit.  pid file: ${pid_file}" > $output_status_file
	exit
fi

pidValue=$(<"${pid_file}")

pid_read_status=$?

if [[ $ps_exit_code -ne 0 ]]; then

	mail_subject="Server XXX: Run Proxl Importer: failed to read pid file (already tested for not exist)."
	mail_body="Failed to read PID file ${pid_file} (already tested for not exist).  Unable to test if program is running."

	echo "${mail_body}" > $output_status_file

	###  Duplicate this line for each email address to mail to
	echo "${mail_body}" | ${mail_program} -s "${mail_subject}" "${mail_to_1}"

	exit
fi

# echo pid read status $pid_read_status

# echo pid read $pidValue

#  Check if pid exists, non-zero exit code if not exists

ps -p "${pidValue}" > /dev/null 2> /dev/null

ps_exit_code=$?

if [[ $ps_exit_code -ne 0 ]]; then

	#  pid not exist so start run importer
#	echo not running pid $pidValue

	mail_subject="Server XXX: Run Proxl Importer was not running so it was started."
	mail_body="PID file ${pid_file} contained PID ${pidValue} that was not found with command ps -p ${pidValue} so the script 	z_run__runImportProxlXML.jar_StartBackground_ForRestartIfNotRunningScript.sh was executed to start the program."

	echo "${mail_body}" > $output_status_file

	###  Duplicate this line for each email address to mail to
	echo "${mail_body}" | ${mail_program} -s "${mail_subject}" "${mail_to_1}"

	#  Run script to start the program
	./z_run__runImportProxlXML.jar_StartBackground_ForRestartIfNotRunningScript.sh
fi

