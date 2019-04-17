#!/bin/bash

java -jar runImportProxlXML.jar \
--config=run_importer_config_file.properties  \
--max_tracking_record_priority_to_retrieve=50	\
 > x_runProxlImporter_sysout.txt 2> x_runProxlImporter_syserr.txt

