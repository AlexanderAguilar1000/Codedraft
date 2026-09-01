package com.proyecto.codedraft.IA_Message.dto;

// Cuerpo enviado a POST /coach-message del servicio de IA (Flask)
public class CoachMessageRequest {

    private CoachUserContext user;
    private CoachCourseContext course;
    private int progress;
    private CoachStudySessionContext studySession;

    public CoachMessageRequest() {
    }

    public CoachMessageRequest(CoachUserContext user, CoachCourseContext course, int progress,
                                CoachStudySessionContext studySession) {
        this.user = user;
        this.course = course;
        this.progress = progress;
        this.studySession = studySession;
    }

    public CoachUserContext getUser() {
        return user;
    }

    public void setUser(CoachUserContext user) {
        this.user = user;
    }

    public CoachCourseContext getCourse() {
        return course;
    }

    public void setCourse(CoachCourseContext course) {
        this.course = course;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public CoachStudySessionContext getStudySession() {
        return studySession;
    }

    public void setStudySession(CoachStudySessionContext studySession) {
        this.studySession = studySession;
    }
}
