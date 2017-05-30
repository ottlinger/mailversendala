package de.aikiit.mailversendala;

import de.aikiit.mailversendala.csv.CsvParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.assertj.core.util.Strings;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Hello world!
 */
public class App {
    private static final Logger LOG =
            LogManager.getLogger(App.class);

    public static void main(String[] args) {

        LOG.info("Hello Mailversendala configured with the help of Tamaya!");

        MailConfig configuration = new MailConfig();
        String csvPath = configuration.getCsvPath();

        if(Strings.isNullOrEmpty(csvPath)) {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(configuration.getCsvPath());
            new CsvParser(new InputStreamReader(inputStream));


        }
    }
}
