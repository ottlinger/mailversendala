package de.aikiit.mailversendala.template;

public class VelocityMailTemplate implements MailTemplate {

    private static String TEMPLATE_SUFFIX = ".vm";

    public VelocityMailTemplate() {
        // TODO https://www.baeldung.com/apache-velocity
    }

    @Override
    public String getHtml() {
        return null;
    }

    @Override
    public String getPlaintext() {
        return null;
    }
}
