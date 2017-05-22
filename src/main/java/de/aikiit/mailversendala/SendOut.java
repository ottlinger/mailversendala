package de.aikiit.mailversendala;

import com.google.common.base.Charsets;
import org.apache.commons.mail.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tamaya.ConfigurationProvider;

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

    public SendOut() {

        this.mailConfig = new MailConfig();

        LOG.info("Initialized authentication.");
        DefaultAuthenticator authenticator = new DefaultAuthenticator(mailConfig.getUsername(), mailConfig.getPassword());

        this.email = new SimpleEmail();
        email.setHostName(mailConfig.getHost());
        email.setSmtpPort(mailConfig.getPort());
        email.setAuthenticator(authenticator);
        email.setSSLOnConnect(true);
        email.setBounceAddress(mailConfig.getFrom());
        email.addHeader(SENT_BY, SENT_BY_ME);
        LOG.info("Simple email initialized.");

        this.htmlEmail = new HtmlEmail();
        htmlEmail.setHostName(mailConfig.getHost());
        htmlEmail.setSSLOnConnect(true);
        htmlEmail.setBounceAddress(mailConfig.getFrom());
        htmlEmail.setAuthenticator(authenticator);
        htmlEmail.addHeader(SENT_BY, SENT_BY_ME);
        LOG.info("HTML and text email initialized.");
    }

    public static void main(String... args) throws Exception {

        SendOut sendOut = new SendOut();
        LOG.debug("Init: DONE");
        sendOut.send(false);
        LOG.info("Send simple text-based message: DONE");
        sendOut.sendComplex(false);
        LOG.info("Send complex HTML and text-based message: DONE");
    }

    public void send(boolean isTest) throws EmailException {
        email.setFrom(mailConfig.getFrom());
        email.addTo(mailConfig.getTo());

        email.setCharset(Charsets.UTF_8.name());

        email.setSubject(mailConfig.getSubject() + " " + new Date());
        email.setMsg("This is a test mail from Mailversendala... :-)");
        if (!isTest) {
            email.send();
        }
    }

    public void sendComplex(boolean isTest) throws EmailException, MalformedURLException {
        htmlEmail.addTo(mailConfig.getTo(), "John Doe Recipiento");
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
