package com.jnotifier.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AddNewMediaRequest {
    @NotNull(message = "File name is required")
    @Pattern(regexp = "^[A-Za-z0-9\\\\s\\\\-_()]\\\\{1,100}$", message = "Invalid file name")
    private String fileName;

    @NotNull(message = "File size is required")
    private Long fileSize;

    @NotNull(message = "File type is required")
    @Pattern(regexp = "^(application\\\\/pdf|image\\\\/.*)$", message = "Invalid file type, our system does not support this file type.")
    private String fileType;

    public AddNewMediaRequest() {
    }

    public AddNewMediaRequest(String fileName, Long fileSize, String fileType) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
