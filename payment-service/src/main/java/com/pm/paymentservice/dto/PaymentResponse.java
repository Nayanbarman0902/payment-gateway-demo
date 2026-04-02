package com.pm.paymentservice.dto;

import java.time.LocalDateTime;

public record PaymentResponse(
        String trackingId,
        String message
        //LocalDateTime timestamp
) {}
