package de.aikiit.mailversendala.csv;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 25.05.17.
 */
public class MailingTest {

    @Test
    public void objectCreationWithBuilder() {
        String email = "foo@bar.uk";
        String name = "FooBarUK";
        Mailing mailing = new Mailing.MailingBuilder().email(email).name(name).build();

        assertThat(mailing).isNotNull();
        assertThat(mailing.toString()).isNotNull();

        assertThat(mailing.getEmail()).isEqualTo(email);
        assertThat(mailing.getName()).isEqualTo(name);
    }
}
