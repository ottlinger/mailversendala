package de.aikiit.mailversendala;

import org.apache.commons.mail.EmailException;
import org.junit.Test;

import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 15.05.17.
 */
public class SendOutTest {

    @Test
    public void initializeMailSenderProperly() {
        assertThat(new SendOut()).isNotNull();
    }

    @Test
    public void sendOutWithoutSendingInTestMode() throws EmailException {
        SendOut sendOut = new SendOut();
        assertThat(sendOut).isNotNull();
        sendOut.send(true);
    }

    @Test
    public void sendOutHtmlMessageWithoutSendingInTestMode() throws EmailException, MalformedURLException {
        SendOut sendOut = new SendOut();
        assertThat(sendOut).isNotNull();
        sendOut.sendComplex(true);
    }
}
