package com.example.springboottemplate.repository;

import com.example.springboottemplate.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Task entity
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Spring Data JPA provides basic CRUD operations by default
    // Custom query methods can be added here if needed
}
