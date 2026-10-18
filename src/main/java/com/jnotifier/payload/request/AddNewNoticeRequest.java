package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class AddNewNoticeRequest {
    @NotNull(message = "Notice title is required.")
    @Size(max = 100, message = "Notice title is too long.")
    private String noticeTitle;

    @NotNull(message = "Notice title is required.")
    private String noticeDesc;

    private String noticeAdvertisement;

    private MultipartFile noticeAdvFile;

    @NotNull(message = "Notice tags is required")
    private String noticeTags;

    public AddNewNoticeRequest() {
    }

    public AddNewNoticeRequest(String title, String desc, String advertisement, String tags, MultipartFile noticeAdvFile) {
        this.noticeTitle = title;
        this.noticeDesc = desc;
        this.noticeAdvertisement = advertisement;
        this.noticeTags = tags;
        this.noticeAdvFile = noticeAdvFile;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeDesc() {
        return noticeDesc;
    }

    public void setNoticeDesc(String noticeDesc) {
        this.noticeDesc = noticeDesc;
    }

    public String getNoticeAdvertisement() {
        return noticeAdvertisement;
    }

    public void setNoticeAdvertisement(String noticeAdvertisement) {
        this.noticeAdvertisement = noticeAdvertisement;
    }

    public String getNoticeTags() {
        return noticeTags;
    }

    public void setNoticeTags(String noticeTags) {
        this.noticeTags = noticeTags;
    }

    public MultipartFile getNoticeAdvFile() {
        return noticeAdvFile;
    }

    public void setNoticeAdvFile(MultipartFile noticeAdvFile) {
        this.noticeAdvFile = noticeAdvFile;
    }
}
