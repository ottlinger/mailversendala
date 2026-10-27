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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Starts the real shaded JAR after packaging, independently of the test classpath.
 */
class ExecutableJarIT {

    @TempDir
    Path directory;

    @Test
    void startsPackagedApplicationWithLoggingAndExternalTemplates() throws Exception {
        Path jar = Path.of(System.getProperty("executableJar")).toAbsolutePath();
        assertThat(jar).isRegularFile();
        Path csv = directory.resolve("recipients.csv");
        Files.writeString(csv, "firstname,surname,email\nAda,Lovelace,recipient@example.test\n");
        Files.writeString(directory.resolve("welcome.html"),
                "<p>Hello ${firstName} ${lastName}</p><img src='images/logo.png'>");
        Files.createDirectory(directory.resolve("images"));
        Files.createDirectory(directory.resolve("attachments"));
        try (var image = getClass().getResourceAsStream("/template/img/monkey-toy-16-118729.png");
             var pdf = getClass().getResourceAsStream("/template/attachments/information.pdf")) {
            Files.copy(image, directory.resolve("images/logo.png"));
            Files.copy(pdf, directory.resolve("attachments/information.pdf"));
        }
        Files.writeString(directory.resolve("welcome.txt"), "Hello ${firstName} ${lastName}");
        Path outputFile = directory.resolve("application.log");

        Process process = new ProcessBuilder(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-Ddemomode=true",
                "-Dhost=localhost", "-Dport=465",
                "-Dusername=test-user", "-Dpassword=test-password",
                "-Dfrom=sender@example.test", "-Dsubject=Packaged application test",
                "-Dtemplatepath=" + directory,
                "-Dhtmltemplate=welcome.html", "-Dplaintexttemplate=welcome.txt",
                "-Dattachments=attachments/information.pdf",
                "-Dcsvpath=" + csv,
                "-jar", jar.toString())
                .directory(directory.toFile())
                .redirectErrorStream(true)
                .redirectOutput(outputFile.toFile())
                .start();
        try {
            assertThat(process.waitFor(30, TimeUnit.SECONDS))
                    .as("Packaged application must terminate within 30 seconds; output: %s", outputFile)
                    .isTrue();
            String output = Files.readString(outputFile);
            assertThat(process.exitValue()).withFailMessage("Application failed:\n%s", output).isZero();
            assertThat(output)
                    .contains("INFO", "Mailversendala", "Running in DEMO mode",
                            "1 successfully send out", "0 errors", "Mailversendala is shutting down")
                    .doesNotContain("ERROR", "StatusLogger", "Exception", "Reading Tamaya configuration");
        } finally {
            process.destroyForcibly();
        }
    }
}
