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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.ParseError;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Checks rendered HTML without changing the outgoing markup or fetching resources. */
public final class HtmlMarkupValidator {
    private static final int MAX_ERRORS = 20;

    private HtmlMarkupValidator() { }

    /**
     * Reject HTML parser errors and likely misspelled HTML tags before delivery.
     * Full documents and body fragments are accepted. Positions refer to rendered HTML.
     * @param html rendered HTML body.
     * @param source template filename and recipient for diagnostic messages.
     * @throws IOException if markup errors are found.
     */
    public static void validate(String html, String source) throws IOException {
        Parser parser = Parser.htmlParser().setTrackErrors(MAX_ERRORS).setTrackPosition(true);
        Document document = parser.parseInput(html, "");
        List<String> errors = new ArrayList<>();
        for (ParseError error : parser.getErrors()) {
            errors.add("line/column " + error.getCursorPos() + ": " + error.getErrorMessage());
        }
        for (Element element : document.getAllElements()) {
            String tag = element.normalName();
            // Jsoup accepts unknown tags silently. Keep custom elements and namespaced
            // mail/SVG markup, but reject ordinary misspellings such as <spna>.
            if (!(element instanceof Document) && Parser.NamespaceHtml.equals(element.tag().namespace())
                    && !element.tag().isKnownTag() && !tag.contains("-") && !tag.contains(":")) {
                if (errors.size() == MAX_ERRORS) {
                    break;
                }
                errors.add("line/column " + element.sourceRange().start().lineNumber() + ":"
                        + element.sourceRange().start().columnNumber() + ": Unknown HTML tag <" + tag + ">");
            }
        }
        if (!errors.isEmpty()) {
            throw new IOException("Invalid HTML markup in " + source + " (up to " + MAX_ERRORS
                    + " errors shown):\n" + String.join("\n", errors));
        }
    }
}
