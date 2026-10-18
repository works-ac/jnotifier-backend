package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.services.IAppliedJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/users/applied-jobs")
public class AppliedJobController {

    @Autowired
    private IAppliedJobService appliedJobService;

    @PostMapping("/mark/{applicationId}")
    public ResponseEntity<ApiResponse<?>> markJobAsApplied(Authentication authentication, @PathVariable Long applicationId) throws GenericException {
        ServiceReply reply = appliedJobService.markJobAsApplied(authentication.getName(), applicationId);
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<?>> getAppliedJobs(Authentication authentication, 
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) throws GenericException {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedOn").descending());
        ServiceReply reply = appliedJobService.getUserAppliedJobs(authentication.getName(), pageable);
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }
}
