package com.jnotifier.payload.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class BulkApplicationRequest {

    @NotEmpty(message = "Job applications list cannot be empty")
    @Size(max = 25, message = "You can create at most 25 jobs at a time")
    @Valid
    private List<ApplicationRequest> applications;

    public BulkApplicationRequest() {
    }

    public BulkApplicationRequest(List<ApplicationRequest> applications) {
        this.applications = applications;
    }

    public List<ApplicationRequest> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationRequest> applications) {
        this.applications = applications;
    }
}
