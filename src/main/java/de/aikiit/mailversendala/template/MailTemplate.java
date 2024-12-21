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
     * Returns HTML contents.
     * @return mailing content in format HTML.
     */
    String getHtml();

    /**
     * Returns plaintext contents.
     * @return mailing content in plaintext format.
     */
    String getPlaintext();
}
