package com.billing.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentMethod {
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private LocalDateTime lastUpdated;

    public PaymentMethod(String cardNumber, String expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isValid() {
        // Basic validation - in real implementation, use proper card validation
        return !cardNumber.isEmpty() && !expiryDate.isEmpty() && !cvv.isEmpty();
    }

    public void update(String cardNumber, String expiryDate, String cvv) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.lastUpdated = LocalDateTime.now();
    }

}
