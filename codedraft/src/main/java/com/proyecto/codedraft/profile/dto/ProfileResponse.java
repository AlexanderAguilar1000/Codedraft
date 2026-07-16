package com.proyecto.codedraft.profile.dto;

import java.util.List;

import com.proyecto.codedraft.profile.model.Profile;

public class ProfileResponse {

    private String rol;
    private String carrera;
    private List<String> intereses;
    private int experiencePoints;

    public ProfileResponse() {
    }

    public ProfileResponse(String rol, String carrera, List<String> intereses, int experiencePoints) {
        this.rol = rol;
        this.carrera = carrera;
        this.intereses = intereses;
        this.experiencePoints = experiencePoints;
    }

    public static ProfileResponse fromModel(Profile profile) {
        return new ProfileResponse(profile.getRol(), profile.getCarrera(), profile.getIntereses(),
                profile.getExperiencePoints());
    }

    public String getRol() {
        return rol;
    }

    public String getCarrera() {
        return carrera;
    }

    public List<String> getIntereses() {
        return intereses;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }
}
