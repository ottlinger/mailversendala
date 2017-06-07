package de.aikiit.mailversendala;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hirsch on 06.06.17.
 */
public class MailingResult {

    private AtomicInteger errorCounter;
    private AtomicInteger mailCounter;

    public void addError() {
        if(errorCounter == null) {
            this.errorCounter = new AtomicInteger(0);
        }
        errorCounter.incrementAndGet();
    }

    public Optional<AtomicInteger> getErrorCounter() {
        return Optional.ofNullable(errorCounter);
    }

    public Optional<AtomicInteger> getMailCounter() {
        return Optional.ofNullable(mailCounter);
    }
}
