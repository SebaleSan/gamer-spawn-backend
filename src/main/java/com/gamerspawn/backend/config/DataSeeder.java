package com.gamerspawn.backend.config;


import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.gamerspawn.backend.entity.User;
import com.gamerspawn.backend.repository.UserRepository;

@Component
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        createUserIfAbsent("admin", "admin@correo.cl", "admin", "ROLE_ADMIN");
        createUserIfAbsent("user1", "user1@correo.cl", "user1", "ROLE_USER");
        createUserIfAbsent("user2", "user2@correo.cl", "user2", "ROLE_USER");
    }

    private void createUserIfAbsent(String username, String email, String password, String role) {
        if (userRepository.findByUsername(username) == null) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            userRepository.save(user);
        }
    }
}