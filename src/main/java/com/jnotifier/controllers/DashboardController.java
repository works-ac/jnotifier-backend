package com.jnotifier.controllers;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.ServiceReply;
import com.jnotifier.repository.ApplicationRepository;
import com.jnotifier.repository.MediaRepository;
import com.jnotifier.repository.NoticeRepository;
import com.jnotifier.repository.UserRepository;
import com.jnotifier.services.impl.ViewsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/dashboard")
@PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN')")
public class DashboardController {
    @Autowired
    private ViewsServiceImpl viewsService;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MediaRepository mediaRepository;

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

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalJobs", applicationRepository.count());
        stats.put("totalNotices", noticeRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalMedias", mediaRepository.countActiveMedia());
        stats.put("archivedJobs", applicationRepository.countByStatusFalse());
        stats.put("archivedNotices", noticeRepository.countArchivedNotices());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/total-jobs")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTotalJobs() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalJobs", applicationRepository.count());
        return ResponseEntity.ok(ApiResponse.success(map));
    }

    @GetMapping("/total-notices")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTotalNotices() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalNotices", noticeRepository.count());
        return ResponseEntity.ok(ApiResponse.success(map));
    }

    @GetMapping("/total-users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTotalUsers() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalUsers", userRepository.count());
        return ResponseEntity.ok(ApiResponse.success(map));
    }

    @GetMapping("/total-medias")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTotalMedias() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalMedias", mediaRepository.countActiveMedia());
        return ResponseEntity.ok(ApiResponse.success(map));
    }

    @GetMapping("/archived-jobs")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getArchivedJobs() {
        Map<String, Object> map = new HashMap<>();
        map.put("archivedJobs", applicationRepository.countByStatusFalse());
        return ResponseEntity.ok(ApiResponse.success(map));
    }

    @GetMapping("/archived-notices")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getArchivedNotices() {
        Map<String, Object> map = new HashMap<>();
        map.put("archivedNotices", noticeRepository.countArchivedNotices());
        return ResponseEntity.ok(ApiResponse.success(map));
    }
}
