/*
 * Copyright (c) P. Ottlinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.aikiit.mailversendala;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarFile;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Starts the real shaded JAR after packaging, independently of the test classpath.
 */
class ExecutableJarIT {

    @TempDir
    Path directory;

    @Test
    void bundlesJakartaMailAndActivationProvidersWithoutLegacyJavaxMail() throws Exception {
        try (JarFile jar = new JarFile(System.getProperty("executableJar"))) {
            assertThat(jar.getEntry("jakarta/mail/Session.class")).isNotNull();
            assertThat(jar.getEntry("jakarta/activation/DataHandler.class")).isNotNull();
            assertThat(jar.getEntry("org/eclipse/angus/mail/smtp/SMTPTransport.class")).isNotNull();
            assertThat(jar.getEntry("META-INF/services/jakarta.mail.Provider")).isNotNull();
            assertThat(jar.getEntry("META-INF/services/jakarta.mail.util.StreamProvider")).isNotNull();
            assertThat(jar.getEntry("META-INF/services/jakarta.activation.spi.MailcapRegistryProvider")).isNotNull();
            assertThat(jar.getEntry("META-INF/services/jakarta.activation.spi.MimeTypeRegistryProvider")).isNotNull();
            assertThat(jar.getEntry("javax/mail/Session.class")).isNull();
            assertThat(jar.getEntry("javax/activation/DataHandler.class")).isNull();
            assertThat(jar.getEntry("com/sun/mail/smtp/SMTPTransport.class")).isNull();
            assertThat(jar.getEntry("com/icegreen/greenmail/util/GreenMail.class")).isNull();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {465, 587})
    void startsPackagedApplicationWithLoggingAndExternalTemplates(int port) throws Exception {
        Path jar = Path.of(System.getProperty("executableJar")).toAbsolutePath();
        assertThat(jar).isRegularFile();
        Path csv = directory.resolve("recipients.csv");
        Files.writeString(csv, "firstname,surname,email,salutation\nAda,Lovelace,recipient@example.test,Dear\n");
        Files.writeString(directory.resolve("welcome.html"),
                "<p>${salutation} ${firstName} ${lastName}</p><img src='images/logo.png'>");
        Files.createDirectory(directory.resolve("images"));
        Files.createDirectory(directory.resolve("attachments"));
        try (var image = getClass().getResourceAsStream("/template/img/monkey-toy-16-118729.png");
             var pdf = getClass().getResourceAsStream("/template/attachments/information.pdf")) {
            Files.copy(image, directory.resolve("images/logo.png"));
            Files.copy(pdf, directory.resolve("attachments/information.pdf"));
        }
        Files.writeString(directory.resolve("welcome.txt"), "${salutation} ${firstName} ${lastName}");
        Path config = directory.resolve("mailversendala.properties");
        Files.writeString(config, """
                demomode=true
                host=localhost
                port=465
                username=test-user
                password=test-password
                from=sender@example.test
                to=myself@example.test
                subject=Packaged application test
                templatepath=.
                htmltemplate=welcome.html
                plaintexttemplate=welcome.txt
                attachments=attachments/information.pdf
                csvpath=missing.csv
                """);
        Path outputFile = directory.resolve("application.log");

        Process process = new ProcessBuilder(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                // The merged stdout/stderr file must be UTF-8, including non-Log4j output.
                "-Dfile.encoding=UTF-8",
                "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
                // Java 17 uses these names for the standard streams.
                "-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8",
                "-Dconfig=" + config,
                // JVM properties must still override values from the external file.
                "-Dcsvpath=" + csv,
                "-Dport=" + port,
                "-jar", jar.toString())
                .directory(directory.toFile())
                .redirectErrorStream(true)
                .redirectOutput(outputFile.toFile())
                .start();
        try {
            assertThat(process.waitFor(30, TimeUnit.SECONDS))
                    .as("Packaged application must terminate within 30 seconds; output: %s", outputFile)
                    .isTrue();
            String output = Files.readString(outputFile, StandardCharsets.UTF_8);
            assertThat(process.exitValue()).withFailMessage("Application failed:\n%s", output).isZero();
            assertThat(output)
                    .contains("INFO", "Mailversendala", "Loaded external configuration file:",
                            "Running in DEMO mode",
                            "Prepared unrendered test mail to: myself@example.test",
                            "2 successfully send out", "0 errors", "Mailversendala is shutting down")
                    .containsOnlyOnce("SMTP transport: " + (port == 465 ? "SMTPS" : "STARTTLS"))
                    .containsOnlyOnce("Running in DEMO mode")
                    .containsOnlyOnce("Mail 1/2 im DEMO-Modus vorbereitet für: myself@example.test")
                    .containsOnlyOnce("Mail 2/2 im DEMO-Modus vorbereitet für: recipient@example.test")
                    .doesNotContain("ERROR", "StatusLogger", "Exception", "Reading Tamaya configuration",
                            "test-password", "Using TLS", "Using SSL", "email initialized",
                            "Initializing SMTP clients", "Successfully processed mail", "erfolgreich gesendet an:");
        } finally {
            process.destroyForcibly();
        }
    }
}
