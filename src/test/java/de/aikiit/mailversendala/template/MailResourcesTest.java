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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MailResourcesTest {
    @TempDir
    Path directory;

    @ParameterizedTest
    @ValueSource(strings = {"https://example.test/logo.png", "http://127.0.0.1/logo.png",
            "file:///etc/passwd", "//example.test/logo.png", "data:image/png;base64,AAAA",
            "cid:unresolved", "/etc/passwd", "../outside.png", "%2e%2e/outside.png",
            "image.png?download=true", "image.png#fragment", "..\\outside.png", ""})
    void rejectsUnsafeInlineReferences(String reference) throws Exception {
        MailResources resources = resources("");
        FileMailTemplate template = new FileMailTemplate("<img src=\"" + reference + "\">", "Hello");

        assertThatThrownBy(() -> resources.validate(template)).isInstanceOf(IOException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://example.test/document.pdf", "file:///etc/passwd",
            "/etc/passwd", "../outside.pdf", "%2e%2e/outside.pdf", "missing.pdf"})
    void rejectsUnsafeOrMissingAttachments(String reference) {
        assertThatThrownBy(() -> resources(reference)).isInstanceOf(IOException.class);
    }

    @Test
    void acceptsNestedImagesWithSpacesEntitiesAndUnquotedAttributes() throws Exception {
        writeImage("images/logo & name.png");
        writeImage("images/plain.png");
        MailResources resources = resources("");

        assertThatCode(() -> resources.validate(new FileMailTemplate(
                "<IMG SRC=\"images/logo%20%26%20name.png\"><img src='images/logo &amp; name.png'>"
                        + "<img src=images/plain.png>", "Hello"))).doesNotThrowAnyException();
    }

    @Test
    void rejectsMissingImagesAndDirectories() throws Exception {
        MailResources resources = resources("");
        Files.createDirectory(directory.resolve("images"));

        assertThatThrownBy(() -> resources.validate(new FileMailTemplate("<img src='missing.png'>", "Hello")))
                .isInstanceOf(IOException.class);
        assertThatThrownBy(() -> resources.validate(new FileMailTemplate("<img src='images'>", "Hello")))
                .isInstanceOf(IOException.class);
    }

    @Test
    void rejectsNonImageFilesAndSrcset() throws Exception {
        Files.writeString(directory.resolve("document.txt"), "not an image");
        MailResources resources = resources("");

        assertThatThrownBy(() -> resources.validate(new FileMailTemplate("<img src='document.txt'>", "Hello")))
                .isInstanceOf(IOException.class).hasMessageContaining("not an image");
        assertThatThrownBy(() -> resources.validate(new FileMailTemplate("<img srcset='logo.png 1x'>", "Hello")))
                .isInstanceOf(IOException.class).hasMessageContaining("srcset");
    }

    @Test
    void rejectsTraversalToExistingFilesOutsideTheTemplateDirectory(@TempDir Path outside) throws Exception {
        Path target = Files.writeString(outside.resolve("secret.png"), "outside data");
        String traversal = directory.relativize(target).toString().replace('\\', '/');
        MailResources resources = resources("");

        assertThatThrownBy(() -> resources.validate(new FileMailTemplate("<img src='" + traversal + "'>", "Hello")))
                .isInstanceOf(IOException.class).hasMessageContaining("inside templatepath");
        assertThatThrownBy(() -> resources(traversal.replace("..", "%2e%2e")))
                .isInstanceOf(IOException.class).hasMessageContaining("inside templatepath");
    }

    @Test
    void rejectsSymlinksEscapingTheTemplateDirectory(@TempDir Path outside) throws Exception {
        Path target = Files.writeString(outside.resolve("secret.png"), "outside data");
        try {
            Files.createSymbolicLink(directory.resolve("link.png"), target);
        } catch (IOException | UnsupportedOperationException | SecurityException e) {
            assumeTrue(false, "Symbolic links unavailable: " + e.getMessage());
        }
        MailResources resources = resources("");

        assertThatThrownBy(() -> resources.validate(new FileMailTemplate("<img src='link.png'>", "Hello")))
                .isInstanceOf(IOException.class).hasMessageContaining("inside templatepath");
        assertThatThrownBy(() -> resources("link.png"))
                .isInstanceOf(IOException.class).hasMessageContaining("inside templatepath");
    }

    private MailResources resources(String attachments) throws IOException {
        MailConfig config = mock(MailConfig.class);
        when(config.getTemplatePath()).thenReturn(directory.toString());
        when(config.getAttachments()).thenReturn(attachments);
        return new MailResources(config);
    }

    private void writeImage(String name) throws IOException {
        Path file = directory.resolve(name);
        Files.createDirectories(file.getParent());
        try (InputStream input = getClass().getResourceAsStream("/template/img/monkey-toy-16-118729.png")) {
            Files.copy(input, file);
        }
    }
}
