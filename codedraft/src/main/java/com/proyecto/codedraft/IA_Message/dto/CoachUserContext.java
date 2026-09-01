package com.proyecto.codedraft.IA_Message.dto;

import java.util.List;

// Contexto del estudiante que se envia al servicio de IA para personalizar el feedback
public class CoachUserContext {

    private String rol;
    private String carrera;
    private List<String> intereses;

    public CoachUserContext() {
    }

    public CoachUserContext(String rol, String carrera, List<String> intereses) {
        this.rol = rol;
        this.carrera = carrera;
        this.intereses = intereses;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public List<String> getIntereses() {
        return intereses;
    }

    public void setIntereses(List<String> intereses) {
        this.intereses = intereses;
    }
}
