package com.jnotifier.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import com.jnotifier.app.JNotifierConstants;
import com.jnotifier.entity.Application;
import com.jnotifier.entity.Category;
import com.jnotifier.exception.GenericException;
import com.jnotifier.helpers.ExcelHelper;
import com.jnotifier.helpers.FileHelper;
import com.jnotifier.payload.request.ApplicationRequest;
import com.jnotifier.payload.request.ApplicationStatusRequest;
import com.jnotifier.payload.request.BulkApplicationRequest;
import com.jnotifier.payload.request.CategoryRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.payload.response.PaginatedResponse;
import com.jnotifier.payload.response.ProtectedJobApplicationResponse;
import com.jnotifier.services.ApplicationService;
import com.jnotifier.services.CategoryService;
import com.jnotifier.services.core.FileStorageService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private ExcelHelper excelHelper;

    // --- Job Application Endpoints ---

    @PostMapping("/applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Application>> createApplication(@Valid @ModelAttribute ApplicationRequest request,
                                                                      @RequestParam(value = "file", required = false) MultipartFile file,
                                                                      @RequestParam(value = "advFile", required = false) MultipartFile advFile) throws IOException {
        // Info: Processing upcoming markdown file.
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

        // Info: Processing upcoming advertisement PDF file.
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

    @PostMapping("/applications/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Application>>> createBulkApplications(
            @Valid @RequestBody BulkApplicationRequest bulkRequest) {
        List<ApplicationRequest> requests = bulkRequest.getApplications();
        if (requests == null || requests.isEmpty()) {
            throw new GenericException(ApiResponse.error("EMPTY_REQUEST", "Job applications list cannot be empty"));
        }
        if (requests.size() > 25) {
            throw new GenericException(ApiResponse.error("EXCEEDS_MAX_LIMIT", "You can create at most 25 jobs at a time"));
        }

        List<Application> saved = applicationService.saveBulk(requests);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    @PostMapping("/applications/bulk/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ApplicationRequest>>> parseBulkExcel(
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new GenericException(ApiResponse.error("INVALID_FILE", "Please upload an excel file."));
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.toLowerCase().endsWith(".xlsx") && !originalFilename.toLowerCase().endsWith(".xls"))) {
            throw new GenericException(ApiResponse.error("INVALID_FILE_TYPE", "Only Excel files (.xlsx) are supported."));
        }

        List<ApplicationRequest> parsed = excelHelper.parseJobApplicationsFromExcel(file.getInputStream());
        return ResponseEntity.ok(ApiResponse.success(parsed));
    }

    @GetMapping("/applications/bulk/template")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERADMIN')")
    public ResponseEntity<Resource> downloadBulkJobTemplate() {
        Resource resource = new ClassPathResource("bulk-job-creation-template.xlsx");
        if (!resource.exists()) {
            throw new GenericException(ApiResponse.error("FILE_NOT_FOUND", "Template file not found on server"));
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bulk-job-creation-template.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @PostMapping("/applications/{id}/files")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Application>> uploadApplicationFiles(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "advFile", required = false) MultipartFile advFile) throws IOException {
        Application updated = applicationService.uploadFiles(id, file, advFile);
        return ResponseEntity.ok(ApiResponse.success(updated));
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
        // Info: Processing upcoming markdown file.
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

        // Info: Processing upcoming advertisement PDF file.
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
