package com.jnotifier.payload.pojo;

public class PublicJobCategoriesPojo {
    private String jobCategoryName;

    public PublicJobCategoriesPojo() {
    }

    public PublicJobCategoriesPojo(String jobCategoryName) {
        this.jobCategoryName = jobCategoryName;
    }

    public String getJobCategoryName() {
        return jobCategoryName;
    }

    public void setJobCategoryName(String jobCategoryName) {
        this.jobCategoryName = jobCategoryName;
    }
}
