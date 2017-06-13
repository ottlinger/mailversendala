package de.aikiit.mailversendala;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;


/**
 * Encapsulates the configuration of this application.
 */
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
    private final String csvpath;
    private final String templatePath;
    private final boolean isDemoMode;

    /**
     * Read from Tamaya configuration or fallback to dummy default values.
     */
    public MailConfig() {
        LOG.debug("Reading Tamaya configuration ...");
        final Configuration configuration = ConfigurationProvider.getConfiguration();

        this.host = configuration.getOrDefault("host", "smtp.example.com");
        this.port = Integer.valueOf(configuration.getOrDefault("port", "465"));
        this.username = configuration.getOrDefault("username", "user@example.com");
        this.password = configuration.getOrDefault("password", "chooseMeWisely");
        this.to = configuration.getOrDefault("to", "xmas@man.com");
        this.from = configuration.getOrDefault("from", "santa@cruz.com");
        this.subject = configuration.getOrDefault("subject", "Do adapt your configuration - will not work");
        this.csvpath = configuration.getOrDefault("csvpath", "mailversendala-example.csv");
        this.isDemoMode = Boolean.valueOf(configuration.getOrDefault("demomode", "true"));
        this.templatePath = configuration.getOrDefault("templatepath", "template");
        LOG.debug("Configuration: DONE.");
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getCsvPath() {
        return csvpath;
    }

    /**
     * If in demo mode <strong>NO</strong> mails should be send out!
     *
     * @return {@code true} if in demo mode, thus no mails will be sent by default.
     */
    public boolean sendOutMails() {
        return !isDemoMode;
    }

    public String getTemplatePath() {
        return templatePath;
    }
}
