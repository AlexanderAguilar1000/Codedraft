package com.proyecto.codedraft.catalogo.controller;

import com.proyecto.codedraft.course.dto.SuggestedCourseResponse;
import com.proyecto.codedraft.course.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
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





}