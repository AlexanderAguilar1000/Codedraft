package com.proyecto.codedraft.progress_student.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;

public class StudySessionRequest {

    @NotBlank(message = "El id del curso es obligatorio")
    private String courseId;

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate date;

    // -1 = menos de 1 hora, 1 = 1 hora, 2 = mas de 1 hora
    @NotNull(message = "La duración es obligatoria")
    @Pattern(regexp = "-1|1|2", message = "La duración debe ser -1 (menos de 1 hora), 1 (1 hora) o 2 (más de 1 hora)")
    private String duration;

    private String notes;

    public StudySessionRequest() {
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
