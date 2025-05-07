package org.BSB.com.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private String description;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors
    public Transaction() {
    }

    public Transaction(BigDecimal amount, String description, LocalDate date, User user) {
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.user = user;
    }
}
