package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.payload.request.ChangeMediaVisibility;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.services.impl.MediaServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/media")
@PreAuthorize("hasRole('ADMIN')")
public class MediaController {
    @Autowired
    private MediaServiceImpl mediaService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addMedia(@RequestParam("file") MultipartFile file) {
        ServiceReply reply = mediaService.addMedia(file);
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @PutMapping("/change/visibility")
    public ResponseEntity<ApiResponse<?>> changeMediaVisibility(@Valid @RequestBody ChangeMediaVisibility changeMediaVisibility) {
        ServiceReply reply = mediaService.changeMediaVisibility(changeMediaVisibility.getMediaId(), changeMediaVisibility.getVisibility());
        return ResponseEntity.status(reply.getHttpStatusCode()).build();
    }

    @DeleteMapping("/delete/{mediaId}")
    public ResponseEntity<ApiResponse<?>> deleteMedia(@PathVariable("mediaId") Long mediaId) {
        ServiceReply reply = mediaService.deleteMedia(mediaId);
        return ResponseEntity.status(reply.getHttpStatusCode()).build();
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<?>> getAllActiveMedia(Authentication authentication, @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        String createdBy = authentication.getName();
        ServiceReply reply = mediaService.listAllMedia(createdBy, page, size);
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }
}
