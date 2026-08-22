package com.proyecto.codedraft.progress_student.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.proyecto.codedraft.progress_student.model.StudySession;

public class StudySessionResponse {

    private String id;
    private String courseId;
    private LocalDate date;
    private int duration;
    private String notes;
    private int progressAdded;
    private int experiencePoints;
    private LocalDateTime createdAt;

    public StudySessionResponse() {
    }

    public StudySessionResponse(String id, String courseId, LocalDate date, int duration, String notes,
                                 int progressAdded, int experiencePoints, LocalDateTime createdAt) {
        this.id = id;
        this.courseId = courseId;
        this.date = date;
        this.duration = duration;
        this.notes = notes;
        this.progressAdded = progressAdded;
        this.experiencePoints = experiencePoints;
        this.createdAt = createdAt;
    }

    public static StudySessionResponse fromModel(StudySession session) {
        return new StudySessionResponse(session.getId(), session.getCourseId(), session.getDate(),
                session.getDuration(), session.getNotes(), session.getProgressAdded(),
                session.getExperiencePoints(), session.getCreatedAt());
    }

    public String getId() {
        return id;
    }

    public String getCourseId() {
        return courseId;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getDuration() {
        return duration;
    }

    public String getNotes() {
        return notes;
    }

    public int getProgressAdded() {
        return progressAdded;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
