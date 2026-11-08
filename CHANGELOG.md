<!--
Copyright (c) P. Ottlinger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

# Changelog

## v1.0.0

### Added

- **Personalized HTML and plain-text emails:** Each CSV recipient receives one
  message with both alternatives, rendered from external UTF-8 Velocity templates.
  Configure the directory and filenames with `templatepath`, `htmltemplate` and
  `plaintexttemplate`; the configured subject is preserved. HTML values are escaped
  automatically. Templates support directives such as `#if` and `#foreach`.
- **Recipient-specific greetings:** Use the new `salutation` CSV column and
  `${salutation}` placeholder to produce greetings such as `Liebe Erika` or
  `Lieber Peter` with `${salutation} ${firstName}`. Example CSV files and templates
  now demonstrate this usage.
- **Embedded images and attachments:** Local HTML images are embedded in the
  message instead of downloaded by the recipient. Add PDFs or other files with
  the optional comma-separated `attachments` setting. Paths are relative to
  `templatepath`; repeated references to the same image or attachment are deduplicated.
- **Explicit external configuration:** Pass `-Dconfig=path/to/file.properties`
  before `-jar` to load a UTF-8 configuration file without putting SMTP credentials on
  the command line or into the JAR. Configuration precedence is JVM properties,
  environment variables, the explicit file, then classpath configuration and defaults.
- **Configurable Reply-To:** Set `replyto` to send replies to a different address.
  Leave it empty or omit it to retain the normal reply-to-sender behavior.
- **An additional test mail to yourself:** Set `to` to your own address to receive
  one extra message during the normal CSV run, containing the original templates
  with unresolved placeholders. CSV recipients are still processed separately.
  Omit `to` or leave it empty to disable this feature; there is no built-in test
  recipient. The example configuration retains `to=xmas@man.com` for adaptation.
- **HTML checks before sending:** Jsoup checks every successfully rendered CSV
  message for markup errors and ordinary misspelled tags such as `<spna>`. Errors
  identify the template, recipient and line/column in the rendered HTML, and abort
  the run before any message is sent. The raw test mail is not checked as finished
  HTML, but is also withheld if a CSV message fails markup validation. This is a
  parser/typo check, not a complete HTML conformance or mail-client rendering validator.
- **A delivery protocol for every run:** A uniquely named UTF-8 `.log` file in the
  current working directory records recipient progress, the final report and errors,
  including startup failures. Console output remains available. Files are kept until
  manually archived or deleted and are excluded from Git. Protect these protocols:
  they contain recipient addresses and may contain personal data in error messages.

### Changed

- **Existing CSV files must include `salutation`.** Rows with a missing, empty or
  whitespace-only `email` value are logged and skipped; valid rows continue to be
  processed. The unused `language` recipient field and template variable are removed.
- Templates and local resources are prepared before the first SMTP delivery. Missing
  or invalid template files, unsafe resource paths and missing images abort the run.
  Remote image URLs, absolute paths, paths escaping `templatepath` and `srcset` are
  rejected. Resource contents are snapshotted so filesystem changes cannot alter
  an already validated message.
- **Demo mode still sends nothing**, including the additional mail to `to`, while
  preparing and checking messages. With `demomode=false`, both the additional test
  mail and successfully rendered CSV messages are sent; `to` is not a test-only mode.
- Logging now reports progress and the recipient once per processed message, for
  example `Mail 1/27 erfolgreich gesendet an: foo@bar.de`. Demo-mode messages say
  they were prepared, not sent. The additional test mail is included in the totals.
  Transport and demo-mode notices appear once per CSV run; initialization and raw
  test-mail preparation details are only visible at DEBUG level.
- Building from source now requires **Java 17 or newer**. Use the included Maven
  Wrapper (`./mvnw`, or `.\mvnw.cmd` on Windows) to obtain the configured Maven
  version, satisfying the minimum Maven 3.9 requirement without a separate installation.

### Fixed

- Explicit configuration files reliably override bundled/classpath values. An
  unreadable or malformed file now stops startup instead of silently using defaults.
- Restore reliable logging initialization and mail-provider discovery in the
  executable JAR by using stable Log4j 2 and merging provider service descriptors.
- Use explicit UTF-8 logging and subprocess output so umlauts do not break the
  packaged-JAR tests on Windows. Per-run filenames are Windows-compatible and stay
  stable when logging is reconfigured during startup.
- Correct the success report wording to `successfully sent out`.

### For developers

- Migrate mail delivery and tests to Commons Email 2's Jakarta variant
  (`commons-email2-jakarta` 2.0.0-M1, a milestone release), GreenMail 2 and the shared
  Eclipse Angus Mail implementation. Remove legacy/duplicate Javax mail providers.
- Convert `Mailing` and `FileMailTemplate` to immutable Java records. `Mailing`
  retains its Lombok builder and constructor argument order; record accessors replace
  JavaBean getters, and setters are no longer available.
- Replace deprecated Guava charset constants in `SendOut` with `StandardCharsets`.
  Update dependencies and build/test tooling.
- Extend automated coverage for personalized Unicode messages, SMTP authentication
  and TLS, MIME alternatives, images and attachments, invalid CSV rows, preflight
  failures, Reply-To, raw test mails, HTML typo diagnostics and per-run protocols.
  Packaged-JAR regression tests run through `./mvnw verify` (`.\mvnw.cmd verify`
  on Windows), including Windows encoding
  checks and consecutive runs with separate protocol files.
