mvn -DargLine="-Xms256m -Xmx2048m -XX:PermSize=64m -XX:MaxPermSize=128m" \
    -Dsurefire.useFile=true \
    -Ph2,test clean test

mvn surefire-report:report-only
