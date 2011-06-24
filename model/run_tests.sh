# I need to live with two Maven versions, usually the first will be just fine
MVN=mvn 
#MVN="mvn2 -s $HOME/.m2_old/settings.xml"

$MVN -DargLine="-Xms512m -Xmx4G -XX:PermSize=128m -XX:MaxPermSize=256m" \
    -Dsurefire.useFile=true \
    -Ph2,test clean test

$MVN surefire-report:report-only
