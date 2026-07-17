package com.proyecto.codedraft.profile.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class ProfileRequest {

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "^[^0-9]*$", message = "El rol no debe contener números")
    private String rol;

    @NotBlank(message = "La carrera es obligatoria")
    @Pattern(regexp = "^[^0-9]*$", message = "La carrera no debe contener números")
    private String carrera;

    @NotEmpty(message = "Debe indicar al menos un interes")
    private List<@NotBlank(message = "El interes no puede estar vacio") @Pattern(regexp = "^[^0-9]*$", message = "El interes no debe contener números") String> intereses;

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
