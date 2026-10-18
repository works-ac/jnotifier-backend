package com.jnotifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "applications", indexes = {@Index(name = "idx_application_status", columnList = "status")})
public class Application extends BaseEntity {

    @NotBlank
    @Column(name = "title", columnDefinition = "VARCHAR(32)", nullable = false)
    private String title;

    @Column(name = "tags")
    private String tags;

    @Column(name = "advertisement_no", nullable = true, columnDefinition = "VARCHAR(100)")
    private String advertisementNo;

    @NotNull
    @Column(name = "application_start_date", nullable = false)
    private LocalDate applicationStartDate;

    @NotNull
    @Column(name = "application_end_date", nullable = false)
    private LocalDate applicationEndDate;

    @Size(max = 700)
    @Column(name = "short_description", length = 700)
    private String shortDescription;

    @Column(name = "view_page_description", columnDefinition = "TEXT")
    private String viewPageDescription;

    @Column(name = "adv_filename", columnDefinition = "VARCHAR(100)")
    private String advFileName;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Size(max = 250)
    @Column(name = "apply_link", columnDefinition = "VARCHAR(250)")
    private String applyLink;

    public Application() {
    }

    public Application(String title, String tags, LocalDate applicationStartDate, LocalDate applicationEndDate, String shortDescription, Boolean status, String advNo, String viewPageDescription, String applyLink) {
        this.title = title;
        this.tags = tags;
        this.applicationStartDate = applicationStartDate;
        this.applicationEndDate = applicationEndDate;
        this.shortDescription = shortDescription;
        this.status = status;
        this.advertisementNo = advNo;
        this.viewPageDescription = viewPageDescription;
        this.applyLink = applyLink;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getAdvertisementNo() {
        return advertisementNo;
    }

    public void setAdvertisementNo(String advertisementNo) {
        this.advertisementNo = advertisementNo;
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
