package com.jnotifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "calendar")
public class Calendar extends BaseEntity {
    @NotNull(message = "Title is required")
    @Column(name = "description", columnDefinition = "VARCHAR(512)")
    private String description;

    @NotNull(message = "Date is required")
    @Column(name = "activity_date", columnDefinition = "VARCHAR(12)")
    private String activityDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public Calendar() {
    }

    public Calendar(String description, String date) {
        this.description = description;
        this.activityDate = date;
    }

    public Calendar(String description, String date, Boolean isActive, Boolean isDeleted) {
        this.description = description;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
        this.activityDate = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }
}
