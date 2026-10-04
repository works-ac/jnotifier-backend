package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotBlank;

public class OtpRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String otpCode;

    @NotBlank
    private String verificationType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }
}
