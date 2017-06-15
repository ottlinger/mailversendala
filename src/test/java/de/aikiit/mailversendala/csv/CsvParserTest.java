package de.aikiit.mailversendala.csv;

import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 27.05.17.
 */
public class CsvParserTest {

    public static final String CSV_INPUT = "firstname,surname,email,language\n" +
            "Me,Again,foo@bar.com,en\n" +
            "Ich,wieder,foo@bar.de,de\n" +
            "я,то́же,foo@bar.ru,ru\n";

    @Test
    public void objectCreation() {
        assertThat(new CsvParser(null)).isNotNull();
    }

    @Test
    public void launchParsing() throws IOException {
        CsvParser parser = new CsvParser(null);
        assertThat(parser.parse("")).isEmpty();
        assertThat(parser.parse("de")).isEmpty();
    }

    @Test
    public void parseRealCSVDataIntoMailingsForAllLanguages() throws IOException {
        CsvParser parser = new CsvParser(new StringReader(CSV_INPUT));
        assertThat(parser.parse(null)).hasSize(3);
    }

    @Test
    public void parseRealCSVDataIntoMailingsForASpecificLanguages() throws IOException {
        Arrays.asList("de", "en", "ru")
                .forEach(
                        language -> {
                            try {
                                CsvParser parser = new CsvParser(new StringReader(CSV_INPUT));
                                assertThat(parser.parse(language)).hasSize(1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                );
    }
}
