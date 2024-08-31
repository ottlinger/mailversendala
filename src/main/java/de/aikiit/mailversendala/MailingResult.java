package de.aikiit.mailversendala;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class encapsulates internal counters to gauge
 * <li>how many errors and</li>
 * <li>how many mails were successfully sent out</li>
 * during an application run.
 */
public class MailingResult {

    private AtomicInteger errorCounter;
    private AtomicInteger mailCounter;

    /**
     * Increment internal error counter by 1.
     */
    public void addError() {
        if (errorCounter == null) {
            this.errorCounter = new AtomicInteger(0);
        }
        errorCounter.incrementAndGet();
    }

    /**
     * Increment internal success counter by 1.
     */
    public void addSuccess() {
        if (mailCounter == null) {
            this.mailCounter = new AtomicInteger(0);
        }
        mailCounter.incrementAndGet();
    }

    /**
     * @return current error counter if existing.
     */
    public Optional<AtomicInteger> getErrorCounter() {
        return Optional.ofNullable(errorCounter);
    }

    /**
     * @return current success counter if existing.
     */
    public Optional<AtomicInteger> getMailCounter() {
        return Optional.ofNullable(mailCounter);
    }

    /**
     * @return sum of {@link #mailCounter} and {@link #errorCounter}.
     */
    public int getTotal() {
        return getErrorCounter().orElse(new AtomicInteger(0)).get() + getMailCounter().orElse(new AtomicInteger(0)).get();
    }

}
