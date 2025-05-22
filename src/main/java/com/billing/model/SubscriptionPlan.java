package com.billing.model;

import lombok.Data;
import java.time.Period;

@Data
public class SubscriptionPlan {
    private final String id;
    private final String name;
    private final Period billingCycle;
    private final double price;
    private final String description;

    public static final SubscriptionPlan WEEKLY = new SubscriptionPlan(
            "weekly",
            "weekly",
            Period.ofWeeks(1),
            9.99,
            "Weekly monthly subscription"
    );

    public static final SubscriptionPlan MONTHLY = new SubscriptionPlan(
            "monthly",
            "Monthly",
            Period.ofMonths(1),
            19.99,
            "Professional monthly subscription"
    );

    public static final SubscriptionPlan YEARLY = new SubscriptionPlan(
            "yearly",
            "Yearly",
            Period.ofYears(1),
            99.99,
            "Annual subscription"
    );

    // Custom plan constructor
    public SubscriptionPlan(String id, String name, Period billingCycle, double price, String description) {
        this.id = id;
        this.name = name;
        this.billingCycle = billingCycle;
        this.price = price;
        this.description = description;
    }

    @Override
    public String toString() {
        return "SubscriptionPlan{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", billingCycle=" + billingCycle +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }
}
