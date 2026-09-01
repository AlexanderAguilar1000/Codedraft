package com.proyecto.codedraft.IA_Message.dto;

// Informacion del curso de la sesion (no se envia el catalogo completo, solo el curso correspondiente)
public class CoachCourseContext {

    private String name;
    private String description;

    public CoachCourseContext() {
    }

    public CoachCourseContext(String name, String description) {
        this.name = name;
        this.description = description;
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
}
