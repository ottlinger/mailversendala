package de.aikiit.mailversendala.csv;

import de.aikiit.mailversendala.MailConfig;

/**
 * Contains the headers that need to be in the CSV-file.
 *
 * @see MailConfig
 */
public interface Headers {
    /**
     * Firstname in the mailing.
     */
    String FIRSTNAME = "firstname";
    /**
     * Surname in the mailing.
     */
    String SURNAME = "surname";
    /**
     * Email used to send the mailing to.
     */
    String EMAIL = "email";
}
