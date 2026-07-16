package com.proyecto.codedraft.profile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.proyecto.codedraft.profile.dto.ProfileRequest;
import com.proyecto.codedraft.profile.repositorio.ProfileRepository;

class ProfileServiceTest {

    @Test
    void registerProfileShouldThrowWhenRequiredFieldsAreMissing() {
        ProfileRepository repository = mock(ProfileRepository.class);
        ProfileService service = new ProfileService(repository);

        ProfileRequest request = new ProfileRequest();
        request.setRol("");
        request.setCarrera(" ");
        request.setIntereses(List.of(""));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.registerProfile(request));

        assertEquals("Por favor registra rol, carrera e intereses para continuar.", exception.getMessage());
    }
}
