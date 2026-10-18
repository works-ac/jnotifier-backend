package com.jnotifier.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EmailVerifyRequest {
    @NotBlank(message = "An email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Invalid email")
    private String email;

    @Size(max = 32, message = "Too large password")
    private String password;

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
}
