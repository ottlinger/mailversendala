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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HtmlMarkupValidatorTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "<p>Hello</p>",
            "<!doctype html><html><head><title>Mail</title></head><body><p>Hello</p></body></html>",
            "<html><body><p>Hello</p></body></html>",
            "<p>Hello<br><img src='logo.png'></p>",
            "<ul><li>One<li>Two</ul>",
            "<p>${firstName} ${unknownField}</p>",
            "<!--[if mso]><v:rect></v:rect><![endif]--><p>Hello</p>",
            "<custom-element>Hello</custom-element>",
            "<svg><path d='M0 0'/></svg>"
    })
    void acceptsValidHtmlDocumentsAndFragments(String html) {
        assertThatCode(() -> HtmlMarkupValidator.validate(html, "welcome.html")).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "<p>Hello</pp>",
            "<div><span>Hello</div>",
            "<div><span>Hello",
            "<p title='broken>Hello</p>",
            "<p title='one' title='two'>Hello</p>",
            "<spna>Hello</spna>"
    })
    void rejectsMarkupTyposWithFilenameAndPosition(String html) {
        assertThatThrownBy(() -> HtmlMarkupValidator.validate("\n" + html, "welcome.html"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Invalid HTML markup in welcome.html")
                .hasMessageContaining("line/column 2:");
    }

    @Test
    void identifiesMisspelledTagsThatJsoupWouldOtherwiseAccept() {
        assertThatThrownBy(() -> HtmlMarkupValidator.validate("<p>Hello</p>\n<spna>World</spna>", "welcome.html"))
                .hasMessageContaining("line/column 2:1: Unknown HTML tag <spna>");
    }

    @Test
    void collectsMultipleErrors() {
        assertThatThrownBy(() -> HtmlMarkupValidator.validate("<spna>One</spna>\n<dvi>Two</dvi>", "welcome.html"))
                .hasMessageContaining("Unknown HTML tag <spna>")
                .hasMessageContaining("Unknown HTML tag <dvi>");
    }
}
