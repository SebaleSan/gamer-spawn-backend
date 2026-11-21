package com.gamerspawn.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gamerspawn.backend.entity.EventMedia.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class EventMediaControllerTest {

    private static final String EMBED = "https://www.youtube-nocookie.com/embed/dQw4w9WgXcQ";

    @Test
    void acceptsHttpsPhotoUrl() {
        assertThat(EventMediaController.normalizeUrl(MediaType.PHOTO, "  https://example.com/a.jpg "))
                .isEqualTo("https://example.com/a.jpg");
    }

    @Test
    void convertsYoutubeLinksToEmbedUrl() {
        assertThat(EventMediaController.normalizeUrl(MediaType.VIDEO, "https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=10"))
                .isEqualTo(EMBED);
        assertThat(EventMediaController.normalizeUrl(MediaType.VIDEO, "https://youtu.be/dQw4w9WgXcQ"))
                .isEqualTo(EMBED);
        assertThat(EventMediaController.normalizeUrl(MediaType.VIDEO, "https://youtube.com/shorts/dQw4w9WgXcQ"))
                .isEqualTo(EMBED);
    }

    @Test
    void rejectsUnsafeOrInvalidUrls() {
        String[][] invalid = {
                {"PHOTO", "javascript:alert(1)"},
                {"PHOTO", "data:image/png;base64,AAAA"},
                {"PHOTO", "http://example.com/a.jpg"},
                {"PHOTO", "https://user@example.com/a.jpg"},
                {"PHOTO", "https://example.com/" + "a".repeat(500)},
                {"PHOTO", " "},
                {"VIDEO", "https://vimeo.com/123456"},
                {"VIDEO", "https://www.youtube.com/watch?v=short"},
                {"VIDEO", "https://youtu.be"},
                {"VIDEO", "https://evil.com/watch?v=dQw4w9WgXcQ"},
        };

        for (String[] input : invalid) {
            assertThatThrownBy(() -> EventMediaController.normalizeUrl(MediaType.valueOf(input[0]), input[1]))
                    .as(input[1])
                    .isInstanceOf(ResponseStatusException.class);
        }
    }
}
