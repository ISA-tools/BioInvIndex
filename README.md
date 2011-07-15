# BioInvestigation Index - the Database model, persistence code, webservices and web application.


- General info: <http://isa-tools.org>
- Issue tracking and bug reporting: <https://github.com/ISA-tools/BioInvIndex/issues>
- Mainline source code: <https://github.com/ISA-tools/BioInvIndex>
- Twitter: [@isatools](http://twitter.com/isatools)
- IRC: [irc://irc.freenode.net/#isatab](irc://irc.freenode.net/#isatab)
- Development blog: <http://isatools.wordpress.com>

## Installation

**Build dependencies:**
    These are all managed by Maven <http://maven.apache.org/>. You'll just need to install it and then let maven handle everything else 

**Get the source:**

    git clone https://github.com/ISA-tools/BioInvIndex.git
    cd BioInvIndex/
    mvn clean package -Pdeploy,<<your_database_profile>>,<<your_index_profile>> -Dmaven.test.skip=true

The profiles.xml file contains example database connection profiles for H2, Oracle, MySQL and PostGreSQL. If you have no database yet, you can use H2 by running this command for instance
    mvn clean package -Pdeploy,h2,index_local -Dmaven.test.skip=true


### Refreshing your clone

A simple `git pull origin master` should suffice!

### Contributing

1. Fork it.
2. Create a branch (`git checkout -b mybii`)
3. Make your changes
4. Run the tests (`mvn clean test -Ptest,h2,index_local`)
5. Commit your changes (`git commit -am "Added something useful"`)
6. Push to the branch (`git push origin mybii`)
7. Create a [Pull Request](http://help.github.com/pull-requests/) from your branch.
8. Promote it. Get others to drop in and +1 it.

#### Contributor License Agreement

Before we can accept any contributions to the BioInvIndex codebase, you need to sign a [CLA](http://en.wikipedia.org/wiki/Contributor_License_Agreement):

Please email us <isatools@googlegroups.com> to receive the CLA. Then you should sign this and send it back asap so we can add you to our development pool.

> The purpose of this agreement is to clearly define the terms under which intellectual property has been contributed to the BioInvestigation Index and thereby allow us to defend the project should there be a legal dispute regarding the software at some future time.

For a list of contributors, please see <http://github.com/ISA-tools/BioInvIndex/contributors>

## License

The BioInvIndex code and resources are licensed under the Mozilla Public License (MPL) version
 1.1/GPL version 2.0/LGPL version 2.1