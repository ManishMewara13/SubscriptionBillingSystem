package com.billing.controller;

import com.billing.service.BillingService;
import com.billing.model.Customer;
import com.billing.model.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.billing.model.SubscriptionPlan;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final BillingService billingService;

    @Autowired
    public SubscriptionController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/test")
    public String test() {
        log.info("test");
        return "test";
    }

    @PostMapping("/create")
    public ResponseEntity<Customer> createSubscription(@RequestParam String name, @RequestParam String email, @RequestParam String cardNumber, @RequestParam String expiryDate, @RequestParam String cvv) {
        try {
            PaymentMethod paymentMethod = new PaymentMethod(cardNumber, expiryDate, cvv);
            Customer customer = billingService.createCustomer(name, email, paymentMethod);
            log.info("Created subscription for customer {}", customer);
            return ResponseEntity.ok(customer);
        } catch (Exception e) {
            log.error("Error creating subscription", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{customerId}/activate")
    public ResponseEntity<?> activateSubscription(@PathVariable String customerId, @RequestParam SubscriptionPlan plan) {
        String activeSubscription = billingService.createSubscription(customerId, plan);
        log.info("Activated subscription for customer {}", customerId);
        return ResponseEntity.ok(activeSubscription);
    }

    @PostMapping("/{customerId}/cancel")
    public ResponseEntity<String> cancelSubscription(@PathVariable String customerId) {
        String cancelledSubscription = billingService.cancelSubscription(customerId);
        log.info("Cancelled subscription for customer {}", customerId);
        return ResponseEntity.ok(cancelledSubscription);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getSubscription(@PathVariable String customerId) {
        Optional<Customer> customerOpt = billingService.getCustomerById(customerId);
        if (!customerOpt.isPresent()) {
            log.info("Subscription not found for customer {}", customerId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.of(customerOpt);
    }
}