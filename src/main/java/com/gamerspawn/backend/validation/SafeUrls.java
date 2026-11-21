package com.gamerspawn.backend.validation;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class SafeUrls {

    private static final Pattern CSS_UNSAFE_CHARACTERS = Pattern.compile("[()'\"\\\\;\\s]");

    private SafeUrls() {
    }

    public static URI requireHttps(String rawUrl, int maxLength) {
        if (rawUrl == null || rawUrl.isBlank()) {
            throw badRequest("La URL no puede estar vacía.");
        }

        String url = rawUrl.trim();
        if (url.length() > maxLength) {
            throw badRequest("La URL no puede superar los " + maxLength + " caracteres.");
        }

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException exception) {
            throw badRequest("La URL tiene un formato inválido.");
        }

        if (!"https".equalsIgnoreCase(uri.getScheme())
                || uri.getHost() == null
                || uri.getRawUserInfo() != null) {
            throw badRequest("La URL debe comenzar con https://.");
        }
        return uri;
    }

    public static String requireCssSafeHttps(String rawUrl, int maxLength) {
        String url = requireHttps(rawUrl, maxLength).toString();
        if (CSS_UNSAFE_CHARACTERS.matcher(url).find()) {
            throw badRequest("La URL contiene caracteres no permitidos.");
        }
        return url;
    }

    public static ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
