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
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingConfigurationTest {

    @Test
    void loadsMainPropertiesWithInfoLevelAndConsoleOutput() {
        // Use an isolated context: do not replace the test suite's logging configuration.
        try (LoggerContext context = new LoggerContext("main-logging-test")) {
            context.setConfigLocation(Path.of("src/main/resources/log4j2.properties").toAbsolutePath().toUri());

            assertConsoleConfiguration(context.getConfiguration(), "Mailversendala", Level.INFO);
        }
    }

    @Test
    void automaticallyLoadsTestPropertiesWithWarnLevel() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);

        assertConsoleConfiguration(context.getConfiguration(), "MailversendalaTest", Level.WARN);
    }

    private void assertConsoleConfiguration(Configuration configuration, String name, Level level) {
        assertThat(configuration.getName()).isEqualTo(name);
        assertThat(configuration.getRootLogger().getLevel()).isEqualTo(level);
        assertThat(configuration.getAppenders()).containsOnlyKeys("STDOUT");
        assertThat(configuration.getRootLogger().getAppenders()).containsOnlyKeys("STDOUT");
        assertThat(configuration.getAppenders().get("STDOUT")).isInstanceOf(ConsoleAppender.class);
        assertThat(configuration.getFilter()).isNull();
    }
}
