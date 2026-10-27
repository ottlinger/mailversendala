package de.aikiit.mailversendala.template;

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


import de.aikiit.mailversendala.MailConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileMailTemplateTest {

    private static final String TEXT = "Grüße für Jörg & Zoë";
    private static final String HTML = "<html><body>Grüße für <b>Jörg &amp; Zoë</b></body></html>";
    private static FileMailTemplate template;

    @Mock
    private MailConfig config;

    @TempDir
    public Path folder;

    @BeforeAll
    static void createTemplate() throws IOException {
        InputStream html = new ByteArrayInputStream(HTML.getBytes(StandardCharsets.UTF_8));
        InputStream plaintext = new ByteArrayInputStream(TEXT.getBytes(StandardCharsets.UTF_8));
        template = new FileMailTemplate(html, plaintext);
    }

    @Test
    public void getHtml() {
        assertThat(template.getHtml()).isEqualTo(HTML);
    }

    @Test
    public void getPlaintext() {
        assertThat(template.getPlaintext()).isEqualTo(TEXT);
    }

    @Test
    public void recordAccessorsMatchInterfaceGetters() {
        FileMailTemplate result = new FileMailTemplate(HTML, TEXT);

        assertThat(result.html()).isEqualTo(result.getHtml()).isEqualTo(HTML);
        assertThat(result.plaintext()).isEqualTo(result.getPlaintext()).isEqualTo(TEXT);
    }

    @Test
    public void equalityAndHashCodeAreBasedOnBothBodies() {
        FileMailTemplate first = new FileMailTemplate(HTML, TEXT);
        FileMailTemplate same = new FileMailTemplate(HTML, TEXT);

        assertThat(first).isEqualTo(same).hasSameHashCodeAs(same);
        assertThat(first).isNotEqualTo(new FileMailTemplate("Other HTML", TEXT));
        assertThat(first).isNotEqualTo(new FileMailTemplate(HTML, "Other text"));
    }

    @Test
    public void dataExtractionFromConfigFilePath() throws IOException {
        String configBasePath = folder.toString();
        when(config.getTemplatePath()).thenReturn(configBasePath);
        when(config.getHtmlTemplate()).thenReturn("custom.html");
        when(config.getPlaintextTemplate()).thenReturn("custom.txt");

        Files.writeString(Paths.get(configBasePath, "custom.txt"), TEXT, StandardCharsets.UTF_8);
        Files.writeString(Paths.get(configBasePath, "custom.html"), HTML, StandardCharsets.UTF_8);

        template = new FileMailTemplate(config);

        assertThat(template.getHtml()).isEqualTo(HTML);
        assertThat(template.getPlaintext()).isEqualTo(TEXT);
        assertThat(template).isEqualTo(new FileMailTemplate(HTML, TEXT));
    }
}
