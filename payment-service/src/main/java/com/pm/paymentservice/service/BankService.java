package com.pm.paymentservice.service;


import com.pm.paymentservice.exception.BankTimeoutException;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Retryable;

import java.util.Random;

@Service
public class BankService {

    @Retryable(value = {BankTimeoutException.class}, maxAttempts = 3)
    public void process(String paymentId) throws InterruptedException {
        Thread.sleep(new Random().nextInt(2000) + 1000);

        if (Math.random() > 0.8) {
            throw new BankTimeoutException("Bank Connection Failed - Retrying...");
        }
        // Logic to mark payment as COMPLETED
    }

}
