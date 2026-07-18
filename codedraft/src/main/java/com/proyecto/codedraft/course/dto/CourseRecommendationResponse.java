package com.proyecto.codedraft.course.dto;

public class CourseRecommendationResponse {

    private CourseResponse course;
    private String message;

    public CourseRecommendationResponse() {
    }

    private CourseRecommendationResponse(CourseResponse course, String message) {
        this.course = course;
        this.message = message;
    }

    //hay un curso recomendado
    public static CourseRecommendationResponse of(CourseResponse course) {
        return new CourseRecommendationResponse(course, "");
    }

    //no hay cursos pendientes por recomendar
    public static CourseRecommendationResponse empty(String message) {
        return new CourseRecommendationResponse(null, message);
    }

    public CourseResponse getCourse() {
        return course;
    }

    public String getMessage() {
        return message;
    }
}
