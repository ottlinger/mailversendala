# mailversendala
Prototyp to send out mass emails

## Technology used

* JavaMail
* CSV stuff from Apache

## Project Status

[![Stories in Backlog](https://badge.waffle.io/ottlinger/mailversendala.png?label=backlog&title=Backlog)](https://waffle.io/ottlinger/mailversendala)
[![Stories in Ready](https://badge.waffle.io/ottlinger/mailversendala.png?label=ready&title=Ready)](https://waffle.io/ottlinger/mailversendala)
[![Stories in Progress](https://badge.waffle.io/ottlinger/mailversendala.png?label=in%20progress&title=In%20Progress)](https://waffle.io/ottlinger/mailversendala)
[![Known Vulnerabilities](https://snyk.io/test/github/ottlinger/mailversendala/badge.svg)](https://snyk.io/test/github/ottlinger/mailversendala)
[![GPL v3.0](https://img.shields.io/github/license/ottlinger/mailversendala.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

## Github integration
### Travis / CI

In order to just play around with it I've integrated a CI run:

[![Build Status](https://travis-ci.org/ottlinger/mailversendala.svg?branch=master)](https://travis-ci.org/ottlinger/mailversendala)

### Code coverage

[![codecov](https://codecov.io/gh/ottlinger/mailversendala/branch/master/graph/badge.svg)](https://codecov.io/gh/ottlinger/mailversendala)

### Codacy - code quality and static analysis

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c8fc0c6ef3d14192a2a8f84a670ccb92)](https://www.codacy.com/app/github_25/mailversendala)

### VersionEye - dependency update management

Not activated for this project, but still a very cool integration: example PHP project

[![Dependency versions](https://www.versioneye.com/user/projects/58978d3ea35eb6002e873a36/badge.svg)](https://www.versioneye.com/user/projects/58978d3ea35eb6002e873a36?child=summary)


## How to configure the application

In order to properly configure the application you need to set some mail-related properties.
Please use the file [javaconfiguration.properties.template](./src/main/resources/META-INF/javaconfiguration.properties.template) and save it as *javaconfiguration.properties*.

### Available configuration modes

If you are running the application as a fat JAR you may want to overwrite each of the configuration key either environment variable or runtime property:
```
$ java -jar target/mailversendala-0.1-executable.jar
// runs with default parameters from within the JAR

$ export csvpath=/tmp/my.csv; java -jar target/mailversendala-0.1-executable.jar
// configuration set as environment variable

$ java -Dcsvpath=/tmp/my.csv -jar target/mailversendala-0.1-executable.jar
// configuration option set as JVM parameter
```


## Copyright

&copy; P. Ottlinger, 2017
