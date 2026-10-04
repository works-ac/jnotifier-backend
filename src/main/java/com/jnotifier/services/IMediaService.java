package com.jnotifier.services;

import com.jnotifier.payload.response.ServiceReply;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IMediaService {
    public ServiceReply addMedia(MultipartFile file);

    public ServiceReply listActiveMedia(int page, int size);

    public ServiceReply listAllMedia(String createdBy,int page, int size);

    public ServiceReply changeMediaVisibility(Long mediaId, Boolean visibility);

    public ServiceReply deleteMedia(Long mediaId);

    public ResponseEntity<Resource> downloadMedia(String fileName, Long mediaId, HttpServletRequest request) throws IOException;
}
