package de.aikiit.mailversendala.template;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

public class FileMailTemplateTest {

    private static final String TEXT = "This is a mailtemplate";
    private static final String HTML = "<html><body>This is an <b>HTML</b> mailtemplate</body></html>";
    private FileMailTemplate template;

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


}