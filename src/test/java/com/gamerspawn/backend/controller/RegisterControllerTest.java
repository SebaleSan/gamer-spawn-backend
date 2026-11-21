package com.gamerspawn.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    void acceptsValidEmails() {
        for (String email : new String[] {"user@mail.com", "a@b.cl", "first.last@duocuc.cl", "x_y@sub.domain.org"}) {
            assertThat(RegisterController.EMAIL.matcher(email).matches()).as(email).isTrue();
        }
    }

    @Test
    void rejectsInvalidEmails() {
        for (String email : new String[] {"user", "user@", "user@mail", "@mail.com", "user@.com", "user@mail..com", "a b@mail.com", "a@b@c.com"}) {
            assertThat(RegisterController.EMAIL.matcher(email).matches()).as(email).isFalse();
        }
    }

    @Test
    void rejectsLongMalformedInputQuickly() {
        String malicious = "a@" + "b".repeat(50_000) + "!";
        long start = System.nanoTime();
        boolean matches = RegisterController.EMAIL.matcher(malicious).matches();
        Duration elapsed = Duration.ofNanos(System.nanoTime() - start);

        assertThat(matches).isFalse();
        assertThat(elapsed).isLessThan(Duration.ofMillis(200));
    }
}
