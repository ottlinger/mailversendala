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
    private static final String SALUTATION = "Dear";

    @Test
    public void objectCreationWithBuilder() {
        Mailing mailing = Mailing.builder().email(EMAIL).surname(NAME).firstname(FIRSTNAME)
                .salutation(SALUTATION).build();

        assertThat(mailing).isNotNull();
        assertThat(mailing.toString()).isNotNull();

        assertThat(mailing.email()).isEqualTo(EMAIL);
        assertThat(mailing.surname()).isEqualTo(NAME);
        assertThat(mailing.firstname()).isEqualTo(FIRSTNAME);
        assertThat(mailing.salutation()).isEqualTo(SALUTATION);
    }

    @Test
    public void objectCreationWithoutBuilder() {
        Mailing mailing = new Mailing(FIRSTNAME, NAME, EMAIL, SALUTATION);

        assertThat(mailing).isNotNull();
        assertThat(mailing.toString()).isNotNull();

        assertThat(mailing.email()).isEqualTo(EMAIL);
        assertThat(mailing.surname()).isEqualTo(NAME);
        assertThat(mailing.firstname()).isEqualTo(FIRSTNAME);
        assertThat(mailing.salutation()).isEqualTo(SALUTATION);
    }

    @Test
    void builderAndConstructorProduceEqualRecords() {
        Mailing constructed = new Mailing(FIRSTNAME, NAME, EMAIL, SALUTATION);
        Mailing built = Mailing.builder().firstname(FIRSTNAME).surname(NAME)
                .email(EMAIL).salutation(SALUTATION).build();

        assertThat(Mailing.class.isRecord()).isTrue();
        assertThat(built).isEqualTo(constructed).hasSameHashCodeAs(constructed);
        assertThat(built).isNotEqualTo(new Mailing(FIRSTNAME, NAME, "other@example.test", SALUTATION));
    }

    @Test
    void builderAllowsMissingValues() {
        Mailing mailing = Mailing.builder().build();

        assertThat(mailing).isEqualTo(new Mailing(null, null, null, null));
    }

}
