package org.BSB.com.service;

// Thrown when someone tries to register/update to an email that already exists
public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String message) {
        super(message);
    }
}