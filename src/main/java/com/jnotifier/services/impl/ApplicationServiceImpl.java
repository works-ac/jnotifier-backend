package com.jnotifier.services.impl;

import java.util.List;

import com.jnotifier.payload.pojo.JobsListingsPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Application update(Long id, ApplicationRequest request) {
        Application application = findById(id);

        application.setTitle(request.getTitle());
        application.setTags(request.getTags());
        application.setApplicationStartDate(request.getApplicationStartDate());
        application.setApplicationEndDate(request.getApplicationEndDate());
        application.setShortDescription(request.getShortDescription());
        application.setViewPageDescription(request.getViewPageDescription());
        application.setApplyLink(request.getApplyLink());
        application.setAdvertisementNo(request.getAdvNo());

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
