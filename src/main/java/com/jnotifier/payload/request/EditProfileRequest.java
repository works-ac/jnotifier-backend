package com.jnotifier.payload.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class EditProfileRequest {
    @Size(max = 50)
    private String username;

    @NotBlank(message = "Full name is required")
    @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    private String fullname;

    @Pattern(regexp = "^$|^[0-9]{10}$", message = "Mobile number must be 10 digits")
    private String mobile;

    private LocalDate dob;

    @Pattern(regexp = "^(M|F|T)$", message = "Gender must be M, F, or T")
    private String gender;

    @Pattern(regexp = "^$|^(GEN|EWS|OBC|SC|ST)$", message = "Category must be GEN, EWS, OBC, SC or ST.")
    private String category;

    private Boolean isPwd;

    @Pattern(regexp = "^$|^[A-Za-z0-9\\s\\-_().,]{4,64}$", message = "Invalid company name")
    @Size(max = 64, message = "Company name is too long")
    private String companyName;

    @Pattern(regexp = "^$|^[A-Za-z0-9\\s\\-_().,#]{4,128}$", message = "Invalid address")
    @Size(max = 128, message = "Address is too long")
    private String address;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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
