package de.aikiit.mailversendala.csv;

import lombok.Builder;
import lombok.Data;

/**
 * Comprises a mailing recipient.
 */
@Builder
@Data
public class Mailing {
    private String firstname;
    private String surname;
    private String email;
    private String language;
}
