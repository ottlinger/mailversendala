package de.aikiit.mailversendala.csv;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;

/**
 * Created by hirsch on 24.05.17.
 */
public class CsvParser {
    private static final Logger LOG =
            LogManager.getLogger(CsvParser.class);

    public List<Mailing> parse(String language) {
        List<Mailing> results = Lists.newArrayList();

        if (Strings.isNullOrEmpty(language)) {
            LOG.info("Will parse for all languages, which may mean more mails being sent out.");
        }

        Mailing mailing = new Mailing.MailingBuilder().email("my@mail.com").firstname("Your name").surname("Is my name").language(Locale.GERMAN.getLanguage()).build();
        return results;
    }
}
