package ru.itmo.onlinecourses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itmo.onlinecourses.entity.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
}
