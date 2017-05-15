package de.aikiit.mailversendala;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 15.05.17.
 */
public class SendOutTest {

    @Test
    public void initializeMailSenderProperly() {
        assertThat(new SendOut()).isNotNull();
    }
}
