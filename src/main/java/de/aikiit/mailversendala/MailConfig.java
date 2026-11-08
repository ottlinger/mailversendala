package de.aikiit.mailversendala;

/*
 * Copyright (c) P. Ottlinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import de.aikiit.mailversendala.template.MailTemplate;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tamaya.Configuration;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;


/**
 * Encapsulates the configuration of this application.
 */
@Getter
public class MailConfig {

    private static final Logger LOG =
            LogManager.getLogger(MailConfig.class);

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String to;
    private final String from;
    private final String replyTo;
    private final String subject;
    private final String csvPath;
    private final String templatePath;
    private final String htmlTemplate;
    private final String plaintextTemplate;
    private final String attachments;
    private final boolean isDemoMode;

    /**
     * Read an optional UTF-8 properties file specified by {@code -Dconfig=path}.
     * JVM properties and environment variables override the file, which overrides
     * Tamaya's classpath configuration and the built-in defaults.
     */
    public MailConfig() {
        this(loadExternalConfiguration());
    }

    private MailConfig(Properties external) {
        LOG.debug("Reading Tamaya configuration ...");
        final ConfigValues configuration = new ConfigValues(external, Configuration.current());

        this.host = configuration.getOrDefault("host", "smtp.example.com");
        this.port = Integer.parseInt(configuration.getOrDefault("port", "465"));
        this.username = configuration.getOrDefault("username", "user@example.com");
        this.password = configuration.getOrDefault("password", "chooseMeWisely");
        this.to = configuration.getOrDefault("to", "");
        this.from = configuration.getOrDefault("from", "santa@cruz.com");
        this.replyTo = configuration.getOrDefault("replyto", "");
        this.subject = configuration.getOrDefault("subject", "Do adapt your configuration - will not work");
        this.csvPath = configuration.getOrDefault("csvpath", "mailversendala-example.csv");
        this.isDemoMode = Boolean.parseBoolean(configuration.getOrDefault("demomode", "true"));
        this.templatePath = configuration.getOrDefault("templatepath", "template");
        this.htmlTemplate = configuration.getOrDefault("htmltemplate", MailTemplate.BASE_NAME_HTML);
        this.plaintextTemplate = configuration.getOrDefault("plaintexttemplate", MailTemplate.BASE_NAME_PLAINTEXT);
        this.attachments = configuration.getOrDefault("attachments", "");
        LOG.debug("Configuration: DONE.");
    }

    private static Properties loadExternalConfiguration() {
        Properties properties = new Properties();
        String filename = System.getProperty("config");
        if (filename != null) {
            if (filename.isBlank()) {
                throw new IllegalArgumentException("Configuration file path (-Dconfig) must not be blank");
            }
            Path path = Path.of(filename).toAbsolutePath().normalize();
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                properties.load(reader);
            } catch (IOException | IllegalArgumentException e) {
                // Do not expose property values (in particular SMTP credentials).
                throw new IllegalArgumentException("Cannot read configuration file: " + path);
            }
            LOG.info("Loaded external configuration file: {}", path);
        }
        return properties;
    }

    private record ConfigValues(Properties external, Configuration tamaya) {
        private String getOrDefault(String key, String fallback) {
            String value = System.getProperty(key);
            if (value == null) {
                value = System.getenv(key);
            }
            if (value == null) {
                value = external.getProperty(key);
            }
            return value == null ? tamaya.getOrDefault(key, fallback) : value;
        }
    }

    /**
     * If in demo mode <strong>NO</strong> mails should be sent out!
     *
     * @return {@code true} if in demo mode, thus no mails will be sent by default.
     */
    public boolean sendOutMails() {
        return !isDemoMode;
    }

}
