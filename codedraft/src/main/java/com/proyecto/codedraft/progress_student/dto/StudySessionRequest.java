package com.proyecto.codedraft.progress_student.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StudySessionRequest {

    @NotBlank(message = "El id del curso es obligatorio")
    private String courseId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    // -1 = menos de 1 hora, 1 = 1 hora, 2 = mas de 1 hora
    @NotNull(message = "La duración es obligatoria")
    private Integer duration;

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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
