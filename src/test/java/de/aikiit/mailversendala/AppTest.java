package de.aikiit.mailversendala;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void appIsNotNullAndLaunchable() {
        assertThat(new App()).isNotNull();
        App.main(null);
    }

}
