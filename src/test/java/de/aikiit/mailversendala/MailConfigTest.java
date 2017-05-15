package de.aikiit.mailversendala;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 15.05.17.
 */
public class MailConfigTest {

    @Test
    public void ensureConfigurationIsValid() {
        for(String token : Arrays.asList(MailConfig.FROM, MailConfig.PASSWORD, MailConfig.SMTP_HOST, MailConfig.TO, MailConfig.USERNAME)) {
            assertThat(token).isNotNull();
        }

        assertThat(MailConfig.SMTP_PORT).isEqualTo(465);
    }
}
