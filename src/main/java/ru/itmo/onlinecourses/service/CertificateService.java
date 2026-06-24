package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.onlinecourses.dto.ApiDtos.CertificateResponse;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.repository.CertificateRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateService {
    private final CertificateRepository certificateRepository;
    private final EntityMapper mapper;

    @Transactional(readOnly = true)
    public List<CertificateResponse> findByStudent(UUID studentId) {
        return certificateRepository.findByStudentId(studentId).stream().map(mapper::toCertificateResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CertificateResponse> findByCourse(UUID courseId) {
        return certificateRepository.findByCourseId(courseId).stream().map(mapper::toCertificateResponse).toList();
    }
}
