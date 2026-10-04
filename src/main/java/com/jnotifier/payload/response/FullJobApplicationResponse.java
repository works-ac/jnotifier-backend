package com.jnotifier.payload.response;

import java.time.LocalDate;

public class FullJobApplicationResponse {
    private String title;
    private String tags;
    private LocalDate applicationStartDate;
    private LocalDate applicationEndDate;
    private String shortDescription;
    private String advNo;
    private long applicationId;
    private String viewPageDescription;
    private String applyLink;
    private String advFilePath;
    private Boolean status;

    public FullJobApplicationResponse() {
    }

    public FullJobApplicationResponse(String title, String tags, LocalDate applicationStartDate, LocalDate applicationEndDate, String shortDescription, String advNo, long applicationId, String viewPageDescription) {
        this.title = title;
        this.tags = tags;
        this.applicationStartDate = applicationStartDate;
        this.applicationEndDate = applicationEndDate;
        this.shortDescription = shortDescription;
        this.advNo = advNo;
        this.applicationId = applicationId;
        this.viewPageDescription = viewPageDescription;
    }

    public FullJobApplicationResponse(String title, String tags, LocalDate applicationStartDate, LocalDate applicationEndDate, String shortDescription, String advNo,
                                      long applicationId, String viewPageDescription, String applyLink, String advFilePath, Boolean status) {
        this.title = title;
        this.tags = tags;
        this.applicationStartDate = applicationStartDate;
        this.applicationEndDate = applicationEndDate;
        this.shortDescription = shortDescription;
        this.advNo = advNo;
        this.applicationId = applicationId;
        this.viewPageDescription = viewPageDescription;
        this.applyLink = applyLink;
        this.advFilePath = advFilePath;
        this.status = status;
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

    public String getAdvFilePath() {
        return advFilePath;
    }

    public void setAdvFilePath(String advFilePath) {
        this.advFilePath = advFilePath;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
