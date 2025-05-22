package com.billing.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String customerId;
    private double amount;
    private LocalDateTime paymentDate;
    private PaymentStatus status;
    private String failureReason;

    public Payment(String customerId, double amount) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.amount = amount;
        this.paymentDate = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    public void markAsSuccessful() {
        this.status = PaymentStatus.SUCCESSFUL;
    }

    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

}

