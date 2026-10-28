/*
 * Copyright (c) P. Ottlinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.aikiit.mailversendala.template;

import de.aikiit.mailversendala.MailConfig;
import de.aikiit.mailversendala.csv.Mailing;
import de.aikiit.mailversendala.csv.CsvParser;
import org.apache.velocity.exception.VelocityException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VelocityMailTemplateTest {

    @TempDir
    Path directory;

    @Test
    void rendersExternalTemplatesWithEscapedHtmlAndUnescapedPlaintext() throws Exception {
        VelocityMailTemplate templates = templates("<p>${firstName} ${lastName}: ${email}</p>",
                "Dear ${firstname} ${surname}: ${email}\n");
        Mailing recipient = Mailing.builder().firstname("Jörg <Admin>").surname("Smith & Jones")
                .email("user@example.test").build();

        MailTemplate result = templates.render(recipient);

        assertThat(result.getHtml()).isEqualTo("<p>J&ouml;rg &lt;Admin&gt; Smith &amp; Jones: user@example.test</p>");
        assertThat(result.getPlaintext())
                .isEqualTo("Dear Jörg <Admin> Smith & Jones: user@example.test\n");
    }

    @Test
    void compiledTemplatesKeepRecipientAndAlternativeContextsIsolated() throws Exception {
        VelocityMailTemplate templates = templates("#set($firstName = 'HTML only')${firstName} ${surname}",
                "${firstName} ${lastName}");

        MailTemplate first = templates.render(Mailing.builder().firstname("Ada").surname("Lovelace").build());
        MailTemplate second = templates.render(Mailing.builder().firstname("Grace").surname("Hopper").build());

        assertThat(first.getHtml()).isEqualTo("HTML only Lovelace");
        assertThat(first.getPlaintext()).isEqualTo("Ada Lovelace");
        assertThat(second.getHtml()).isEqualTo("HTML only Hopper");
        assertThat(second.getPlaintext()).isEqualTo("Grace Hopper");
    }

    @Test
    void treatsAbsentRecipientFieldsAsEmptyStringsAndPreservesWhitespace() throws Exception {
        VelocityMailTemplate templates = templates("  <p>${salutation}${firstname}${surname}${email}</p>\n",
                "\n${salutation}${firstName}${lastName}\n");

        MailTemplate result = templates.render(Mailing.builder().build());

        assertThat(result.getHtml()).isEqualTo("  <p></p>\n");
        assertThat(result.getPlaintext()).isEqualTo("\n\n");
    }

    @Test
    void supportsVelocityConditionals() throws Exception {
        VelocityMailTemplate templates = templates("#if($firstname)Hello ${firstname}#else Hello there#end",
                "#if($firstname)Hello ${firstname}#else Hello there#end");

        MailTemplate result = templates.render(Mailing.builder().firstname("Ada").build());

        assertThat(result.getHtml()).isEqualTo("Hello Ada");
        assertThat(result.getPlaintext()).isEqualTo("Hello Ada");
    }

    @Test
    void rendersIndividualSalutationsFromCsvInBothAlternatives() throws Exception {
        String csv = """
                firstname,surname,email,salutation
                Erika,Müller,erika@example.test,Liebe
                Peter,Schmidt,peter@example.test,Lieber
                """;
        VelocityMailTemplate templates = templates("<p>${salutation} ${firstName}</p>",
                "${salutation} ${firstName}");
        var recipients = new CsvParser(new StringReader(csv)).parse();

        MailTemplate erika = templates.render(recipients.get(0));
        MailTemplate peter = templates.render(recipients.get(1));

        assertThat(erika.getHtml()).isEqualTo("<p>Liebe Erika</p>");
        assertThat(erika.getPlaintext()).isEqualTo("Liebe Erika");
        assertThat(peter.getHtml()).isEqualTo("<p>Lieber Peter</p>");
        assertThat(peter.getPlaintext()).isEqualTo("Lieber Peter");
    }

    @Test
    void escapesSalutationOnlyInHtml() throws Exception {
        VelocityMailTemplate templates = templates("<p>${salutation} ${firstName}</p>",
                "${salutation} ${firstName}");
        Mailing recipient = Mailing.builder().salutation("Liebe <Freundin> & Kollegin").firstname("Erika").build();

        MailTemplate result = templates.render(recipient);

        assertThat(result.getHtml()).isEqualTo("<p>Liebe &lt;Freundin&gt; &amp; Kollegin Erika</p>");
        assertThat(result.getPlaintext()).isEqualTo("Liebe <Freundin> & Kollegin Erika");
    }

    @Test
    void failsToLoadMissingTemplates() throws Exception {
        MailConfig config = configuration();
        Files.writeString(directory.resolve("custom.html"), "<p>Hello</p>");

        assertThatThrownBy(() -> new VelocityMailTemplate(config))
                .isInstanceOf(IOException.class).hasMessageContaining(directory.toString());
    }

    @Test
    void failsToLoadMalformedTemplates() {
        assertThatThrownBy(() -> templates("#if($firstname)unterminated", "Hello"))
                .isInstanceOf(IOException.class);
    }

    @Test
    void rejectsUnknownPlaceholdersRatherThanSendingUnexpandedReferences() throws Exception {
        VelocityMailTemplate templates = templates("<p>${unknownField}</p>", "Hello");

        assertThatThrownBy(() -> templates.render(Mailing.builder().build()))
                .isInstanceOf(VelocityException.class);
    }

    private VelocityMailTemplate templates(String html, String plaintext) throws IOException {
        Files.writeString(directory.resolve("custom.html"), html);
        Files.writeString(directory.resolve("custom.txt"), plaintext);
        return new VelocityMailTemplate(configuration());
    }

    private MailConfig configuration() {
        MailConfig config = mock(MailConfig.class);
        when(config.getTemplatePath()).thenReturn(directory.toString());
        when(config.getHtmlTemplate()).thenReturn("custom.html");
        when(config.getPlaintextTemplate()).thenReturn("custom.txt");
        return config;
    }
}
