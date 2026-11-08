package com.fittrack.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "Le prenom est obligatoire.")
    @Size(max = 80, message = "80 caracteres maximum.")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 80, message = "80 caracteres maximum.")
    private String lastName;

    @NotBlank(message = "L'adresse e-mail est obligatoire.")
    @Email(message = "Adresse e-mail invalide.")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 8, max = 72, message = "Le mot de passe doit contenir entre 8 et 72 caracteres.")
    private String password;

    @NotBlank(message = "Confirmez le mot de passe.")
    private String confirmPassword;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
