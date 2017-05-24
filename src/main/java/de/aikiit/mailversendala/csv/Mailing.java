package de.aikiit.mailversendala.csv;

import lombok.Builder;
import lombok.Data;

/**
 * Created by hirsch on 24.05.17.
 */
@Builder
@Data
public class Mailing {
    private String name;
    private String email;
}
