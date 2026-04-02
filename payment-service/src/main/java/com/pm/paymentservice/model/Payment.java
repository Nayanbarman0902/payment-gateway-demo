package com.pm.paymentservice.model;

import com.pm.paymentservice.constants.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(indexes = @Index(columnList = "idempotencyKey", unique = true))
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Version
    private Long version;

    @Column(unique = true, nullable = false)
    private BigDecimal amount;

    @Column(unique = true, nullable = false)
    private String currency;

}