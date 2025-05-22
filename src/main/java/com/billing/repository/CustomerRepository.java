package com.billing.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.billing.model.Customer;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String> {
    Optional<Customer> findById(String id);
    List<Customer> findByNextBillingDateBeforeAndIsActiveTrue(LocalDateTime date);
}
