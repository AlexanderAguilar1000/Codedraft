package com.proyecto.codedraft.progress_student.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.proyecto.codedraft.course.service.CourseNotFoundException;
import com.proyecto.codedraft.profile.service.ProfileNotFoundException;
import com.proyecto.codedraft.progress_student.dto.StudySessionRequest;
import com.proyecto.codedraft.progress_student.dto.StudySessionResponse;
import com.proyecto.codedraft.progress_student.model.StudySession;
import com.proyecto.codedraft.progress_student.servicio.StudySessionService;

@RestController
@RequestMapping("/api/study-sessions")
@CrossOrigin(origins = "http://localhost:5173")
public class StudySessionController {

    private final StudySessionService studySessionService;

    public StudySessionController(StudySessionService studySessionService) {
        this.studySessionService = studySessionService;
    }

    // registra una sesion de estudio: el backend calcula progressAdded y experiencePoints
    @PostMapping
    public ResponseEntity<StudySessionResponse> registerSession(@Valid @RequestBody StudySessionRequest request) {
        StudySession session = studySessionService.registerSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(StudySessionResponse.fromModel(session));
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCourseNotFound(CourseNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProfileNotFound(ProfileNotFoundException ex) {
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
