package ru.itmo.onlinecourses.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.onlinecourses.dto.ApiDtos.UserRequest;
import ru.itmo.onlinecourses.dto.ApiDtos.UserResponse;
import ru.itmo.onlinecourses.entity.Role;
import ru.itmo.onlinecourses.entity.User;
import ru.itmo.onlinecourses.enums.RoleName;
import ru.itmo.onlinecourses.exception.NotFoundException;
import ru.itmo.onlinecourses.mapper.EntityMapper;
import ru.itmo.onlinecourses.repository.RoleRepository;
import ru.itmo.onlinecourses.repository.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EntityMapper mapper;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(mapper::toUserResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        return mapper.toUserResponse(getUser(id));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        User user = new User();
        apply(user, request);
        return mapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(UUID id, UserRequest request) {
        User user = getUser(id);
        apply(user, request);
        return mapper.toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        userRepository.delete(getUser(id));
    }

    public User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    private void apply(User user, UserRequest request) {
        user.setEmail(request.email());
        user.setPasswordHash(request.passwordHash());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        Set<RoleName> names = request.roles() == null || request.roles().isEmpty()
                ? Set.of(RoleName.STUDENT)
                : request.roles();
        Set<Role> roles = names.stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new NotFoundException("Role not found: " + name)))
                .collect(Collectors.toSet());
        user.setRoles(roles);
    }
}
