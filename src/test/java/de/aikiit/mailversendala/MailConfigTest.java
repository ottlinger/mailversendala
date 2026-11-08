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


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Created by hirsch on 15.05.17.
 */
public class MailConfigTest {

    private final MailConfig mailConfig = new MailConfig();

    @TempDir
    Path directory;

    @Test
    void readsExternalUtf8ConfigurationAndAllowsJvmOverrides() throws Exception {
        Path file = directory.resolve("mail.properties");
        Files.writeString(file, "subject=Einladung für Jörg\nhtmltemplate=external.html\ndemomode=true\n"
                + "replyto=antwort@example.test\n");
        String previousConfig = System.getProperty("config");
        String previousSubject = System.getProperty("subject");
        try {
            System.setProperty("config", file.toString());
            System.clearProperty("subject");
            MailConfig external = new MailConfig();
            assertThat(external.getSubject()).isEqualTo("Einladung für Jörg");
            assertThat(external.getHtmlTemplate()).isEqualTo("external.html");
            assertThat(external.getReplyTo()).isEqualTo("antwort@example.test");
            assertThat(external.sendOutMails()).isFalse();
            assertThat(external.getPlaintextTemplate()).isEqualTo("mailtemplate.txt");

            System.setProperty("subject", "JVM override");
            assertThat(new MailConfig().getSubject()).isEqualTo("JVM override");
        } finally {
            restoreProperty("config", previousConfig);
            restoreProperty("subject", previousSubject);
        }
    }

    @Test
    void rejectsMissingBlankAndMalformedConfigurationInsteadOfUsingDefaults() throws Exception {
        String previousConfig = System.getProperty("config");
        Path malformed = directory.resolve("malformed.properties");
        Files.writeString(malformed, "password=secret\\uINVALID\n");
        try {
            for (String path : Arrays.asList(directory.resolve("missing.properties").toString(),
                    directory.toString(), " ", malformed.toString())) {
                System.setProperty("config", path);
                assertThatThrownBy(MailConfig::new)
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageNotContaining("secret");
            }
        } finally {
            restoreProperty("config", previousConfig);
        }
    }

    private static void restoreProperty(String key, String previous) {
        if (previous == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, previous);
        }
    }

    @Test
    public void ensureConfigurationIsValid() {
        for (String token : Arrays.asList(mailConfig.getSubject(), mailConfig.getFrom(), mailConfig.getPassword(), mailConfig.getHost(), mailConfig.getTo(), mailConfig.getUsername(), mailConfig.getCsvPath(), mailConfig.getTemplatePath())) {
            assertThat(token).isNotNull();
        }

        assertThat(mailConfig.getPort()).isGreaterThan(0);
        assertThat(mailConfig.getHtmlTemplate()).isEqualTo("mailtemplate.html");
        assertThat(mailConfig.getPlaintextTemplate()).isEqualTo("mailtemplate.txt");
        assertThat(mailConfig.getAttachments()).isEmpty();
        assertThat(mailConfig.getReplyTo()).isEmpty();
    }
}
