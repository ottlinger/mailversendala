package de.aikiit.mailversendala.csv;

import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 27.05.17.
 */
public class CsvParserTest {

// TODO add test data source  Reader in = new FileReader("path/to/file.csv");

    @Test
    public void objectCreation() {
        assertThat(new CsvParser(Optional.empty())).isNotNull();
    }

    @Test
    public void launchParsing() throws IOException {
        CsvParser parser = new CsvParser(Optional.empty());
        assertThat(parser.parse(Optional.empty())).hasSize(1);
        assertThat(parser.parse(Optional.of("de"))).hasSize(1);
    }
}
