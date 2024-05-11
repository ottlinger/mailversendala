package de.aikiit.mailversendala.template;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import de.aikiit.mailversendala.MailConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileMailTemplate implements MailTemplate {
    private String html;
    private String plaintext;

    public FileMailTemplate(InputStream html, InputStream plaintext) throws IOException {
        this.html = CharStreams.toString(new InputStreamReader(html, Charsets.UTF_8));
        this.plaintext = CharStreams.toString(new InputStreamReader(plaintext, Charsets.UTF_8));
    }

    public FileMailTemplate(MailConfig config) throws IOException {
        this.html = readInLanguage(Paths.get(config.getTemplatePath(), MailTemplate.BASE_NAME_HTML));
        this.plaintext = readInLanguage(Paths.get(config.getTemplatePath(), MailTemplate.BASE_NAME_PLAINTEXT));
    }

    private static String readInLanguage(Path file) throws IOException {
        return Files.readString(file, StandardCharsets.UTF_8);
    }

    @Override
    public String getHtml() {
        return html;
    }

    @Override
    public String getPlaintext() {
        return plaintext;
    }
}
