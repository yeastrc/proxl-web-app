
export ANT_OPTS="-Xmx1024m -XX:MaxPermSize=512m"

ant -f ant__build_all_proxl.xml > ZZ_ant_out.txt 2>&1 
