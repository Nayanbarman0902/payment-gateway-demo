package com.pm.paymentservice.repository;

import com.pm.paymentservice.dto.PaymentAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<PaymentAuditLog, Long> {

}
