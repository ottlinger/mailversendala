package de.aikiit.mailversendala;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    @Test
    public void appIsNotNullAndLaunchable() throws IOException {
        assertThat(new App()).isNotNull();
        App.main(null);
    }

}
