package de.aikiit.mailversendala.csv;

import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by hirsch on 24.05.17.
 */
public class CsvParser {
    private static final Logger LOG =
            LogManager.getLogger(CsvParser.class);
    private final Reader reader;

    public CsvParser(Reader csvInput) {
        this.reader = csvInput;
    }

    public List<Mailing> parse(Optional<String> language) throws IOException {
        List<Mailing> results = Lists.newArrayList();

        if (!language.isPresent()) {
            LOG.info("Will parse for all languages, which may mean more mails being sent out.");
        }

        if (reader != null) {
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);
            if (records != null) {
                records.forEach(record ->
                        results.add(Mailing.builder().email(record.get(Headers.EMAIL)).firstname(record.get(Headers.FIRSTNAME)).language(record.get(Headers.LANGUAGE)).surname(record.get(Headers.SURNAME)).build())
                );
            }
        }


        Mailing mailing = Mailing.builder().email("my@mail.com").firstname("Your name").surname("Is my name").language(Locale.GERMAN.getLanguage()).build();
        LOG.info("Parsed mailing: {}", mailing);
        results.add(mailing);

        return results;
    }
}
