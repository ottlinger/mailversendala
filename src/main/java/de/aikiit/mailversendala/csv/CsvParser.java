package de.aikiit.mailversendala.csv;

import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
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
        final List<Mailing> results = Lists.newArrayList();

        LOG.info("Will parse for language: " + language.orElse("all - which means more mails to send out."));

        if (reader != null) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            records.forEach(record -> {
                        try {
                            String lang = record.get(Headers.LANGUAGE);

                            Mailing mailing = Mailing.builder().//
                                    email(record.get(Headers.EMAIL)).//
                                    firstname(record.get(Headers.FIRSTNAME)).//
                                    language(lang).//
                                    surname(record.get(Headers.SURNAME)).//
                                    build();

                            if (!language.isPresent() || lang.equalsIgnoreCase(language.get())) {
                                LOG.debug("Parsed and added mailing: {}", mailing);
                                results.add(mailing);
                            }
                        } catch (IllegalArgumentException e) {
                            LOG.error("Unable to parse CSV-row '{}'.", record);
                        }
                    }
            );
        }
        return results;
    }
}
