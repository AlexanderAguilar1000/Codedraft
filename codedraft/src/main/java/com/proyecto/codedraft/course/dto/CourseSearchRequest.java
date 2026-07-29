package com.proyecto.codedraft.course.dto;

public class CourseSearchRequest {

    private String name;
    private String status;
    private String priority;
    private Integer minProgress;
    private Integer maxProgress;

    public CourseSearchRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Integer getMinProgress() {
        return minProgress;
    }

    public void setMinProgress(Integer minProgress) {
        this.minProgress = minProgress;
    }

    public Integer getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(Integer maxProgress) {
        this.maxProgress = maxProgress;
    }
}
