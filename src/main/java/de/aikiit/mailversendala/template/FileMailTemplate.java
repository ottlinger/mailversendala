package de.aikiit.mailversendala.template;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import de.aikiit.mailversendala.MailConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileMailTemplate implements MailTemplate {
    private Map<String, String> html = new HashMap<>();
    private Map<String, String> plaintext = new HashMap<>();

    public FileMailTemplate(InputStream html, InputStream plaintext) throws IOException {
        this.html.put(MailTemplate.BACKUP_LANGUAGE, CharStreams.toString(new InputStreamReader(html, Charsets.UTF_8)));
        this.plaintext.put(MailTemplate.BACKUP_LANGUAGE, CharStreams.toString(new InputStreamReader(plaintext, Charsets.UTF_8)));
    }

    public FileMailTemplate(MailConfig config) throws IOException {
        this.html.put(MailTemplate.BACKUP_LANGUAGE, readInLanguage(Paths.get(config.getTemplatePath(), MailTemplate.BASE_NAME_HTML)));
        this.plaintext.put(MailTemplate.BACKUP_LANGUAGE, readInLanguage(Paths.get(config.getTemplatePath(), MailTemplate.BASE_NAME_PLAINTEXT)));
    }

    private static String readInLanguage(Path file) throws IOException {
        return new String(Files.readAllBytes(file));
    }

    @Override
    public String getHtml(String language) {
        if (Strings.isNullOrEmpty(language)) {
            return html.get(MailTemplate.BACKUP_LANGUAGE);
        }

        return html.get(language);
    }

    @Override
    public String getPlaintext(String language) {
        if (Strings.isNullOrEmpty(language)) {
            return plaintext.get(MailTemplate.BACKUP_LANGUAGE);
        }

        return plaintext.get(language);
    }
}
