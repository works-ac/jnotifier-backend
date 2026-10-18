package com.jnotifier.payload.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class JobsListingsPojo {
    private Long applicationId;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private String title;
    private String shortDescription;
    private String createdBy;
    private LocalDateTime createdAt;
    private String tags;
    private Boolean status;
    private String advNo;

    public JobsListingsPojo() {
    }

    public JobsListingsPojo(Long applicationId, LocalDate applicationStartDate, LocalDate applicationEndDate, String title, String shortDescription, String createdBy,
                            LocalDateTime createdAt, String tags, Boolean status, String advNo) {
        this.applicationId = applicationId;
        this.applicationStartDate = applicationStartDate;
        this.applicationEndDate = applicationEndDate;
        this.title = title;
        this.shortDescription = shortDescription;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.tags = tags;
        this.status = status;
        this.advNo = advNo;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public LocalDate getApplicationStartDate() {
        return applicationStartDate;
    }

    public void setApplicationStartDate(LocalDate applicationStartDate) {
        this.applicationStartDate = applicationStartDate;
    }

    public LocalDate getApplicationEndDate() {
        return applicationEndDate;
    }

    public void setApplicationEndDate(LocalDate applicationEndDate) {
        this.applicationEndDate = applicationEndDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getAdvNo() {
        return advNo;
    }

    public void setAdvNo(String advNo) {
        this.advNo = advNo;
    }
}
