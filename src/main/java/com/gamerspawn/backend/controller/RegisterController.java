package com.gamerspawn.backend.controller;

import com.gamerspawn.backend.entity.User;
import com.gamerspawn.backend.repository.UserRepository;
import com.gamerspawn.backend.validation.SafeUrls;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class RegisterController {

    private static final String DEFAULT_ROLE = "ROLE_USER";
    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9_.-]{3,30}");
    static final Pattern EMAIL = Pattern.compile("[^@\\s]++@[^@\\s.]++(?:\\.[^@\\s.]++)++");
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_BYTES = 72;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public record RegisterRequest(String username, String email, String password) {
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequest request) {
        if (request == null) {
            throw SafeUrls.badRequest("Los datos de registro son obligatorios.");
        }

        String username = request.username() == null ? "" : request.username().trim();
        if (!USERNAME.matcher(username).matches()) {
            throw SafeUrls.badRequest(
                    "El nombre de usuario debe tener entre 3 y 30 caracteres: letras, números, punto, guion o guion bajo.");
        }

        String email = request.email() == null ? "" : request.email().trim().toLowerCase(Locale.ROOT);
        if (email.length() > MAX_EMAIL_LENGTH || !EMAIL.matcher(email).matches()) {
            throw SafeUrls.badRequest("El correo electrónico no es válido.");
        }

        String password = request.password();
        if (password == null
                || password.length() < MIN_PASSWORD_LENGTH
                || password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            throw SafeUrls.badRequest("La contraseña debe tener al menos 8 caracteres y como máximo 72 bytes.");
        }

        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            throw alreadyRegistered();
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(DEFAULT_ROLE);

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw alreadyRegistered();
        }
    }

    private static ResponseStatusException alreadyRegistered() {
        return new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El usuario o el correo ya están registrados.");
    }
}
