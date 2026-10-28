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
package de.aikiit.mailversendala;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import de.aikiit.mailversendala.csv.Mailing;
import de.aikiit.mailversendala.csv.CsvParser;
import de.aikiit.mailversendala.template.VelocityMailTemplate;
import de.aikiit.mailversendala.template.MailResources;
import de.aikiit.mailversendala.template.FileMailTemplate;
import org.jsoup.Jsoup;
import org.apache.commons.mail2.jakarta.Email;
import org.apache.commons.mail2.core.EmailConstants;
import org.apache.commons.mail2.core.EmailException;
import org.apache.commons.mail2.jakarta.HtmlEmail;
import org.apache.commons.mail2.jakarta.SimpleEmail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Local mail delivery tests. GreenMail loads the test-only {@code /greenmail.p12}
 * from the classpath, with its default password {@code changeit}. This fixture
 * contains a self-signed localhost certificate and is not a production credential.
 * To regenerate it from the project root:
 * <pre>
 * keytool -genkeypair -alias greenmail -keyalg RSA -keysize 2048 -storetype PKCS12
 *   -keystore src/test/resources/greenmail.p12 -storepass changeit -keypass changeit
 *   -dname "CN=localhost, OU=Tests, O=mailversendala"
 *   -ext "SAN=dns:localhost,ip:127.0.0.1" -startdate "2020/01/01 00:00:00" -validity 36500
 * </pre>
 */
class SendOutGreenMailTest {

    private static final String APPLICATION_PDF = "application/pdf";
    private static final String MULTIPART_MIXED = "multipart/mixed";
    private static final String IMAGE_PNG = "image/png";

    private static final String RECIPIENT = "recipient@example.test";
    private static final String FROM = "sender@example.test";
    private static final String USERNAME = "smtp-user";
    private static final String PASSWORD = "smtp-password";
    private static final String SUBJECT = "GreenMail integration";
    private static final Mailing MAILING = Mailing.builder()
            .email(RECIPIENT).firstname("Ada").surname("Lovelace").build();
    private static final URL IMAGE = SendOutGreenMailTest.class
            .getResource("/template/img/monkey-toy-16-118729.png");

    private GreenMail greenMail;
    private MailConfig config;

    @BeforeEach
    void startMailServer() {
        greenMail = new GreenMail(new ServerSetup(0, "127.0.0.1", ServerSetup.PROTOCOL_SMTPS));
        greenMail.setUser(RECIPIENT, USERNAME, PASSWORD);
        greenMail.start();

        config = mock(MailConfig.class);
        when(config.getHost()).thenReturn("localhost");
        // Exercise the production SMTPS configuration; only the test clients' SSL port is overridden.
        when(config.getPort()).thenReturn(465);
        when(config.getUsername()).thenReturn(USERNAME);
        when(config.getPassword()).thenReturn(PASSWORD);
        when(config.getFrom()).thenReturn(FROM);
        when(config.getSubject()).thenReturn(SUBJECT);
        when(config.sendOutMails()).thenReturn(true);
    }

    @AfterEach
    void stopMailServer() {
        if (greenMail != null) {
            greenMail.stop();
        }
    }

    @Test
    void deliversPlainTextOverAuthenticatedSmtps() throws Exception {
        newSendOut().send();

        MimeMessage message = receivedMessage();
        assertAddressesAndHeaders(message);
        assertThat(message.getSubject()).startsWith(SUBJECT + " ");
        assertThat(message.isMimeType(EmailConstants.TEXT_PLAIN)).isTrue();
        assertThat(message.getContentType()).containsIgnoringCase("charset=" + StandardCharsets.UTF_8.name());
        assertThat(message.getContent().toString())
                .contains("This is a test mail from Mailversendala... :-)");
    }

