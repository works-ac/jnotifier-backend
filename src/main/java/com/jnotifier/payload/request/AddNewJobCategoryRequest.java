package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotNull;

public class AddNewJobCategoryRequest {
    @NotNull(message = "Job category name is required")
    private String jobCategoryName;

    public AddNewJobCategoryRequest() {
    }

    public AddNewJobCategoryRequest(String jobCategoryName) {
        this.jobCategoryName = jobCategoryName;
    }

    public String getJobCategoryName() {
        return jobCategoryName;
    }

    public void setJobCategoryName(String jobCategoryName) {
        this.jobCategoryName = jobCategoryName;
    }
}
