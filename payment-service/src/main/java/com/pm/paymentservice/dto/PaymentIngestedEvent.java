package com.pm.paymentservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentIngestedEvent {
    private final String paymentId;
}
