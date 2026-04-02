package com.pm.paymentservice.service.frauddetection;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FraudService {

    public boolean check(String paymentId) {
        log.info("Performing fraud assessment for payment: {}", paymentId);
        boolean isSafe = Math.random() > 0.05;

        if (!isSafe) {
            log.warn("FRAUD ALERT: Payment {} flagged as suspicious", paymentId);
        }

        return isSafe;
    }
}
