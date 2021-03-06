package de.aikiit.mailversendala.template;

/**
 * Contains the contents of a mail.
 */
public interface MailTemplate {
    String BASE_NAME_HTML = "mailtemplate.html";
    String BASE_NAME_PLAINTEXT = "mailtemplate.txt";

    String getHtml();

    String getPlaintext();
}
