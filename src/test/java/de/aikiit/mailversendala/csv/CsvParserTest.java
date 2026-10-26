package de.aikiit.mailversendala.csv;

import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 27.05.17.
 */
public class CsvParserTest {

    public static final String CSV_INPUT = """
            firstname,surname,email
            Me,Again,foo@bar.com
            Ich,wieder,foo@bar.de
            я,то́же,foo@bar.ru
            """;

    @Test
    public void objectCreation() {
        assertThat(new CsvParser(null)).isNotNull();
    }

    @Test
    public void launchParsing() throws IOException {
        CsvParser parser = new CsvParser(null);
        assertThat(parser.parse()).isEmpty();
    }

    @Test
    public void parseRealCSVDataIntoMailingsForAllLanguages() throws IOException {
        CsvParser parser = new CsvParser(new StringReader(CSV_INPUT));
        assertThat(parser.parse()).hasSize(3);
    }
}
