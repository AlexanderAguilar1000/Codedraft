package com.proyecto.codedraft.course.dto;

import java.time.LocalDate;

import com.proyecto.codedraft.course.model.Course;
import com.proyecto.codedraft.course.model.CoursePriority;
import com.proyecto.codedraft.course.model.CourseStatus;

public class CourseResponse {

    private String id;
    private String name;
    private String description;
    private CourseStatus status;
    private CoursePriority priority;
    private LocalDate targetDate;
    private int progress;

    public CourseResponse() {
    }

    public CourseResponse(String id, String name, String description, CourseStatus status,
                           CoursePriority priority, LocalDate targetDate, int progress) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.targetDate = targetDate;
        this.progress = progress;
    }

    public static CourseResponse fromModel(Course course) {
        return new CourseResponse(course.getId(), course.getName(), course.getDescription(),
                course.getStatus(), course.getPriority(), course.getTargetDate(), course.getProgress());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public CoursePriority getPriority() {
        return priority;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public int getProgress() {
        return progress;
    }
}
