package com.proyecto.codedraft.course.dto;

import jakarta.validation.constraints.NotBlank;

public class CoursePriorityUpdateRequest {

    @NotBlank(message = "La prioridad es obligatoria")
    private String priority;

    public CoursePriorityUpdateRequest() {
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
