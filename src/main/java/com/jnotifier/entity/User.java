package com.jnotifier.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        },
        indexes = {
                @Index(name = "idx_users_search_terms", columnList = "email, username"),
                @Index(name = "idx_users_flags", columnList = "is_suspended, is_deleted")
        })
public class User extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    private String username;

    @NotBlank
    @Size(max = 100)
    @Column(name = "fullname")
    private String fullname;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "dob")
    private LocalDate dob;

    @NotBlank
    @Column(name = "gender", columnDefinition = "VARCHAR(1)")
    private String gender;

    @Column(name = "category", columnDefinition = "VARCHAR(3)")
    private String category;

    @Column(name = "company_name", columnDefinition = "VARCHAR(64)")
    private String companyName;

    @Column(name = "address", columnDefinition = "VARCHAR(128)")
    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "is_pwd")
    private Boolean isPwd = false;

    @Column(name = "is_email_verified")
    private Boolean isEmailVerified = false;

    @Column(name = "is_suspended")
    private Boolean isSuspended = false;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public User() {
    }

    public User(String fullname, String email, String password, LocalDate dob, String gender, String mobile, String category, Boolean isPwd, Boolean isEmailVerified) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.gender = gender;
        this.mobile = mobile;
        this.category = category;
        this.isPwd = isPwd;
        this.isEmailVerified = isEmailVerified;
        this.isSuspended = false;
        this.isDeleted = false;
    }

    public User(String fullname, String email, String password, LocalDate dob, String gender, String mobile, Boolean isEmailVerified,
                String companyName, String address) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.dob = dob;
        this.isPwd = null;
        this.gender = gender;
        this.mobile = mobile;
        this.isEmailVerified = isEmailVerified;
        this.companyName = companyName;
        this.address = address;
        this.isSuspended = false;
        this.isDeleted = false;
    }

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
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

    public Boolean getIsSuspended() {
        return isSuspended;
    }

    public void setIsSuspended(Boolean isSuspended) {
        this.isSuspended = isSuspended;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
