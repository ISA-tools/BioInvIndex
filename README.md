# BioInvestigation Index - the Database model, persistence code, webservices and web application.

<p align="center">
<img src="http://isatools.files.wordpress.com/2011/09/bii1.png" align="center" alt="BioInvestigation Index"/>
</p>

- General info: <http://isa-tools.org>
- Issue tracking and bug reporting: <https://github.com/ISA-tools/BioInvIndex/issues>
- Mainline source code: <https://github.com/ISA-tools/BioInvIndex>
- Twitter: [@isatools](http://twitter.com/isatools)
- IRC: [irc://irc.freenode.net/#isatab](irc://irc.freenode.net/#isatab)
- Development blog: <http://isatools.wordpress.com>

## Installation

**Build dependencies:**

Even though there are newer installations of JBoss and Maven available, please do not use them, simply because we have not tested with those yet. 
You may think it to be trivial to provide support for latest version, but this is not the case. There are major changes in JBoss 6+ which are not 
yet supported by dependencies we use in our codebase. 

1. JBoss 5/5.1 <http://www.jboss.org/jbossas/downloads/> (not 6+)
2. Maven 2.2.1 <http://maven.apache.org/download.html> (not 3+)
3. All Java dependencies etc are all managed by Maven <http://maven.apache.org/>. You'll just need to install it and then let maven handle everything else 

**Get the source:**

    git clone https://github.com/ISA-tools/BioInvIndex.git
    cd BioInvIndex/
    mvn clean package -Pdeploy,<<your_database_profile>>,<<your_index_profile>> -Dmaven.test.skip=true

The profiles.xml file contains example database connection profiles for H2, Oracle, MySQL and PostGreSQL. If you have no database yet, you can use H2 by running this command for instance:

    mvn clean package -Pdeploy,h2,index_local -Dmaven.test.skip=true


### Refreshing your clone

A simple `git pull origin master` should suffice!

## Contributing

You should read this article about Git Flow: <http://scottchacon.com/2011/08/31/github-flow.html>. It's a really useful tutorial on how to use Git for collaborative development.

1. Fork it.
2. Clone your forked repository to your machine
3. Create a branch (`git checkout -b mybii`)
4. Make your changes
5. Run the tests (`mvn clean test -Ptest,h2,index_local`)
6. Commit your changes (`git commit -am "Added something useful"`)
7. Push to the branch (`git push origin mybii`)
8. Create a [Pull Request](http://help.github.com/pull-requests/) from your branch.
9. Promote it. Get others to drop in and +1 it.

#### Contributor License Agreement

Before we can accept any contributions to the BioInvIndex codebase, you need to sign a [CLA](http://en.wikipedia.org/wiki/Contributor_License_Agreement):

Please email us <isatools@googlegroups.com> to receive the CLA. Then you should sign this and send it back asap so we can add you to our development pool.

> The purpose of this agreement is to clearly define the terms under which intellectual property has been contributed to the BioInvestigation Index and thereby allow us to defend the project should there be a legal dispute regarding the software at some future time.

For a list of contributors, please see <http://github.com/ISA-tools/BioInvIndex/contributors>

## License

The BioInvIndex code and resources are licensed under the Mozilla Public License (MPL) version
 1.1/GPL version 2.0/LGPL version 2.1