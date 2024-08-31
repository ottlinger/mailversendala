package de.aikiit.mailversendala.template;

/**
 * Contains the contents of a mail.
 */
public interface MailTemplate {
    /**
     * Default template file name for HTML.
     */
    String BASE_NAME_HTML = "mailtemplate.html";
    /**
     * Default template file name for plain text
     */
    String BASE_NAME_PLAINTEXT = "mailtemplate.txt";

    /**
     * @return mailing content in format HTML.
     */
    String getHtml();

    /**
     * @return mailing content in plain text format.
     */
    String getPlaintext();
}
