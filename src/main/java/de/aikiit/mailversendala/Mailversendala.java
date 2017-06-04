package de.aikiit.mailversendala;

import de.aikiit.mailversendala.csv.CsvParser;
import de.aikiit.mailversendala.csv.Mailing;
import org.apache.commons.mail.EmailException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.util.Strings;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Encapsulates the actual CSV parsing and mailing generation.
 */
public class Mailversendala {
    private static final Logger LOG =
            LogManager.getLogger(Mailversendala.class);

    public static void sendOut(MailConfig configuration) throws IOException {
        LOG.info("**** MAILVERSENDALA: Starting .... ****");

        String csvPath = configuration.getCsvPath();

        LOG.info("Consuming CSV: {}", csvPath);

        if (!Strings.isNullOrEmpty(csvPath)) {
            LOG.info(configuration.getCsvPath());


            File asFile = new File(configuration.getCsvPath());
            if (asFile.exists()) {
                CsvParser parser = new CsvParser(new FileReader(asFile));
                final List<Mailing> mailings = parser.parse(Optional.empty());

                final int total = mailings.size();
                LOG.info("Will send out {} mails ... hold on tight :-)", total);


                final AtomicInteger errorCounter = new AtomicInteger(0);
                final AtomicInteger mailCounter = new AtomicInteger(0);
                mailings.forEach(mailing -> {
                    try {
                        new SendOut(mailing).send();
                        LOG.info("Successfully send out {}.mail", mailCounter.get());
                    } catch (EmailException e) {
                        errorCounter.incrementAndGet();
                        LOG.error("Problem while sending out {}", mailing, e);
                    }
                });

                LOG.info("**** MAILVERSENDALA-report: {} total mails ****", total);
                LOG.info("**** MAILVERSENDALA-report: {} successfully send out ****", mailCounter.get());
                LOG.info("**** MAILVERSENDALA-report: {} errors ****", errorCounter.get());

            } else {
                LOG.warn("Nothing to do - please configure your CSV path properly, either as environment variable or as a runtime parameter. Example: java -Dcsvpath=foo -jar fatJar.jar");
            }
        }
        LOG.info("**** MAILVERSENDALA: Application shutdown .... ****");


    }
}
