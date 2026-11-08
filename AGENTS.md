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

# Agent instructions

These instructions apply to the entire repository.

## Project documentation

- Read [README.md](README.md) before changing configuration, CSV processing,
  templates, delivery behavior or logging. It is the source for user-facing usage
  instructions and the Maven site's landing page.
- Consult [CHANGELOG.md](CHANGELOG.md) for existing changes and compatibility notes.
  The documented release is **v1.0.0**; the current development version in `pom.xml`
  is **2.0.0-SNAPSHOT**. Keep current build/run examples aligned with `pom.xml`, without
  rewriting historical release notes. Summarize user-visible changes in the
  appropriate release section, explaining their practical impact rather than copying
  commit messages. Do not invent release dates or change versions without a request.
- Keep the README, changelog, configuration template and relevant tests consistent
  when changing user-facing behavior. Preserve the example addresses, including
  `to=xmas@man.com`, as examples; never send mail to them during development.
- Edit documentation sources, not generated files under `docs/`. The Maven site
  includes both the README and changelog.

## Safe mail delivery

See [external configuration](README.md#external-configuration-without-credentials-on-the-command-line)
and [additional test mail](README.md#additional-test-mail-to-yourself-during-the-csv-run).

- Keep `demomode=true` for development and initial checks. It must prevent **all**
  SMTP delivery while still preparing messages and performing validation.
- Never trigger live delivery or set `demomode=false` without explicit authorization.
  Prefer automated tests with the local GreenMail server and fictitious credentials.
- `to` adds one unrendered test message during the normal CSV run; it does **not**
  replace CSV recipients or turn the run into a test-only mode. With demo mode off,
  both that message and successfully rendered CSV messages are sent.
- Keep the additional message disabled when `to` is empty or absent. Do not introduce
  a real or example address as an automatic fallback recipient.

## Credentials and private data

See [external configuration](README.md#external-configuration-without-credentials-on-the-command-line)
and [delivery protocols](README.md#delivery-protocol-for-each-run).

- Never commit real SMTP credentials, recipient lists or private configuration files,
  or package them inside a JAR. Test fixtures must use fictitious data.
- Prefer an external UTF-8 configuration file via `-Dconfig=path` before `-jar`.
  Do not put real passwords in shell commands, commit messages, logs or documentation.
  External configuration passwords are still stored in **plain text**.
- Restrict access to credential files. The README recommends `chmod 700` for the
  configuration directories and `chmod 600` for the properties file on macOS/Linux;
  use file/folder security settings on Windows.
- Preserve configuration precedence: JVM properties, environment variables, explicit
  configuration file, classpath configuration, then defaults. An unreadable or
  malformed explicit file must fail startup instead of silently using defaults.
- Each application start creates a UTF-8 protocol in the working directory. Preserve
  unique, stable run filenames, existing log contents and the `*.log` Git exclusion.
- Protocols contain recipient addresses and may contain personal data in errors.
  Do not commit them or publish their contents without appropriate redaction. Restrict
  access and retain them only as long as needed; there is no automatic deletion.
- A successful SMTP send means server acceptance, not confirmed inbox delivery.
  Demo-mode progress must say "prepared", not "sent".

## Templates and resource validation

See [templates](README.md#html-and-plain-text-mail-templates),
[markup checks](README.md#html-markup-checks-before-delivery) and
[images/attachments](README.md#inline-images-and-file-attachments).

- Only use trusted Velocity templates. Keep automatic HTML escaping for recipient
  values; use placeholders in HTML text or double-quoted attributes, not JavaScript
  or unvalidated URLs. Plain-text values remain unescaped.
- Render and validate all prepared CSV messages **before the first SMTP delivery**.
  Invalid template files, HTML markup or missing/forbidden resources must not cause
  partial delivery before the validation failure is discovered.
- Keep HTML diagnostics useful: template path, affected recipient and rendered
  line/column positions. Jsoup is a parser/typo check, not a full HTML conformance
  validator or a guarantee of safe content or consistent mail-client rendering.
- Do not treat raw Velocity directives as finished HTML. The additional unrendered
  test message intentionally preserves placeholders and directives, but must still
  obey resource checks and must not be sent if CSV markup validation aborts the run.
- Images and attachments must be relative local paths inside `templatepath`.
  Preserve rejection of remote URLs, absolute paths, traversal and escaping symlinks,
  `file:`/`data:`/pre-existing `cid:` image sources, query strings, fragments and
  unsupported `srcset` references.
- Do not add network downloads during template/resource processing. Ordinary web
  hyperlinks remain unchanged and are not fetched by the application.
- Preserve resource deduplication and in-memory snapshots so files changed after
  validation cannot alter a prepared message. Account for memory usage in large runs.

## Development and verification

- Use Java 17 or newer and the checked-in Maven Wrapper; a separate Maven installation
  is not required. The wrapper supplies the project's Maven version (at least 3.9).
- Run `./mvnw verify` after code changes (`.\mvnw.cmd verify` on Windows). It includes
  unit tests and packaged-JAR integration tests; `./mvnw test` alone does not exercise
  the executable JAR. Use the wrapper for all Maven commands in documentation and
  development, rather than a globally installed `mvn`.
- Add regression tests for changed configuration, delivery, validation or logging
  behavior. Keep tests portable across Windows, macOS and Linux, including explicit
  UTF-8 log encoding and Windows-compatible protocol filenames.
- Retain Apache license headers. For documentation-only changes, run `./mvnw validate`
  (`.\mvnw.cmd validate` on Windows) for license checks and `git diff --check` for
  whitespace errors.
- Do not commit or push changes unless requested.
