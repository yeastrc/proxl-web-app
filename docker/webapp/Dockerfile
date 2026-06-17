FROM tomcat:11.0-jdk25-temurin-noble
MAINTAINER Michael Riffle <mriffle@uw.edu>

COPY mysql-connector-java-8.0.26.jar /usr/local/tomcat/lib
COPY context.xml /usr/local/tomcat/conf
COPY setenv.sh /usr/local/tomcat/bin

COPY proxl.war /usr/local/tomcat/webapps
