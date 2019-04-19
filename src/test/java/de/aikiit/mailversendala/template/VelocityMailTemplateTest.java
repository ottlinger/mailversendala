package de.aikiit.mailversendala.template;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VelocityMailTemplateTest {
    @Test
    public void initVelocityEngine() {
        final VelocityMailTemplate mailTemplate = new VelocityMailTemplate();
        assertThat(mailTemplate.getHtml()).isNull();
        assertThat(mailTemplate.getPlaintext()).isEqualTo("Dear ME REALLY, this mail is for you only. Best regards, Mailversendala");
    }
}
