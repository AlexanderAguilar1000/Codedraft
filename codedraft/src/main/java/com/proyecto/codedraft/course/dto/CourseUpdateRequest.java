package com.proyecto.codedraft.course.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;

public class CourseUpdateRequest {

    private String name;

    private String description;

    private String priority;

    @Future(message = "La fecha objetivo debe ser una fecha futura")
    private LocalDate targetDate;

    public CourseUpdateRequest() {
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
}
