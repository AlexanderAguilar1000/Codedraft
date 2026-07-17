package com.proyecto.codedraft.course.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Course {

    private String id;
    private String name;
    private String description;
    private CourseStatus status;
    private CoursePriority priority;
    private LocalDate targetDate; //fecha objetivo para completar el curso 
    private int progress;

    public Course() {
    }

    @JsonCreator
    public Course(@JsonProperty("id") String id,
                  @JsonProperty("name") String name,
                  @JsonProperty("description") String description,
                  @JsonProperty("status") CourseStatus status, //no iniciado, en curso, completado
                  @JsonProperty("priority") CoursePriority priority,//alta , media  y baja 
                  @JsonProperty("targetDate") LocalDate targetDate, //fecha objetivo 
                  @JsonProperty("progress") int progress) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.targetDate = targetDate;
        this.progress = progress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public CoursePriority getPriority() {
        return priority;
    }

    public void setPriority(CoursePriority priority) {
        this.priority = priority;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
