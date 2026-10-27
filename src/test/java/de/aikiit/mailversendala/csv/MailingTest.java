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


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 25.05.17.
 */
public class MailingTest {
    private static final String EMAIL = "foo@bar.uk";
    private static final String NAME = "FooBarUK";
    private static final String FIRSTNAME = "Deforest";

    @Test
    public void objectCreationWithBuilder() {
        Mailing mailing = Mailing.builder().email(EMAIL).surname(NAME).firstname(FIRSTNAME).build();

        assertThat(mailing).isNotNull();
        assertThat(mailing.toString()).isNotNull();

        assertThat(mailing.getEmail()).isEqualTo(EMAIL);
        assertThat(mailing.getSurname()).isEqualTo(NAME);
        assertThat(mailing.getFirstname()).isEqualTo(FIRSTNAME);
    }

    @Test
    public void objectCreationWithoutBuilder() {
        Mailing mailing = new Mailing(FIRSTNAME, NAME, EMAIL);

        assertThat(mailing).isNotNull();
        assertThat(mailing.toString()).isNotNull();

        assertThat(mailing.getEmail()).isEqualTo(EMAIL);
        assertThat(mailing.getSurname()).isEqualTo(NAME);
        assertThat(mailing.getFirstname()).isEqualTo(FIRSTNAME);
    }

}
