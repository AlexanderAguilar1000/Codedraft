package com.proyecto.codedraft.profile.repositorio;

import java.io.File;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import tools.jackson.databind.ObjectMapper;
import com.proyecto.codedraft.profile.model.Profile;

@Repository
public class ProfileRepository {

    private final ObjectMapper objectMapper;
    private final File profileFile;
    private final Object lock = new Object();

    public ProfileRepository(ObjectMapper objectMapper,
                              @Value("${app.data.profile-file:data/profile.json}") String profileFilePath) {
        this.objectMapper = objectMapper;
        this.profileFile = new File(profileFilePath);
    }

    public Optional<Profile> findProfile() {
        synchronized (lock) {
            if (!profileFile.exists()) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(profileFile, Profile.class));
        }
    }

    public Profile save(Profile profile) {
        synchronized (lock) {
            File parentDir = profileFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(profileFile, profile);
            return profile;
        }
    }
}
