package com.billing.service;

import com.billing.event.PaymentFailedEvent;
import com.billing.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import java.util.concurrent.atomic.AtomicInteger;
import com.billing.utility.EmailUtility;

@Service
public class DunningManager {
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_INTERVAL_MS = 24 * 60 * 60 * 1000; // 24 hours
    private final AtomicInteger retryCount = new AtomicInteger(0);
    @Autowired
    private EmailUtility emailUtility;

    private BillingService billingService;
    @Autowired
    public DunningManager() {
    }
    @Autowired
    public void setBillingService(BillingService billingService) {
        this.billingService = billingService;
    }

    @EventListener
    public void handlePaymentFailedEvent(PaymentFailedEvent event) {
        notifyCustomer(event.getCustomer(), event.getFailureReason());
    }

    @Scheduled(fixedDelay = RETRY_INTERVAL_MS)
    public void processFailedPayments() {
        if (retryCount.get() >= MAX_RETRIES) {
            retryCount.set(0);
            return;
        }

        billingService.processFailedPayments();
        retryCount.incrementAndGet();
    }

    public void notifyCustomer(Customer customer, String failureReason) {
        String subject = "Payment Failure Notification";
        String body = "Dear " + customer.getName() + ",\n\n" +
                "We have experienced a payment failure and will retry again in 24 hours.\n\n" +
                "Reason: " + failureReason + "\n\n" +
                "Please make sure there are sufficient funds in your account.\n\n" +
                "Thank you for your cooperation.\n\n" +
                "Best regards,\n" +
                "Your Billing Team";

        emailUtility.sendEmail(customer.getEmail(), subject, body);
    }
}