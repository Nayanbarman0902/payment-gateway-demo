package com.pm.paymentservice.controller;

import com.pm.paymentservice.dto.PaymentRequest;
import com.pm.paymentservice.dto.PaymentResponse;
import com.pm.paymentservice.dto.PaymentStatusDTO;
import com.pm.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {


    private PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> initiatePayment(@Valid @RequestBody PaymentRequest request) {
        // Returns record with a tracking ID
        String trackingId = paymentService.ingest(request);
        return ResponseEntity.accepted().body(new PaymentResponse(trackingId, "PROCESSING"));
    }

    @GetMapping("/{trackingId}")
    public ResponseEntity<PaymentStatusDTO> getStatus(@PathVariable String trackingId) {
        return ResponseEntity.ok(paymentService.getStatus(trackingId));
    }
}
