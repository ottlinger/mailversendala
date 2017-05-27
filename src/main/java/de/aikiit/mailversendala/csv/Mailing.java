package de.aikiit.mailversendala.csv;

import lombok.Builder;
import lombok.Data;

/**
 * Created by hirsch on 24.05.17.
 */
@Builder
@Data(staticConstructor = "of")
public class Mailing {
    private String firstname;
    private String surname;
    private String email;
    private String language;
}
