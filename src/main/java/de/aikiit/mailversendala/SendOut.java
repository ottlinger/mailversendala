package de.aikiit.mailversendala;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import de.aikiit.mailversendala.csv.Mailing;
import org.apache.commons.mail.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Locale;

/**
 * This class performs the actual mail sending and encapsulates all the technicalities to create and send out an email.
 */
public class SendOut {
    private static final Logger LOG =
            LogManager.getLogger(SendOut.class);

    // is transformed into User-Agent-Header
    private static final String SENT_BY = "X-Mailer";
    private static final String SENT_BY_ME = "Mailversendala - https://github.com/ottlinger/mailversendala";

    private final Email email;
    private final HtmlEmail htmlEmail;
    private final MailConfig mailConfig;
    private final Mailing recipient;

    /**
     * Create mail based on the given mailing recipient.
     * @param recipient mailing contents and parameters.
     */
    public SendOut(Mailing recipient) {
        this.recipient = recipient;
        this.mailConfig = new MailConfig();

        if (mailConfig.sendOutMails()) {
            LOG.warn("Running in DEMO mode, thus no mails will be sent out.");
        }

        LOG.info("Initialized authentication.");
        DefaultAuthenticator authenticator = new DefaultAuthenticator(mailConfig.getUsername(), mailConfig.getPassword());

        int port = mailConfig.getPort();

        this.email = new SimpleEmail();
        email.setHostName(mailConfig.getHost());
        email.setSmtpPort(port);
        email.setAuthenticator(authenticator);
        email.setSSLCheckServerIdentity(true);
        if (465 == port) {
            LOG.info("Using SSL.");
            email.setSSLOnConnect(true);
        } else {
            LOG.info("Using TLS.");
            email.setStartTLSRequired(true);
        }
        email.setBounceAddress(mailConfig.getFrom());
        email.addHeader(SENT_BY, SENT_BY_ME);
        LOG.info("Simple email initialized.");

        this.htmlEmail = new HtmlEmail();
        htmlEmail.setHostName(mailConfig.getHost());
        htmlEmail.setSmtpPort(port);
        htmlEmail.setSSLCheckServerIdentity(true);
        if (465 == port) {
            LOG.info("Using SSL.");
            htmlEmail.setSSLOnConnect(true);
        } else {
            LOG.info("Using TLS.");
            htmlEmail.setStartTLSRequired(true);
        }
        htmlEmail.setBounceAddress(mailConfig.getFrom());
        htmlEmail.setAuthenticator(authenticator);
        htmlEmail.addHeader(SENT_BY, SENT_BY_ME);
        LOG.info("HTML and text email initialized.");
    }

    /**
     * Test method to verify the actual mail creation and send out.
     * @param args allows sending an email to a given mail address. One parameter is required.
     * @throws Exception in case of errors.
     */
    public static void main(String... args) throws Exception {
        if (args == null || args.length < 1 || Strings.isNullOrEmpty(args[0])) {
            LOG.error("Please call this method with a mail address to send to.");
        } else {
            String email = args[0];
            LOG.warn("Init: Using mail address from runtime parameter: {}", email);

            Mailing mailing = Mailing.builder().email(email).firstname("Your name").surname("Is my name").language(Locale.GERMAN.getLanguage()).build();
            SendOut sendOut = new SendOut(mailing);
            LOG.info("Init: DONE");
            sendOut.send();
            LOG.info("Send simple text-based message: DONE");
            sendOut.sendComplex();
            LOG.info("Send complex HTML and text-based message: DONE");
        }
    }

    /**
     * Send out an email that contains only text contents.
     * @throws EmailException in case of errors.
     */
    public void send() throws EmailException {
        email.setFrom(mailConfig.getFrom());
        email.addTo(this.recipient.getEmail());

        email.setCharset(Charsets.UTF_8.name());

        email.setSubject(mailConfig.getSubject() + " " + new Date());
        email.setMsg("This is a test mail from Mailversendala... :-)");
        if (mailConfig.sendOutMails()) {
            email.send();
        }
    }

    /**
     * Send out an email with plain text and HTML contents.
     *
     * @throws EmailException in case of mail-related errors.
     * @throws MalformedURLException in case of problem while generating the HTML-part of.
     */
    public void sendComplex() throws EmailException, MalformedURLException {
        htmlEmail.addTo(this.recipient.getEmail(), this.recipient.getFirstname() + " " + this.recipient.getSurname());
        htmlEmail.setFrom(mailConfig.getFrom(), "Me");
        htmlEmail.setSubject("HTML" + mailConfig.getSubject() + " " + new Date());

        // embed the image and get the content id
        URL url = new URL("https://www.apache.org/images/asf_logo_wide.gif");
        String cid = htmlEmail.embed(url, "ASF logo");

        // set the html message
        htmlEmail.setHtmlMsg("<html>The ASF logo - <img src=\"cid:" + cid + "\"></html>");

        // set the alternative message
        htmlEmail.setTextMsg("Your email client does not support HTML messages - thus no ASF logo visible.");

        if (mailConfig.sendOutMails()) {
            htmlEmail.send();
        }
    }
}
