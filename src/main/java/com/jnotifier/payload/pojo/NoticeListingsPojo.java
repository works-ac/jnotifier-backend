package com.jnotifier.payload.pojo;

import java.time.LocalDateTime;

public class NoticeListingsPojo {
    private Long id;
    private String title;
    private String tags;
    private String noticeDescription;
    private String createdBy;
    private LocalDateTime createdAt;
    private Boolean isActive;

    public NoticeListingsPojo() {
    }

    public NoticeListingsPojo(Long id, String title, String tags, String noticeDescription, String createdBy, LocalDateTime createdAt, Boolean isActive) {
        this.id = id;
        this.title = title;
        this.tags = tags;
        this.noticeDescription = noticeDescription;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNoticeDescription() {
        return noticeDescription;
    }

    public void setNoticeDescription(String noticeDescription) {
        this.noticeDescription = noticeDescription;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
