package de.aikiit.mailversendala.csv;

import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 27.05.17.
 */
public class CsvParserTest {

    @Test
    public void objectCreation() {
        assertThat(new CsvParser()).isNotNull();
    }

    @Test
    public void launchParsing() {
        assertThat(new CsvParser().parse(Optional.empty())).hasSize(1);
        assertThat(new CsvParser().parse(Optional.of("de"))).hasSize(1);
    }
}
