FROM amazoncorretto:11-alpine-jdk

COPY db_config_file.properties  /usr/local/bin/
COPY run_importer_config_file.properties  /usr/local/bin/
COPY importProxlXML.jar  /usr/local/bin/
COPY runImportProxlXML.jar  /usr/local/bin/

ENTRYPOINT ["java", "-jar", "/usr/local/bin/runImportProxlXML.jar", "-c", "/usr/local/bin/run_importer_config_file.properties"]
