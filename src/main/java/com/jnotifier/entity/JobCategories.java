package com.jnotifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_categories", schema = "masters")
public class JobCategories extends BaseEntity {
    @Column(name = "category_name", columnDefinition = "VARCHAR(64)")
    private String categoryName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public JobCategories() {
    }

    public JobCategories(String categoryName) {
        this.categoryName = categoryName;
    }

    public JobCategories(String categoryName, Boolean isActive, Boolean isDeleted) {
        this.categoryName = categoryName;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
