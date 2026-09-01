package com.proyecto.codedraft.IA_Message.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Respuesta de POST /coach-message del servicio de IA (Flask).
// Se ignoran campos desconocidos para no romper la integracion si el servicio de IA agrega campos nuevos.
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoachMessageResponse {

    private String character;
    private boolean valid;
    private String message;
    private String whyItMatters;
    private String realWorldUse;
    private String challenge;

    public CoachMessageResponse() {
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getWhyItMatters() {
        return whyItMatters;
    }

    public void setWhyItMatters(String whyItMatters) {
        this.whyItMatters = whyItMatters;
    }

    public String getRealWorldUse() {
        return realWorldUse;
    }

    public void setRealWorldUse(String realWorldUse) {
        this.realWorldUse = realWorldUse;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }
}
