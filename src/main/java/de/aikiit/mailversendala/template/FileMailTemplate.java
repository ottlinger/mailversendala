package de.aikiit.mailversendala.template;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import lombok.Data;
import org.assertj.core.util.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Data
public class FileMailTemplate implements MailTemplate {
    private Map<String, String> html = new HashMap<>();
    private Map<String, String> plaintext = new HashMap<>();

    public FileMailTemplate(InputStream html, InputStream plaintext, Map<String, String> parameters) throws IOException {
        // TODO 		try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) { in a separate class
        this.html.put(MailTemplate.BACKUP_LANGUAGE, CharStreams.toString(new InputStreamReader(html, Charsets.UTF_8)));
        this.plaintext.put(MailTemplate.BACKUP_LANGUAGE, CharStreams.toString(new InputStreamReader(plaintext, Charsets.UTF_8)));
    }

    /*
    use MailTemplate.LANGUAGES

    public FileMailTemplate(File basePath, Map<String, String> parameters) {
        Paths.get(new File(basePath, MailTemplate.BASE_NAME_PLAINTEXT));
        Paths.get(new File(basePath, MailTemplate.BASE_NAME_HTML));
    }
*/
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
