package de.aikiit.mailversendala.csv;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 27.05.17.
 */
public class CsvParserTest {

    private static final String CSV_INPUT = "firstname,surname,email,language\n" +
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
        assertThat(parser.parse(Optional.empty())).isEmpty();
        assertThat(parser.parse(Optional.of("de"))).isEmpty();
    }

    @Test
    public void parseRealCSVDataIntoMailingsForAllLanguages() throws IOException {
        CsvParser parser = new CsvParser(new StringReader(CSV_INPUT));
        assertThat(parser.parse(Optional.empty())).hasSize(3);
    }

    @Ignore
    public void parseRealCSVDataIntoMailingsForASpecificLanguages() throws IOException {
        CsvParser parser = new CsvParser(new StringReader(CSV_INPUT));
        assertThat(parser.parse(Optional.of("de"))).hasSize(1);
        assertThat(parser.parse(Optional.of("ru"))).hasSize(1);
        assertThat(parser.parse(Optional.of("en"))).hasSize(1);
    }
}
