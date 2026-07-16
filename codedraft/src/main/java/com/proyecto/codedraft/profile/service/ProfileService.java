package com.proyecto.codedraft.profile.service;

import org.springframework.stereotype.Service;

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
        Profile profile = new Profile(request.getRol(), request.getCarrera(), request.getIntereses(), 0);
        return profileRepository.save(profile);
    }

    public Profile getProfile() {
        return profileRepository.findProfile()
                .orElseThrow(() -> new ProfileNotFoundException("Aun no se ha registrado un perfil de usuario"));
    }
}
