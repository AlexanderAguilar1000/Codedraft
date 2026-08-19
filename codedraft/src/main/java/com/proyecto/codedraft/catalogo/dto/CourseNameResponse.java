package com.proyecto.codedraft.catalogo.dto;

/**
 * DTO simplificado para mostrar solo el nombre de un curso sugerido.
 * Utilizado en el selector de cursos al momento de crear un nuevo curso.
 */
public class CourseNameResponse {

    private String name;

    public CourseNameResponse() {
    }

    public CourseNameResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
