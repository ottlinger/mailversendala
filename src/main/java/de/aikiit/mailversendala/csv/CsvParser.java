package de.aikiit.mailversendala.csv;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by hirsch on 24.05.17.
 */
public class CsvParser {
    private static final Logger LOG =
            LogManager.getLogger(CsvParser.class);

    public List<Mailing> parse(Optional<String> language) {
        List<Mailing> results = Lists.newArrayList();

        if (!language.isPresent()) {
            LOG.info("Will parse for all languages, which may mean more mails being sent out.");
        }

        Mailing mailing = Mailing.builder().email("my@mail.com").firstname("Your name").surname("Is my name").language(Locale.GERMAN.getLanguage()).build();
        LOG.info("Parsed mailing: {}", mailing);
        results.add(mailing);

        return results;
    }
}
