# I need to live with two Maven versions, usually leaving MVN unset will be just fine
if [ "$MVN" == "" ]; then
  echo "Setting 'mvn' as default Maven command, you possibly need to setup Maven 2 via export MVN=<path> before invoking me"
  MVN=mvn
fi 
#MVN="mvn2 -s $HOME/.m2_old/settings.xml -Dmaven.repo.local=$HOME/.m2_old/repository"

$MVN -DargLine="-Xms512m -Xmx4G -XX:PermSize=128m -XX:MaxPermSize=256m" \
    -Dsurefire.useFile=true \
    -Ph2,test clean test

$MVN surefire-report:report-only
