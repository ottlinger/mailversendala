# mailversendala

Application to send out mass emails based on a list of recipients in a CSV file.

## CSV format

The application can be fed with a CSV file (separator is ,). It needs to have a header line and the following naming for columns:

| firstname  | surname | email |
| --------------------- | ----------- | ----------------------- |
| Your | Family | foo@bar.com |

Email needs to be a complete mail address.
An example file is [mailversendala-example.csv](./src/test/resources/mailversendala-example.csv).

## Project webpage

A Maven-generated site report is also available [here](https://ottlinger.github.io/mailversendala/).

## Project Status

[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/ottlinger/mailversendala.svg)](http://isitmaintained.com/project/ottlinger/mailversendala "Average time to resolve an issue")
[![Percentage of issues still open](http://isitmaintained.com/badge/open/ottlinger/mailversendala.svg)](http://isitmaintained.com/project/ottlinger/mailversendala "Percentage of issues still open")
[![Known Vulnerabilities](https://snyk.io/test/github/ottlinger/mailversendala/badge.svg)](https://snyk.io/test/github/ottlinger/mailversendala)
[![ASF 2.0](https://img.shields.io/github/license/ottlinger/mailversendala.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

https://github.com/users/ottlinger/projects/9

## GitHub integrations
### CI / GHA

[![GitHub Action master branch status](https://github.com/ottlinger/mailversendala/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/ottlinger/mailversendala/actions)

### Code coverage

[![codecov](https://codecov.io/gh/ottlinger/mailversendala/branch/master/graph/badge.svg)](https://codecov.io/gh/ottlinger/mailversendala)

### Codacy - code quality and static analysis

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f27ed60267144c1a88e12f0539ff9a6a)](https://app.codacy.com/gh/ottlinger/mailversendala/dashboard)
## How to run and use the application

You need to checkout the application and build it with the help of [Java](https://java.sun.com) and [Maven](https://maven.apache.org/).

```
$ git clone https://github.com/ottlinger/mailversendala.git
Cloning into 'mailversendala'...
...

$ ./mvnw
....
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 8.864 s
[INFO] Final Memory: 34M/481M
[INFO] ------------------------------------------------------------------------

 $ java -jar target/mailversendala-1.0.0-SNAPSHOT-executable.jar
2024-12-22 00:48:00,714 DEBUG d.a.m.MailConfig [main] Reading Tamaya configuration ...
Dez. 22, 2024 12:48:00 AM org.apache.tamaya.spi.ServiceContextManager loadDefaultServiceProvider
INFORMATION: Using Service Context of type: org.apache.tamaya.spisupport.DefaultServiceContext
2024-12-22 00:48:00,755 DEBUG d.a.m.MailConfig [main] Configuration: DONE.
2024-12-22 00:48:00,755 INFO d.a.m.Mailversendala [main] **** MAILVERSENDALA: Starting .... ****
2024-12-22 00:48:00,757 INFO d.a.m.Mailversendala [main] Consuming CSV: mailversendala-example.csv
2024-12-22 00:48:00,758 INFO d.a.m.Mailversendala [main] mailversendala-example.csv
2024-12-22 00:48:00,758 WARN d.a.m.Mailversendala [main] Nothing to do - please configure your CSV path properly, either as environment variable or as a runtime parameter. Example: java -Dcsvpath=foo -jar fatJar.jar
2024-12-22 00:48:00,758 INFO d.a.m.Mailversendala [main] **** MAILVERSENDALA: Application shutdown .... ****
2024-12-22 00:48:00,758 INFO d.a.m.App [main] Mailversendala is shutting down. Bye Bye :-)
```

In case you want to use the application productively you need to configure it, please continue reading the following paragraph.

## How to configure the application

In order to properly configure the application you need to set some mail-related properties.
Please use the file [javaconfiguration.properties.template](./src/test/resources/META-INF/javaconfiguration.properties.template) and save it as *javaconfiguration.properties*.

Technically the configuration is based on [Apache Tamaya (incubating)](https://tamaya.apache.org)

### Available configuration modes

If you are running the application as a fat JAR you may want to overwrite each of the configuration key either environment variable or runtime property:
```
$ java -jar target/mailversendala-1.0.0-SNAPSHOT-executable.jar
// runs with default parameters from within the JAR

$ export csvpath=/tmp/my.csv; java -jar target/mailversendala-1.0.0-SNAPSHOT-executable.jar
// configuration set as environment variable

$ java -Dcsvpath=/tmp/my.csv -jar target/mailversendala-1.0.0-SNAPSHOT-executable.jar
// configuration option set as JVM parameter
```

### Configurable program options

The following parameters can be set:

| Configuration option  | Description | Example/default setting |
| --------------------- | ----------- | ----------------------- |
| host | SMTP mail host | smtp.example.com |
| port | SMTP host port | 465 |
| username | SMTP plaintext username | user@example.com |
| password | SMTP plaintext password | chooseMeWisely |
| demomode | boolean, whether to not send mails | true |
| csvpath | fully qualified path to your CSV data input file | /tmp/mailversendala-example.csv |
| templatepath | fully qualified path to your template directory. This directory needs to contain mailtemplate.html and mailtemplate.txt | /tmp/mailversendala |
| subject | full subject line of the example mail | Whatever you want is a subject line |
| from | mail address that is sent from | santa@cruz.com |
| to | mail address that an example mail is sent to **deprecated** | xmas@man.com |

### Development

In order to properly integrate the project into your IDE you need to setup lombok support -
please follow the guidelines about integration, e.g. [Eclipse integration](https://projectlombok.org/setup/eclipse).

## Copyright

&copy; P. Ottlinger, 2017
