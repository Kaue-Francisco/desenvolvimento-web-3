package com.comunicacao.api.modelos;

import javax.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "O nome de usuário é obrigatório")
    private String username;

    @NotBlank(message = "A senha é obrigatória")
    private String password;

    // Getters e Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Método toString (opcional)
    @Override
    public String toString() {
        return "LoginRequest{username='" + username + "', password='" + password + "'}";
    }
}
