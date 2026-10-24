package com.jnotifier.services;

import java.io.IOException;
import java.util.List;

import com.jnotifier.payload.pojo.JobsListingsPojo;
import org.springframework.data.domain.Page;
import com.jnotifier.entity.Application;
import com.jnotifier.payload.request.ApplicationRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ApplicationService {
    Application save(ApplicationRequest request);

    List<Application> saveBulk(List<ApplicationRequest> requests);

    Application uploadFiles(Long id, MultipartFile file, MultipartFile advFile) throws IOException;

    Application update(Long id, ApplicationRequest request);

    Application updateStatus(Long id, Boolean status);

    List<Application> findAll();

    Page<Application> findAllPageable(String createdBy, int page, int size);

    Application findById(Long id);

    List<Application> findActiveApplications();

    Page<Application> findActiveApplications(int page, int size);

    Page<Application> findApplicationsForUser(String username, boolean isSuperAdmin, int page, int size);

    Page<Application> findAllArchivedPublicApplications(int page, int size);

    Page<JobsListingsPojo> findAllActiveJobListingsDetails(int page, int size);

    Page<JobsListingsPojo> findAllArchiveJobListingsDetails(int page, int size);

    Page<JobsListingsPojo> findAllJobListingsBySeachCriteria(String tags, int page, int size);
}
