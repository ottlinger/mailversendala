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

## Unreleased

### Changed

- Migrate GreenMail from 1.6.15 to 2.1.14, implementing the dependency update
  proposed in [PR #552](https://github.com/ottlinger/mailversendala/pull/552).
- Replace Commons Email 1.6.0 with Commons Email 2's Jakarta variant
  (`commons-email2-jakarta` 2.0.0-M1, a milestone release) and migrate application
  and test imports from `javax.mail` to `jakarta.mail` for GreenMail 2 compatibility.
- Use Eclipse Angus Mail 2.0.5 as the shared mail implementation for the application
  and tests, excluding legacy and duplicate bundled mail implementations.

### Tests

- Retain coverage of authenticated SMTPS delivery, MIME content, attachments,
  invalid credentials, demo mode, and rejection of plaintext fallback.
- Verify that the executable JAR includes Jakarta Mail/Activation APIs and provider
  service descriptors without legacy Javax APIs or the test-only GreenMail server.
