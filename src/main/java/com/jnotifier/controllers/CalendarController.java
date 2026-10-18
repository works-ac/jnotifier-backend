package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.payload.request.AddNewActivityRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.services.impl.CalendarServicesImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/calendar")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class CalendarController {
    @Autowired
    private CalendarServicesImpl calendarServices;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addNewActivity(@Valid @RequestBody AddNewActivityRequest request) {
        ServiceReply reply = calendarServices.createNewActivity(request);

        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }
}
