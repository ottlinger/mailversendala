package de.aikiit.mailversendala;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 15.05.17.
 */
public class MailConfigTest {

    private final MailConfig mailConfig = new MailConfig();

    @Test
    public void ensureConfigurationIsValid() {
        for (String token : Arrays.asList(mailConfig.getSubject(), mailConfig.getFrom(), mailConfig.getPassword(), mailConfig.getHost(), mailConfig.getTo(), mailConfig.getUsername())) {
            assertThat(token).isNotNull();
        }

        assertThat(mailConfig.getPort()).isGreaterThan(0);
    }
}
