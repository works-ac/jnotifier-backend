package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.services.impl.ViewsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/dashboard")
@PreAuthorize("hasRole('SUPERADMIN')")
public class DashboardController {
    @Autowired
    private ViewsServiceImpl viewsService;

    @GetMapping("/today-views")
    public ResponseEntity<ApiResponse<?>> getTodayViews(@RequestParam String visitedDate) {
        ServiceReply reply = viewsService.getDailyActiveViews(visitedDate);
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @GetMapping("/total-views")
    public ResponseEntity<ApiResponse<?>> getTotalViews() {
        ServiceReply reply = viewsService.getTotalViews();
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }
}
