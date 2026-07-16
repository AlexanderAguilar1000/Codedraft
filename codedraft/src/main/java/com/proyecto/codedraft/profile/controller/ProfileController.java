package com.proyecto.codedraft.profile.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.codedraft.profile.dto.ProfileRequest;
import com.proyecto.codedraft.profile.dto.ProfileResponse;
import com.proyecto.codedraft.profile.model.Profile;
import com.proyecto.codedraft.profile.service.ProfileNotFoundException;
import com.proyecto.codedraft.profile.service.ProfileService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    //service para llamar funciones 
    private final ProfileService profileService;

//constructor para inyectar el servicio
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> registerProfile(@Valid @RequestBody ProfileRequest request) {
        Profile profile = profileService.registerProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProfileResponse.fromModel(profile));
    }

// Este se encarga de la busqueda del perfil, si no lo encuentra lanza una excepcion que es manejada por el metodo handleProfileNotFound
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() {
        Profile profile = profileService.getProfile();
        return ResponseEntity.ok(ProfileResponse.fromModel(profile));
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<String> handleProfileNotFound(ProfileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
