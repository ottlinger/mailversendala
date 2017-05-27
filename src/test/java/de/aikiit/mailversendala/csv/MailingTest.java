package de.aikiit.mailversendala.csv;

import javafx.scene.input.KeyCode;
import org.junit.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 25.05.17.
 */
public class MailingTest {
    private static final String EMAIL = "foo@bar.uk";
    private static final String NAME = "FooBarUK";
    private static final String FIRSTNAME = "Deforest";
    private static final String LANGUAGE = Locale.ENGLISH.getLanguage();

    @Test
    public void objectCreationWithBuilder() {
        Mailing mailing = new Mailing.MailingBuilder().email(EMAIL).surname(NAME).language(LANGUAGE).firstname(FIRSTNAME).build();

        assertThat(mailing).isNotNull();
        assertThat(mailing.toString()).isNotNull();

        assertThat(mailing.getEmail()).isEqualTo(EMAIL);
        assertThat(mailing.getSurname()).isEqualTo(NAME);
        assertThat(mailing.getFirstname()).isEqualTo(FIRSTNAME);
        assertThat(mailing.getLanguage()).isEqualTo("en");
    }

    @Test
    public void objectCreationWithoutBuilder() {
        Mailing mailing = new Mailing(FIRSTNAME, NAME, EMAIL, LANGUAGE);

        assertThat(mailing).isNotNull();
        assertThat(mailing.toString()).isNotNull();

        assertThat(mailing.getEmail()).isEqualTo(EMAIL);
        assertThat(mailing.getSurname()).isEqualTo(NAME);
        assertThat(mailing.getFirstname()).isEqualTo(FIRSTNAME);
        assertThat(mailing.getLanguage()).isEqualTo(LANGUAGE);
    }
}
