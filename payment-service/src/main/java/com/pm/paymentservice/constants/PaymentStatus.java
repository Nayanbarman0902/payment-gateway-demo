package com.pm.paymentservice.constants;

import lombok.Getter;

@Getter
public enum PaymentStatus {

    // Initial and In-Progress States
    INITIALIZED("The payment has been created and is awaiting processing."),
    FRAUD_CHECKING("Undergoing security and fraud assessment."),
    SENT_TO_BANK("Transaction details forwarded to the acquiring bank."),
    PROCESSING("Bank is currently authorizing the transaction."),

    // Terminal (Final) States
    COMPLETED("Payment successfully captured."),
    FAILED("The bank declined the transaction."),
    FRAUD_REJECTED("Rejected by internal security rules."),
    SYSTEM_ERROR("An unrecoverable internal failure occurred.");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public boolean isFinal() {
        return this == COMPLETED || this == FAILED ||
                this == FRAUD_REJECTED || this == SYSTEM_ERROR;
    }
}
