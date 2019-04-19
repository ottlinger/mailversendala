package de.aikiit.mailversendala.template;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.StringWriter;

public class VelocityMailTemplate implements MailTemplate {

    private VelocityEngine velocityEngine = new VelocityEngine();

    public VelocityMailTemplate() {
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
        velocityEngine.setProperty("class.resource.loader.class",
                ClasspathResourceLoader.class.getName());
        velocityEngine.init();
    }

    @Override
    public String getHtml() {
        Template t = velocityEngine.getTemplate("template" + File.separator + MailTemplate.BASE_NAME_HTML);

        StringWriter writer = new StringWriter();
        t.merge(handleAndGetContextChanges(), writer);
        t.process();

        return writer.toString().trim();
    }

    @Override
    public String getPlaintext() {
        Template t = velocityEngine.getTemplate("template" + File.separator + MailTemplate.BASE_NAME_PLAINTEXT);

        StringWriter writer = new StringWriter();
        t.merge(handleAndGetContextChanges(), writer);
        t.process();

        return writer.toString().trim();
    }

    private VelocityContext handleAndGetContextChanges() {
        VelocityContext context = new VelocityContext();
        context.put("firstName", "ME");
        context.put("lastName", "REALLY");
        return context;
    }
}
