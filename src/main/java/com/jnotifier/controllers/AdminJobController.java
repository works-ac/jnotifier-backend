package com.jnotifier.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.exception.GenericException;
import com.jnotifier.helpers.FileHelper;
import com.jnotifier.payload.request.ApplicationStatusRequest;
import com.jnotifier.payload.response.ProtectedJobApplicationResponse;
import com.jnotifier.services.core.FileStorageService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import com.jnotifier.entity.Application;
import com.jnotifier.entity.Category;
import com.jnotifier.payload.request.ApplicationRequest;
import com.jnotifier.payload.request.CategoryRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.PaginatedResponse;
import com.jnotifier.services.ApplicationService;
import com.jnotifier.services.CategoryService;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(JNotifierConstants.API_BASE_URL + "/admin")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
public class AdminJobController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileHelper fileHelper;

    // --- Job Application Endpoints ---

    @PostMapping("/applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Application>> createApplication(@Valid @ModelAttribute ApplicationRequest request,
                                                                      @RequestParam(value = "file", required = false) MultipartFile file,
                                                                      @RequestParam(value = "advFile", required = false) MultipartFile advFile) throws IOException {
        //Info: Processing upcoming mark down file.
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();

            boolean hasMdExtension = originalFilename != null && originalFilename.toLowerCase().endsWith(".md");
            boolean hasMdMimeType = "text/markdown".equalsIgnoreCase(contentType);
            long fileSize = file.getSize() / (1024 * 1024);

            if (!hasMdExtension && !hasMdMimeType)
                throw new GenericException(ApiResponse.error("INVALID_FILE_EXT", "Please upload a markdown file."));

            if (fileSize > 5)
                throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Your file size is too large"));

            byte[] fileBytes = file.getBytes();
            String markdownContent = new String(fileBytes, StandardCharsets.UTF_8);

            request.setViewPageDescription(markdownContent);
        }

        //Info: Processing upcoming advertisement PDF file.
        if (advFile != null && !advFile.isEmpty()) {
            String advFilename = advFile.getOriginalFilename();
            String advContentType = advFile.getContentType();


            boolean hasPdfExtension = advFilename != null && advFilename.toLowerCase().endsWith(".pdf");
            boolean hasPdfMimeType = "application/pdf".equalsIgnoreCase(advContentType);
            long advFileSize = advFile.getSize() / (1024 * 1024);

            if (!hasPdfExtension && !hasPdfMimeType)
                throw new GenericException(ApiResponse.error("INVALID_FILE_EXT", "Please upload a pdf file."));

            if (advFileSize > 50)
                throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Your file size is too large"));

            if (!fileHelper.isValidPdf(advFile))
                throw new GenericException(ApiResponse.error("INVALID_FILE_EXTENSION", "Invalid file extension"));

            request.setAdvFileName("/uploads/" + fileStorageService.saveFile(advFile));
        }

        // Info: Validating application start and end dates.
        LocalDate applicationStartDate = request.getApplicationStartDate();
        LocalDate applicationEndDate = request.getApplicationEndDate();
        LocalDate today = LocalDate.now();
        LocalDate pastCurrentDate = LocalDate.now().minusDays(10);

        if (applicationStartDate.isBefore(today) && applicationStartDate.isBefore(pastCurrentDate))
            throw new GenericException(ApiResponse.error("INVALID_APPLICATION_START_DATE", "Invalid application start date"));

        if (applicationEndDate.isBefore(applicationStartDate))
            throw new GenericException(ApiResponse.error("INVALID_APPLICATION_END_DATE", "Invalid application end date"));

        Application saved = applicationService.save(request);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    @GetMapping("/jobs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<ProtectedJobApplicationResponse>>> getActiveJobList(Authentication authentication,
                                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                                            @RequestParam(defaultValue = "10") int size) {
        String createdBy = authentication.getName();
        Page<ProtectedJobApplicationResponse> jobsPage = applicationService.findAllPageable(createdBy, page, size)
                .map(app -> new ProtectedJobApplicationResponse(
                        app.getTitle(),
                        app.getTags(),
                        app.getApplicationStartDate(),
                        app.getApplicationEndDate(),
                        app.getShortDescription(),
                        app.getAdvertisementNo(),
                        app.getId(),
                        app.getCreatedAt(),
                        app.getUpdatedAt(),
                        app.getStatus(),
                        app.getViewPageDescription(),
                        app.getAdvFileName(),
                        app.getApplyLink()
                ));

        return ResponseEntity.ok(ApiResponse.success(new PaginatedResponse<>(jobsPage)));
    }

    @PutMapping("/applications/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Application>> updateApplication(@PathVariable Long id,
                                                                      @Valid @ModelAttribute ApplicationRequest request,
                                                                      @RequestParam(value = "file", required = false) MultipartFile file,
                                                                      @RequestParam(value = "advFile", required = false) MultipartFile advFile) throws IOException {
        //Info: Processing upcoming mark down file.
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();

            boolean hasMdExtension = originalFilename != null && originalFilename.toLowerCase().endsWith(".md");
            boolean hasMdMimeType = "text/markdown".equalsIgnoreCase(contentType);
            long fileSize = file.getSize() / (1024 * 1024);

            if (!hasMdExtension && !hasMdMimeType)
                throw new GenericException(ApiResponse.error("INVALID_FILE_EXT", "Please upload a markdown file."));

            if (fileSize > 5)
                throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Your file size is too large"));

            byte[] fileBytes = file.getBytes();
            String markdownContent = new String(fileBytes, StandardCharsets.UTF_8);

            request.setViewPageDescription(markdownContent);
        }

        //Info: Processing upcoming advertisement PDF file.
        if (advFile != null && !advFile.isEmpty()) {
            String advFilename = advFile.getOriginalFilename();
            String advContentType = advFile.getContentType();


            boolean hasPdfExtension = advFilename != null && advFilename.toLowerCase().endsWith(".pdf");
            boolean hasPdfMimeType = "application/pdf".equalsIgnoreCase(advContentType);
            long advFileSize = advFile.getSize() / (1024 * 1024);

            if (!hasPdfExtension && !hasPdfMimeType)
                throw new GenericException(ApiResponse.error("INVALID_FILE_EXT", "Please upload a pdf file."));

            if (advFileSize > 50)
                throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Your file size is too large"));

            if (!fileHelper.isValidPdf(advFile))
                throw new GenericException(ApiResponse.error("INVALID_FILE_EXTENSION", "Invalid file extension"));

            request.setAdvFileName("/uploads/" + fileStorageService.saveFile(advFile));
        }

        Application updated = applicationService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PatchMapping("/applications/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Application>> updateApplicationStatus(@PathVariable Long id,
                                                                            @Valid @RequestBody ApplicationStatusRequest request) {
        Application updated = applicationService.updateStatus(id, request.getActive());
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<PaginatedResponse<Application>>> getAllApplications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        boolean isSuperAdmin = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_SUPERADMIN"));
        String username = authentication.getName();

        Page<Application> applicationsPage = applicationService.findApplicationsForUser(username, isSuperAdmin, page, size);

        return ResponseEntity.ok(ApiResponse.success(new PaginatedResponse<>(applicationsPage)));
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<ApiResponse<Application>> getApplicationById(@PathVariable Long id) {
        Application application = applicationService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(application));
    }

    // --- Category Endpoints ---

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryRequest request) {
        Category saved = categoryService.save(request);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id,
                                                                @Valid @RequestBody CategoryRequest request) {
        Category updated = categoryService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PatchMapping("/categories/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> updateCategoryStatus(@PathVariable Long id,
                                                                      @RequestParam("active") Boolean active) {
        Category updated = categoryService.updateStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(category));
    }
}
