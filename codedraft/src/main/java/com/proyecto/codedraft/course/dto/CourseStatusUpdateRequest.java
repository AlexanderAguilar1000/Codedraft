package com.proyecto.codedraft.course.dto;

import jakarta.validation.constraints.NotBlank;

public class CourseStatusUpdateRequest {

    @NotBlank(message = "El estado es obligatorio")
    private String status;

    public CourseStatusUpdateRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
