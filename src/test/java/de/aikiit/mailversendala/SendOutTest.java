package de.aikiit.mailversendala;

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


import de.aikiit.mailversendala.csv.Mailing;
import org.apache.commons.mail.EmailException;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;

public class SendOutTest {

    private static final Mailing DUMMY_RECIPIENT = Mailing.builder().email("my@news.com").build();

    @Test
    public void initializeMailSenderProperly() {
        assertThat(new SendOut(DUMMY_RECIPIENT)).isNotNull();
    }

    @Test
    public void sendOutWithoutSendingInTestMode() throws EmailException {
        SendOut sendOut = new SendOut(DUMMY_RECIPIENT);
        assertThat(sendOut).isNotNull();
        sendOut.send();
    }

    @Test
    public void sendOutHtmlMessageWithoutSendingInTestMode() throws EmailException, MalformedURLException {
        SendOut sendOut = new SendOut(DUMMY_RECIPIENT);
        assertThat(sendOut).isNotNull();
        sendOut.sendComplex(getClass().getResource("/template/img/monkey-toy-16-118729.png"));
    }

    @Test
    public void whenSendOutLacksRuntimeParameterNothingHappens() throws Exception {
        SendOut.main("");
        SendOut.main((String[]) null);
        assertThat(Boolean.FALSE).isFalse();
    }
}
