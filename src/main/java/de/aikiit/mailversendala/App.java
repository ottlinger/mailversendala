package de.aikiit.mailversendala;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Main application that sends out mailings found in the configurable CSV file.
 * Remember to disable demo mode in order to send out mails.
 */
public class App {
    private static final Logger LOG =
            LogManager.getLogger(App.class);

    /**
     * Main method to launch MailVersendala with the given arguments.
     * @param args parameters for mail sending.
     * @throws IOException in case of errors.
     */
    public static void main(String[] args) throws IOException {
        Mailversendala.sendOut(new MailConfig());
        LOG.info("Mailversendala is shutting down. Bye Bye :-)");
    }
}
