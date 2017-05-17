package de.aikiit.mailversendala;

import com.google.common.base.Charsets;
import org.apache.commons.mail.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public SendOut() {
        LOG.info("Initialized authentication.");
        DefaultAuthenticator authenticator = new DefaultAuthenticator(MailConfig.USERNAME, MailConfig.PASSWORD);

        this.email = new SimpleEmail();
        email.setHostName(MailConfig.SMTP_HOST);
        email.setSmtpPort(MailConfig.SMTP_PORT);
        email.setAuthenticator(authenticator);
        email.setSSLOnConnect(true);
        email.setBounceAddress(MailConfig.FROM);
        email.addHeader(SENT_BY, SENT_BY_ME);
        LOG.info("Simple email initialized.");

        this.htmlEmail = new HtmlEmail();
        htmlEmail.setHostName(MailConfig.SMTP_HOST);
        htmlEmail.setSSLOnConnect(true);
        htmlEmail.setBounceAddress(MailConfig.FROM);
        htmlEmail.setAuthenticator(authenticator);
        htmlEmail.addHeader(SENT_BY, SENT_BY_ME);
        LOG.info("HTML and text email initialized.");
    }

    public static void main(String... args) throws Exception {

        SendOut sendOut = new SendOut();
        System.out.println("Init: DONE");
        sendOut.send(false);
        System.out.println("Send simple text-based message: DONE");
        sendOut.sendComplex(false);
        System.out.println("Send complex HTML and text-based message: DONE");
    }

    public void send(boolean isTest) throws EmailException {
        email.setFrom(MailConfig.FROM);
        email.addTo(MailConfig.TO);

        email.setCharset(Charsets.UTF_8.name());

        email.setSubject(MailConfig.SUBJECT + " " + new Date());
        email.setMsg("This is a test mail from Mailversendala... :-)");
        if (!isTest) {
            email.send();
        }
    }

    public void sendComplex(boolean isTest) throws EmailException, MalformedURLException {
        htmlEmail.addTo(MailConfig.TO, "John Doe Recipiento");
        htmlEmail.setFrom(MailConfig.FROM, "Me");
        htmlEmail.setSubject("HTML" + MailConfig.SUBJECT + " " + new Date());

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
