package org.BSB.com.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "goals")
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(name = "limit_amount", nullable = false)
    private BigDecimal limitAmount;

    @Transient // computed at runtime
    private BigDecimal spent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors, Getters & Setters
    public Goal() {
    }

    public Goal(User user, String category, BigDecimal limitAmount) {
        this.user = user;
        this.category = category;
        this.limitAmount = limitAmount;
        this.spent = BigDecimal.ZERO;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public BigDecimal getSpent() {
        return spent;
    }

    public void setSpent(BigDecimal spent) {
        this.spent = spent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
