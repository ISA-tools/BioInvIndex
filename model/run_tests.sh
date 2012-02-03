mvn -DargLine="-Xms512m -Xmx4G -XX:PermSize=128m -XX:MaxPermSize=256m" \
    -Dsurefire.useFile=true
   -Ph2,test clean test -Dtest=BioEntityPersistenceTest

mvn surefire-report:report-only
