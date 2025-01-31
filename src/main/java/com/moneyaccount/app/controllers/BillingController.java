package com.moneyaccount.app.controllers;

import com.moneyaccount.app.services.BillingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;


@RestController
@RequestMapping("/billing")
public class BillingController {
    private final BillingService billingService;

    @Autowired
    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    /**
     * Get user balance
     */
    @GetMapping("/{userId}/balance")
    public ResponseEntity<Double> getUserBalance(@PathVariable UUID userId) {
        double balance = billingService.getUserBalance(userId);
        return ResponseEntity.ok(balance);
    }

    /**
     * Top up user account
     */
    @PostMapping("/{userId}/replenish")
    public ResponseEntity<Void> replenishAccount(@PathVariable UUID userId, @RequestParam double amount) {
        billingService.replenishAccount(userId, amount);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
