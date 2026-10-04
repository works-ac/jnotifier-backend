package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotNull;

public class ChangeMediaVisibility {
    @NotNull(message = "Visibility is required")
    private Boolean visibility;

    @NotNull(message = "Media Id is required")
    private Long mediaId;

    public ChangeMediaVisibility() {
    }

    public ChangeMediaVisibility(Boolean visibility, Long mediaId) {
        this.visibility = visibility;
        this.mediaId = mediaId;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }
}
