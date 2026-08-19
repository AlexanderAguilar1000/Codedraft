package com.proyecto.codedraft.catalogo.controller;

import com.proyecto.codedraft.course.dto.SuggestedCourseRequest;
import com.proyecto.codedraft.course.dto.SuggestedCourseResponse;
import com.proyecto.codedraft.course.service.CatalogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@CrossOrigin(origins = "http://localhost:5173")
@Validated
public class CatalogoController {




    private final CatalogService catalogService;
    
    public CatalogoController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }
    
    //obtiene los cursos sugeridos del catálogo según el perfil del usuario
    //utiliza el algoritmo de puntuación: rol (+3), carrera (+1), interés (+2 cada uno)
    //AQUI  RECOMIENDA CURSOS AL USUARIO EN BASE A SU PERFIL E INTERESE 
    @GetMapping("/suggested") //ultimo    
    public ResponseEntity<List<SuggestedCourseResponse>> getSuggestedCourses() {
        List<SuggestedCourseResponse> suggested = catalogService.getSuggestedCourses();
        return ResponseEntity.ok(suggested);
    }

    //elimina un curso de la lista de cursos sugeridos por su ID
    @DeleteMapping("/suggested/{courseId}")
    public ResponseEntity<Void> removeSuggestedCourse(@PathVariable @NotBlank String courseId) {
        catalogService.removeSuggestedCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    //agrega un nuevo curso a la lista de cursos sugeridos
    @PostMapping("/suggested")
    public ResponseEntity<SuggestedCourseResponse> addSuggestedCourse(@Valid @RequestBody SuggestedCourseRequest request) {
        catalogService.addSuggestedCourse(request);
        SuggestedCourseResponse response = new SuggestedCourseResponse(
                request.getId(),
                request.getName(),
                request.getDescription(),
                request.getRoles(),
                request.getCareers(),
                request.getTags(),
                0
        );
        return ResponseEntity.ok(response);
    }





}