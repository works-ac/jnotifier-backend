package com.jnotifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "notices", indexes = {@Index(name = "idx_notices_flags", columnList = "is_active, is_deleted")})
public class Notice extends BaseEntity {
    @NotNull(message = "Notice title is mandatory")
    @NotBlank(message = "Notice title cannot be blank.")
    @Column(name = "title", columnDefinition = "VARCHAR(100)")
    private String title;

    @NotNull(message = "Notice description is mandatory")
    @NotBlank(message = "Notice description cannot be blank.")
    @Column(name = "notice_desc", columnDefinition = "TEXT")
    private String noticeDescription;

    @Column(name = "notice_detailed_adv", columnDefinition = "TEXT")
    private String noticeDetailedAdv = null;

    @NotNull(message = "Notice tags is mandatory")
    @NotBlank(message = "Notice tags cannot be blank.")
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public Notice() {
    }

    public Notice(String title, String noticeDescription, String tags) {
        this.title = title;
        this.noticeDescription = noticeDescription;
        this.tags = tags;
    }

    public Notice(String title, String noticeDescription, String tags, String noticeDetailedAdv) {
        this.title = title;
        this.noticeDescription = noticeDescription;
        this.tags = tags;
        this.noticeDetailedAdv = noticeDetailedAdv;
    }

    public Notice(String title, String noticeDescription, String tags, String noticeDetailedAdv, Boolean isActive, Boolean isDeleted) {
        this.title = title;
        this.noticeDescription = noticeDescription;
        this.tags = tags;
        this.noticeDetailedAdv = noticeDetailedAdv;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getNoticeDetailedAdv() {
        return noticeDetailedAdv;
    }

    public void setNoticeDetailedAdv(String noticeDetailedAdv) {
        this.noticeDetailedAdv = noticeDetailedAdv;
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
