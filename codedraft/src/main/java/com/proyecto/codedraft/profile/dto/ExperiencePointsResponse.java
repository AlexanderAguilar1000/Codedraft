package com.proyecto.codedraft.profile.dto;

import com.proyecto.codedraft.profile.model.Profile;

public class ExperiencePointsResponse {

    private int experiencePoints;

    public ExperiencePointsResponse() {
    }

    public ExperiencePointsResponse(int experiencePoints) {
        this.experiencePoints = experiencePoints;
    }

    public static ExperiencePointsResponse fromModel(Profile profile) {
        return new ExperiencePointsResponse(profile.getExperiencePoints());
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }
}
