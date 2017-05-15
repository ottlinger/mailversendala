package de.aikiit.mailversendala;

import com.google.common.base.Charsets;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.util.Date;

/**
 * Created by hirsch on 15.05.17.
 */
public class SendOut {

    private Email email;

    public SendOut() {
        this.email = new SimpleEmail();
        email.setHostName(MailConfig.SMTP_HOST);
        email.setSmtpPort(MailConfig.SMTP_PORT);
        email.setAuthenticator(new DefaultAuthenticator(MailConfig.USERNAME, MailConfig.PASSWORD));
        email.setSSLOnConnect(true);
    }

    public static void main(String... args) throws Exception {
        new SendOut().send();
    }

    public void send() throws EmailException {
        email.setFrom(MailConfig.FROM);
        email.addTo(MailConfig.TO);

        email.setCharset(Charsets.UTF_8.name());

        email.setSubject("TestMail " + new Date());
        email.setMsg("This is a test mail from Mailversendala... :-)");
        email.send();
    }

}
