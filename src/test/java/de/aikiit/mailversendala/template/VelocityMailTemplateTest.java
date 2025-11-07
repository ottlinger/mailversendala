package de.aikiit.mailversendala.template;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VelocityMailTemplateTest {
    @Test
    public void replaceContentsInTemplateFiles() {
        final VelocityMailTemplate mailTemplate = new VelocityMailTemplate();
        assertThat(mailTemplate.getHtml()).containsOnlyOnce("ME REALLY");
        assertThat(mailTemplate.getPlaintext()).isEqualTo("Dear ME REALLY, this mail is for you only. Best regards, Mailversendala");
    }
}
