/*
 * Copyright (c) P. Ottlinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.aikiit.mailversendala.template;

import de.aikiit.mailversendala.MailConfig;
import de.aikiit.mailversendala.csv.Mailing;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.implement.EscapeHtmlReference;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Compiles external UTF-8 templates once per run and renders isolated recipient contexts.
 * HTML references are escaped; plain-text references are left unchanged.
 */
public class VelocityMailTemplate {

    private final Template html;
    private final Template plaintext;

    /**
     * Load both templates from the configured external directory before sending begins.
     * @param configuration directory and template filenames.
     * @throws IOException if a template is missing, unreadable or syntactically invalid.
     */
    public VelocityMailTemplate(MailConfig configuration) throws IOException {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "file");
        engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, configuration.getTemplatePath());
        engine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE, true);
        engine.setProperty(RuntimeConstants.RUNTIME_REFERENCES_STRICT, true);
        try {
            engine.init();
            html = engine.getTemplate(configuration.getHtmlTemplate(), StandardCharsets.UTF_8.name());
            plaintext = engine.getTemplate(configuration.getPlaintextTemplate(), StandardCharsets.UTF_8.name());
        } catch (VelocityException e) {
            throw new IOException("Cannot load mail templates from " + configuration.getTemplatePath(), e);
        }
    }

    /**
     * Render both alternatives for one recipient, without retaining recipient state.
     * @param recipient CSV recipient data.
     * @return rendered HTML and plain-text bodies.
     */
    public MailTemplate render(Mailing recipient) {
        VelocityContext htmlContext = context(recipient);
        EventCartridge events = new EventCartridge();
        events.addEventHandler(new EscapeHtmlReference());
        events.attachToContext(htmlContext);
        return new RenderedMail(merge(html, htmlContext), merge(plaintext, context(recipient)));
    }

    private VelocityContext context(Mailing recipient) {
        VelocityContext context = new VelocityContext();
        String firstName = Objects.requireNonNullElse(recipient.firstname(), "");
        String lastName = Objects.requireNonNullElse(recipient.surname(), "");
        context.put("firstName", firstName);
        context.put("lastName", lastName);
        context.put("firstname", firstName);
        context.put("surname", lastName);
        context.put("email", Objects.requireNonNullElse(recipient.email(), ""));
        context.put("salutation", Objects.requireNonNullElse(recipient.salutation(), ""));
        return context;
    }

    private String merge(Template template, VelocityContext context) {
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private record RenderedMail(String html, String plaintext) implements MailTemplate {
        @Override
        public String getHtml() {
            return html;
        }

        @Override
        public String getPlaintext() {
            return plaintext;
        }
    }
}
