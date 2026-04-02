package com.pm.paymentservice.service.audit;

import com.pm.paymentservice.constants.PaymentStatus;
import com.pm.paymentservice.dto.PaymentAuditLog;
import com.pm.paymentservice.repository.AuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogger {


    private final AuditRepository auditRepository;

    @Autowired
    public AuditLogger(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String paymentId,  PaymentStatus statusTo) {
        PaymentAuditLog logEntry = new PaymentAuditLog();
        logEntry.setPaymentId(paymentId);
        //logEntry.setStatusFrom(statusFrom != null ? statusFrom.name() : "NONE");
        logEntry.setStatusTo(statusTo.name());
        auditRepository.save(logEntry);
    }
}