    @Test
    void deliversHtmlWithTextAlternativeAndInlineImage() throws Exception {
        newSendOut().sendComplex(IMAGE);

        MimeMessage message = receivedMessage();
        assertAddressesAndHeaders(message);
        assertThat(message.getSubject()).startsWith("HTML" + SUBJECT + " ");
        assertThat(((InternetAddress) message.getRecipients(Message.RecipientType.TO)[0]).getPersonal())
                .isEqualTo("Ada Lovelace");
        assertThat(message.isMimeType("multipart/*")).isTrue();

        List<Part> parts = new ArrayList<>();
        collectParts(message, parts);
        Part text = onlyPartOfType(parts, EmailConstants.TEXT_PLAIN);
        Part html = onlyPartOfType(parts, EmailConstants.TEXT_HTML);
        Part image = onlyPartOfType(parts, IMAGE_PNG);
        assertThat(text.getContent().toString())
                .contains("Your email client does not support HTML messages - thus no ASF logo visible.");
        String contentId = image.getHeader("Content-ID")[0];
        assertThat(contentId).startsWith("<").endsWith(">");
        assertThat(html.getContent().toString())
                .contains("cid:" + contentId.substring(1, contentId.length() - 1));
        assertThat(image.getDisposition()).isEqualToIgnoringCase(Part.INLINE);
        try (InputStream receivedImage = image.getInputStream(); InputStream fixture = IMAGE.openStream()) {
            assertThat(receivedImage.readAllBytes()).isEqualTo(fixture.readAllBytes());
        }
    }

    @Test
    void demoModeDoesNotDeliverEitherMessage() throws Exception {
        when(config.sendOutMails()).thenReturn(false);
        SendOut sendOut = newSendOut();

        sendOut.send();
        sendOut.sendComplex(IMAGE);

        assertThat(greenMail.getReceivedMessages()).isEmpty();
    }

    @Test
    void rejectsInvalidSmtpCredentials() throws Exception {
        when(config.getPassword()).thenReturn("incorrect-password");
        SendOut sendOut = newSendOut();

        assertThatThrownBy(sendOut::send)
                .isInstanceOf(EmailException.class)
                .hasRootCauseInstanceOf(AuthenticationFailedException.class);
        assertThat(greenMail.getReceivedMessages()).isEmpty();
    }

    @Test
    void refusesPlaintextFallbackWhenServerDoesNotSupportStartTls() throws Exception {
        // GreenMail's SMTP listener does not advertise STARTTLS.
        GreenMail smtpOnly = new GreenMail(new ServerSetup(0, "127.0.0.1", ServerSetup.PROTOCOL_SMTP));
        try {
            smtpOnly.setUser(RECIPIENT, USERNAME, PASSWORD);
            smtpOnly.start();
            when(config.getPort()).thenReturn(smtpOnly.getSmtp().getPort());
            SendOut sendOut = newSendOut();

            assertThatThrownBy(sendOut::send)
                    .isInstanceOf(EmailException.class)
                    .hasStackTraceContaining("STARTTLS is required but host does not support STARTTLS");
            assertThat(smtpOnly.getReceivedMessages()).isEmpty();
        } finally {
            smtpOnly.stop();
        }
    }

    @Test
    void deliversExternalVelocityTemplatesAsUtf8Alternatives(@TempDir Path directory) throws Exception {
        VelocityMailTemplate templates = externalTemplates(directory);
        when(config.getSubject()).thenReturn("Einladung für Jörg");
        Mailing recipient = Mailing.builder().email(RECIPIENT)
                .firstname("Jörg <Admin>").surname("Smith & Jones").build();

        newSendOut(recipient).send(templates.render(recipient));

        MimeMessage message = receivedMessage();
        assertAddressesAndHeaders(message);
        assertThat(message.getSubject()).isEqualTo("Einladung für Jörg");
        List<Part> parts = new ArrayList<>();
        collectParts(message, parts);
        assertThat(parts).hasSize(2);
        Part html = onlyPartOfType(parts, EmailConstants.TEXT_HTML);
        Part text = onlyPartOfType(parts, EmailConstants.TEXT_PLAIN);
        assertThat(html.getContentType()).containsIgnoringCase("charset=" + StandardCharsets.UTF_8.name());
        assertThat(text.getContentType()).containsIgnoringCase("charset=" + StandardCharsets.UTF_8.name());
        assertThat(html.getContent().toString())
                .contains("<p>Hello J&ouml;rg &lt;Admin&gt; Smith &amp; Jones</p>");
        assertThat(text.getContent().toString()).contains("Hello Jörg <Admin> Smith & Jones");
    }

