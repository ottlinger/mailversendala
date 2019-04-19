package de.aikiit.mailversendala.template;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.StringWriter;

public class VelocityMailTemplate implements MailTemplate {

    private static String TEMPLATE_SUFFIX = ".vm";
    private VelocityEngine velocityEngine = new VelocityEngine();

    public VelocityMailTemplate() {
        // TODO https://www.baeldung.com/apache-velocity
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
        velocityEngine.setProperty("class.resource.loader.class",
                ClasspathResourceLoader.class.getName());
        velocityEngine.init();
    }

    @Override
    public String getHtml() {
        return null;
    }

    @Override
    public String getPlaintext() {
        Template t = velocityEngine.getTemplate("template" + File.separator + MailTemplate.BASE_NAME_PLAINTEXT);


        VelocityContext context = new VelocityContext();
        context.put("firstName", "ME");
        context.put("lastName", "REALLY");

        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        t.process();

        return writer.toString().trim();
    }
}
