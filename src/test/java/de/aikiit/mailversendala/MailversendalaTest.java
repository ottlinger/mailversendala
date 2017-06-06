package de.aikiit.mailversendala;

import de.aikiit.mailversendala.csv.CsvParserTest;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by hirsch on 04.06.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class MailversendalaTest {

    private static final String CSV_NAME = "MailversendalaTest";
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Mock
    private MailConfig configuration;

    @Before
    public void flushCSVData() throws IOException {
        final File csv = testFolder.newFile(CSV_NAME);
        Files.write(csv.toPath(), CsvParserTest.CSV_INPUT.getBytes());
    }

    @Test
    public void parseCSVFromARealFolder() throws IOException {

        when(configuration.getCsvPath()).thenReturn(testFolder.toString() + "/"+CSV_NAME);

        Mailversendala.sendOut(configuration);
        assertThat(true).isTrue();


    }


}
