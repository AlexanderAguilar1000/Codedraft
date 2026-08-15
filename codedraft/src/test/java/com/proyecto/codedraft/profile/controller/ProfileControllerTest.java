package com.proyecto.codedraft.profile.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;
import com.proyecto.codedraft.profile.dto.ProfileRequest;
import com.proyecto.codedraft.profile.model.Profile;
import com.proyecto.codedraft.profile.service.ProfileService;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProfileService profileService;

    @Test
    void registerProfileShouldReturnCreatedWhenValid() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setRol("Desarrollador");
        request.setCarrera("Ingeniería de Sistemas");
        request.setIntereses(List.of("Programación", "Base de datos"));

        Profile mockProfile = new Profile("Desarrollador", "Ingeniería de Sistemas", List.of("Programación", "Base de datos"), 0);
        when(profileService.registerProfile(any(ProfileRequest.class))).thenReturn(mockProfile);

        mockMvc.perform(post("/api/profile/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("Desarrollador"))
                .andExpect(jsonPath("$.carrera").value("Ingeniería de Sistemas"));
    }

    @Test
    void registerProfileShouldReturnBadRequestWhenRolContainsNumbers() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setRol("Desarrollador 123");
        request.setCarrera("Ingeniería de Sistemas");
        request.setIntereses(List.of("Programación"));

        mockMvc.perform(post("/api/profile/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("El rol no debe contener números")));
    }

    @Test
    void registerProfileShouldReturnBadRequestWhenCarreraContainsNumbers() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setRol("Desarrollador");
        request.setCarrera("Ingeniería 2");
        request.setIntereses(List.of("Programación"));

        mockMvc.perform(post("/api/profile/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("La carrera no debe contener números")));
    }

    @Test
    void registerProfileShouldReturnBadRequestWhenInteresesContainNumbers() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setRol("Desarrollador");
        request.setCarrera("Ingeniería de Sistemas");
        request.setIntereses(List.of("Programación 101"));

        mockMvc.perform(post("/api/profile/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("El interes no debe contener números")));
    }
}
