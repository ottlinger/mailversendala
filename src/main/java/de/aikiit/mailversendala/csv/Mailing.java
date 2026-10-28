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


import lombok.Builder;

/**
 * Comprises a mailing recipient.
 * @param firstname  firstname in the mailing.
 * @param surname    surname in the mailing.
 * @param email      required, non-blank email address used to send the mailing to.
 * @param salutation greeting prefix, for example "Liebe" or "Lieber" in German.
 */
@Builder
public record Mailing(
        String firstname,
        String surname,
        String email,
        String salutation
) {}
