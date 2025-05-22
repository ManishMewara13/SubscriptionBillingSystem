package com.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SubscriptionBillingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SubscriptionBillingApplication.class, args);
    }
}