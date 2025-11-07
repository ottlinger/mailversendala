package de.aikiit.mailversendala;

import de.aikiit.mailversendala.csv.CsvParserTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by hirsch on 04.06.17.
 */
@ExtendWith(MockitoExtension.class)
public class MailversendalaTest {

    private static final String CSV_NAME = "MailversendalaTest";

    @TempDir
    public static Path testFolder;

    @Mock
    private MailConfig configuration;

    @BeforeAll
    public static void flushCSVData() throws IOException {
        final Path csv = Files.createFile(testFolder.resolve(CSV_NAME));
        Files.write(csv, (CsvParserTest.CSV_INPUT + "error,foo@bar.ru\n").getBytes());
    }

    @Test
    public void parseCSVFromARealFolder() throws IOException {
        when(configuration.getCsvPath()).thenReturn(testFolder.toString() + File.separator + CSV_NAME);

        final MailingResult result = Mailversendala.sendOut(configuration);
        assertThat(result).isNotNull();
        assertThat(result.getErrorCounter()).isEmpty();

        final Optional<AtomicInteger> mailCounter = result.getMailCounter();
        assertThat(mailCounter).isPresent();
        assertThat(mailCounter.get().get()).isEqualTo(3);
    }

}
