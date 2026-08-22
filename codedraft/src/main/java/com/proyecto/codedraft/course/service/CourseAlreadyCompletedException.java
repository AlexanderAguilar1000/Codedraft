package com.proyecto.codedraft.course.service;

public class CourseAlreadyCompletedException extends RuntimeException {

    public CourseAlreadyCompletedException(String message) {
        super(message);
    }
}
