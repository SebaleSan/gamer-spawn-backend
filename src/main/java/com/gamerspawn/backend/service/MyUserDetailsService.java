package com.gamerspawn.backend.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import com.gamerspawn.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gamerspawn.backend.entity.User;


@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger =
            LoggerFactory.getLogger(MyUserDetailsService.class);

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
    User user = userRepository.findByUsername(username);
    if (user == null) {
        logger.warn("Intento de login con usuario inexistente");
        throw new UsernameNotFoundException(username);
    }
    return user;
    }
}