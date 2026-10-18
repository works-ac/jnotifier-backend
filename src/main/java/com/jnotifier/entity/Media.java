package com.jnotifier.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "medias")
public class Media extends BaseEntity {
    @NotNull(message = "File name is required")
    @Column(name = "file_name", columnDefinition = "VARCHAR(100)")
    private String fileName;

    @NotNull(message = "File size is required")
    @Column(name = "file_size")
    private Long fileSize;

    @NotNull(message = "File mime type is required")
    @Column(name = "file_type", columnDefinition = "VARCHAR(64)")
    private String fileType;

    @Column(name = "file_uri", columnDefinition = "TEXT")
    private String fileUri;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public Media() {
    }

    public Media(String fileName, String fileType, Long fileSize, String fileUri) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUri = fileUri;
        this.isPublic = true;
        this.isDeleted = false;
    }

    public Media(String fileName, String fileType, Long fileSize, String fileUri, Boolean isPublic, Boolean isDeleted) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUri = fileUri;
        this.isPublic = isPublic;
        this.isDeleted = isDeleted;
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

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
