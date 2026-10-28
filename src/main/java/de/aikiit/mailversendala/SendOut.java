package de.aikiit.mailversendala;

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


import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import de.aikiit.mailversendala.csv.Mailing;
import de.aikiit.mailversendala.template.MailResources;
import de.aikiit.mailversendala.template.MailTemplate;
import de.aikiit.mailversendala.template.VelocityMailTemplate;
import org.apache.commons.mail2.core.EmailException;
import org.apache.commons.mail2.jakarta.DefaultAuthenticator;
import org.apache.commons.mail2.jakarta.Email;
import org.apache.commons.mail2.jakarta.HtmlEmail;
import org.apache.commons.mail2.jakarta.SimpleEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * This class performs the actual mail sending and encapsulates all the technicalities to create and send out an email.
 */
public class SendOut {
    private static final Logger LOG =
            LogManager.getLogger(SendOut.class);

    // is transformed into User-Agent-Header
    private static final String SENT_BY = "X-Mailer";
    private static final String SENT_BY_ME = "Mailversendala - https://github.com/ottlinger/mailversendala";

    private final Email email;
    private final HtmlEmail htmlEmail;
    private final MailConfig mailConfig;
    private final Mailing recipient;

    /**
     * Create mail based on the given mailing recipient.
     * @param recipient mailing contents and parameters.
     */
    public SendOut(Mailing recipient) {
        this(recipient, new MailConfig());
    }

    /**
     * Create a sender using the same configuration as the mailing run.
     * @param recipient mailing recipient.
     * @param mailConfig SMTP and message configuration.
     */
    public SendOut(Mailing recipient, MailConfig mailConfig) {
        this(recipient, mailConfig, new SimpleEmail(), new HtmlEmail());
    }

    /**
     * Configure supplied mail clients, allowing tests to use isolated mail sessions.
     */
    SendOut(Mailing recipient, MailConfig mailConfig, Email email, HtmlEmail htmlEmail) {
        this.recipient = recipient;
        this.mailConfig = mailConfig;
        this.email = email;
        this.htmlEmail = htmlEmail;

        if (!mailConfig.sendOutMails()) {
            LOG.warn("Running in DEMO mode, thus no mails will be sent out.");
        }

        LOG.info("Initialized authentication.");
        DefaultAuthenticator authenticator = new DefaultAuthenticator(mailConfig.getUsername(), mailConfig.getPassword());

        int port = mailConfig.getPort();

        email.setHostName(mailConfig.getHost());
        email.setSmtpPort(port);
        email.setAuthenticator(authenticator);
        email.setSSLCheckServerIdentity(true);
        if (465 == port) {
            LOG.info("Using SSL.");
            email.setSSLOnConnect(true);
        } else {
            LOG.info("Using TLS.");
            email.setStartTLSEnabled(true);
            email.setStartTLSRequired(true);
        }
        email.setBounceAddress(mailConfig.getFrom());
        email.addHeader(SENT_BY, SENT_BY_ME);
        LOG.info("Simple email initialized.");

        htmlEmail.setHostName(mailConfig.getHost());
        htmlEmail.setSmtpPort(port);
        htmlEmail.setSSLCheckServerIdentity(true);
        if (465 == port) {
            LOG.info("Using SSL.");
            htmlEmail.setSSLOnConnect(true);
        } else {
            LOG.info("Using TLS.");
            htmlEmail.setStartTLSEnabled(true);
            htmlEmail.setStartTLSRequired(true);
        }
        htmlEmail.setBounceAddress(mailConfig.getFrom());
        htmlEmail.setAuthenticator(authenticator);
        htmlEmail.addHeader(SENT_BY, SENT_BY_ME);
        LOG.info("HTML and text email initialized.");
    }

    /**
     * Test method to verify the actual mail creation and send out.
     * @param args allows sending an email to a given mail address. One parameter is required.
     * @throws Exception in case of errors.
     */
    public static void main(String... args) throws Exception {
        if (args == null || args.length < 1 || Strings.isNullOrEmpty(args[0])) {
            LOG.error("Please call this method with a mail address to send to.");
        } else {
            String email = args[0];
            LOG.warn("Init: Using mail address from runtime parameter: {}", email);

            Mailing mailing = Mailing.builder().email(email).firstname("Your name").surname("Is my name").build();
            MailConfig configuration = new MailConfig();
            VelocityMailTemplate templates = new VelocityMailTemplate(configuration);
            SendOut sendOut = new SendOut(mailing, configuration);
            sendOut.send(templates.render(mailing));
            LOG.info("Send HTML and plain-text message: DONE");
        }
    }

    /**
     * Send a rendered message with HTML and plain-text alternatives in UTF-8.
     * @param template rendered message bodies.
     * @throws EmailException if message construction or delivery fails.
     */
    public void send(MailTemplate template) throws EmailException {
        try {
            send(template, new MailResources(mailConfig));
        } catch (IOException e) {
            throw new EmailException("Cannot load mail resources", e);
        }
    }

    /** Send using the resources validated and snapshotted for the entire run. */
    void send(MailTemplate template, MailResources resources) throws EmailException {
        htmlEmail.setCharset(Charsets.UTF_8.name());
        htmlEmail.setFrom(mailConfig.getFrom());
        htmlEmail.addTo(recipient.getEmail());
        htmlEmail.setSubject(mailConfig.getSubject());
        resources.applyTo(htmlEmail, template);
        if (mailConfig.sendOutMails()) {
            htmlEmail.send();
        }
    }

    /**
     * Send out an email that contains only text contents.
     * @throws EmailException in case of errors.
     */
    public void send() throws EmailException {
        email.setFrom(mailConfig.getFrom());
        email.addTo(this.recipient.getEmail());

        email.setCharset(Charsets.UTF_8.name());

        email.setSubject(mailConfig.getSubject() + " " + new Date());
        email.setMsg("This is a test mail from Mailversendala... :-)");
        if (mailConfig.sendOutMails()) {
            email.send();
        }
    }

    /**
     * Send out an email with plain text and HTML contents.
     *
     * @throws EmailException in case of mail-related errors.
     * @throws MalformedURLException in case of problem while generating the HTML-part of.
     */
    public void sendComplex() throws EmailException, MalformedURLException {
        sendComplex(new URL("https://www.apache.org/images/asf_logo_wide.gif"));
    }

    /**
     * Send HTML mail using the supplied inline image, without requiring a remote resource.
     */
    void sendComplex(URL imageUrl) throws EmailException {
        htmlEmail.addTo(this.recipient.getEmail(), this.recipient.getFirstname() + " " + this.recipient.getSurname());
        htmlEmail.setFrom(mailConfig.getFrom(), "Me");
        htmlEmail.setSubject("HTML" + mailConfig.getSubject() + " " + new Date());

        // embed the image and get the content id
        String cid = htmlEmail.embed(imageUrl, "ASF logo");

        // set the html message
        htmlEmail.setHtmlMsg("<html>The ASF logo - <img src=\"cid:" + cid + "\"></html>");

        // set the alternative message
        htmlEmail.setTextMsg("Your email client does not support HTML messages - thus no ASF logo visible.");

        if (mailConfig.sendOutMails()) {
            htmlEmail.send();
        }
    }
}
