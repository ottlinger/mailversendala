package de.aikiit.mailversendala;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 07.06.17.
 */
public class MailingResultTest {

    @Test
    public void errorCounterIsZeroIfWithoutInteraction() {
        assertThat(new MailingResult().getErrorCounter()).isEmpty();
    }

    @Test
    public void successCounterIsZeroIfWithoutInteraction() {
        assertThat(new MailingResult().getMailCounter()).isEmpty();
    }

    @Test
    public void incrementErrorCounter() {
        final MailingResult result = new MailingResult();
        result.addError();
        result.addError();
        result.addError();
        result.addError();
        assertThat(result.getErrorCounter().get().get()).isEqualTo(4);
    }
}
