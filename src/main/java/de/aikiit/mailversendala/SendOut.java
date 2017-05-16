package de.aikiit.mailversendala;

import com.google.common.base.Charsets;
import org.apache.commons.mail.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by hirsch on 15.05.17.
 */
public class SendOut {

    private Email email;
    private HtmlEmail htmlEmail;

    public SendOut() {
        DefaultAuthenticator authenticator = new DefaultAuthenticator(MailConfig.USERNAME, MailConfig.PASSWORD);

        this.email = new SimpleEmail();
        email.setHostName(MailConfig.SMTP_HOST);
        email.setSmtpPort(MailConfig.SMTP_PORT);
        email.setAuthenticator(authenticator);
        email.setSSLOnConnect(true);
        email.setBounceAddress(MailConfig.FROM);

        this.htmlEmail = new HtmlEmail();
        htmlEmail.setHostName(MailConfig.SMTP_HOST);
        htmlEmail.setSSLOnConnect(true);
        htmlEmail.setBounceAddress(MailConfig.FROM);
        htmlEmail.setAuthenticator(authenticator);
    }

    public static void main(String... args) throws Exception {
        SendOut sendOut = new SendOut();
        sendOut.send(false);
        sendOut.sendComplex(false);
    }

    public void send(boolean isTest) throws EmailException {
        email.setFrom(MailConfig.FROM);
        email.addTo(MailConfig.TO);

        email.setCharset(Charsets.UTF_8.name());

        email.setSubject(MailConfig.SUBJECT + new Date());
        email.setMsg("This is a test mail from Mailversendala... :-)");
        if (!isTest) {
            email.send();
        }
    }

    public void sendComplex(boolean isTest) throws EmailException, MalformedURLException {
        htmlEmail.addTo(MailConfig.TO, "John Doe Recipiento");
        htmlEmail.setFrom(MailConfig.FROM, "Me");

        // embed the image and get the content id
        URL url = new URL("http://www.apache.org/images/asf_logo_wide.gif");
        String cid = htmlEmail.embed(url, "Apache logo");

        // set the html message
        htmlEmail.setHtmlMsg("<html>The apache logo - <img src=\"cid:"+cid+"\"></html>");

        // set the alternative message
        htmlEmail.setTextMsg("Your email client does not support HTML messages - thus no Apache logo");

        if (!isTest) {
            email.send();
        }
    }

}
