package de.aikiit.mailversendala;

import de.aikiit.mailversendala.csv.CsvParserTest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

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
        final File csv = new File(testFolder.getRoot(), CSV_NAME);
        Files.write(csv.toPath(), (CsvParserTest.CSV_INPUT + "error,foo@bar.ru\n").getBytes());
    }

    @Test
    public void parseCSVFromARealFolder() throws IOException {

        when(configuration.getCsvPath()).thenReturn(testFolder.getRoot().toString() + "/" + CSV_NAME);

        final MailingResult result = Mailversendala.sendOut(configuration);
        assertThat(result).isNotNull();
        assertThat(result.getErrorCounter()).isEmpty();

        final Optional<AtomicInteger> mailCounter = result.getMailCounter();
        assertThat(mailCounter).isNotEmpty();
        assertThat(mailCounter.get().get()).isEqualTo(3);
    }

}
