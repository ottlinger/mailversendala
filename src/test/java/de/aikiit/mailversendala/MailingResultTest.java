package de.aikiit.mailversendala;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by hirsch on 07.06.17.
 */
public class MailingResultTest {

    @Test
    public void errorCounterIsZeroIfWithoutInteraction() {
        MailingResult result = new MailingResult();
        assertThat(result.getErrorCounter()).isEmpty();
        assertThat(result.getTotal()).isEqualTo(0);
    }

    @Test
    public void successCounterIsZeroIfWithoutInteraction() {
        MailingResult result = new MailingResult();
        assertThat(result.getMailCounter()).isEmpty();
        assertThat(result.getTotal()).isEqualTo(0);
    }

    @Test
    public void incrementErrorCounter() {
        final MailingResult result = new MailingResult();
        result.addError();
        result.addError();
        result.addError();
        result.addError();
        assertThat(result.getErrorCounter()).isPresent();
        assertThat(result.getErrorCounter().get().get()).isEqualTo(4);
        assertThat(result.getTotal()).isEqualTo(4);
    }

    @Test
    public void incrementSuccessCounterAndTotal() {
        final MailingResult result = new MailingResult();
        result.addSuccess();
        result.addSuccess();
        result.addError();
        assertThat(result.getMailCounter()).isPresent();
        assertThat(result.getMailCounter().get().get()).isEqualTo(2);
        assertThat(result.getTotal()).isEqualTo(3);
    }

}
