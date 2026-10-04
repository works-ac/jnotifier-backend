package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.payload.request.AddNewNoticeRequest;
import com.jnotifier.payload.request.UpdateNoticeRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.services.impl.NoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/notice")
@PreAuthorize("hasRole('ADMIN')")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addNotice(@Valid @ModelAttribute AddNewNoticeRequest notice) throws IOException {
        ServiceReply reply = noticeService.addNewNotice(notice, notice.getNoticeAdvFile());
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<?>> updateNotice(@Valid @ModelAttribute UpdateNoticeRequest notice) {
        ServiceReply reply = noticeService.updateNotice(notice);
        return ResponseEntity.status(reply.getHttpStatusCode()).build();
    }

    @PutMapping("/archive/{noticeId}")
    public ResponseEntity<ApiResponse<?>> markAsArchive(@Valid @PathVariable Long noticeId) {
        ServiceReply reply = noticeService.markAsArchived(noticeId);
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @PutMapping("/activate/{noticeId}")
    public ResponseEntity<ApiResponse<?>> markAsActive(@Valid @PathVariable Long noticeId) {
        ServiceReply reply = noticeService.markAsActive(noticeId);
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @DeleteMapping("/delete/{noticeId}")
    public ResponseEntity<ApiResponse<?>> deleteNotice(@Valid @PathVariable Long noticeId) {
        ServiceReply reply = noticeService.deleteNotice(noticeId);
        return ResponseEntity.status(reply.getHttpStatusCode()).build();
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<?>> getAllActiveNotices(Authentication authentication, @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String createdBy = authentication.getName();
        ServiceReply reply = noticeService.getAllActiveNotices(createdBy, pageable);

        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllUserNotices(Authentication authentication, @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String createdBy = authentication.getName();
        ServiceReply reply = noticeService.getAllUserNotices(createdBy, pageable);

        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @GetMapping("/archived")
    public ResponseEntity<ApiResponse<?>> getAllArchived(Authentication authentication, @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String createdBy = authentication.getName();
        ServiceReply reply = noticeService.getAllArchivedNotices(createdBy, pageable);

        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<?>> getAllDeleted(Authentication authentication, @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        String createdBy = authentication.getName();
        ServiceReply reply = noticeService.getAllDeletedNotices(createdBy, pageable);

        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }
}
