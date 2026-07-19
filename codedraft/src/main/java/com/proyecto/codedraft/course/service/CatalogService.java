package com.proyecto.codedraft.course.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.codedraft.course.dto.SuggestedCourseResponse;
import com.proyecto.codedraft.course.model.CatalogCourse;
import com.proyecto.codedraft.course.repositorio.CatalogRepository;
import com.proyecto.codedraft.profile.model.Profile;
import com.proyecto.codedraft.profile.service.ProfileService;

/**
 * Servicio que implementa el algoritmo de recomendación de cursos
 * basado en puntuación.
 *
 * Reglas de puntuación:
 *   - Coincidencia de rol:              +3 puntos
 *   - Coincidencia de carrera:           +1 punto
 *   - Cada interés coincidente (tags):   +2 puntos
 *
 * Si ningún curso obtiene puntaje > 0, se retorna el catálogo
 * completo ordenado alfabéticamente (fallback).
 */
@Service
public class CatalogService {

    private static final int SCORE_ROL = 3;
    private static final int SCORE_CARRERA = 1;
    private static final int SCORE_INTERES = 2;

    private final CatalogRepository catalogRepository;
    private final ProfileService profileService;

    public CatalogService(CatalogRepository catalogRepository, ProfileService profileService) {
        this.catalogRepository = catalogRepository;
        this.profileService = profileService;
    }

    /**
     * Obtiene la lista de cursos sugeridos del catálogo, ordenados según
     * el puntaje de relevancia calculado a partir del perfil del usuario.
     */
    public List<SuggestedCourseResponse> getSuggestedCourses() {
        Profile profile = profileService.getProfile();
        List<CatalogCourse> catalog = catalogRepository.findAll();

        // Calcular puntaje para cada curso
        List<SuggestedCourseResponse> scored = catalog.stream()
                .map(course -> SuggestedCourseResponse.fromCatalogCourse(course, calculateScore(course, profile)))
                .toList();

        // Verificar si al menos un curso tiene puntaje > 0
        boolean hasRelevantCourses = scored.stream().anyMatch(c -> c.getScore() > 0);

        if (hasRelevantCourses) {
            // Ordenar de mayor a menor puntaje
            return scored.stream()
                    .sorted(Comparator.comparingInt(SuggestedCourseResponse::getScore).reversed())
                    .toList();
        } else {
            // Fallback: retornar todo el catálogo ordenado alfabéticamente
            return scored.stream()
                    .sorted(Comparator.comparing(SuggestedCourseResponse::getName, String.CASE_INSENSITIVE_ORDER))
                    .toList();
        }
    }

    /**
     * Calcula el puntaje de un curso del catálogo respecto al perfil del usuario.
     *
     * +3 si el rol del usuario está en la lista de roles del curso.
     * +1 si la carrera del usuario está en la lista de carreras del curso.
     * +2 por cada interés del usuario que coincida con un tag del curso.
     */
    private int calculateScore(CatalogCourse course, Profile profile) {
        int score = 0;

        // Coincidencia de rol (+3)
        if (course.getRoles() != null && profile.getRol() != null) {
            boolean rolMatches = course.getRoles().stream()
                    .anyMatch(role -> role.equalsIgnoreCase(profile.getRol()));
            if (rolMatches) {
                score += SCORE_ROL;
            }
        }

        // Coincidencia de carrera (+1)
        if (course.getCareers() != null && profile.getCarrera() != null) {
            boolean careerMatches = course.getCareers().stream()
                    .anyMatch(career -> career.equalsIgnoreCase(profile.getCarrera()));
            if (careerMatches) {
                score += SCORE_CARRERA;
            }
        }

        // Coincidencia de intereses (+2 cada uno)
        if (course.getTags() != null && profile.getIntereses() != null) {
            long matchingInterests = profile.getIntereses().stream()
                    .filter(interest -> course.getTags().stream()
                            .anyMatch(tag -> tag.equalsIgnoreCase(interest)))
                    .count();
            score += (int) matchingInterests * SCORE_INTERES;
        }

        return score;
    }
}
