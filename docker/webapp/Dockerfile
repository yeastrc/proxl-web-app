FROM tomcat:9-jdk11-corretto
MAINTAINER Michael Riffle <mriffle@uw.edu>

COPY mysql-connector-java-8.0.26.jar /usr/local/tomcat/lib
COPY context.xml /usr/local/tomcat/conf
COPY setenv.sh /usr/local/tomcat/bin

COPY proxl.war /usr/local/tomcat/webapps
