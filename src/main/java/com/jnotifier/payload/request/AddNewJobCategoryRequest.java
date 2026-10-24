package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotNull;

public class AddNewJobCategoryRequest {
    @NotNull(message = "Job category name is required")
    private String jobCategoryName;

    private String captchaId;
    private String captcha;

    public AddNewJobCategoryRequest() {
    }

    public AddNewJobCategoryRequest(String jobCategoryName) {
        this.jobCategoryName = jobCategoryName;
    }

    public AddNewJobCategoryRequest(String jobCategoryName, String captchaId, String captcha) {
        this.jobCategoryName = jobCategoryName;
        this.captchaId = captchaId;
        this.captcha = captcha;
    }

    public String getJobCategoryName() {
        return jobCategoryName;
    }

    public void setJobCategoryName(String jobCategoryName) {
        this.jobCategoryName = jobCategoryName;
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

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
