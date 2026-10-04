package com.jnotifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "views")
public class Views extends BaseEntity {
    @NotBlank
    @Column(name = "ip_address", columnDefinition = "VARCHAR(16)")
    private String ipAddress;

    @NotBlank
    @Column(name = "visited_date", columnDefinition = "VARCHAR(11)")
    private String visitedDate = null;

    @NotBlank
    @Column(name = "visited_page", columnDefinition = "VARCHAR(100)")
    private String visitedPage = null;

    @Column(name = "browser_name", columnDefinition = "VARCHAR(50)")
    private String browserName = null;

    @Column(name = "browser_version", columnDefinition = "VARCHAR(50)")
    private String browserVersion = null;

    @Column(name = "os_name", columnDefinition = "VARCHAR(50)")
    private String osName = null;

    @Column(name = "device_type", columnDefinition = "VARCHAR(64)")
    private String deviceType = null;

    @Column(name = "device_vendor", columnDefinition = "VARCHAR(50)")
    private String deviceVendor = null;

    public Views() {
    }

    public Views(String ipAddress, String visitedDate, String visitedPage, String browserName, String browserVersion,
                 String osName, String deviceType, String deviceVendor) {
        this.ipAddress = ipAddress;
        this.visitedDate = visitedDate;
        this.visitedPage = visitedPage;
        this.browserName = browserName;
        this.browserVersion = browserVersion;
        this.osName = osName;
        this.deviceType = deviceType;
        this.deviceVendor = deviceVendor;
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

    public String getVisitedPage() {
        return visitedPage;
    }

    public void setVisitedPage(String visitedPage) {
        this.visitedPage = visitedPage;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceVendor() {
        return deviceVendor;
    }

    public void setDeviceVendor(String deviceVendor) {
        this.deviceVendor = deviceVendor;
    }
}
