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
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.net.URL;
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
        assertThat(message.isMimeType("text/plain")).isTrue();
        assertThat(message.getContentType()).containsIgnoringCase("charset=UTF-8");
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
        Part text = onlyPartOfType(parts, "text/plain");
        Part html = onlyPartOfType(parts, "text/html");
        Part image = onlyPartOfType(parts, "image/png");
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
        // GreenMail 1.6.x deliberately has no STARTTLS support on its SMTP listener.
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

    private SendOut newSendOut() throws EmailException {
        SimpleEmail text = new SimpleEmail();
        HtmlEmail html = new HtmlEmail();
        SendOut sendOut = new SendOut(MAILING, config, text, html);
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
        properties.setProperty("mail.smtp.connectiontimeout", "5000");
        properties.setProperty("mail.smtp.timeout", "5000");
        properties.setProperty("mail.smtp.writetimeout", "5000");
        if (!email.isSSLOnConnect()) {
            assertThat(properties.getProperty("mail.smtp.starttls.enable")).isEqualTo("true");
            assertThat(properties.getProperty("mail.smtp.starttls.required")).isEqualTo("true");
        }
        assertThat(properties.getProperty("mail.smtp.ssl.checkserveridentity")).isEqualTo("true");
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
