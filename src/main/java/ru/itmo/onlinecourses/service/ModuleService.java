package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.onlinecourses.dto.ApiDtos.ModuleRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.ModuleResponse;
import ru.itmo.onlinecourses.entity.CourseModule;
import ru.itmo.onlinecourses.exception.NotFoundException;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.repository.CourseModuleRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModuleService {
    private final CourseModuleRepository moduleRepository;
    private final CourseService courseService;
    private final EntityMapper mapper;

    @Transactional(readOnly = true)
    public List<ModuleResponse> findByCourse(UUID courseId) {
        return moduleRepository.findByCourseIdOrderByPosition(courseId).stream().map(mapper::toModuleResponse).toList();
    }

    @Transactional
    public ModuleResponse create(UUID courseId, ModuleRequest request) {
        CourseModule module = new CourseModule();
        module.setCourse(courseService.getCourse(courseId));
        apply(module, request);
        return mapper.toModuleResponse(moduleRepository.save(module));
    }

    @Transactional
    public ModuleResponse update(UUID moduleId, ModuleRequest request) {
        CourseModule module = getModule(moduleId);
        apply(module, request);
        return mapper.toModuleResponse(moduleRepository.save(module));
    }

    @Transactional
    public void delete(UUID moduleId) {
        moduleRepository.delete(getModule(moduleId));
    }

    public CourseModule getModule(UUID id) {
        return moduleRepository.findById(id).orElseThrow(() -> new NotFoundException("Module not found: " + id));
    }

    private void apply(CourseModule module, ModuleRequest request) {
        module.setTitle(request.title());
        module.setDescription(request.description());
        module.setPosition(request.position());
    }
}
