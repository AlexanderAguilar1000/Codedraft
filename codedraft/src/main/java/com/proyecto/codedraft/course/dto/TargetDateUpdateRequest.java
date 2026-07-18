package com.proyecto.codedraft.course.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public class TargetDateUpdateRequest {

    @NotNull(message = "La fecha objetivo es obligatoria")
    @Future(message = "La fecha objetivo debe ser una fecha futura")
    private LocalDate targetDate;

    public TargetDateUpdateRequest() {
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
}
