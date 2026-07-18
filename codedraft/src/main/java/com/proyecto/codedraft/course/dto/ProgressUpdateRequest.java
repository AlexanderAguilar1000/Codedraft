package com.proyecto.codedraft.course.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ProgressUpdateRequest {

    @Min(value = 0, message = "El progreso debe estar entre 0 y 100")
    @Max(value = 100, message = "El progreso debe estar entre 0 y 100")
    private Integer progress;

    public ProgressUpdateRequest() {
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
