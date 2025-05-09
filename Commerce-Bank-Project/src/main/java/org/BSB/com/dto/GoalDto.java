package org.BSB.com.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.NumberFormat;
import java.math.BigDecimal;

public class GoalDto {
    @NotBlank(message = "Category is required")
    private String category;
    private String customCategory;

    @DecimalMin(value = "0.01", message = "Limit must be at least $0.01")
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private BigDecimal limitAmount;

    public GoalDto() {
    }

    public GoalDto(String category, BigDecimal limitAmount) {
        this.category = category;
        this.limitAmount = limitAmount;
    }

    // Getters & Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCustomCategory() {
        return customCategory;
    }

    public void setCustomCategory(String customCategory) {
        this.customCategory = customCategory;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }
}