package ru.itmo.onlinecourses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.onlinecourses.entity.User;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
