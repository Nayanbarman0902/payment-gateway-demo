package com.pm.paymentservice.service;


import com.pm.paymentservice.constants.PaymentStatus;
import com.pm.paymentservice.dto.PaymentAuditLog;
import com.pm.paymentservice.dto.PaymentIngestedEvent;
import com.pm.paymentservice.dto.PaymentRequest;
import com.pm.paymentservice.dto.PaymentStatusDTO;
import com.pm.paymentservice.exception.PaymentNotFoundException;
import com.pm.paymentservice.model.Payment;
import com.pm.paymentservice.repository.AuditRepository;
import com.pm.paymentservice.repository.PaymentRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Data
public class PaymentService {
    private final PaymentRepository repository;
    private final AuditRepository auditRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public String ingest(PaymentRequest request) {

        // 1. Idempotency Check: Prevent duplicate financial processing
        return repository.findByIdempotencyKey(request.idempotencyKey())
                .map(existing -> {
                    log.info("Duplicate request detected for key: {}. Returning existing ID.", request.idempotencyKey());
                    return existing.getId();
                })
                .orElseGet(() -> {

                    // 2. Create new Payment Record
                    Payment payment = new Payment();
                    payment.setAmount(request.amount());
                    payment.setCurrency(request.currency());
                    payment.setIdempotencyKey(request.idempotencyKey());
                    payment.setStatus(PaymentStatus.INITIALIZED);
                    Payment saved = repository.save(payment);

                    logAudit(saved.getId(), null, PaymentStatus.INITIALIZED);
                    eventPublisher.publishEvent(new PaymentIngestedEvent(saved.getId()));
                    return saved.getId();
                });
    }

    /**
     * Secure status inquiry for clients.
     */
    @Transactional(readOnly = true)
    public PaymentStatusDTO getStatus(String trackingId) {
        Payment payment = repository.findById(trackingId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment ID not found: " + trackingId));

        return new PaymentStatusDTO(
                payment.getId(),
                payment.getStatus(),
                payment.getStatus().getDescription(),
                payment.getAmount(),
                payment.getCurrency(),
                LocalDateTime.now() // Ideally mapped from an 'updatedAt' field in Entity
        );
    }

    /**
     * Internal method used by the Orchestrator to update states.
     * Uses Transactional propagation to ensure DB and Audit log stay in sync.
     */
    @Transactional
    public void updateStatus(String id, PaymentStatus newStatus) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Payment missing during async processing"));

        PaymentStatus oldStatus = payment.getStatus();

        // Prevent state transitions if already in a final state
        if (oldStatus.isFinal()) {
            log.warn("Attempted to update final payment {} from {} to {}", id, oldStatus, newStatus);
            return;
        }

        payment.setStatus(newStatus);
        repository.save(payment);
        logAudit(id, oldStatus, newStatus);
    }

    private void logAudit(String paymentId, PaymentStatus from, PaymentStatus to) {
        PaymentAuditLog logEntry = new PaymentAuditLog();
        logEntry.setPaymentId(paymentId);
        logEntry.setStatusFrom(from != null ? from.name() : "NONE");
        logEntry.setStatusTo(to.name());
        logEntry.setTimestamp(LocalDateTime.now());
        auditRepository.save(logEntry);
    }
}
