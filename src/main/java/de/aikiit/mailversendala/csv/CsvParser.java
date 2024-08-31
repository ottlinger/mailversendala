package de.aikiit.mailversendala.csv;

import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Main class to parse given arguments for mailversendala configuration.
 */
public class CsvParser {
    private static final Logger LOG =
            LogManager.getLogger(CsvParser.class);
    private final Reader reader;

    /**
     * Parse given CSV values internally.
     * @param csvInput read configuration data as CSV.
     */
    public CsvParser(Reader csvInput) {
        this.reader = csvInput;
    }

    /**
     * Do the actual parsing of a given CSV configuration.
     * @return a list of mailings to send out.
     * @throws IOException in case of errors.
     */
    public List<Mailing> parse() throws IOException {
        final List<Mailing> results = Lists.newArrayList();
        if (reader != null) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder().setSkipHeaderRecord(true).build().parse(reader);
            records.forEach(record -> {
                        try {
                            Mailing mailing = Mailing.builder().//
                                    email(record.get(Headers.EMAIL)).//
                                    firstname(record.get(Headers.FIRSTNAME)).//
                                    surname(record.get(Headers.SURNAME)).//
                                    build();

                            LOG.debug("Parsed and added mailing: {}", mailing);
                            results.add(mailing);
                        } catch (IllegalArgumentException e) {
                            LOG.error("Unable to parse CSV-row '{}'.", record);
                        }
                    }
            );
        }
        return results;
    }
}
