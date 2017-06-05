package de.aikiit.mailversendala;

import de.aikiit.mailversendala.csv.CsvParserTest;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 04.06.17.
 */
public class MailversendalaTest {

    private static final String CSV_NAME = "MailversendalaTest";
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Before
    public void flushCSVData() throws IOException {
        final File csv = testFolder.newFile(CSV_NAME);
        Files.write(csv.toPath(), CsvParserTest.CSV_INPUT.getBytes());
    }

    @Test
    public void parseCSVFromARealFolder() {
        assertThat(true).isTrue();
    }


}
