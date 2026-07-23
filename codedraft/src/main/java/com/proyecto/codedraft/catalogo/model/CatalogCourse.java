package com.proyecto.codedraft.course.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Modelo que representa un curso del catálogo estático (catalog.json).
 * Incluye roles, careers y tags para el algoritmo de recomendación
 * por puntuación.
 */
public class CatalogCourse {

    private String id;
    private String name;
    private String description;
    private List<String> roles;
    private List<String> careers;
    private List<String> tags;

    public CatalogCourse() {
    }

    @JsonCreator
    public CatalogCourse(@JsonProperty("id") String id,
                          @JsonProperty("name") String name,
                          @JsonProperty("description") String description,
                          @JsonProperty("roles") List<String> roles,
                          @JsonProperty("careers") List<String> careers,
                          @JsonProperty("tags") List<String> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.roles = roles;
        this.careers = careers;
        this.tags = tags;
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
}
