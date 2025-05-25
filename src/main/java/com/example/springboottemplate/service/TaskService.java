package com.example.springboottemplate.service;

import com.example.springboottemplate.dto.TaskCreateRequest;
import com.example.springboottemplate.dto.TaskDto;
import com.example.springboottemplate.dto.TaskUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for Task operations
 */
public interface TaskService {
    
    /**
     * Get all tasks with pagination
     * 
     * @param pageable Pagination information
     * @return Page of TaskDto objects
     */
    Page<TaskDto> getAllTasks(Pageable pageable);
    
    /**
     * Get a task by ID
     * 
     * @param id Task ID
     * @return TaskDto
     */
    TaskDto getTaskById(Long id);
    
    /**
     * Create a new task
     * 
     * @param request Task creation request
     * @return Created TaskDto
     */
    TaskDto createTask(TaskCreateRequest request);
    
    /**
     * Update an existing task
     * 
     * @param id Task ID
     * @param request Task update request
     * @return Updated TaskDto
     */
    TaskDto updateTask(Long id, TaskUpdateRequest request);
    
    /**
     * Delete a task by ID
     * 
     * @param id Task ID
     */
    void deleteTask(Long id);
}
