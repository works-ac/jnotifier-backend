package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.services.impl.JobCategoriesServicesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/public/job-categories")
public class PublicJobCategoriesController {
    @Autowired
    private JobCategoriesServicesImpl jobCategoriesServices;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<?>> getAllJobCategories() {
        ServiceReply reply = jobCategoriesServices.listAllJobCategoriesPublic();
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }
}
