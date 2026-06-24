package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.onlinecourses.dto.ApiDtos.PaymentRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.PaymentResponse;
import ru.itmo.onlinecourses.entity.Payment;
import ru.itmo.onlinecourses.enums.PaymentStatus;
import ru.itmo.onlinecourses.exception.NotFoundException;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final EntityMapper mapper;

    @Transactional
    public PaymentResponse create(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setStudent(userService.getUser(request.studentId()));
        payment.setCourse(courseService.getCourse(request.courseId()));
        payment.setAmount(request.amount());
        payment.setStatus(request.status());
        if (request.status() == PaymentStatus.PAID) {
            payment.setPaidAt(LocalDateTime.now());
        }
        return mapper.toPaymentResponse(paymentRepository.saveAndFlush(payment));
    }

    @Transactional(readOnly = true)
    public PaymentResponse findById(UUID id) {
        return mapper.toPaymentResponse(paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found: " + id)));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findByStudent(UUID studentId) {
        return paymentRepository.findByStudentId(studentId).stream().map(mapper::toPaymentResponse).toList();
    }
}
