FROM tomcat:11.0-jdk25-temurin-noble
MAINTAINER Michael Riffle <mriffle@uw.edu>

# Contents copied into Tomcat's lib/ (e.g. mysql-connector-j-<version>.jar)
COPY tomcat_lib_jars/ /usr/local/tomcat/lib/
COPY context.xml /usr/local/tomcat/conf

#  ${PROXL_MYSQL_USER}, ${PROXL_MYSQL_PASSWORD} and ${PROXL_DATABASE_NAME} in context.xml are replaced from the
#  process ENVIRONMENT (set by docker-compose.yml), so no password is on the java command line (visible in 'ps' on the host).
RUN echo "org.apache.tomcat.util.digester.PROPERTY_SOURCE=org.apache.tomcat.util.digester.EnvironmentPropertySource" >> ${CATALINA_HOME}/conf/catalina.properties

COPY setenv.sh /usr/local/tomcat/bin

COPY proxl.war /usr/local/tomcat/webapps
