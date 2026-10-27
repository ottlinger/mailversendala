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
    private final String subject;
    private final String csvPath;
    private final String templatePath;
    private final String htmlTemplate;
    private final String plaintextTemplate;
    private final String attachments;
    private final boolean isDemoMode;

    /**
     * Read from Tamaya configuration or fallback to dummy default values.
     */
    public MailConfig() {
        LOG.debug("Reading Tamaya configuration ...");
        final Configuration configuration = Configuration.current();

        this.host = configuration.getOrDefault("host", "smtp.example.com");
        this.port = Integer.parseInt(configuration.getOrDefault("port", "465"));
        this.username = configuration.getOrDefault("username", "user@example.com");
        this.password = configuration.getOrDefault("password", "chooseMeWisely");
        this.to = configuration.getOrDefault("to", "xmas@man.com");
        this.from = configuration.getOrDefault("from", "santa@cruz.com");
        this.subject = configuration.getOrDefault("subject", "Do adapt your configuration - will not work");
        this.csvPath = configuration.getOrDefault("csvpath", "mailversendala-example.csv");
        this.isDemoMode = Boolean.parseBoolean(configuration.getOrDefault("demomode", "true"));
        this.templatePath = configuration.getOrDefault("templatepath", "template");
        this.htmlTemplate = configuration.getOrDefault("htmltemplate", MailTemplate.BASE_NAME_HTML);
        this.plaintextTemplate = configuration.getOrDefault("plaintexttemplate", MailTemplate.BASE_NAME_PLAINTEXT);
        this.attachments = configuration.getOrDefault("attachments", "");
        LOG.debug("Configuration: DONE.");
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
