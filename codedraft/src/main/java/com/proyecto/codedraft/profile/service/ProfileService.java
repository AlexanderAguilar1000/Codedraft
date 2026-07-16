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
        //creo un nuevo objeto  profile con los datos del request y lo guardo en la base de datos
        Profile profile = new Profile(request.getRol(), request.getCarrera(), request.getIntereses(), 0);
        return profileRepository.save(profile);
    }

    public Profile getProfile() {
        //busco el perfil en la base de datos y si no lo encuentra lanza una excepcion
        return profileRepository.findProfile()
                .orElseThrow(() -> new ProfileNotFoundException("Aun no se ha registrado un perfil de usuario"));
    }
}
