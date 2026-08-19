package com.proyecto.codedraft.course.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public class SuggestedCourseRequest {

    @NotBlank(message = "El ID del curso es obligatorio")
    private String id;

    @NotBlank(message = "El nombre del curso es obligatorio")
    private String name;

    private String description;

    private List<String> roles;

    private List<String> careers;

    private List<String> tags;

    public SuggestedCourseRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getCareers() {
        return careers;
    }

    public void setCareers(List<String> careers) {
        this.careers = careers;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void validateNoNullElements() {
        if (roles != null && roles.stream().anyMatch(role -> role == null || role.trim().isEmpty())) {
            throw new IllegalArgumentException("La lista de roles no puede contener elementos nulos o vacíos");
        }
        if (careers != null && careers.stream().anyMatch(career -> career == null || career.trim().isEmpty())) {
            throw new IllegalArgumentException("La lista de carreras no puede contener elementos nulos o vacíos");
        }
        if (tags != null && tags.stream().anyMatch(tag -> tag == null || tag.trim().isEmpty())) {
            throw new IllegalArgumentException("La lista de tags no puede contener elementos nulos o vacíos");
        }
    }
}
