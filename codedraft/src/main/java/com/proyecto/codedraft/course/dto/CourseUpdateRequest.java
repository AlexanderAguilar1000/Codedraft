package com.proyecto.codedraft.course.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class CourseUpdateRequest {

    private String status;

    private String priority;

    @Min(value = 0, message = "El progreso debe estar entre 0 y 100")
    @Max(value = 100, message = "El progreso debe estar entre 0 y 100")
    private Integer progress;

    @Future(message = "La fecha objetivo debe ser una fecha futura")
    private LocalDate targetDate;

    public CourseUpdateRequest() {
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

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
}
