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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingConfigurationTest {

    @Test
    void loadsMainPropertiesWithInfoLevelConsoleAndUtf8RunFile(@TempDir Path directory) throws Exception {
        // Use an isolated context and file: do not change suite logging or write into the repository.
        Properties properties = new Properties();
        try (var input = Files.newInputStream(Path.of("src/main/resources/log4j2.properties"))) {
            properties.load(input);
        }
        Path runFile = directory.resolve("run.log");
        Files.writeString(runFile, "Previous protocol\n", StandardCharsets.UTF_8);
        properties.setProperty("appender.file.fileName", runFile.toString());
        Path testConfig = directory.resolve("log4j2.properties");
        try (var output = Files.newOutputStream(testConfig)) {
            properties.store(output, "Isolated main configuration test");
        }
        try (LoggerContext context = new LoggerContext("main-logging-test")) {
            context.setConfigLocation(testConfig.toUri());

            assertLoggingConfiguration(context.getConfiguration(), "Mailversendala", Level.INFO, "STDOUT", "RUN_FILE");
            FileAppender file = (FileAppender) context.getConfiguration().getAppenders().get("RUN_FILE");
            assertThat(file.getFileName()).isEqualTo(runFile.toString());
            assertThat(((PatternLayout) file.getLayout()).getCharset()).isEqualTo(StandardCharsets.UTF_8);
            context.getLogger("Mailversendala").info("Mail 1/1 im DEMO-Modus vorbereitet für: Jörg");
            PatternLayout layout = (PatternLayout) context.getConfiguration().getAppenders().get("STDOUT").getLayout();
            assertThat(layout.getCharset()).isEqualTo(StandardCharsets.UTF_8);
            assertThat(new String(layout.toByteArray(Log4jLogEvent.newBuilder()
                    .setLoggerName("Mailversendala").setLevel(Level.INFO)
                    .setMessage(new SimpleMessage("vorbereitet für: Jörg"))
                    .build()), StandardCharsets.UTF_8)).contains("vorbereitet für: Jörg");
        }
        assertThat(Files.readString(runFile, StandardCharsets.UTF_8))
                .startsWith("Previous protocol\n")
                .contains("Mail 1/1 im DEMO-Modus vorbereitet für: Jörg");
    }

    @Test
    void automaticallyLoadsTestPropertiesWithWarnLevel() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);

        assertLoggingConfiguration(context.getConfiguration(), "MailversendalaTest", Level.WARN, "STDOUT");
    }

    private void assertLoggingConfiguration(Configuration configuration, String name, Level level, String... appenders) {
        assertThat(configuration.getName()).isEqualTo(name);
        assertThat(configuration.getRootLogger().getLevel()).isEqualTo(level);
        assertThat(configuration.getAppenders()).containsOnlyKeys(appenders);
        assertThat(configuration.getRootLogger().getAppenders()).containsOnlyKeys(appenders);
        assertThat(configuration.getAppenders().get("STDOUT")).isInstanceOf(ConsoleAppender.class);
        assertThat(configuration.getFilter()).isNull();
    }
}
