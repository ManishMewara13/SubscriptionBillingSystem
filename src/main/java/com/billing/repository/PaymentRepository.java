package com.billing.repository;

import com.billing.model.Payment;
import com.billing.model.PaymentStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByStatus(PaymentStatus status);
}
