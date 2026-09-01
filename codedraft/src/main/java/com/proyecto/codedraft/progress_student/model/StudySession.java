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

    // Feedback del Mentor IA para esta sesion. Quedan en null si el servicio de IA no respondio.
    private String mentorCharacter;
    private Boolean mentorValid;
    private String mentorMessage;
    private String mentorWhyItMatters;
    private String mentorRealWorldUse;
    private String mentorChallenge;

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
                         @JsonProperty("createdAt") LocalDateTime createdAt,
                         @JsonProperty("mentorCharacter") String mentorCharacter,
                         @JsonProperty("mentorValid") Boolean mentorValid,
                         @JsonProperty("mentorMessage") String mentorMessage,
                         @JsonProperty("mentorWhyItMatters") String mentorWhyItMatters,
                         @JsonProperty("mentorRealWorldUse") String mentorRealWorldUse,
                         @JsonProperty("mentorChallenge") String mentorChallenge) {
        this.id = id;
        this.courseId = courseId;
        this.date = date;
        this.duration = duration;
        this.notes = notes;
        this.progressAdded = progressAdded;
        this.experiencePoints = experiencePoints;
        this.createdAt = createdAt;
        this.mentorCharacter = mentorCharacter;
        this.mentorValid = mentorValid;
        this.mentorMessage = mentorMessage;
        this.mentorWhyItMatters = mentorWhyItMatters;
        this.mentorRealWorldUse = mentorRealWorldUse;
        this.mentorChallenge = mentorChallenge;
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

    public String getMentorCharacter() {
        return mentorCharacter;
    }

    public void setMentorCharacter(String mentorCharacter) {
        this.mentorCharacter = mentorCharacter;
    }

    public Boolean getMentorValid() {
        return mentorValid;
    }

    public void setMentorValid(Boolean mentorValid) {
        this.mentorValid = mentorValid;
    }

    public String getMentorMessage() {
        return mentorMessage;
    }

    public void setMentorMessage(String mentorMessage) {
        this.mentorMessage = mentorMessage;
    }

    public String getMentorWhyItMatters() {
        return mentorWhyItMatters;
    }

    public void setMentorWhyItMatters(String mentorWhyItMatters) {
        this.mentorWhyItMatters = mentorWhyItMatters;
    }

    public String getMentorRealWorldUse() {
        return mentorRealWorldUse;
    }

    public void setMentorRealWorldUse(String mentorRealWorldUse) {
        this.mentorRealWorldUse = mentorRealWorldUse;
    }

    public String getMentorChallenge() {
        return mentorChallenge;
    }

    public void setMentorChallenge(String mentorChallenge) {
        this.mentorChallenge = mentorChallenge;
    }
}
