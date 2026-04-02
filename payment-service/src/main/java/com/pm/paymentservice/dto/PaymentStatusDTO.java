package com.pm.paymentservice.dto;

import com.pm.paymentservice.constants.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentStatusDTO(
        String trackingId,
        PaymentStatus status,
        String statusDescription,
        BigDecimal amount,
        String currency,
        LocalDateTime lastUpdated
) {}
