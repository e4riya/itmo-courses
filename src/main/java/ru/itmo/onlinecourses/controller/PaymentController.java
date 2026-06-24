package ru.itmo.onlinecourses.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.itmo.onlinecourses.dto.ApiDtos.PaymentRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.PaymentResponse;
import ru.itmo.onlinecourses.service.PaymentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/payments")
    public PaymentResponse create(@Valid @RequestBody PaymentRequest request) {
        return paymentService.create(request);
    }

    @GetMapping("/payments/{id}")
    public PaymentResponse one(@PathVariable UUID id) {
        return paymentService.findById(id);
    }

    @GetMapping("/students/{studentId}/payments")
    public List<PaymentResponse> byStudent(@PathVariable UUID studentId) {
        return paymentService.findByStudent(studentId);
    }
}
