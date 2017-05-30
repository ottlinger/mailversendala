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
 * Created by hirsch on 15.05.17.
 */
public class SendOut {
    private static final Logger LOG =
            LogManager.getLogger(SendOut.class);

    // is transformed into User-Agent-Header
    private static final String SENT_BY = "X-Mailer";
    private static final String SENT_BY_ME = "Mailversendala - https://github.com/ottlinger/mailversendala";

    private Email email;
    private HtmlEmail htmlEmail;
    private MailConfig mailConfig;
    private Mailing recipient;

    public SendOut(Mailing recipient) {

        this.recipient = recipient;

        this.mailConfig = new MailConfig();

        LOG.info("Initialized authentication.");
        DefaultAuthenticator authenticator = new DefaultAuthenticator(mailConfig.getUsername(), mailConfig.getPassword());

        int port = mailConfig.getPort();

        this.email = new SimpleEmail();
        email.setHostName(mailConfig.getHost());
        email.setSmtpPort(port);
        email.setAuthenticator(authenticator);
        email.setSSLCheckServerIdentity(true);
        if (465 == port) {
            email.setSSLOnConnect(true);
        } else {
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
            htmlEmail.setSSLOnConnect(true);
        } else {
            htmlEmail.setStartTLSRequired(true);
        }
        htmlEmail.setBounceAddress(mailConfig.getFrom());
        htmlEmail.setAuthenticator(authenticator);
        htmlEmail.addHeader(SENT_BY, SENT_BY_ME);
        LOG.info("HTML and text email initialized.");
    }

    public static void main(String... args) throws Exception {
        if (args != null && (args.length >= 1 || Strings.isNullOrEmpty(args[0]))) {
            LOG.error("Please call this method with a mail address to send to.");
            return;
        } else {
            String email = args[0];
            LOG.info("Init: Using mail address from runtime parameter: {}", email);

            Mailing mailing = Mailing.builder().email(email).firstname("Your name").surname("Is my name").language(Locale.GERMAN.getLanguage()).build();
            SendOut sendOut = new SendOut(mailing);
            LOG.info("Init: DONE");
            sendOut.send(false);
            LOG.info("Send simple text-based message: DONE");
            sendOut.sendComplex(false);
            LOG.info("Send complex HTML and text-based message: DONE");
        }
    }

    public void send(boolean isTest) throws EmailException {
        email.setFrom(mailConfig.getFrom());
        email.addTo(this.recipient.getEmail());

        email.setCharset(Charsets.UTF_8.name());

        email.setSubject(mailConfig.getSubject() + " " + new Date());
        email.setMsg("This is a test mail from Mailversendala... :-)");
        if (!isTest) {
            email.send();
        }
    }

    public void sendComplex(boolean isTest) throws EmailException, MalformedURLException {
        htmlEmail.addTo(this.recipient.getEmail(), this.recipient.getFirstname() + " " + this.recipient.getSurname());
        htmlEmail.setFrom(mailConfig.getFrom(), "Me");
        htmlEmail.setSubject("HTML" + mailConfig.getSubject() + " " + new Date());

        // embed the image and get the content id
        URL url = new URL("http://www.apache.org/images/asf_logo_wide.gif");
        String cid = htmlEmail.embed(url, "Apache logo");

        // set the html message
        htmlEmail.setHtmlMsg("<html>The apache logo - <img src=\"cid:" + cid + "\"></html>");

        // set the alternative message
        htmlEmail.setTextMsg("Your email client does not support HTML messages - thus no Apache logo");

        if (!isTest) {
            htmlEmail.send();
        }
    }

}
