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


import de.aikiit.mailversendala.csv.CsvParser;
import de.aikiit.mailversendala.csv.Mailing;
import de.aikiit.mailversendala.template.FileMailTemplate;
import de.aikiit.mailversendala.template.MailResources;
import de.aikiit.mailversendala.template.MailTemplate;
import de.aikiit.mailversendala.template.VelocityMailTemplate;
import org.apache.commons.mail2.core.EmailException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.exception.VelocityException;
import org.assertj.core.util.Strings;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Encapsulates the actual CSV parsing and mailing generation.
 */
public class Mailversendala {
    private static final Logger LOG =
            LogManager.getLogger(Mailversendala.class);

    /**
     * Send out mails with the given configuration and return result object afterwards.
     * @param configuration application configuration.
     * @return result object of the current application run.
     * @throws IOException in case of errors.
     */
    public static MailingResult sendOut(MailConfig configuration) throws IOException {
        LOG.info("**** MAILVERSENDALA: Starting .... ****");

        String csvPath = configuration.getCsvPath();
        MailingResult result = new MailingResult();

        LOG.info("Consuming CSV: {}", csvPath);

        if (!Strings.isNullOrEmpty(csvPath)) {
            File asFile = new File(configuration.getCsvPath());
            if (asFile.exists()) {
                try (FileReader r = new FileReader(asFile, StandardCharsets.UTF_8)) {
                    CsvParser parser = new CsvParser(r);
                    final List<Mailing> mailings = parser.parse();

                    // Load and compile both external templates before sending any messages.
                    final VelocityMailTemplate templates = new VelocityMailTemplate(configuration);
                    String testAddress = configuration.getTo();
                    boolean sendTestMail = testAddress != null && !testAddress.isBlank();
                    final int total = mailings.size() + (sendTestMail ? 1 : 0);
                    LOG.info("Will send out {} mails ... hold on tight :-)", total);

                    MailResources resources = new MailResources(configuration);
                    List<PreparedMail> prepared = new ArrayList<>();
                    if (sendTestMail) {
                        // The single test mail deliberately keeps all placeholders unresolved.
                        MailTemplate rawTemplate = new FileMailTemplate(configuration);
                        resources.validate(rawTemplate);
                        Mailing testRecipient = Mailing.builder().email(testAddress.trim()).build();
                        prepared.add(new PreparedMail(testRecipient, rawTemplate));
                        LOG.debug("Prepared unrendered test mail to: {}", testRecipient.email());
                    }
                    for (Mailing mailing : mailings) {
                        try {
                            MailTemplate rendered = templates.render(mailing);
                            resources.validate(rendered);
                            prepared.add(new PreparedMail(mailing, rendered));
                        } catch (VelocityException e) {
                            result.addError();
                            LOG.error("Problem while rendering {}", mailing, e);
                        }
                    }
                    // Validate every recipient's resource references before the first SMTP delivery.
                    if (!prepared.isEmpty()) {
                        LOG.info("SMTP transport: {}", configuration.getPort() == 465 ? "SMTPS" : "STARTTLS");
                        if (!configuration.sendOutMails()) {
                            LOG.warn("Running in DEMO mode, thus no mails will be sent out.");
                        }
                    }
                    int mailNumber = 0;
                    for (PreparedMail mail : prepared) {
                        mailNumber++;
                        try {
                            new SendOut(mail.recipient(), configuration).send(mail.template(), resources);
                            result.addSuccess();
                            if (configuration.sendOutMails()) {
                                LOG.info("Mail {}/{} erfolgreich gesendet an: {}", mailNumber, total, mail.recipient().email());
                            } else {
                                LOG.info("Mail {}/{} im DEMO-Modus vorbereitet für: {}", mailNumber, total, mail.recipient().email());
                            }
                        } catch (EmailException e) {
                            result.addError();
                            LOG.error("Problem while sending out {}", mail.recipient(), e);
                        }
                    }

                    LOG.info("**** MAILVERSENDALA-report: {} total mails ****", total);
                    LOG.info("**** MAILVERSENDALA-report: {} successfully sent out ****", result.getMailCounter().orElse(new AtomicInteger(0)));
                    LOG.info("**** MAILVERSENDALA-report: {} errors ****", result.getErrorCounter().orElse(new AtomicInteger(0)));
                }

            } else {
                LOG.warn("Nothing to do - please configure your CSV path properly, either as environment variable or as a runtime parameter. Example: java -Dcsvpath=foo -jar fatJar.jar");
            }
        }
        LOG.info("**** MAILVERSENDALA: Application shutdown .... ****");
        return result;
    }

    private record PreparedMail(Mailing recipient, MailTemplate template) { }
}
