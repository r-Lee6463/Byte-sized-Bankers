package org.BSB.com.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.BSB.com.entity.Transaction;
import org.BSB.com.entity.User;
import org.BSB.com.repository.TransactionRepository;
import org.BSB.com.service.TransactionService;
import org.BSB.com.service.UserService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository txRepo;
    private final UserService userService;

    public TransactionServiceImpl(TransactionRepository txRepo,
            UserService userService) {
        this.txRepo = txRepo;
        this.userService = userService;
    }

    @Override
    public List<Transaction> findByUser(User user) {
        return txRepo.findByUser(user);
    }

    @Override
    public List<Transaction> findByUserAndCategory(User user, String category) {
        return txRepo.findByUserAndCategory(user, category);
    }

    @Override
    public List<String> getCategoriesForUser(User user) {
        return txRepo.findDistinctCategoriesByUser(user);
    }

    @Override
    public Transaction save(Transaction tx) {
        return txRepo.save(tx);
    }

    @Override
    public Optional<Transaction> findById(Long id) {
        return txRepo.findById(id);
    }

    /**
     * AI-driven action: record a new transaction for the given user email.
     * This must run in a transaction so the save() actually commits.
     */
    @Override
    @Transactional
    public void addTransaction(String userEmail,
            BigDecimal amount,
            LocalDate date,
            String description,
            String category) {
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setDate(date);
        tx.setDescription(description);
        tx.setCategory(category);
        txRepo.save(tx);
    }
}
