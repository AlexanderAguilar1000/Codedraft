package com.proyecto.codedraft.profile.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Profile {

    private String rol;
    private String carrera;
    private List<String> intereses;
    private int experiencePoints;

    public Profile() {
    }

    @JsonCreator
    public Profile(@JsonProperty("rol") String rol,
                    @JsonProperty("carrera") String carrera,
                    @JsonProperty("intereses") List<String> intereses,
                    @JsonProperty("experiencePoints") int experiencePoints) {
        this.rol = rol;
        this.carrera = carrera;
        this.intereses = intereses;
        this.experiencePoints = experiencePoints;
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

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public void setExperiencePoints(int experiencePoints) {
        this.experiencePoints = experiencePoints;
    }
}
