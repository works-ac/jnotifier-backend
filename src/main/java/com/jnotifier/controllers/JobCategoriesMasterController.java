package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.payload.request.AddNewJobCategoryRequest;
import com.jnotifier.payload.request.UpdateJobCategoryRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.services.impl.JobCategoriesServicesImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/job-categories/master")
@PreAuthorize("hasRole('SUPERADMIN')")
public class JobCategoriesMasterController {
    @Autowired
    private JobCategoriesServicesImpl jobCategoriesServices;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addNewJobCategory(@Valid @RequestBody AddNewJobCategoryRequest request) {
        ServiceReply reply = jobCategoriesServices.addNewJobCategory(request);
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<?>> getAllJobCategories(Authentication authentication, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        String createdBy = authentication.getName();
        ServiceReply reply = jobCategoriesServices.listAllJobCategories(page, size, createdBy);
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<?>> updateJobCategory(@Valid @RequestBody UpdateJobCategoryRequest request) {
        ServiceReply reply = jobCategoriesServices.updateJobCategory(request);
        return ResponseEntity.status(reply.getHttpStatusCode()).build();
    }

    @PatchMapping("/mark/archive/{jobCategoryId}")
    public ResponseEntity<ApiResponse<?>> markJobCategoryAsArchive(@PathVariable Long jobCategoryId) {
        ServiceReply reply = jobCategoriesServices.markJobCategoryAsArchived(jobCategoryId);
        return ResponseEntity.status(reply.getHttpStatusCode()).build();
    }

    @PatchMapping("/mark/active/{jobCategoryId}")
    public ResponseEntity<ApiResponse<?>> markJobCategoryAsActive(@PathVariable Long jobCategoryId) {
        ServiceReply reply = jobCategoriesServices.markJobCategoryAsActive(jobCategoryId);
        return ResponseEntity.status(reply.getHttpStatusCode()).build();
    }

    @DeleteMapping("/mark/delete/{jobCategoryId}")
    public ResponseEntity<ApiResponse<?>> deleteJobCategory(@PathVariable Long jobCategoryId) {
        ServiceReply reply = jobCategoriesServices.markJobCategoryAsDeleted(jobCategoryId);
        return ResponseEntity.status(reply.getHttpStatusCode()).build();
    }
}
