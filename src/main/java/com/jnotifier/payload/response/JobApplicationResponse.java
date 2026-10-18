package com.jnotifier.payload.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class JobApplicationResponse {
    private String title;
    private String tags;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private String shortDescription;
    private String advNo;
    private Long applicationId;
    private String createdBy;
    private LocalDateTime createdAt;

    public JobApplicationResponse() {
    }

    public JobApplicationResponse(String title, String tags, LocalDate applicationStartDate, LocalDate applicationEndDate, String shortDescription, String advNo,
                                  Long applicationId) {
        this.title = title;
        this.tags = tags;
        this.applicationStartDate = applicationStartDate;
        this.applicationEndDate = applicationEndDate;
        this.shortDescription = shortDescription;
        this.advNo = advNo;
        this.applicationId = applicationId;
    }

    public JobApplicationResponse(String title, String tags, LocalDate applicationStartDate, LocalDate applicationEndDate, String shortDescription, String advNo,
                                  Long applicationId, String createdBy, LocalDateTime createdAt) {
        this.title = title;
        this.tags = tags;
        this.applicationStartDate = applicationStartDate;
        this.applicationEndDate = applicationEndDate;
        this.shortDescription = shortDescription;
        this.advNo = advNo;
        this.applicationId = applicationId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getAdvNo() {
        return advNo;
    }

    public void setAdvNo(String advNo) {
        this.advNo = advNo;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
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
}
