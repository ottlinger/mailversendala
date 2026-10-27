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


import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class encapsulates internal counters to gauge
 * <ul>
 * <li>how many errors and</li>
 * <li>how many mails were successfully sent out</li>
 * </ul>
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
     * Returns error counter.
     * @return current error counter if existing.
     */
    public Optional<AtomicInteger> getErrorCounter() {
        return Optional.ofNullable(errorCounter);
    }

    /**
     * Returns success counter.
     * @return current success counter if existing.
     */
    public Optional<AtomicInteger> getMailCounter() {
        return Optional.ofNullable(mailCounter);
    }

    /**
     * Returns total counter.
     * @return sum of {@link #mailCounter} and {@link #errorCounter}.
     */
    public int getTotal() {
        return getErrorCounter().orElse(new AtomicInteger(0)).get() + getMailCounter().orElse(new AtomicInteger(0)).get();
    }

}
