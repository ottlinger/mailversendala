package de.aikiit.mailversendala;

import de.aikiit.mailversendala.csv.Mailing;
import org.apache.commons.mail.EmailException;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 15.05.17.
 */
public class SendOutTest {

    private static final Mailing DUMMY_RECIPIENT = Mailing.builder().email("my@address.com").build();

    @Test
    public void initializeMailSenderProperly() {
        assertThat(new SendOut(DUMMY_RECIPIENT)).isNotNull();
    }

    @Test
    public void sendOutWithoutSendingInTestMode() throws EmailException {
        SendOut sendOut = new SendOut(DUMMY_RECIPIENT);
        assertThat(sendOut).isNotNull();
        sendOut.send();
    }

    @Test
    public void sendOutHtmlMessageWithoutSendingInTestMode() throws EmailException, MalformedURLException {
        SendOut sendOut = new SendOut(DUMMY_RECIPIENT);
        assertThat(sendOut).isNotNull();
        sendOut.sendComplex();
    }

    @Test
    public void whenSendOutLacksRuntimeParameterNothingHappens() throws Exception {
        SendOut.main("");
        SendOut.main((String[]) null);
    }
}
