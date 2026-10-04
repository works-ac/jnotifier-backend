package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.services.impl.ViewsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/views")
@PreAuthorize("hasRole('ADMIN')")
public class ViewsController {
    @Autowired
    private ViewsServiceImpl viewsService;

    @PostMapping("/calc-n-get")
    public ResponseEntity<ApiResponse<?>> getPageViews(HttpServletRequest request) {
        String visitedPage = request.getHeader("x-user-loc");
        ServiceReply reply = viewsService.getPageViews(visitedPage);

        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }
}
