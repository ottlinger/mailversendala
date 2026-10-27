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
import com.google.common.io.CharStreams;
import de.aikiit.mailversendala.MailConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Template class that is based on a file, whose contents is replaced in a structured manner.
 */
public class FileMailTemplate implements MailTemplate {
    private final String html;
    private final String plaintext;

    /**
     * Read given input streams and populate internal mailing contents.
     * @param html HTML-format contents.
     * @param plaintext plain text-contents.
     * @throws IOException in case of errors.
     */
    public FileMailTemplate(InputStream html, InputStream plaintext) throws IOException {
        this.html = CharStreams.toString(new InputStreamReader(html, Charsets.UTF_8));
        this.plaintext = CharStreams.toString(new InputStreamReader(plaintext, Charsets.UTF_8));
    }

    /**
     * Apply given configuration and generate internal mailing contents.
     * @param config configuration parameters of the application.
     * @throws IOException in case of errors.
     */
    public FileMailTemplate(MailConfig config) throws IOException {
        this.html = readInLanguage(Paths.get(config.getTemplatePath(), BASE_NAME_HTML));
        this.plaintext = readInLanguage(Paths.get(config.getTemplatePath(), BASE_NAME_PLAINTEXT));
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
