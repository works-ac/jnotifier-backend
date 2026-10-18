package com.jnotifier.payload.pojo;

public class DailyActiveUsersPojo {
    private String ipAddress;
    private String visitedDate;
    private Long dau;

    public DailyActiveUsersPojo() {
    }

    public DailyActiveUsersPojo(String ipAddress, String visitedDate, Long dau) {
        this.ipAddress = ipAddress;
        this.visitedDate = visitedDate;
        this.dau = dau;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getVisitedDate() {
        return visitedDate;
    }

    public void setVisitedDate(String visitedDate) {
        this.visitedDate = visitedDate;
    }

    public Long getDau() {
        return dau;
    }

    public void setDau(Long dau) {
        this.dau = dau;
    }
}
