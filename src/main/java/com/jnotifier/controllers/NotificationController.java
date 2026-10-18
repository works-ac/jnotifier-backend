package com.jnotifier.controllers;

import java.util.List;

import com.jnotifier.app.JNotifierConstants;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import com.jnotifier.entity.Notification;
import com.jnotifier.services.NotificationService;
import com.jnotifier.payload.response.ApiResponse;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<Notification>> createNotification(@Valid @RequestBody Notification notification, Authentication authentication) {
        notification.setSender(authentication.getName());

        // Call service that has retry logic and recovery handling.
        // If the delivery fails after retries, it recovers gracefully and sets status to FAILED.
        Notification sentNotification = notificationService.sendNotification(notification);

        return ResponseEntity.ok(ApiResponse.success(sentNotification));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<ApiResponse<List<Notification>>> getAllNotifications(Authentication authentication) {
        boolean isAdminOrSuperAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_SUPERADMIN"));

        List<Notification> notifications;
        if (isAdminOrSuperAdmin) {
            notifications = notificationService.findAll();
        } else {
            notifications = notificationService.findByRecipient(authentication.getName());
        }

        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
}
