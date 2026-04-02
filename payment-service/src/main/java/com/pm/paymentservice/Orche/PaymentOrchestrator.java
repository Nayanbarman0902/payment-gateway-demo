package com.pm.paymentservice.Orche;

import com.pm.paymentservice.constants.PaymentStatus;
import com.pm.paymentservice.dto.PaymentIngestedEvent;
import com.pm.paymentservice.repository.PaymentRepository;
import com.pm.paymentservice.service.audit.AuditLogger;
import com.pm.paymentservice.service.frauddetection.BankSimulator;
import com.pm.paymentservice.service.frauddetection.FraudService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentOrchestrator {
    private final FraudService fraudService;
    private final BankSimulator bankSimulator;
    private final AuditLogger auditLogger;

    @Autowired
    private PaymentRepository paymentRepository;

    @Async("paymentExecutor")
    @EventListener
    public void handlePaymentIngested(PaymentIngestedEvent event) {
        String id = event.getPaymentId();

        // 1. checking Fraud Assessment
        boolean isSafe = fraudService.check(id);
        if (!isSafe) {
            updateStatus(id, PaymentStatus.FRAUD_REJECTED);
            return;
        }

        // 2. calling Bank service
        updateStatus(id, PaymentStatus.SENT_TO_BANK);
        bankSimulator.process(id);
    }

    private void updateStatus(String id, PaymentStatus newStatus) {
        // Every call here persists to the DB and writes to an IMMUTABLE Audit table
        paymentRepository.updateStatus(id, newStatus);
        auditLogger.log(id, newStatus);
    }
}
