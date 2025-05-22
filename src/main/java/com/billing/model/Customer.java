package com.billing.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Document(collection = "customers")
public class Customer {
    @Id
    private String id;
    private final String name;
    private final String email;
    private PaymentMethod paymentMethod;
    private SubscriptionPlan subscriptionPlan;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime nextBillingDate;
    private boolean isActive;

    public Customer(String name, String email, PaymentMethod paymentMethod) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.paymentMethod = paymentMethod;
        this.isActive = false;
    }

    public void activate(SubscriptionPlan plan, LocalDateTime startDate) {
        this.subscriptionPlan = plan;
        this.subscriptionStartDate = startDate;
        this.nextBillingDate = startDate.plus(plan.getBillingCycle());
        this.isActive = true;
    }


    @Override
    public String toString() {
        return "Customer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", paymentMethod=" + paymentMethod +
                ", subscriptionPlan=" + subscriptionPlan +
                ", subscriptionStartDate=" + subscriptionStartDate +
                ", nextBillingDate=" + nextBillingDate +
                ", isActive=" + isActive +
                '}';
    }

}
