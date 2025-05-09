package org.BSB.com.service.impl;

import java.util.Optional;

import org.BSB.com.dto.UserRegistrationDto;
import org.BSB.com.dto.AccountUpdateDto;
import org.BSB.com.entity.User;
import org.BSB.com.repository.UserRepository;
import org.BSB.com.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of UserService.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepo,
            PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    @Override
    public void registerNewUser(UserRegistrationDto registrationDto) {
        // 1) email uniqueness
        if (emailExists(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + registrationDto.getEmail());
        }
        // 2) password match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        // 3) build & save
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setUsername(registrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        userRepo.save(user);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    @Override
    public void updateProfile(String currentEmail,
            AccountUpdateDto dto,
            String imageFilename) {
        User user = userRepo.findByEmail(currentEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // email change
        if (dto.getEmail() != null
                && !dto.getEmail().equals(user.getEmail())) {
            if (emailExists(dto.getEmail())) {
                throw new IllegalArgumentException("That email is already in use");
            }
            user.setEmail(dto.getEmail());
        }

        // password change
        if (dto.getPassword() != null
                && !dto.getPassword().isBlank()) {
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match");
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // profile image change
        if (imageFilename != null) {
            user.setProfileImage(imageFilename);
        }

        userRepo.save(user);
    }

    @Override
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) {
            throw new UsernameNotFoundException("No authenticated user");
        }
        String email = ((UserDetails) auth.getPrincipal()).getUsername();
        return findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
