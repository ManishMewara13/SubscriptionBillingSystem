package com.billing.service;

import com.billing.event.PaymentFailedEvent;
import com.billing.model.*;
import com.billing.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BillingService {
    private final ApplicationEventPublisher eventPublisher;
    @Autowired
    private final CustomerRepository customerRepository;
    @Autowired
    private final PaymentRepository paymentRepository;

    @Transactional
    public Customer createCustomer(String name, String email, PaymentMethod paymentMethod) {
        Customer customer = new Customer(name, email, paymentMethod);
        return customerRepository.save(customer);
    }

    @Transactional
    public String createSubscription(String customerId, SubscriptionPlan plan) {
        try {
            Optional<Customer> customer = customerRepository.findById(customerId);

            if (customer.isPresent()) {
                customer.get().activate(plan, LocalDateTime.now());
                customerRepository.save(customer.get());
                String message = "Subscription Created for customer: " + customer.get();
                log.info(message);
                return message;
            } else {
                String message = "Customer not found: " + customerId;
                log.error(message);
                throw new CustomerNotFoundException(message);
            }
        } catch (Exception e) {
            log.error("Error creating subscription", e);
            return "failed";
        }
    }

    @Transactional
    public String cancelSubscription(String customerId) {
        try {
            Optional<Customer> customer = customerRepository.findById(customerId);
            if (customer.isPresent()) {
                Customer existingCustomer = customer.get();
                existingCustomer.setActive(false);
                customerRepository.save(existingCustomer);
                String message = "Subscription canceled for customer: " + existingCustomer.toString();
                log.info(message);
                return message;
            } else {
                String message = "Customer not found: " + customerId;
                log.error(message);
                throw new CustomerNotFoundException(message);
            }
        } catch (Exception e) {
            log.error("Error canceling subscription", e);
            return "failed";
        }
    }

    public Optional<Customer> getCustomerById(String customerId) {
        return customerRepository.findById(customerId);
    }

    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void processBillingCycle() {
        try {
            List<Customer> customersToBill = customerRepository.findByNextBillingDateBeforeAndIsActiveTrue(
                    LocalDateTime.now());

            customersToBill.forEach(this::processCustomerPayment);
        } catch (Exception e) {
            log.error("Error processing billing cycle", e);
        }
    }

    @Transactional
    void processCustomerPayment(Customer customer) {
        try {
            Payment payment = new Payment(customer.getId(), customer.getSubscriptionPlan().getPrice());
            paymentRepository.save(payment);

            if (processPayment(customer.getPaymentMethod())) {
                payment.markAsSuccessful();
                customer.setNextBillingDate(
                        customer.getNextBillingDate().plus(customer.getSubscriptionPlan().getBillingCycle()));
                customerRepository.save(customer);
            } else {
                payment.markAsFailed("Payment failed");
                eventPublisher.publishEvent(new PaymentFailedEvent(customer, "Payment failed for subscription"));
            }
        } catch (Exception e) {
            log.error("Error processing payment for customer " + customer.getId(), e);
        }
    }

    private boolean processPayment(PaymentMethod paymentMethod) {
        if (!paymentMethod.isValid()) {
            log.warn("Invalid payment method provided");
            return false;
        }
        return true;
    }

    @Transactional
    public void processFailedPayments() {
        try {
            paymentRepository.findByStatus(PaymentStatus.FAILED)
                    .forEach(payment -> customerRepository.findById(payment.getCustomerId())
                            .ifPresent(this::processCustomerPayment));
        } catch (Exception e) {
            log.error("Error processing failed payments", e);
        }
    }
}

class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) { super(message); }
}

class InvalidPaymentMethodException extends RuntimeException {
    public InvalidPaymentMethodException(String message) { super(message); }
}