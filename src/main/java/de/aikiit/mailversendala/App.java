package de.aikiit.mailversendala;

import de.aikiit.mailversendala.csv.CsvParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.util.Strings;

import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Hello world!
 */
public class App {
    private static final Logger LOG =
            LogManager.getLogger(App.class);

    public static void main(String[] args) throws IOException {

        LOG.info("Hello Mailversendala configured with the help of Tamaya!");

        MailConfig configuration = new MailConfig();
        String csvPath = configuration.getCsvPath();

        LOG.info("Using CSV: {}", csvPath);

        if (!Strings.isNullOrEmpty(csvPath)) {
            LOG.info(configuration.getCsvPath());

            CsvParser parser = new CsvParser(new FileReader(new File(configuration.getCsvPath())));
            parser.parse(Optional.empty());
        }
    }
}
