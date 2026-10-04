package com.jnotifier.payload.request;

import java.time.LocalDate;
import jakarta.validation.constraints.*;

public class SignupRequest {
    @NotBlank
    @Size(max = 100)
    private String fullName;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String mobile;

    @NotNull
    private LocalDate dob;

    @Pattern(regexp = "^(M|F|T)$", message = "Gender must be M, F, or T")
    private String gender;

    @Pattern(regexp = "^(GEN|EWS|OBC|SC|ST)$", message = "Category must be gen, ews, obc, sc or st.")
    private String category;

    private Boolean isPwd;


    @Pattern(regexp = "^[A-Za-z0-9\\s\\-_().,]{4,64}$", message = "Invalid company name")
    @Size(min = 4, max = 64, message = "Company name is too long")
    private String companyName;

    @Pattern(regexp = "^[A-Za-z0-9\\s\\-_().,#]{4,128}$", message = "Invalid address")
    @Size(min = 4, max = 128, message = "Address is too long")
    private String address;

    private String role;

    @NotBlank
    private String captchaId;

    @NotBlank
    private String captcha;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCaptchaId() {
        return captchaId;
    }

    public void setCaptchaId(String captchaId) {
        this.captchaId = captchaId;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptchaValue(String captcha) {
        this.captcha = captcha;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getIsPwd() {
        return isPwd;
    }

    public void setIsPwd(Boolean isPwd) {
        this.isPwd = isPwd;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
