package com.proyecto.codedraft.course.dto;

import java.util.List;

import com.proyecto.codedraft.course.model.CatalogCourse;

/**
 * DTO de respuesta para cursos sugeridos.
 * Incluye el puntaje de relevancia calculado por el algoritmo de recomendación.
 */
public class SuggestedCourseResponse {

    private String id;
    private String name;
    private String description;
    private List<String> roles;
    private List<String> careers;
    private List<String> tags;
    private int score;

    public SuggestedCourseResponse() {
    }

    public SuggestedCourseResponse(String id, String name, String description,
                                    List<String> roles, List<String> careers,
                                    List<String> tags, int score) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.roles = roles;
        this.careers = careers;
        this.tags = tags;
        this.score = score;
    }

    public static SuggestedCourseResponse fromCatalogCourse(CatalogCourse course, int score) {
        return new SuggestedCourseResponse(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getRoles(),
                course.getCareers(),
                course.getTags(),
                score
        );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getCareers() {
        return careers;
    }

    public List<String> getTags() {
        return tags;
    }

    public int getScore() {
        return score;
    }
}
