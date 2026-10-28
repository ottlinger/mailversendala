package de.aikiit.mailversendala.csv;

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
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            records.forEach(record -> {
                        try {
                            String email = record.get(Headers.EMAIL);
                            if (email.isBlank()) {
                                throw new IllegalArgumentException("Required field 'email' must not be blank");
                            }
                            Mailing mailing = Mailing.builder().//
                                    email(email).//
                                    firstname(record.get(Headers.FIRSTNAME)).//
                                    surname(record.get(Headers.SURNAME)).//
                                    salutation(record.get(Headers.SALUTATION)).//
                                    build();

                            LOG.debug("Parsed and added mailing: {}", mailing);
                            results.add(mailing);
                        } catch (IllegalArgumentException e) {
                            LOG.error("Unable to parse CSV-row '{}': {}", record, e.getMessage());
                        }
                    }
            );
        }
        return results;
    }
}
