package com.proyecto.codedraft.course.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CourseRequest {

    @NotBlank(message = "El nombre del curso es obligatorio")
    private String name;

    private String description;

    private String status;

    private String priority;

    private String targetDate;

    @Min(value = 0, message = "El progreso debe estar entre 0 y 100")
    @Max(value = 100, message = "El progreso debe estar entre 0 y 100")
    private Integer progress;

    public CourseRequest() {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
