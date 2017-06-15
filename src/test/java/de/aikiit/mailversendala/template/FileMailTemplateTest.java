package de.aikiit.mailversendala.template;

import com.google.common.base.Charsets;
import de.aikiit.mailversendala.MailConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileMailTemplateTest {

    private static final String TEXT = "This is a mailtemplate";
    private static final String HTML = "<html><body>This is an <b>HTML</b> mailtemplate</body></html>";
    private FileMailTemplate template;

    @Mock
    private MailConfig config;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void createTemplate() throws IOException {
        InputStream html = new ByteArrayInputStream(HTML.getBytes());
        InputStream plaintext = new ByteArrayInputStream(TEXT.getBytes());
        this.template = new FileMailTemplate(html, plaintext, Collections.emptyMap());
    }

    @Test
    public void getHtml() {
        assertThat(template.getHtml(null)).isEqualTo(HTML);
    }

    @Test
    public void getPlaintext() {
        assertThat(template.getPlaintext(null)).isEqualTo(TEXT);
    }

    @Test
    public void dataExtractionFromConfigFilePath() throws IOException {
        String configBasePath = folder.getRoot().getAbsolutePath();
        when(config.getTemplatePath()).thenReturn(configBasePath);

        Files.write(Paths.get(configBasePath, MailTemplate.BASE_NAME_PLAINTEXT), TEXT.getBytes(Charsets.UTF_8));
        Files.write(Paths.get(configBasePath, MailTemplate.BASE_NAME_HTML), HTML.getBytes(Charsets.UTF_8));

        this.template = new FileMailTemplate(config);

        assertThat(template.getHtml(null)).isEqualTo(HTML);
        assertThat(template.getPlaintext(null)).isEqualTo(TEXT);
    }

}