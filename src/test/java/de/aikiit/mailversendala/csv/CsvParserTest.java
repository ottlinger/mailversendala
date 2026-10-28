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


import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 27.05.17.
 */
public class CsvParserTest {

    public static final String CSV_INPUT = """
            firstname,surname,email,salutation
            Me,Again,foo@bar.com,Dear
            Ich,wieder,foo@bar.de,Hallo
            я,то́же,foo@bar.ru,Привет
            """;

    @Test
    public void objectCreation() {
        assertThat(new CsvParser(null)).isNotNull();
    }

    @Test
    public void launchParsing() throws IOException {
        CsvParser parser = new CsvParser(null);
        assertThat(parser.parse()).isEmpty();
    }

    @Test
    public void parseRealCSVDataIntoMailingsWithUnicodeNames() throws IOException {
        CsvParser parser = new CsvParser(new StringReader(CSV_INPUT));
        var mailings = parser.parse();
        assertThat(mailings).hasSize(3);
        assertThat(mailings).extracting(Mailing::getSalutation).containsExactly("Dear", "Hallo", "Привет");
    }

    @Test
    void parsesSalutationByHeaderName() throws IOException {
        String csv = """
                salutation,email,surname,firstname
                Liebe,erika@example.test,Müller,Erika
                Lieber,peter@example.test,Schmidt,Peter
                Dear,ada@example.test,Lovelace,Ada
                """;

        var mailings = new CsvParser(new StringReader(csv)).parse();

        assertThat(mailings).extracting(Mailing::getSalutation).containsExactly("Liebe", "Lieber", "Dear");
        assertThat(mailings).extracting(Mailing::getFirstname).containsExactly("Erika", "Peter", "Ada");
        assertThat(mailings).extracting(Mailing::getSurname).containsExactly("Müller", "Schmidt", "Lovelace");
        assertThat(mailings).extracting(Mailing::getEmail)
                .containsExactly("erika@example.test", "peter@example.test", "ada@example.test");
    }

    @Test
    void skipsRowsWithoutRequiredEmailColumn() throws IOException {
        String csv = "firstname,surname,salutation\nErika,Müller,Liebe\n";

        assertThat(new CsvParser(new StringReader(csv)).parse()).isEmpty();
    }

    @Test
    void skipsRowsMissingTheEmailField() throws IOException {
        String csv = "firstname,surname,salutation,email\nErika,Müller,Liebe\n";

        assertThat(new CsvParser(new StringReader(csv)).parse()).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "\t", " \t "})
    void skipsBlankEmailsAndContinuesWithValidRecipients(String email) throws IOException {
        String csv = "firstname,surname,email,salutation\n"
                + "Erika,Müller,\"" + email + "\",Liebe\n"
                + "Peter,Schmidt,peter@example.test,Lieber\n";

        var mailings = new CsvParser(new StringReader(csv)).parse();

        assertThat(mailings).hasSize(1);
        assertThat(mailings.get(0).getEmail()).isEqualTo("peter@example.test");
        assertThat(mailings.get(0).getFirstname()).isEqualTo("Peter");
    }

    @Test
    void skipsRowsWithoutRequiredSalutationColumn() throws IOException {
        String csv = "firstname,surname,email\nErika,Müller,erika@example.test\n";

        assertThat(new CsvParser(new StringReader(csv)).parse()).isEmpty();
    }

    @Test
    void skipsRowsMissingTheSalutationField() throws IOException {
        String csv = "firstname,surname,email,salutation\nErika,Müller,erika@example.test\n";

        assertThat(new CsvParser(new StringReader(csv)).parse()).isEmpty();
    }
}
