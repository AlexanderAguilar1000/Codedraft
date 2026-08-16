package com.proyecto.codedraft.course.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.proyecto.codedraft.course.dto.CoursePriorityUpdateRequest;
import com.proyecto.codedraft.course.dto.CourseRecommendationResponse;
import com.proyecto.codedraft.course.dto.CourseRequest;
import com.proyecto.codedraft.course.dto.CourseResponse;
import com.proyecto.codedraft.course.dto.CourseSearchRequest;
import com.proyecto.codedraft.course.dto.CourseStatsResponse;
import com.proyecto.codedraft.course.dto.CourseStatusUpdateRequest;
import com.proyecto.codedraft.course.dto.CourseUpdateRequest;
import com.proyecto.codedraft.course.dto.ProgressUpdateRequest;
import com.proyecto.codedraft.course.dto.SuggestedCourseResponse;
import com.proyecto.codedraft.course.dto.TargetDateUpdateRequest;
import com.proyecto.codedraft.course.model.Course;
import com.proyecto.codedraft.course.service.CatalogService;
import com.proyecto.codedraft.course.service.CourseNotFoundException;
import com.proyecto.codedraft.course.service.CourseService;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:5173")
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
        return ResponseEntity.status(HttpStatus.CREATED).body(CourseResponse.fromModel(course)); // esto devuelve el codigo de estado 201  , el json que devuelve el servicio
    }

    //muestra la lista de cursos 
    @GetMapping
    public ResponseEntity<?> listCourses() {
        List<Course> courses = courseService.listCourses();
        
        if (courses == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al obtener la lista de cursos"));
        }
        
        if (courses.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "No hay cursos registrados"));
        }
        
        List<CourseResponse> response = courses.stream()
                .map(CourseResponse::fromModel)
                .toList();
        return ResponseEntity.ok(response);
    }

    //busca cursos por filtros (nombre, status, priority, rango de progreso)
    @GetMapping("/search")
    public ResponseEntity<List<CourseResponse>> searchCourses(CourseSearchRequest request) {
        List<Course> courses = courseService.searchCourses(
                request.getName(),
                request.getStatus(),
                request.getPriority(),
                request.getMinProgress(),
                request.getMaxProgress()
        );
        
        List<CourseResponse> response = courses.stream()
                .map(CourseResponse::fromModel)
                .toList();
        return ResponseEntity.ok(response);
    }

    //obtiene estadísticas de los cursos (total, por estado, por prioridad, promedio de progreso)
    @GetMapping("/stats")
    public ResponseEntity<CourseStatsResponse> getCourseStats() {
        CourseStatsResponse stats = courseService.getCourseStats();
        return ResponseEntity.ok(stats);
    }

    //obtiene el detalle de un curso por ID
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable String id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok(CourseResponse.fromModel(course));
    }

    //actualiza el estado del curso 
    // NO_INICIADO,
    //EN_CURSO,
    //COMPLETADO
    @PatchMapping("/{id}/status")
    public ResponseEntity<CourseResponse> updateStatus(@PathVariable String id,
                                                         @Valid @RequestBody CourseStatusUpdateRequest request) {
        Course course = courseService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(CourseResponse.fromModel(course));
    }

    //actualiza la prioridad del curso  (ALTA , MEDIA  Y BAJA )
    @PatchMapping("/{id}/priority")
    public ResponseEntity<CourseResponse> updatePriority(@PathVariable String id,
                                                           @Valid @RequestBody CoursePriorityUpdateRequest request) {
        Course course = courseService.updatePriority(id, request.getPriority());
        return ResponseEntity.ok(CourseResponse.fromModel(course));
    }

    //actualiza el progreso del curso
    @PatchMapping("/{id}/progress")
    public ResponseEntity<CourseResponse> updateProgress(@PathVariable String id,
                                                          @Valid @RequestBody ProgressUpdateRequest request) {
        Course course = courseService.updateProgress(id, request.getProgress());
        return ResponseEntity.ok(CourseResponse.fromModel(course));
    }

    //actualiza la fecha objetivo del curso
    @PatchMapping("/{id}/target-date")
    public ResponseEntity<CourseResponse> updateTargetDate(@PathVariable String id,
                                                             @Valid @RequestBody TargetDateUpdateRequest request) {
        Course course = courseService.updateTargetDate(id, request.getTargetDate());
        return ResponseEntity.ok(CourseResponse.fromModel(course));
    }

    //actualiza múltiples campos de un curso (name, description, priority, targetDate)
    @PatchMapping("/{id}/update")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable String id,
                                                        @Valid @RequestBody CourseUpdateRequest request) {
        Course course = courseService.updateCourse(
                id,
                request.getName(),
                request.getDescription(),
                request.getPriority(),
                request.getTargetDate()
        );
        return ResponseEntity.ok(CourseResponse.fromModel(course));
    }

    //elimina un curso  aqui
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    //obtiene el siguiente curso recomendado a estudiar (AQUI )
    @GetMapping("/recommendation")
    public ResponseEntity<CourseRecommendationResponse> getRecommendation() {
        Optional<Course> recommended = courseService.getRecommendation();
        CourseRecommendationResponse response = recommended
                .map(course -> CourseRecommendationResponse.of(CourseResponse.fromModel(course)))
                .orElseGet(() -> CourseRecommendationResponse.empty("No hay cursos pendientes"));
        return ResponseEntity.ok(response);
    }

    

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCourseNotFound(CourseNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(com.proyecto.codedraft.profile.service.ProfileNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProfileNotFound(
            com.proyecto.codedraft.profile.service.ProfileNotFoundException ex) {
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
