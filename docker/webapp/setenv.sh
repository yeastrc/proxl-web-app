#  Java options only.  Nothing secret here:  the DB user, password and database name reach context.xml through the
#  process environment (see the Dockerfile), never through a -D on the command line (visible in 'ps' on the host).
JAVA_OPTS="-server"

CATALINA_OPTS="${WEBAPP_JAVA_OPTIONS}"
