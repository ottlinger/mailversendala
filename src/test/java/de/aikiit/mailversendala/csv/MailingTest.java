package de.aikiit.mailversendala.csv;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 25.05.17.
 */
public class MailingTest {
    private static final String EMAIL = "foo@bar.uk";
    private static final String NAME = "FooBarUK";

    @Test
    public void objectCreationWithBuilder() {
        Mailing mailing = new Mailing.MailingBuilder().email(EMAIL).name(NAME).build();

        assertThat(mailing).isNotNull();
        assertThat(mailing.toString()).isNotNull();

        assertThat(mailing.getEmail()).isEqualTo(EMAIL);
        assertThat(mailing.getName()).isEqualTo(NAME);
    }

    @Test
    public void objectCreationWithoutBuilder() {
        Mailing mailing = new Mailing(EMAIL, NAME);

        assertThat(mailing).isNotNull();
        assertThat(mailing.toString()).isNotNull();

        assertThat(mailing.getEmail()).isEqualTo(EMAIL);
        assertThat(mailing.getName()).isEqualTo(NAME);
    }
}
