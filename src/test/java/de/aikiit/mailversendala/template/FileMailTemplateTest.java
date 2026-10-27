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


import com.google.common.base.Charsets;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileMailTemplateTest {

    private static final String TEXT = "This is a mailtemplate";
    private static final String HTML = "<html><body>This is an <b>HTML</b> mailtemplate</body></html>";
    private static FileMailTemplate template;

    @Mock
    private MailConfig config;

    @TempDir
    public Path folder;

    @BeforeAll
    static void createTemplate() throws IOException {
        InputStream html = new ByteArrayInputStream(HTML.getBytes());
        InputStream plaintext = new ByteArrayInputStream(TEXT.getBytes());
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
    public void dataExtractionFromConfigFilePath() throws IOException {
        String configBasePath = folder.toString();
        when(config.getTemplatePath()).thenReturn(configBasePath);
        when(config.getHtmlTemplate()).thenReturn("custom.html");
        when(config.getPlaintextTemplate()).thenReturn("custom.txt");

        Files.writeString(Paths.get(configBasePath, "custom.txt"), TEXT, Charsets.UTF_8);
        Files.writeString(Paths.get(configBasePath, "custom.html"), HTML, Charsets.UTF_8);

        template = new FileMailTemplate(config);

        assertThat(template.getHtml()).isEqualTo(HTML);
        assertThat(template.getPlaintext()).isEqualTo(TEXT);
    }
}
