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

## GitHub integrations
### CI / GHA

[![GitHub Action master branch status](https://github.com/ottlinger/mailversendala/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/ottlinger/mailversendala/actions)

### Code coverage

[![codecov](https://codecov.io/gh/ottlinger/mailversendala/branch/master/graph/badge.svg)](https://codecov.io/gh/ottlinger/mailversendala)

### Codacy - code quality and static analysis

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/f27ed60267144c1a88e12f0539ff9a6a)](https://app.codacy.com/gh/ottlinger/mailversendala/dashboard)
## How to run and use the application

You need to checkout the application and build it with the help of [Java](https://java.sun.com) (17 or newer) and [Maven](https://maven.apache.org/).

Mail delivery uses Commons Email 2's Jakarta variant (`2.0.0-M1`, currently a milestone release)
with Eclipse Angus as the Jakarta Mail/Activation implementation. Local delivery tests use
GreenMail 2.1.x with the same mail stack; the executable JAR includes the runtime providers
but not GreenMail or the legacy `javax.mail`/`javax.activation` APIs.

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

If you are running the application as a fat JAR you may want to overwrite each of the configuration keys either by environment variable or runtime property:
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
| templatepath | Path to the external UTF-8 Velocity template directory | /tmp/mailversendala |
| htmltemplate | HTML template filename, relative to templatepath | mailtemplate.html |
| plaintexttemplate | Plain-text template filename, relative to templatepath | mailtemplate.txt |
| attachments | Optional comma-separated attachment paths, relative to templatepath | attachments/information.pdf |
| subject | full subject line of the example mail | Whatever you want is a subject line |
| from | mail address that is sent from | santa@cruz.com |
| to | mail address that an example mail is sent to **deprecated** | xmas@man.com |

### HTML and plain-text mail templates

Each CSV recipient receives **one email with both HTML and plain-text alternatives**.
The mail client chooses which version to display. Both bodies are rendered with Apache
Velocity; the configured `subject` is used unchanged and both alternatives use UTF-8.

Create two UTF-8 files outside the JAR, for example:

**`/tmp/mailversendala/welcome.html`**
```html
<html><body>
<p>Hello ${firstName} ${lastName},</p>
<p>Your invitation is ready.</p>
</body></html>
```

**`/tmp/mailversendala/welcome.txt`**
```text
Hello ${firstName} ${lastName},

Your invitation is ready.
```

Configure the directory and filenames in `javaconfiguration.properties`, or override
these settings using JVM properties **before** `-jar`:

```sh
java -Dtemplatepath=/tmp/mailversendala \
     -Dhtmltemplate=welcome.html \
     -Dplaintexttemplate=welcome.txt \
     -Dcsvpath=/tmp/recipients.csv \
     -jar target/mailversendala-1.0.0-SNAPSHOT-executable.jar
```

SMTP credentials and `from` must also be configured. Demo mode is enabled by default;
set `demomode=false` only when ready to deliver the messages.

Available recipient variables are `${firstName}`, `${lastName}`, and `${email}`.
`${firstname}` and `${surname}` are aliases matching the CSV column names. Missing recipient values become empty strings. References in HTML are
HTML-escaped automatically; plain-text values are not HTML-escaped. Use these
variables in HTML text or double-quoted attributes, not as JavaScript or unvalidated URLs.

Both templates are loaded and compiled once per run. Missing or syntactically invalid
templates abort the run before delivery; rendering or SMTP errors are counted per
recipient. Unknown variables are rendering errors rather than being sent as literal
placeholders. Templates can use Velocity directives such as `#if` and `#foreach`;
only use trusted template files. The template-based sending flow never downloads remote
images or attachments.

### Inline images and file attachments

Images referenced by HTML `<img src="...">` elements are automatically embedded in the
email using MIME Content-IDs. The image is sent with the message rather than downloaded
from a website. PDF documents, images, and other files can also be added as ordinary,
downloadable attachments. HTML and plain-text alternatives are preserved in both cases.

For example:

```text
/path/to/templates/
├── welcome.html
├── welcome.txt
├── images/
│   └── logo.png
└── attachments/
    └── information.pdf
```

In `welcome.html`:
```html
<p>Hello ${firstName} ${lastName},</p>
<img src="images/logo.png" alt="Our logo">
<p>Please see the attached information document.</p>
```

Configure the ordinary attachment separately:
```properties
templatepath=/path/to/templates
htmltemplate=welcome.html
plaintexttemplate=welcome.txt
attachments=attachments/information.pdf
```

The equivalent JVM override is `-Dattachments=attachments/information.pdf` before
`-jar`. To attach several files, separate their paths with commas, for example
`attachments=attachments/information.pdf,images/logo.png`. Leave the setting empty
for no ordinary attachments. Repeated paths to the same file are attached only once.
Merely linking a PDF with `<a href="...">` does not attach it. Attachment configuration
is shared by all recipients and is not interpreted as a Velocity template.

All image and attachment paths are relative to **`templatepath`**, even when the HTML
template itself is in a subdirectory. UTF-8 filenames and percent-encoded paths are
supported; use `/` as the separator. Filenames containing commas cannot be listed in
`attachments`. MIME types are inferred from filenames, so use appropriate extensions.
Inline resources must have an image MIME type.

Absolute paths, paths escaping the template directory (including symlinks), remote
URLs, `file:`/`data:`/pre-existing `cid:` image sources, and query strings/fragments
are rejected. `srcset` is not supported; use `img src`. CSS background images and other
HTML resource references are not automatically embedded. Ordinary web hyperlinks
remain unchanged and are not fetched by the application.

Before the first SMTP delivery, the application renders the recipients and validates
all referenced images and configured attachments. Missing or forbidden resources abort
the entire run, including when referenced only by a later recipient. Resource bytes
are snapshotted in memory once per unique file, so a file changed or deleted afterward
does not change already validated content. Allow enough memory for the files and the
rendered messages in a large mailing. Demo mode performs these checks without sending.

For a complete working example, copy the existing template directory, including its
`img/` and `attachments/` subdirectories:
```sh
mkdir -p /tmp/mailversendala
cp -R src/test/resources/template/. /tmp/mailversendala/
```

Use `htmltemplate=mailtemplate.html`, `plaintexttemplate=mailtemplate.txt`, and
`attachments=attachments/information.pdf`. The example HTML embeds a local logo;
the example PDF is a harmless information document. Image attribution is preserved
in `src/test/resources/template/img/readme.txt`.

### Development

In order to properly integrate the project into your IDE you need to setup lombok support -
please follow the guidelines about integration, e.g. [Eclipse integration](https://projectlombok.org/setup/eclipse).

## Changelog

See [CHANGELOG.md](https://github.com/ottlinger/mailversendala/blob/master/CHANGELOG.md)
for changes, including the GreenMail 2 migration related to
[PR #552](https://github.com/ottlinger/mailversendala/pull/552).

## Copyright

&copy; P. Ottlinger, 2017
