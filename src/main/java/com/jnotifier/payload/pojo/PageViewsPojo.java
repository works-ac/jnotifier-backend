package com.jnotifier.payload.pojo;

public class PageViewsPojo {
    private String ipAddress;
    private String visitedDate;
    private Long pageViews;
    private String visitedPage;

    public PageViewsPojo() {
    }

    public PageViewsPojo(String ipAddress, String visitedDate, Long pageViews, String visitedPage) {
        this.ipAddress = ipAddress;
        this.visitedDate = visitedDate;
        this.pageViews = pageViews;
        this.visitedPage = visitedPage;
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

    public Long getPageViews() {
        return pageViews;
    }

    public void setPageViews(Long pageViews) {
        this.pageViews = pageViews;
    }

    public String getVisitedPage() {
        return visitedPage;
    }

    public void setVisitedPage(String visitedPage) {
        this.visitedPage = visitedPage;
    }
}
