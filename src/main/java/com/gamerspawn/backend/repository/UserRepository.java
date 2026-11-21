package com.gamerspawn.backend.repository;

import org.springframework.data.repository.CrudRepository;
import com.gamerspawn.backend.entity.User;

public interface UserRepository extends CrudRepository<User, Long> {

    User findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
