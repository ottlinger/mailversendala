package de.aikiit.mailversendala;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class MailingResult {

    private AtomicInteger errorCounter;
    private AtomicInteger mailCounter;

    public void addError() {
        if (errorCounter == null) {
            this.errorCounter = new AtomicInteger(0);
        }
        errorCounter.incrementAndGet();
    }

    public void addSuccess() {
        if (mailCounter == null) {
            this.mailCounter = new AtomicInteger(0);
        }
        mailCounter.incrementAndGet();
    }

    public Optional<AtomicInteger> getErrorCounter() {
        return Optional.ofNullable(errorCounter);
    }

    public Optional<AtomicInteger> getMailCounter() {
        return Optional.ofNullable(mailCounter);
    }

    public int getTotal() {
        return getErrorCounter().orElse(new AtomicInteger(0)).get() + getMailCounter().orElse(new AtomicInteger(0)).get();
    }

}
