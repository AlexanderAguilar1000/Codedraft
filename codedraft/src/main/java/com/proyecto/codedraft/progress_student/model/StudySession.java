package com.proyecto.codedraft.progress_student.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StudySession {

    private String id;
    private String courseId;
    private LocalDate date;
    private int duration; // -1 = menos de 1 hora, 1 = 1 hora, 2 = mas de 1 hora
    private String notes;
    private int progressAdded;
    private int experiencePoints; // total acumulado del perfil despues de esta sesion
    private LocalDateTime createdAt;

    public StudySession() {
    }

    @JsonCreator
    public StudySession(@JsonProperty("id") String id,
                         @JsonProperty("courseId") String courseId,
                         @JsonProperty("date") LocalDate date,
                         @JsonProperty("duration") int duration,
                         @JsonProperty("notes") String notes,
                         @JsonProperty("progressAdded") int progressAdded,
                         @JsonProperty("experiencePoints") int experiencePoints,
                         @JsonProperty("createdAt") LocalDateTime createdAt) {
        this.id = id;
        this.courseId = courseId;
        this.date = date;
        this.duration = duration;
        this.notes = notes;
        this.progressAdded = progressAdded;
        this.experiencePoints = experiencePoints;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getProgressAdded() {
        return progressAdded;
    }

    public void setProgressAdded(int progressAdded) {
        this.progressAdded = progressAdded;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