    @Test
    void deliversSeparatelyPersonalizedMessagesFromCompiledTemplates(@TempDir Path directory) throws Exception {
        VelocityMailTemplate templates = externalTemplates(directory);
        Mailing second = Mailing.builder().email(RECIPIENT).firstname("Grace").surname("Hopper").build();

        newSendOut().send(templates.render(MAILING));
        newSendOut(second).send(templates.render(second));

        assertThat(greenMail.waitForIncomingEmail(5000, 2)).isTrue();
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(2);
        List<Part> firstParts = new ArrayList<>();
        List<Part> secondParts = new ArrayList<>();
        collectParts(messages[0], firstParts);
        collectParts(messages[1], secondParts);
        assertThat(onlyPartOfType(firstParts, EmailConstants.TEXT_PLAIN).getContent().toString())
                .contains("Ada Lovelace").doesNotContain("Grace Hopper");
        assertThat(onlyPartOfType(secondParts, EmailConstants.TEXT_HTML).getContent().toString())
                .contains("Grace Hopper").doesNotContain("Ada Lovelace");
    }

    @Test
    void deliversPersonalizedSalutationsFromCsv(@TempDir Path directory) throws Exception {
        Files.writeString(directory.resolve("greeting.html"), "<p>${salutation} ${firstName}</p>");
        Files.writeString(directory.resolve("greeting.txt"), "${salutation} ${firstName}");
        when(config.getTemplatePath()).thenReturn(directory.toString());
        when(config.getHtmlTemplate()).thenReturn("greeting.html");
        when(config.getPlaintextTemplate()).thenReturn("greeting.txt");
        VelocityMailTemplate templates = new VelocityMailTemplate(config);
        String csv = "firstname,surname,email,salutation\n"
                + "Erika,Müller," + RECIPIENT + ",Liebe\n"
                + "Peter,Schmidt," + RECIPIENT + ",Lieber\n";

        for (Mailing recipient : new CsvParser(new StringReader(csv)).parse()) {
            newSendOut(recipient).send(templates.render(recipient));
        }

        assertThat(greenMail.waitForIncomingEmail(5000, 2)).isTrue();
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertThat(messages).hasSize(2);
        String[] greetings = {"Liebe Erika", "Lieber Peter"};
        for (int i = 0; i < messages.length; i++) {
            List<Part> parts = new ArrayList<>();
            collectParts(messages[i], parts);
            assertThat(onlyPartOfType(parts, EmailConstants.TEXT_HTML).getContent().toString())
                    .isEqualTo("<p>" + greetings[i] + "</p>");
            assertThat(onlyPartOfType(parts, EmailConstants.TEXT_PLAIN).getContent().toString())
                    .isEqualTo(greetings[i]);
        }
    }

    @Test
    void demoModeDoesNotDeliverRenderedTemplates(@TempDir Path directory) throws Exception {
        when(config.sendOutMails()).thenReturn(false);
        VelocityMailTemplate templates = externalTemplates(directory);

        newSendOut().send(templates.render(MAILING));

        assertThat(greenMail.getReceivedMessages()).isEmpty();
    }

