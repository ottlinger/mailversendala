package de.aikiit.mailversendala;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

/**
 * Created by hirsch on 15.05.17.
 */
public class MailConfig {

    private static final Logger LOG =
            LogManager.getLogger(MailConfig.class);

    private String host;
    private int port;
    private String username;
    private String password;
    private String to;
    private String from;
    private String subject;
    private String csvpath;

    /**
     * Read from Tamaya configuration or fallback to dummy default values.
     */
    public MailConfig() {
        LOG.info("Reading configuration with the help of Tamaya ... from Tamaya config.");
        final Configuration configuration = ConfigurationProvider.getConfiguration();

        this.host = configuration.getOrDefault("host", "smtp.example.com");
        this.port = Integer.valueOf(configuration.getOrDefault("port", "465"));
        this.username = configuration.getOrDefault("username", "user@example.com");
        this.password = configuration.getOrDefault("password", "chooseMeWisely");
        this.to = configuration.getOrDefault("to", "xmas@man.com");
        this.from = configuration.getOrDefault("from", "santa@cruz.com");
        this.subject = configuration.getOrDefault("subject", "Do adapt your configuration - will not work");
        this.csvpath = configuration.getOrDefault("csvpath", "mailversendala-example.csv");
        LOG.info("Configuration: DONE.");
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
}
