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
import java.nio.charset.StandardCharsets;
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
            LOG.info(configuration.getCsvPath());

            File asFile = new File(configuration.getCsvPath());
            if (asFile.exists()) {
                try (FileReader r = new FileReader(asFile, StandardCharsets.UTF_8)) {
                    CsvParser parser = new CsvParser(r);
                    final List<Mailing> mailings = parser.parse();

                    final int total = mailings.size();
                    LOG.info("Will send out {} mails ... hold on tight :-)", total);

                    mailings.forEach(mailing -> {
                        try {
                            new SendOut(mailing).send();
                            result.addSuccess();
                            LOG.info("Successfully send out {}.mail", result.getMailCounter().orElse(new AtomicInteger(0)));
                        } catch (EmailException e) {
                            result.addError();
                            LOG.error("Problem while sending out {}", mailing, e);
                        }
                    });

                    LOG.info("**** MAILVERSENDALA-report: {} total mails ****", total);
                    LOG.info("**** MAILVERSENDALA-report: {} successfully send out ****", result.getMailCounter().orElse(new AtomicInteger(0)));
                    LOG.info("**** MAILVERSENDALA-report: {} errors ****", result.getErrorCounter().orElse(new AtomicInteger(0)));
                }

            } else {
                LOG.warn("Nothing to do - please configure your CSV path properly, either as environment variable or as a runtime parameter. Example: java -Dcsvpath=foo -jar fatJar.jar");
            }
        }
        LOG.info("**** MAILVERSENDALA: Application shutdown .... ****");
        return result;
    }
}
