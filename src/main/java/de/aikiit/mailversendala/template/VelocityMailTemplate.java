package de.aikiit.mailversendala.template;

/*
 * Copyright (c) P. Ottlinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.StringWriter;

/**
 * Mailing template that is based on Apache Velocity templates.
 */
public class VelocityMailTemplate implements MailTemplate {

    private final VelocityEngine velocityEngine = new VelocityEngine();

    /**
     * Instantiate underlying template engine.
     */
    public VelocityMailTemplate() {
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
        velocityEngine.setProperty("class.resource.loader.class",
                ClasspathResourceLoader.class.getName());
        velocityEngine.init();
    }

    @Override
    public String getHtml() {
        return loadAndGetContents("template" + File.separator + BASE_NAME_HTML);
    }

    @Override
    public String getPlaintext() {
        return loadAndGetContents("template" + File.separator + BASE_NAME_PLAINTEXT);
    }

    private VelocityContext handleAndGetContextChanges() {
        VelocityContext context = new VelocityContext();
        context.put("firstName", "ME");
        context.put("lastName", "REALLY");
        return context;
    }

    private String loadAndGetContents(String templateName) {
        Template t = velocityEngine.getTemplate(templateName);

        StringWriter writer = new StringWriter();
        t.merge(handleAndGetContextChanges(), writer);
        t.process();

        return writer.toString().trim();
    }
}
