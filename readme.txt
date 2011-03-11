To build the web application

1. Download Maven 2.2.1 from here http://www.apache.org/dyn/closer.cgi/maven/source/apache-maven-2.2.1-src.zip and follow
   the instructions in its readme file to install.
2. Edit the relevant database profile in profiles.xml. You can also specify where the application should look for the index folder,
   see an example via the index_local profile.
3. run this command:
    mvn clean package -Pdeploy,<<your_db_profile_id>>,index_local -Dmaven.test.skip=true
4. This command will build an ear (bii-<<version>>.ear) in the ear/target directory. You should copy this ear into your JBoss
   directory in server/default/deploy
5. To run JBoss (you may want to give it more memory), go to the bin directory in your JBoss installation and execute run.sh
   or run.bat (depending on your system) with the options -c default, e.g. ./run.sh -c default.