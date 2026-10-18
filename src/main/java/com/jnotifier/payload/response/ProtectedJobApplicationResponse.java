package com.jnotifier.payload.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProtectedJobApplicationResponse extends JobApplicationResponse {
    private LocalDateTime createdOn;
    private LocalDateTime lastUpdatedOn;
    private Boolean status;
    private String viewPageDescription;
    private String advUri;
    private String applyLink;

    public ProtectedJobApplicationResponse(String title, String tags, LocalDate applicationStartDate, LocalDate applicationEndDate,
                                           String shortDescription, String advNo, long applicationId, LocalDateTime createdOn,
                                           LocalDateTime lastUpdatedOn, Boolean status, String viewPageDescription, String advUri, String applyLink) {
        super(title, tags, applicationStartDate, applicationEndDate, shortDescription, advNo, applicationId);
        this.createdOn = createdOn;
        this.lastUpdatedOn = lastUpdatedOn;
        this.status = status;
        this.viewPageDescription = viewPageDescription;
        this.advUri = advUri;
        this.applyLink = applyLink;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(LocalDateTime lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getViewPageDescription() {
        return viewPageDescription;
    }

    public void setViewPageDescription(String viewPageDescription) {
        this.viewPageDescription = viewPageDescription;
    }

    public String getAdvUri() {
        return advUri;
    }

    public void setAdvUri(String advUri) {
        this.advUri = advUri;
    }

    public  String getApplyLink(){
        return  applyLink;
    }

    public void setApplyLink(String applyLink){
        this.applyLink = applyLink;
    }
}
