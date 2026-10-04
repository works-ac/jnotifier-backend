package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotNull;

public class UpdateJobCategoryRequest extends AddNewJobCategoryRequest {
    @NotNull(message = "Job category id is required")
    private Long jobCategoryId;

    private Boolean isActive;
    private Boolean isDeleted;

    public UpdateJobCategoryRequest() {
    }

    public UpdateJobCategoryRequest(Long jobCategoryId, String jobCategoryName, Boolean isActive, Boolean isDeleted) {
        super(jobCategoryName);
        this.jobCategoryId = jobCategoryId;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
    }

    public Long getJobCategoryId() {
        return jobCategoryId;
    }

    public void setJobCategoryId(Long jobCategoryId) {
        this.jobCategoryId = jobCategoryId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
