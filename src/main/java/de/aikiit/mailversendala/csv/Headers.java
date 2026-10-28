package de.aikiit.mailversendala.csv;

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


import de.aikiit.mailversendala.MailConfig;

/**
 * Contains the required CSV headers.
 *
 * @see MailConfig
 */
public interface Headers {
    /**
     * Firstname in the mailing.
     */
    String FIRSTNAME = "firstname";
    /**
     * Surname in the mailing.
     */
    String SURNAME = "surname";
    /**
     * Required, non-blank email address used to send the mailing to.
     */
    String EMAIL = "email";
    /**
     * Greeting prefix, for example "Liebe" or "Lieber".
     */
    String SALUTATION = "salutation";
}
