package org.BSB.com.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.BSB.com.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);
}