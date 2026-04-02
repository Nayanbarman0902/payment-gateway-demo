package com.pm.paymentservice.service.frauddetection;

import com.pm.paymentservice.exception.BankTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class BankSimulator {

    private final Random random = new Random();

    @Retryable(
            value = { BankTimeoutException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public boolean process(String paymentId) {
        log.info("Communicating with Acquiring Bank for payment: {}", paymentId);

        try {
            // Simulate variable network latency (between 500ms and 2500ms)
            long latency = 500 + random.nextInt(2000);
            Thread.sleep(latency);

            // Simulate random bank failure (15% failure rate)
            if (random.nextDouble() < 0.15) {
                log.error("Bank gateway timeout for payment: {}", paymentId);
                throw new RuntimeException("Downstream Bank Timeout");
            }

            log.info("Bank authorized payment: {} in {}ms", paymentId, latency);
            return true;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
