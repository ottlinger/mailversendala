package de.aikiit.mailversendala.template;

import lombok.Data;

/**
 * Contains the contents of a mail.
 */
public interface MailTemplate {
    String getHtml();
    String getPlaintext();
}
