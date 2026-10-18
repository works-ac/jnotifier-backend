package com.jnotifier.payload.response;

public class UserDetailsResponse {
    private Long id;
    private String fullname;
    private String username;
    private String email;
    private String mobile;
    private String gender;
    private String address;
    private String dob;
    private String category;
    private Boolean isPwd;
    private String companyName;
    private String roleName;
    private Boolean isSuspended;
    private Boolean isDeleted;
    private Boolean isEmailVerified;

    public UserDetailsResponse() {
    }

    public UserDetailsResponse(Long id, String fullname, String username, String email, String mobile, String gender, String address,
                               String dob, String category, Boolean isPwd, String companyName, String roleName,
                               Boolean isSuspended, Boolean isDeleted, Boolean isEmailVerified) {
        this.id = id;
        this.fullname = fullname;
        this.username = username;
        this.email = email;
        this.mobile = mobile;
        this.gender = gender;
        this.address = address;
        this.dob = dob;
        this.category = category;
        this.isPwd = isPwd;
        this.companyName = companyName;
        this.roleName = roleName;
        this.isSuspended = isSuspended;
        this.isDeleted = isDeleted;
        this.isEmailVerified = isEmailVerified;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }
}
