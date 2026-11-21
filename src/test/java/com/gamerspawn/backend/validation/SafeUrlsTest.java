package com.gamerspawn.backend.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class SafeUrlsTest {

    @Test
    void acceptsPlainHttpsImageUrl() {
        assertThat(SafeUrls.requireCssSafeHttps("https://example.com/banner.jpg", 255))
                .isEqualTo("https://example.com/banner.jpg");
    }

    @Test
    void rejectsUrlsThatCouldBreakOutOfCssUrl() {
        String[] invalid = {
                "https://example.com/a.jpg);background:red",
                "https://example.com/a.jpg'",
                "https://example.com/a(1).jpg",
                "https://example.com/a.jpg;color:red",
        };

        for (String url : invalid) {
            assertThatThrownBy(() -> SafeUrls.requireCssSafeHttps(url, 255))
                    .as(url)
                    .isInstanceOf(ResponseStatusException.class);
        }
    }
}
