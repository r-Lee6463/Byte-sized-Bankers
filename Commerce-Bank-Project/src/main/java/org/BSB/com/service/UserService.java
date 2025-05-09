package org.BSB.com.service;

import java.util.Optional;
import org.BSB.com.dto.UserRegistrationDto;
import org.BSB.com.dto.AccountUpdateDto;
import org.BSB.com.entity.User;

/**
 * Main business interface for user management.
 */
public interface UserService {

    /**
     * Look up an existing user by email.
     * 
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if
     *                                                                                 none
     *                                                                                 found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Create a brand-new user account.
     * 
     * @param registrationDto contains email, username, password, confirmPassword
     * @throws IllegalArgumentException if email already in use or passwords
     *                                  mismatch
     */
    void registerNewUser(UserRegistrationDto registrationDto);

    /**
     * @return the currently authenticated User
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException
     */
    User getCurrentUser();

    /**
     * Simple existence check.
     */
    boolean emailExists(String email);

    /**
     * Update an existing user’s email, password, and/or profile image.
     * 
     * @param currentEmail  the user’s current email (to look them up)
     * @param dto           contains new email, newPassword, confirmPassword
     * @param imageFilename filename returned by your StorageService (or null if no
     *                      new upload)
     * @throws IllegalArgumentException on validation failure
     */
    void updateProfile(String currentEmail,
            AccountUpdateDto dto,
            String imageFilename);
}
