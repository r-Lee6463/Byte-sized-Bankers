package org.BSB.com.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.BSB.com.entity.Transaction;
import org.BSB.com.entity.User;

/**
 * Core transaction operations.
 */
public interface TransactionService {

    /** All transactions for a user */
    List<Transaction> findByUser(User user);

    /** Transactions for a user in a given category */
    List<Transaction> findByUserAndCategory(User user, String category);

    /** Distinct categories this user has used */
    List<String> getCategoriesForUser(User user);

    /** Save or update a transaction */
    Transaction save(Transaction tx);

    /** Find one by its ID */
    Optional<Transaction> findById(Long id);

    /**
     * **AI-driven**: record a new transaction for the given user email.
     */
    void addTransaction(
            String userEmail,
            BigDecimal amount,
            LocalDate date,
            String description,
            String category);
}
