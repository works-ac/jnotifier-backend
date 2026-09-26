package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ApplicationRequest {
    @NotNull(message = "Job application title is required")
    @Size(max = 32, message = "Your job application title is too long")
    private String title;

    @NotNull(message = "Job application tags are required")
    private String tags;

    @NotNull
    @NotNull(message = "Application start date is required")
    private LocalDate applicationStartDate;

    @NotNull(message = "Application end date is required")
    private LocalDate applicationEndDate;

    @Size(max = 100)
    private String advNo;

    @NotNull(message = "Job application's short description is required.")
    @Size(max = 700)
    private String shortDescription;

    private String viewPageDescription;

    @NotNull(message = "Apply link is required")
    @Size(max = 250)
    private String applyLink;

    private Boolean status;

    private String advFileName;

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

    public String getViewPageDescription() {
        return viewPageDescription;
    }

    public void setViewPageDescription(String viewPageDescription) {
        this.viewPageDescription = viewPageDescription;
    }

    public String getApplyLink() {
        return applyLink;
    }

    public void setApplyLink(String applyLink) {
        this.applyLink = applyLink;
    }

    public String getAdvFileName() {
        return advFileName;
    }

    public void setAdvFileName(String advFileName) {
        this.advFileName = advFileName;
    }
}
