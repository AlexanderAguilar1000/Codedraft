package com.proyecto.codedraft.profile.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.proyecto.codedraft.profile.dto.ExperiencePointsResponse;
import com.proyecto.codedraft.profile.dto.ProfileRequest;
import com.proyecto.codedraft.profile.dto.ProfileResponse;
import com.proyecto.codedraft.profile.model.Profile;
import com.proyecto.codedraft.profile.service.ProfileNotFoundException;
import com.proyecto.codedraft.profile.service.ProfileService;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class ProfileController {

    //service para llamar funciones 
    private final ProfileService profileService;

//constructor para inyectar el servicio
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/register")
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

    //consulta los puntos de experiencia acumulados por el usuario
    @GetMapping("/points")
    public ResponseEntity<ExperiencePointsResponse> getExperiencePoints() {
        Profile profile = profileService.getProfile();
        return ResponseEntity.ok(ExperiencePointsResponse.fromModel(profile));
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<String> handleProfileNotFound(ProfileNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }

// este es un metodo que se encarga de manejar las excepciones automaticamente  si detecta que esta vacio el interes 
//rol o carrera o la lista de intereses devuelve un mensaje de error con el campo que esta vacio
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Por favor completa los datos obligatorios para continuar: " + String.join(", ", errors.values())));
    }

    //AGREGAR VALIDACION PARA QUE NO PERMITA INGRESO DE NUMEROS EN LA ENTRADA DE ROL , CARRE E INTERESES 
    
}
