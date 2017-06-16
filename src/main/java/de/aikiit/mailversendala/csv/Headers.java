package de.aikiit.mailversendala.csv;

import de.aikiit.mailversendala.MailConfig;

/**
 * Contains the headers that need to be in the CSV-file.
 *
 * @see MailConfig#getCsvPath()
 */
public interface Headers {
    String FIRSTNAME = "firstname";
    String SURNAME = "surname";
    String EMAIL = "email";
}
