package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotNull;

public class ApplicationStatusRequest {
    @NotNull(message = "Application status is required")
    private Boolean active;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
