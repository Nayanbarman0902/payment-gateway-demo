package com.pm.paymentservice.dto;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "payment_audit_logs")
public class PaymentAuditLog {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

    @Column(name = "payment_id", nullable = false)
    private String paymentId;

   @Column(name = "status_from")
    private String statusFrom;

    @Column(name = "status_io")
    private String statusTo;

   @Column(name = "timestamp")
    private LocalDateTime timestamp;

   @Column(name = "metadata")
    private String metadata ;
}
