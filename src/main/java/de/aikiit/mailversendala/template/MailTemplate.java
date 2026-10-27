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


/**
 * Contains the contents of a mail.
 */
public interface MailTemplate {
    /**
     * Default template file name for HTML.
     */
    String BASE_NAME_HTML = "mailtemplate.html";
    /**
     * Default template file name for plain text
     */
    String BASE_NAME_PLAINTEXT = "mailtemplate.txt";

    /**
     * Returns HTML contents.
     * @return mailing content in format HTML.
     */
    String getHtml();

    /**
     * Returns plaintext contents.
     * @return mailing content in plaintext format.
     */
    String getPlaintext();
}
