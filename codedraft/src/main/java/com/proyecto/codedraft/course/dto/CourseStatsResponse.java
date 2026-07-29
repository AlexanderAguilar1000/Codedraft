package com.proyecto.codedraft.course.dto;

public class CourseStatsResponse {

    private int totalCourses;
    private int notStarted;
    private int inProgress;
    private int completed;
    private int highPriority;
    private int mediumPriority;
    private int lowPriority;
    private double averageProgress;

    public CourseStatsResponse() {
    }

    public CourseStatsResponse(int totalCourses, int notStarted, int inProgress, int completed,
                              int highPriority, int mediumPriority, int lowPriority, double averageProgress) {
        this.totalCourses = totalCourses;
        this.notStarted = notStarted;
        this.inProgress = inProgress;
        this.completed = completed;
        this.highPriority = highPriority;
        this.mediumPriority = mediumPriority;
        this.lowPriority = lowPriority;
        this.averageProgress = averageProgress;
    }

    public int getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(int totalCourses) {
        this.totalCourses = totalCourses;
    }

    public int getNotStarted() {
        return notStarted;
    }

    public void setNotStarted(int notStarted) {
        this.notStarted = notStarted;
    }

    public int getInProgress() {
        return inProgress;
    }

    public void setInProgress(int inProgress) {
        this.inProgress = inProgress;
    }

    public int getCompleted() {
        return completed;
    }

    public void setCompleted(int completed) {
        this.completed = completed;
    }

    public int getHighPriority() {
        return highPriority;
    }

    public void setHighPriority(int highPriority) {
        this.highPriority = highPriority;
    }

    public int getMediumPriority() {
        return mediumPriority;
    }

    public void setMediumPriority(int mediumPriority) {
        this.mediumPriority = mediumPriority;
    }

    public int getLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(int lowPriority) {
        this.lowPriority = lowPriority;
    }

    public double getAverageProgress() {
        return averageProgress;
    }

    public void setAverageProgress(double averageProgress) {
        this.averageProgress = averageProgress;
    }
}
