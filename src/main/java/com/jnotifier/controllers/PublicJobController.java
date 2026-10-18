package com.jnotifier.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.entity.Application;
import com.jnotifier.helpers.ViewsHelper;
import com.jnotifier.payload.response.*;
import com.jnotifier.services.core.FileStorageService;
import com.jnotifier.services.impl.NoticeService;
import com.jnotifier.services.impl.ViewsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import com.jnotifier.services.ApplicationService;
import com.jnotifier.services.CategoryService;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/public")
public class PublicJobController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ViewsServiceImpl viewsService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ViewsHelper viewsHelper;

    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<PaginatedResponse<JobApplicationResponse>>> getActiveJobList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<JobApplicationResponse> jobsPage = applicationService.findAllActiveJobListingsDetails(page, size)
                .map(app -> new JobApplicationResponse(
                        app.getTitle(),
                        app.getTags(),
                        app.getApplicationStartDate(),
                        app.getApplicationEndDate(),
                        app.getShortDescription(),
                        app.getAdvNo(),
                        app.getApplicationId(),
                        app.getCreatedBy(),
                        app.getCreatedAt()
                ));

        return ResponseEntity.ok(ApiResponse.success(new PaginatedResponse<>(jobsPage)));
    }

    @GetMapping("/jobs/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<JobApplicationResponse>>> getActiveJobListBySearchCriteria(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, @RequestParam String search) {
        Page<JobApplicationResponse> jobsPage = applicationService.findAllJobListingsBySeachCriteria(search, page, size)
                .map(app -> new JobApplicationResponse(
                        app.getTitle(),
                        app.getTags(),
                        app.getApplicationStartDate(),
                        app.getApplicationEndDate(),
                        app.getShortDescription(),
                        app.getAdvNo(),
                        app.getApplicationId(),
                        app.getCreatedBy(),
                        app.getCreatedAt()
                ));

        return ResponseEntity.ok(ApiResponse.success(new PaginatedResponse<>(jobsPage)));
    }

    @GetMapping("/jobs/archived")
    public ResponseEntity<ApiResponse<PaginatedResponse<JobApplicationResponse>>> getArchivedJobList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<JobApplicationResponse> jobsPage = applicationService.findAllArchiveJobListingsDetails(page, size)
                .map(app -> new JobApplicationResponse(
                        app.getTitle(),
                        app.getTags(),
                        app.getApplicationStartDate(),
                        app.getApplicationEndDate(),
                        app.getShortDescription(),
                        app.getAdvNo(),
                        app.getApplicationId(),
                        app.getCreatedBy(),
                        app.getCreatedAt()
                ));

        return ResponseEntity.ok(ApiResponse.success(new PaginatedResponse<>(jobsPage)));
    }

    @GetMapping("/applications/{applicationId}/categories")
    public ResponseEntity<ApiResponse<List<CategoryPublicResponse>>> getCategoriesByApplicationId(@PathVariable Long applicationId) {
        List<CategoryPublicResponse> categories = categoryService.findActiveByApplicationId(applicationId).stream()
                .map(cat -> new CategoryPublicResponse(
                        cat.getCategoryName(),
                        cat.getCategoryDesc(),
                        cat.getOrderId()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApiResponse<FullJobApplicationResponse>> getJobApplication(@PathVariable Long applicationId) {
        Application application = applicationService.findById(applicationId);
        FullJobApplicationResponse response = new FullJobApplicationResponse(application.getTitle(), application.getTags(),
                application.getApplicationStartDate(), application.getApplicationEndDate(), application.getShortDescription(),
                application.getAdvertisementNo(), applicationId, application.getViewPageDescription(), application.getApplyLink(),
                application.getAdvFileName(),application.getStatus());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename, HttpServletRequest request)
            throws IOException {
        // 1. Load the file as a resource
        Resource resource = fileStorageService.loadFileAsResource(filename);

        // 2. Determine the file's content type (e.g., image/jpeg, application/pdf)
        String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

        // Fallback to the default type if the type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // 3. Return the file — include Content-Length so reverse proxies (Nginx) can
        //    stream without buffering the entire response first.
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/views")
    public ResponseEntity<ApiResponse<?>> saveViews(HttpServletRequest request) throws IOException {
        Map<String, String> clientDetails = viewsHelper.getClientDetails(request);

        ServiceReply reply = viewsService.addView(clientDetails);
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @GetMapping("/notices")
    public ResponseEntity<ApiResponse<?>> getAllActiveNotices(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        ServiceReply reply = noticeService.getAllActiveNoticeListingsPublic(pageable);

        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @GetMapping("/notices/search")
    public ResponseEntity<ApiResponse<?>> getAllActiveNoticesBySearchCriteria(@RequestParam(defaultValue = "0") int page,
                                                                              @RequestParam(defaultValue = "10") int size,
                                                                              @RequestParam String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        ServiceReply reply = noticeService.getAllNoticeListingsBySearchCriteriaPublic(true, search, pageable);

        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @GetMapping("/notices/archived")
    public ResponseEntity<ApiResponse<?>> getAllArchivedNotices(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        ServiceReply reply = noticeService.getAllArchiveNoticeListingsPublic(pageable);

        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }

    @GetMapping("/notice/{noticeId}")
    public ResponseEntity<ApiResponse<?>> getActiveNotice(@PathVariable Long noticeId) {
        ServiceReply reply = noticeService.getNotice(noticeId);
        return ResponseEntity.status(reply.getHttpStatusCode()).body(ApiResponse.success(reply.getReply()));
    }
}
