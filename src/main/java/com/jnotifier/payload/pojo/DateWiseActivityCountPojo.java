package com.jnotifier.payload.pojo;

public class DateWiseActivityCountPojo {
    private String activityDate;
    private Integer activityCount;

    public DateWiseActivityCountPojo() {}

    public DateWiseActivityCountPojo(String activityDate, Integer activityCount) {
        this.activityDate = activityDate;
        this.activityCount = activityCount;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public Integer getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(Integer activityCount) {
        this.activityCount = activityCount;
    }
}
