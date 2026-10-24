package com.jnotifier.services.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.jnotifier.helpers.FileHelper;
import com.jnotifier.payload.pojo.JobsListingsPojo;
import com.jnotifier.services.core.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.jnotifier.entity.Application;
import com.jnotifier.exception.GenericException;
import com.jnotifier.payload.request.ApplicationRequest;
import com.jnotifier.payload.response.ApiResponse;
import com.jnotifier.repository.ApplicationRepository;
import com.jnotifier.services.ApplicationService;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileHelper fileHelper;

    @Override
    public Application save(ApplicationRequest request) {
        Application application = new Application();
        application.setTitle(request.getTitle());
        application.setTags(request.getTags());
        application.setApplicationStartDate(request.getApplicationStartDate());
        application.setApplicationEndDate(request.getApplicationEndDate());
        application.setShortDescription(request.getShortDescription());
        application.setViewPageDescription(request.getViewPageDescription());
        application.setApplyLink(request.getApplyLink());
        application.setStatus(true);
        application.setAdvFileName(request.getAdvFileName());

        if (request.getAdvNo() != null) {
            application.setAdvertisementNo(request.getAdvNo());
        }

        return applicationRepository.save(application);
    }

    @Override
    public List<Application> saveBulk(List<ApplicationRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new GenericException(ApiResponse.error("EMPTY_REQUEST", "Job applications list cannot be empty"));
        }

        LocalDate today = LocalDate.now();
        LocalDate pastLimit = today.minusDays(10);

        List<Application> applicationsToSave = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            ApplicationRequest req = requests.get(i);
            String prefix = "Row " + (i + 1) + ": ";

            if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
                throw new GenericException(ApiResponse.error("INVALID_TITLE", prefix + "Job application title is required"));
            }
            if (req.getTitle().length() > 100) {
                throw new GenericException(ApiResponse.error("INVALID_TITLE", prefix + "Job application title must be at most 100 characters"));
            }

            if (req.getTags() == null || req.getTags().trim().isEmpty()) {
                throw new GenericException(ApiResponse.error("INVALID_TAGS", prefix + "Job application tags are required"));
            }

            if (req.getShortDescription() == null || req.getShortDescription().trim().isEmpty()) {
                throw new GenericException(ApiResponse.error("INVALID_DESCRIPTION", prefix + "Job short description is required"));
            }
            if (req.getShortDescription().length() > 700) {
                throw new GenericException(ApiResponse.error("INVALID_DESCRIPTION", prefix + "Job short description must be at most 700 characters"));
            }

            if (req.getApplyLink() == null || req.getApplyLink().trim().isEmpty()) {
                throw new GenericException(ApiResponse.error("INVALID_APPLY_LINK", prefix + "Apply link is required"));
            }
            if (req.getApplyLink().length() > 250) {
                throw new GenericException(ApiResponse.error("INVALID_APPLY_LINK", prefix + "Apply link must be at most 250 characters"));
            }

            LocalDate startDate = req.getApplicationStartDate();
            LocalDate endDate = req.getApplicationEndDate();

            if (startDate == null) {
                throw new GenericException(ApiResponse.error("INVALID_APPLICATION_START_DATE", prefix + "Application start date is required"));
            }
            if (endDate == null) {
                throw new GenericException(ApiResponse.error("INVALID_APPLICATION_END_DATE", prefix + "Application end date is required"));
            }

            if (startDate.isBefore(today) && startDate.isBefore(pastLimit)) {
                throw new GenericException(ApiResponse.error("INVALID_APPLICATION_START_DATE",
                        prefix + "Application start date cannot be older than 10 days from today"));
            }

            if (endDate.isBefore(startDate)) {
                throw new GenericException(ApiResponse.error("INVALID_APPLICATION_END_DATE",
                        prefix + "Application end date cannot be before application start date"));
            }

            Application application = new Application();
            application.setTitle(req.getTitle().trim());
            application.setTags(req.getTags().trim());
            application.setApplicationStartDate(startDate);
            application.setApplicationEndDate(endDate);
            application.setShortDescription(req.getShortDescription().trim());
            application.setViewPageDescription(req.getViewPageDescription());
            application.setApplyLink(req.getApplyLink().trim());
            application.setStatus(true);
            application.setAdvFileName(req.getAdvFileName());

            if (req.getAdvNo() != null && !req.getAdvNo().trim().isEmpty()) {
                application.setAdvertisementNo(req.getAdvNo().trim());
            }

            applicationsToSave.add(application);
        }

        return applicationRepository.saveAll(applicationsToSave);
    }

    @Override
    public Application uploadFiles(Long id, MultipartFile file, MultipartFile advFile) throws IOException {
        Application application = findById(id);

        boolean hasFile = file != null && !file.isEmpty();
        boolean hasAdvFile = advFile != null && !advFile.isEmpty();

        if (!hasFile && !hasAdvFile) {
            throw new GenericException(ApiResponse.error("NO_FILES_PROVIDED", "Please select at least one file to upload."));
        }

        // Info: Processing upcoming markdown file.
        if (hasFile) {
            String originalFilename = file.getOriginalFilename();
            String contentType = file.getContentType();

            boolean hasMdExtension = originalFilename != null && originalFilename.toLowerCase().endsWith(".md");
            boolean hasMdMimeType = "text/markdown".equalsIgnoreCase(contentType);
            long fileSize = file.getSize() / (1024 * 1024);

            if (!hasMdExtension && !hasMdMimeType) {
                throw new GenericException(ApiResponse.error("INVALID_FILE_EXT", "Please upload a markdown (.md) file."));
            }

            if (fileSize > 5) {
                throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "Markdown file size cannot exceed 5 MB."));
            }

            byte[] fileBytes = file.getBytes();
            String markdownContent = new String(fileBytes, StandardCharsets.UTF_8);
            application.setViewPageDescription(markdownContent);
        }

        // Info: Processing upcoming advertisement PDF file.
        if (hasAdvFile) {
            String advFilename = advFile.getOriginalFilename();
            String advContentType = advFile.getContentType();

            boolean hasPdfExtension = advFilename != null && advFilename.toLowerCase().endsWith(".pdf");
            boolean hasPdfMimeType = "application/pdf".equalsIgnoreCase(advContentType);
            long advFileSize = advFile.getSize() / (1024 * 1024);

            if (!hasPdfExtension && !hasPdfMimeType) {
                throw new GenericException(ApiResponse.error("INVALID_FILE_EXT", "Please upload a PDF file."));
            }

            if (advFileSize > 50) {
                throw new GenericException(ApiResponse.error("INVALID_FILE_SIZE", "PDF file size cannot exceed 50 MB."));
            }

            if (!fileHelper.isValidPdf(advFile)) {
                throw new GenericException(ApiResponse.error("INVALID_FILE_EXTENSION", "Invalid PDF file signature."));
            }

            application.setAdvFileName("/uploads/" + fileStorageService.saveFile(advFile));
        }

        return applicationRepository.save(application);
    }

    @Override
    public Application update(Long id, ApplicationRequest request) {
        Application application = findById(id);

        application.setTitle(request.getTitle());
        application.setTags(request.getTags());
        application.setShortDescription(request.getShortDescription());
        if (request.getViewPageDescription() != null) {
            application.setViewPageDescription(request.getViewPageDescription());
        }
        if (request.getAdvFileName() != null) {
            application.setAdvFileName(request.getAdvFileName());
        }
        application.setApplyLink(request.getApplyLink());
        application.setAdvertisementNo(request.getAdvNo());

        // Info: Validating application's start and end dates.
        LocalDate applicationStartDate = request.getApplicationStartDate();
        LocalDate applicationEndDate = request.getApplicationEndDate();

        if (!application.getApplicationStartDate().isEqual(request.getApplicationStartDate())) {
            LocalDate today = LocalDate.now();
            LocalDate pastCurrentDate = LocalDate.now().minusDays(10);

            if (applicationStartDate.isBefore(today) && applicationStartDate.isBefore(pastCurrentDate))
                throw new GenericException(ApiResponse.error("INVALID_APPLICATION_START_DATE",
                        "Invalid application start date, You're not allowed to create jobs with application start date older than 10 days from today."));
        }

        if (!application.getApplicationEndDate().isEqual(request.getApplicationEndDate())) {
            if (applicationEndDate.isBefore(applicationStartDate))
                throw new GenericException(ApiResponse.error("INVALID_APPLICATION_END_DATE",
                        "Invalid application end date, Application end date cannot be before the application start date."));
        }

        application.setApplicationStartDate(applicationStartDate);
        application.setApplicationEndDate(applicationEndDate);

        if (request.getStatus() != null) {
            application.setStatus(request.getStatus());
        }
        return applicationRepository.save(application);
    }

    @Override
    public Application updateStatus(Long id, Boolean status) {
        Application application = findById(id);
        application.setStatus(status);
        return applicationRepository.save(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> findAll() {
        return applicationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Application> findAllPageable(String createdBy, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return applicationRepository.findByCreatedBy(createdBy, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new GenericException(ApiResponse.error("RESOURCE_NOT_FOUND", "Application not found with id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Application> findActiveApplications() {
        return applicationRepository.findByStatusTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Application> findActiveApplications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return applicationRepository.findByStatusTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Application> findApplicationsForUser(String username, boolean isSuperAdmin, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (isSuperAdmin) {
            return applicationRepository.findAll(pageable);
        } else {
            return applicationRepository.findByCreatedBy(username, pageable);
        }
    }

    @Override
    public Page<Application> findAllArchivedPublicApplications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return applicationRepository.findAllArchivedPublicApplications(pageable);
    }

    @Override
    public Page<JobsListingsPojo> findAllActiveJobListingsDetails(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return applicationRepository.findAllJobListingsDetails(true, pageable);
    }

    @Override
    public Page<JobsListingsPojo> findAllArchiveJobListingsDetails(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return applicationRepository.findAllJobListingsDetails(false, pageable);
    }

    @Override
    public Page<JobsListingsPojo> findAllJobListingsBySeachCriteria(String tags, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return applicationRepository.findAllJobListingsBySearchCriteria(true, tags, pageable);
    }
}