    @Test
    void embedsLocalImagesAndDeliversPdfAndImageAttachments(@TempDir Path directory) throws Exception {
        copyAssets(directory);
        externalTemplates(directory);
        Files.writeString(directory.resolve("with-images.html"),
                "<p>Hello ${firstName}</p><IMG SRC=images/logo.png><img src='./images/logo.png'>");
        when(config.getHtmlTemplate()).thenReturn("with-images.html");
        when(config.getAttachments()).thenReturn("attachments/information.pdf, images/logo.png, attachments/information.pdf");
        VelocityMailTemplate templates = new VelocityMailTemplate(config);

        newSendOut().send(templates.render(MAILING));

        MimeMessage message = receivedMessage();
        assertAddressesAndHeaders(message);
        assertThat(message.isMimeType(MULTIPART_MIXED)).isTrue();
        List<Part> parts = new ArrayList<>();
        collectParts(message, parts);
        assertThat(parts).hasSize(5);
        Part pdf = onlyPartOfType(parts, APPLICATION_PDF);
        assertThat(pdf.getDisposition()).isEqualToIgnoringCase(Part.ATTACHMENT);
        assertThat(pdf.getFileName()).isEqualTo("information.pdf");
        assertPartBytes(pdf, Files.readAllBytes(directory.resolve("attachments/information.pdf")));
        Part inline = null;
        Part attachedImage = null;
        for (Part part : parts) {
            if (part.isMimeType(IMAGE_PNG)) {
                if (Part.INLINE.equalsIgnoreCase(part.getDisposition())) {
                    inline = part;
                } else {
                    attachedImage = part;
                }
            }
        }
        assertThat(inline).isNotNull();
        assertThat(attachedImage).isNotNull();
        assertThat(attachedImage.getDisposition()).isEqualToIgnoringCase(Part.ATTACHMENT);
        assertThat(attachedImage.getFileName()).isEqualTo("logo.png");
        byte[] imageBytes = Files.readAllBytes(directory.resolve("images/logo.png"));
        assertPartBytes(inline, imageBytes);
        assertPartBytes(attachedImage, imageBytes);
        String header = inline.getHeader("Content-ID")[0];
        String cid = "cid:" + header.substring(1, header.length() - 1);
        assertThat(Jsoup.parse(onlyPartOfType(parts, EmailConstants.TEXT_HTML).getContent().toString()).select("img"))
                .extracting(image -> image.attr("src")).containsExactly(cid, cid);
        assertThat(onlyPartOfType(parts, EmailConstants.TEXT_PLAIN).getContent().toString()).contains("Hello Ada Lovelace");
    }

    @Test
    void sendsValidatedSnapshotsEvenIfOriginalFilesAreDeleted(@TempDir Path directory) throws Exception {
        copyAssets(directory);
        when(config.getTemplatePath()).thenReturn(directory.toString());
        when(config.getAttachments()).thenReturn("attachments/information.pdf");
        FileMailTemplate template = new FileMailTemplate("<img src='images/logo.png'>", "Hello Ada");
        MailResources resources = new MailResources(config);
        resources.validate(template);
        byte[] pdfBytes = Files.readAllBytes(directory.resolve("attachments/information.pdf"));
        Files.delete(directory.resolve("images/logo.png"));
        Files.delete(directory.resolve("attachments/information.pdf"));

        newSendOut().send(template, resources);

        List<Part> parts = new ArrayList<>();
        collectParts(receivedMessage(), parts);
        assertPartBytes(onlyPartOfType(parts, APPLICATION_PDF), pdfBytes);
        assertThat(onlyPartOfType(parts, IMAGE_PNG).getDisposition()).isEqualToIgnoringCase(Part.INLINE);
    }

    @Test
    void demoModeValidatesResourcesWithoutSending(@TempDir Path directory) throws Exception {
        copyAssets(directory);
        when(config.getTemplatePath()).thenReturn(directory.toString());
        when(config.getAttachments()).thenReturn("attachments/information.pdf");
        when(config.sendOutMails()).thenReturn(false);

        newSendOut().send(new FileMailTemplate("<img src='images/logo.png'>", "Hello Ada"));

        assertThat(greenMail.getReceivedMessages()).isEmpty();
    }

    private void copyAssets(Path directory) throws Exception {
        Files.createDirectories(directory.resolve("images"));
        Files.createDirectories(directory.resolve("attachments"));
        try (InputStream input = IMAGE.openStream()) {
            Files.copy(input, directory.resolve("images/logo.png"));
        }
        try (InputStream input = getClass().getResourceAsStream("/template/attachments/information.pdf")) {
            Files.copy(input, directory.resolve("attachments/information.pdf"));
        }
    }

