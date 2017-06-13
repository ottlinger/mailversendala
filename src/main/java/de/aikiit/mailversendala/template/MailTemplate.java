package de.aikiit.mailversendala.template;

import lombok.Data;

/**
 * Contains the contents of a mail.
 */
public interface MailTemplate {
    String BASE_NAME_HTML = "mailtemplate.html";
    String BASE_NAME_PLAINTEXT = "mailtemplate.txt";

    String DEFAULT_LANGUAGES = "de,en";
    String BACKUP_LANGUAGE = "de";

    String getHtml(String language);

    String getPlaintext(String language);
}
