package org.BSB.com.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.BSB.com.entity.Transaction;
import org.BSB.com.entity.User;
import org.BSB.com.repository.TransactionRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Fetch all transactions belonging to the given user.
     */
    public List<Transaction> findByUser(User user) {
        return transactionRepository.findByUser(user);
    }

    /**
     * (Optional) Fetch a single transaction by its ID.
     */
    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    /**
     * (Optional) Save or update a transaction.
     */
    public Transaction save(Transaction tx) {
        return transactionRepository.save(tx);
    }

    /**
     * (Optional) Delete a transaction by its ID.
     */
    public void deleteById(Long id) {
        transactionRepository.deleteById(id);
    }
}