    private void assertPartBytes(Part part, byte[] expected) throws Exception {
        try (InputStream input = part.getInputStream()) {
            assertThat(input.readAllBytes()).isEqualTo(expected);
        }
    }

    private VelocityMailTemplate externalTemplates(Path directory) throws Exception {
        Files.writeString(directory.resolve("welcome.html"), "<p>Hello ${firstName} ${lastName}</p>");
        Files.writeString(directory.resolve("welcome.txt"), "Hello ${firstName} ${lastName}");
        when(config.getTemplatePath()).thenReturn(directory.toString());
        when(config.getHtmlTemplate()).thenReturn("welcome.html");
        when(config.getPlaintextTemplate()).thenReturn("welcome.txt");
        return new VelocityMailTemplate(config);
    }

    private SendOut newSendOut() throws EmailException {
        return newSendOut(MAILING);
    }

    private SendOut newSendOut(Mailing recipient) throws EmailException {
        SimpleEmail text = new SimpleEmail();
        HtmlEmail html = new HtmlEmail();
        SendOut sendOut = new SendOut(recipient, config, text, html);
        configureTestSession(text);
        configureTestSession(html);
        return sendOut;
    }

    private void configureTestSession(Email email) throws EmailException {
        if (email.isSSLOnConnect()) {
            email.setSslSmtpPort(Integer.toString(greenMail.getSmtps().getPort()));
        }
        Properties properties = email.getMailSession().getProperties();
        // Trust only this test server's self-signed certificate, in this session only.
        // Production TLS settings and server identity verification are unchanged.
        properties.setProperty("mail.smtp.ssl.trust", "localhost");
        properties.setProperty(EmailConstants.MAIL_SMTP_CONNECTIONTIMEOUT, "5000");
        properties.setProperty(EmailConstants.MAIL_SMTP_TIMEOUT, "5000");
        properties.setProperty("mail.smtp.writetimeout", "5000");
        if (!email.isSSLOnConnect()) {
            assertThat(properties.getProperty(EmailConstants.MAIL_TRANSPORT_STARTTLS_ENABLE)).isEqualTo("true");
            assertThat(properties.getProperty(EmailConstants.MAIL_TRANSPORT_STARTTLS_REQUIRED)).isEqualTo("true");
        }
        assertThat(properties.getProperty(EmailConstants.MAIL_SMTP_SSL_CHECKSERVERIDENTITY)).isEqualTo("true");
    }

    private MimeMessage receivedMessage() {
        assertThat(greenMail.waitForIncomingEmail(5000, 1)).isTrue();
        assertThat(greenMail.getReceivedMessages()).hasSize(1);
        return greenMail.getReceivedMessages()[0];
    }

    private void assertAddressesAndHeaders(MimeMessage message) throws Exception {
        assertThat(((InternetAddress) message.getFrom()[0]).getAddress()).isEqualTo(FROM);
        assertThat(message.getAllRecipients()).hasSize(1);
        assertThat(((InternetAddress) message.getAllRecipients()[0]).getAddress()).isEqualTo(RECIPIENT);
        assertThat(message.getHeader("X-Mailer", null))
                .isEqualTo("Mailversendala - https://github.com/ottlinger/mailversendala");
    }

    private void collectParts(Part part, List<Part> parts) throws Exception {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            for (int i = 0; i < multipart.getCount(); i++) {
                collectParts(multipart.getBodyPart(i), parts);
            }
        } else {
            parts.add(part);
        }
    }

    private Part onlyPartOfType(List<Part> parts, String mimeType) throws Exception {
        List<Part> matching = new ArrayList<>();
        for (Part part : parts) {
            if (part.isMimeType(mimeType)) {
                matching.add(part);
            }
        }
        assertThat(matching).hasSize(1);
        return matching.get(0);
    }
}
