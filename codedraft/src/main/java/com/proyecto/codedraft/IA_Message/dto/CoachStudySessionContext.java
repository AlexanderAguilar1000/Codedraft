package com.proyecto.codedraft.IA_Message.dto;

// Informacion de la sesion de estudio que el LLM debe analizar
public class CoachStudySessionContext {

    private int durationMinutes;
    private String notes;

    public CoachStudySessionContext() {
    }

    public CoachStudySessionContext(int durationMinutes, String notes) {
        this.durationMinutes = durationMinutes;
        this.notes = notes;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
