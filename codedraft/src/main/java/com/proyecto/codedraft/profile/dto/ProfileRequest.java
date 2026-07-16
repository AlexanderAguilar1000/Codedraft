package com.proyecto.codedraft.profile.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class ProfileRequest {

    @NotBlank(message = "El rol es obligatorio")
    private String rol;

    @NotBlank(message = "La carrera es obligatoria")
    private String carrera;

    @NotEmpty(message = "Debe indicar al menos un interes")
    private List<@NotBlank(message = "El interes no puede estar vacio") String> intereses;

    public ProfileRequest() {
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public List<String> getIntereses() {
        return intereses;
    }

    public void setIntereses(List<String> intereses) {
        this.intereses = intereses;
    }
}
