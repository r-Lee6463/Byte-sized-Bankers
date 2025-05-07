package org.BSB.com.repository;

import org.BSB.com.entity.Transaction;
import org.BSB.com.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);
    List<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);


    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user = :user")
    BigDecimal getAccountBalanceByUser(@Param("user") User user);
}
