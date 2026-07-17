package com.proyecto.codedraft.course.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.proyecto.codedraft.course.dto.CoursePriorityUpdateRequest;
import com.proyecto.codedraft.course.dto.CourseRequest;
import com.proyecto.codedraft.course.dto.CourseResponse;
import com.proyecto.codedraft.course.dto.CourseStatusUpdateRequest;
import com.proyecto.codedraft.course.model.Course;
import com.proyecto.codedraft.course.service.CourseNotFoundException;
import com.proyecto.codedraft.course.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CursoController {

    private final CourseService courseService;

    public CursoController(CourseService courseService) {
        this.courseService = courseService;
    }


    //registra un curso 
    @PostMapping("/registerCurso")
    public ResponseEntity<CourseResponse> registerCourse(@Valid @RequestBody CourseRequest request) {
        //recibe  un curso  y lo guarda 
        Course course = courseService.registerCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CourseResponse.fromModel(course));
    }

    //muestra la lista de cursos 
    @GetMapping
    public ResponseEntity<List<CourseResponse>> listCourses() {
        List<CourseResponse> response = courseService.listCourses().stream()
                .map(CourseResponse::fromModel)
                .toList();
        return ResponseEntity.ok(response);
    }

    //actualiza el estado del curso 
    @PatchMapping("/{id}/status")
    public ResponseEntity<CourseResponse> updateStatus(@PathVariable String id,
                                                         @Valid @RequestBody CourseStatusUpdateRequest request) {
        Course course = courseService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(CourseResponse.fromModel(course));
    }

    //actualiza la prioridad del curso 
    @PatchMapping("/{id}/priority")
    public ResponseEntity<CourseResponse> updatePriority(@PathVariable String id,
                                                           @Valid @RequestBody CoursePriorityUpdateRequest request) {
        Course course = courseService.updatePriority(id, request.getPriority());
        return ResponseEntity.ok(CourseResponse.fromModel(course));
    }


    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCourseNotFound(CourseNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Por favor completa los datos obligatorios para continuar: "
                        + String.join(", ", errors.values())));
    }
}
