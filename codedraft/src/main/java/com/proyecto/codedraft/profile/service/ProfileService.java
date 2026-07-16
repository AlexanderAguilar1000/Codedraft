package com.proyecto.codedraft.profile.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.proyecto.codedraft.profile.dto.ProfileRequest;
import com.proyecto.codedraft.profile.model.Profile;
import com.proyecto.codedraft.profile.repositorio.ProfileRepository;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile registerProfile(ProfileRequest request) {
        validateRequiredFields(request);

        Profile profile = new Profile(request.getRol(), request.getCarrera(), request.getIntereses(), 0);
        return profileRepository.save(profile);
    }

    public Profile getProfile() {
        return profileRepository.findProfile()
                .orElseThrow(() -> new ProfileNotFoundException("Aun no se ha registrado un perfil de usuario"));
    }

    private void validateRequiredFields(ProfileRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getRol())
                || !StringUtils.hasText(request.getCarrera())
                || request.getIntereses() == null
                || request.getIntereses().isEmpty()
                || request.getIntereses().stream().anyMatch(interest -> !StringUtils.hasText(interest))) {
            throw new IllegalArgumentException("Por favor registra rol, carrera e intereses para continuar.");
        }
    }
}
