package com.billing.event;

import com.billing.model.Customer;
import lombok.Getter;

@Getter
public class PaymentFailedEvent {
    private final Customer customer;
    private final String failureReason;

    public PaymentFailedEvent(Customer customer, String failureReason) {
        this.customer = customer;
        this.failureReason = failureReason;
    }

}
