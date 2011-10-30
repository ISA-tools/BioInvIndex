#!/bin/sh

# Runs the H2 test DB in server mode. Can be useful if you want to use your
# IDE's DB browser.
#
# Once launched, use a JDBC driver with the parameters:
#
#   driver org.h2.Driver
#   url    jdbc:h2:tcp://localhost/test-db/bioinvindex
#   db     bioinvindex
#   user   sa
#   passwd <none>
#
H2PATH=/Applications/local/dev/jlib/h2/bin/h2.jar
DBPATH=$(dirname $0)/../../../bii-db/test-db
echo Running the DB: "$DBPATH"
java -cp $H2PATH org.h2.tools.Server -tcp -ifExists true -trace -baseDir $DBPATH
