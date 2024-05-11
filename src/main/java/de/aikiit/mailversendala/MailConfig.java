package de.aikiit.mailversendala;

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
        LOG.debug("Configuration: DONE.");
    }

    /**
     * If in demo mode <strong>NO</strong> mails should be send out!
     *
     * @return {@code true} if in demo mode, thus no mails will be sent by default.
     */
    public boolean sendOutMails() {
        return !isDemoMode;
    }

}
