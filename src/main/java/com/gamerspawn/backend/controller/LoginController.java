package com.gamerspawn.backend.controller;

import com.gamerspawn.backend.config.JWTAuthenticationConfig;
import com.gamerspawn.backend.service.MyUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
public class LoginController {

    private final JWTAuthenticationConfig jwtAuthenticationConfig;
    private final MyUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public LoginController(JWTAuthenticationConfig jwtAuthenticationConfig,
                          MyUserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.jwtAuthenticationConfig = jwtAuthenticationConfig;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

        @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam("user") String username,
            @RequestParam("encryptedPass") String encryptedPass) {

        if (username == null || username.isBlank() || username.length() > 50
                || encryptedPass == null || encryptedPass.isEmpty()
                || encryptedPass.getBytes(StandardCharsets.UTF_8).length > 72) {
            return invalidCredentials();
        }

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException exception) {
            return invalidCredentials();
        }

        if (!passwordEncoder.matches(encryptedPass, userDetails.getPassword())) {
            return invalidCredentials();
        }

        return ResponseEntity.ok(jwtAuthenticationConfig.getJWTToken(userDetails));
    }

    private ResponseEntity<String> invalidCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }
}
