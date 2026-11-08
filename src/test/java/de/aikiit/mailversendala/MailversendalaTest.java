package de.aikiit.mailversendala;

/*
 * Copyright (c) P. Ottlinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import de.aikiit.mailversendala.csv.CsvParserTest;
import de.aikiit.mailversendala.csv.Mailing;
import de.aikiit.mailversendala.template.MailResources;
import de.aikiit.mailversendala.template.MailTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    public void parseCSVFromARealFolderWithoutTestRecipient(String testAddress) throws IOException {
        when(configuration.getTo()).thenReturn(testAddress);
        when(configuration.getCsvPath()).thenReturn(testFolder.toString() + File.separator + CSV_NAME);
        when(configuration.getTemplatePath()).thenReturn("src/test/resources/template");
        when(configuration.getHtmlTemplate()).thenReturn("mailtemplate.html");
        when(configuration.getPlaintextTemplate()).thenReturn("mailtemplate.txt");
        when(configuration.getHost()).thenReturn("localhost");
        when(configuration.getPort()).thenReturn(465);
        when(configuration.getUsername()).thenReturn("user");
        when(configuration.getPassword()).thenReturn("password");
        when(configuration.getFrom()).thenReturn("sender@example.test");
        when(configuration.getSubject()).thenReturn("Template mailing");

        final MailingResult result = Mailversendala.sendOut(configuration);
        assertThat(result).isNotNull();
        assertThat(result.getErrorCounter()).isEmpty();

        final Optional<AtomicInteger> mailCounter = result.getMailCounter();
        assertThat(mailCounter).isPresent();
        assertThat(mailCounter.get().get()).isEqualTo(3);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void sendsOneRawTestMailInNormalCsvRunEvenWhenReferencesCannotBeResolved(boolean unknownReference,
                                                                             @TempDir Path folder) throws Exception {
        Path csv = folder.resolve("recipients.csv");
        Files.writeString(csv, "firstname,surname,email,salutation\n"
                + "Ada,Lovelace,ada@example.test,Dear\nGrace,Hopper,grace@example.test,Hello\n");
        String reference = unknownReference ? "${unknownField}" : "${firstName}";
        Files.writeString(folder.resolve("preview.html"), "<p>" + reference + "</p>");
        Files.writeString(folder.resolve("preview.txt"), "Hello " + reference);
        when(configuration.getCsvPath()).thenReturn(csv.toString());
        when(configuration.getTemplatePath()).thenReturn(folder.toString());
        when(configuration.getHtmlTemplate()).thenReturn("preview.html");
        when(configuration.getPlaintextTemplate()).thenReturn("preview.txt");
        when(configuration.getTo()).thenReturn("  myself@example.test  ");
        List<Mailing> recipients = new ArrayList<>();

        try (MockedConstruction<SendOut> senders = mockConstruction(SendOut.class,
                (mock, context) -> recipients.add((Mailing) context.arguments().get(0)))) {
            MailingResult result = Mailversendala.sendOut(configuration);

            assertThat(result.getTotal()).isEqualTo(3);
            assertThat(result.getMailCounter().orElseThrow().get()).isEqualTo(unknownReference ? 1 : 3);
            assertThat(result.getErrorCounter().map(AtomicInteger::get).orElse(0))
                    .isEqualTo(unknownReference ? 2 : 0);
            assertThat(recipients.get(0).email()).isEqualTo("myself@example.test");
            ArgumentCaptor<MailTemplate> preview = ArgumentCaptor.forClass(MailTemplate.class);
            verify(senders.constructed().get(0)).send(preview.capture(), any(MailResources.class));
            assertThat(preview.getValue().getHtml()).isEqualTo("<p>" + reference + "</p>");
            assertThat(preview.getValue().getPlaintext()).isEqualTo("Hello " + reference);
            if (unknownReference) {
                assertThat(senders.constructed()).hasSize(1);
            } else {
                assertThat(recipients).extracting(Mailing::email)
                        .containsExactly("myself@example.test", "ada@example.test", "grace@example.test");
                ArgumentCaptor<MailTemplate> personalized = ArgumentCaptor.forClass(MailTemplate.class);
                verify(senders.constructed().get(1)).send(personalized.capture(), any(MailResources.class));
                assertThat(personalized.getValue().getHtml()).isEqualTo("<p>Ada</p>");
            }
        }
    }

    @Test
    void missingTemplatesAbortBeforeAnySmtpClientIsCreated() {
        when(configuration.getCsvPath()).thenReturn(testFolder.resolve(CSV_NAME).toString());
        when(configuration.getTemplatePath()).thenReturn(testFolder.toString());
        when(configuration.getHtmlTemplate()).thenReturn("missing.html");

        assertThatThrownBy(() -> Mailversendala.sendOut(configuration))
                .isInstanceOf(IOException.class).hasMessageContaining("Cannot load mail templates");
        verify(configuration, never()).getHost();
    }

    @Test
    void missingImageForLaterRecipientAbortsBeforeFirstDelivery() throws IOException {
        Path csv = testFolder.resolve("image-recipients.csv");
        Files.writeString(csv, "firstname,surname,email,salutation\n"
                + "valid,One,first@example.test,Dear\nmissing,Two,second@example.test,Dear\n");
        Files.writeString(testFolder.resolve("images.html"), "<p>Hello ${firstname}</p><img src='${firstname}.png'>");
        Files.writeString(testFolder.resolve("images.txt"), "Hello ${firstname}");
        try (var input = getClass().getResourceAsStream("/template/img/monkey-toy-16-118729.png")) {
            Files.copy(input, testFolder.resolve("valid.png"));
        }
        when(configuration.getCsvPath()).thenReturn(csv.toString());
        when(configuration.getTemplatePath()).thenReturn(testFolder.toString());
        when(configuration.getHtmlTemplate()).thenReturn("images.html");
        when(configuration.getPlaintextTemplate()).thenReturn("images.txt");

        assertThatThrownBy(() -> Mailversendala.sendOut(configuration))
                .isInstanceOf(IOException.class).hasMessageContaining("missing.png");
        verify(configuration, never()).getHost();
    }

    @Test
    void countsRenderingErrorsWithoutAttemptingDelivery() throws IOException {
        Files.writeString(testFolder.resolve("invalid-reference.html"), "<p>${unknownField}</p>");
        Files.writeString(testFolder.resolve("valid.txt"), "Hello ${firstName}");
        when(configuration.getCsvPath()).thenReturn(testFolder.resolve(CSV_NAME).toString());
        when(configuration.getTemplatePath()).thenReturn(testFolder.toString());
        when(configuration.getHtmlTemplate()).thenReturn("invalid-reference.html");
        when(configuration.getPlaintextTemplate()).thenReturn("valid.txt");

        MailingResult result = Mailversendala.sendOut(configuration);

        assertThat(result.getTotal()).isEqualTo(3);
        assertThat(result.getErrorCounter().orElseThrow().get()).isEqualTo(3);
        assertThat(result.getMailCounter()).isEmpty();
        verify(configuration, never()).getHost();
    }

}
