package org.BSB.com.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.BSB.com.dto.AccountUpdateDto;
import org.BSB.com.dto.UserRegistrationDto;
import org.BSB.com.entity.User;
import org.BSB.com.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User registerNewUser(UserRegistrationDto dto) {
        // Only email uniqueness
        if (repo.existsByEmail(dto.getEmail())) {
            throw new EmailExistsException("Email already in use");
        }

        User u = new User();
        u.setUsername(dto.getUsername());
        u.setEmail(dto.getEmail());
        u.setPassword(encoder.encode(dto.getPassword()));
        return repo.save(u);
    }

    public User findByUsername(String username) {
        return repo.findByUsername(username)
                   .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public User updateUserAccount(String username, AccountUpdateDto dto) {
        User u = findByUsername(username);

        // email‐only uniqueness check
        if (!dto.getEmail().equals(u.getEmail()) && repo.existsByEmail(dto.getEmail())) {
            throw new EmailExistsException("Email already in use");
        }

        u.setEmail(dto.getEmail());
        u.setPassword(encoder.encode(dto.getPassword()));
        return repo.save(u);
    }

    public boolean emailExists(String email) {
        return repo.existsByEmail(email);
    }

    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    // and to check raw vs. encoded password:
    public boolean matchesPassword(String raw, String encoded) {
        return encoder.matches(raw, encoded);
    }
}